package org.pocketcampus.plugin.scanner;

import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.communication.RequestHandler;
import org.pocketcampus.core.communication.RequestParameters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Uploader {

	private RequestHandler requestHandler_;
	private UploaderOnUploadDoneCallback onUploadDone_;

	public Uploader() {
		requestHandler_ = (new ScannerPlugin()).getScannerRequestHandler();
	}

	public void uploadRecord(ScannerRecord record) {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		
		String serializedRecord = gson.toJson(record.makeBean());
		System.out.println(serializedRecord);
		
		class UploadRecordRequest extends DataRequest {
			@Override
			protected void doInUiThread(String result) {
				if(result != null) {
					System.out.println("Record uploaded.");
					onUploadDone_.call(true);
					return;
					
				} else {
					System.out.println("Null!");
					onCancelled();
				}
			}
			
			@Override
			protected void onCancelled() {
				System.out.println("Cancelled!");
				onUploadDone_.call(false);
			}
		}
		
		RequestParameters params = new RequestParameters();
		params.addParameter("serializedRecord", serializedRecord);
		requestHandler_.execute(new UploadRecordRequest(), "uploadRecord", params);
	}

	public void setOnUploadDoneListener(UploaderOnUploadDoneCallback onUploadDone) {
		onUploadDone_ = onUploadDone;
	}
	
}


















