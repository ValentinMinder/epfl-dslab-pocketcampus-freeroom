package org.pocketcampus.plugin.freeroom.android.adapter;

import java.util.List;

import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel;
import org.pocketcampus.plugin.freeroom.android.utils.FRUtilsClient;
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
	private int ressourceText;
	private boolean favorites;

	public FRRoomSuggestionArrayAdapter(Context c, int ressource,
			int ressourceText, List<FRRoom> data, FreeRoomModel model, boolean favorites) {
		super(c, ressource, ressourceText, data);
		this.context = c;
		this.data = data;
		this.mModel = model;
		this.ressourceText = ressourceText;
		this.favorites = favorites;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		View row = super.getView(index, convertView, parent);
		TextView tv = (TextView) row.findViewById(ressourceText);
		FRRoom room = data.get(index);
		tv.setText(FRUtilsClient.formatFullRoom(room));
		
		if (favorites) {
			if (mModel.isFavorite(room)) {
				tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.freeroom_ic_action_favorite_enabled, 0, 0, 0);
			} else {
				tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.freeroom_ic_action_favorite_disabled, 0, 0, 0);
			}
		}
		return row;
	}
}