package org.pocketcampus.plugin.mainscreen;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.Core;
import org.pocketcampus.core.plugin.Icon;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.core.ui.ActionBar.Action;
import org.pocketcampus.plugin.logging.Tracker;
import org.pocketcampus.plugin.news.INewsListener;
import org.pocketcampus.plugin.news.NewsAdapter;
import org.pocketcampus.plugin.news.NewsProvider;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainscreenPlugin extends PluginBase implements INewsListener {
	private Context ctx_;
	private Core core_;
	private Vector<PluginBase> plugins_;
	private Tracker tracker_;

	private NewsAdapter adapter_;
	private NewsProvider newsProvider_;
	private ActionBar actionBar_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainscreen_main);

		Tracker.getInstance().trackPageView("news/home");

		setupActionBar(false);

		tracker_ = Tracker.getInstance();
		tracker_.start("UA-22135241-2", 10, this);

		ctx_ = this.getApplicationContext();
		core_ = Core.getInstance();
		plugins_ = core_.getAvailablePlugins();

		//newsProvider_ = NewsProvider.getInstance(ctx_);
		//newsProvider_.addNewsListener(this);
		//displayNews();
		
		
		//Checkin internet connection
		/*if(!isOnline()) {
			Toast toast = Toast.makeText(ctx_, "This application requires internet connectivity. Please check your internet connection and try again later.", Toast.LENGTH_SHORT);
			toast.show();
		}*/

		LinearLayout menuLayout = (LinearLayout) findViewById(R.id.MenuLayout);

		for (final PluginBase plugin : plugins_) {
			PluginInfo pluginInfo = plugin.getPluginInfo();

			// MENU ICONS
			if(pluginInfo.hasMenuIcon() == true) {
				// layout
				RelativeLayout relLayout = new RelativeLayout(ctx_);
				RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				//layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				relLayout.setLayoutParams(layoutParams);

				// button
				ImageButton button = new ImageButton(ctx_);

				button.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						core_.displayPlugin(ctx_, plugin);
					}
				});

				// icon
				if(pluginInfo.getIcon() != null) {
					button.setImageDrawable(pluginInfo.getIcon().getDrawable(ctx_));
				} else {
					button.setImageDrawable(Icon.getDefaultDrawable(ctx_));
				}

				RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				buttonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
				button.setLayoutParams(buttonParams);
				button.setBackgroundColor(0x00000000);
				button.setId(1);
				relLayout.addView(button);

				// label
				TextView text = new TextView(ctx_);
				text.setText(pluginInfo.getName());

				RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

				textParams.addRule(RelativeLayout.BELOW, 1);
				textParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
				text.setLayoutParams(textParams);
				text.setTextColor(0xff444444);
				text.setGravity(Gravity.TOP);
				relLayout.addView(text);


				// put it in
				relLayout.setPadding(0, 5, 0, 10);
				menuLayout.addView(relLayout);
			}
		}

	}


	private boolean isOnline() {
		try {
			URL url = new URL(core_.getServerUrl());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.connect();
			return connection.getResponseCode() == 200;
		} catch (IOException e) {
			return false;
		}

	}

	private void showAbout() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("About");

		LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.mainscreen_about_dialog, null);
		builder.setView(layout);

		builder.setNeutralButton("Credits", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {				
				dialog.dismiss();
				showCredits();
			}
		});

		builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {				
				dialog.dismiss();
			}
		});

		AlertDialog alert = builder.create();
		alert.setCanceledOnTouchOutside(true);
		alert.show();
	}

	private void showCredits() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Credits");

		LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.mainscreen_credits_dialog, null);
		builder.setView(layout);

		builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {				
				dialog.dismiss();
			}
		});

		AlertDialog alert = builder.create();
		alert.setCanceledOnTouchOutside(true);
		alert.show();
	}

	public static Intent createIntent(Context context) {
		Intent i = new Intent(context, MainscreenPlugin.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return i;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainscreen, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.transport_menu_settings:
			Intent intent = new Intent(this, MainscreenPreference.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			return true;

		case R.id.transport_menu_about:
			showAbout();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public PluginPreference getPluginPreference() {
		return new MainscreenPreference();
	}

	@Override
	public PluginInfo getPluginInfo() {
		return new MainscreenInfo();
	}

	@Override
	public void newsRefreshing() {
		actionBar_.setProgressBarVisibility(View.VISIBLE);
	}

	@Override
	public void newsRefreshed() {
		actionBar_.setProgressBarVisibility(View.GONE);
	}

	private void displayNews() {
		final ListView l = (ListView) findViewById(R.id.mainscreen_news_list_list);
		adapter_ = new NewsAdapter(ctx_, newsProvider_);
		l.setAdapter(adapter_);

		l.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(adapter_ != null) {
					adapter_.setClickedItem(parent, view, position, id);

					Tracker.getInstance().trackPageView("news/previewItem");
				}
			}
		});
	}

	@Override
	protected void setupActionBar(boolean addHomeButton) {
		actionBar_ = (ActionBar) findViewById(R.id.actionbar);
		actionBar_.setTitle(getResources().getString(R.string.app_name));
		actionBar_.addAction(new Action() {

			@Override
			public void performAction(View view) {
				newsProvider_.forceRefresh();
			}

			@Override
			public int getDrawable() {
				return R.drawable.refresh;
			}
		});

		super.setupActionBar(addHomeButton);

	}

	@Override
	protected void onResume() {
		super.onResume();

		//newsProvider_.refreshIfNeeded();
	}
}