package org.pocketcampus.core.communication.pipes;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface IPipeReader<T> {
	/**
	 * Checks whether the pipe is empty or not.
	 * @return true iff the pipe is empty
	 */
	public boolean isEmpty();
	
	/**
	 * Reads and removes the first object on the reader's side of the pipe.
	 * @return the first object on the reader's side of the pipe, or null if the pipe
	 * is empty
	 */
	public T read();
	
	/**
	 * Reads and removes the first object on the reader's side of the pipe. If the pipe is
	 * empty, waits for something to be written on the writer side of the pipe, but waits
	 * at most the given timeout. If the timeout is reached and nothing was written to the
	 * pipe, a TimeoutException is thrown.
	 * @param timeout the maximum time to wait
	 * @param unit the time unit of the timeout argument
	 * @return the first object on the reader's side of the pipe
	 * @throws InterruptedException if the current thread is interrupted
	 * @throws TimeoutException if the timeout is reached and the pipe was still empty
	 * @throws NullPointerException if the unit argument is null
	 */
	public T blockingRead(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException;
	
	/**
	 * @return true iff the pipe is open
	 */
	public boolean isOpen();
}
