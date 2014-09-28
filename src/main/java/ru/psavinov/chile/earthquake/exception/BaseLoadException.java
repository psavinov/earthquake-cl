package ru.psavinov.chile.earthquake.exception;

public class BaseLoadException extends Exception {

	public BaseLoadException(Throwable th) {
		super(th.getMessage(), th.getCause());
	}

	private static final long serialVersionUID = 4224886534895394230L;

}
