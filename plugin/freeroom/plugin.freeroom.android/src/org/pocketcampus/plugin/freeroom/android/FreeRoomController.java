package org.pocketcampus.plugin.freeroom.android;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomController;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.android.req.BuildingAutoCompleteRequest;
import org.pocketcampus.plugin.freeroom.android.req.CheckOccupancyRequest;
import org.pocketcampus.plugin.freeroom.android.req.CheckWhoIsWorkingRequest;
import org.pocketcampus.plugin.freeroom.android.req.FRRequestASyncTask;
import org.pocketcampus.plugin.freeroom.android.req.GetFreeRoomRequest;
import org.pocketcampus.plugin.freeroom.android.req.SubmitImWorkingRequest;
import org.pocketcampus.plugin.freeroom.shared.AutoCompleteReply;
import org.pocketcampus.plugin.freeroom.shared.AutoCompleteRequest;
import org.pocketcampus.plugin.freeroom.shared.FRReply;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomReply;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomRequest;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomService.Client;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomService.Iface;
import org.pocketcampus.plugin.freeroom.shared.ImWorkingReply;
import org.pocketcampus.plugin.freeroom.shared.ImWorkingRequest;
import org.pocketcampus.plugin.freeroom.shared.Occupancy;
import org.pocketcampus.plugin.freeroom.shared.OccupancyReply;
import org.pocketcampus.plugin.freeroom.shared.OccupancyRequest;
import org.pocketcampus.plugin.freeroom.shared.WhoIsWorkingReply;
import org.pocketcampus.plugin.freeroom.shared.WhoIsWorkingRequest;
import org.pocketcampus.plugin.freeroom.shared.WorkingOccupancy;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * FreeRoomController - Main logic for the FreeRoom Plugin.
 * 
 * This class issues requests to the FreeRoom PocketCampus server to get the
 * FreeRoom data of the logged in user.
 * 
 * @author FreeRoom Project Team - Julien WEBER <julien.weber@epfl.ch> and
 *         Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */
public class FreeRoomController extends PluginController implements
		IFreeRoomController {

	/**
	 * This name must match given in the Server.java file in
	 * plugin.launcher.server. It's used to route the request to the right
	 * server implementation.
	 */
	private String mPluginName = "freeroom";

	/**
	 * Stores reference to the Model associated with this plugin.
	 */
	private FreeRoomModel mModel;

	/**
	 * HTTP Clients used to communicate with the PocketCampus server. Use thrift
	 * to transport the data.
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

	private FreeRoomRequest freeRoomRequest = null;

	public void prepareSearchFreeRoom(FreeRoomRequest request) {
		this.freeRoomRequest = request;
	}

	public void searchFreeRoom(IFreeRoomView view) {
		if (freeRoomRequest != null) {
			new GetFreeRoomRequest(view).start(this, mClient,
					this.freeRoomRequest);
			freeRoomRequest = null;
		} else {
			Log.e(this.getClass().toString(),
					"request not defined in controller!");
		}
	}

	public void autoCompleteBuilding(IFreeRoomView view,
			AutoCompleteRequest request) {
		new BuildingAutoCompleteRequest(view).start(this, mClient, request);
	}

	private OccupancyRequest occupancyRequest;

	public void prepareCheckOccupancy(OccupancyRequest request) {
		occupancyRequest = request;
	}

	public void checkOccupancy(IFreeRoomView view) {
		if (occupancyRequest != null) {
			new CheckOccupancyRequest(view).start(this, mClient,
					occupancyRequest);
			occupancyRequest = null;
		} else {
			Log.e(this.getClass().toString(),
					"request not defined in controller!");
		}
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

		// ONLY for testing new interface with old data!
		Map<String, List<Occupancy>> map = new HashMap<String, List<Occupancy>>();
		System.out.println(result.getOccupancyOfRooms().size()
				+ " size of rooms av.");
		map.put("BC", result.getOccupancyOfRooms());
		mModel.setOccupancyResults(map);

		List<Occupancy> list = result.getOccupancyOfRooms();

		// this check the uniqueness of the rooms in the reply
		LinkedHashSet<FRRoom> mLinkedHashSet = null;
		boolean allUniqueFlag = true;
		if (list != null) {
			mLinkedHashSet = new LinkedHashSet<FRRoom>(list.size());
			Iterator<Occupancy> iter = list.iterator();
			while (iter.hasNext() && allUniqueFlag) {
				FRRoom mFrRoom = iter.next().getRoom();
				if (mLinkedHashSet.contains(mFrRoom)) {
					allUniqueFlag = false;
				} else {
					mLinkedHashSet.add(mFrRoom);
				}
			}
		} else {
			allUniqueFlag = false;
		}

		if (allUniqueFlag) {
			mModel.setOccupancyResultsListOccupancy(list);
			mModel.setOccupancyResultsLinkedHashSetFRRoom(mLinkedHashSet);
		} else {
			Log.e(this.getClass().toString(),
					"Warning: the response from the server contains duplicates!");
		}
	}

	public void handleReplySuccess(IFreeRoomView caller, int status,
			String statusComment, String callingClass, String requestClass) {
		Log.v(callingClass, "Server replied successfully to a " + requestClass
				+ "!");
	}

	public void handleReplyError(IFreeRoomView caller, int status,
			String statusComment, String callingClass) {
		Log.e(callingClass, "the server response was not successful. Message: "
				+ statusComment);
		if (status == 400) {
			Log.e(callingClass,
					"server complains about a bad request from the client");
			caller.freeRoomServerBadRequest();
			Toast.makeText(
					getApplicationContext(),
					"server complains about a bad request from the client"
							+ statusComment, Toast.LENGTH_LONG).show();
		} else if (status == 500) {
			Log.e(callingClass, "server had an internal error");
			caller.freeRoomServersInternalError();
		} else {
			Log.e(callingClass, "server sent another UNKNOWN status" + status);
			caller.freeRoomServersUnknownError();
		}
	}

	private ImWorkingRequest imWorkingRequest;

	public void prepareImWorking(ImWorkingRequest request) {
		imWorkingRequest = request;
	}

	public void ImWorking(IFreeRoomView view) {
		if (imWorkingRequest != null) {
			new SubmitImWorkingRequest(view).start(this, mClient,
					imWorkingRequest);
			mModel.addImWorkingRequest(imWorkingRequest);
			imWorkingRequest = null;
		} else {
			Log.e(this.getClass().toString(),
					"request not defined in controller!");
		}
	}

	public void validateImWorking(ImWorkingReply result) {
		// we do nothing here... we could display a confirmation
		// the user MUST be blocked to send others "ImWorking" indication for
		// the same period!
		Toast.makeText(getApplicationContext(), "succcessfully submitted",
				Toast.LENGTH_LONG).show();
	}

	public void cannotValidateImWorking() {
		// TODO if failure, we should re-enable the buttons, for the user to
		// submit something else!
		Toast.makeText(getApplicationContext(), "an error occured, sorry!",
				Toast.LENGTH_LONG).show();
	}

	/**
	 * Stores the prepared request for future sending to the server.
	 */
	private WhoIsWorkingRequest whoIsWorkingRequest = null;

	/**
	 * Stores a <code>WhoIsWorkingRequest</code> for future use.
	 * 
	 * This is should be used before changing activity, and you can call the
	 * corresponding method (same name without "prepare").
	 * 
	 * @param request
	 *            the <code>WhoIsWorkingRequest</code> to store.
	 */
	public void prepareCheckWhoIsWorking(WhoIsWorkingRequest request) {
		this.whoIsWorkingRequest = request;
	}

	/**
	 * Sends the ALREADY prepared <code>WhoIsWorkingRequest</code> to the
	 * server.
	 * 
	 * @param view
	 *            the holder <code>IFreeRoomView</code> calling.
	 */
	public void checkWhoIsWorking(IFreeRoomView view) {
		if (whoIsWorkingRequest != null) {
			new CheckWhoIsWorkingRequest(view).start(this, mClient,
					whoIsWorkingRequest);
			whoIsWorkingRequest = null;
		} else {
			Log.e(this.getClass().toString(),
					"request not defined in controller!");
		}
	}

	/**
	 * Sets the <code>WhoIsWorkingReply</code> results received from the server
	 * in the model.
	 * 
	 * @param result
	 *            the <code>WhoIsWorkingReply</code> from the server.
	 */
	public void setWhoIsWorkingReply(WhoIsWorkingReply result) {
		List<WorkingOccupancy> listWorkingOccupancies = result
				.getTheyAreWorking();
		mModel.setListWorkingOccupancies(listWorkingOccupancies);
	}

	// NEW INTERFACE as of 2104.04.04.
	/**
	 * Sets the FRReply results received from the server in the model.
	 * 
	 * @param result
	 *            FRReply results received from the server
	 */
	public void setOccupancyResults(FRReply result) {
		mModel.setOccupancyResults(result.getOccupancyOfRooms());
	}

	/**
	 * Sends the occupancy request stored in the model to the server.
	 * 
	 * @param view
	 *            the caller view
	 */
	public void sendFRRequest(IFreeRoomView view) {
		new FRRequestASyncTask(view)
				.start(this, mClient, mModel.getFRRequest());
	}
}