package org.pocketcampus.plugin.map;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.pocketcampus.plugin.map.search.jsonitems.BasicSearchResponse;
import org.pocketcampus.plugin.map.search.jsonitems.GeometryF;
import org.pocketcampus.plugin.map.search.jsonitems.GeometryR;
import org.pocketcampus.plugin.map.search.jsonitems.Roadmap;
import org.pocketcampus.plugin.map.search.jsonitems.Routing;
import org.pocketcampus.plugin.map.search.jsonitems.SearchProperties;
import org.pocketcampus.shared.plugin.map.CoordinateConverter;
import org.pocketcampus.shared.plugin.map.MapElementBean;
import org.pocketcampus.shared.plugin.map.Path;
import org.pocketcampus.shared.plugin.map.Position;
import org.pocketcampus.shared.utils.URLLoader;

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
	public static int getClosestPOI(Position person) {

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("Server error: unable to load jdbc Drivers");
			e.printStackTrace();
			return -1;
		}
		
		String titleClosetPOI = "";

		Connection dbConnection = null;
		try {
			dbConnection = DriverManager.getConnection("jdbc:mysql:///pocketcampus", "pocketbuddy", "");
			Statement statement = dbConnection.createStatement();
			ResultSet rs = statement.executeQuery("select *, 3956*2*asin(sqrt(power(sin((" + person.getLatitude() + "-abs(dest.centerX))*pi()/180/2),2)+cos(" + person.getLatitude() + "*pi()/180)*cos(abs(dest.centerX)*pi()/180)*power(sin((" + person.getLongitude() + "-dest.centerY)*pi()/180/2),2))) as distance from map_pois dest order by distance asc limit 1");

			if(rs.next()) {
				titleClosetPOI = rs.getString("title");
			}
			
			statement.close();
			dbConnection.close();
		} catch (SQLException e) {
			System.err.println("Error with SQL");
			e.printStackTrace();
			return -1;
		}
		return getVertexId(titleClosetPOI);
	}
	
	/**
	 * Get the vertex_id of a given element. We need to fetch it every time because
	 * it changes often (too bad...)
	 * @param title the title of the element we want (= field 'text')
	 * @return the vertex_id of the element
	 */
	private static int getVertexId(String title) {
		URL searchUrl = null;
		try {
			searchUrl = new URL("http://plan.epfl.ch/search?keyword=" + URLEncoder.encode(title, "UTF-8"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return -1;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return -1;
		}
		
		Gson gson = new Gson();
		int vertexId = -1;
		try {
			InputStreamReader reader = new InputStreamReader(searchUrl.openStream());
			BasicSearchResponse response = gson.fromJson(reader, BasicSearchResponse.class);
			String vid = response.features[0].properties.vertex_id;
			if(vid != null && !vid.equals("null"))
				vertexId = Integer.parseInt(response.features[0].properties.vertex_id);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return vertexId;
	}
	
	/**
	 * Queries the plan.epfl.ch website and return the results
	 * @param text the query.
	 * @param max the maximum number of results.
	 * @return A list of MapElementBean containing the results.
	 */
	public static List<MapElementBean> searchTextOnEpflWebsite(String text, int max) {
		List<MapElementBean> list = new LinkedList<MapElementBean>();
		URL searchUrl = null;
		try {
			searchUrl = new URL("http://plan.epfl.ch/search?keyword=" + URLEncoder.encode(text + "*", "UTF-8"));
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
						String description = "";
						if(properties.room != null && properties.room.length() > 0) {
							description = properties.room;
						}
						Position p = CoordinateConverter.convertEPSG4326ToLatLong(geometry.coordinates[0], geometry.coordinates[1], 0);
						MapElementBean meb = new MapElementBean(properties.text, description, p.getLatitude(), p.getLongitude(), 0, -1, -1);
						list.add(meb);
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
	public static List<Position> searchPathBetween(Position userPosition, int endMapElement, boolean bike) {
		int poi = getClosestPOI(userPosition);

		List<Position> path = searchPathBetween(poi, endMapElement, bike);
		
		path.add(0, userPosition);
		
		return path;
	}

	/**
	 * Loop for a path between two positions.
	 * Use the closest POI to do so.
	 * 
	 * @param userPosition Position of the user
	 * @param endPosition End position
	 * @param bike Whether we want to use the path with a bike
	 * @return List of points of the path
	 */
	public static List<Position> searchPathBetween(Position userPosition, Position endPosition, boolean bike) {
		int startPoi = getClosestPOI(userPosition);
		int endPoi = getClosestPOI(endPosition);
		
		List<Position> path = searchPathBetween(startPoi, endPoi, bike);
		
		path.add(0, userPosition);
		path.add(endPosition);
		
		return path;
	}

	/**
	 * Loop for a path between two POIs.
	 * 
	 * @param startMapElement Position of the user
	 * @param endMapElement End position
	 * @param bike Whether we want to use the path with a bike
	 * @return List of points of the path
	 */
	public static List<Position> searchPathBetween(int startMapElement, int endMapElement, boolean bike) {

		if(startMapElement < 0 || endMapElement < 0 || startMapElement == endMapElement) {
			return null;
		}
		
		// Using the bike or not
		String bikeOn = bike ? "&disabled=on" : "";
		
		// Building the URL
		String pageUrl = "http://plan.epfl.ch/routing?from=" + startMapElement + "&to=" + endMapElement + bikeOn;
		
		// Getting the content from the server
		String jsonString;
		try {
			jsonString = URLLoader.getSource(pageUrl);
		} catch (IOException e) {
			return null;
		}

		// Parsing the content into an object
		Routing r = new Gson().fromJson(jsonString, Routing.class);
		
		// Points data
		GeometryF geom = r.feature.geometry;
		
		// Coordinates
		double[][][] coor = geom.coordinates;
		
		// Roadmap object which contains informations about level
		Roadmap[] road = r.roadmap;

		// Creating point for the road
		Path path = new Path();
		for (int i = 0; i < road.length; ++i){

			Position pos = CoordinateConverter.convertEPSG4326ToLatLong(
					road[i].geometry.coordinates[0],
					road[i].geometry.coordinates[1],
					Integer.parseInt(road[i].properties.level));

			path.getRoadmapList().add(pos); 	
		}

		// First point
		Position previousEnd = CoordinateConverter.convertEPSG4326ToLatLong(coor[0][0][0], coor[0][0][1], path.getRoadmapList().get(0).getAltitude());
		path.getPositionList().add(previousEnd);

		// Creating points for the path
		int k=0;
		for (int i = 0; i < coor.length; ++i) {

			double l1 = path.getRoadmapList().get(k).getAltitude();
			double l2 = path.getRoadmapList().get(k).getAltitude();

			Position pos1 = CoordinateConverter.convertEPSG4326ToLatLong(coor[i][0][0], coor[i][0][1], l1);
			Position pos2 = CoordinateConverter.convertEPSG4326ToLatLong(coor[i][1][0], coor[i][1][1], l2);

			if((path.getRoadmapList().contains(pos1)) || (path.getRoadmapList().contains(pos2))) {
				++k;          	
			}

			if(pos1 == previousEnd) {
				path.getPositionList().add(pos2);
				previousEnd = pos2;
			} else {
				path.getPositionList().add(pos1);
				previousEnd = pos1;
			}
		}
		
		return path.getPositionList();
	}

	/**
	 * Searches the elements with a specific title or description
	 * @param query the text query
	 * @param maxResults the max number of results returned
	 * @return the elements corresponding to the query (null if an error happened)
	 */
	public static List<MapElementBean> searchText(String query, int maxResults) {
		if(query == null || query.length() <= 0 || maxResults <= 0)
			return null;
		
		List<MapElementBean> elements = new LinkedList<MapElementBean>();
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("Server error: unable to load jdbc Drivers");
			e.printStackTrace();
			return null;
		}

		Connection dbConnection = null;
		try {
			//dbConnection = DriverManager.getConnection("jdbc:mysql:///pocketcampus", "root", "fyInjhWO");
			dbConnection = DriverManager.getConnection("jdbc:mysql:///pocketcampus", "pocketbuddy", "");
			PreparedStatement statement = dbConnection.prepareStatement("select * from `MAP_POIS` where `title` like ? or `description` like ? order by `title`, `description` limit ?");
			statement.setString(1, "%" + query + "%");
			statement.setString(2, "%" + query + "%");
			statement.setInt(3, maxResults);
			ResultSet rs = statement.executeQuery();

			while (rs.next()) {
				MapElementBean meb = new MapElementBean(rs.getString("title"), rs.getString("description"), rs.getDouble("centerX"), rs.getDouble("centerY"), rs.getDouble("altitude"), rs.getInt("id"), rs.getInt("layer_id"));
				elements.add(meb);
			}
			
			statement.close();
			dbConnection.close();
		} catch (SQLException e) {
			System.err.println("Error with SQL");
			e.printStackTrace();
			return null;
		}
		
		return elements;
	}
}

