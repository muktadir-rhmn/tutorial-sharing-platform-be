package tsp.be.error.exceptions;

public class DataIntegrityValidationException extends ValidationException {

	public DataIntegrityValidationException(String message) {
		this.errors.put("code", errorCodes.DataIntegrityError.ordinal());
		this.errors.put("message", message);
	}
}
