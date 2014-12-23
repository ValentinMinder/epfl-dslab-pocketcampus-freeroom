package org.pocketcampus.plugin.freeroom.android.adapter;

import java.util.List;

import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.FreeRoomHomeView;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adapter for FRRoom display, to remove them from selection.
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 * 
 * @param <T>
 *            used by FRRoom / editRoomDialog.
 */
public class FRRoomRemoveArrayAdapter<T> extends ArrayAdapter<FRRoom> {
	private FreeRoomHomeView caller;

	public FRRoomRemoveArrayAdapter(FreeRoomHomeView caller, Context context,
			int row, int label, List<FRRoom> items) {
		super(context, row, label, items);
		this.caller = caller;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		View row = super.getView(position, convertView, parent);

		// remove click listener
		ImageView removeRoom = (ImageView) row
				.findViewById(R.id.freeroom_layout_selected_remove);

		// there is already a clicklistener for the whole line, and removing
		// from here cause an UI issue.
		// removeRoom.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// caller.onRemoveRoomClickListener(position);
		// }
		// });

		// play click listener
		TextView roomName = (TextView) row
				.findViewById(R.id.freeroom_layout_selected_text);
		FRRoom room = super.getItem(position);
		if (room.isSetDoorCodeAlias()) {
			roomName.setText(room.getDoorCodeAlias());
		} else {
			roomName.setText(room.getDoorCode());
		}
		return (row);
	}
}
