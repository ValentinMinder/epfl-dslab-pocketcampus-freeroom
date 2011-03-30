package org.pocketcampus.plugin.map;

import javax.servlet.http.HttpServletRequest;

import org.pocketcampus.core.router.IServerBase;
import org.pocketcampus.core.router.PublicMethod;

public class Map implements IServerBase {

	@PublicMethod
	public String map(HttpServletRequest request) {
		return "I am MAP ";
	}
	
	@PublicMethod
	public String hello(HttpServletRequest request) {
		return "Hello World";
	}
	
	@PublicMethod
	public String bonjour(HttpServletRequest request) {
		return "Bonjour Monde";
	}
	
	@PublicMethod
	public String hola(HttpServletRequest request) {
		return "Hola mundo";
	}
	
	@PublicMethod
	public Object arr(HttpServletRequest request) {
		return new String[] {"Hi", "guys", "huhu"};
	}
	
	@PublicMethod
	public String getLayers(HttpServletRequest request) {
		return "{layers: [{id: 1, name: \"Restaurants\", description: \"List of restaurants\", icon: \"http://....\", cache: -1}, {id: 2, name: \"Rooms\", description: \"Rooms at EPFL\", icon: \"http://....\", cache: 60}, {id: 3, name: \"Friends\", description: \"Where your friends are\", icon: \"http://....\", cache: 0}]}";
	}
	
	@PublicMethod
	public String getItems(HttpServletRequest request) {
		return "{layers: [{id: 1, items: [{name: \"Corbu\", description: \"Super\", longitude: \"...\", latitude: \"...\", altitude: \"...\"}, {name: \"Parmentier\", description: \"Numéro 318 s'il vous plait!\", longitude: \"...\", latitude: \"...\", altitude: \"...\"}]}, {id: 3, items: [{name: \"Jonas\", description: \"Youpi\", longitude: \"...\", latitude: \"...\", altitude: \"...\"}, {name: \"Johan\", description: \"What a good jerk dancer !\", longitude: \"...\", latitude: \"...\", altitude: \"...\"}]}]}";
	}

	public String getDefaultMethod() {
		return "map";
	}
}
