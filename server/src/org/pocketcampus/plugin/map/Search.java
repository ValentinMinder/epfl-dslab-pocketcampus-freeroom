package org.pocketcampus.plugin.map;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.pocketcampus.plugin.map.routing.Routing;
import org.pocketcampus.shared.plugin.map.Path;
import org.pocketcampus.shared.plugin.map.Position;

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
	/*
	public static MapPOI getClosestPOI(MapPerson person, Marker icon) {
		// conversion to EPSG4326 coordinate system (used on the EPFL POI database)
		Position personPositionConverted = CoordinateConverter.convertLatLongToEPSG4326( person.position().getLat(),  person.position().getLon(),  person.position().getLevel());

		// warps the positon in a PositionData object, so we can send it to the PC server
		PositionData personPositionData = new PositionData(personPositionConverted.getLat(), personPositionConverted.getLon(), personPositionConverted.getLevel());

		// gets the closest point from the PC server
		MapPOIData closestPOIData = null;
		try {
			closestPOIData = (new ServerAPI()).getNearestMapPOIData(personPositionData);
		} catch (ServerException e) {
			Log.d("MapSearch","Server does not answer. Details: "+e.toString());
		}

		if(closestPOIData == null) {
			return null;
		}
		
		// we need the vertexId of this POI, so we ask the EPFL server the complete MapPOI object
		HashSet<MapElement> closestPOIList = (HashSet<MapElement>) searchMapElement(closestPOIData.type_, closestPOIData.title_, icon);

		if(closestPOIList==null || closestPOIList.size()==0) {
			return null;
		}
		
		MapElement closestPOI = closestPOIList.iterator().next();
		Position position = CoordinateConverter.convertEPSG4326ToLatLong(closestPOIData.position_.lat, closestPOIData.position_.lon, closestPOIData.position_.level);
		MapPOI closestPOIWithVertexId = new MapPOI(position, closestPOIData.type_, icon, closestPOIData.id_, closestPOI.getClosestPOI().vertexID());
		return closestPOIWithVertexId;
	}
	*/

	
	
	/**
	 * Computes the shortest walkable Path between two MapElements.
	 */
	public static Path searchPathBetween(String startMapElement, String endMapElement, boolean bike) {
		if(startMapElement==null || endMapElement==null) {
			return null;
		}
		
		/*
		MapPOI start = startMapElement.getClosestPOI();
		MapPOI end = endMapElement.getClosestPOI();
		Log.d("Search", "searchPathBetween VertexID:"+start.vertexID()+" and "+ end.vertexID());
		*/
		
		//Path myPath = new Path();

		try {
			String on = bike ? "&disabled=on" : ""; 
			//String pageUrl = "http://plan.epfl.ch/routing?from=" + start.vertexID() + "&to=" + end.vertexID() + on;
			String pageUrl = "http://plan.epfl.ch/routing?from=" + 17435 + "&to=" + 6718 + on;
			String jsonString = getJsonString(pageUrl);
			
			if (jsonString == null) {
				//Log.d("Search", "jsonString is null in searchPathBetween-Method between VertexID:"+start.vertexID()+" and "+ end.vertexID());
				return null;
			}
			
			Routing r = new Gson().fromJson(jsonString, Routing.class);
			

			Path path = new Path();
			List<Position> list = path.getPositionList();
//			list.add(new Position(46.51811752656941, 6.568092385190248, 1));
//			list.add(new Position(46.52011208093279, 6.565411761843846, 1));
//			list.add(new Position(46.51854536111413, 6.563350147693381, 1));
			list.add(new Position(46.51811752656941f, 6.568092385190248f, 1));
			list.add(new Position(46.52011208093279f, 6.565411761843846f, 1));
			list.add(new Position(46.51854536111413f, 6.563350147693381f, 1));
			
			
			return path;
			
			/*
			//get the JSONarray which contain coordinates
			JsonObject json = new JsonObject(jsonString);
			JSONObject geom = json.getJSONObject("feature").getJSONObject("geometry");
			JSONArray coor = geom.getJSONArray("coordinates");

			//get the roadmap object which contains informations about level
			JSONArray road = json.getJSONArray("roadmap");
			for (int i=0;i<road.length();i++){

				Position pos = CoordinateConverter.convertEPSG4326ToLatLong(road.getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates").getDouble(0),
						road.getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates").getDouble(1),
						road.getJSONObject(i).getJSONObject("properties").getInt("level"));
				myPath.getRoadmapList().add(pos); 	
			}


			Position previousEnd = new Position(46.51811752656941, 6.568092385190248, 0); //start.position();
			myPath.getPositionList().add(previousEnd);

			int k=0;
			for (int i = 0; i < coor.length(); i++) {
				
				int l1=myPath.getRoadmapList().get(k).getAltitude();
				int l2=myPath.getRoadmapList().get(k).getAltitude();
				Position pos1 = CoordinateConverter.convertEPSG4326ToLatLong(coor.getJSONArray(i).getJSONArray(0).getDouble(0), coor.getJSONArray(i).getJSONArray(0).getDouble(1), l1);
				Position pos2 = CoordinateConverter.convertEPSG4326ToLatLong(coor.getJSONArray(i).getJSONArray(1).getDouble(0), coor.getJSONArray(i).getJSONArray(1).getDouble(1), l2);
				
				if((myPath.getRoadmapList().contains(pos1))||(myPath.getRoadmapList().contains(pos2))){
					k=k+1;          	
				}

				if(pos1 == previousEnd) {
					myPath.getPositionList().add(pos2);
					previousEnd = pos2;
				} else {
					myPath.getPositionList().add(pos1);
					previousEnd = pos1;
				}
			}
			*/

		} catch (Exception e) { // TODO JSONException
			e.printStackTrace();
		}

		//return myPath;
		return null;
	}

	
	/**
	 * This method returns the content of a web page as a String.
	 * This should probably be a static method in one of the Parser package in Shared.
	 * @param sourceUrl The address of the web page
	 * @return the content of the web page
	 */
	private static String getJsonString(String sourceUrl){
		BufferedInputStream buffer = null;
		try{
			URL url = new URL(sourceUrl);
			URLConnection urlc = url.openConnection();

			buffer = new BufferedInputStream(urlc.getInputStream());

			StringBuilder builder = new StringBuilder();
			int byteRead;
			while ((byteRead = buffer.read()) != -1){
				builder.append((char) byteRead);
			}
			return builder.toString();

		}catch (IOException e) {
			//Log.i("Search","",e);

		}finally{
			if(buffer != null){
				try {
					buffer.close();
				} catch (IOException e) {
					//Log.i("Search","",e);
				}
			}
		}
		return null;
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

