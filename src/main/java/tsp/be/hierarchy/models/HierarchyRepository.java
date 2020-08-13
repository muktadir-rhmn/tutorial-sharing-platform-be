package tsp.be.hierarchy.models;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tsp.be.db.DatabaseManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
public class HierarchyRepository {
	private MongoCollection<Document> hierarchyCollection;

	@Autowired
	public HierarchyRepository( DatabaseManager databaseManager) {
		hierarchyCollection = databaseManager.getCollection("hierarchy");
	}

	public Category getHierarchy() {
		Document rootCategoryDoc = hierarchyCollection.find().first();

		Category category = new Category();
		category.id = rootCategoryDoc.getObjectId("_id").toString();
		category.name = rootCategoryDoc.getString("name");
		category.subcategories = parseChildren((List<Document>) rootCategoryDoc.get("subcategories"));

		return category;
	}

	private List<Category> parseChildren(List<Document> childrenDocs) {
		List<Category> list = new ArrayList<>(childrenDocs.size());
		for(Document childDoc: childrenDocs) {
			Category childCategory = new Category();
			childCategory.id = childDoc.getObjectId("_id").toString();
			childCategory.name = childDoc.getString("name");
			childCategory.subcategories = parseChildren((List<Document>) childDoc.get("subcategories"));

			list.add(childCategory);
		}
		return list;
	}

	public String createCategory(String name, String[] pathFromRoot) {
		ObjectId categoryID = new ObjectId();

		Document categoryDoc = new Document();
		categoryDoc.append("_id", categoryID);
		categoryDoc.append("name", name);
		categoryDoc.append("subcategories", Collections.emptyList());

		List<Bson> arrayFilters = new ArrayList<>();

		StringBuilder updateString = new StringBuilder("subcategories");
		for(int i = 1; i < pathFromRoot.length; i++) {
			updateString.append(".$[t");
			updateString.append(i);
			updateString.append("]");
			updateString.append(".subcategories");

			arrayFilters.add(Filters.eq("t" + i + "._id", new ObjectId(pathFromRoot[i])));
		}

		List<Document> list = new ArrayList<>();
		list.add(categoryDoc);

		hierarchyCollection.updateOne(
				Filters.eq("_id", new ObjectId(pathFromRoot[0])),
				Updates.pushEach(updateString.toString(), list, new PushOptions().sortDocument(Sorts.ascending("name"))),
				new UpdateOptions().arrayFilters(arrayFilters)
		);

		return categoryID.toString();
	}
}
