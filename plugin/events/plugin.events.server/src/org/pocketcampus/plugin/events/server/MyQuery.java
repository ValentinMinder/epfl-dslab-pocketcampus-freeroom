package org.pocketcampus.plugin.events.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.pocketcampus.plugin.events.server.utils.Utils;


public class MyQuery {
	private StringBuilder query = new StringBuilder();
	private List<Object> values = new LinkedList<Object>();

	public MyQuery addPart(String part) {
		query.append(part);
		return this;
	}

	public MyQuery addPartWithValue(String part, Object arg) {
		query.append(part);
		values.add(arg);
		return this;
	}

	public MyQuery addPartWithList(String part, List<? extends Object> arg, String partIfEmptyList) {
		if (arg.size() == 0)
			return addPart(partIfEmptyList);
		List<String> placeholders = new LinkedList<String>();
		for (Object o : arg) {
			values.add(o);
			placeholders.add("?");
		}
		query.append(part.replace("?", "(" + Utils.join(placeholders, ", ") + ")"));
		return this;
	}

	public PreparedStatement getPreparedStatement(Connection conn) throws SQLException {
		PreparedStatement stm = conn.prepareStatement(query.toString());
		int index = 1;
		for (Object arg : values) {
			if (arg instanceof Integer)
				stm.setInt(index++, ((Integer) arg));
			else if (arg instanceof Long)
				stm.setLong(index++, ((Long) arg));
			else if (arg instanceof String)
				stm.setString(index++, ((String) arg));
			else if (arg instanceof Boolean)
				stm.setBoolean(index++, ((Boolean) arg));
			else
				throw new RuntimeException("Hey, are you kidding me? What kind of obj is that?");
		}
		return stm;
	}
}
