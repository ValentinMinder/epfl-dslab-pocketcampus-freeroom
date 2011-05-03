package org.pocketcampus.plugin.map;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.pocketcampus.core.plugin.Core;
import org.pocketcampus.core.plugin.IPlugin;
import org.pocketcampus.core.plugin.PublicMethod;
import org.pocketcampus.provider.mapelements.IMapElementsProvider;
import org.pocketcampus.shared.plugin.map.MapElementBean;
import org.pocketcampus.shared.plugin.map.MapLayerBean;
import org.pocketcampus.shared.plugin.map.Position;


/**
 * IPlugin server class for the Map plugin.
 * 
 * The plugin lists all the layers each server plugin make available using {@link IMapElementsProvider}.
 * This plugin is used as a regular IMapElementsProvider plugin.
 * 
 * The plugins create MapLayerBean objects to make the layers available. 
 * They have to provide their hashcode (to make it unique) and a layer id.
 * This layer id is only unique in the plugin's scope.
 * We create an "external id", that we provide to the mobile.
 * We have hashmaps to recall where an "external id" comes from a get the items from the correct plugin.
 * 
 * @status WIP
 * 
 * @author Jonas, Johan
 *
 */
public class Map implements IPlugin, IMapElementsProvider {

	// Remember the available layers (but not their content)
	private List<MapLayerBean> layersList_ = new ArrayList<MapLayerBean>();
	private HashMap<String, IMapElementsProvider> layerProviders_ = new HashMap<String, IMapElementsProvider>();
	private HashMap<String, Integer> layerIds_ = new HashMap<String, Integer>();

	/**
	 * Get a list of available layers
	 *
	 * @param request not used
	 * @return A list of available layers
	 */
	@PublicMethod
	public List<MapLayerBean> getLayers(HttpServletRequest request) {

		// Get the layers
		// Do it only once
		synchronized (layersList_) {
			if(layersList_.size() == 0) {
				getPluginsLayers();

				// Sort the layers by alphabetic order
				Collections.sort(layersList_, new Comparator<MapLayerBean>() {
					@Override
					public int compare(MapLayerBean o1, MapLayerBean o2) {
						return o1.getName().compareToIgnoreCase(o2.getName());
					}
				});
			}
		}
		
		return layersList_;
	}

	/**
	 * Get the items of a particular layer.
	 *
	 * @param request Has a string parameter "layer_id" containing the external layer ID
	 * @return
	 */
	@PublicMethod
	public List<MapElementBean> getItems(HttpServletRequest request) {

		if(request == null) {
			return null;
		}

		String layerId = request.getParameter("layer_id");

		// Does the parameter exist
		if(layerId != null) {		
			return getPluginItems(layerId);
		} else {
			return new ArrayList<MapElementBean>();
		}
	}

	/**
	 * Get the points that define a path between two places.
	 * The start position must be composed by a latitude and a longitude.
	 * The end position can be either a POI ID or a latitude+longitude.
	 * 
	 * Start position:
	 * - startLatitude
	 * - startLongitude
	 * 
	 * End position:
	 * - endLatitude
	 * - endLongitude
	 * OR
	 * - endPoiId
	 * 
	 * @param request
	 * @return List of points
	 */
	@PublicMethod
	public List<Position> routing(HttpServletRequest request) {

		double startLat = 46.520101;
		double startLon = 6.565189;
		double endLat = startLat;
		double endLon = startLon;
		int poi = 0;

		try {
			startLat = Double.parseDouble(request.getParameter("startLatitude"));
		} catch (Exception e) {}

		try {
			startLon = Double.parseDouble(request.getParameter("startLongitude"));
		} catch (Exception e) {}

		Position startPos = new Position(startLat, startLon, 0);

		String endPoi = request.getParameter("endPoiId");

		if(endPoi != null) {
			try {
				poi = Integer.parseInt(endPoi);
			} catch (Exception e) {}

			return Search.searchPathBetween(startPos, poi, false);

		} else {

			try {
				endLat = Double.parseDouble(request.getParameter("endLatitude"));
			} catch (Exception e) {}

			try {
				endLon = Double.parseDouble(request.getParameter("endLongitude"));
			} catch (Exception e) {}

			Position endPos = new Position(endLat, endLon, 0);

			return Search.searchPathBetween(startPos, endPos, false);
		}
	}

	/**
	 * Allows to search a text among the title and description of the elements. 
	 * @param request a request where the parameter q is the searched text
	 * @return a list answering the query
	 */
	@PublicMethod
	public List<MapElementBean> search(HttpServletRequest request) {
		String query = null;
		try {
			query = request.getParameter("q");
		} catch(Exception e) {}
		return Search.searchText(query,100);
	}


	/**
	 * Fill the hashmaps with a list of layers
	 * coming from the {@link IMapElementsProvider} plugins.
	 */
	private void getPluginsLayers() {

		// Get the plugins with the "map" interface
		HashSet<IPlugin> providers = Core.getInstance().getProvidersOf(IMapElementsProvider.class);

		// Iterate through all the plugins
		Iterator<IPlugin> iter = providers.iterator();
		IMapElementsProvider provider;
		while(iter.hasNext()) {
			provider = (IMapElementsProvider)iter.next();

			// For each layer of the current plugin, remember it
			for(MapLayerBean mlb : provider.getLayers()) {
				layersList_.add(mlb);
				layerProviders_.put(mlb.getExternalId(), provider);
				layerIds_.put(mlb.getExternalId(), mlb.getInternalId());
			}
		}
	}

	/**
	 * Get a list of items coming from a {@link IMapElementsProvider} plugin
	 * 
	 * @param externalId ID of the layer to use
	 * @return List of items from the layer
	 */
	private List<MapElementBean> getPluginItems(String externalId) {

		IMapElementsProvider provider = layerProviders_.get(externalId);

		if(provider != null) {
			return provider.getLayerItems(layerIds_.get(externalId));
		}

		// Nothing found
		return new ArrayList<MapElementBean>();
	}



	/***** Methods from the interface ****/

	@Override
	public List<MapLayerBean> getLayers() {
		return getInternalLayers();
	}

	@Override
	public List<MapElementBean> getLayerItems(int layerId) {
		return getInternalItems(layerId);
	}

	/**
	 * Get a list of layers coming from the Map plugin itself, from the database
	 * 
	 * @return list of database layers
	 */
	private List<MapLayerBean> getInternalLayers() {
		List<MapLayerBean> layers = new LinkedList<MapLayerBean>();

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("Server error: unable to load jdbc Drivers");
			e.printStackTrace();
			return layers;
		}

		Connection dbConnection = null;
		try {
			//dbConnection = DriverManager.getConnection("jdbc:mysql:///pocketcampus", "root", "fyInjhWO");
			dbConnection = DriverManager.getConnection("jdbc:mysql:///pocketcampus", "pocketbuddy", "");
			Statement statement = dbConnection.createStatement();
			ResultSet rs = statement.executeQuery("select * from MAP_LAYERS");

			while (rs.next()) {
				MapLayerBean mlb = new MapLayerBean(rs.getString("title"), 
						rs.getString("image_url"),
						this,
						rs.getInt("id"),
						rs.getInt("cache"),
						rs.getBoolean("displayable"));
				layers.add(mlb);
			}

			statement.close();
			dbConnection.close();
		} catch (SQLException e) {
			System.err.println("Error with SQL");
			e.printStackTrace();
		}

		return layers;
	}

	/**
	 * Get a list of items from a certain layer, coming from the Map plugin itself, from the database
	 * 
	 * @param layerId ID of the layer to use
	 * @return List of items
	 */
	private List<MapElementBean> getInternalItems(int layerId) {
		List<MapElementBean> elements = new LinkedList<MapElementBean>();

		int id = layerId;

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("Server error: unable to load jdbc Drivers");
			e.printStackTrace();
			return elements;
		}

		Connection dbConnection = null;
		try {
			//dbConnection = DriverManager.getConnection("jdbc:mysql:///pocketcampus", "root", "fyInjhWO");
			dbConnection = DriverManager.getConnection("jdbc:mysql:///pocketcampus", "pocketbuddy", "");
			PreparedStatement statement = dbConnection.prepareStatement("select * from MAP_POIS where layer_id=?");
			statement.setInt(1, id);
			ResultSet rs = statement.executeQuery();

			while (rs.next()) {
				MapElementBean meb = new MapElementBean();
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
