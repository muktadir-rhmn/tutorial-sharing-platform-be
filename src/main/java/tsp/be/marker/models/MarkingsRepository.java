package tsp.be.marker.models;

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

import java.util.ArrayList;
import java.util.List;

@Service
public class MarkingsRepository {
	/** Index: (userID, tutorialID) */
	private final static String MARKINGS_COLLECTION = "markings";
	private MongoCollection<Document> markingsCollection;

	@Autowired
	public MarkingsRepository(DatabaseManager databaseManager) {
		this.markingsCollection = databaseManager.getCollection(MARKINGS_COLLECTION);
	}

	/** inserting lessonID as string, instead of ObjectId, because it will save conversion between them */
	public void mark(String userID, String tutorialID, String lessonID, String mark) {
		ObjectId userObjectID = DBUtils.validateAndCreateObjectID(userID);
		ObjectId tutorialObjectID = DBUtils.validateAndCreateObjectID(tutorialID);
		DBUtils.validateObjectID(lessonID);

		UpdateResult result = markingsCollection.updateOne(
				Filters.and(Filters.eq("userID", userObjectID), Filters.eq("tutorialID", tutorialObjectID)),
				Updates.push(mark, lessonID)
		);

		if (result.getModifiedCount() == 0) {
			List<String> markedLessonIDs = new ArrayList<>();
			markedLessonIDs.add(lessonID);

			Document progressDoc = new Document();
			progressDoc.append("userID", userObjectID);
			progressDoc.append("tutorialID", tutorialObjectID);
			progressDoc.append(mark, markedLessonIDs);

			markingsCollection.insertOne(progressDoc);
		}
	}

	public List<String> getMarkedLessons(String userID, String tutorialID, String mark) {
		ObjectId userObjectID = DBUtils.validateAndCreateObjectID(userID);
		ObjectId tutorialObjectID = DBUtils.validateAndCreateObjectID(tutorialID);

		Document progressDoc = markingsCollection.find(Filters.and(Filters.eq("userID", userObjectID), Filters.eq("tutorialID", tutorialObjectID)))
				.projection(Projections.include(mark))
				.first();
		List<String> markedLessonIDs = (List<String>) progressDoc.get(mark);
		return markedLessonIDs;
	}


	public boolean[] hasMarks(String userID, String tutorialID, String lessonID, String[] marks) {
		ObjectId userObjectID = DBUtils.validateAndCreateObjectID(userID);
		ObjectId tutorialObjectID = DBUtils.validateAndCreateObjectID(tutorialID);

		boolean[] ans = new boolean[marks.length];
		for (int i = 0; i < marks.length; i++) {
			long nMarking = markingsCollection.countDocuments(
					Filters.and(Filters.eq("userID", userObjectID), Filters.eq("tutorialID", tutorialObjectID), Filters.eq(marks[i], lessonID))
			);
			ans[i] = nMarking > 0;
		}

		return ans;
	}

}
