package org.pocketcampus.plugin.transport.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pocketcampus.plugin.transport.shared.*;

/**
 * Static class to convert Schildbach(the sdk we used to get all the public
 * transport informations) objects to pocketcampus-thrift object.
 * 
 * @author Pascal <pascal.scheiben@gmail.com>
 * 
 */
public class SchildbachToPCConverter {

	private static final HashMap<String, String> normalLineNameForSchildName;

	static {
		normalLineNameForSchildName = new HashMap<String, String>();
		// normalLineNameForSchildName.put("UMm", "UMetm");
		// normalLineNameForSchildName.put("BNFBm", "UMetm");
		normalLineNameForSchildName.put("TNFT", "Tram ");
		// normalLineNameForSchildName.put("UMetm1", "M1");
		// normalLineNameForSchildName.put("UMetm2", "M2");
	}

	static protected QueryTripsResult convertSchToPC(
			de.schildbach.pte.dto.QueryConnectionsResult s) {
		QueryTripsResult qcr = new QueryTripsResult(convertSchToPC(s.from),
				convertSchToPC(s.to), convertSchConToPC(s.connections));
		return qcr;
	}

	static protected TransportStation convertSchToPC(
			de.schildbach.pte.dto.Location s) {
		if (s != null)
			return new TransportStation(s.id, s.lat,
					s.lon, s.name);
		else
			return null;
	}

	static protected List<TransportStation> convertSchToPC(
			List<de.schildbach.pte.dto.Location> l) {
		if (l == null)
			return null;

		LinkedList<TransportStation> ret = new LinkedList<TransportStation>();
		for (de.schildbach.pte.dto.Location loc : l) {
			ret.add(convertSchToPC(loc));
		}
		return ret;
	}

	static protected TransportTrip convertSchToPC(
			de.schildbach.pte.dto.Connection sc) {
		TransportTrip pcc = new TransportTrip(sc.id,
				sc.departureTime.getTime(), sc.arrivalTime.getTime(),
				convertSchToPC(sc.from), convertSchToPC(sc.to), convertSchPartsToPC(sc.parts));
		return pcc;
	}

	static protected List<TransportTrip> convertSchConToPC(
			List<de.schildbach.pte.dto.Connection> l) {
		if (l == null)
			return null;

		LinkedList<TransportTrip> ret = new LinkedList<TransportTrip>();
		for (de.schildbach.pte.dto.Connection con : l) {
			ret.add(convertSchToPC(con));
		}
		return ret;
	}

	// PARTS

	static protected List<TransportConnection> convertSchPartsToPC(
			List<de.schildbach.pte.dto.Connection.Part> l) {
		LinkedList<TransportConnection> ret = new LinkedList<TransportConnection>();
		for (de.schildbach.pte.dto.Connection.Part part : l) {
			TransportConnection conn = convertSchToPC(part);
			if (conn != null) {
				ret.add(conn);
			}
		}
		return ret;
	}

	static protected TransportConnection convertSchToPC(
			de.schildbach.pte.dto.Connection.Part sf) {
		if (sf instanceof de.schildbach.pte.dto.Connection.Trip) {
			de.schildbach.pte.dto.Connection.Trip sft = (de.schildbach.pte.dto.Connection.Trip) sf;
			TransportConnection part = new TransportConnection(
					convertSchToPC(sf.departure), convertSchToPC(sf.arrival), false);
			part.setDepartureTime(sft.departureTime.getTime());
			part.setArrivalTime(sft.arrivalTime.getTime());
			part.setLine(convertSchToPC(sft.line));
			part.setDeparturePosition(sft.departurePosition);
			part.setArrivalPosition(sft.arrivalPosition);

			return part;
		}
		else if(sf instanceof de.schildbach.pte.dto.Connection.Footway){
			de.schildbach.pte.dto.Connection.Footway sft = (de.schildbach.pte.dto.Connection.Footway) sf;
			TransportConnection part = new TransportConnection(
					convertSchToPC(sf.departure), convertSchToPC(sf.arrival),  true);
			part.setFootDuration(sft.min);
	
			return part;
		}

		return null;
	}

	// LINE
	static protected TransportLine convertSchToPC(de.schildbach.pte.dto.Line sl) {
		ArrayList<String> al = new ArrayList<String>();
		String nicerLine = nicerLineName(sl.label);
		return new TransportLine(nicerLine, al);
	}

	static protected String nicerLineName(String schildlLineName) {
		for (String key : normalLineNameForSchildName.keySet()) {
			if (schildlLineName.startsWith(key)) {
				return schildlLineName.replace(key,
						normalLineNameForSchildName.get(key));
			}
		}

		Pattern pattern;
		Matcher matcher;

		pattern = Pattern.compile("^BNFB(\\d*)");
		matcher = pattern.matcher(schildlLineName);
		if (matcher.find() && matcher.group(1) != null && matcher.group(1).length() > 0) {
			return "BBus" + matcher.group(1);
		}
		
		pattern = Pattern.compile("^UM(\\d)");
		matcher = pattern.matcher(schildlLineName);
		if (matcher.find() && matcher.group(1) != null && matcher.group(1).length() > 0) {
			return "M" + matcher.group(1);
		}

		pattern = Pattern.compile("^BBUS(\\d*)");
		matcher = pattern.matcher(schildlLineName);
		if (matcher.find() && matcher.group(1) != null && matcher.group(1).length() > 0) {
			return "BBus" + matcher.group(1);
		}

		return schildlLineName;
	}
}
