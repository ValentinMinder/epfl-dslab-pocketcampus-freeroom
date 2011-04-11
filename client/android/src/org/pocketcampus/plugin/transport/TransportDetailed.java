package org.pocketcampus.plugin.transport;

import java.io.Serializable;
import java.util.Calendar;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.communication.ServerRequest;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * Transport tests.
 * 
 * @status WIP
 * @author Florian
 */
public class TransportDetailed extends Activity {
	// autocomplete input fields
	private AutoCompleteTextView departureAutoCompleteTextView_;
	private AutoCompleteTextView arrivalAutoCompleteTextView_;
	
	// date buttons: day and time
	private Button dayButton_;
	//private TimePickerButton timeButton_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// setup activity
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transport_main);
		//setupActionBar(true);
		
		// setup autocomplete fields
//		departureAutoCompleteTextView_ = (AutoCompleteTextView) findViewById(R.id.location_departure);
//		LocationAdapter adapterDeparture = new LocationAdapter(this, R.layout.transport_locationentry, departureAutoCompleteTextView_, getRequestHandler());
//		departureAutoCompleteTextView_.setAdapter(adapterDeparture);
//
//		arrivalAutoCompleteTextView_ = (AutoCompleteTextView) findViewById(R.id.location_arrival);
//		LocationAdapter adapterArrival = new LocationAdapter(this, R.layout.transport_locationentry, arrivalAutoCompleteTextView_, getRequestHandler());
//		arrivalAutoCompleteTextView_.setAdapter(adapterArrival);

		// setup date buttons
		dayButton_ = (Button) findViewById(R.id.date_button);
		
		
//		dayButton_ = new DayPickerButton((Button) findViewById(R.id.date_button));
//		timeButton_ = new TimePickerButton((Button) findViewById(R.id.time_button));
//		
//		updateDateDisplay();
//		dayButton_.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				showDialog(DATE_DIALOG_ID);
//			}
//		});
//
//
//		
//		updateTimeDisplay();
//		timeButton_.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				showDialog(TIME_DIALOG_ID);
//			}
//		});
		
		Button button = (Button) findViewById(R.id.search_button);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					class ConnectionsRequest extends ServerRequest {

						@Override
						protected void onPostExecute(String result) {
							System.out.println(result);
							
							Intent intent = new Intent(TransportDetailed.this, org.pocketcampus.plugin.transport.TransportResults.class);
							intent.putExtra("result", (Serializable) result);
							startActivity(intent); 
						}
						
					} 
					
					RequestParameters params = new RequestParameters();
					params.addParameter("from", "paris");
					params.addParameter("to", "berlin");
					
//					getRequestHandler().execute(new ConnectionsRequest(), "connections", params);
					
//					Intent intent = new Intent(TransportPlugin.this, org.pocketcampus.plugin.transport.TransportResults.class);
//					intent.putExtra("requestHandler", (Serializable) getRequestHandler());
//					startActivity(intent); 
					
				} catch (Exception e) {
					
				}
			}
		});
		
		
	}

//	@Override
//	protected Dialog onCreateDialog(int id) {
//		switch (id) {
//		case DATE_DIALOG_ID:
//			return new DatePickerDialog(this,
//					dateSetListener_,
//					date_.get(Calendar.YEAR), 
//					date_.get(Calendar.MONTH), 
//					date_.get(Calendar.DAY_OF_MONTH));
//		case TIME_DIALOG_ID:
//			return new TimePickerDialog(this,
//					timeSetListener_, 
//					date_.get(Calendar.HOUR_OF_DAY), 
//					date_.get(Calendar.MINUTE), 
//					false);
//		}
//		return null;
//	}
//
//	private DatePickerDialog.OnDateSetListener dateSetListener_ =
//		new DatePickerDialog.OnDateSetListener() {
//
//		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//			date_.set(Calendar.YEAR, year);
//			date_.set(Calendar.MONTH, monthOfYear);
//			date_.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//			updateDateDisplay();
//		}
//	};
//
//	private TimePickerDialog.OnTimeSetListener timeSetListener_ =
//		new TimePickerDialog.OnTimeSetListener() {
//		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//			date_.set(Calendar.HOUR_OF_DAY, hourOfDay);
//			date_.set(Calendar.MINUTE, minute);
//			updateTimeDisplay();
//		}
//	};
//
//	private void updateDateDisplay() {
//		dayButton_.setText(
//				new StringBuilder()
//				// Month is 0 based so add 1
//				.append(StringUtils.pad(date_.get(Calendar.DAY_OF_MONTH), 2)).append("-")
//				.append(StringUtils.pad(date_.get(Calendar.MONTH) + 1, 2)).append("-")
//				.append(date_.get(Calendar.YEAR)).append(" "));
//	}
//
//	private void updateTimeDisplay() {
//		timeButton_.setText(
//				new StringBuilder()
//				.append(StringUtils.pad(date_.get(Calendar.HOUR_OF_DAY), 2)).append(":")
//				.append(StringUtils.pad(date_.get(Calendar.MINUTE), 2)));
//	}
	
}