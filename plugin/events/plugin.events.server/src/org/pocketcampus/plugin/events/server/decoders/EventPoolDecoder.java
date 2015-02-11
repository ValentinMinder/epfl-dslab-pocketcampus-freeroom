package org.pocketcampus.plugin.events.server.decoders;

import org.pocketcampus.plugin.events.server.utils.MyQuery;
import org.pocketcampus.plugin.events.server.utils.Utils;
import org.pocketcampus.plugin.events.shared.EventPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class EventPoolDecoder {


	private static class EventPoolDecoderFromDb {

		private static String getSelectFields() {
			return "poolId,poolPicture,poolTitle,poolPlace,poolDetails,disableStar,disableFilterByCateg,disableFilterByTags,enableScan,refreshOnBack,sendStarred,noResultText,overrideLink,parentEvent";
		}
		private static EventPool decodeFromResultSet(ResultSet rs) throws SQLException {
			EventPool ep = new EventPool();

			ep.setPoolId(rs.getLong(1));
			if (rs.wasNull())
				ep.unsetPoolId(); // should never happen

			ep.setPoolPicture(Utils.getResizedPhotoUrl(rs.getString(2), 50));
			ep.setPoolTitle(rs.getString(3));
			ep.setPoolPlace(rs.getString(4));
			ep.setPoolDetails(rs.getString(5));
			ep.setDisableStar(rs.getBoolean(6));
			ep.setDisableFilterByCateg(rs.getBoolean(7));
			ep.setDisableFilterByTags(rs.getBoolean(8));
			ep.setEnableScan(rs.getBoolean(9));
			ep.setRefreshOnBack(rs.getBoolean(10));
			ep.setSendStarredItems(rs.getBoolean(11));
			ep.setNoResultText(rs.getString(12));
			ep.setOverrideLink(rs.getString(13));
			ep.setParentEvent(rs.getLong(14));
			ep.setChildrenEvents(new LinkedList<Long>());

			return ep;
		}

		public static MyQuery getSelectEventPoolsQuery() {
			return new MyQuery().
					addPart("SELECT " + getSelectFields() + " FROM eventpools");
		}

		public static MyQuery getFillChildrenEventItemsQuery() {
			return new MyQuery().
					addPart("SELECT poolId,eventId FROM eventpools,eventitems WHERE (parentPool=poolId)");
		}

		public static Map<Long, EventPool> getEventPoolsUsingQueries(Connection conn, MyQuery selectPoolsQuery, MyQuery fillChildrenItemsQuery) throws SQLException {
			PreparedStatement stm;
			ResultSet rs;
			Map<Long, EventPool> pools = new HashMap<Long, EventPool>();

			stm = selectPoolsQuery.getPreparedStatement(conn);
			rs = stm.executeQuery();
			while (rs.next()) {
				EventPool ep = decodeFromResultSet(rs);
				pools.put(ep.getPoolId(), ep);
			}
			rs.close();
			stm.close();

			// Now fill children
			stm = fillChildrenItemsQuery.getPreparedStatement(conn);
			rs = stm.executeQuery();
			while (rs.next()) {
				if (pools.get(rs.getLong(1)) != null)
					pools.get(rs.getLong(1)).getChildrenEvents().add(rs.getLong(2));
			}
			rs.close();
			stm.close();

			return pools;
		}
	}

	public static Map<Long, EventPool> eventPoolsFromDb(Connection conn, long parentId) throws SQLException {
		MyQuery publicEvents = EventPoolDecoderFromDb.getSelectEventPoolsQuery().
				addPartWithValue(" WHERE (parentEvent=?)", new Long(parentId));
		MyQuery fillChildren = EventPoolDecoderFromDb.getFillChildrenEventItemsQuery().
				addPartWithValue(" AND (parentEvent=?)", new Long(parentId));
		return EventPoolDecoderFromDb.getEventPoolsUsingQueries(conn, publicEvents, fillChildren);
	}

	public static EventPool eventPoolFromDb(Connection conn, long id) throws SQLException {
		MyQuery publicEvents = EventPoolDecoderFromDb.getSelectEventPoolsQuery().
				addPartWithValue(" WHERE (poolId=?)", new Long(id));
		MyQuery fillChildren = EventPoolDecoderFromDb.getFillChildrenEventItemsQuery().
				addPartWithValue(" AND (poolId=?)", new Long(id));
		Map<Long, EventPool> res = EventPoolDecoderFromDb.getEventPoolsUsingQueries(conn, publicEvents, fillChildren);
		if (res.size() == 0)
			return null;
		return res.get(res.keySet().iterator().next());
	}

}
