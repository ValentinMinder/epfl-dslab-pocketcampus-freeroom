package org.pocketcampus.platform.launcher.server;
import java.util.ArrayList;

import org.pocketcampus.plugin.foo.server.TestServiceImpl;
import org.pocketcampus.plugin.test.shared.TestService;
import org.pocketcampus.plugin.food.server.FoodServiceImpl;
import org.pocketcampus.plugin.food.shared.FoodService;

public class PocketCampusServer extends ServerBase {

	@Override
	protected ArrayList<Processor> getServiceProcessors() {
		ArrayList<Processor> processors = new ArrayList<Processor>();
		
		// Add all the plugin server implementations here.
		// The name must match the plugin name given in the plugin controller (and must be unique!)
		// TODO create a unique idea directly in the thrift file when compiling the thrift files?
		processors.add(new Processor(new TestService.Processor<TestServiceImpl>(new TestServiceImpl()), "test"));
		processors.add(new Processor(new FoodService.Processor<FoodServiceImpl>(new FoodServiceImpl()), "food"));
		
		return processors;
	}
}