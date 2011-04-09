package org.pocketcampus.plugin.transport;


import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.pocketcampus.core.plugin.IPlugin;
import org.pocketcampus.core.plugin.PublicMethod;
import org.pocketcampus.shared.plugin.transport.Location;
import org.pocketcampus.shared.plugin.transport.QueryConnectionsResult;

import de.schildbach.pte.NetworkProvider.WalkSpeed;
import de.schildbach.pte.SbbProvider;

public class Transport implements IPlugin {

	private SbbProvider sbbProvider_;

	public Transport() {
		sbbProvider_ = new SbbProvider("MJXZ841ZfsmqqmSymWhBPy5dMNoqoGsHInHbWJQ5PTUZOJ1rLTkn8vVZOZDFfSe");
	}

	@PublicMethod
	public Object autocomplete(HttpServletRequest request) {
		String constraint = request.getParameter("constraint");

		if(constraint == null) {
			return null;
		}

		List<Location> completions = null;
		try {
			completions = sbbProvider_.autocompleteStations(constraint);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return completions;
	}

	@PublicMethod
	public Object connections(HttpServletRequest request) {
		String fromConstraint = request.getParameter("from");
		String toConstraint = request.getParameter("to");
		
		if(fromConstraint==null || toConstraint==null) {
			return null;
		}
		
		Location from = null, via = null, to = null;
		
		try {
			// FIXME autocomplete not optimal
			from = sbbProvider_.autocompleteStations(fromConstraint).get(0);
			to = sbbProvider_.autocompleteStations(toConstraint).get(0);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Date date = new Date();
		boolean dep = true;
		String products = (String)null;
		WalkSpeed walkSpeed = WalkSpeed.NORMAL;
		
		QueryConnectionsResult connections = null;
		try {
			connections = sbbProvider_.queryConnections(from, via, to, date, dep, products, walkSpeed);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return connections;
	}

}
















