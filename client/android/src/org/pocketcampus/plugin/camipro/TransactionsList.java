package org.pocketcampus.plugin.camipro;

import java.lang.reflect.Type;
import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.communication.RequestHandler;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.parser.Json;
import org.pocketcampus.core.parser.JsonException;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.core.ui.ActionBar.Action;
import org.pocketcampus.plugin.authentication.AuthenticationPlugin;
import org.pocketcampus.shared.plugin.authentication.AuthToken;
import org.pocketcampus.shared.plugin.camipro.BalanceBean;
import org.pocketcampus.shared.plugin.camipro.TransactionBean;
import org.pocketcampus.utils.Notification;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TransactionsList extends PluginBase {
	private ActionBar actionBar_;
	private RequestHandler requestHandler_;
	private int progressCount_ = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camipro_main);
		setupActionBar(true);

		requestHandler_ = getRequestHandler();

		downloadData();
	}


	@Override
	protected void setupActionBar(boolean addHomeButton) {
		actionBar_ = (ActionBar) findViewById(R.id.actionbar);
		
		// Refresh the camipro data
		actionBar_.addAction(new Action() {

			@Override
			public void performAction(View view) {
				downloadData();
			}

			@Override
			public int getDrawable() {
				return R.drawable.refresh;
			}
		});
		
		super.setupActionBar(addHomeButton);
	}

	/**
	 * Download both Balance and Transactions
	 * Updated the UI
	 */
	private void downloadData() {
		downloadBalance();
		downloadTransactions();
	}

	/**
	 * Download the Balance info
	 */
	private void downloadBalance() {
		incrementProgressCounter();
		requestHandler_.execute(new BalanceRequest(), "getBalance", getRequestParameters());
	}

	/**
	 * Download the list of transactions
	 */
	private void downloadTransactions() {
		incrementProgressCounter();
		requestHandler_.execute(new TransactionsRequest(), "getTransactions", getRequestParameters());
	}
	
	/**
	 * Create a request to the Camipro server plugin using username/password
	 * @return
	 */
	private RequestParameters getRequestParameters() {
		RequestParameters parameters = new RequestParameters();
		parameters.addParameter("token", getAuthToken());
		
		return parameters;
	}
	
	private String getAuthToken() {
		AuthToken t = AuthenticationPlugin.getAuthToken(this);
		
		return new Gson().toJson(t);
	}

	/**
	 * Increments the progressCounter. It displays the progress bar
	 * of the action bar. It allows several parallel threads doing background
	 * work.
	 */
	private synchronized void incrementProgressCounter() {
		progressCount_++;
		actionBar_.setProgressBarVisibility(View.VISIBLE);
	}

	/**
	 * Decrements the progressCounter. Called when a thread has finished
	 * doing some background work.
	 */
	private synchronized void decrementProgressCounter() {
		progressCount_--;
		if(progressCount_ < 0) { //Should never happen!
			Log.e(this.getClass().toString(), "ERROR progresscount is negative!");
		}

		if(progressCount_ <= 0) {
			actionBar_.setProgressBarVisibility(View.GONE);
		}
	}

	@Override
	public PluginInfo getPluginInfo() {
		return new CamiproInfo();
	}

	@Override
	public PluginPreference getPluginPreference() {
		return null;
	}

	/**
	 * Server request for the camipro balance.
	 * 
	 * @author Jonas
	 *
	 */
	private class BalanceRequest extends DataRequest {
		BalanceBean bb_;

		@Override
		protected void doInBackgroundThread(String result) {
			try {
				bb_ = Json.fromJson(result, BalanceBean.class);
			} catch (JsonException e) {
				return;
			}
		}

		@Override
		protected void doInUiThread(String result) {
			decrementProgressCounter();
			
			if(bb_ != null) {
				TextView balance = (TextView) findViewById(R.id.camipro_balance_number);
				balance.setText(Float.toString(bb_.getCurrentBalance()));
			} else {
				Notification.showToast(getApplicationContext(), R.string.camipro_unable_balance);
			}
		}

		@Override
		protected void onCancelled() {
			decrementProgressCounter();
		}
	}

	/**
	 * Server request for the transactions
	 * 
	 * @author Jonas
	 *
	 */
	private class TransactionsRequest extends DataRequest {
		List<TransactionBean> ltb_;

		@Override
		protected void doInBackgroundThread(String result) {
			try {
				Type transactionsType = new TypeToken<List<TransactionBean>>(){}.getType();
				ltb_ = Json.fromJson(result, transactionsType);
			} catch (JsonException e) {
				return;
			}
		}

		@Override
		protected void doInUiThread(String result) {
			decrementProgressCounter();
			
			if(ltb_ != null) {
				ListView lv = (ListView) findViewById(R.id.camipro_list);
				
				// Create an adapter for the data
				lv.setAdapter(new TransactionAdapter(getApplicationContext(), R.layout.camipro_transaction, ltb_));
			} else {
				Notification.showToast(getApplicationContext(), R.string.camipro_unable_transactions);
			}
		}

		@Override
		protected void onCancelled() {
			decrementProgressCounter();
		}
	}
}
