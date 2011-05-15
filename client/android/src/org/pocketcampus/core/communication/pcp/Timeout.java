package org.pocketcampus.core.communication.pcp;

import java.util.concurrent.TimeUnit;

public class Timeout {
	private static Timeout instance_ = new Timeout();
	
	private TimeUnit timeUnit_;
	private long connectionTimeout_;
	private long socketTimeout_;
	
	/**
	 * Creates an instance with default values.
	 */
	private Timeout() {
		timeUnit_ = TimeUnit.SECONDS;
		connectionTimeout_ = 2;
		socketTimeout_ = 4;
	}
	
	/**
	 * Creates an instance with given timeout values.
	 * @param connectionTimeout Timeout for the connection to be established
	 * @param socketTimeout Socket timeout (for waiting data)
	 * @param timeUnit
	 */
	public Timeout(long connectionTimeout, long socketTimeout, TimeUnit timeUnit) {
		connectionTimeout_ = connectionTimeout;
		socketTimeout_ = socketTimeout;
		timeUnit_ = timeUnit;
	}
	
	/**
	 * @return an instance of Timeout with default values.
	 */
	public static Timeout getDefaultTimeout() {
		return instance_;
	}
	
	/**
	 * Returns the connection timeout (timeout for the connection to be established).
	 * @param tu unit of the returned timeout value
	 * @return
	 */
	public long connection(TimeUnit tu) {
		return tu.convert(connectionTimeout_, timeUnit_);
	}
	
	/**
	 * Returns the socket timeout (for waiting data)
	 * @param tu unit of the returned timeout value
	 * @return
	 */
	public long socket(TimeUnit tu) {
		return tu.convert(socketTimeout_, timeUnit_);
	}
	
	/**
	 * Returns the total timeout, which corresponds to the connection plus the socket timeout.
	 * @param tu unit of the returned timeout value
	 * @return
	 */
	public long total(TimeUnit tu) {
		return tu.convert(connectionTimeout_ + socketTimeout_, timeUnit_);
	}
}
