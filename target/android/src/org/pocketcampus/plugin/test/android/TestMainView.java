package org.pocketcampus.plugin.test.android;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardLayout;
import org.pocketcampus.plugin.test.android.iface.ITestModel;
import org.pocketcampus.plugin.test.android.iface.ITestView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

public class TestMainView extends PluginView implements ITestView {
	private TestController mController;
	private ITestModel mModel;
	
	private StandardLayout mLayout;

	/**
	 * Defines what the main controller is for this view. This is optional, some view may not need
	 * a controller (see for example the dashboard).
	 * 
	 * This is only a shortcut for what is done in <code>getOtherController()</code> below: if you know you'll
	 * need a controller before doing anything else in this view, you can define it as you're main controller so you
	 * know it'll be ready as soon as <code>onDisplay()</code> is called.
	 */
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return TestController.class;
	}
	
	/**
	 * Called once the view is connected to the controller.
	 * If you don't implement <code>getMainControllerClass()</code> 
	 * then the controller given here will simply be <code>null</code>.
	 */
	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		// Get and cast the controller and model
		mController = (TestController) controller;
		mModel = (TestModel) controller.getModel();
		
		// The StandardLayout is a RelativeLayout with a TextView in its center.
		mLayout = new StandardLayout(this);
		
		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);

		// We need to force the display before asking the controller for the data, 
		// as the controller may take some time to get it.
		displayData();
	}

	/**
	 * Called automatically from the model as soon as the value of foo is changed.
	 */
	@Override
	public void fooUpdated() {
		System.out.println("foo changed to " + mModel.getFoo());
		displayData();
	}

	@Override
	public void barUpdated() {
		System.out.println("bar changed to " + mModel.getBar());
		displayData();
	}
	
	private void displayData() {
		mLayout.setText("foo = " + mModel.getFoo() + "; bar = " + mModel.getBar());
	}

	/**
	 * We could also have gotten the controller this way.
	 * With this we can connect the view to multiple controllers at once.
	 * 
	 * IMPORTANT for each controller to connect to, we must be able to handle the messages
	 * we'll get back from its model, ie we'll need to implement a specific interface.
	 * The program may crash if you don't, as it will call non-existing method of the view.
	 */
	@SuppressWarnings("unused")
	private void getOtherController() {
		ViewBoundCallback callback = new ViewBoundCallback() {
			@Override
			public void onViewBound(PluginController controller) {
				mController = (TestController) controller;
				mModel = (TestModel) mController.getModel();
				System.out.println(mModel.getFoo());
				releaseController(mController);
			}
		};

		getController(TestController.class, callback);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.test_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		if (item.getItemId() == R.id.test_open) {
			startActivity(new Intent(this, TestOtherView.class));
		} else if (item.getItemId() == R.id.test_request) {
			mController.loadBar();
		}

		return true;
	}

	@Override
	public void networkErrorHappened() {
		Toast toast = Toast.makeText(getApplicationContext(), "Network error!", Toast.LENGTH_SHORT);
		toast.show();
	}

}