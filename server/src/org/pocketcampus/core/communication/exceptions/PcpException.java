package org.pocketcampus.core.communication.exceptions;

import org.pocketcampus.core.communication.Status;

/**
 * Indicates a problem at the communication service level
 */
public class PcpException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -917632132703067056L;
	
	/**
	 * The status associated with this exception
	 */
	private Status status_;
	
	/**
	 * Constructs a new PcpException with specified cause and PCP Status
	 * @param cause
	 * @param status
	 */
	public PcpException(Throwable cause, Status status) {
		super(cause);
		this.status_ = status;
	}
	
	/**
	 * Constructs a new PcpException with specified message and PCP Status
	 * @param message
	 * @param status
	 */
	public PcpException(String message, Status status) {
		super(status.toString() + " : " + message);
		
		this.status_ = status;
	}
	
	/**
	 * @return the PCP Status code associated with this PcpException
	 */
	public Status getStatus() {
		return this.status_;
	}
}
