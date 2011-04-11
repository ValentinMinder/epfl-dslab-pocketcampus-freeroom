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
		
		return getInternalLayers();
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
		
		items.addAll(getExternalItems(id));
		
		
		return items;
	}

	@PublicMethod
	public List<Position> routing(HttpServletRequest request) {

		double lat = 46.520101;
		double lon = 6.565189;

		try {
			lat = Double.parseDouble(request.getParameter("latitude"));
		} catch (Exception e) {}

		try {
			lon = Double.parseDouble(request.getParameter("longitude"));
		} catch (Exception e) {}

		Position p = new Position(lat, lon, 0);

		List<Position> path = Search.searchPathBetween(p, 6718, false);

		return path;
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
	
	private List<MapElementBean> getExternalItems(int id) {
		HashSet<IPlugin> providers = Core.getInstance().getProvidersOf(IMapElementsProvider.class);

		Iterator<IPlugin> iter = providers.iterator();
		IMapElementsProvider provider;

		while(iter.hasNext()) {
			provider = (IMapElementsProvider)iter.next();
		}
		
		return new ArrayList<MapElementBean>();
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
}
