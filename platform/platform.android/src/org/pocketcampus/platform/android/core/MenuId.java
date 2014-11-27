package org.pocketcampus.platform.android.core;



/**
 * Represents a menu unique identifier, used to check if a plugin has to be displayed on a given menu.
 * 
 * @author Florian <florian.laurent@epfl.ch>
 */
public class MenuId {
	private int mId;
	
	public MenuId(int mainscreenMenuId) {
		mId = mainscreenMenuId;
		System.out.println("New menu: " + this);
	}
	
	private int getId() {
		return mId;
	}
	
	@Override
	public boolean equals(Object otherMenuId) {
		if(otherMenuId instanceof MenuId) {
			return (((MenuId) otherMenuId).getId() == this.getId());
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return "[id:" + mId + "]";
	}
}
