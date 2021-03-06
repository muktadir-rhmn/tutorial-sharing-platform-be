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
import tsp.be.db.DBUtils;
import tsp.be.db.DatabaseManager;
import tsp.be.error.exceptions.DataIntegrityValidationException;
import tsp.be.error.exceptions.SingleMessageValidationException;

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
		ObjectId tutorialObjectID = new ObjectId();

		Document tutorialDoc = new Document();
		tutorialDoc.append("_id", tutorialObjectID);
		tutorialDoc.append("name", name);
		tutorialDoc.append("description", description);
		tutorialDoc.append("categoryID", DBUtils.validateAndCreateObjectID(categoryID));
		tutorialDoc.append("authorID", DBUtils.validateAndCreateObjectID(authorID));
		tutorialDoc.append("authorName", authorName);
		tutorialDoc.append("chapters", Collections.emptyList());

		tutorialsCollection.insertOne(tutorialDoc);

		return tutorialObjectID.toString();
	}

	public String addChapter(String tutorialID, String name) {
		ObjectId chapterObjectID = new ObjectId();

		Document chapter = new Document();
		chapter.append("_id", chapterObjectID);
		chapter.append("name", name);
		chapter.append("lessons", Collections.emptyList());

		tutorialsCollection.updateOne(
				Filters.eq("_id", DBUtils.validateAndCreateObjectID(tutorialID)),
				Updates.push("chapters", chapter)
		);

		return chapterObjectID.toString();
	}

	public List<TutorialMetaData> getTutorials(String categoryID, String authorID) {
		Bson filters = null;
		if (categoryID != null && authorID != null) {
			filters = Filters.and(Filters.eq("categoryID", DBUtils.validateAndCreateObjectID(categoryID)), Filters.eq("authorID", DBUtils.validateAndCreateObjectID(authorID)));
		} else if(categoryID != null) {
			filters = Filters.eq("categoryID", DBUtils.validateAndCreateObjectID(categoryID));
		} else if (authorID != null) {
			filters = Filters.eq("authorID", DBUtils.validateAndCreateObjectID(authorID));
		}

		FindIterable<Document> tutorialDocs;
		if (filters != null) tutorialDocs = tutorialsCollection.find(filters);
		else tutorialDocs = tutorialsCollection.find();

		tutorialDocs.projection(Projections.include("_id", "name", "description", "rating", "categoryID", "authorID", "authorName"));

		List<TutorialMetaData> tutorials = new ArrayList<>();
		for(Document tutorialDoc: tutorialDocs) {
			TutorialMetaData tutorial = tutorialDocToTutorialMetaData(tutorialDoc);

			tutorials.add(tutorial);
		}

		return tutorials;
	}

	public List<TutorialMetaData> getTutorials(String authorID) {
		return getTutorials(null, authorID);
	}

	public List<TutorialMetaData> getRecentTutorials(int limit) {
		FindIterable<Document> tutorialDocs = tutorialsCollection.find()
				.projection(Projections.include("_id", "name", "description", "rating", "categoryID", "authorID", "authorName"))
				.sort(Sorts.ascending("createdAt"))
				.limit(limit);

		List<TutorialMetaData> tutorials = new ArrayList<>();
		for(Document tutorialDoc: tutorialDocs) {
			TutorialMetaData tutorial = tutorialDocToTutorialMetaData(tutorialDoc);

			tutorials.add(tutorial);
		}

		return tutorials;
	}

	public TutorialMetaData getTutorialMetaData(String tutorialID) {
		ObjectId tutorialObjectID = DBUtils.validateAndCreateObjectID(tutorialID);

		Document tutorialDoc = tutorialsCollection.find(Filters.eq("_id", tutorialObjectID))
				.projection(Projections.include("_id", "name", "description", "categoryID", "authorID", "authorName"))
				.first();
		DBUtils.validateNotNull(tutorialDoc);

		return tutorialDocToTutorialMetaData(tutorialDoc);
	}

	private TutorialMetaData tutorialDocToTutorialMetaData(Document tutorialDoc) {
		TutorialMetaData tutorial = new TutorialMetaData();
		tutorial.id = tutorialDoc.getObjectId("_id").toString();
		tutorial.name = tutorialDoc.getString("name");
		tutorial.description = tutorialDoc.getString("description");
		tutorial.avgRating = calculateAvgRating((Document) tutorialDoc.get("rating"));
		tutorial.categoryID = tutorialDoc.getObjectId("categoryID").toString();
		tutorial.authorID = tutorialDoc.getObjectId("authorID").toString();
		tutorial.authorName = tutorialDoc.getString("authorName");

		return tutorial;
	}

	public Tutorial getTutorialContents(String tutorialID) {
		Document tutorialDoc = tutorialsCollection.find(Filters.eq("_id", DBUtils.validateAndCreateObjectID(tutorialID))).first();
		if (tutorialDoc == null) throw new SingleMessageValidationException("Tutorial not found");

		Tutorial tutorial = new Tutorial();
		tutorial.id = tutorialDoc.getObjectId("_id").toString();
		tutorial.name = tutorialDoc.getString("name");
		tutorial.description = tutorialDoc.getString("description");
		tutorial.avgRating = calculateAvgRating((Document) tutorialDoc.get("rating"));
		tutorial.authorID = tutorialDoc.getObjectId("authorID").toString();
		tutorial.authorName = tutorialDoc.getString("authorName");
		tutorial.chapters = extractChapters(tutorialDoc.getList("chapters", Document.class));

		return tutorial;
	}

	private Double calculateAvgRating(Document rating) {
		if (rating == null) return null;

		int nOne = (int) rating.getOrDefault("one", 0);
		int nTwo = (int) rating.getOrDefault("two", 0);
		int nThree = (int) rating.getOrDefault("three", 0);
		int nFour = (int) rating.getOrDefault("four", 0);
		int nFive = (int) rating.getOrDefault("five", 0);

		return  (1.0 * nOne + 2.0 * nTwo + 3.0 * nThree + 4.0 * nFour + 5.0 * nFive) / (nOne + nTwo + nThree + nFour + nFive);
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
		ObjectId tutorialObjectID = DBUtils.validateAndCreateObjectID(tutorialID);
		ObjectId chapterObjectID = DBUtils.validateAndCreateObjectID(chapterID);
		ObjectId lessonObjectID = DBUtils.validateAndCreateObjectID(lessonID);

		Document lessonMetaData = new Document();
		lessonMetaData.append("_id", lessonObjectID);
		lessonMetaData.append("name", name);

		UpdateResult result = tutorialsCollection.updateOne(
				Filters.and(Filters.eq("_id", tutorialObjectID), Filters.eq("chapters._id", chapterObjectID)),
				Updates.push("chapters.$.lessons", lessonMetaData)
		);

		if(result.getModifiedCount() == 0)  throw new DataIntegrityValidationException("Invalid tutorial and chapter");
	}

	public void updateLesson(String tutorialID, String chapterID, String lessonID, String name) {
		ObjectId tutorialObjectID = DBUtils.validateAndCreateObjectID(tutorialID);
		ObjectId chapterObjectID = DBUtils.validateAndCreateObjectID(chapterID);
		ObjectId lessonObjectID = DBUtils.validateAndCreateObjectID(lessonID);

		List<Bson> arrayFilters = new ArrayList<>();
		arrayFilters.add(Filters.eq("chapter._id", chapterObjectID));
		arrayFilters.add(Filters.eq("lesson._id", lessonObjectID));

		UpdateResult result = tutorialsCollection.updateOne(
				Filters.eq("_id", tutorialObjectID),
				Updates.set("chapters.$[chapter].lessons.$[lesson].name", name),
				new UpdateOptions().arrayFilters(arrayFilters)
		);

		if(result.getModifiedCount() == 0)  throw new DataIntegrityValidationException("Invalid tutorial and chapter");
	}

	public void updateChapter(String tutorialID, String chapterID, String newName) {
		ObjectId tutorialObjectID = DBUtils.validateAndCreateObjectID(tutorialID);
		ObjectId chapterObjectID = DBUtils.validateAndCreateObjectID(chapterID);

		UpdateResult result = tutorialsCollection.updateOne(
				Filters.and(Filters.eq("_id", tutorialObjectID), Filters.eq("chapters._id", chapterObjectID)),
				Updates.set("chapters.$.name", newName)
		);

		if(result.getModifiedCount() == 0)  throw new DataIntegrityValidationException("Invalid tutorial and chapter");
	}

	public void updateTutorial(String tutorialID, String categoryID, String name, String description) {
		ObjectId tutorialObjectID = DBUtils.validateAndCreateObjectID(tutorialID);
		ObjectId categoryObjectID = DBUtils.validateAndCreateObjectID(categoryID);

		UpdateResult result = tutorialsCollection.updateOne(
				Filters.eq("_id", tutorialObjectID),
				Updates.combine(Updates.set("name", name), Updates.set("description", description), Updates.set("categoryID", categoryObjectID))
		);

		if(result.getModifiedCount() == 0)  throw new DataIntegrityValidationException("Invalid tutorial");
	}

	public void addRating(String tutorialID, String rating) {
		ObjectId tutorialObjectID = DBUtils.validateAndCreateObjectID(tutorialID);

		tutorialsCollection.updateOne(Filters.eq("_id", tutorialObjectID), Updates.inc("rating." + rating, 1));
	}
}
