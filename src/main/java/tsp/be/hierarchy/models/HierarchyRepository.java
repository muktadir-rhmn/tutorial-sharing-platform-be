package tsp.be.hierarchy.models;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tsp.be.db.DatabaseManager;

import java.util.ArrayList;
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
			System.out.println(childDoc);
			Category childCategory = new Category();
			childCategory.id = childDoc.getObjectId("_id").toString();
			childCategory.name = childDoc.getString("name");
			childCategory.subcategories = parseChildren((List<Document>) childDoc.get("subcategories"));

			list.add(childCategory);
		}
		return list;
	}
}
