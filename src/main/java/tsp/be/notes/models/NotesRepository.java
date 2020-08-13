package tsp.be.notes.models;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tsp.be.db.DBUtils;
import tsp.be.db.DatabaseManager;

@Service
public class NotesRepository {
	private final static String NOTES_COLLECTION_NAME = "notes";
	private MongoCollection<Document> notesCollection;

	@Autowired
	public NotesRepository(DatabaseManager databaseManager) {
		notesCollection = databaseManager.getCollection(NOTES_COLLECTION_NAME);
	}

	public void addOrUpdateNote(String userID, String lessonID, String note) {
		ObjectId userObjectID = DBUtils.validateAndCreateObjectID(userID);
		ObjectId lessonObjectID = DBUtils.validateAndCreateObjectID(lessonID);

		UpdateResult result = notesCollection.updateOne(
				Filters.and(Filters.eq("userID", userObjectID), Filters.eq("lessonID", lessonObjectID)),
				Updates.set("note", note)
		);

		if (result.getModifiedCount() == 0) {
			Document noteDoc = new Document();
			noteDoc.append("_id", new ObjectId());
			noteDoc.append("userID", userObjectID);
			noteDoc.append("lessonID", lessonObjectID);
			noteDoc.append("note", note);

			notesCollection.insertOne(noteDoc);
		}
	}

	public String getNote(String userID, String lessonID) {
		ObjectId userObjectID = DBUtils.validateAndCreateObjectID(userID);
		ObjectId lessonObjectID = DBUtils.validateAndCreateObjectID(lessonID);

		Document noteDoc = notesCollection.find(Filters.and(Filters.eq("userID", userObjectID), Filters.eq("lessonID", lessonObjectID)))
				.projection(Projections.include("note"))
				.first();
		if (noteDoc == null) return "";

		return noteDoc.getString("note");
	}
}
