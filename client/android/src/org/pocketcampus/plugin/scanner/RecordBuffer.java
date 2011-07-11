package org.pocketcampus.plugin.scanner;

import java.util.ArrayList;

public class RecordBuffer {
	private static RecordBuffer instance_;
	private static ArrayList<ScannerRecord> recordBuffer_;

	public RecordBuffer() {
		recordBuffer_ = new ArrayList<ScannerRecord>();
	}
	
	public static RecordBuffer getInstance() {
		if(instance_ == null) {
			instance_ = new RecordBuffer();
		}
		
		return instance_;
	}
	
	public void saveRecord(ScannerRecord record_) {
		recordBuffer_.add(record_);
	}
	
	public int getNbBuffered() {
		return recordBuffer_.size();
	}

	public ScannerRecord getOldestBuffered() {
		return recordBuffer_.remove(0);
	}
}
