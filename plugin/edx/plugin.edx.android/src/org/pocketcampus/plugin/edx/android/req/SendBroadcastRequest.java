package org.pocketcampus.plugin.edx.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.edx.android.EdXController;
import org.pocketcampus.plugin.edx.android.EdXModel;
import org.pocketcampus.plugin.edx.android.iface.IEdXView;
import org.pocketcampus.plugin.edx.shared.EdXService.Iface;
import org.pocketcampus.plugin.edx.shared.EdxReq;
import org.pocketcampus.plugin.edx.shared.EdxResp;
import org.pocketcampus.plugin.edx.shared.MsgPsgSendBroadcastReq;
import org.pocketcampus.plugin.edx.shared.MsgPsgSendBroadcastResp;

/**
 * SendBroadcastRequest
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class SendBroadcastRequest extends Request<EdXController, Iface, MsgPsgSendBroadcastReq, MsgPsgSendBroadcastResp> {

	private IEdXView caller;
	
	public SendBroadcastRequest(IEdXView caller) {
		this.caller = caller;
	}
	
	@Override
	protected MsgPsgSendBroadcastResp runInBackground(Iface client, MsgPsgSendBroadcastReq param) throws Exception {
		return client.sendBroadcast(param);
	}

	@Override
	protected void onResult(EdXController controller, MsgPsgSendBroadcastResp result) {
		if(result.getStatus() == 200) {

			// TODO 
			
		} else if(result.getStatus() == 500) {
			caller.serverFailure();
			
		} else { // 400?
			caller.upstreamServerFailure();
			
		}
	}

	@Override
	protected void onError(EdXController controller, Exception e) {
		e.printStackTrace();
		caller.networkErrorHappened();
	}
	
}
