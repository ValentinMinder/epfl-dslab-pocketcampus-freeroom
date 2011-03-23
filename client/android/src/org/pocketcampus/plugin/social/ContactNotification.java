package org.pocketcampus.plugin.social;

import java.io.Serializable;

public class ContactNotification implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8058621944503285324L;
	private final Username from_;
	private final Username to_;
	
	public ContactNotification(Username from, Username to) {
		from_ = from;
		to_ = to;
	}
	
	public Username getFrom() {
		return from_;
	}
	
	public Username getTo() {
		return to_;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof ContactNotification) {
			ContactNotification oo = (ContactNotification) o;
			if(from_.equals(oo.getFrom()) && to_.equals(oo.getTo())) return true;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
	
	@Override
	public String toString() {
		return "FROM:"+from_+"-TO:"+to_;
	}
}
