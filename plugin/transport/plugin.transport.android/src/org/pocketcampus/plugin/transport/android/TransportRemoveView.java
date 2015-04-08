package org.pocketcampus.plugin.transport.android;

import java.util.Map;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.platform.android.ui.adapter.LazyAdapter;
import org.pocketcampus.platform.android.ui.adapter.LazyAdapter.Actuator;
import org.pocketcampus.platform.android.utils.Preparated;
import org.pocketcampus.platform.android.utils.Preparator;
import org.pocketcampus.plugin.transport.R;
import org.pocketcampus.plugin.transport.shared.TransportStation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.Html;
import android.widget.ListView;

/**
 * @author silviu@pocketcampus.org
 */
public class TransportRemoveView extends PluginView {
	private TransportModel model;

	/**
	 * Defines what the main controller is for this view.
	 */
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return TransportController.class;
	}

	/**
	 * Called when first displaying the view. Retrieves the model and the
	 * controller and calls the<code>setUpLayout</code> method.
	 */
	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		model = (TransportModel) controller.getModel();
		setContentView(R.layout.transport_remove);
		setActionBarTitle(getString(R.string.transport_remove_station));
		updateStationsToRemoveList();
	}

	@Override
	protected String screenName() {
		return "/transport/removeStation";
	}

	private void updateStationsToRemoveList() {
		Preparated<TransportStation> preparated = new Preparated<TransportStation>(
				model.getPersistedTransportStations(), new Preparator<TransportStation>() {
					@Override
					public Object content(int res, final TransportStation item) {
						switch (res) {
						case R.id.transport_edit_remove_station_name:
							return new LazyAdapter.Actuated(item.getName(), new Actuator() {
								@Override
								public void triggered() {
									trackEvent("Delete", item.getName());
									confirmationDialog(item);
								}
							});
						default:
							throw new RuntimeException("Unknown resource " + res);
						}
					}

					@Override
					public int[] resources() {
						return new int[] { R.id.transport_edit_remove_station_name };
					}

					@Override
					public void finalize(Map<String, Object> map, TransportStation item) {

					};
				});
		LazyAdapter adapter = new LazyAdapter(this, preparated.getMap(), R.layout.transport_remove_row,
				preparated.getKeys(), preparated.getResources());
		((ListView) findViewById(R.id.transport_remove_listview)).setAdapter(adapter);
	}

	/**
	 * Creates and shows a confirmation dialog for removing a station from the
	 * favorite stations.
	 */
	private void confirmationDialog(final TransportStation station) {
		final String dest = station.getName();
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle(getResources().getString(R.string.transport_confirmation));
		b.setMessage(Html.fromHtml(getResources().getString(R.string.transport_confirmation_delete_station_start)
				+ " <b>" + dest + "</b> "
				+ getResources().getString(R.string.transport_confirmation_delete_station_end)));

		b.setPositiveButton(getResources().getString(R.string.transport_yes), new OnClickListener() {

			/**
			 * When the user clicks on the "Yes" button of the dialog, the
			 * station is removed from his favorite ones and the list is
			 * updated.
			 */
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				trackEvent("DeleteDialogConfirm", null);
				model.removeTransportStationFromPersistedStorage(station);
				updateStationsToRemoveList();
				dialog.dismiss();
			}
		});

		b.setNegativeButton(getResources().getString(R.string.transport_no), new OnClickListener() {

			/**
			 * When the user clicks on the "No" button of the dialog, nothing
			 * happens and the dialog is dismissed.
			 */
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				trackEvent("DeleteDialogCancel", null);

				// Do nothing and dismiss the dialog
				dialog.dismiss();
			}
		});

		// Create and display the dialog
		AlertDialog d = b.create();
		d.show();
	}
}
