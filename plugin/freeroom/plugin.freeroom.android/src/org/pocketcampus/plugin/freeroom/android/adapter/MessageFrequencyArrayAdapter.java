package org.pocketcampus.plugin.freeroom.android.adapter;

import java.util.List;

import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.FreeRoomHomeView;
import org.pocketcampus.plugin.freeroom.shared.FRMessageFrequency;

import android.R.color;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Adapter for message frequency, to display them correctly. Easy to add new
 * button on each line.
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 * 
 * @param <T>
 *            used by FRRequestDetails.
 */
public class MessageFrequencyArrayAdapter<T> extends
		ArrayAdapter<FRMessageFrequency> {
	private FreeRoomHomeView caller;

	public MessageFrequencyArrayAdapter(FreeRoomHomeView caller,
			Context context, int row, int label, List<FRMessageFrequency> items) {
		super(context, row, label, items);
		this.caller = caller;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		View row = super.getView(position, convertView, parent);

		// play click listener
		TextView messageTextView = (TextView) row
				.findViewById(R.id.freeroom_layout_message_text);
		TextView frequencyTextView = (TextView) row
				.findViewById(R.id.freeroom_layout_message_number);
		FRMessageFrequency req = super.getItem(position);
		String message = req.getMessage();
		// no message: sharing without message -> display grey "undefined"
		if (message != null && message.equals("")) {
			messageTextView.setText(caller
					.getString(R.string.freeroom_whoIsWorking_unknown));
			messageTextView.setTextColor(Color.RED);
		} else {
			messageTextView.setText(req.getMessage());
			messageTextView.setTextColor(Color.BLACK);
		}
		frequencyTextView.setText("x " + req.getCount());
		return (row);
	}
}
