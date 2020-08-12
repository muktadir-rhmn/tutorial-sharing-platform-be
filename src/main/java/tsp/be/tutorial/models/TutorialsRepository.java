package tsp.be.tutorial.models;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tsp.be.db.DatabaseManager;
import tsp.be.error.SingleMessageValidationException;

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
		tutorialDoc.append("authorID", new ObjectId(authorID));
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
			TutorialMetaData tutorial = tutorialDocToTutorialMetaData(tutorialDoc);

			tutorials.add(tutorial);
		}

		return tutorials;
	}

	public List<TutorialMetaData> getRecentTutorials(int limit) {
		FindIterable<Document> tutorialDocs = tutorialsCollection.find()
				.projection(Projections.include("_id", "name", "description", "authorID", "authorName"))
				.sort(Sorts.ascending("createdAt"))
				.limit(limit);

		List<TutorialMetaData> tutorials = new ArrayList<>();
		for(Document tutorialDoc: tutorialDocs) {
			TutorialMetaData tutorial = tutorialDocToTutorialMetaData(tutorialDoc);

			tutorials.add(tutorial);
		}

		return tutorials;
	}

	public List<TutorialMetaData> getTutorialsOfUser(String userID) {
		FindIterable<Document> tutorialDocs = tutorialsCollection.find(Filters.eq("authorID", new ObjectId(userID)))
				.projection(Projections.include("_id", "name", "description", "authorID", "authorName"));

		List<TutorialMetaData> tutorials = new ArrayList<>();
		for(Document tutorialDoc: tutorialDocs) {
			System.out.println(tutorialDoc);
			TutorialMetaData tutorial = tutorialDocToTutorialMetaData(tutorialDoc);

			tutorials.add(tutorial);
		}

		return tutorials;
	}

	private TutorialMetaData tutorialDocToTutorialMetaData(Document tutorialDoc) {
		TutorialMetaData tutorial = new TutorialMetaData();
		tutorial.id = tutorialDoc.getObjectId("_id").toString();
		tutorial.name = tutorialDoc.getString("name");
		tutorial.description = tutorialDoc.getString("description");
		tutorial.authorID = tutorialDoc.getObjectId("authorID").toString();
		tutorial.authorName = tutorialDoc.getString("authorName");

		return tutorial;
	}

	public Tutorial getTutorialContents(String tutorialID) {
		Document tutorialDoc = tutorialsCollection.find(Filters.eq("_id", new ObjectId(tutorialID))).first();
		if (tutorialDoc == null) throw new SingleMessageValidationException("Tutorial not found");

		Tutorial tutorial = new Tutorial();
		tutorial.id = tutorialDoc.getObjectId("_id").toString();
		tutorial.name = tutorialDoc.getString("name");
		tutorial.description = tutorialDoc.getString("description");
		tutorial.authorID = tutorialDoc.getObjectId("authorID").toString();
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

	public void addLesson(String tutorialID, String chapterID, String lessonID, String name) {
		Document lessonMetaData = new Document();
		lessonMetaData.append("_id", new ObjectId(lessonID));
		lessonMetaData.append("name", name);

		UpdateResult result = tutorialsCollection.updateOne(
				Filters.and(Filters.eq("_id", new ObjectId(tutorialID)), Filters.eq("chapters._id", new ObjectId(chapterID))),
				Updates.push("chapters.$.lessons", lessonMetaData)
		);

		if(result.getModifiedCount() == 0)  throw new SingleMessageValidationException("Invalid tutorial and chapter");
	}

	public void updateLesson(String tutorialID, String chapterID, String lessonID, String name) {
		List<Bson> arrayFilters = new ArrayList<>();
		arrayFilters.add(Filters.eq("chapter._id", new ObjectId(chapterID)));
		arrayFilters.add(Filters.eq("lesson._id", new ObjectId(lessonID)));

		tutorialsCollection.updateOne(
				Filters.eq("_id", new ObjectId(tutorialID)),
				Updates.set("chapters.$[chapter].lessons.$[lesson].name", name),
				new UpdateOptions().arrayFilters(arrayFilters)
		);
	}
}
