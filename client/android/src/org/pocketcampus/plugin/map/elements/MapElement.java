package org.pocketcampus.plugin.map.elements;

import org.osmdroid.util.GeoPoint;
import org.pocketcampus.core.plugin.Icon;

public abstract class MapElement {

	protected String title_;
	protected String description_;
	protected GeoPoint position_;
	protected int level_;
	
	public String getTitle() {
		return title_;
	}
	public void setTitle(String title) {
		this.title_ = title;
	}
	public String getDescription() {
		return description_;
	}
	public void setDescription(String description) {
		this.description_ = description;
	}
	public GeoPoint getPosition() {
		return position_;
	}
	public void setPosition(GeoPoint position) {
		this.position_ = position;
	}
	public int getLevel() {
		return level_;
	}
	public void setLevel(int level) {
		this.level_ = level;
	}
	
	public abstract Icon getIcon();
}
