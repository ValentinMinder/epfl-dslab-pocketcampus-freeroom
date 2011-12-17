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

public class CamiproMainView extends PluginView implements ICamiproView {

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return CamiproController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		Log.v("DEBUG", "CamiproMainView::onDisplay");
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
		ActionBar a = getActionBar();
		if (a != null) {
			RefreshAction refresh = new RefreshAction();
			a.addAction(refresh, 0);
		}
	}
	
	@Override
	protected void handleIntent(Intent aIntent) {
		Log.v("DEBUG", "CamiproMainView::handleIntent");
		// If we were pinged by auth plugin, then we must read the sessId
		if(aIntent != null && Intent.ACTION_VIEW.equals(aIntent.getAction())) {
			Uri aData = aIntent.getData();
			if(aData != null && "pocketcampus-authenticate".equals(aData.getScheme())) {
				String sessId = aData.getQueryParameter("sessid");
				mController.setCamiproCookie(sessId);
			}
		}
		
		// Normal start-up
		if(mModel.getCamiproCookie() == null) { // if we don't have cookie
			// get cookie (ping auth plugin)
			//Intent authIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("pocketcampus-authenticate://authentication.plugin.pocketcampus.org/do_auth?service=camipro"));
			//startActivity(authIntent);
			Intent authIntent = new Intent("org.pocketcampus.plugin.authentication.ACTION_AUTHENTICATE",
					Uri.parse("pocketcampus-authenticate://authentication.plugin.pocketcampus.org/do_auth?service=camipro"));
			startService(authIntent);
		}
		//if(mModel.getBalance() == null || mModel.getTransactions() == null) { // if we don't have some data
			// fetch them
			mController.refreshBalanceAndTransactions();
		//}
		//if(mModel.getCardStatistics() == null || mModel.getCardLoadingWithEbankingInfo() == null) { // if we don't have some other data
			// get them
			mController.refreshStatsAndLoadingInfo();
		//}
		// update display
		updateDisplay();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.v("DEBUG", "CamiproMainView::onResume");
		if(mController != null && mController.getCamiproCookie() == null) {
			// Resumed and lot logged in? go back
			finish();
		}
	}

	@Override
	public void transactionsUpdated() {
		List<Transaction> ltb = mModel.getTransactions();
		if(ltb == null)
			return;
		
		/*ArrayList<String> list = new ArrayList<String>();
		for (Transaction s : ltb) {
			list.add(s.getIDate() + "\t" + s.getIPlace() + "\t" + formatMoney(s.getIAmount()));
		}*/
		ListView lv = new ListView(getApplicationContext());
		lv.setAdapter(new TransactionAdapter(getApplicationContext(), R.layout.camipro_transaction, ltb));
		
		mLayout.removeSecondLayoutFillerView();
		//mLayout.addSecondLayoutFillerView(new ListViewElement(this, list));
		mLayout.addSecondLayoutFillerView(lv);

		
		//ListView lv = (ListView) findViewById(R.id.camipro_list);
		// Create an adapter for the data
		//lv.setAdapter(new TransactionAdapter(getApplicationContext(), R.layout.camipro_transaction, ltb));
		updateDate();
	}

	@Override
	public void balanceUpdated() {
		Double bal = mModel.getBalance();
		if(bal == null)
			return;
		
		updatedBalanceOrStats();
		//TextView balance = (TextView) findViewById(R.id.camipro_balance_number);
		//balance.setText(formatMoney(bal));
		updateDate();
	}

	@Override
	public void cardLoadingWithEbankingInfoUpdated() {
		CardLoadingWithEbankingInfo i = mModel.getCardLoadingWithEbankingInfo();
		if(i == null)
			return;
		
		/*TextView tv = (TextView) findViewById(R.id.camipro_ebanking_paid_to_text);
		tv.setText(i.getIPaidTo());

		tv = (TextView) findViewById(R.id.camipro_ebanking_account_number_text);
		tv.setText(i.getIAccountNumber());

		tv = (TextView) findViewById(R.id.camipro_ebanking_ref_number_text);
		tv.setText(i.getIReferenceNumber());
		updateDate();*/
	}

	@Override
	public void cardStatisticsUpdated() {
		CardStatistics s = mModel.getCardStatistics();
		if(s == null)
			return;
		
		updatedBalanceOrStats();
		/*TextView tv = (TextView) findViewById(R.id.camipro_ebanking_1month_text);
		tv.setText(formatMoney(s.getITotalPaymentsLastMonth()));

		tv = (TextView) findViewById(R.id.camipro_ebanking_3months_text);
		tv.setText(formatMoney(s.getITotalPaymentsLastThreeMonths()));

		tv = (TextView) findViewById(R.id.camipro_ebanking_average_text);
		tv.setText(formatMoney(s.getITotalPaymentsLastThreeMonths() / 3.0));*/
		updateDate();
	}
	
	private void updatedBalanceOrStats() {
		ArrayList<Amout> l = new ArrayList<Amout>();
		Double bal = mModel.getBalance();
		if(bal != null) {
			l.add(new Amout(getResources().getString(R.string.camipro_current_balance), bal));
		}
		CardStatistics s = mModel.getCardStatistics();
		if(s != null) {
			l.add(new Amout(getResources().getString(R.string.camipro_ebanking_1month_title),
					s.getITotalPaymentsLastMonth()));
			l.add(new Amout(getResources().getString(R.string.camipro_ebanking_3months_title),
					s.getITotalPaymentsLastThreeMonths()));
			l.add(new Amout(getResources().getString(R.string.camipro_ebanking_average_title),
					s.getITotalPaymentsLastThreeMonths() / 3.0));
		}
		//ListViewElement mAddView = new ListViewElement(this, l);
		ListView lv = new ListView(getApplicationContext());
		lv.setAdapter(new AmountAdapter(getApplicationContext(), R.layout.camipro_amount, l));
		RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		lv.setLayoutParams(p);
		mLayout.removeFirstLayoutFillerView();
		mLayout.addFirstLayoutFillerView(lv);
	}

	
	private void refreshAll() {
		mController.refreshBalanceAndTransactions();
		mController.refreshStatsAndLoadingInfo();
	}
	
	private void updateDisplay() {
		transactionsUpdated();
		balanceUpdated();
		cardLoadingWithEbankingInfoUpdated();
		cardStatisticsUpdated();
	}
	
	private void updateDate() {
		mLayout.setFirstTitle(getResources().getString(R.string.camipro_balance_section_title));
		// Last update
		String date = mModel.getLastUpdateDate();
		if(date != null) {
			//TextView dateLastUpdated = (TextView) findViewById(R.id.camipro_balance_date_text);
			//dateLastUpdated.setText("As of " + date + " given no offline transactions");
			mLayout.setSecondTitle(String.format(
					getResources().getString(R.string.camipro_transactions_section_title), date));
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.camipro_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		
		if(item.getItemId() == R.id.camipro_recharge) {			
			Intent i = new Intent(this, CamiproCardRechargeView.class);
			startActivity(i);
		} else if(item.getItemId() == R.id.camipro_logout) {			
			mController.reset();
			finish();
		}
		

		return true;
	}
	
	@Override
	public void networkErrorHappened() {
		Toast.makeText(getApplicationContext(), "Network error!", Toast.LENGTH_SHORT).show();
	}

	private CamiproController mController;
	private ICamiproModel mModel;
	
	private StandardTitledDoubleSeparatedLayout mLayout;


	
	
	

	/*****
	 * HELPERS
	 */
	
	private static String formatMoney(double money) {
		return String.format("CHF %.2f", money);
	}

	public class TransactionAdapter extends ArrayAdapter<Transaction> {
		private LayoutInflater li_;
		//private java.text.DateFormat df_; // Used to format the date
		private Context context_;
	
		// Colors
		private int minus_;
		private int plus_;
		
		/**
		 * Adapter constructor
		 * @param context The Camipro plugin
		 * @param textViewResourceId Layout for a row in the list
		 * @param transactions List of transactions
		 */
		public TransactionAdapter(Context context, int textViewResourceId, List<Transaction> transactions) {
			super(context, textViewResourceId, transactions);
			li_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			//df_ = DateFormat.getDateFormat(context);
			context_ = context;
	
			minus_ = context_.getResources().getColor(R.color.camipro_minus);
			plus_ = context_.getResources().getColor(R.color.camipro_plus);
		}
	
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
	        if (v == null) {
	            v = li_.inflate(R.layout.camipro_transaction, null);
	        }
	        Transaction t = getItem(position);
	        
	        TextView tv;
	
	        tv = (TextView)v.findViewById(R.id.camipro_item_date);
	        tv.setText(t.getIDate());
	        
	        tv = (TextView)v.findViewById(R.id.camipro_item_description);
	        tv.setText(t.getIPlace());
	        
	        tv = (TextView)v.findViewById(R.id.camipro_item_amount);
	        tv.setText(formatMoney(t.getIAmount()));
	        tv.setTextColor(t.getIAmount() < 0.0 ? minus_ : plus_);
	        
	        return v;
		}
	}

	public class Amout {
		Amout(String t, double v) {
			title = t;
			value = v;
		}
		public String title;
		public double value;
	}
	
	public class AmountAdapter extends ArrayAdapter<Amout> {
		
		public AmountAdapter(Context context, int textViewResourceId, List<Amout> amounts) {
			super(context, textViewResourceId, amounts);
			li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rid = textViewResourceId;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			if (v == null) {
				v = li.inflate(rid, null);
			}
			TextView tv;
			Amout t = getItem(position);
			tv = (TextView) v.findViewById(R.id.camipro_amount_title);
			tv.setText(t.title);
			tv = (TextView) v.findViewById(R.id.camipro_amount_value);
			tv.setText(formatMoney(t.value));
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
			refreshAll();
		}
	}







	
}
