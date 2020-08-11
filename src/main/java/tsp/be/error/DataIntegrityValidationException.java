package tsp.be.error;

public class DataIntegrityValidationException extends ValidationException {

	public DataIntegrityValidationException(String message) {
		this.errors.put("code", errorCodes.DataIntegrityError.ordinal());
		this.errors.put("message", message);
	}
}
