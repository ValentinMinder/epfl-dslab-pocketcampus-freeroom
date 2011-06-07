package org.pocketcampus.plugin.map.ui;

public interface OnLevelBarChangeListener {

	/**
	 * Is called when a new level is wanted
	 * @param level the wanted level
	 */
	public void onLevelChanged(int level);
	
	/**
	 * Called when the level bar is being seek.
	 * @param level the current level
	 */
	public void onLevelChanging(int level);
	
}
