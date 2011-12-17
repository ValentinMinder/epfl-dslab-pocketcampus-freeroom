package org.pocketcampus.plugin.camipro.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledDoubleSeparatedLayout;
import org.pocketcampus.android.platform.sdk.ui.list.ListViewElement;
import org.pocketcampus.plugin.camipro.android.CamiproMainView.TransactionAdapter;
import org.pocketcampus.plugin.camipro.android.iface.ICamiproModel;
import org.pocketcampus.plugin.camipro.android.iface.ICamiproView;
import org.pocketcampus.plugin.camipro.shared.CardLoadingWithEbankingInfo;
import org.pocketcampus.plugin.camipro.shared.CardStatistics;
import org.pocketcampus.plugin.camipro.shared.Transaction;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

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
		mLayout = new StandardTitledDoubleSeparatedLayout(this);

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);
		//setContentView(R.layout.camipro_main);

		//mLayout.setText("Loading");
		//refreshAll();
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
	public void transactionsUpdated() {
	}

	@Override
	public void balanceUpdated() {
	}

	@Override
	public void cardLoadingWithEbankingInfoUpdated() {
		mLayout.hideFirstTitle();
		//mLayout.setSecondTitle(getResources().getString());
		mLayout.setSecondTitle("Recharge Card with E-Banking");
		CardLoadingWithEbankingInfo ebanking = mModel.getCardLoadingWithEbankingInfo();
		if(ebanking == null)
			return;
		
		ArrayList<EbankingInfo> einfos = new ArrayList<CamiproCardRechargeView.EbankingInfo>();
		einfos.add(new EbankingInfo(null, "In order to load your CAMIPRO card, please copy the payment details below (account number, reference number, etc.) in the e-banking tool provided by your financial institution.\nThe payment to select is an orange payment slip."));
		einfos.add(new EbankingInfo("Reference number", ebanking.getIReferenceNumber()));
		einfos.add(new EbankingInfo("Paid to", ebanking.getIPaidTo()));
		einfos.add(new EbankingInfo("Account number", ebanking.getIAccountNumber()));
		einfos.add(new EbankingInfo(null, "The processing time for charging your CAMIPRO card is about 3 working days\nThe maximum amount is CHF 300.-"));
		
		ListView lv = new ListView(this);
		lv.setAdapter(new EbankingAdapter(this, R.layout.camipro_ebankinginfo, einfos));
		
		mLayout.removeSecondLayoutFillerView();
		mLayout.addSecondLayoutFillerView(lv);
	}

	@Override
	public void cardStatisticsUpdated() {
	}


	private void updateDisplay() {
		cardLoadingWithEbankingInfoUpdated();
	}
	
	
	@Override
	public void networkErrorHappened() {
		Toast.makeText(getApplicationContext(), "Network error!", Toast.LENGTH_SHORT).show();
	}

	private CamiproController mController;
	private ICamiproModel mModel;
	
	private StandardTitledDoubleSeparatedLayout mLayout;

	
	
	

	public class EbankingInfo {
		EbankingInfo(String t, String v) {
			title = t;
			value = v;
		}
		public String title;
		public String value;
	}
	
	public class EbankingAdapter extends ArrayAdapter<EbankingInfo> {

		public EbankingAdapter(Context context, int textViewResourceId, List<EbankingInfo> ebankingInfo) {
			super(context, textViewResourceId, ebankingInfo);
			li_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rid = textViewResourceId;
		}
	
		@Override
		public View getView(int position, View v, ViewGroup parent) {
	        if (v == null) {
	            v = li_.inflate(rid, null);
	        }
	        EbankingInfo t = getItem(position);
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
	        return v;
		}
		
		private LayoutInflater li_;
		private int rid;
		
	}



	
}
