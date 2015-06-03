package org.pocketcampus.plugin.cloudprint.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.cloudprint.android.CloudPrintController;
import org.pocketcampus.plugin.cloudprint.android.iface.ICloudPrintView;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintService.Iface;
import org.pocketcampus.plugin.cloudprint.shared.CloudPrintStatusCode;
import org.pocketcampus.plugin.cloudprint.shared.PrintDocumentRequest;
import org.pocketcampus.plugin.cloudprint.shared.PrintDocumentResponse;

/**
 * PrintFileRequest
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class PrintFileRequest extends Request<CloudPrintController, Iface, PrintDocumentRequest, PrintDocumentResponse> {

	private ICloudPrintView caller;
	
	public PrintFileRequest(ICloudPrintView caller) {
		this.caller = caller;
	}
	
	@Override
	protected PrintDocumentResponse runInBackground(Iface client, PrintDocumentRequest param) throws Exception {
		return client.printDocument(param);
	}

	@Override
	protected void onResult(CloudPrintController controller, PrintDocumentResponse result) {
		if(result.getStatusCode() == CloudPrintStatusCode.OK) {
			caller.printedSuccessfully();
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
