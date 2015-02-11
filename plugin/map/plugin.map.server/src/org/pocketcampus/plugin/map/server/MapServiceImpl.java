package org.pocketcampus.plugin.map.server;

import org.apache.thrift.TException;
import org.pocketcampus.platform.server.launcher.PocketCampusServer;
import org.pocketcampus.plugin.map.shared.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class MapServiceImpl implements MapService.Iface {

    private final MapDatabase mapDb;

    public MapServiceImpl() {
        mapDb = new MapDatabase();
    }

    @Override
    public MapLayersResponse getLayers() throws TException {
        String lang = PocketCampusServer.getUserLanguageCode();
        try {
            Map<Long, MapLayer> layers = mapDb.getMapLayers(lang);
            MapLayersResponse response = new MapLayersResponse(MapStatusCode.OK, layers);
            return response;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new TException("Failed to get layers from database");
        }
    }

    @Override
    public List<MapItem> search(String query) throws TException {
        List<MapItem> results = Search.searchTextOnEpflWebsite(query, 100);
        return results;
    }

}
