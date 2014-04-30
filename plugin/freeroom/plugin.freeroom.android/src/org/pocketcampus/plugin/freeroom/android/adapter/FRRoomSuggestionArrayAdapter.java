package org.pocketcampus.plugin.freeroom.android.adapter;

import java.util.List;

import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * <code>FRRoomSuggestionArrayAdapter</code> is a simple
 * <code>ArrayAdapter</code>, which only redefines getView in order to change
 * the display without changing the data.
 * 
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 * 
 * @param <T>
 *            The type of data is the lists.
 */

public class FRRoomSuggestionArrayAdapter<T> extends ArrayAdapter<FRRoom> {
	private Context context;
	private List<FRRoom> data;
	// hold the caller view for colors updates.
	private FreeRoomModel mModel;

	public FRRoomSuggestionArrayAdapter(Context c, List<FRRoom> data,
			FreeRoomModel model) {
		super(c, R.layout.sdk_list_entry, R.id.sdk_list_entry_text, data);
		this.context = c;
		this.data = data;
		this.mModel = model;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		ViewHolder vholder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.freeroom_layout_room_empty, null);
			vholder = new ViewHolder();
			vholder.setTextView((TextView) convertView
					.findViewById(R.id.freeroom_layout_empty_text));
			convertView.setTag(vholder);
		} else {
			vholder = (ViewHolder) convertView.getTag();
		}

		final FRRoom mFrRoom = data.get(index);
		TextView tv = vholder.getTextView();
		tv.setText(mFrRoom.getDoorCode());

		return convertView;
	}

	/**
	 * Class used to keep a view, it saves ressources by avoiding multiple
	 * inflate and findViewById operations.
	 * 
	 */
	private class ViewHolder {
		private TextView tv = null;

		public void setTextView(TextView tv) {
			this.tv = tv;
		}

		public TextView getTextView() {
			return this.tv;
		}
	}

}
