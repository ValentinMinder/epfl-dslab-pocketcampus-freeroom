package org.pocketcampus.core.exception;

public class ServerException extends Exception {

	private static final long serialVersionUID = -7108977658617157470L;
	private String explanation_;

	public ServerException(String explanation) {
		explanation_ = explanation;
	}
	
	public ServerException() {
		explanation_ = "No explanation available.";
	}

	public String getExplanation() {
		return explanation_;
	}
}
