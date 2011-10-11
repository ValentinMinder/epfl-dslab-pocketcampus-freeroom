package org.pocketcampus.plugin.food.android;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.food.android.iface.IFoodModel;
import org.pocketcampus.plugin.food.android.iface.IFoodView;

public class FoodModel extends PluginModel implements IFoodModel {

	@Override
	protected Class<? extends IView> getViewInterface() {
		return IFoodView.class;
	}

}
