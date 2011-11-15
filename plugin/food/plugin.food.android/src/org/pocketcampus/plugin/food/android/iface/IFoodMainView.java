package org.pocketcampus.plugin.food.android.iface;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.plugin.food.shared.SubmitStatus;

public interface IFoodMainView extends IView {
	
	public void restaurantsUpdated();
	public void menusUpdated();
	public void ratingsUpdated();
	public void ratingsUpdated(SubmitStatus status);
	public void sandwichesUpdated();

}
