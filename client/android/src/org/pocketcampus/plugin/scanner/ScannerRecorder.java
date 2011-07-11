package org.pocketcampus.plugin.scanner;

import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.plugin.logging.Tracker;
import org.pocketcampus.plugin.mainscreen.MainscreenPlugin;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class ScannerRecorder extends Activity {
	private Context ctx_;
	private ActionBar actionBar_;
	private int progressCount_ = 0;
	private ImageButton scanButton_;
	private TextView statusText_;
	private TextView statusDetailText_;
	
	private GpsScanner gpsScanner_;
	private GsmScanner gsmScanner_;
	private WifiScanner wifiScanner_;
	private Uploader uploader_;
	private ScannerRecord record_;
	private boolean scanInProgress_;
	private int pointId_;
	private RecordBuffer recordBuffer_;
	
	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.scanner_recorder);
		ctx_ = this;
		
		uploader_ = new Uploader();
		scanInProgress_ = false;
		recordBuffer_ = RecordBuffer.getInstance();
		
		// UI elements
		actionBar_ = (ActionBar) findViewById(R.id.actionbar);
		scanButton_ = (ImageButton) findViewById(R.id.scanner_scan);
		statusText_ = (TextView) findViewById(R.id.scanner_status);
		statusDetailText_ = (TextView) findViewById(R.id.scanner_status_detail);
		
		// sets everything up
		setupActionBar();
		setupTracker();
		setupListeners();
		
		// extracts point id
		Bundle extras = getIntent().getExtras();
		if(extras!=null && extras.containsKey("id")) {
			pointId_ = extras.getInt("id");
		} else {
			System.out.println("No id!!");
		}
		
		setStatus("Ready to scan.");
	}

	private void setupTracker() {
		Tracker.getInstance().trackPageView("labs/home");
	}

	private void setupListeners() {
		scanButton_.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(!scanInProgress_) {
					scanInProgress_ = true;
					scanButton_.setEnabled(false);
					performScan();
				}
			}
		});
	}

	private void setupScanners() {
		wifiScanner_ = new WifiScanner(ctx_);
		gsmScanner_ = new GsmScanner(ctx_);
		gpsScanner_ = new GpsScanner(ctx_);
		record_ = new ScannerRecord(pointId_);
	}

	private void performScan() {
		incrementProgressCounter();
		
		setupScanners();
		
		performGpsScan();
		performWifiScan();
		performGsmScan();
	}
	
	private void performGpsScan() {
		GpsScannerLocationChangedCallback onFix = new GpsScannerLocationChangedCallback() {

			@Override
			public void call(Location location) {
				updateStatusDetails();
			}
			
		};
		
		GpsScannerScanFinishedCallback onScanFinished = new GpsScannerScanFinishedCallback() {
			@Override
			public void call(ArrayList<Location> fixes) {
				record_.setGpsData(fixes);
				uploadRecord();
			}
		};
		
		gpsScanner_.setOnFixListener(onFix);
		gpsScanner_.setOnScanFinishListener(onScanFinished);
		gpsScanner_.startScanning();
	}
	
	private void uploadRecord() {
		setStatus("Uploading...");
		
		UploaderOnUploadDoneCallback onUploadDone = new UploaderOnUploadDoneCallback() {
			@Override
			public void call(boolean success) {
				if(success) {
					setStatus("Uploaded.");
					uploadBufferedRecords();
					
				} else {
					setStatus("Upload impossible for now.");
					saveRecordInBuffer();
				}
			}

		};
		
		uploader_.setOnUploadDoneListener(onUploadDone);
		
		uploader_.uploadRecord(record_);
		
		scanComplete();
	}
	
	private void uploadBufferedRecords() {
		if(recordBuffer_.getNbBuffered() > 0) {
			setStatus("Uploading previous records, "+recordBuffer_.getNbBuffered()+" to go...");
			uploader_.uploadRecord(recordBuffer_.getOldestBuffered());
			
		} else {
			scanInProgress_ = false;
			scanButton_.setEnabled(true);
		}
	}
	
	private void saveRecordInBuffer() {
		RecordBuffer.getInstance().saveRecord(record_);
	}
	
	private void scanComplete() {
		gpsScanner_.stopScanning();
		decrementProgressCounter();
	}

	private void performWifiScan() {
		setStatus("Looking for Wifi access points...");
		
		List<ScanResult> accessPoints = wifiScanner_.scan();
		
		record_.setWifiData(accessPoints);
		updateStatusDetails();
	}
	
	private void performGsmScan() {
		setStatus("Getting GSM data...");
		
		GsmCellLocation gsmCellLocation = gsmScanner_.scan();
		
		setStatus("Waiting for GPS fixes...");
		
		record_.setGsmData(gsmCellLocation);
		updateStatusDetails();
	}

	private void setupActionBar() {
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle(getResources().getText(R.string.app_name));
		actionBar.addAction(new ActionBar.IntentAction(this, MainscreenPlugin.createIntent(this), R.drawable.mini_home));
	}
	
	public void setStatus(String status) {
		statusText_.setText(status);
	}
	
	private void updateStatusDetails() {
		setStatusDetail("Got GSM, " + record_.getNbAccessPoints() + " Wifi AP and " + gpsScanner_.getNbFixes() + " GPS fixes.");
	}
	
	public void setStatusDetail(String status) {
		statusDetailText_.setText(status);
	}
	
	private synchronized void incrementProgressCounter() {
		progressCount_ ++;
		actionBar_.setProgressBarVisibility(View.VISIBLE);
	}

	private synchronized void decrementProgressCounter() {
		progressCount_--;
		if(progressCount_ < 0) { //Should never happen!
			Log.e(this.getClass().toString(), "ERROR progresscount is negative!");
		}

		if(progressCount_ <= 0) {
			actionBar_.setProgressBarVisibility(View.GONE);
		}
	}
}