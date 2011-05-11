package org.pocketcampus.shared.plugin.social.permissions;

public class Permission {
	private String name_;
	
	public Permission(String name) {
		this.name_ = name;
	}
	
	public String getName() {
		return name_;
	}
	
	@Override
	public int hashCode() {
		return name_.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Permission) {
			Permission other = (Permission) o;
			return this.name_.equals(other.name_);
		}
		return false;
	}
}
