package tsp.be.hierarchy.models;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tsp.be.db.DBUtils;
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
		return createCategory(new ObjectId(), name, pathFromRoot);
	}

	private String createCategory(ObjectId categoryID, String name, String[] pathFromRoot) {
		Document categoryDoc = new Document();
		categoryDoc.append("_id", categoryID);
		categoryDoc.append("name", name);
		categoryDoc.append("subcategories", Collections.emptyList());

		List<Bson> arrayFilters = new ArrayList<>();
		String updateString = buildUpdateStringFromIDPath(pathFromRoot, arrayFilters);

		List<Document> list = new ArrayList<>();
		list.add(categoryDoc);

		hierarchyCollection.updateOne(
				Filters.eq("_id", DBUtils.validateAndCreateObjectID(pathFromRoot[0])),
				Updates.pushEach(updateString, list, new PushOptions().sortDocument(Sorts.ascending("name"))),
				new UpdateOptions().arrayFilters(arrayFilters)
		);

		return categoryID.toString();
	}

	//todo: make it transactional
	public void updateCategory(String[] idPath, String name, String[] parentIDPath) {
		removeSubcategory(idPath);
		createCategory(DBUtils.validateAndCreateObjectID(idPath[idPath.length - 1]), name, parentIDPath);
	}

	private void removeSubcategory(String[] idPath) {
		List<Bson> arrayFilters = new ArrayList<>();

		String[] parentIDPath = new String[idPath.length - 1];
		System.arraycopy(idPath, 0, parentIDPath, 0, parentIDPath.length);
		String updateString = buildUpdateStringFromIDPath(parentIDPath, arrayFilters);

		hierarchyCollection.updateOne(
				Filters.eq("_id", DBUtils.validateAndCreateObjectID(idPath[0])),
				Updates.pull(updateString, Filters.eq("_id", DBUtils.validateAndCreateObjectID(idPath[idPath.length - 1]))),
				new UpdateOptions().arrayFilters(arrayFilters)
		);
	}

	private String buildUpdateStringFromIDPath(String[] parentIDPath, List<Bson> arrayFilters) {
		StringBuilder updateString = new StringBuilder("subcategories");
		for(int i = 1; i < parentIDPath.length; i++) {
			updateString.append(".$[t");
			updateString.append(i);
			updateString.append("]");
			updateString.append(".subcategories");

			arrayFilters.add(Filters.eq("t" + i + "._id", DBUtils.validateAndCreateObjectID(parentIDPath[i])));
		}

		return updateString.toString();
	}
}
