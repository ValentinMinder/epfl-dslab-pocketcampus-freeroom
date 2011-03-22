package org.pocketcampus.plugin.social;

import java.io.Serializable;

public class SessionId implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5050210406172679090L;

	private final String id_;
	
	public final static int SESSION_ID_SIZE = 128;
	public final static String DEAD_SESSION_ID = //128 0's
		"00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
	
	public SessionId(String id) {
		id_ = id;
	}
	
	public String getId() {
		return id_;
	}
	
	public String toString() {
		return getId();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof SessionId) {
			SessionId oo = (SessionId) o;
			return this.id_.equals(oo.getId());
		}
		return false;
	}
}
