package org.pocketcampus.core.communication.pipe;

/**
 * A Pipe is a message-passing mechanism which basically allows to write something at
 * one of its end, and read it at the other end, in a queue-like fashion.
 *
 * @param <T> the type of messages (objects) that this pipe can handle
 */
public interface IPipe<T> {
	/**
	 * Retrieve a reader for this pipe. A reader can retrieve objects at the read-end
	 * of this pipe.
	 * @return a reader for this pipe
	 */
	public IPipeReader<T> reader();
	
	/**
	 * Retrieves a writer for this pipe. A writer can put objects in the pipe from its
	 * write-end.
	 * @return a writer for this pipe
	 */
	public IPipeWriter<T> writer();
	
	/**
	 * Checks whether this pipe is open or not. A closed pipe cannot be written into.
	 * Whether it can be read from if closed is left to the implementations.
	 * @return true iff the pipe is open
	 */
	public boolean isOpen();
	
	/**
	 * Opens this pipe. Should not open writers or readers attached to this pipe.
	 */
	public void open();
	
	/**
	 * Closes this pipe. Should not close writers or readers attached to this pipe.
	 */
	public void close();
}
