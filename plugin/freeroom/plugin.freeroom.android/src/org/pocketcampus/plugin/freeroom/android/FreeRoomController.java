package org.pocketcampus.plugin.freeroom.android;

import java.util.List;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomController;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.android.req.BuildingAutoCompleteRequest;
import org.pocketcampus.plugin.freeroom.android.req.CheckOccupancyRequest;
import org.pocketcampus.plugin.freeroom.android.req.GetFreeRoomRequest;
import org.pocketcampus.plugin.freeroom.shared.AutoCompleteReply;
import org.pocketcampus.plugin.freeroom.shared.AutoCompleteRequest;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomReply;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomRequest;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomService.Client;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomService.Iface;
import org.pocketcampus.plugin.freeroom.shared.Occupancy;
import org.pocketcampus.plugin.freeroom.shared.OccupancyReply;
import org.pocketcampus.plugin.freeroom.shared.OccupancyRequest;

import android.util.Log;


/**
 * FreeRoomController - Main logic for the FreeRoom Plugin.
 * 
 * This class issues requests to the FreeRoom PocketCampus
 * server to get the FreeRoom data of the logged in user.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class FreeRoomController extends PluginController implements IFreeRoomController{

	/**
	 *  This name must match given in the Server.java file in plugin.launcher.server.
	 *  It's used to route the request to the right server implementation.
	 */
	private String mPluginName = "freeroom";
	

	/**
	 * Stores reference to the Model associated with this plugin.
	 */
	private FreeRoomModel mModel;
	
	/**
	 * HTTP Clients used to communicate with the PocketCampus server.
	 * Use thrift to transport the data.
	 */
	private Iface mClient;

	@Override
	public void onCreate() {
		mModel = new FreeRoomModel(getApplicationContext());
		mClient = (Iface) getClient(new Client.Factory(), mPluginName);

	}
	
	@Override
	public PluginModel getModel() {
		return mModel;
	}
	
	public void searchFreeRoom(IFreeRoomView view, FreeRoomRequest request) {
		new GetFreeRoomRequest(view).start(this, mClient, request);
	}
	
	public void autoCompleteBuilding(IFreeRoomView view, AutoCompleteRequest request) {
		new BuildingAutoCompleteRequest(view).start(this, mClient, request);
	}
	
	public void checkOccupancy(IFreeRoomView view, OccupancyRequest request) {
		new CheckOccupancyRequest(view).start(this, mClient, request);
	}
	
	/**
	 * Sets the result in the model.
	 */
	public void setFreeRoomResults(FreeRoomReply rep) {
		mModel.setFreeRoomResults(rep.getRooms());
	}

	public void setAutoCompleteResults(AutoCompleteReply result) {
		mModel.setAutoCompleteResults(result.getListFRRoom());
	}
	
	public void setCheckOccupancyResults(OccupancyReply result) {
		List<Occupancy> list = result.getOccupancyOfRooms();
		Log.v("fr.controller","size in controller = " + list.get(0).getOccupancySize());
		mModel.setOccupancyResults(list); 
	}
	
}