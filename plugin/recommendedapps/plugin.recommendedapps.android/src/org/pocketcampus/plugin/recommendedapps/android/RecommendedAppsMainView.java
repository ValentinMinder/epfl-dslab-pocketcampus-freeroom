package org.pocketcampus.plugin.recommendedapps.android;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.platform.android.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.recommendedapps.R;
import org.pocketcampus.plugin.recommendedapps.android.iface.IRecommendedAppsView;
import org.pocketcampus.plugin.recommendedapps.shared.RecommendedApp;
import org.pocketcampus.plugin.recommendedapps.shared.RecommendedAppCategory;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * RecommendedAppsMainView - Main view that shows RecommendedApps courses.
 * 
 * This is the main view in the RecommendedApps Plugin. It checks if the user is
 * logged in, if not it pings the Authentication Plugin. When it gets back a
 * valid SessionId it fetches the user's RecommendedApps data.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class RecommendedAppsMainView extends PluginView implements
		IRecommendedAppsView {

	private RecommendedAppsController mController;
	private RecommendedAppsModel mModel;

	private StandardTitledLayout mLayout;

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return RecommendedAppsController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		// Get and cast the controller and model
		mController = (RecommendedAppsController) controller;
		mModel = (RecommendedAppsModel) controller.getModel();

		// Setup the layout
		mLayout = new StandardTitledLayout(this);

		// The ActionBar is added automatically when you call setContentView
		mLayout.hideTitle();

		mLayout.setText(getString(R.string.recommendedapps_downloading));
		setActionBarTitle(getString(R.string.recommendedapps_plugin_title));

		mController.refreshRecommendedApps(this);
	}

	/**
	 * This is called when the Activity is resumed.
	 * 
	 * If the user presses back on the Authentication window, This Activity is
	 * resumed but we do not have the recommendedappsCookie. In this case we
	 * close the Activity.
	 */
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected String screenName() {
		return "/recommendedapps";
	}

	@Override
	public void networkErrorHappened() {
		setUnrecoverableErrorOccurred(getString(R.string.recommendedapps_connection_error_happened));
	}

	@Override
	public void serverDown() {
		setUnrecoverableErrorOccurred(getString(R.string.recommendedapps_error_recommendedapps_down));
	}

	@Override
	public void recommendedAppsRefreshed() {
		setContentView(mLayout);

		System.out.println(mModel.apps());
		System.out.println(mModel.categories());

		Map<Integer, RecommendedApp> apps = mModel.apps();

		LayoutInflater inflater = (LayoutInflater) getBaseContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		LinearLayout storeLayout = (LinearLayout) inflater.inflate(
				R.layout.recommended_apps_main, null);
		LinearLayout linearLayout = (LinearLayout) storeLayout
				.findViewById(R.id.recommendedAppCategoryList);

		for (RecommendedAppCategory category : mModel.categories()) {
			LinearLayout categoryLayout = (LinearLayout) inflater.inflate(
					R.layout.recommended_apps_appcategory, null);
			((TextView) categoryLayout
					.findViewById(R.id.recommendedAppCategoryName))
					.setText(category.getCategoryName());

			if (category.getCategoryDescription() != null && category.getCategoryDescription().length() > 0) {
				((TextView) categoryLayout
						.findViewById(R.id.recommendedAppCategoryDescription))
						.setText(category.getCategoryDescription());
			} else {
				categoryLayout.findViewById(
						R.id.recommendedAppCategoryDescription).setVisibility(
						View.GONE);
			}
			LinearLayout appLayout = (LinearLayout) categoryLayout
					.findViewById(R.id.recommendedAppCategoryApps);
			for (int appId : category.getAppIds()) {
				final RecommendedApp app = apps.get(appId);
				final LinearLayout appThumbLayout = (LinearLayout) inflater
						.inflate(R.layout.recommended_apps_appthumbnail, null);

				((TextView) appThumbLayout
						.findViewById(R.id.recommendedAppName)).setText(app
						.getAppName());
				((TextView) appThumbLayout
						.findViewById(R.id.recommendedAppDescription))
						.setText(app.getAppDescription());

				appLayout.addView(appThumbLayout);
				appThumbLayout.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						System.out.println("CLICKED ON " + app);
						final String appPackageName = app.getAppStoreQuery();

						if (isAppInstalled(appPackageName)) {
							String startingAppParameter = app
									.getAppOpenURLPattern();
							if (startingAppParameter != null) {

								startActivity(new Intent(Intent.ACTION_VIEW,
										Uri.parse(startingAppParameter)));
							} else {
								Intent launchIntent = getPackageManager()
										.getLaunchIntentForPackage(
												appPackageName);
								startActivity(launchIntent);

							}
							return;
						}
						try {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri
									.parse("market://details?id="
											+ appPackageName)));
						} catch (android.content.ActivityNotFoundException anfe) {
							startActivity(new Intent(
									Intent.ACTION_VIEW,
									Uri.parse("http://play.google.com/store/apps/details?id="
											+ appPackageName)));
						}
					}
				});
				new AsyncTask<String, Void, String>() {

					@Override
					protected String doInBackground(String... params) {
						InputStream input = null;
						ByteArrayOutputStream output = null;
						HttpURLConnection connection = null;
						try {
							URL url = new URL(
									"https://42matters.com/api/1/apps/lookup.json?access_token=862cc1618ee8bd7aec6e90edb75c9848c4357c27&p="
											+ params[0]);
							connection = (HttpURLConnection) url
									.openConnection();
							connection.connect();
							if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
								return "Server returned HTTP "
										+ connection.getResponseCode() + " "
										+ connection.getResponseMessage();
							}
							int fileLength = connection.getContentLength();

							input = connection.getInputStream();
							if (fileLength > 0) {
								output = new ByteArrayOutputStream(fileLength);
							} else {
								output = new ByteArrayOutputStream();
							}

							byte data[] = new byte[4096];
							int count;
							while ((count = input.read(data)) != -1) {
								// allow canceling with back button
								if (isCancelled()) {
									input.close();
									return null;
								}
								output.write(data, 0, count);
							}
						} catch (Exception e) {
							return e.toString();
						} finally {
							try {
								if (input != null)
									input.close();
							} catch (IOException ignored) {
							}

							if (connection != null)
								connection.disconnect();
						}
						try {
							return output.toString("UTF-8");
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return "";
						}
					}

					@Override
					protected void onPostExecute(String result) {
						try {
							JSONObject jsonResponse = new JSONObject(result);

							((TextView) appThumbLayout
									.findViewById(R.id.recommendedAppName))
									.setText(jsonResponse.getString("title"));
							((TextView) appThumbLayout
									.findViewById(R.id.recommendedAppDescription))
									.setText(jsonResponse
											.getString("description"));
							ImageLoader
									.getInstance()
									.displayImage(
											jsonResponse.getString("icon_72"),
											(ImageView) appThumbLayout
													.findViewById(R.id.recommendedAppLogo));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return;
						}

					}

				}.execute(app.getAppStoreQuery());

			}
			linearLayout.addView(categoryLayout);
		}

		setContentView(storeLayout);
	}

	private boolean isAppInstalled(String appPackageName) {
		PackageManager pm = this.getPackageManager();
		try {
			pm.getPackageInfo(appPackageName, PackageManager.GET_ACTIVITIES);
			return true;
		} catch (NameNotFoundException e) {
			System.err.println(e);
			return false;
		}
	}
}
