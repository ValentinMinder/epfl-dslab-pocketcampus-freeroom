package org.pocketcampus.platform.launcher.server;

import java.util.ArrayList;

import org.pocketcampus.plugin.news.server.NewsServiceImpl;
import org.pocketcampus.plugin.news.shared.NewsService;


public class ServerLauncher {
	
	public static class PocketCampusServer extends ServerBase {
		protected ArrayList<Processor> getServiceProcessors() {
			ArrayList<Processor> processors = new ArrayList<Processor>();
			processors.add(new Processor(new NewsService.Processor<NewsServiceImpl>(new NewsServiceImpl()), "news"));
			return processors;
		}
	}
	
	public static void main(String[] args) throws Exception {
		ServerBase server = new PocketCampusServer();
		server.start();
	}
	
}
