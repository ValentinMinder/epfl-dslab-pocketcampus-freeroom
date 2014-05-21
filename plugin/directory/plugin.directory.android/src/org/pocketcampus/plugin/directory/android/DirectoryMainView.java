package org.pocketcampus.plugin.directory.android;

import java.util.Map;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.adapter.LazyAdapter;
import org.pocketcampus.android.platform.sdk.ui.adapter.MultiListAdapter;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardLayout;
import org.pocketcampus.android.platform.sdk.utils.Preparated;
import org.pocketcampus.android.platform.sdk.utils.Preparator;
import org.pocketcampus.plugin.directory.R;
import org.pocketcampus.plugin.directory.android.iface.IDirectoryView;
import org.pocketcampus.plugin.directory.shared.Person;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

/**
 * The Main View of the Directory plugin.
 * 
 * Allows to search for people
 * 
 * @author Amer <amer@accandme.com>
 * 
 */
public class DirectoryMainView extends PluginView implements IDirectoryView {

	final String MAP_KEY_PERSON_OBJ = "LIST_ITEM_PERSON";
	
	private DirectoryController mController;
	private DirectoryModel mModel;
	
	
	ListView listView;
	StandardLayout msgView;
		
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return DirectoryController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		
		// Get and cast the controller and model
		mController = (DirectoryController) controller;
		mModel = (DirectoryModel) controller.getModel();

		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		
		setContentView(R.layout.directory_main);
		msgView = new StandardLayout(this);
		listView = (ListView) inflater.inflate(R.layout.directory_list, null);

		setKeyListener();
		
		setActionBarTitle(getString(R.string.directory_plugin_name));

	}
	
	private void showMsg() {
		RelativeLayout mainView = (RelativeLayout) findViewById(R.id.directory_main);
		mainView.removeAllViews();
		mainView.addView(msgView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
	}

	private void showList() {
		RelativeLayout mainView = (RelativeLayout) findViewById(R.id.directory_main);
		mainView.removeAllViews();
		mainView.addView(listView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
	}
	
	private void setKeyListener() {
		final EditText searchBar = (EditText) findViewById(R.id.directory_searchinput);
		searchBar.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			public void afterTextChanged(Editable s) {
				mController.search(DirectoryMainView.this, s.toString());
			}
		});
		
		final Button clearButton = (Button) findViewById(R.id.directory_searchinput_delete);
		clearButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				searchBar.setText("");
			}
		});
		
	}
	

	/**
	 * Handles the intent that was used to start this plugin.
	 * 
	 * We need to read the Extras.
	 */
	@Override
	protected void handleIntent(Intent aIntent) {
		
		Uri aData = aIntent.getData();
		if(aData != null && "/query".equals(aData.getPath()) && aData.getQueryParameter("q") != null) {
			EditText s = (EditText) findViewById(R.id.directory_searchinput);
			s.setText(aData.getQueryParameter("q"));
		}

		
		//Tracker
		//if(eventPoolId == Constants.CONTAINER_EVENT_ID) Tracker.getInstance().trackPageView("directory");
		//else Tracker.getInstance().trackPageView("directory/" + eventPoolId + "/subevents");
	}

	/**
	 * This is called when the Activity is resumed.
	 * 
	 * If the user presses back on the Authentication window,
	 * This Activity is resumed but we do not have the
	 * credentials. In this case we close the Activity.
	 */
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		//menu.clear();
		return false;
	}

	



	@Override
	public void resultListUpdated() {
		
		
		//SeparatedListAdapter adapter = new SeparatedListAdapter(this, R.layout.directory_list_header);
		MultiListAdapter adapter = new MultiListAdapter();
		

		Preparated<Person> p = new Preparated<Person>(mModel.getResults(), new Preparator<Person>() {
			public int[] resources() {
				return new int[] { R.id.directory_person_name, R.id.directory_person_details, R.id.directory_person_picture };
			}
			public Object content(int res, final Person e) {
				switch (res) {
				case R.id.directory_person_name:
					return e.getFirstName() + " " + e.getLastName();
				case R.id.directory_person_details:
					return e.getOrganisationalUnits().iterator().next();
				case R.id.directory_person_picture:
					return e.getPictureUrl();
				default:
					return null;
				}
			}
			public void finalize(Map<String, Object> map, Person item) {
				map.put(MAP_KEY_PERSON_OBJ, item);
			}
		});
		adapter.addSection(new LazyAdapter(this, p.getMap(), R.layout.directory_list_row, p.getKeys(), p.getResources())
				.setImageOnFail(R.drawable.sdk_empty_person).setNoImage(R.drawable.sdk_empty_person).setImageForEmptyUri(R.drawable.sdk_empty_person)
				);
			
			
		
		if(mModel.getResults().size() == 0) {
			showMsg();
			//msgView.setText(getString(R.string.directory_no_results_found));
		} else {
			showList();
			listView.setAdapter(adapter);
			//mList.setCacheColorHint(Color.TRANSPARENT);
			//mList.setFastScrollEnabled(true);
			//mList.setScrollingCacheEnabled(false);
			//mList.setPersistentDrawingCache(ViewGroup.PERSISTENT_SCROLLING_CACHE);
			//mList.setDivider(null);
			//mList.setDividerHeight(0);
			
			listView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
			
			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					Object o = arg0.getItemAtPosition(arg2);
					if(o instanceof Map<?, ?>) {
						Object obj = ((Map<?, ?>) o).get(MAP_KEY_PERSON_OBJ);
						if(obj != null && obj instanceof Person) {
							Uri.Builder builder = new Uri.Builder();
							builder.scheme("pocketcampus").authority("directory.plugin.pocketcampus.org").appendPath("search").appendQueryParameter("q", ((Person) obj).getSciper());
							Intent i = new Intent(Intent.ACTION_VIEW, builder.build());
							startActivity(i);
						}
					} else {
						Toast.makeText(getApplicationContext(), o.toString(), Toast.LENGTH_SHORT).show();
					}
				}
			});
			
			/*if(scrollState != null)
				scrollState.restore(listView);*/
			
		}
		
		EditText s = (EditText) findViewById(R.id.directory_searchinput);
		mController.search(this, s.getText().toString());
		
	}
	
	/*private void showOnMap(MapItem mapItem) {
		try{
			Intent i = new Intent();
			i.setAction(Intent.ACTION_VIEW);
			i.setData(Uri.parse("pocketcampus://map.plugin.pocketcampus.org/search"));
			i.putExtra("MapElement", mapItem);
			startActivity(i);
		} catch(Exception e) {
			// Should never happen
			Toast.makeText(getApplicationContext(), "The Map plugin is not installed??", Toast.LENGTH_SHORT).show();
		}
	}*/
	

	
	
	
	

	
	
	@Override
	public void networkErrorHappened() {
		Toast.makeText(getApplicationContext(), getString(R.string.sdk_connection_error_happened), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void ldapServersDown() {
		Toast.makeText(getApplicationContext(), getString(R.string.sdk_upstream_server_down), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void gotPerson(Person p) {
	}

	@Override
	public void ambiguousQuery() {
	}

	
}
