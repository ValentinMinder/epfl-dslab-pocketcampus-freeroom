package org.pocketcampus.shared.plugin.social.exception;

@SuppressWarnings("serial")
public class ConflictingPermissionException extends RuntimeException {
	public ConflictingPermissionException(String msg) {
		super(msg);
	}
}
