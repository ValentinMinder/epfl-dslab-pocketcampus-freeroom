package org.pocketcampus.core.plugin;

import java.util.ArrayList;

import org.pocketcampus.plugin.mainscreen.MainscreenNews;

public interface ICallback {
	public void callback(ArrayList<MainscreenNews> news); 
}
