package ch.scythe.hsr.error;

public class EnumNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public EnumNotFoundException(String detailMessage) {
		super(detailMessage);
	}

}
