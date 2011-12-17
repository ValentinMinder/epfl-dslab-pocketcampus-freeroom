package org.pocketcampus.plugin.camipro.android;

import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledScrollableDoubleLayout;
import org.pocketcampus.plugin.camipro.android.iface.ICamiproModel;
import org.pocketcampus.plugin.camipro.android.iface.ICamiproView;
import org.pocketcampus.plugin.camipro.shared.CardLoadingWithEbankingInfo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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
		
		mLayout.setTitle(getResources().getString(R.string.camipro_ebanking_section_title));
		CardLoadingWithEbankingInfo ebanking = mModel.getCardLoadingWithEbankingInfo();
		if(ebanking == null)
			return;
		
		ArrayList<EbankingInfo> einfos = new ArrayList<CamiproCardRechargeView.EbankingInfo>();
		einfos.add(new EbankingInfo(null, getResources().getString(R.string.camipro_ebanking_infos_text)));
		einfos.add(new EbankingInfo(getResources().getString(R.string.camipro_ebanking_ref_number_title), ebanking.getIReferenceNumber()));
		einfos.add(new EbankingInfo(getResources().getString(R.string.camipro_ebanking_paid_to_title), ebanking.getIPaidTo()));
		einfos.add(new EbankingInfo(getResources().getString(R.string.camipro_ebanking_account_number_title), ebanking.getIAccountNumber()));
		einfos.add(new EbankingInfo(null, getResources().getString(R.string.camipro_ebanking_infos_remark)));
		
		ListView lv = new ListView(this);
		
		lv.setAdapter(new EbankingAdapter(this, R.layout.camipro_ebankinginfo, einfos));
		LayoutParams p = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		lv.setLayoutParams(p);
		
		mLayout.removeFillerView();
		mLayout.addFillerView(lv);
	}

	@Override
	public void cardStatisticsUpdated() {
	}


	private void updateDisplay() {
		cardLoadingWithEbankingInfoUpdated();
	}
	
	
	@Override
	public void networkErrorHappened() {
		Toast.makeText(getApplicationContext(), getResources().getString(R.string.sdk_connection_error_happened), Toast.LENGTH_SHORT).show();
	}

	private CamiproController mController;
	private ICamiproModel mModel;
	
	private StandardTitledLayout mLayout;

	
	
	

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
