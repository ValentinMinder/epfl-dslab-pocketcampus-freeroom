package org.pocketcampus.plugin.edx.android.req;

import java.util.List;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.platform.sdk.shared.utils.Callback;
import org.pocketcampus.plugin.edx.android.EdXController;
import org.pocketcampus.plugin.edx.android.EdXController.Stopper;
import org.pocketcampus.plugin.edx.android.iface.IEdXView;
import org.pocketcampus.plugin.edx.shared.EdXService.Iface;
import org.pocketcampus.plugin.edx.shared.MsgPsgMessage;
import org.pocketcampus.plugin.edx.shared.MsgPsgMessageType;
import org.pocketcampus.plugin.edx.shared.MsgPsgReceiveBroadcastReq;
import org.pocketcampus.plugin.edx.shared.MsgPsgReceiveBroadcastResp;

/**
 * ReceiveBroadcastRequest
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class ReceiveBroadcastRequest extends Request<EdXController, Iface, MsgPsgReceiveBroadcastReq, MsgPsgReceiveBroadcastResp> {

	private IEdXView caller;
	private Stopper stopper;
	private Callback<List<MsgPsgMessage>> callback;
	
	private EdXController controller;
	private Iface iface;
	private MsgPsgReceiveBroadcastReq param;
	
	public ReceiveBroadcastRequest(IEdXView caller, Stopper stopper, Callback<List<MsgPsgMessage>> callback,
			EdXController controller, Iface iface, MsgPsgReceiveBroadcastReq param) {
		this.caller = caller;
		this.stopper = stopper;
		this.callback = callback;
		
		this.controller = controller;
		this.iface = iface;
		this.param = param;
	}
	
	public void go() {
		start(controller, iface, param);
	}
	
	@Override
	protected MsgPsgReceiveBroadcastResp runInBackground(Iface client, MsgPsgReceiveBroadcastReq param) throws Exception {
		return client.receiveBroadcast(param);
	}

	@Override
	protected void onResult(EdXController controller, MsgPsgReceiveBroadcastResp result) {
		if(result.getStatus() == 200) {

			long lastId = 0;
			for(MsgPsgMessage msg : result.getMessages()) {
				if(msg.getMessageType() == MsgPsgMessageType.MESSAGE)
					lastId = msg.getMessageRef();
			}
			if(lastId != 0)
				param.setLastMessageRef(lastId);
			
			callback.callback(result.getMessages());
			
		} else if(result.getStatus() == 500) {
			caller.serverFailure();
			
		} else { // 400?
			caller.upstreamServerFailure();
			
		}
		
		if(!stopper.isStopped())
			new ReceiveBroadcastRequest(caller, stopper, callback, controller, iface, param).go();
	}

	@Override
	protected void onError(EdXController controller, Exception e) {
		e.printStackTrace();
		caller.networkErrorHappened();
		
		if(!stopper.isStopped())
			new ReceiveBroadcastRequest(caller, stopper, callback, controller, iface, param).go();
	}
	
}
