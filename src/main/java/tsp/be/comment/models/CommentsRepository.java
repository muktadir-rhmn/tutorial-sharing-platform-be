package tsp.be.comment.models;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tsp.be.db.DatabaseManager;

@Service
public class CommentsRepository {
	private final static String COMMENTS_COLLECTION_NAME = "comments";

	private MongoCollection<Document> commentsCollection;

	@Autowired
	public CommentsRepository(DatabaseManager databaseManager) {
		commentsCollection = databaseManager.getCollection(COMMENTS_COLLECTION_NAME);
	}

	public String addComment(String commenterID, String commenterName, String lessonID, String commentBody) {
		ObjectId commentIDObject = new ObjectId();

		Document commentDoc = new Document();
		commentDoc.append("_id", commentIDObject);
		commentDoc.append("lessonID", new ObjectId(lessonID));
		commentDoc.append("commenterID", new ObjectId(commenterID));
		commentDoc.append("commenterName", commenterName);
		commentDoc.append("body", commentBody);
		commentDoc.append("createdAt", System.currentTimeMillis());
		commentDoc.append("updatedAt", System.currentTimeMillis());

		commentsCollection.insertOne(commentDoc);

		return commentIDObject.toString();
	}
}
