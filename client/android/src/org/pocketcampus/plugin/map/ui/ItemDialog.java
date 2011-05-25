package org.pocketcampus.plugin.map.ui;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.Core;
import org.pocketcampus.plugin.map.MapPlugin;
import org.pocketcampus.plugin.map.elements.MapElement;
import org.pocketcampus.plugin.map.utils.GeoPointConverter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

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
	private MapElement item_;

	public ItemDialog(final MapPlugin mp, final MapElement item) {
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
		Log.d("FoodPlugin", "Plugin ID : " + pluginId);

		if(pluginId != null && !"".equals(pluginId)) {
			builder.setNegativeButton(mp_.getResources().getString(R.string.map_open_plugin_button), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					if(pluginId.startsWith(MapPlugin.ITEM_GO_URL)) {
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(pluginId.substring(MapPlugin.ITEM_GO_URL.length())));
						mp_.startActivity(intent);
					} else {
						if(pluginId.equals("org.pocketcampus.plugin.food.FoodPlugin")){
							if(isRestaurant(item_.getItemId())){
								Core.startPluginWithID(mp_, pluginId, item_.getItemId());
							}else{
								Toast.makeText(mp_, mp_.getResources().getString(R.string.food_no_additional_information), Toast.LENGTH_LONG).show();
							}
						}else{
							Core.startPluginWithID(mp_, pluginId, item_.getItemId());
						}
					}
					dialog.dismiss();	
				}
			});
		}


		AlertDialog alert = builder.create();
		alert.setCanceledOnTouchOutside(true);

		alert.show();
	}
	
	private boolean isRestaurant(int id){
		boolean isRestaurant = false;
		
		switch (id){
		case 39321 :
		case 39322 :
		case 39323 :
		case 39324 :
		case 39326 :
		case 39327 :
		case 39330 :
		case 39333 :
		case 39335 :
		case 39337 :
		case 39338 :
			isRestaurant = true;
			break;
		default :
			isRestaurant = false;
			break;
		}
		
		return isRestaurant;
	}

}
