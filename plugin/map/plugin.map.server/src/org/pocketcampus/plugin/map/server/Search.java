package org.pocketcampus.plugin.map.server;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import org.pocketcampus.plugin.map.common.CoordinateConverter;
import org.pocketcampus.plugin.map.common.Position;
import org.pocketcampus.plugin.map.server.jsonitems.BasicSearchResponse;
import org.pocketcampus.plugin.map.server.jsonitems.GeometryR;
import org.pocketcampus.plugin.map.server.jsonitems.SearchProperties;
import org.pocketcampus.plugin.map.shared.MapItem;

import com.google.gson.Gson;


/**
 * Class used to do routing search 
 * 
 * @status WIP
 * 
 * @author Jonas, Johan
 *
 */
public class Search {

	/**
	 * Returns the POI ID closest to the given position
	 * 
	 * @param person Position where we look for a POI
	 * @return -1 if not POI found
	 */
//	public static int getClosestPOI(Position person) {
//
//		return getVertexId(mapDB_.getTitleClosestPOI(person));
//	}
	
	/**
	 * Get the vertex_id of a given element. We need to fetch it every time because
	 * it changes often (too bad...)
	 * @param title the title of the element we want (= field 'text')
	 * @return the vertex_id of the element
	 */
//	private static int getVertexId(String title) {
//		URL searchUrl = null;
//		try {
//			searchUrl = new URL("http://plan.epfl.ch/search?keyword=" + URLEncoder.encode(title, "UTF-8"));
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//			return -1;
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//			return -1;
//		}
//		
//		Gson gson = new Gson();
//		int vertexId = -1;
//		try {
//			InputStreamReader reader = new InputStreamReader(searchUrl.openStream());
//			BasicSearchResponse response = gson.fromJson(reader, BasicSearchResponse.class);
//			if(response != null && response.features != null && response.features.length > 0) {
//				String vid = response.features[0].properties.vertex_id;
//				if(vid != null && !vid.equals("null"))
//					vertexId = Integer.parseInt(response.features[0].properties.vertex_id);
//			}
//		} catch (Exception e) {
//			System.err.println(e.toString());
//			return -1;
//		}
//		return vertexId;
//	}
	
	/**
	 * Queries the plan.epfl.ch website and return the results
	 * @param text the query.
	 * @param max the maximum number of results.
	 * @return A list of MapElementBean containing the results.
	 */
	public static List<MapItem> searchTextOnEpflWebsite(String text, int max) {
		List<MapItem> list = new LinkedList<MapItem>();
		URL searchUrl = null;
		try {
			searchUrl = new URL("http://plan.epfl.ch/search?keyword=" + URLEncoder.encode(text, "UTF-8"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return list;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return list;
		}
		
		Gson gson = new Gson();

		try {
			InputStreamReader reader = new InputStreamReader(searchUrl.openStream());
			BasicSearchResponse response = gson.fromJson(reader, BasicSearchResponse.class);
			if(response != null && response.features != null) {
				for(int i = 0; i < response.features.length && i < max; i++) {
					GeometryR geometry = response.features[i].geometry;
					SearchProperties properties = response.features[i].properties;
					if(geometry != null && properties != null) {
						String description = null;
						if(properties.room != null && properties.room.length() > 0) {
							description = properties.room;
						}
						Position p = CoordinateConverter.convertEPSG4326ToLatLong(geometry.coordinates[0], geometry.coordinates[1], 0);
						MapItem mapItem = new MapItem(properties.text, p.getLatitude(), p.getLongitude(), -1, -1);
						if (description != null) {
							mapItem.setDescription(description);
						}
						
						try {
							int floor = Integer.parseInt(properties.floor);
							mapItem.setFloor(floor);	
						} catch (Exception e){};
						
						mapItem.setCategory(properties.category);
						
						list.add(mapItem);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return list;
		}
		return list;
	}

	/**
	 * Loop for a path between the user position and a certain POI.
	 * 
	 * @param userPosition Position of the user
	 * @param endMapElement ID of the POI
	 * @param bike Whether we want to use the path with a bike
	 * @return List of points of the path
	 */
//	public static List<Position> searchPathBetween(Position userPosition, int endMapElement, boolean bike) {
//		int poi = getClosestPOI(userPosition);
//
//		List<Position> path = searchPathBetween(poi, endMapElement, bike);
//		
//		path.add(0, userPosition);
//		
//		return path;
//	}

	/**
	 * Loop for a path between two positions.
	 * Use the closest POI to do so.
	 * 
	 * @param userPosition Position of the user
	 * @param endPosition End position
	 * @param bike Whether we want to use the path with a bike
	 * @return List of points of the path
	 */
//	public static List<Position> searchPathBetween(Position userPosition, Position endPosition, boolean bike) {
//		int startPoi = getClosestPOI(userPosition);
//		int endPoi = getClosestPOI(endPosition);
//		
//		List<Position> path = searchPathBetween(startPoi, endPoi, bike);
//		
//		if(path != null) {
//			path.add(0, userPosition);
//			path.add(endPosition);
//		}
//		return path;
//	}

	/**
	 * Loop for a path between two POIs.
	 * 
	 * @param startMapElement Position of the user
	 * @param endMapElement End position
	 * @param bike Whether we want to use the path with a bike
	 * @return List of points of the path
	 */
//	public static List<Position> searchPathBetween(int startMapElement, int endMapElement, boolean bike) {
//
//		if(startMapElement < 0 || endMapElement < 0 || startMapElement == endMapElement) {
//			return null;
//		}
//		
//		// Using the bike or not
//		String bikeOn = bike ? "&disabled=on" : "";
//		
//		// Building the URL
//		String pageUrl = "http://plan.epfl.ch/routing?from=" + startMapElement + "&to=" + endMapElement + bikeOn;
//		
//		// Getting the content from the server
//		String jsonString;
//		try {
//			jsonString = URLLoader.getSource(pageUrl);
//		} catch (IOException e) {
//			return null;
//		}
//
//		// Parsing the content into an object
//		Routing r = new Gson().fromJson(jsonString, Routing.class);
//		
//		// Points data
//		GeometryF geom = r.feature.geometry;
//		
//		// Coordinates
//		double[][][] coor = geom.coordinates;
//		
//		// Roadmap object which contains informations about level
//		Roadmap[] road = r.roadmap;
//
//		// Creating point for the road
//		Path path = new Path();
//		for (int i = 0; i < road.length; ++i){
//
//			Position pos = CoordinateConverter.convertEPSG4326ToLatLong(
//					road[i].geometry.coordinates[0],
//					road[i].geometry.coordinates[1],
//					Integer.parseInt(road[i].properties.level));
//
//			path.getRoadmapList().add(pos); 	
//		}
//
//		// First point
//		Position previousEnd = CoordinateConverter.convertEPSG4326ToLatLong(coor[0][0][0], coor[0][0][1], path.getRoadmapList().get(0).getAltitude());
//		path.getPositionList().add(previousEnd);
//
//		// Creating points for the path
//		int k=0;
//		for (int i = 0; i < coor.length; ++i) {
//
//			double l1 = path.getRoadmapList().get(k).getAltitude();
//			double l2 = path.getRoadmapList().get(k).getAltitude();
//
//			Position pos1 = CoordinateConverter.convertEPSG4326ToLatLong(coor[i][0][0], coor[i][0][1], l1);
//			Position pos2 = CoordinateConverter.convertEPSG4326ToLatLong(coor[i][1][0], coor[i][1][1], l2);
//
//			if((path.getRoadmapList().contains(pos1)) || (path.getRoadmapList().contains(pos2))) {
//				++k;          	
//			}
//
//			if(pos1 == previousEnd) {
//				path.getPositionList().add(pos2);
//				previousEnd = pos2;
//			} else {
//				path.getPositionList().add(pos1);
//				previousEnd = pos1;
//			}
//		}
//		
//		return path.getPositionList();
//	}
}

