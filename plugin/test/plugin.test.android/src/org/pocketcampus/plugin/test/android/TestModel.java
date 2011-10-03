package org.pocketcampus.plugin.test.android;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.test.android.iface.ITestModel;
import org.pocketcampus.plugin.test.android.iface.ITestView;

public class TestModel extends PluginModel implements ITestModel {
	ITestView mListeners = (ITestView) getListeners();
	private int mFoo;
	private int mBar;
	
	/**
	 * Modify the value of <code>foo</code> and notifies the listeners.
	 * @param value New value of foo.
	 */
	public void setFoo(int value) {
		mFoo = value;
		
		// It may be tempting to only notify the listeners if the new value is different from the old one.
		// Don't do that! otherwise if the view started playing a loading animation when starting the request it'll never end.
		// That's why it's fooUpdated and not fooChanged.
		mListeners.fooUpdated();
	}
	
	@Override
	public int getFoo() {
		return mFoo;
	}

	/**
	 * Indicates the interface a view must implement to be able to receive it's updates.
	 */
	@Override
	protected Class<? extends IView> getViewInterface() {
		return ITestView.class;
	}

	public void setBar(int value) {
		mBar = value;
		mListeners.barUpdated();
	}
	
	@Override
	public int getBar() {
		return mBar;
	}

}
