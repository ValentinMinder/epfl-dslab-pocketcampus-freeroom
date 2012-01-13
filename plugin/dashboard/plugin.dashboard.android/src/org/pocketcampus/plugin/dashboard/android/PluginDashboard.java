package org.pocketcampus.plugin.dashboard.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.GlobalContext;
import org.pocketcampus.android.platform.sdk.core.PluginInfo;
import org.pocketcampus.android.platform.sdk.ui.layout.DashboardLayout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This Dashboard displays multiple plugins on a single-page grid. The optimal spacing between the items is
 * computed to give the best possible appearance based on the screen size and number of icons.
 * 
 * @author Florian <florian.laurent@epfl.ch>
 */
public class PluginDashboard {
	private DashboardLayout	mDashboard;
	private Context	mContext;
	private LayoutInflater mInflater;

	public PluginDashboard(Context context) {
		mContext = context;
		mDashboard = new DashboardLayout(context);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * Adds icons for multiple plugins to the dashboard.
	 * @param pluginInfos <code>PluginManifest</code>s of the plugins to add
	 */
	public void addPlugins(ArrayList<PluginInfo> pluginInfos) {
		Comparator<PluginInfo> comparator = new Comparator<PluginInfo>() {
			@Override
			public int compare(PluginInfo lhs, PluginInfo rhs) {
				return lhs.getLabel().compareToIgnoreCase(rhs.getLabel());
			}
		};
		Collections.sort(pluginInfos, comparator );
		
		for(PluginInfo pluginInfo : pluginInfos) {
			addPlugin(pluginInfo);
		}
	}

	/**
	 * Adds a plugin to the dashboard.
	 * @param pluginInfo <code>PluginManifest</code> of the plugin to add
	 */
	public void addPlugin(final PluginInfo pluginInfo) {
		// fills in and adds the launcher view
		View launcherView = mInflater.inflate(R.layout.dashboard_plugin_button, null);

		ImageView launcherImage = (ImageView) launcherView.findViewById(R.id.launcher_image);
		launcherImage.setImageDrawable(pluginInfo.getIcon().getDrawable(mContext));

		TextView launcherText = (TextView) launcherView.findViewById(R.id.launcher_text);
		launcherText.setText(pluginInfo.getLabel());

		mDashboard.addView(launcherView);
		
		// adds the click listener
		launcherView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((GlobalContext) mContext.getApplicationContext()).displayPlugin(mContext, pluginInfo);
			}
		});
	}

	/**
	 * Returns the dashboard's <code>View</code>.
	 * @return
	 */
	public View getView() {
		return mDashboard;
	}



}
