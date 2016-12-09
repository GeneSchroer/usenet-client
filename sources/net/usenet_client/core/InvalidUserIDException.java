package cse310client;

public class InvalidUserIDException extends Exception{
    private static final long serialVersionUID = 3L;

	public InvalidUserIDException() {
	}

	public InvalidUserIDException(String s) {
		super(s);
	}
}
