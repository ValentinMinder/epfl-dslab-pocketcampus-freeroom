package org.pocketcampus.core;

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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id_ == null) ? 0 : id_.hashCode());
		return result;
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
