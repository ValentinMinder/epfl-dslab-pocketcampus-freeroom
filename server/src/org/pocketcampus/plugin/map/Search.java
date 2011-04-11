package org.pocketcampus.plugin.map;

import java.io.IOException;
import java.util.List;

import org.pocketcampus.plugin.map.routing.GeometryF;
import org.pocketcampus.plugin.map.routing.Roadmap;
import org.pocketcampus.plugin.map.routing.Routing;
import org.pocketcampus.shared.plugin.map.CoordinateConverter;
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
		// conversion to EPSG4326 coordinate system (used on the EPFL POI database)
		//Position personPositionConverted = CoordinateConverter.convertLatLongToEPSG4326( person.getLatitude(),  person.getLongitude(),  person.getAltitude());

		//Position closestPOI = closestPOIList.iterator().next();

		// TODO Johan

		return 17435;
	}


	public static List<Position> searchPathBetween(Position userPosition, int endMapElement, boolean bike) {

		int poi = getClosestPOI(userPosition);

		return searchPathBetween(userPosition, poi, endMapElement, bike);
	}

	/**
	 * Computes the shortest walkable Path between two MapElements.
	 */
	public static List<Position> searchPathBetween(Position startPosition, int startMapElement, int endMapElement, boolean bike) {

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


		Position previousEnd = startPosition;
		path.getPositionList().add(previousEnd);

		int k=0;
		for (int i = 0; i < coor.length; ++i) {

			int l1 = path.getRoadmapList().get(k).getAltitude();
			int l2 = path.getRoadmapList().get(k).getAltitude();

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
}

