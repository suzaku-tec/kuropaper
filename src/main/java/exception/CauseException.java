package exception;

public class CauseException extends RuntimeException {
	public CauseException(String message) {
		super(message);
	}

	public CauseException(String message, Throwable cause) {
		super(message, cause);
	}

	public CauseException(Throwable cause) {
		super(cause);
	}

	public CauseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
