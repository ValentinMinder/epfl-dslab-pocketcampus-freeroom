package org.pocketcampus.plugin.map.android.ui;

import org.pocketcampus.R;
import org.pocketcampus.plugin.map.android.MapMainView;
import org.pocketcampus.plugin.map.android.elements.MapElement;
import org.pocketcampus.plugin.map.android.utils.GeoPointConverter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

/**
 * Show a dialog box when the user clicks on an item on the map
 * 
 * @status WIP
 * 
 * @author Jonas <jonas.schmid@epfl.ch>
 * @author Florian <florian.laurent@epfl.ch>
 *
 */
public class ItemDialog {
	
	private MapMainView mp_;
	private MapElement item_;

	public ItemDialog(final MapMainView mp, final MapElement item) {
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

		// The item has a plugin linked to it
		final String pluginId = item_.getPluginId();
		if(pluginId != null && !"".equals(pluginId)) {
			builder.setNegativeButton(mp_.getResources().getString(R.string.map_open_plugin_button), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					if(pluginId.startsWith(MapMainView.ITEM_GO_URL)) {
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(pluginId.substring(MapMainView.ITEM_GO_URL.length())));
						mp_.startActivity(intent);
					} else {
//						Core.startPluginWithID(mp_, pluginId, item_.getItemId());
					}
					dialog.dismiss();	
				}
			});
		}


		AlertDialog alert = builder.create();
		alert.setCanceledOnTouchOutside(true);

		alert.show();
	}

}
