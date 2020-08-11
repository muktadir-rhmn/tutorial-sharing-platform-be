package tsp.be.lesson.models;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
		if (!ObjectId.isValid(lessonID)) throw new DataIntegrityValidationException("Lesson does not exists");

		Document lessonDoc = lessonsCollection.find(Filters.eq("_id", lessonID))
				.projection(Projections.include("tutorialID", "chapterID"))
				.first();
		if (lessonDoc == null) throw new DataIntegrityValidationException("Lesson does not exists");

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
}
