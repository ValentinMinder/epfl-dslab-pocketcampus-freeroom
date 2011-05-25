package org.pocketcampus.plugin.mainscreen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.Core;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.core.ui.ActionBar.Action;
import org.pocketcampus.plugin.logging.Tracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SlidingDrawer;

public class MainscreenPlugin extends PluginBase {
	private Context ctx_;
	private Core core_;
	private Vector<PluginBase> plugins_;
	private Tracker tracker_;
	
	private static ActionBar actionBar_;
	private SlidingDrawer quickView_;
	
	private static List<Class> readyNews_;
	
	public final static String PACKAGE = "org.pocketcampus.plugin.";
	
	private MainscreenAdapter adapter_;
	
	private static List<MainscreenNews> news_;
	
	private PluginsAdapter pluginsAdapter_;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainscreen_main);
		
		setupActionBar(false);

		quickView_ = (SlidingDrawer) findViewById(R.id.SlidingDrawer);
		
		readyNews_ = new ArrayList<Class>();
		news_ = new ArrayList<MainscreenNews>();
		
		tracker_ = Tracker.getInstance();
		tracker_.start(this);
		tracker_.trackPageView("mainscreen/home");
		
		ctx_ = this.getApplicationContext();
		core_ = Core.getInstance();
		plugins_ = core_.getAvailablePlugins();
			
		refresh();

		GridView gv = (GridView) findViewById(R.id.gridview);
		
		pluginsAdapter_ = new PluginsAdapter(this, plugins_);
		gv.setAdapter(pluginsAdapter_);
		gv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				core_.displayPlugin(ctx_, pluginsAdapter_.getItem(position));
			}
		});

	}

	/**
	 * Closes the drawer when coming back from another plugin.
	 */
	@Override
	public void onRestart(){
		super.onRestart();
		
		if(quickView_.isOpened()) {
			quickView_.animateClose();
		}
		
		refresh();
	}

	/**
	 * Closes the drawer on back pressed if it is open.
	 */
	@Override
	public void onBackPressed() {
		if(quickView_.isOpened()) {
			quickView_.animateClose();
		} else {
			super.onBackPressed();
		}
	}
	
	/**
	 * Shows a dialog with informations about the application.
	 * TODO add text for the licenses!
	 */
	private void showAbout() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(R.string.mainscreen_menu_about);

		LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.mainscreen_about_dialog, null);
		builder.setView(layout);

		builder.setNeutralButton(R.string.credits, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {				
				dialog.dismiss();
				showCredits();
			}
		});

		builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {				
				dialog.dismiss();
			}
		});

		AlertDialog alert = builder.create();
		alert.setCanceledOnTouchOutside(true);
		alert.show();
	}

	/**
	 * Shows the credits for the application.
	 */
	private void showCredits() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.credits);

		LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.mainscreen_credits_dialog, null);
		builder.setView(layout);

		builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
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

	/**
	 * Shows the menu when the hardware button is pressed.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainscreen, menu);
		return true;
	}
	
	/**
	 * Handles clicks on the menu buttons.
	 */
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
	
	protected void displayNews() {
		final ListView l = (ListView) findViewById(R.id.mainscreen_news_list_list);
		adapter_ = new MainscreenAdapter(ctx_, news_);
		l.setAdapter(adapter_);
		l.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				MainscreenNews selected = (MainscreenNews) adapter_.getItem(position);
				if(selected.getPlugin_() instanceof IAllowsID) {
					Core.startPluginWithID(ctx_, selected.getPlugin_(), selected.getId_());
				}
			}
		});
	}
	

	@Override
	protected void setupActionBar(boolean addHomeButton) {

		actionBar_ = (ActionBar) findViewById(R.id.actionbar);
		actionBar_.addAction(new Action() {

			@Override
			public void performAction(View view) {
				refresh();
			}

			@Override
			public int getDrawable() {
				return R.drawable.refresh;
			}
		});
		
		super.setupActionBar(addHomeButton);

	}
	
	public void refresh() {		
		Log.d("MainscreenPlugin", "Refreshing");
		(new MainscreenNewsProvider(ctx_, this)).getNews();
	}
	
	public void refreshing() {
		actionBar_.setProgressBarVisibility(View.VISIBLE);
	}


	public void refreshed() {
		actionBar_.setProgressBarVisibility(View.GONE);
	}
	
	public static void notifyMainscreen(Class cl) {
		Log.d("MainscreenPlugin", "Notification added for plugin " + cl);
		readyNews_.add(cl);
	}
	
	public static boolean hasNotification(Class cl) {
		Log.d("MainscreenPlugin","Notification requested for plugin " + cl);
		return readyNews_.contains(cl);
	}
	
	
	public void addAll(List<MainscreenNews> list) {
		news_.addAll(list);
		sort();
		displayNews();
	}
	
	public void clean() {
		news_ = new ArrayList<MainscreenNews>();
	}

	public static void sort() {
		Collections.sort(news_);
	}
	

}