package ru.psavinov.chile.earthquake.exception;

public class UnknownRegionException extends Exception {

	public UnknownRegionException(String string) {
		super("Unknown region name: ".concat(string));
	}

	private static final long serialVersionUID = 5525900818557288031L;

}
