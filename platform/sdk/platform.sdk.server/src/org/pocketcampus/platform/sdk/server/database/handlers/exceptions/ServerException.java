package org.pocketcampus.platform.sdk.server.database.handlers.exceptions;

public class ServerException extends Exception {

	private static final long serialVersionUID = -9190624100724525654L;
	private static final String defaultMessage_ = "No explanation available";
	
	public ServerException() {
		super(defaultMessage_);
	}
	
	public ServerException(String explanation) {
		super(explanation);
	}
	
	public ServerException(Throwable t) {
		super(defaultMessage_, t);
	}
	
	public ServerException(String explanation, Throwable t) {
		super(explanation, t);
	}
	
	/**
	 * Returns the explanation of this ServerError.
	 * @return the explanation of this ServerError instance
	 */
	public String getExplanation() {
		return super.getMessage();
	}
}
