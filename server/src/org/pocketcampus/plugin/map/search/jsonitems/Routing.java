package org.pocketcampus.plugin.map.search.jsonitems;

/**
 * Class used to deserialize content from the routing server 
 * 
 * @status Complete
 * 
 * @author Jonas
 */
public class Routing {
	public Feature feature;
	public Roadmap roadmap[];
	public boolean success;
}
