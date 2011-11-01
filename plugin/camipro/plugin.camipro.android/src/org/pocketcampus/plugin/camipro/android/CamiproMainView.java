package org.pocketcampus.plugin.camipro.android;

import java.util.Date;
import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardLayout;
import org.pocketcampus.plugin.camipro.android.iface.ICamiproModel;
import org.pocketcampus.plugin.camipro.android.iface.ICamiproView;
import org.pocketcampus.plugin.camipro.shared.EbankingBean;
import org.pocketcampus.plugin.camipro.shared.Transaction;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CamiproMainView extends PluginView implements ICamiproView {

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

		// The ActionBar is added automatically when you call setContentView
		//setContentView(mLayout);
		setContentView(R.layout.camipro_main);

		//mLayout.setText("Loading");
		//refreshAll();
	}

	@Override
	public void transactionsUpdated() {
		List<Transaction> ltb = mModel.getTransactions();
		ListView lv = (ListView) findViewById(R.id.camipro_list);

		// Create an adapter for the data
		lv.setAdapter(new TransactionAdapter(getApplicationContext(), R.layout.camipro_transaction, ltb));
	}

	@Override
	public void balanceUpdated() {
		TextView balance = (TextView) findViewById(R.id.camipro_balance_number);
		balance.setText(formatMoney(mModel.getBalance()));

		// Last update
		String date = new Date().toLocaleString();
		balance = (TextView) findViewById(R.id.camipro_balance_date_text);
		balance.setText(date);
	}

	@Override
	public void ebankingUpdated() {
		EbankingBean ebb = mModel.getEbanking();
		TextView tv = (TextView) findViewById(R.id.camipro_ebanking_paid_to_text);
		tv.setText(ebb.getPaidNameTo());

		tv = (TextView) findViewById(R.id.camipro_ebanking_account_number_text);
		tv.setText(ebb.getAccountNr());

		tv = (TextView) findViewById(R.id.camipro_ebanking_ref_number_text);
		tv.setText(ebb.getBvrReferenceReadable());

		tv = (TextView) findViewById(R.id.camipro_ebanking_1month_text);
		tv.setText(formatMoney(ebb.getTotal1M()));

		tv = (TextView) findViewById(R.id.camipro_ebanking_3months_text);
		tv.setText(formatMoney(ebb.getTotal3M()));

		tv = (TextView) findViewById(R.id.camipro_ebanking_average_text);
		tv.setText(formatMoney(ebb.getAverage3M()));
	}

	private void refreshAll() {
		mController.refreshBalance();
		mController.refreshEbanking();
		mController.refreshTransactions();
	}
	
	private static String formatMoney(double money) {
		return String.format("CHF %.2f", money);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.camipro_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		
		if(item.getItemId() == R.id.camipro_refresh_ebanking) {			
			mController.refreshEbanking();
		} else if(item.getItemId() == R.id.camipro_refresh_transactions) {			
			mController.refreshTransactions();
		} else if(item.getItemId() == R.id.camipro_refresh_balance) {			
			mController.refreshBalance();
		}
		

		return true;
	}
	
	@Override
	public void networkErrorHappened() {
		Toast.makeText(getApplicationContext(), "Network error!", Toast.LENGTH_SHORT).show();
	}

	private CamiproController mController;
	private ICamiproModel mModel;

	
	
	
	
	
	
		
		
	// TODO remove this class from here
	
	public class TransactionAdapter extends ArrayAdapter<Transaction> {
		private LayoutInflater li_;
		private java.text.DateFormat df_; // Used to format the date
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
			df_ = DateFormat.getDateFormat(context);
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
	        tv.setText(t.xDate);
	        
	        tv = (TextView)v.findViewById(R.id.camipro_item_description);
	        tv.setText(t.xDescription);
	        
	        tv = (TextView)v.findViewById(R.id.camipro_item_amount);
	        tv.setText(formatMoney(t.xAmount));
	        tv.setTextColor(t.xAmount < 0.0 ? minus_ : plus_);
	        
	        return v;
		}
	}

	
}
