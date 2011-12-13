package org.pocketcampus.platform.launcher.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.pocketcampus.authentication.server.AuthenticationServiceImpl;
import org.pocketcampus.plugin.authentication.shared.AuthenticationService;
import org.pocketcampus.plugin.bikes.server.BikesServiceImpl;
import org.pocketcampus.plugin.bikes.shared.BikeService;
import org.pocketcampus.plugin.camipro.server.CamiproServiceImpl;
import org.pocketcampus.plugin.camipro.shared.CamiproService;
import org.pocketcampus.plugin.directory.server.DirectoryServiceImpl;
import org.pocketcampus.plugin.directory.shared.DirectoryService;
import org.pocketcampus.plugin.food.server.FoodServiceImpl;
import org.pocketcampus.plugin.food.shared.FoodService;
import org.pocketcampus.plugin.isacademia.server.IsacademiaServiceImpl;
import org.pocketcampus.plugin.isacademia.shared.IsacademiaService;
import org.pocketcampus.plugin.map.server.MapServiceImpl;
import org.pocketcampus.plugin.map.shared.MapService;
import org.pocketcampus.plugin.news.server.NewsServiceImpl;
import org.pocketcampus.plugin.news.shared.NewsService;
import org.pocketcampus.plugin.satellite.server.SatelliteServiceImpl;
import org.pocketcampus.plugin.satellite.shared.SatelliteService;
import org.pocketcampus.plugin.takeoutreceiver.shared.TakeoutGateway;
import org.pocketcampus.plugin.takeoutreceiver.shared.TakeoutOrderService;
import org.pocketcampus.plugin.takeoutreceiver.shared.TakeoutReceiverService;
import org.pocketcampus.plugin.transport.server.TransportServiceImpl;
import org.pocketcampus.plugin.transport.shared.TransportService;
import org.pocketcampus.server.plugin.takeout.TakeoutGatewayImpl;
import org.pocketcampus.server.plugin.takeout.TakeoutOrderServiceImpl;
import org.pocketcampus.server.plugin.takeoutreceiver.TakeoutReceiverServiceImpl;

public class PocketCampusServer extends ServerBase {

	@Override
	protected ArrayList<Processor> getServiceProcessors() {
		try {
			String thisIp = InetAddress.getLocalHost().getHostAddress();
			System.out.println("Local address: " + thisIp);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		ArrayList<Processor> processors = new ArrayList<Processor>();
		
		// Add all the plugin server implementations here.
		// The name must match the plugin name given in the plugin controller (and must be unique!)
		// TODO create a unique id directly in the thrift file when compiling the thrift files?
		processors.add(new Processor(new AuthenticationService.Processor<AuthenticationServiceImpl>(new AuthenticationServiceImpl()), "authentication"));
		processors.add(new Processor(new BikeService.Processor<BikesServiceImpl>(new BikesServiceImpl()), "bikes"));
		processors.add(new Processor(new CamiproService.Processor<CamiproServiceImpl>(new CamiproServiceImpl()), "camipro"));
		processors.add(new Processor(new DirectoryService.Processor<DirectoryServiceImpl>(new DirectoryServiceImpl()), "directory"));
		processors.add(new Processor(new FoodService.Processor<FoodServiceImpl>(new FoodServiceImpl()), "food"));
		processors.add(new Processor(new IsacademiaService.Processor<IsacademiaServiceImpl>(new IsacademiaServiceImpl()), "isacademia"));
		processors.add(new Processor(new MapService.Processor<MapServiceImpl>(new MapServiceImpl()), "map"));
		processors.add(new Processor(new NewsService.Processor<NewsServiceImpl>(new NewsServiceImpl()), "news"));
		processors.add(new Processor(new SatelliteService.Processor<SatelliteServiceImpl>(new SatelliteServiceImpl()), "satellite"));
		processors.add(new Processor(new TakeoutGateway.Processor<TakeoutGatewayImpl>(new TakeoutGatewayImpl()), "takeout-gateway"));
		processors.add(new Processor(new TakeoutOrderService.Processor<TakeoutOrderServiceImpl>(new TakeoutOrderServiceImpl()), "takeout-order"));
		processors.add(new Processor(new TakeoutReceiverService.Processor<TakeoutReceiverServiceImpl>(new TakeoutReceiverServiceImpl()), "takeoutreceiver"));
		processors.add(new Processor(new TransportService.Processor<TransportServiceImpl>(new TransportServiceImpl()), "transport"));
		
		return processors;
	}
}