package org.pocketcampus.plugin.directory.android;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.platform.android.ui.adapter.LazyAdapter;
import org.pocketcampus.platform.android.ui.adapter.MultiListAdapter;
import org.pocketcampus.platform.android.utils.Preparated;
import org.pocketcampus.platform.android.utils.Preparator;
import org.pocketcampus.plugin.directory.R;
import org.pocketcampus.plugin.directory.android.iface.IDirectoryView;
import org.pocketcampus.plugin.directory.shared.DirectoryPersonRole;
import org.pocketcampus.plugin.directory.shared.Person;

import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar.Action;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

/**
 * The Main View of the Directory plugin.
 * 
 * Allows to search for people
 * 
 * @author Amer <amer@accandme.com>
 * 
 */
public class DirectoryPersonView extends PluginView implements IDirectoryView {

	final String MAP_KEY_ACTION_URI = "PERSON_INTERACTION_URI";

	private DirectoryController mController;
	//private DirectoryModel mModel;
	
	
		
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return DirectoryController.class;
	}

	@Override
	protected void onPreCreate() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		
		// Get and cast the controller and model
		mController = (DirectoryController) controller;
		//mModel = (DirectoryModel) controller.getModel();

		//disableActionBar();
		makeActionBarLayoutWrapContent();
		setContentView(R.layout.directory_list);
		
		setActionBarTitle(getString(R.string.directory_popup_title));
	}
	
	

	@Override
	protected void handleIntent(Intent aIntent) {
		
		Uri aData = aIntent.getData();
		if(aData != null && "/search".equals(aData.getPath()) && aData.getQueryParameter("sciper") != null) {
			mController.searchBySciper(this, aData.getQueryParameter("sciper"));
		} else if(aData != null && "/view".equals(aData.getPath())) {
			Person p = new Person();
			if(aData.getQueryParameter("firstName") != null) p.setFirstName(aData.getQueryParameter("firstName"));
			if(aData.getQueryParameter("lastName") != null) p.setLastName(aData.getQueryParameter("lastName"));
			if(aData.getQueryParameter("sciper") != null) p.setSciper(aData.getQueryParameter("sciper"));
			p.setEmail(aData.getQueryParameter("email"));
			p.setWeb(aData.getQueryParameter("web"));
			p.setPrivatePhoneNumber(aData.getQueryParameter("privatePhoneNumber"));
			p.setOfficePhoneNumber(aData.getQueryParameter("officePhoneNumber"));
			p.setOffice(aData.getQueryParameter("office"));
			p.setGaspar(aData.getQueryParameter("gaspar"));
			List<String> oU = new LinkedList<String>();
			if(aData.getQueryParameter("OrganisationalUnit") != null) oU.add(aData.getQueryParameter("OrganisationalUnit"));
			p.setOrganisationalUnits(oU);
			if(aData.getQueryParameter("pictureUrl") != null) p.setPictureUrl(aData.getQueryParameter("pictureUrl"));
			displayPerson(p);
		}

		
		//Tracker
		//if(eventPoolId == Constants.CONTAINER_EVENT_ID) Tracker.getInstance().trackPageView("directory");
		//else Tracker.getInstance().trackPageView("directory/" + eventPoolId + "/subevents");
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected String screenName() {
		return "/directory/person";
	}
	
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		//menu.clear();
		return false;
	}

	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		Rect dialogBounds = new Rect();
		getWindow().getDecorView().getHitRect(dialogBounds);

		if (!dialogBounds.contains((int) ev.getX(), (int) ev.getY())) {
			// Tapped outside so we finish the activity
			this.finish();
		}
		return super.dispatchTouchEvent(ev);
	}

	
	@Override
	public void resultListUpdated() {
		
	}

	private void displayPerson(final Person p) {
		
		
		//SeparatedListAdapter adapter = new SeparatedListAdapter(this, R.layout.directory_list_header);
		MultiListAdapter adapter = new MultiListAdapter();
		

		Preparated<Person> pp = new Preparated<Person>(Arrays.asList(new Person[]{p}), new Preparator<Person>() {
			public int[] resources() {
				return new int[] { R.id.directory_person_details_picture, R.id.directory_person_details_name, R.id.directory_person_details_affiliation, R.id.directory_person_details_expanded_affiliation };
			}
			public Object content(int res, final Person e) {
				switch (res) {
				case R.id.directory_person_details_picture:
					return e.getPictureUrl();
				case R.id.directory_person_details_name:
					return new LazyAdapter.Actuated(DirectoryController.getFullName(e), new LazyAdapter.Actuator() {
						int count = 10;
						public void triggered() {
							count--;
							if(count == 0)
								startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://people.epfl.ch/cgi-bin/people/getPhoto?id=" + e.getSciper() + "&show=1")));
						}
					});
				case R.id.directory_person_details_affiliation:
					//return TextUtils.join(", ",  e.getOrganisationalUnits());
					return getOrgUnitsString(e);
				case R.id.directory_person_details_expanded_affiliation:
					return null;
				default:
					return null;
				}
			}
			public void finalize(Map<String, Object> map, Person item) {
				map.put(LazyAdapter.NOT_SELECTABLE, "1");
				map.put(LazyAdapter.LINK_CLICKABLE, "1");
			}
		});
		adapter.addSection(new LazyAdapter(this, pp.getMap(), R.layout.directory_person_details, pp.getKeys(), pp.getResources())
				.setImageOnFail(R.drawable.sdk_empty_person).setNoImage(R.drawable.sdk_empty_person).setImageForEmptyUri(R.drawable.sdk_empty_person)
				);

		
		List<PersonInteraction> actions = new LinkedList<DirectoryPersonView.PersonInteraction>();
		if(p.isSetOfficePhoneNumber() || p.isSetPrivatePhoneNumber()) {
			//ic_menu_call,ic_dialog_dialer
			String pN = (p.isSetOfficePhoneNumber() ? p.getOfficePhoneNumber() : p.getPrivatePhoneNumber());
			actions.add(new PersonInteraction(R.drawable.directory_phone, pN, Uri.parse("tel:" + pN), "Call"));
		}
		if(p.isSetEmail()) {
			//ic_dialog_email, ic_menu_send
			actions.add(new PersonInteraction(R.drawable.directory_mail, p.getEmail(), Uri.parse("mailto:" + p.getEmail()), "SendEmail"));
		}
		if(p.isSetHomepages() && p.getHomepages().containsKey("Personal profile")) {
			//ic_dialog_info, ic_menu_info_details
			String homepage = p.getHomepages().get("Personal profile");
			actions.add(new PersonInteraction(R.drawable.directory_web, homepage, Uri.parse(homepage), "ViewWebsite"));
		} else if(p.isSetWeb()) {
			actions.add(new PersonInteraction(R.drawable.directory_web, p.getWeb(), Uri.parse(p.getWeb()), "ViewWebsite"));
		}
		if(p.isSetOffice()) {
			//ic_dialog_map, ic_menu_compass, ic_menu_mapmode
			Uri.Builder builder = new Uri.Builder();
			builder.scheme("pocketcampus").authority("map.plugin.pocketcampus.org").appendPath("search").appendQueryParameter("q", p.getOffice());
			actions.add(new PersonInteraction(R.drawable.directory_map, p.getOffice(), builder.build(), "ViewOffice"));
		}
		
		
		Preparated<PersonInteraction> pD = new Preparated<PersonInteraction>(actions, new Preparator<PersonInteraction>() {
			public int[] resources() {
				return new int[] { R.id.directory_person_action_icon, R.id.directory_person_action_text };
			}
			public Object content(int res, final PersonInteraction e) {
				switch (res) {
				case R.id.directory_person_action_icon:
					return e.icon;
				case R.id.directory_person_action_text:
					return e.text;
				default:
					return null;
				}
			}
			public void finalize(Map<String, Object> map, PersonInteraction item) {
				map.put(MAP_KEY_ACTION_URI, item);
			}
		});
		adapter.addSection(new LazyAdapter(this, pD.getMap(), R.layout.directory_person_action, pD.getKeys(), pD.getResources())
				.setImageOnFail(R.drawable.sdk_empty_person).setNoImage(R.drawable.sdk_empty_person).setImageForEmptyUri(R.drawable.sdk_empty_person)
				);
			
			

		ListView listView = (ListView) findViewById(R.id.directory_main_list);
		listView.setAdapter(adapter);
		
		listView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Object o = arg0.getItemAtPosition(arg2);
				if(o instanceof Map<?, ?>) {
					Object obj = ((Map<?, ?>) o).get(MAP_KEY_ACTION_URI);
					if(obj != null && obj instanceof PersonInteraction) {
						trackEvent(((PersonInteraction) obj).tracking, ((PersonInteraction) obj).text);
						Intent i = new Intent(Intent.ACTION_VIEW, ((PersonInteraction) obj).uri);
						startActivity(i);
					}
				} else {
					Toast.makeText(getApplicationContext(), o.toString(), Toast.LENGTH_SHORT).show();
				}
			}
		});

		removeAllActionsFromActionBar();
		addActionToActionBar(new Action() {
			public void performAction(View view) {
				trackEvent("CreateNewContact", null);
				DirectoryController.importContact(DirectoryPersonView.this, p);
			}
			public int getDrawable() {
				return R.drawable.directory_import_contact;
			}
		});

		
	}
	
	
	class PersonInteraction {
		int icon;
		String text;
		Uri uri;
		String tracking;
		public PersonInteraction(int icon, String text, Uri uri, String tracking) {
			this.icon = icon;
			this.text = text;
			this.uri = uri;
			this.tracking = tracking;
		}
	}
	
	
	

	
	
	@Override
	public void networkErrorHappened() {
		Toast.makeText(getApplicationContext(), getString(R.string.sdk_connection_error_happened), Toast.LENGTH_SHORT).show();
		finish();
	}

	@Override
	public void ldapServersDown() {
		Toast.makeText(getApplicationContext(), getString(R.string.sdk_upstream_server_down), Toast.LENGTH_SHORT).show();
		finish();
	}
	
	@Override
	public void gotPerson(Person p) {
		displayPerson(p);
	}

	@Override
	public void ambiguousQuery() {
		Toast.makeText(getApplicationContext(), getString(R.string.directory_ambiguous_query), Toast.LENGTH_SHORT).show();
		finish();
	}

	private static String getOrgUnitsString(Person p) {
		if(!p.isSetRoles())
			return null;
		List<String> roles = new LinkedList<String>();
		for(String k : p.getRoles().keySet()) {
			DirectoryPersonRole r = p.getRoles().get(k);
			roles.add("<b>&bull; <i>" + r.getLocalizedTitle() + "</i></b> &mdash; <i>" + r.getExtendedLocalizedUnit() + " (<a href=\"pocketcampus://directory.plugin.pocketcampus.org/query?q=" + k + "\">" + k + "</a>)</i>");
		}
		return TextUtils.join("<br>",  roles);
	}
	
}
