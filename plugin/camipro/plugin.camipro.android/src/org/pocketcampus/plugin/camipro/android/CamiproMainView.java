package org.pocketcampus.plugin.camipro.android;

import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.platform.android.ui.layout.StandardLayout;
import org.pocketcampus.platform.android.ui.layout.StandardTitledDoubleSeparatedLayout;
import org.pocketcampus.plugin.camipro.R;
import org.pocketcampus.plugin.camipro.android.iface.ICamiproView;
import org.pocketcampus.plugin.camipro.shared.Transaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * CamiproMainView - Main view that shows Camipro balance and transactions.
 * 
 * This is the main view in the Camipro Plugin. It shows the Balance and the
 * transactions. It checks if the user is logged in, if not it pings the
 * Authentication Plugin. When it gets back a valid SessionId it fetches the
 * user's Camipro data.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class CamiproMainView extends PluginView implements ICamiproView {

	private CamiproController mController;
	private CamiproModel mModel;

	private StandardTitledDoubleSeparatedLayout mLayout;

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return CamiproController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {

		// Get and cast the controller and model
		mController = (CamiproController) controller;
		mModel = (CamiproModel) controller.getModel();

		// Setup layout
		mLayout = new StandardTitledDoubleSeparatedLayout(this);

		mLayout.hideFirstTitle();
		mLayout.hideSecondTitle();

		setActionBarTitle(getString(R.string.camipro_plugin_title));
	}

	/**
	 * Handles the intent that was used to start this plugin.
	 * 
	 * If we were pinged by auth plugin, then we must read the sessId. Otherwise
	 * we do a normal startup, and if we do not have the camiproCookie we ping
	 * the Authentication Plugin.
	 */
	@Override
	protected void handleIntent(Intent aIntent) {
		// check if pinged by auth plugin
		// check if auth succeeded
		/*
		 * boolean pinged = false; boolean succ = false; if(aIntent != null &&
		 * Intent.ACTION_VIEW.equals(aIntent.getAction())) { Uri aData =
		 * aIntent.getData(); if(aData != null &&
		 * "pocketcampus-authenticate".equals(aData.getScheme())) { pinged =
		 * true; //String sessId = aData.getQueryParameter("sessid");
		 * //mModel.setCamiproCookie(sessId); Bundle extras =
		 * aIntent.getExtras(); if(extras != null &&
		 * extras.getString("tequilatoken") != null) { succ = true; } } }
		 * 
		 * Log.v("DEBUG", "CamiproMainView::handleIntent " + pinged + succ);
		 */

		// startup logic
		/*
		 * if(!pinged) { mController.getTequilaToken(); return; } if(succ) {
		 * mController.getCamiproSession(); } else { finish(); }
		 */

//		// Normal start-up
//		if (mModel.getCamiproCookie() == null) {
//			mController.getTequilaToken();
//		} else {
//			mController.refreshBalanceAndTransactions();
//		}

		if(CamiproController.sessionExists(this)) { // I think this is no longer necessary, since the auth plugin doesnt blindly redo auth (well, this saves the one call that the auth plugin does to check if the session is valid)
			mController.refreshBalanceAndTransactions();
		} else { 
			CamiproController.pingAuthPlugin(this);
		}
	}

	/**
	 * This is called when the Activity is resumed.
	 * 
	 * If the user presses back on the Authentication window, This Activity is
	 * resumed but we do not have the camiproCookie. In this case we close the
	 * Activity.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		/*
		 * if(mModel != null && mModel.getCamiproCookie() == null) { // Resumed
		 * and lot logged in? go back finish(); } if(mController != null) { //
		 * Whenever we switch back to this activity, update contents
		 * mController.refreshBalanceAndTransactions(); }
		 */
	}

	@Override
	protected String screenName() {
		return "/camipro";
	}

	@Override
	public void transactionsUpdated() {
		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);
		removeAllActionsFromActionBar();
		addActionToActionBar(new StatsAction());
		addActionToActionBar(new EBankingAction());
		addActionToActionBar(new RefreshAction());

		List<Transaction> ltb = mModel.getTransactions();
		if (ltb == null)
			return;
		mLayout.removeSecondLayoutFillerView();
		if (ltb.size() > 0) {
			ListView lv = new ListView(getApplicationContext());
			lv.setAdapter(new TransactionAdapter(getApplicationContext(),
					R.layout.camipro_transaction, ltb));
			mLayout.addSecondLayoutFillerView(lv);
		} else {
			StandardLayout eLayout = new StandardLayout(this);
			eLayout.setText(getResources().getString(
					R.string.camipro_no_recent_transactions));
			mLayout.addSecondLayoutFillerView(eLayout);
		}
	}

	@Override
	public void balanceUpdated() {
		Double bal = mModel.getBalance();
		if (bal == null)
			return;
		mLayout.setFirstTitle(getResources().getString(
				R.string.camipro_balance_section_title));
		ArrayList<Amout> l = new ArrayList<Amout>();
		l.add(new Amout(getResources().getString(
				R.string.camipro_current_balance), bal));
		ListView lv = new ListView(getApplicationContext());
		lv.setAdapter(new AmountAdapter(getApplicationContext(),
				R.layout.camipro_amount, l));
		RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lv.setLayoutParams(p);
		mLayout.removeFirstLayoutFillerView();
		mLayout.addFirstLayoutFillerView(lv);
	}

	@Override
	public void cardLoadingWithEbankingInfoUpdated() {
	}

	@Override
	public void cardStatisticsUpdated() {
	}

	@Override
	public void lastUpdateDateUpdated() {
		String date = mModel.getLastUpdateDate();
		if (date != null) {
			mLayout.setSecondTitle(String.format(
					getResources().getString(
							R.string.camipro_transactions_section_title), date));
		}
	}

	@Override
	public void gotCamiproCookie() {
		mController.refreshBalanceAndTransactions();
	}

	/*
	 * private void updateDisplay() { transactionsUpdated(); balanceUpdated();
	 * cardLoadingWithEbankingInfoUpdated(); cardStatisticsUpdated();
	 * lastUpdateDateUpdated(); }
	 */

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// MenuInflater inflater = getMenuInflater();
	// inflater.inflate(R.menu.camipro_main, menu);
	// return true;
	// }
	//
	// @Override
	// public boolean onOptionsItemSelected(android.view.MenuItem item) {
	// if(item.getItemId() == R.id.camipro_recharge) {
	// Intent i = new Intent(this, CamiproCardRechargeView.class);
	// startActivity(i);
	// trackEvent("OpenStatsAndRefill", null);
	// }
	// return super.onOptionsItemSelected(item);
	// }

	@Override
	public void emailSent(String result) {
		Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void networkErrorHappened() {
		setUnrecoverableErrorOccurred(getString(R.string.sdk_connection_error_happened));
	}

	@Override
	public void authenticationFailed() {
		setUnrecoverableErrorOccurred(getString(R.string.sdk_authentication_failed));
	}

	@Override
	public void userCancelledAuthentication() {
		finish();
	}

	@Override
	public void camiproServersDown() {
		setUnrecoverableErrorOccurred(getString(R.string.camipro_error_camipro_down));
	}

	/*****
	 * HELPER CLASSES AND FUNCTIONS
	 */

	public static String formatMoney(double money) {
		return String.format("CHF %.2f", money);
	}

	public class TransactionAdapter extends ArrayAdapter<Transaction> {
		private LayoutInflater li_;
		private Context context_;

		// Colors
		private int minus_;
		private int plus_;

		/**
		 * Adapter constructor
		 * 
		 * @param context
		 *            The Camipro plugin
		 * @param textViewResourceId
		 *            Layout for a row in the list
		 * @param transactions
		 *            List of transactions
		 */
		public TransactionAdapter(Context context, int textViewResourceId,
				List<Transaction> transactions) {
			super(context, textViewResourceId, transactions);
			li_ = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

			tv = (TextView) v.findViewById(R.id.camipro_item_date);
			tv.setText(t.getIDate());

			tv = (TextView) v.findViewById(R.id.camipro_item_description);
			tv.setText(t.getIPlace());

			tv = (TextView) v.findViewById(R.id.camipro_item_amount);
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

		public AmountAdapter(Context context, int textViewResourceId,
				List<Amout> amounts) {
			super(context, textViewResourceId, amounts);
			li = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rid = textViewResourceId;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			if (v == null) {
				v = li.inflate(rid, null);
			}
			TextView tv;
			Amout t = getItem(position);
			tv = (TextView) v.findViewById(R.id.camipro_amount_value);
			tv.setText(formatMoney(t.value));
			return v;
		}

		private LayoutInflater li;
		private int rid;

	}

	private class EBankingAction implements
			com.markupartist.android.widget.Action {

		@Override
		public int getDrawable() {
			return R.drawable.camipro_menu_recharge;
		}

		@Override
		public void performAction(View view) {
			Intent i = new Intent(CamiproMainView.this,
					CamiproCardRechargeView.class);
			startActivity(i);
			trackEvent("Refill", null);
		}

		@Override
		public String getDescription() {
			return getString(R.string.camipro_string_recharge);
		}
	}

	private class StatsAction implements com.markupartist.android.widget.Action {

		@Override
		public int getDrawable() {
			return R.drawable.camipro_menu_stats;
		}

		@Override
		public void performAction(View view) {
			Intent i = new Intent(CamiproMainView.this, CamiproStatsView.class);
			startActivity(i);
			trackEvent("Stats", null);
		}

		@Override
		public String getDescription() {
			return getString(R.string.camipro_statistics_section_title);
		}

	}

	/**
	 * Refreshes camipro
	 * 
	 * @author Amer <amer.chamseddine@epfl.ch>
	 * 
	 */
	private class RefreshAction implements
			com.markupartist.android.widget.Action {

		/**
		 * Returns the resource for the icon of the button in the action bar
		 */
		@Override
		public int getDrawable() {
			return R.drawable.sdk_refresh;
		}

		/**
		 * Defines what is to be performed when the user clicks on the button in
		 * the action bar
		 */
		@Override
		public void performAction(View view) {
			trackEvent("Refresh", null);
			mController.refreshBalanceAndTransactions();
			CamiproMainView.this.setLoadingContentScreen();
		}

		@Override
		public String getDescription() {
			return getString(R.string.sdk_reload_title);
		}
	}

}
