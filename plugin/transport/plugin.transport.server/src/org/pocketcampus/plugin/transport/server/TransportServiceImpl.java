package org.pocketcampus.plugin.transport.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;
import org.pocketcampus.plugin.transport.shared.Location;
import org.pocketcampus.plugin.transport.shared.LocationType;
import org.pocketcampus.plugin.transport.shared.TransportService;

import de.schildbach.pte.SbbProvider;


public class TransportServiceImpl implements TransportService.Iface {
	private SbbProvider mSbbProvider;
	
	public TransportServiceImpl() {
		mSbbProvider = new SbbProvider("MJXZ841ZfsmqqmSymWhBPy5dMNoqoGsHInHbWJQ5PTUZOJ1rLTkn8vVZOZDFfSe");
	}
	
	@Override
	public List<Location> autocomplete(String constraint) throws TException {
		System.out.println("autocomplete");
		
		List<de.schildbach.pte.dto.Location> sbbCompletions = null;
		try {
			sbbCompletions = mSbbProvider.autocompleteStations(constraint);
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<Location> completions = new ArrayList<Location>();
		for(de.schildbach.pte.dto.Location location : sbbCompletions) {
			completions.add(new Location(LocationType.ANY , location.id, location.lat, location.lon, location.place, location.name));
		}
		
		return completions;
	}

}
