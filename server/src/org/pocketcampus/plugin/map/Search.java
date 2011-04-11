package org.pocketcampus.plugin.map;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.pocketcampus.plugin.map.routing.GeometryF;
import org.pocketcampus.plugin.map.routing.Roadmap;
import org.pocketcampus.plugin.map.routing.Routing;
import org.pocketcampus.shared.plugin.map.CoordinateConverter;
import org.pocketcampus.shared.plugin.map.MapElementBean;
import org.pocketcampus.shared.plugin.map.Path;
import org.pocketcampus.shared.plugin.map.Position;
import org.pocketcampus.shared.utils.URLLoader;

import com.google.gson.Gson;

public class Search {
	/**
	 * WILL DO THE SEARCH ON PLAN.EPFL.CH.
	 * Returns a Set of all the MapElements of a given type matching a search string.
	 */
	/*
	public static Set<MapElement> searchMapElement(MapElementType type, String search, Marker marker) {
		//if there are spaces, replace with HTML-space-encoding %20 etc...
		search = URLEncoder.encode(search);

		// OLD CODE USING REQUEST TO PLAN.EPFL.CH
		Set<MapElement> elemSet = new HashSet<MapElement>();

		try {
			String jsonString = getJsonString("http://plan.epfl.ch/search?keyword=" + search);

			if(jsonString == null) {
				return elemSet;
			}

			JSONArray jsonArray = (new JSONObject(jsonString)).getJSONArray("features");
			JSONObject current;
			JSONObject geom;
			JSONObject prop;
			for (int i = 0; i < jsonArray.length(); i++) {
				current = jsonArray.getJSONObject(i);

				geom = current.getJSONObject("geometry");
				JSONArray coor = geom.getJSONArray("coordinates");

				prop = current.getJSONObject("properties");
				String poiName = prop.getString("text");
				//Log.d("Search", "Found a MapPoi with "+poiName);

				//vertex_id may be null and throw an exception
				if(!prop.isNull("vertex_id")){
					MapElement elem = new MapPOI(CoordinateConverter.convertEPSG4326ToLatLong(coor.getInt(0), coor.getInt(1), 0), 
							type, marker, prop.getInt("id"), prop.getInt("vertex_id"), poiName, "");
					elemSet.add(elem);
				}
			}

		}catch (JSONException e) {
			Log.i("Search","",e);
		}

		return elemSet;
	}
	 */



	/**
	 * Returns the MapPOI closest to the MapPerson person. 
	 */
	public static int getClosestPOI(Position person) {
		int loc_id = -1;
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("Server error: unable to load jdbc Drivers");
			e.printStackTrace();
			return loc_id;
		}

		Connection dbConnection = null;
		try {
			dbConnection = DriverManager.getConnection("jdbc:mysql:///pocketcampus", "root", "fyInjhWO");
			Statement statement = dbConnection.createStatement();
			ResultSet rs = statement.executeQuery("select *, 3956*2*asin(sqrt(power(sin((" + person.getLatitude() + "-abs(dest.centerX))*pi()/180/2),2)+cos(" + person.getLatitude() + "*pi()/180)*cos(abs(dest.centerX)*pi()/180)*power(sin((" + person.getLongitude() + "-dest.centerY)*pi()/180/2),2))) as distance from map_pois dest order by distance asc limit 1");

			if(rs.next()) {
				loc_id = rs.getInt("loc_id");
			}
			
			statement.close();
			dbConnection.close();
		} catch (SQLException e) {
			System.err.println("Error with SQL");
			e.printStackTrace();
		}
		return loc_id;
	}


	public static List<Position> searchPathBetween(Position userPosition, int endMapElement, boolean bike) {
		int poi = getClosestPOI(userPosition);
		return searchPathBetween(poi, endMapElement, bike);
	}

	public static List<Position> searchPathBetween(Position userPosition, Position endPosition, boolean bike) {
		int startPoi = getClosestPOI(userPosition);
		int endPoi = getClosestPOI(endPosition);
		return searchPathBetween(startPoi, endPoi, bike);
	}

	/**
	 * Computes the shortest walkable Path between two MapElements.
	 */
	public static List<Position> searchPathBetween(int startMapElement, int endMapElement, boolean bike) {

		/*
		MapPOI start = startMapElement.getClosestPOI();
		MapPOI end = endMapElement.getClosestPOI();
		Log.d("Search", "searchPathBetween VertexID:"+start.vertexID()+" and "+ end.vertexID());
		 */

		String bikeOn = bike ? "&disabled=on" : ""; 
		String pageUrl = "http://plan.epfl.ch/routing?from=" + startMapElement + "&to=" + endMapElement + bikeOn;
		
		String jsonString;
		try {
			jsonString = URLLoader.getSource(pageUrl);
		} catch (IOException e) {
			return null;
		}

		Routing r = new Gson().fromJson(jsonString, Routing.class);

		Path path = new Path();

		GeometryF geom = r.feature.geometry;
		double[][][] coor = geom.coordinates;

		//get the roadmap object which contains informations about level
		Roadmap[] road = r.roadmap;
		for (int i = 0; i < road.length; ++i){

			Position pos = CoordinateConverter.convertEPSG4326ToLatLong(
					road[i].geometry.coordinates[0],
					road[i].geometry.coordinates[1],
					Integer.parseInt(road[i].properties.level));

			path.getRoadmapList().add(pos); 	
		}


		Position previousEnd = CoordinateConverter.convertEPSG4326ToLatLong(coor[0][0][0], coor[0][0][1], path.getRoadmapList().get(0).getAltitude());
		path.getPositionList().add(previousEnd);

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
	 * Converts a Collection of MapElementData to a Set a MapElement.
	 */
	/*
	public static Set<MapElement> dataToMapElement(Collection<MapElementData> elemsData, Marker marker){
		Set<MapElement> elems = new HashSet<MapElement>();

		for (MapElementData e : elemsData) {
			Position pos = CoordinateConverter.convertEPSG4326ToLatLong(e.position_.lat, e.position_.lon, e.position_.level);
			elems.add(new MapPOI(pos , e.type_, marker, 0, 0, e.title_, e.description_));
		}

		return elems;
	}
	 */
	
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
			dbConnection = DriverManager.getConnection("jdbc:mysql:///pocketcampus", "root", "fyInjhWO");
			PreparedStatement statement = dbConnection.prepareStatement("select * from MAP_POIS where title like ? or description like ? limit ?");
			statement.setString(1, "%" + query + "%");
			statement.setString(2, "%" + query + "%");
			statement.setInt(3, maxResults);
			ResultSet rs = statement.executeQuery();

			while (rs.next()) {
				MapElementBean meb = new MapElementBean();
				meb.setId(rs.getInt("id"));
				meb.setLayer_id(rs.getInt("layer_id"));
				meb.setTitle(rs.getString("title"));
				meb.setDescription(rs.getString("description"));
				meb.setLatitude(rs.getDouble("centerX"));
				meb.setLongitude(rs.getDouble("centerY"));
				meb.setAltitude(rs.getDouble("altitude"));
				elements.add(meb);
			}
			
			statement.close();
			dbConnection.close();
		} catch (SQLException e) {
			System.err.println("Error with SQL");
			e.printStackTrace();
		}
		
		return elements;
	}
}

