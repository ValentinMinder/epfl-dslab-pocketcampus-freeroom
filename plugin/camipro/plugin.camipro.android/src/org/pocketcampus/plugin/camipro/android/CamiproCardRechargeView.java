package org.pocketcampus.plugin.camipro.android;

import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.PCSectionedList.PCSectionItem;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledScrollableDoubleLayout;
import org.pocketcampus.plugin.camipro.android.iface.ICamiproModel;
import org.pocketcampus.plugin.camipro.android.iface.ICamiproView;
import org.pocketcampus.plugin.camipro.shared.CardLoadingWithEbankingInfo;
import org.pocketcampus.plugin.camipro.shared.CardStatistics;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class CamiproCardRechargeView extends PluginView implements ICamiproView {

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return CamiproController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		// Get and cast the controller and model
		mController = (CamiproController) controller;
		mModel = (CamiproModel) controller.getModel();

		// The StandardLayout is a RelativeLayout with a TextView in its center.
		//mLayout = new StandardLayout(this);
		mLayout = new StandardTitledLayout(this);

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);
		//setContentView(R.layout.camipro_main);

		//mLayout.setText("Loading");
		//refreshAll();
		ActionBar a = getActionBar();
		if (a != null) {
			RefreshAction refresh = new RefreshAction();
			a.addAction(refresh, 0);
		}
	}
	
	@Override
	protected void handleIntent(Intent aIntent) {
		// Normal start-up
		if(mModel.getCardLoadingWithEbankingInfo() == null) { // if we don't have the data
			// get it
			// Normally this shouldn't happen
			mController.refreshStatsAndLoadingInfo();
		}
		// update display
		updateDisplay();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(mController != null && mController.getCamiproCookie() == null) {
			// Resumed and lot logged in? go back
			finish();
		}
	}


	@Override
	public void transactionsUpdated() {
		// no need coz not displayed
	}

	@Override
	public void balanceUpdated() {
		// no need coz not displayed
	}
	
	@Override
	public void lastUpdateDateUpdated() {
		// no need coz not displayed
	}

	@Override
	public void cardLoadingWithEbankingInfoUpdated() {
		updateDisplay();
	}

	@Override
	public void cardStatisticsUpdated() {
		updateDisplay();
	}


	private void updateDisplay() {
		CardLoadingWithEbankingInfo ebanking = mModel.getCardLoadingWithEbankingInfo();
		CardStatistics stats = mModel.getCardStatistics();
		
		if(ebanking == null && stats == null) 
			return;
		
		ArrayList<EbankingInfo> einfos = new ArrayList<CamiproCardRechargeView.EbankingInfo>();
		mLayout.hideTitle();
		
		if(ebanking != null) {
			einfos.add(new EbankingInfo(getResources().getString(R.string.camipro_ebanking_section_title), null, true));
			einfos.add(new EbankingInfo(null, getResources().getString(R.string.camipro_ebanking_infos_text), false));
			einfos.add(new EbankingInfo(getResources().getString(R.string.camipro_ebanking_ref_number_title), ebanking.getIReferenceNumber(), false));
			einfos.add(new EbankingInfo(getResources().getString(R.string.camipro_ebanking_paid_to_title), ebanking.getIPaidTo(), false));
			einfos.add(new EbankingInfo(getResources().getString(R.string.camipro_ebanking_account_number_title), ebanking.getIAccountNumber(), false));
			einfos.add(new EbankingInfo(null, getResources().getString(R.string.camipro_ebanking_infos_remark), false));
		}
		
		if(stats != null) {
			einfos.add(new EbankingInfo(getResources().getString(R.string.camipro_statistics_section_title), null, true));
			einfos.add(new EbankingInfo(getResources().getString(R.string.camipro_ebanking_1month_title), CamiproMainView.formatMoney(stats.getITotalPaymentsLastMonth()), false));
			einfos.add(new EbankingInfo(getResources().getString(R.string.camipro_ebanking_3months_title), CamiproMainView.formatMoney(stats.getITotalPaymentsLastThreeMonths()), false));
			einfos.add(new EbankingInfo(getResources().getString(R.string.camipro_ebanking_average_title), CamiproMainView.formatMoney(stats.getITotalPaymentsLastThreeMonths() / 3.0), false));
		}
			
		ListView lv = new ListView(this);
		
		lv.setAdapter(new EbankingAdapter(this, R.layout.camipro_ebankinginfo, einfos));
		LayoutParams p = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		lv.setLayoutParams(p);
		
		mLayout.removeFillerView();
		mLayout.addFillerView(lv);
	}
	
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.camipro_ebanking_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		
		if(item.getItemId() == R.id.camipro_send_ebankinginfo_byemail) {			
			mController.sendEmailWithLoadingDetails();
		}
		

		return true;
	}
	

	@Override
	public void emailSent(String result) {
		Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void camiproServersDown() {
		Toast.makeText(getApplicationContext(), getResources().getString(R.string.camipro_error_camipro_down), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void notLoggedIn() {
		mController.reset();
		CamiproMainView.pingAuthPlugin(this);
	}
	@Override
	public void networkErrorHappened() {
		Toast.makeText(getApplicationContext(), getResources().getString(R.string.camipro_connection_error_happened), Toast.LENGTH_SHORT).show();
	}

	private CamiproController mController;
	private ICamiproModel mModel;
	
	private StandardTitledLayout mLayout;

	
	
	

	public class EbankingInfo {
		EbankingInfo(String t, String v, boolean s) {
			title = t;
			value = v;
			isSeparator = s;
		}
		public String title;
		public String value;
		public boolean isSeparator;
	}
	
	public class EbankingAdapter extends ArrayAdapter<EbankingInfo> {

		public EbankingAdapter(Context context, int textViewResourceId, List<EbankingInfo> ebankingInfo) {
			super(context, textViewResourceId, ebankingInfo);
			li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rid = textViewResourceId;
		}
	
		@Override
		public View getView(int position, View v, ViewGroup parent) {
	        EbankingInfo t = getItem(position);
	        if(t.isSeparator) {
				v = li.inflate(R.layout.sdk_sectioned_list_item_section, null);
				//v.setOnClickListener(null);
				//v.setOnLongClickListener(null);
				//v.setLongClickable(true);
		        TextView tv;
		        tv = (TextView)v.findViewById(R.id.PCSectioned_list_item_section_text);
		        if(t.title != null)
		        	tv.setText(t.title);
		        else
		        	tv.setVisibility(View.GONE);
		        tv = (TextView)v.findViewById(R.id.PCSectioned_list_item_section_description);
		        if(t.value != null)
		        	tv.setText(t.value);
		        else
		        	tv.setVisibility(View.GONE);
	        } else {
	            v = li.inflate(rid, null);
		        TextView tv;
		        tv = (TextView)v.findViewById(R.id.camipro_ebankinginfo_title);
		        if(t.title != null)
		        	tv.setText(t.title);
		        else
		        	tv.setVisibility(View.GONE);
		        tv = (TextView)v.findViewById(R.id.camipro_ebankinginfo_value);
		        if(t.value != null)
		        	tv.setText(t.value);
		        else
		        	tv.setVisibility(View.GONE);
	        }
	        return v;
		}
		
		private LayoutInflater li;
		private int rid;
		
	}


	/**
	 * Refreshes camipro
	 * 
	 * @author Amer <amer.chamseddine@epfl.ch>
	 * 
	 */
	private class RefreshAction implements Action {

		/**
		 * The constructor which doesn't do anything
		 */
		RefreshAction() {
		}

		/**
		 * Returns the resource for the icon of the button in the action bar
		 */
		@Override
		public int getDrawable() {
			return R.drawable.sdk_action_bar_refresh;
		}

		/**
		 * Defines what is to be performed when the user clicks on the button in
		 * the action bar
		 */
		@Override
		public void performAction(View view) {
			mController.refreshStatsAndLoadingInfo();
		}
	}


	
}
