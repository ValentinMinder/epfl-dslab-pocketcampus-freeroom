package org.pocketcampus.plugin.map;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.pocketcampus.core.database.ConnectionManager;
import org.pocketcampus.core.exception.ServerException;
import org.pocketcampus.shared.plugin.map.MapElementBean;
import org.pocketcampus.shared.plugin.map.MapLayerBean;
import org.pocketcampus.shared.plugin.map.Position;

public class MapDatabase {
	private static final String DB_URL = "jdbc:mysql://ec2-46-51-131-245.eu-west-1.compute.amazonaws.com/pocketcampus";
	private static final String DB_USERNAME = "pocketbuddy";
	private static final String DB_PASSWORD = "";
	
	private static final String TABLE_LAYERS = "MAP_LAYERS";
	private static final String TABLE_POIS = "MAP_POIS";
	
	private static final String LAYER_TITLE = "title";
	private static final String LAYER_IMAGE_URL = "image_url";
	private static final String LAYER_ID = "id";
	private static final String LAYER_CACHE = "cache";
	private static final String LAYER_DISPLAYABLE = "displayable";
	
	private static final String POI_LAYER_ID = "layer_id";
	private static final String POI_TITLE = "title";
	private static final String POI_DESCRIPTION = "description";
	private static final String POI_LATITUDE = "centerX";
	private static final String POI_LONGITUDE = "centerY";
	private static final String POI_ALTITUDE = "altitude";
	private static final String POI_ID = "id";
	private static final String POI_PLUGIN = "plugin_package";
//	private static final String POI_ = "";

	
	private ConnectionManager connectionManager_;
	
	public MapDatabase() {
		try {
			this.connectionManager_ = new ConnectionManager(DB_URL, DB_USERNAME, DB_PASSWORD);
		} catch (ServerException e) {
			e.printStackTrace();
		}
	}
	
	public List<MapLayerBean> getMapLayers() {
		List<MapLayerBean> layers = new LinkedList<MapLayerBean>();
		
		try {
			Connection dbConnection = connectionManager_.getConnection();
			Statement statement = dbConnection.createStatement();
			ResultSet rs = statement.executeQuery("select * from " + TABLE_LAYERS);

			while (rs.next()) {
				MapLayerBean mlb = new MapLayerBean(rs.getString(LAYER_TITLE), 
						rs.getString(LAYER_IMAGE_URL),
						this,
						rs.getInt(LAYER_ID),
						rs.getInt(LAYER_CACHE),
						rs.getBoolean(LAYER_DISPLAYABLE));
				layers.add(mlb);
			}

			statement.close();
			connectionManager_.disconnect();
		} catch (SQLException e) {
			System.err.println("Error with SQL");
			e.printStackTrace();
		}
		
		return layers;
	}
	
	public List<MapElementBean> getMapElements(int layerId) {
		List<MapElementBean> elements = new LinkedList<MapElementBean>();

		try {
			Connection dbConnection = connectionManager_.getConnection();
			PreparedStatement statement = dbConnection.prepareStatement("select * from " + TABLE_POIS + " where " + POI_LAYER_ID + "=?");
			statement.setInt(1, layerId);
			ResultSet rs = statement.executeQuery();

			while (rs.next()) {
				MapElementBean meb = new MapElementBean(rs.getString(POI_TITLE),
														rs.getString(POI_DESCRIPTION),
														rs.getDouble(POI_LATITUDE),
														rs.getDouble(POI_LONGITUDE),
														rs.getDouble(POI_ALTITUDE),
														layerId,
														rs.getInt(POI_ID));

				// Check if this item wants to launch another plugin
				String pluginPackage = rs.getString(POI_PLUGIN);
				if(pluginPackage != null && !"".equals(pluginPackage)) {
					meb.setPluginId(pluginPackage);
				}
				
				elements.add(meb);
			}

			statement.close();
			connectionManager_.disconnect();
		} catch (SQLException e) {
			System.err.println("Error with SQL");
			e.printStackTrace();
		}

		return elements;
	}
	
	public String getTitleClosestPOI(Position person) {
		String titleClosetPOI = "";

		try {
			Connection dbConnection = connectionManager_.getConnection();
			Statement statement = dbConnection.createStatement();
			ResultSet rs = statement.executeQuery("select *, 3956*2*asin(sqrt(power(sin((" + person.getLatitude() + "-abs(dest.centerX))*pi()/180/2),2)+cos(" + person.getLatitude() + "*pi()/180)*cos(abs(dest.centerX)*pi()/180)*power(sin((" + person.getLongitude() + "-dest.centerY)*pi()/180/2),2))) as distance from " + TABLE_POIS + " dest order by distance asc limit 1");

			if(rs.next()) {
				titleClosetPOI = rs.getString(POI_TITLE);
			}
			
			statement.close();
			connectionManager_.disconnect();
		} catch (SQLException e) {
			System.err.println("Error with SQL");
			e.printStackTrace();
		}
		
		return titleClosetPOI;
	}
	
	
//  NOT USE ANYMORE, SEARCH IS DONE DIRECTLY ON EPFL WEBSITE
//	
//	/**
//	 * Searches the elements with a specific title or description
//	 * @param query the text query
//	 * @param maxResults the max number of results returned
//	 * @return the elements corresponding to the query (null if an error happened)
//	 */
//	public List<MapElementBean> searchText(String query, int maxResults) {
//		if(query == null || query.length() <= 0 || maxResults <= 0)
//			return null;
//		
//		List<MapElementBean> elements = new LinkedList<MapElementBean>();
//		
//		try {
//			Connection dbConnection = connectionManager_.getConnection();
//			PreparedStatement statement = dbConnection.prepareStatement("select * from `" + TABLE_POIS +"` where `" + POI_TITLE + "` like ? or `" + POI_DESCRIPTION + "` like ? order by `" + POI_TITLE + "`, `" + POI_DESCRIPTION + "` limit ?");
//			statement.setString(1, "%" + query + "%");
//			statement.setString(2, "%" + query + "%");
//			statement.setInt(3, maxResults);
//			ResultSet rs = statement.executeQuery();
//
//			while (rs.next()) {
//				MapElementBean meb = new MapElementBean(rs.getString(POI_TITLE),
//														rs.getString(POI_DESCRIPTION),
//														rs.getDouble(POI_LATITUDE),
//														rs.getDouble(POI_LONGITUDE),
//														rs.getDouble(POI_ALTITUDE),
//														rs.getInt(POI_ID),
//														rs.getInt(POI_LAYER_ID));
//				elements.add(meb);
//			}
//			
//			statement.close();
//			dbConnection.close();
//		} catch (SQLException e) {
//			System.err.println("Error with SQL");
//			e.printStackTrace();
//			return null;
//		}
//		
//		return elements;
//	}
}
