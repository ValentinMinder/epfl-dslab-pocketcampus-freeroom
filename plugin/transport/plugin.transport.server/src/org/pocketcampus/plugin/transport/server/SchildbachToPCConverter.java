package org.pocketcampus.plugin.transport.server;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.pocketcampus.plugin.transport.shared.QueryTripsResult;
import org.pocketcampus.plugin.transport.shared.TransportLine;
import org.pocketcampus.plugin.transport.shared.TransportTrip;
import org.pocketcampus.plugin.transport.shared.Departure;
import org.pocketcampus.plugin.transport.shared.Fare;
import org.pocketcampus.plugin.transport.shared.FareType;
import org.pocketcampus.plugin.transport.shared.LineDestination;
import org.pocketcampus.plugin.transport.shared.TransportStation;
import org.pocketcampus.plugin.transport.shared.TransportStationType;
import org.pocketcampus.plugin.transport.shared.NearbyStatus;
import org.pocketcampus.plugin.transport.shared.TransportConnection;
import org.pocketcampus.plugin.transport.shared.Point;
import org.pocketcampus.plugin.transport.shared.QueryDepartureResult;
import org.pocketcampus.plugin.transport.shared.StationDepartures;
import org.pocketcampus.plugin.transport.shared.Stop;

import de.schildbach.pte.dto.QueryDeparturesResult;

/**
 * Static class to convert Schildbach(the sdk we used to get all the public transport informations) objects to pocketcampus-thrift object.
 * @author Pascal <pascal.scheiben@gmail.com>
 *
 */
public class SchildbachToPCConverter {
	static protected QueryTripsResult convertSchToPC(de.schildbach.pte.dto.QueryConnectionsResult s){
		QueryTripsResult qcr = new QueryTripsResult(
				convertSchToPC(s.from),
				convertSchToPC(s.to),
				convertSchConToPC(s.connections)
		);
		qcr.ambiguousFrom = convertSchToPC(s.ambiguousFrom);
		qcr.ambiguousVia = convertSchToPC(s.ambiguousVia);
		qcr.ambiguousTo = convertSchToPC(s.ambiguousTo);
		qcr.queryUri = s.queryUri;
		qcr.via = convertSchToPC(s.via);
		qcr.context = s.context;
		return qcr;
	}
	
	static protected QueryDepartureResult convertSchToPC(QueryDeparturesResult sq) {
		return new QueryDepartureResult(convertSchToPC(sq.status),
				convertSchStaDepToPC(sq.stationDepartures));
	}
	
	//STATIONS DEPARTURS
	static protected List<StationDepartures> convertSchStaDepToPC(List<de.schildbach.pte.dto.StationDepartures> l){
		LinkedList<StationDepartures> ret = new LinkedList<StationDepartures>();
		for(de.schildbach.pte.dto.StationDepartures sd : l){
			ret.add(convertSchToPC(sd));
		}
		return ret;
	}
	
	static protected StationDepartures convertSchToPC(de.schildbach.pte.dto.StationDepartures sf){
		return new StationDepartures(convertSchToPC(sf.location),
				convertSchDepToPC(sf.departures),
				convertSchLiDesToPC(sf.lines));
	}

	static protected List<LineDestination> convertSchLiDesToPC(List<de.schildbach.pte.dto.LineDestination> lines) {
		if(lines == null)
			return null;
		
		LinkedList<LineDestination> ret = new LinkedList<LineDestination>();
		for(de.schildbach.pte.dto.LineDestination sd : lines){
			ret.add(convertSchToPC(sd));
		}
		return ret;
	}

	static protected LineDestination convertSchToPC(de.schildbach.pte.dto.LineDestination sd) {
		return new LineDestination(sd.line.label, null, sd.destinationId, sd.destination);
	}

	// DEPARTURES
	static protected List<Departure> convertSchDepToPC(List<de.schildbach.pte.dto.Departure> ld) {
		LinkedList<Departure> ret = new LinkedList<Departure>();
		for(de.schildbach.pte.dto.Departure d : ld){
			ret.add(convertSchToPC(d));
		}
		return ret;
	}

	static protected Departure convertSchToPC(de.schildbach.pte.dto.Departure d) {
		long plt = 0, prt = 0;
		if(d.plannedTime!= null)
			plt = d.plannedTime.getTime();
		
		if(d.predictedTime != null)
			prt = d.predictedTime.getTime();
		
		return new Departure(plt,
				prt,
				d.line.label, 
				null, "", d.position, d.destinationId, d.destination, d.message);
	}

	static protected NearbyStatus convertSchToPC(de.schildbach.pte.dto.QueryDeparturesResult.Status ss) {
		switch(ss){
		case INVALID_STATION:
			return NearbyStatus.INVALID_STATION;
		case OK:
			return NearbyStatus.OK;
		case SERVICE_DOWN:
			default:
				return NearbyStatus.SERVICE_DOWN;
		}
	}

	static protected TransportStation convertSchToPC(de.schildbach.pte.dto.Location s){
		if(s != null)
			return new TransportStation(convertSchToPC(s.type), s.id, s.lat, s.lon, s.place, s.name);
		else
			return null;
	}

	static protected TransportStationType convertSchToPC(de.schildbach.pte.dto.LocationType type) {
		switch(type){
			case ADDRESS :
				return TransportStationType.ADDRESS;
			case STATION :
				return TransportStationType.STATION;
			case POI	:
				return TransportStationType.POI;
			case ANY	:
			default		:
				return	TransportStationType.ANY;
		}
	}

	static protected List<TransportStation> convertSchToPC(List<de.schildbach.pte.dto.Location> l){
		if(l== null)
			return null;
		
		LinkedList<TransportStation> ret = new LinkedList<TransportStation>();
		for(de.schildbach.pte.dto.Location loc : l){
			ret.add(convertSchToPC(loc));
		}
		return ret;
	}

	static protected TransportTrip convertSchToPC(de.schildbach.pte.dto.Connection sc){
		TransportTrip pcc = new TransportTrip(sc.id,
				sc.departureTime.getTime(),
				sc.arrivalTime.getTime(),
				convertSchToPC(sc.from),
				convertSchToPC(sc.to)
				);
		pcc.setLink(sc.link);
		pcc.setParts(convertSchPartsToPC(sc.parts));
		pcc.setFares(convertSchFaresToPC(sc.fares));
		return pcc;
	}
	
	static protected List<TransportTrip> convertSchConToPC(List<de.schildbach.pte.dto.Connection> l){
		if(l == null)
			return null;
		
		LinkedList<TransportTrip> ret = new LinkedList<TransportTrip>();
		for(de.schildbach.pte.dto.Connection con : l){
			ret.add(convertSchToPC(con));
		}
		return ret;
	}
	
	// PARTS
	
	static protected List<TransportConnection> convertSchPartsToPC(List<de.schildbach.pte.dto.Connection.Part> l){
		LinkedList<TransportConnection> ret = new LinkedList<TransportConnection>();
		for(de.schildbach.pte.dto.Connection.Part part : l){
			ret.add(convertSchToPC(part));
		}
		return ret;
	}
	
	static protected TransportConnection convertSchToPC(de.schildbach.pte.dto.Connection.Part sf){
		TransportConnection part = new TransportConnection(convertSchToPC(sf.departure),
				convertSchToPC(sf.arrival));
		
		if(sf instanceof de.schildbach.pte.dto.Connection.Trip ){
			de.schildbach.pte.dto.Connection.Trip sft = (de.schildbach.pte.dto.Connection.Trip) sf;
			
			part.foot = false;
			part.line = convertSchToPC(sft.line);
			part.path = convertSchPointsToPC(sf.path);
			part.destination = convertSchToPC(sft.destination);
			part.departureTime = sft.departureTime.getTime();
			part.arrivalTime = sft.arrivalTime.getTime();
			part.departurePosition = sft.departurePosition;
			part.arrivalPosition = sft.arrivalPosition;
			part.intermediateStops = convertSchStopToPC(sft.intermediateStops);
		}
		if(sf instanceof de.schildbach.pte.dto.Connection.Footway){
			de.schildbach.pte.dto.Connection.Footway sff = (de.schildbach.pte.dto.Connection.Footway) sf;
			part.foot = true;
			part.min = sff.min;
			
		}
		return part;
	}
	
	//STOP
	static protected Stop convertSchToPC(de.schildbach.pte.dto.Stop s){
		Stop stop = new Stop(convertSchToPC(s.location),
				s.time.getTime());
		stop.position = s.position;
		return stop;
	}
	
	static protected List<Stop> convertSchStopToPC(List<de.schildbach.pte.dto.Stop> ls){
		LinkedList<Stop> ret = new LinkedList<Stop>();
		for(de.schildbach.pte.dto.Stop s : ls){
			ret.add(convertSchToPC(s));
		}
		return ret;
	}
	
	//LINE
	static protected TransportLine convertSchToPC(de.schildbach.pte.dto.Line sl){
		ArrayList<String> al = new ArrayList<String>();
		for(int i : sl.colors){
			al.add(Integer.toString(i));
		}
		return new TransportLine(sl.label, al);
	}
	
	//POINTS
	static protected List<Point> convertSchPointsToPC(List<de.schildbach.pte.dto.Point> l){
		if(l == null)
			return null;
		
		LinkedList<Point> ret = new LinkedList<Point>();
		for(de.schildbach.pte.dto.Point p : l){
			ret.add(convertSchToPC(p));
		}
		return ret;
	}
	
	static protected Point convertSchToPC(de.schildbach.pte.dto.Point sf){
		return new Point(sf.lat, sf.lon);
	}
	
	// FARE
	static protected List<Fare> convertSchFaresToPC(List<de.schildbach.pte.dto.Fare> l){
		if(l == null)
			return null;
		LinkedList<Fare> ret = new LinkedList<Fare>();
		for(de.schildbach.pte.dto.Fare fare : l){
			ret.add(convertSchToPC(fare));
		}
		return ret;
	}
	
	static protected Fare convertSchToPC(de.schildbach.pte.dto.Fare sf){
		return new Fare(sf.network, convertSchToPC(sf.type), sf.currency.toString(), (double)sf.fare, sf.unitName, sf.units);
	}
	
	static protected FareType convertSchToPC(de.schildbach.pte.dto.Fare.Type f){
		switch(f){
		case ADULT:
			return FareType.ADULT;
		case CHILD:
			return FareType.CHILD;
		case YOUTH:
			return FareType.YOUTH;
		case STUDENT:
			return FareType.STUDENT;
		case SENIOR:
			return FareType.SENIOR;
		case MILITARY:
			return FareType.MILITARY;
		case DISABLED:
		default:
			return FareType.DISABLED;
		}
	}
}
