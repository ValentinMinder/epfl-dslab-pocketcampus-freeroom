package org.pocketcampus.plugin.moodle.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.moodle.android.MoodleController;
import org.pocketcampus.plugin.moodle.android.iface.IMoodleView;
import org.pocketcampus.plugin.moodle.shared.MoodlePrintFileRequest2;
import org.pocketcampus.plugin.moodle.shared.MoodlePrintFileResponse2;
import org.pocketcampus.plugin.moodle.shared.MoodleService.Iface;
import org.pocketcampus.plugin.moodle.shared.MoodleStatusCode2;

import android.net.Uri;

/**
 * PrintFileRequest
 * 
 * This class sends an HttpRequest using Thrift to the PocketCampus server
 * in order to print a Moodle file
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class PrintFileRequest extends Request<MoodleController, Iface, MoodlePrintFileRequest2, MoodlePrintFileResponse2> {

	private IMoodleView caller;
	private String fileName;
	
	public PrintFileRequest(IMoodleView caller) {
		this.caller = caller;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		caller.showLoading();
	}
	
	@Override
	protected MoodlePrintFileResponse2 runInBackground(Iface client, MoodlePrintFileRequest2 param) throws Exception {
		fileName = Uri.parse(param.getFileUrl()).getLastPathSegment();
		return client.printFile(param);
	}

	@Override
	protected void onResult(MoodleController controller, MoodlePrintFileResponse2 result) {
		caller.hideLoading();
		if(result.getStatusCode() == MoodleStatusCode2.OK) {
			MoodleController.openPrintDialog(controller, result.getPrintJobId(), fileName);
			
		} else if(result.getStatusCode() == MoodleStatusCode2.AUTHENTICATION_ERROR) {
			caller.notLoggedIn();
			
		} else {
			caller.moodleServersDown();
			
		}
	}

	@Override
	protected void onError(MoodleController controller, Exception e) {
		caller.hideLoading();
		caller.networkErrorHappened();
		e.printStackTrace();
	}
	
}
