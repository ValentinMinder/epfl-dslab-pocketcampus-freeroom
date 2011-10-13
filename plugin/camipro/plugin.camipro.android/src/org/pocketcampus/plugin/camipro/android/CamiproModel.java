package org.pocketcampus.plugin.camipro.android;

import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.camipro.android.iface.ICamiproModel;
import org.pocketcampus.plugin.camipro.android.iface.ICamiproView;

public class CamiproModel extends PluginModel implements ICamiproModel {
	
	@Override
	protected Class<? extends IView> getViewInterface() {
		return ICamiproView.class;
	}
	
}
