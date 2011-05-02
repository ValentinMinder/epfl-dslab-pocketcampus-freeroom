package org.pocketcampus.core.communication.pipe;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * SimpleBox is a very simple and limited message passing mechanism. It provides no
 * synchronization (it is not thread-safe), a single-object buffer, only one state
 * (open, i.e. can be read and written), no blocking mechanism (to wait for a non-empty
 * buffer when reading the buffer), it is unidirectional and allows only one reader and
 * one writer.<br />
 * <br />
 * SimpleBox roughly mimics the basic methods' behavior, which returns one and only one
 * object, and is a mere placeholder for a more complex mechanism where asynchronism is
 * the rule.
 *
 * @param <O> the type of object that the SimpleBox can contains 
 */
public class SimpleBox<O> implements IPipe<O> {
	private O buffer_ = null;
	private BoxReader reader_ = new BoxReader();
	private BoxWriter writer_ = new BoxWriter();
	
	private int freeSpaceInBuffer_ = 1;

	/* (non-Javadoc)
	 * @see org.pocketcampus.core.communication.pipe.IPipe#reader()
	 */
	@Override
	public IPipeReader<O> reader() {
		return reader_;
	}

	@Override
	public IPipeWriter<O> writer() {
		return writer_;
	}

	@Override
	public boolean isOpen() {
		return true;
	}

	@Override
	public void open() {
		throw new NotImplementedException();
	}

	@Override
	public void close() {
		throw new NotImplementedException();
	}
	
	public class BoxReader implements IPipeReader<O> {

		@Override
		public boolean isEmpty() {
			return (buffer_ == null);
		}

		@Override
		public O read() {
			O out = buffer_;
			buffer_ = null;
			return out;
		}

		/**
		 * Throws a <code>NotImplementedException</code> : This message-passing mechanism
		 * is too simple to implement that method.
		 * @throws NotImplementedException
		 */
		@Deprecated
		@Override
		public O blockingRead(long timeout, TimeUnit unit)
				throws InterruptedException, TimeoutException {
			throw new NotImplementedException();
		}

		@Override
		public boolean isOpen() {
			return true;
		}
	}
	
	public class BoxWriter implements IPipeWriter<O> {
		
		/**
		 * Puts the given object in the buffer. In this implementation of
		 * {@code IPipeWriter}, at most one object can be stored in the buffer for its
		 * entire life (it mimics the {@code return} statement, except for the <i>returns
		 * from this method</i> part).
		 * 
		 * @throws IllegalStateException if this method is called more than once
		 */
		@Override
		synchronized
		public void write(O message) {
			freeSpaceInBuffer_--;
			if (freeSpaceInBuffer_ == 0)
				buffer_ = message;
			else
				throw new IllegalStateException("The buffer already contains one object");
		}

		/**
		 * This implementation does nothing. It is however good practice to put it at the
		 * end of your method, before the return statement, in order to allow smooth future
		 * improvement of the message-passing mechanism.
		 */
		@Override
		public void close() {
			// Does nothing
		}

		@Override
		public boolean isOpen() {
			return true;
		}
		
	}
}
