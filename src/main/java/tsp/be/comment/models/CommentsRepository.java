package tsp.be.comment.models;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tsp.be.db.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

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

	public List<Comment> getComments(String lessonID, Integer pageNo, Integer pageSize) {
		FindIterable<Document> commentDocs = commentsCollection.find(Filters.eq("lessonID", new ObjectId(lessonID)))
				.sort(Sorts.ascending("createdAt"));

		if (pageNo != null && pageSize != null){
			commentDocs = commentDocs.skip(pageNo * pageSize).limit(pageSize);
		}

		List<Comment> comments = new ArrayList<>();
		for(Document commentDoc: commentDocs) {
			Comment comment = new Comment();
			comment.id = commentDoc.getObjectId("_id").toString();
			comment.lessonID = commentDoc.getObjectId("lessonID").toString();
			comment.commenterID = commentDoc.getObjectId("commenterID").toString();
			comment.commenterName = commentDoc.getString("commenterName");
			comment.body = commentDoc.getString("body");
			comment.createdAt = commentDoc.getLong("createdAt");
			comment.updatedAt = commentDoc.getLong("updatedAt");

			comments.add(comment);
		}

		return comments;
	}

	public int countTotalComments(String lessonID) {
		return (int) commentsCollection.countDocuments(Filters.eq("_id", new ObjectId(lessonID)));
	}
}
