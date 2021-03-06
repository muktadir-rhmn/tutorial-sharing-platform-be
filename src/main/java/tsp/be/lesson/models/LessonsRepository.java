package tsp.be.lesson.models;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tsp.be.db.DBUtils;
import tsp.be.db.DatabaseManager;
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
		lessonDoc.append("tutorialID", DBUtils.validateAndCreateObjectID(tutorialID));
		lessonDoc.append("chapterID", DBUtils.validateAndCreateObjectID(chapterID));
		lessonDoc.append("name", name);
		lessonDoc.append("body", body);
		lessonDoc.append("createdAt", System.currentTimeMillis());
		lessonDoc.append("updatedAt", System.currentTimeMillis());

		lessonsCollection.insertOne(lessonDoc);

		return lessonID.toString();
	}

	//todo: make it transactional
	public void updateLesson(String lessonID, String name, String body) {
		ObjectId lessonObjectID = DBUtils.validateAndCreateObjectID(lessonID);

		Document lessonDoc = lessonsCollection.find(Filters.eq("_id", lessonObjectID))
				.projection(Projections.include("tutorialID", "chapterID"))
				.first();
		DBUtils.validateNotNull(lessonDoc);

		lessonsCollection.updateOne(
				Filters.eq("_id", lessonObjectID),
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
		ObjectId lessonObjectID = DBUtils.validateAndCreateObjectID(lessonID);

		Document lessonDoc = lessonsCollection.find(Filters.eq("_id", lessonObjectID)).first();
		DBUtils.validateNotNull(lessonDoc);

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
