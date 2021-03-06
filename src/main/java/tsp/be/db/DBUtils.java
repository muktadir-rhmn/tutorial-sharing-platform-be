package tsp.be.db;

import org.bson.Document;
import org.bson.types.ObjectId;
import tsp.be.error.exceptions.DataIntegrityValidationException;

public class DBUtils {
	public static void validateObjectID(String id) {
		if (!ObjectId.isValid(id)) throw new DataIntegrityValidationException("Object does not exist.");
	}

	public static ObjectId validateAndCreateObjectID(String id) {
		try{
			return new ObjectId(id);
		} catch (IllegalArgumentException ex) {
			throw new DataIntegrityValidationException("Object does not exist. ");
		}
	}

	public static void validateNotNull(Document document) {
		if (document == null) throw new DataIntegrityValidationException("Object does not exists");
	}
}
