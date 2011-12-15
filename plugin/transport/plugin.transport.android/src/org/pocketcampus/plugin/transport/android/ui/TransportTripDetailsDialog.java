package org.pocketcampus.plugin.transport.android.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.pocketcampus.R;
import org.pocketcampus.plugin.transport.android.utils.DestinationFormatter;
import org.pocketcampus.plugin.transport.shared.TransportConnection;
import org.pocketcampus.plugin.transport.shared.TransportTrip;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class TransportTripDetailsDialog extends Dialog {
	private TransportTrip connection_;
	private Context ctx_;

	public TransportTripDetailsDialog(Context ctxt, TransportTrip connection) {
		super(ctxt);
		connection_ = connection;
		ctx_ = ctxt.getApplicationContext();

		// Setups dialog.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.transport_details_dialog);

		setCanceledOnTouchOutside(true);
		setDialogContent();
	}

	private void setDialogContent() {
		ArrayList<HashMap<String, String>> connectionParts = new ArrayList<HashMap<String, String>>();

		SimpleDateFormat formatter = new SimpleDateFormat();
		formatter.applyPattern("HH:mm");

		HashMap<String, String> partRow;
		for (TransportConnection part : connection_.parts) {
			partRow = new HashMap<String, String>();
			String departureTime = "";
			String departurePlace = part.departure.name;
			String arrivalTime = "";
			String arrivalPlace = part.arrival.name;

			if (!part.foot) {
				// Part is a Trip
				departureTime = formatter.format(part.getDepartureTime());
				arrivalTime = formatter.format(part.getArrivalTime());
				departurePlace = part.getDeparture().getName();

			} else {
				// Part is a Footway
				departureTime = (part).min
						+ " "
						+ ctx_.getResources().getString(
								R.string.transport_walk_in_min);
			}

			partRow.put("departureTime", departureTime);
			partRow.put("departurePlace", departurePlace);

			partRow.put("arrivalTime", arrivalTime);
			partRow.put("arrivalPlace", arrivalPlace);

			connectionParts.add(partRow);
		}

		String[] keys = { "departureTime", "departurePlace", "arrivalTime",
				"arrivalPlace" };

		int[] ids = { R.id.transport_details_dialog_dep_time,
				R.id.transport_details_dialog_dep_place,
				R.id.transport_details_dialog_arr_time,
				R.id.transport_details_dialog_arr_place };

		SimpleAdapter mSchedule = new SimpleAdapter(getContext(),
				connectionParts, R.layout.transport_details_dialog_row, keys,
				ids);

		ListView list = (ListView) findViewById(R.id.transport_details_dialog_list);
		list.setAdapter(mSchedule);

		TextView title = (TextView) findViewById(R.id.transport_title_dialog);
		title.setText(DestinationFormatter.getNiceName(connection_.from) + " "
				+ ctx_.getResources().getString(R.string.transport_to) + " "
				+ DestinationFormatter.getNiceName(connection_.to));
	}
}