package tsp.be.lesson.models;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tsp.be.db.DBValidator;
import tsp.be.db.DatabaseManager;
import tsp.be.error.DataIntegrityValidationException;
import tsp.be.error.SingleMessageValidationException;
import tsp.be.tutorial.external.LessonDeNormalizationHandler;

@Service
public class LessonsRepository {
	private final static String LESSONS_COLLECTION_NAME = "lessons";
	@Autowired
	private LessonDeNormalizationHandler lessonDeNormalizationHandler;
	private MongoCollection<Document> lessonsCollection;

	@Autowired
	LessonsRepository(DatabaseManager databaseManager) {
		lessonsCollection = databaseManager.getCollection(LESSONS_COLLECTION_NAME);
	}

	//todo: make it transactional
	public String addLesson(String tutorialID, String chapterID, String name, String body) {
		ObjectId lessonID = new ObjectId();

		lessonDeNormalizationHandler.addLesson(tutorialID, chapterID, lessonID.toString(), name);

		Document lessonDoc = new Document();
		lessonDoc.append("_id", lessonID);
		lessonDoc.append("tutorialID", new ObjectId(tutorialID));
		lessonDoc.append("chapterID", new ObjectId(chapterID));
		lessonDoc.append("name", name);
		lessonDoc.append("body", body);
		lessonDoc.append("createdAt", System.currentTimeMillis());
		lessonDoc.append("updatedAt", System.currentTimeMillis());

		lessonsCollection.insertOne(lessonDoc);

		return lessonID.toString();
	}

	//todo: make it transactional
	public void updateLesson(String lessonID, String name, String body) {
		DBValidator.validateObjectID("Lesson", lessonID);

		Document lessonDoc = lessonsCollection.find(Filters.eq("_id", new ObjectId(lessonID)))
				.projection(Projections.include("tutorialID", "chapterID"))
				.first();
		DBValidator.validateNotNull("Lesson", lessonDoc);

		lessonsCollection.updateOne(
				Filters.eq("_id", lessonID),
				Updates.combine(
						Updates.set("name", name),
						Updates.set("body", body),
						Updates.set("updatedAt", System.currentTimeMillis())
				)
		);

		String tutorialID = lessonDoc.getObjectId("tutorialID").toString();
		String chapterID = lessonDoc.getObjectId("chapterID").toString();

		lessonDeNormalizationHandler.updateLesson(tutorialID, chapterID, lessonID, name);
	}

	public Lesson getLesson(String lessonID) {
		DBValidator.validateObjectID("Lesson", lessonID);

		Document lessonDoc = lessonsCollection.find(Filters.eq("_id", new ObjectId(lessonID))).first();
		DBValidator.validateNotNull("Lesson", lessonDoc);

		Lesson lesson = new Lesson();
		lesson.id = lessonDoc.getObjectId("_id").toString();
		lesson.tutorialID = lessonDoc.getObjectId("tutorialID").toString();
		lesson.chapterID = lessonDoc.getObjectId("chapterID").toString();
		lesson.name = lessonDoc.getString("name");
		lesson.body = lessonDoc.getString("body");
		lesson.createdAt = lessonDoc.getLong("createdAt");
		lesson.updatedAt = lessonDoc.getLong("updatedAt");

		return lesson;
	}
}
