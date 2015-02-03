package org.pocketcampus.plugin.dashboard.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.pocketcampus.platform.android.core.GlobalContext;
import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginInfo;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.platform.android.ui.layout.DashboardLayout;
import org.pocketcampus.plugin.dashboard.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.markupartist.android.widget.Action;

/**
 * 
 * View for the Dashboard.
 * 
 * @author Florian <florian.laurent@epfl.ch>
 *
 */
public class DashboardView extends PluginView {

	private DashboardController mController;

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return DashboardController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		mController = (DashboardController) controller;

		mController.registerPushNotif();
		mController.fetchDynamicConfig(this);

		addActionToActionBar(new Action() {
			public void performAction(View view) {
				trackEvent("OpenSettings", null);
				startActivity(new Intent(DashboardView.this, DashboardSettingsView.class));
			}

			public int getDrawable() {
				return R.drawable.dashboard_settings;
			}

			@Override
			public String getDescription() {
				return getString(R.string.dashboard_settings);
			}

		});
		getActionBar().setDisplayHomeAsUpEnabled(false);
	}

	@Override
	protected void onResume() {
		displayPlugins();

		super.onResume();
	}

	public void displayPlugins() {

		setContentView(R.layout.dashboard_main);
		findViewById(R.id.dashboard_epfl_logo).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(DashboardView.this, DashboardAboutView.class);
				startActivity(intent);
			}
		});

		// Creates and fills in the <code>PluginDashboard</code>.
		PluginDashboard dash = new PluginDashboard(this);
		ArrayList<PluginInfo> pluginManifests = ((GlobalContext) getApplication()).getAllPluginInfos();
		dash.addPlugins(pluginManifests);

		RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.dashboard_main);
		mainLayout.removeAllViews();
		LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		mainLayout.addView(dash.getView(), layoutParams);
	}

	@Override
	protected String screenName() {
		return "/dashboard";
	}

	/************
	 * HELPER
	 */

	public class PluginDashboard {
		private DashboardLayout mDashboard;
		private Context mContext;
		private LayoutInflater mInflater;

		public PluginDashboard(Context context) {
			mContext = context;
			mDashboard = new DashboardLayout(context);
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		/**
		 * Adds icons for multiple plugins to the dashboard.
		 * 
		 * @param pluginInfos
		 *            <code>PluginManifest</code>s of the plugins to add
		 */
		public void addPlugins(ArrayList<PluginInfo> pluginInfos) {
			Comparator<PluginInfo> comparator = new Comparator<PluginInfo>() {
				@Override
				public int compare(PluginInfo lhs, PluginInfo rhs) {
					return lhs.getLabel().compareToIgnoreCase(rhs.getLabel());
				}
			};
			Collections.sort(pluginInfos, comparator);

			for (PluginInfo pluginInfo : pluginInfos) {
				addPlugin(pluginInfo);
			}
		}

		/**
		 * Adds a plugin to the dashboard.
		 * 
		 * @param pluginInfo
		 *            <code>PluginManifest</code> of the plugin to add
		 */
		public void addPlugin(final PluginInfo pluginInfo) {
			// fills in and adds the launcher view
			final View launcherView = mInflater.inflate(R.layout.dashboard_plugin_button, null);

			final ImageView launcherImage = (ImageView) launcherView.findViewById(R.id.launcher_image);
			launcherImage.setImageDrawable(pluginInfo.getIcon());
			launcherImage.setAlpha(0xff);

			final TextView launcherText = (TextView) launcherView.findViewById(R.id.launcher_text);
			launcherText.setText(pluginInfo.getLabel());
			launcherText.setBackgroundColor(0x00ffffff);

			mDashboard.addView(launcherView);

			launcherView.setOnTouchListener(new OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						launcherText.setTextColor(0x80808080);
						launcherImage.setAlpha(0x80);
					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						launcherText.setTextColor(0xff000000);
						launcherImage.setAlpha(0xff);
					}
					launcherView.invalidate();
					return false;
				}
			});

			// adds the click listener
			launcherView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					trackEvent("OpenPlugin", pluginInfo.getId());
					((GlobalContext) mContext.getApplicationContext()).displayPlugin(mContext, pluginInfo);
					launcherView.setBackgroundColor(0x80808080);
					setContentView(R.layout.dashboard_plugin_button); // HACK
																		// clear
																		// the
																		// screen
				}
			});
		}

		/**
		 * Returns the dashboard's <code>View</code>.
		 * 
		 * @return
		 */
		public View getView() {
			return mDashboard;
		}

	}

}