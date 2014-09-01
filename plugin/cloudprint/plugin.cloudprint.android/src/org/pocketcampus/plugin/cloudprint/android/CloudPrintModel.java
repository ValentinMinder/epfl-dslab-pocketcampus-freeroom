package org.pocketcampus.plugin.cloudprint.android;


import org.pocketcampus.platform.android.core.IView;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.cloudprint.android.iface.ICloudPrintModel;
import org.pocketcampus.plugin.cloudprint.android.iface.ICloudPrintView;

import android.content.Context;
import android.net.Uri;

/**
 * CloudPrintModel 
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class CloudPrintModel extends PluginModel implements ICloudPrintModel {
	
	/**
	 * Reference to the Views that need to be notified when the stored data changes.
	 */
	ICloudPrintView mListeners = (ICloudPrintView) getListeners();
	

	private Uri fileToPrint;
	private Long printjobId;
	
	/**
	 * Constructor with reference to the context.
	 * 
	 * We need the context to be able to instantiate
	 * the SharedPreferences object in order to use
	 * persistent storage.
	 * 
	 * @param context is the Application Context.
	 */
	public CloudPrintModel(Context context) {
		
	}
	
	

	public void setFileToPrint(Uri file) {
		fileToPrint = file;
	}
	public Uri getFileToPrint() {
		return fileToPrint;
	}
	
	public void setPrintJobId(Long id) {
		printjobId = id;
	}
	public Long getPrintJobId() {
		return printjobId;
	}
	
	
	/**
	 * Returns the Type of the Views associated with this plugin.
	 */
	@Override
	protected Class<? extends IView> getViewInterface() {
		return ICloudPrintView.class;
	}

	/**
	 * Returns the registered listeners to by notified.
	 */
	public ICloudPrintView getListenersToNotify() {
		return mListeners;
	}
	
}
