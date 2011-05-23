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
import org.pocketcampus.shared.plugin.camipro.EbankingBean;
import org.pocketcampus.shared.plugin.camipro.TransactionBean;
import org.pocketcampus.utils.Notification;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * PluginBase class for the Camipro plugin.
 * Should only be launched if the user is logged in. 
 * 
 * This uses the WebService provided by the Camipro team. 
 * 
 * Data is redownloaded every time the plugin launches.
 * Data is really small and changes often.
 * 
 * @status WIP
 * 
 * @author Jonas
 *
 */
public class SecuredCamipro extends PluginBase {
	private ActionBar actionBar_;
	private RequestHandler requestHandler_;
	private int progressCount_ = 0;
	
	private static final Gson gson_ = new Gson();

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
		downloadEbanking();
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
	 * Download the ebanking infos
	 */
	private void downloadEbanking() {
		incrementProgressCounter();
		requestHandler_.execute(new EbankingRequest(), "getEbanking", getRequestParameters());
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
	
	/**
	 * Get the user's token to identify him
	 * @return
	 */
	private String getAuthToken() {
		AuthToken t = AuthenticationPlugin.getAuthToken(this);
		
		return gson_.toJson(t);
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
	 * Format a price into an human-readable form
	 * @param money
	 * @return
	 */
	static String formatMoney(double money) {
		return String.format("CHF %.2f", money);
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
				String txt = formatMoney(bb_.getCurrentBalance());
				
				TextView balance = (TextView) findViewById(R.id.camipro_balance_number);
				balance.setText(txt);
				
				balance = (TextView) findViewById(R.id.camipro_ebanking_balance_number);
				balance.setText(txt);
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
	/**
	 * Server request for the ebanking part
	 * 
	 * @author Jonas
	 *
	 */
	private class EbankingRequest extends DataRequest {
		EbankingBean ebb_;

		@Override
		protected void doInBackgroundThread(String result) {
			try {
				ebb_ = Json.fromJson(result, EbankingBean.class);
			} catch (JsonException e) {
				return;
			}
		}

		@Override
		protected void doInUiThread(String result) {
			decrementProgressCounter();
			
			if(ebb_ != null) {
				TextView tv = (TextView) findViewById(R.id.camipro_ebanking_paid_to_text);
				tv.setText(ebb_.getPaidNameTo());

				tv = (TextView) findViewById(R.id.camipro_ebanking_account_number_text);
				tv.setText(ebb_.getAccountNr());

				tv = (TextView) findViewById(R.id.camipro_ebanking_ref_number_text);
				tv.setText(ebb_.getBvrReferenceReadable());

				tv = (TextView) findViewById(R.id.camipro_ebanking_1month_text);
				tv.setText(formatMoney(ebb_.getTotal1M()));

				tv = (TextView) findViewById(R.id.camipro_ebanking_3months_text);
				tv.setText(formatMoney(ebb_.getTotal3M()));

				tv = (TextView) findViewById(R.id.camipro_ebanking_average_text);
				tv.setText(formatMoney(ebb_.getAverage3M()));

				
			} else {
				Notification.showToast(getApplicationContext(), R.string.camipro_unable_ebanking);
			}
		}

		@Override
		protected void onCancelled() {
			decrementProgressCounter();
		}
	}
}
