package tsp.be.lesson.models;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tsp.be.db.DatabaseManager;
import tsp.be.error.SimpleValidationException;
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

	public String addLesson(String tutorialID, String chapterID, String name, String body) {
		ObjectId lessonID = new ObjectId();

		Document lessonDoc = new Document();
		lessonDoc.append("_id", lessonID);
		lessonDoc.append("tutorialID", new ObjectId(tutorialID));
		lessonDoc.append("chapterID", new ObjectId(chapterID));
		lessonDoc.append("name", name);
		lessonDoc.append("body", body);
		lessonDoc.append("createdAt", System.currentTimeMillis());
		lessonDoc.append("updatedAt", System.currentTimeMillis());

		lessonDeNormalizationHandler.addLesson(tutorialID, chapterID, lessonID.toString(), name);

		lessonsCollection.insertOne(lessonDoc);

		return lessonID.toString();
	}
}
