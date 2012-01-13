package org.pocketcampus.plugin.map.android.cache;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.plugin.map.android.elements.MapElementsList;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Class used to remember the selected layers, to display them on a future launch  
 * 
 * @status Complete
 * 
 * @author Florian <florian.laurent@epfl.ch>
 * @author Jonas <jonas.schmid@epfl.ch>
 *
 */
public class LayersCache {
	
	private final static String CACHE_FILENAME = "layerscache.dat";
	private Context context_;
	
	/**
	 * Give the context to be able to read/write files
	 * @param ctx
	 */
	public LayersCache(Context ctx) {
		this.context_ = ctx;
	}
	
	/**
	 * Save the current selected layers.
	 * Use only their Layer ID and not the complete object.
	 * Save done asynchronously.
	 * @param layers List of selected layers
	 */
	public void saveSelectedLayersToFile(final List<MapElementsList> layers) {

		// Asynchronous tasks
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				
				Log.d(this.getClass().toString(), "Saving layers to file");
				
				// Save only the IDs of the layers
				List<Integer> layersStrings = new ArrayList<Integer>();
				for(MapElementsList mel : layers) {
					layersStrings.add((int) mel.getLayerId());
				}

				// Try to save into the file
				FileOutputStream fos;
				try {
					fos = context_.openFileOutput(CACHE_FILENAME, Context.MODE_PRIVATE);
					ObjectOutputStream out = new ObjectOutputStream(fos);
					out.writeObject(layersStrings);
					fos.close();

					Log.d(this.getClass().toString(), "Saved layers to file");
					
					return null;
				}
				// In case of error
				catch (FileNotFoundException e) {}
				catch (IOException e) {}

				Log.d(this.getClass().toString(), "Could not save layers to file");
				
				return null;
			}
			
		}.execute(new Void[]{});
	}
	
	/**
	 * Load the selected layers from the preferences file.
	 * One have to give a list of available layers because the preference only store the layers IDs,
	 * but not the actual layer. 
	 * A callback is called when the load is done (asynchronously)
	 * @param allLayers List of available layers
	 * @param callback Called when the preferences have been fetched
	 */
	public void loadSelectedLayersFromFile(final List<MapElementsList> allLayers, final ILayersCacheCallback callback) {

		// Asynchronous tasks
		new AsyncTask<Void, Void, Boolean>() {
			
			// Actual selected layers
			List<MapElementsList> selected = new ArrayList<MapElementsList>();

			@SuppressWarnings("unchecked")
			@Override
			protected Boolean doInBackground(Void... params) {
				
				List<String> layersStrings;
				
				// Try the read the file
				try {
					FileInputStream fis = context_.openFileInput(CACHE_FILENAME);
					ObjectInputStream in = new ObjectInputStream(fis);

					Object o = in.readObject();

					// Can deserialize
					if(o instanceof List<?>) {
						layersStrings = (List<String>) o;
						
						// Get the actual layers from their IDs
						for(MapElementsList mel : allLayers) {
							if(layersStrings.contains(mel.getLayerId())) {
								selected.add(mel);
							}
						}

						Log.d(this.getClass().toString(), "Got layers from file");

						// Everything was perfect
						return true;
					}
				}
				// Any error, return false
				catch (FileNotFoundException e) {}
				catch (StreamCorruptedException e) {}
				catch (IOException e) {}
				catch (ClassNotFoundException e) {}

				Log.d(this.getClass().toString(), "Could not get layers from file");

				return false;
			}

			/**
			 * Call the callback
			 * The list is empty if there was a problem.
			 */
			@Override
			protected void onPostExecute(Boolean result) {
				callback.onLayersLoadedFromFile(selected);
			}
			
		}.execute(new Void[]{});
	}
}
