package org.pocketcampus.plugin.freeroom.android.adapter;

import java.util.List;

import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.FreeRoomHomeView;
import org.pocketcampus.plugin.freeroom.android.utils.FRRequestDetails;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adpater for previous request display, with play, edit and remove options.
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 * 
 * @param <T>
 *            used by FRRequestDetails.
 */
public class PreviousRequestArrayAdapter<T> extends
		ArrayAdapter<FRRequestDetails> {
	private FreeRoomHomeView caller;

	public PreviousRequestArrayAdapter(FreeRoomHomeView caller,
			Context context, int row, int label, List<FRRequestDetails> items) {
		super(context, row, label, items);
		this.caller = caller;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		View row = super.getView(position, convertView, parent);

		// remove click listener
		ImageView removeRequest = (ImageView) row
				.findViewById(R.id.freeroom_layout_prev_req_remove);

		removeRequest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				caller.searchPreviousRequestRemoveClickListener(position);
			}
		});

		// edit click listener
		ImageView editRequest = (ImageView) row
				.findViewById(R.id.freeroom_layout_prev_req_edit);

		editRequest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				caller.searchPreviousRequestReFillClickListeners(position);
			}
		});

		// play click listener
		TextView requestTextView = (TextView) row
				.findViewById(R.id.freeroom_layout_prev_req_text);
		FRRequestDetails req = super.getItem(position);
		requestTextView.setText(caller.searchPreviousFRRequestToString(req));
		requestTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				caller.searchPreviousRequestRePlayClickListener(position);
			}
		});
		return (row);
	}
}
