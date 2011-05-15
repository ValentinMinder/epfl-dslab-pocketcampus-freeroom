package org.pocketcampus.core.communication.pcp;

public class CommunicationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3003770985976406817L;

	public CommunicationException() {
		
	}

	public CommunicationException(String detailMessage) {
		super(detailMessage);
	}

	public CommunicationException(Throwable throwable) {
		super(throwable);
	}

	public CommunicationException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
