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
import tsp.be.error.SimpleValidationException;

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

	public Tutorial getTutorialContents(String tutorialID) {
		Document tutorialDoc = tutorialsCollection.find(Filters.eq("_id", new ObjectId(tutorialID))).first();
		if (tutorialDoc == null) throw new SimpleValidationException("Tutorial not found");

		Tutorial tutorial = new Tutorial();
		tutorial.id = tutorialDoc.getObjectId("_id").toString();
		tutorial.name = tutorialDoc.getString("name");
		tutorial.description = tutorialDoc.getString("description");
		tutorial.authorID = tutorialDoc.getString("authorID");
		tutorial.authorName = tutorialDoc.getString("authorName");
		tutorial.chapters = extractChapters(tutorialDoc.getList("chapters", Document.class));

		return tutorial;
	}

	private List<Chapter> extractChapters(List<Document> chapterDocs) {
		List<Chapter> chapters = new ArrayList<>(chapterDocs.size());

		for (Document chapterDoc: chapterDocs) {
			Chapter chapter = new Chapter();

			chapter.id = chapterDoc.getObjectId("_id").toString();
			chapter.name = chapterDoc.getString("name");
			chapter.lessons = extractLessons(chapterDoc.getList("lessons", Document.class));

			chapters.add(chapter);
		}
		return chapters;
	}

	private List<LessonMetaData> extractLessons(List<Document> lessonDocs) {
		List<LessonMetaData> lessons = new ArrayList<>(lessonDocs.size());
		for (Document lessonDoc: lessonDocs) {
			LessonMetaData lesson = new LessonMetaData();
			lesson.id = lessonDoc.getObjectId("_id").toString();
			lesson.name = lessonDoc.getString("name");

			lessons.add(lesson);
		}
		return lessons;
	}
}
