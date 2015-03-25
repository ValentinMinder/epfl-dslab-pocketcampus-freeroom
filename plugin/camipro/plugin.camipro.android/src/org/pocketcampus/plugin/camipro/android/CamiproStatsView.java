package org.pocketcampus.plugin.camipro.android;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.platform.android.ui.adapter.LazyAdapter;
import org.pocketcampus.platform.android.utils.Preparated;
import org.pocketcampus.platform.android.utils.Preparator;
import org.pocketcampus.plugin.camipro.R;
import org.pocketcampus.plugin.camipro.android.iface.ICamiproView;
import org.pocketcampus.plugin.camipro.shared.CardStatistics;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

/**
 * CamiproStatsView - View that shows Camipro recharge with e-banking.
 * 
 * This view shows the statistics of the card.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class CamiproStatsView extends PluginView implements ICamiproView {

	private CamiproController mController;
	private CamiproModel mModel;

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
		setContentView(R.layout.camipro_listview);

		setActionBarTitle(getString(R.string.camipro_statistics_section_title));

		mController.refreshStatsAndLoadingInfo();
		updateDisplay();
	}

	@Override
	protected String screenName() {
		return "/camipro/stats";
	}

	@Override
	public void transactionsUpdated() {
	}

	@Override
	public void balanceUpdated() {
	}

	@Override
	public void lastUpdateDateUpdated() {
	}

	@Override
	public void cardLoadingWithEbankingInfoUpdated() {
		updateDisplay();
	}

	@Override
	public void cardStatisticsUpdated() {
		updateDisplay();
	}

	@Override
	public void gotCamiproCookie() {
		mController.refreshStatsAndLoadingInfo();
	}

	private void updateDisplay() {
		CardStatistics stats = mModel.getCardStatistics();

		if (stats == null)
			return;

		ArrayList<Map.Entry<String, String>> einfos = new ArrayList<Map.Entry<String, String>>();

		if (stats != null) {
			einfos.add(new AbstractMap.SimpleEntry<String, String>(
					getResources().getString(
							R.string.camipro_ebanking_1month_title),
					CamiproMainView.formatMoney(stats
							.getITotalPaymentsLastMonth())));
			einfos.add(new AbstractMap.SimpleEntry<String, String>(
					getResources().getString(
							R.string.camipro_ebanking_3months_title),
					CamiproMainView.formatMoney(stats
							.getITotalPaymentsLastThreeMonths())));
			einfos.add(new AbstractMap.SimpleEntry<String, String>(
					getResources().getString(
							R.string.camipro_ebanking_average_title),
					CamiproMainView.formatMoney(stats
							.getITotalPaymentsLastThreeMonths() / 3.0)));

			Preparated<Map.Entry<String, String>> p = new Preparated<Map.Entry<String, String>>(
					einfos, new Preparator<Map.Entry<String, String>>() {

						@Override
						public Object content(int res,
								Entry<String, String> item) {
							switch (res) {
							case R.id.camipro_ebankinginfo_title:
								return item.getKey();
							case R.id.camipro_ebankinginfo_value:
								return item.getValue();
							default:
								throw new RuntimeException();
							}
						}

						@Override
						public int[] resources() {
							return new int[] { R.id.camipro_ebankinginfo_title,
									R.id.camipro_ebankinginfo_value };
						}

						@Override
						public void finalize(Map<String, Object> map,
								Entry<String, String> item) {

						}
					});

			LazyAdapter adapter = new LazyAdapter(this, p.getMap(),
					R.layout.camipro_ebankinginfo, p.getKeys(),
					p.getResources());

			ListView lv = (ListView) findViewById(R.id.camipro_ebanking_stats_list);
			lv.setAdapter(adapter);
		}
	}

	@Override
	public void emailSent(String result) {
		Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void camiproServersDown() {
		setUnrecoverableErrorOccurred(getString(R.string.camipro_error_camipro_down));
	}

	@Override
	public void networkErrorHappened() {
		setUnrecoverableErrorOccurred(getString(R.string.sdk_connection_error_happened));
	}

	@Override
	public void authenticationFailed() {
		Toast.makeText(getApplicationContext(),
				getResources().getString(R.string.sdk_authentication_failed),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void userCancelledAuthentication() {
		finish();
	}
}
