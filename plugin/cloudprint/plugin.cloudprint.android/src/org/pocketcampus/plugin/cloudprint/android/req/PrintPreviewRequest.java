package org.pocketcampus.plugin.cloudprint.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.cloudprint.android.CloudPrintController;
import org.pocketcampus.plugin.cloudprint.android.iface.ICloudPrintView;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintService.Iface;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintStatusCode;
import org.pocketcampus.plugin.cloudprint.shared.PrintDocumentRequest;
import org.pocketcampus.plugin.cloudprint.shared.PrintPreviewDocumentResponse;

/**
 * PrintPreviewRequest
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class PrintPreviewRequest extends Request<CloudPrintController, Iface, PrintDocumentRequest, PrintPreviewDocumentResponse> {

	private ICloudPrintView caller;
	
	public PrintPreviewRequest(ICloudPrintView caller) {
		this.caller = caller;
	}
	
	@Override
	protected PrintPreviewDocumentResponse runInBackground(Iface client, PrintDocumentRequest param) throws Exception {
		return client.printPreview(param);
	}

	@Override
	protected void onResult(CloudPrintController controller, PrintPreviewDocumentResponse result) {
		if(result.getStatusCode() == CloudPrintStatusCode.OK) {
			caller.printPreviewReady(result.getNumberOfPages());
		} else if(result.getStatusCode() == CloudPrintStatusCode.AUTHENTICATION_ERROR) {
			caller.notLoggedIn();
		} else {
			caller.printServerError();
		}
	}

	@Override
	protected void onError(CloudPrintController controller, Exception e) {
		caller.networkErrorHappened();
		e.printStackTrace();
	}
	
}
