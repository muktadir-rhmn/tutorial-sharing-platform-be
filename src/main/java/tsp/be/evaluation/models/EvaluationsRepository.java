package tsp.be.evaluation.models;

import com.mongodb.client.FindIterable;
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
import tsp.be.evaluation.external.EvaluationUpdateNotifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class EvaluationsRepository {
	private final static String EVALUATIONS_COLLECTION_NAME = "evaluations";
	private MongoCollection<Document> evaluationsCollection;
	private EvaluationUpdateNotifier evaluationUpdateNotifier;

	@Autowired
	public EvaluationsRepository(DatabaseManager databaseManager, EvaluationUpdateNotifier evaluationUpdateNotifier) {
		this.evaluationsCollection = databaseManager.getCollection(EVALUATIONS_COLLECTION_NAME);
		this.evaluationUpdateNotifier = evaluationUpdateNotifier;
	}

	public void addEvaluation(String userID, String itemID, String evaluation, String itemType) {
		ObjectId userObjectID = DBUtils.validateAndCreateObjectID(userID);
		ObjectId itemObjectID = DBUtils.validateAndCreateObjectID(itemID);

		Document evaluationDoc = new Document();
		evaluationDoc.append("userID", userObjectID);
		evaluationDoc.append("itemID", itemObjectID);
		evaluationDoc.append("evaluation", evaluation);

		evaluationsCollection.insertOne(evaluationDoc);

		evaluationUpdateNotifier.notifyAddEvaluation(itemType, itemID, evaluation);
	}


	public List<String> getEvaluations(String userID, List<String> itemIDs) {
		ObjectId userObjectID = DBUtils.validateAndCreateObjectID(userID);
		List<ObjectId> itemObjectIDs = new ArrayList<>();
		for (String itemID: itemIDs) {
			itemObjectIDs.add(DBUtils.validateAndCreateObjectID(itemID));
		}

		FindIterable<Document> evaluationDocs = evaluationsCollection.find(
				Filters.and(Filters.eq("userID", userObjectID), Filters.in("itemID", itemObjectIDs))
		).projection(Projections.include("itemID", "evaluation"));

		HashMap<String, String> evaluationMap = new HashMap<>();
		for (Document evaluationDoc: evaluationDocs) {
			evaluationMap.put(evaluationDoc.getObjectId("itemID").toString(), evaluationDoc.getString("evaluation"));
		}

		List<String> evaluations = new ArrayList<>();
		for (String itemID: itemIDs) {
			evaluations.add(evaluationMap.get(itemID));
		}
		return evaluations;
	}
}
