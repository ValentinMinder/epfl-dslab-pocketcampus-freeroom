package org.pocketcampus.plugin.map.server.jsonitems;

/**
 * Class used to deserialize content from the routing server 
 * 
 * @status Complete
 * 
 * @author Jonas
 */
public class Roadmap {
	public int id;
	public String type;
	public GeometryR geometry;
	public Properties properties;
}
