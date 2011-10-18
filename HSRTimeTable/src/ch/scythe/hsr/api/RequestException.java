package ch.scythe.hsr.api;

public class RequestException extends Exception {
	private static final long serialVersionUID = 1L;

	public RequestException(String message) {
		super(message);
	}

	public RequestException(Exception e) {
		super(e);
	}

}
