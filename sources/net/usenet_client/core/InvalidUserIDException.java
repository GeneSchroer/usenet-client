package net.usenet_client.core;

public class InvalidUserIDException extends Exception{
    private static final long serialVersionUID = 3L;

	public InvalidUserIDException() {
	}

	public InvalidUserIDException(String s) {
		super(s);
	}
}

