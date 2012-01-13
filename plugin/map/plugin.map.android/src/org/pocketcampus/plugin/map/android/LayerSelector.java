package org.pocketcampus.plugin.map.android;

import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.plugin.map.android.elements.MapElementsList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;

/**
 * Shows a dialog to select the available layers  
 * 
 * @status to check
 * 
 * @author Florian <florian.laurent@epfl.ch>
 * @author Jonas <jonas.schmid@epfl.ch>
 *
 */
public class LayerSelector {
	
	private Context context_;
	private ArrayList<MapElementsList> selectedLayers_;
	private List<MapElementsList> layers_;
	
	/**
	 * Create the layer selector
	 * @param context Context
	 * @param layers All the available layers
	 * @param selected Layers already selected
	 */
	public LayerSelector(Context context, List<MapElementsList> layers, List<MapElementsList> selected) {
		this.context_ = context;
		this.layers_ = new ArrayList<MapElementsList>(layers);
		this.selectedLayers_ = new ArrayList<MapElementsList>(selected);
	}
	
	/**
	 * Show the dialog.
	 * It uses a callback to tell the caller that the dialog finished.
	 * This method returns instantly.
	 * 
	 * @param listener Callback called when the dialog is dismissed
	 */
	public void selectLayers(final DialogInterface.OnClickListener listener) {
		int i = 0;
		
		// Creation of array that will show on the dialog
		final CharSequence[] items = new CharSequence[layers_.size()];
		final boolean[] checked = new boolean[layers_.size()];
		for(MapElementsList e : layers_) {
			//if(!e.isDisplayable())
			//	continue;
			
			items[i] = e.getLayerTitle();
			checked[i] = selectedLayers_.contains(e);
			++i;
		}

		// Creation of the dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(context_);
		builder.setTitle(R.string.map_layer_pick_text)
		// Callback when an item is clicked
		.setMultiChoiceItems(items, checked, new OnMultiChoiceClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				if(isChecked) {
					selectedLayers_.add(layers_.get(which));
				} else {
					selectedLayers_.remove(layers_.get(which));
				}
			}
		})
		// Callback when the OK button is clicked
		.setPositiveButton(context_.getResources().getString(R.string.map_layer_ok_button), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {				
				dialog.dismiss();
				listener.onClick(dialog, id);
			}
		});
		
		// Show the dialog
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * Get the selected layers. To be called after the callback registered by selectLayers
	 * @return
	 */
	public ArrayList<MapElementsList> getSelectedLayers() {
		return selectedLayers_;
	}
}
