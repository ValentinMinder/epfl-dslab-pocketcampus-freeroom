package org.pocketcampus.plugin.map;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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

public class Map implements IPlugin {
	@PublicMethod
	public List<MapLayerBean> layers(HttpServletRequest request) {
		return getExternalLayers();
	}

	@PublicMethod
	public List<MapLayerBean> getLayers(HttpServletRequest request) {
		
		ArrayList<MapLayerBean> layers = new ArrayList<MapLayerBean>();

		layers.addAll(getInternalLayers());
		layers.addAll(getExternalLayers());
		
		return layers;
	}

	@PublicMethod
	public List<MapElementBean> getItems(HttpServletRequest request) {
		
		if(request == null) {
			return null;
		}
		
		ArrayList<MapElementBean> items = new ArrayList<MapElementBean>();

		String layerId = request.getParameter("layer_id");
		int id;
		try {
			id = Integer.parseInt(layerId);
			items.addAll(getInternalItems(id));
		} catch (Exception e) {
			return null;
		}
		
		if(items.isEmpty()) {
			items.addAll(getExternalItems(id));
		}
		
		return items;
	}

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
			dbConnection = DriverManager.getConnection("jdbc:mysql:///pocketcampus", "root", "fyInjhWO");
			Statement statement = dbConnection.createStatement();
			ResultSet rs = statement.executeQuery("select * from MAP_LAYERS");

			while (rs.next()) {
				MapLayerBean mlb = new MapLayerBean();
				mlb.setId(rs.getInt("id"));
				mlb.setName(rs.getString("title"));
				mlb.setCache(rs.getInt("cache"));
				mlb.setDrawable_url(rs.getString("image_url"));
				mlb.setDisplayable(rs.getBoolean("displayable"));
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

	private List<MapLayerBean> getExternalLayers() {
		HashSet<IPlugin> providers = Core.getInstance().getProvidersOf(IMapElementsProvider.class);

		Iterator<IPlugin> iter = providers.iterator();
		IMapElementsProvider provider;
		ArrayList<MapLayerBean> layers = new ArrayList<MapLayerBean>();

		while(iter.hasNext()) {
			provider = (IMapElementsProvider)iter.next();
			layers.add(provider.getLayer());
		}

		return layers;
	}

	public List<MapElementBean> getInternalItems(int layerId) {
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
			dbConnection = DriverManager.getConnection("jdbc:mysql:///pocketcampus", "root", "fyInjhWO");
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
	
	private List<MapElementBean> getExternalItems(int id) {
		HashSet<IPlugin> providers = Core.getInstance().getProvidersOf(IMapElementsProvider.class);

		Iterator<IPlugin> iter = providers.iterator();
		IMapElementsProvider provider;

		while(iter.hasNext()) {
			provider = (IMapElementsProvider)iter.next();
			if(provider.getLayer().getId() == id) {
				return provider.getLayerItems();
			}
		}
		
		return new ArrayList<MapElementBean>();
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
		return Search.searchText(query,50);
	}
}
