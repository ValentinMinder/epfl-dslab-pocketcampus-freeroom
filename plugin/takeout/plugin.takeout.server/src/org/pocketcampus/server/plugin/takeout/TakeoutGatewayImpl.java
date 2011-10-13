package org.pocketcampus.server.plugin.takeout;

import java.net.InetAddress;

import java.net.UnknownHostException;
import java.util.Arrays;

import org.apache.thrift.TException;
import org.pocketcampus.platform.sdk.shared.common.Location;
import org.pocketcampus.platform.sdk.shared.common.TakeoutServerAddress;
import org.pocketcampus.platform.sdk.shared.restaurant.AvailableRestaurants;
import org.pocketcampus.plugin.takeoutreceiver.shared.TakeoutGateway;
public class TakeoutGatewayImpl implements TakeoutGateway.Iface{

	private String thisIp;

	@Override
	public AvailableRestaurants getRestaurants() throws TException {
		return null;
	}

	@Override
	public AvailableRestaurants getRestaurantForLocation(Location location) throws TException {
		String thisIp = "ec2-79-125-29-79.eu-west-1.compute.amazonaws.com";
		//String thisIp = "128.178.254.164";
		
//		try {
//			thisIp = InetAddress.getLocalHost().getHostAddress();
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}
		
		return new AvailableRestaurants(Arrays.asList(new TakeoutServerAddress(thisIp, 9090)));
	}

}
