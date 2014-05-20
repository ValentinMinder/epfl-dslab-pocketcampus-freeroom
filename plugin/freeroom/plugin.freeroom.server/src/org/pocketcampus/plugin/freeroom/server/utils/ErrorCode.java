package org.pocketcampus.plugin.freeroom.server.utils;

public class ErrorCode {
	private int code;
	private String comment;
	
	public ErrorCode(int code, String commentStatus) {
		this.code = code;
		this.comment = commentStatus;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
