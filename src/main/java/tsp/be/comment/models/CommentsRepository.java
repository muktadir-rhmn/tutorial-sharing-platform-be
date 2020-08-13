package tsp.be.comment.models;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tsp.be.db.DBUtils;
import tsp.be.db.DatabaseManager;

import java.util.ArrayList;
import java.util.Collections;
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
		ObjectId commenterObjectID = DBUtils.validateAndCreateObjectID(commenterID);
		ObjectId lessonObjectID = DBUtils.validateAndCreateObjectID(lessonID);
		ObjectId commentObjectID = new ObjectId();

		Document commentDoc = new Document();
		commentDoc.append("_id", commentObjectID);
		commentDoc.append("lessonID", lessonObjectID);
		commentDoc.append("commenterID", commenterObjectID);
		commentDoc.append("commenterName", commenterName);
		commentDoc.append("body", commentBody);
		commentDoc.append("replies", Collections.emptyList());
		commentDoc.append("createdAt", System.currentTimeMillis());
		commentDoc.append("updatedAt", System.currentTimeMillis());

		commentsCollection.insertOne(commentDoc);

		return commentObjectID.toString();
	}

	public List<Comment> getComments(String lessonID, Integer pageNo, Integer pageSize) {
		ObjectId lessonObjectID = DBUtils.validateAndCreateObjectID(lessonID);

		FindIterable<Document> commentDocs = commentsCollection.find(Filters.eq("lessonID", lessonObjectID))
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
			comment.replies = extractReplies((List<Document>) commentDoc.get("replies"));
			comment.createdAt = commentDoc.getLong("createdAt");
			comment.updatedAt = commentDoc.getLong("updatedAt");

			comments.add(comment);
		}

		return comments;
	}

	private List<Reply> extractReplies(List<Document> replyDocs) {
		List<Reply> replies = new ArrayList<>();
		for (Document replyDoc: replyDocs) {
			Reply reply = new Reply();
			reply.id = replyDoc.getObjectId("_id").toString();
			reply.body = replyDoc.getString("body");
			reply.commenterID = replyDoc.getObjectId("commenterID").toString();
			reply.commenterName = replyDoc.getString("commenterName");
			reply.createdAt = replyDoc.getLong("createdAt");
			reply.updatedAt = replyDoc.getLong("updatedAt");

			replies.add(reply);
		}
		return replies;
	}

	public int countTotalComments(String lessonID) {
		ObjectId lessonObjectID = DBUtils.validateAndCreateObjectID(lessonID);
		return (int) commentsCollection.countDocuments(Filters.eq("lessonID", lessonObjectID));
	}

	public String addReply(String commenterID, String commenterName, String parentCommentID, String commentBody) {
		ObjectId commenterObjectID = DBUtils.validateAndCreateObjectID(commenterID);
		ObjectId parentCommentObjectID = DBUtils.validateAndCreateObjectID(parentCommentID);
		ObjectId replyObjectID = new ObjectId();

		Document replyDoc = new Document();
		replyDoc.append("_id", replyObjectID);
		replyDoc.append("body", commentBody);
		replyDoc.append("commenterID", commenterObjectID);
		replyDoc.append("commenterName", commenterName);
		replyDoc.append("createdAt", System.currentTimeMillis());
		replyDoc.append("updatedAt", System.currentTimeMillis());

		commentsCollection.updateOne(Filters.eq("_id", parentCommentObjectID), Updates.push("replies", replyDoc));
		return commenterObjectID.toString();
	}

}
