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
import org.pocketcampus.plugin.camipro.shared.CardLoadingWithEbankingInfo;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.markupartist.android.widget.Action;

/**
 * CamiproCardRechargeView - View that shows Camipro recharge with e-banking.
 * 
 * This view shows how to recharge your camipro card with e-banking. It also
 * shows the statistics of the card. And features a "send it by email" button in
 * the menu.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class CamiproCardRechargeView extends PluginView implements ICamiproView {

	CamiproController mController;
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
		
		addActionToActionBar(new EmailDetailsAction());
		
		setActionBarTitle(getString(R.string.camipro_ebanking_section_title));

		mController.refreshStatsAndLoadingInfo();
		updateDisplay();
	}

	@Override
	protected String screenName() {
		return "/camipro/refill";
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
		CardLoadingWithEbankingInfo ebanking = mModel
				.getCardLoadingWithEbankingInfo();

		if (ebanking == null)
			return;

		ArrayList<Map.Entry<String, String>> einfos = new ArrayList<Map.Entry<String, String>>();

		if (ebanking != null) {
			einfos.add(new AbstractMap.SimpleEntry<String, String>(null,
					getResources().getString(
							R.string.camipro_ebanking_infos_text)));

			einfos.add(new AbstractMap.SimpleEntry<String, String>(
					getResources().getString(
							R.string.camipro_ebanking_ref_number_title),
					ebanking.getIReferenceNumber()));
			einfos.add(new AbstractMap.SimpleEntry<String, String>(
					getResources().getString(
							R.string.camipro_ebanking_paid_to_title), ebanking
							.getIPaidTo()));
			einfos.add(new AbstractMap.SimpleEntry<String, String>(
					getResources().getString(
							R.string.camipro_ebanking_account_number_title),
					ebanking.getIAccountNumber()));
			einfos.add(new AbstractMap.SimpleEntry<String, String>(null,
					getResources().getString(
							R.string.camipro_ebanking_infos_remark)));

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

	private class EmailDetailsAction implements Action {

		@Override
		public int getDrawable() {
			return R.drawable.sdk_email;
		}

		@Override
		public void performAction(View view) {
			trackEvent("RequestEmail", null);
			mController.sendEmailWithLoadingDetails();
		}

		@Override
		public String getDescription() {
			return getString(R.string.camipro_menu_send_by_email);
		}

	}

}
