package org.pocketcampus.plugin.map.ui;

import org.osmdroid.views.overlay.OverlayItem;
import org.pocketcampus.R;
import org.pocketcampus.plugin.map.MapPlugin;
import org.pocketcampus.plugin.map.utils.GeoPointConverter;

import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * Show a dialog box when the user clicks on an item on the map
 * 
 * @status WIP
 * 
 * @author Jonas
 *
 */
public class ItemDialog {
	
	private MapPlugin mp_;
	private OverlayItem item_;
	
	public ItemDialog(final MapPlugin mp, final OverlayItem item) {
		this.mp_ = mp;
		this.item_ = item;
	}
	
	public void showDialog() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(mp_);
		builder.setTitle(item_.getTitle());
		builder.setMessage(item_.getSnippet());
		builder.setCancelable(true);
		
		builder.setPositiveButton(mp_.getResources().getString(R.string.map_zoom_button), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				mp_.centerOnPoint(item_.getPoint());
				dialog.dismiss();	
			}
		});

		builder.setNeutralButton(mp_.getResources().getString(R.string.map_take_me_there_button), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				mp_.showDirectionsFromHereToPosition(GeoPointConverter.toPosition(item_.getPoint()));				
				dialog.dismiss();	
			}
		});
		
		
		AlertDialog alert = builder.create();
		alert.setCanceledOnTouchOutside(true);
		
		alert.show();
	}
	
}
