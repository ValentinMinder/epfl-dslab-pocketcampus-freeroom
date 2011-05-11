package org.pocketcampus.core.communication.exceptions;

import org.pocketcampus.core.communication.PcpStatus;

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
	private PcpStatus status_;
	
	/**
	 * Constructs a new PcpException with specified cause and PCP Status
	 * @param cause
	 * @param status
	 */
	public PcpException(Throwable cause, PcpStatus status) {
		super(cause);
		this.status_ = status;
	}
	
	/**
	 * Constructs a new PcpException with specified message and PCP Status
	 * @param message
	 * @param status
	 */
	public PcpException(String message, PcpStatus status) {
		super(status.toString() + " : " + message);
		
		this.status_ = status;
	}
	
	/**
	 * @return the PCP Status code associated with this PcpException
	 */
	public PcpStatus getStatus() {
		return this.status_;
	}
}
