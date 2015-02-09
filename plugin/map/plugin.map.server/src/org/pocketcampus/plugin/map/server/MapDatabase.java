package org.pocketcampus.plugin.map.server;

import org.pocketcampus.platform.server.database.ConnectionManager;
import org.pocketcampus.platform.server.launcher.PocketCampusServer;
import org.pocketcampus.plugin.map.shared.MapLayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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

	public Map<Long, MapLayer> getMapLayers(String lang) throws SQLException {
		if (lang == null || lang.length() == 0) {
			throw new IllegalArgumentException("lang must be of length > 0 and cannot be null");
		}
		
		HashMap<Long, MapLayer> layers = new HashMap<Long, MapLayer>();

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
			Long layerId = results.getLong(LAYER_ID);
			String nameForQuery = results.getString(LAYER_NAME_FOR_QUERY);
			String nameForQueryAllFloors = results.getString(LAYER_NAME_FOR_QUERY_ALL_FLOORS);
			if (nameForQuery == null && nameForQueryAllFloors == null) {
				System.err.println("Both nameForQuery and nameForQueryAllFloors are null for layer "+layerName+". Ignoring layer.");
				continue;
			}
			
			MapLayer layer = new MapLayer(layerId, layerName);
			layer.setNameForQuery(nameForQuery);
			layer.setNameForQueryAllFloors(nameForQueryAllFloors);
			
			layers.put(layerId, layer);
		}

		statement.close();
		connectionManager.disconnect();
			
		return layers;
	}
	
	private static String layerNameColumnForLang(String lang) {
		return LAYER_NAME_PREFIX_FOR_LANGUAGE+(lang.toUpperCase());
	}
}
