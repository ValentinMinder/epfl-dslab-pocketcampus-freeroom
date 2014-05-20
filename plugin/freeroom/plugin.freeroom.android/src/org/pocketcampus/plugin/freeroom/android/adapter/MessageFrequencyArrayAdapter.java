package org.pocketcampus.plugin.freeroom.android.adapter;

import java.util.List;

import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.utils.FRRequestDetails;
import org.pocketcampus.plugin.freeroom.android.views.FreeRoomHomeView;
import org.pocketcampus.plugin.freeroom.shared.MessageFrequency;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
		ArrayAdapter<MessageFrequency> {
	private FreeRoomHomeView caller;

	public MessageFrequencyArrayAdapter(FreeRoomHomeView caller,
			Context context, int row, int label, List<MessageFrequency> items) {
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
		MessageFrequency req = super.getItem(position);
		messageTextView.setText(req.getMessage());
		frequencyTextView.setText(req.getFrequency() + " x");
		return (row);
	}
}
