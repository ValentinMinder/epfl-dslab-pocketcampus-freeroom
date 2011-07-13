package org.pocketcampus.plugin.scanner;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.pocketcampus.core.plugin.IPlugin;
import org.pocketcampus.core.plugin.PublicMethod;
import org.pocketcampus.provider.mapelements.IMapElementsProvider;
import org.pocketcampus.shared.plugin.authentication.AuthToken;
import org.pocketcampus.shared.plugin.map.MapElementBean;
import org.pocketcampus.shared.plugin.map.MapLayerBean;
import org.pocketcampus.shared.plugin.scanner.ScannerRecordBean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class Scanner implements IMapElementsProvider, IPlugin {
	private String url_;
	private String username_;
	private String password_;

	public Scanner() {
		url_ = "jdbc:mysql://ec2-46-51-131-245.eu-west-1.compute.amazonaws.com/test";
		username_ = "pocketbuddy";
		password_ = "";
	}

	public Connection createConnection() {
		Connection connection_ = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection_ = DriverManager.getConnection(url_, username_, password_);
			System.out.println("Database connection established");
			
		} catch (Exception e) {
			System.err.println("Cannot connect to database server");
			e.printStackTrace();
		}
		
		return connection_;
	}
	
	@PublicMethod
	public List<MapLayerBean> getLayersList(HttpServletRequest request) {
		return getLayers();
    }
	
	@PublicMethod
	public boolean markPointAsScanned(HttpServletRequest request) {
		Connection connection = createConnection();
		
		return true;
    }
	
	@PublicMethod
	public boolean uploadRecord(HttpServletRequest request) {
		String serializedRecord = request.getParameter("serializedRecord");
		
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		
		Type ScannerRecordBeanType = new TypeToken<ScannerRecordBean>(){}.getType();

		try {
			ScannerRecordBean record = gson.fromJson(serializedRecord, ScannerRecordBeanType);
			return saveRecordInDb(record);
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }
	
	@PublicMethod
	public boolean createScanningPoints(HttpServletRequest request) {
		/*
		 * CO1
		 * 
		int group = 1;
		int level = 1;
		double startLatitude = 46.520092;
		double startLongitude = 6.565087;
		double stopLatitude = 46.519826;
		double stopLongitude = 6.565527;
		int nbStepLatitude = 5;
		int nbStepsLongitude = 17;
		*/
		
		/*
		 * BC
		int group = 0;
		int level = 0;
		double startLatitude = 46.518913;
		double startLongitude = 6.561593;
		double stopLatitude = 46.518189;
		double stopLongitude = 6.56225;
		int nbStepLatitude = 5;
		int nbStepsLongitude = 15;*/
		
		
		/*
		 * INM*/
		int group = 1;
		int level = 1; 
		double startLatitude = 46.518968;
		double startLongitude = 6.562896;
		double stopLatitude = 46.518372;
		double stopLongitude = 6.56343;
		int nbStepLatitude = 5;
		int nbStepsLongitude = 14;
		
		
		Connection connection_ = createConnection();
		
		PreparedStatement insertRecordStatement = null;
		String insertRecordString = "INSERT INTO `location_points` (`group`, `latitude`, `longitude`, `level`) VALUES (?,?,?,?)";
		
		try {
			connection_.setAutoCommit(false);
			insertRecordStatement = connection_.prepareStatement(insertRecordString);
			
			for (int i = 0; i <= nbStepLatitude; i++) {
				for (int j = 0; j <= nbStepsLongitude; j++) {
					double longitude = startLongitude + ((stopLongitude - startLongitude)*(double)i)/nbStepLatitude;
					double latitude = startLatitude + ((stopLatitude - startLatitude)*(double)j)/nbStepsLongitude;
					
					insertRecordStatement.setInt(1, group);
					insertRecordStatement.setFloat(2, (float) latitude);
					insertRecordStatement.setFloat(3, (float) longitude);
					insertRecordStatement.setInt(4, level);
					insertRecordStatement.execute();
				}
			}
			
			connection_.commit();
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
    }
	
	private boolean saveRecordInDb(ScannerRecordBean record) {
		String recordId = UUID.randomUUID().toString();
		
		Connection connection_ = createConnection();
		
		PreparedStatement insertRecordStatement = null;
		String insertRecordString = "INSERT INTO location_records (id, location_id)" + " VALUES (?,?)";
		
		PreparedStatement insertGsmStatement = null;
		String insertGsmString = "INSERT INTO location_gsm (id, lac, cid)" + " VALUES (?,?,?)";
		
		PreparedStatement insertGpsStatement = null;
		String insertGpsString = "INSERT INTO location_gps (id, fix_number, latitude, longitude, altitude, accuracy, bearing)" + " VALUES (?,?,?,?,?,?,?)";
		
		PreparedStatement insertWifiStatement = null;
		String insertWifiString = "INSERT INTO location_wifi (id, scan_number, level, bssid, capabilities, ssid, frequency)" + " VALUES (?,?,?,?,?,?,?)";
		
		
		try {
			connection_.setAutoCommit(false);
			
			// RECORD
			insertRecordStatement = connection_.prepareStatement(insertRecordString);
			insertRecordStatement.setString(1, recordId);
			insertRecordStatement.setInt(2, record.getPointId());
			insertRecordStatement.execute();
			
			// GSM
			insertGsmStatement = connection_.prepareStatement(insertGsmString);
			insertGsmStatement.setString(1, recordId);
			insertGsmStatement.setInt(2, record.getGsmLocation().getLac());
			insertGsmStatement.setInt(3, record.getGsmLocation().getCid());
			insertGsmStatement.execute();
			
			// GPS
			for (int i = 0; i < record.getGpsLocations().size(); i++) {
				insertGpsStatement = connection_.prepareStatement(insertGpsString);
				insertGpsStatement.setString(1, recordId);
				insertGpsStatement.setInt(2, i);
				insertGpsStatement.setFloat(3, (float)record.getGpsLocations().get(i).getLatitude());
				insertGpsStatement.setFloat(4, (float)record.getGpsLocations().get(i).getLongitude());
				insertGpsStatement.setFloat(5, (float)record.getGpsLocations().get(i).getAltitude());
				insertGpsStatement.setFloat(6, record.getGpsLocations().get(i).getAccuracy());
				insertGpsStatement.setFloat(7, record.getGpsLocations().get(i).getBearing());
				insertGpsStatement.execute();
			}
			
			// WIFI 
			for (int i = 0; i < record.getAccessPoints().size(); i++) {
				insertWifiStatement = connection_.prepareStatement(insertWifiString);
				insertWifiStatement.setString(1, recordId);
				insertWifiStatement.setInt(2, i);
				insertWifiStatement.setInt(3, record.getAccessPoints().get(i).getLevel());
				insertWifiStatement.setString(4, record.getAccessPoints().get(i).getBssid());
				insertWifiStatement.setString(5, record.getAccessPoints().get(i).getCapabilities());
				insertWifiStatement.setString(6, record.getAccessPoints().get(i).getSsid());
				insertWifiStatement.setInt(7, record.getAccessPoints().get(i).getFrequency());
				insertWifiStatement.execute();
			}
			
			connection_.commit();
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List<MapLayerBean> getLayers() {
		PreparedStatement getLayers = null;
		Connection connection_ = createConnection();
		String getString = "SELECT * FROM `location_points` GROUP BY `group`";
		List<MapLayerBean> mapLayers = new ArrayList<MapLayerBean>();
		
		try {
			connection_.setAutoCommit(false);
			getLayers = connection_.prepareStatement(getString);
			ResultSet resultSet = getLayers.executeQuery();
			connection_.commit();
			
			while (resultSet.next()) {
				int group = resultSet.getInt("group");
				
				mapLayers.add(new MapLayerBean("Cloud " + group, "data/map/map_marker_target.png", this, group, 0, false));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return mapLayers;
		
//		List<MapLayerBean> mapLayerBeans = new ArrayList<MapLayerBean>();
//		mapLayerBeans.add(new MapLayerBean("Cloud 1", "data/map/map_marker_target.png", this, 0, 1, false));
//		mapLayerBeans.add(new MapLayerBean("Cloud 2", "data/map/map_marker_target.png", this, 0, 1, false));
//		
//		return mapLayerBeans;
	}

	@Override
	public List<MapElementBean> getLayerItems(AuthToken token, int layerId) {
		PreparedStatement getLayers = null;
		Connection connection_ = createConnection();
		String getString = "SELECT * FROM `location_points` WHERE `group` = ?";
		List<MapElementBean> layerItems = new ArrayList<MapElementBean>();
		
		try {
			connection_.setAutoCommit(false);
			getLayers = connection_.prepareStatement(getString);
			getLayers.setInt(1, layerId);
			ResultSet resultSet = getLayers.executeQuery();
			connection_.commit();
			
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				int group = resultSet.getInt("group");
				float latitude = resultSet.getFloat("latitude");
				float longitude = resultSet.getFloat("longitude");
				float level = resultSet.getFloat("level");
				
				layerItems.add(new MapElementBean("Level " + (int)level, "Cloud: " + group + ", Point id: " + id, latitude, longitude, level, layerId, id, "org.pocketcampus.plugin.scanner.ScannerRecorder"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return layerItems;
		
//		List<MapElementBean> layerItems = new ArrayList<MapElementBean>();
//		
//		if(layerId != -1) {
//			MapElementBean e1 = new MapElementBean("Point 1", "Level 2", 46.519865, 6.563451, 0, layerId, 0, "org.pocketcampus.plugin.scanner.ScannerRecorder");
//			MapElementBean e2 = new MapElementBean("Point 2", "Level 1", 46.518566, 6.568429, 0, layerId, 1, "org.pocketcampus.plugin.scanner.ScannerRecorder");
//			MapElementBean e3 = new MapElementBean("Point 2", "Level 1", 46.518366, 6.568449, 0, layerId, 2, "org.pocketcampus.plugin.scanner.ScannerRecorder");
//			layerItems.add(e1);
//			layerItems.add(e2);
//			layerItems.add(e3);
//		}
//		
//		return layerItems;
	}

}
