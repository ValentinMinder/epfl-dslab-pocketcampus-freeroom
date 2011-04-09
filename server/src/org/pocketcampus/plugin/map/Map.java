package org.pocketcampus.plugin.map;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

public class Map implements IPlugin {
	@PublicMethod
	public String layers(HttpServletRequest request) {
		HashSet<IPlugin> providers = Core.getInstance().getProvidersOf(IMapElementsProvider.class);
		
		String layersDesc = "";
		Iterator<IPlugin> iter = providers.iterator();
		IMapElementsProvider provider;
		
		while(iter.hasNext()) {
			provider = (IMapElementsProvider)iter.next();
			layersDesc += provider.getLayerName() + " ("+ provider.getLayerDescription() +"); ";
		}
		
		return layersDesc;
	}

	@PublicMethod
	public String map(HttpServletRequest request) {
		return "I am MAP ";
	}
	
	@PublicMethod
	public String hello(HttpServletRequest request) {
		return "Hello World";
	}
	
	@PublicMethod
	public String bonjour(HttpServletRequest request) {
		return "Bonjour Monde";
	}
	
	@PublicMethod
	public String hola(HttpServletRequest request) {
		return "Hola mundo";
	}
	
	@PublicMethod
	public Object arr(HttpServletRequest request) {
		return new String[] {"Hi", "guys", "huhu"};
	}
	
	@PublicMethod
	public List<MapLayerBean> getLayers(HttpServletRequest request) {
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
	
	@PublicMethod
	public List<MapElementBean> getItems(HttpServletRequest request) {
		List<MapElementBean> elements = new LinkedList<MapElementBean>();
		if(request == null)
			return elements;
		
		String layerId = request.getParameter("layer_id");
		int id;
		try {
			id = Integer.parseInt(layerId);
		} catch (Exception e) {
			System.err.println("Error with parameter (layer_id)");
			return elements;
		}
		
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

	public String getDefaultMethod() {
		return "map";
	}
}
