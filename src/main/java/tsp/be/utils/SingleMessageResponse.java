package tsp.be.utils;

public class SingleMessageResponse {
	public String message;

	public SingleMessageResponse(String message) {
		this.message = message;
	}

	public SingleMessageResponse() {
		this.message = "Operation successful";
	}
}
