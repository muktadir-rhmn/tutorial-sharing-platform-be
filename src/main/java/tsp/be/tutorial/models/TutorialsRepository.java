package tsp.be.tutorial.models;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tsp.be.db.DatabaseManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TutorialsRepository {
	private final static String TUTORIAL_COLLECTION_NAME = "tutorials";

	private MongoCollection<Document> tutorialsCollection;

	@Autowired
	TutorialsRepository(DatabaseManager databaseManager) {
		tutorialsCollection = databaseManager.getCollection(TUTORIAL_COLLECTION_NAME);
	}

	public String createTutorial(String authorID, String authorName, String name, String description, String categoryID) {
		ObjectId tutorialID = new ObjectId();
		Document tutorialDoc = new Document();
		tutorialDoc.append("_id", tutorialID);
		tutorialDoc.append("name", name);
		tutorialDoc.append("description", description);
		tutorialDoc.append("categoryID", new ObjectId(categoryID));
		tutorialDoc.append("authorID", authorID);
		tutorialDoc.append("authorName", authorName);
		tutorialDoc.append("chapters", Collections.emptyList());

		tutorialsCollection.insertOne(tutorialDoc);

		return tutorialID.toString();
	}

	public String addChapter(String tutorialID, String name) {
		ObjectId chapterID = new ObjectId();

		Document chapter = new Document();
		chapter.append("_id", chapterID);
		chapter.append("name", name);
		chapter.append("lessons", Collections.emptyList());

		tutorialsCollection.updateOne(
				Filters.eq("_id", new ObjectId(tutorialID)),
				Updates.combine(Updates.push("chapters", chapter))
		);

		return chapterID.toString();
	}

	public List<TutorialMetaData> getTutorials(String categoryID) {
		FindIterable<Document> tutorialDocs = tutorialsCollection.find(Filters.eq("categoryID", new ObjectId(categoryID)))
				.projection(Projections.include("_id", "name", "description", "authorID", "authorName"));

		List<TutorialMetaData> tutorials = new ArrayList<>();
		for(Document tutorialDoc: tutorialDocs) {
			TutorialMetaData tutorial = new TutorialMetaData();
			tutorial.id = tutorialDoc.getObjectId("_id").toString();
			tutorial.name = tutorialDoc.getString("name");
			tutorial.description = tutorialDoc.getString("description");
			tutorial.authorID = tutorialDoc.getString("authorID");
			tutorial.authorName = tutorialDoc.getString("authorName");

			tutorials.add(tutorial);
		}

		return tutorials;
	}
}
