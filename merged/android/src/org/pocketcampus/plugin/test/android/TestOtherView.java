package org.pocketcampus.plugin.test.android;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.element.InputBarElement;
import org.pocketcampus.plugin.test.android.iface.ITestModel;
import org.pocketcampus.plugin.test.android.iface.ITestView;

import android.app.Service;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class TestOtherView extends PluginView implements ITestView {
	private TestController mController;
	private ITestModel mModel;
	private InputBarElement mInputBar;

	@Override
	protected Class<? extends Service> getMainControllerClass() {
		return TestController.class;
	}
	
	@Override
	protected void onDisplay(Bundle savedInstanceState,	PluginController controller) {
		mController = (TestController) controller;
		mModel = (TestModel) mController.getModel();
		
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				int value;
				
				try {
					value = Integer.parseInt(mInputBar.getInputText());
				} catch (NumberFormatException e) {
					return;
				}
				
				// There's also a setFoo method in the model. Don't use it from here!
				// The views should never modify the model directly.
				mController.setFoo(value);
			}
		};
		
		// InputBarElement displays an input bar at the top of its layout.
		mInputBar = new InputBarElement(this, "Set", "foo = " + mModel.getFoo());
		mInputBar.setOnButtonClickListener(listener);
		
		setContentView(mInputBar);
	}

	@Override
	public void fooUpdated() {
		CharSequence text = "foo changed to " + mModel.getFoo();
		Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	public void barUpdated() {}

	@Override
	public void networkErrorHappened() {
		Toast toast = Toast.makeText(getApplicationContext(), "Network error!", Toast.LENGTH_SHORT);
		toast.show();
	}
}