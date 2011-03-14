package org.pocketcampus.core.plugin;

/**
 * Represents a plugin's unique ID.
 * 
 * @status working, should probably be extended
 * @author florian
 * @license
 */

public class Id {
	String id_;
	
	public Id(String id) {
		id_ = id;
	}
	
	@Override
	public String toString() {
		return id_;
	}

	@Override
	public boolean equals(Object obj) {
		if (getClass() != obj.getClass())
			return false;
		
		Id other = (Id) obj;
		
		if (id_ == null) {
			if (other.id_ != null)
				return false;
		} else if (!id_.equals(other.id_))
			return false;
		return true;
	}
}
