package org.pocketcampus.plugin.map.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.pocketcampus.platform.server.database.ConnectionManager;
import org.pocketcampus.platform.server.launcher.PocketCampusServer;
import org.pocketcampus.plugin.map.shared.MapItem;
import org.pocketcampus.plugin.map.shared.MapLayer;

public class MapDatabase {

	private static final String TABLE_LAYERS = "maplayers";

	private static final String LAYER_ID = "layerId";
	private static final String LAYER_NAME_FOR_QUERY = "nameForQuery";
	private static final String LAYER_NAME_FOR_QUERY_ALL_FLOORS = "nameForQueryAllFloors";
	private static final String LAYER_NAME_PREFIX_FOR_LANGUAGE = "name_";
	
	private ConnectionManager connectionManager;

	public MapDatabase() {
		this.connectionManager = new ConnectionManager(PocketCampusServer.CONFIG.getString("DB_URL"),
				PocketCampusServer.CONFIG.getString("DB_USERNAME"), PocketCampusServer.CONFIG.getString("DB_PASSWORD"));
	}

	public List<MapLayer> getMapLayers(String lang) {
		if (lang == null || lang.length() == 0) {
			throw new IllegalArgumentException("lang must be of length > 0 and cannot be null");
		}
		
		List<MapLayer> layers = new LinkedList<MapLayer>();
		
		try {
			Connection connection = connectionManager.getConnection();
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + TABLE_LAYERS);
			statement.execute();
			ResultSet results = statement.getResultSet();

			String layerNameColumn = layerNameColumnForLang(lang);
			
			while (results.next()) {
				String layerName = results.getString(layerNameColumn);
				if (layerName == null) {
					System.err.println("Could not find required value for column "+layerNameColumn+". Ignoring layer.");
					continue;
				}
				long layerId = results.getLong(LAYER_ID);
				String nameForQuery = results.getString(LAYER_NAME_FOR_QUERY);
				String nameForQueryAllFloors = results.getString(LAYER_NAME_FOR_QUERY_ALL_FLOORS);
				if (nameForQuery == null && nameForQueryAllFloors == null) {
					System.err.println("Both nameForQuery and nameForQueryAllFloors are null for layer "+layerName+". Ignoring layer.");
					continue;
				}
				
				MapLayer layer = new MapLayer(layerId, layerName);
				layer.setNameForQuery(nameForQuery);
				layer.setNameForQueryAllFloors(nameForQueryAllFloors);
				
				layers.add(layer);
			}

			statement.close();
			connectionManager.disconnect();
			
		} catch (SQLException e) {
			System.err.println("Error with SQL");
			e.printStackTrace();
		}

		return layers;
	}
	
	private static String layerNameColumnForLang(String lang) {
		return LAYER_NAME_PREFIX_FOR_LANGUAGE+(lang.toUpperCase());
	}
}
