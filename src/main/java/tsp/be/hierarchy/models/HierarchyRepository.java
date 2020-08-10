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
		category.children = parseChildren(rootCategoryDoc.getList("children", Document.class));

		return category;
	}

	private List<Category> parseChildren(List<Document> childrenDocs) {
		List<Category> list = new ArrayList<>(childrenDocs.size());
		for(Document childDoc: childrenDocs) {
			Category childCategory = new Category();
			childCategory.id = childDoc.getObjectId("_id").toString();
			childCategory.name = childDoc.getString("name");
			childCategory.children = parseChildren(childDoc.getList("children", Document.class));

			list.add(childCategory);
		}
		return list;
	}
}
