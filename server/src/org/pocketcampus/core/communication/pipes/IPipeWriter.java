package org.pocketcampus.core.communication.pipes;

public interface IPipeWriter<T> {
	/**
	 * Puts the given message in the pipe
	 * @param message
	 */
	public void write(T message);
	
	/**
	 * Closes this writer. Following calls to <code>write</code> won't succeed with this
	 * writer.
	 */
	public void close();
	
	/**
	 * Checks whether this writer is open. A pipe writer is open if its pipe is open
	 * and it is open itself.
	 * @return true iff this writer and its pipe are open
	 */
	public boolean isOpen();
}
