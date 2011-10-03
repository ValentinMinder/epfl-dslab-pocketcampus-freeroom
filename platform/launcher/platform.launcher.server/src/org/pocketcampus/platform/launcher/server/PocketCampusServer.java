package org.pocketcampus.platform.launcher.server;
import java.util.ArrayList;

import org.pocketcampus.plugin.foo.server.TestServiceImpl;
import org.pocketcampus.plugin.test.shared.TestService;

public class PocketCampusServer extends ServerBase {

	@Override
	protected ArrayList<Processor> getServiceProcessors() {
		ArrayList<Processor> processors = new ArrayList<Processor>();
		
		// TODO add all the plugin server implementations here
		processors.add(new Processor(new TestService.Processor<TestServiceImpl>(new TestServiceImpl()), "test"));
		
		return processors;
	}
}