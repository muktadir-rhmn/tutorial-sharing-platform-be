package tsp.be.db;

import org.bson.Document;
import org.bson.types.ObjectId;
import tsp.be.error.DataIntegrityValidationException;

public class DBValidator {
	public static void validateObjectID(String fieldName, String objectID) {
		if (!ObjectId.isValid(objectID)) throw new DataIntegrityValidationException(fieldName + " does not exists");
	}

	public static void validateNotNull(String fieldName, Document document) {
		if (document == null) throw new DataIntegrityValidationException(fieldName + " does not exists");
	}
}
