package org.pocketcampus.plugin.transport;

import java.util.Calendar;
import java.util.Date;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.utils.StringUtils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
public class TransportPlugin extends PluginBase {
	static final int DATE_DIALOG_ID = 0;
	static final int TIME_DIALOG_ID = 1;

//	private SbbProvider sbbProvider_; XXX
	private Calendar dateTime_;
	
	private AutoCompleteTextView departureAutoCompleteTextView_;
	private AutoCompleteTextView arrivalAutoCompleteTextView_;
	private TextView resultTextView_;
	
	private Button dateButton_;
	private Button timeButton_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.transport_main);
		setupActionBar(true);
		
//		sbbProvider_ = new SbbProvider("MJXZ841ZfsmqqmSymWhBPy5dMNoqoGsHInHbWJQ5PTUZOJ1rLTkn8vVZOZDFfSe");
		dateTime_ = Calendar.getInstance();
		
		resultTextView_ = (TextView) findViewById(R.id.result);

//		departureAutoCompleteTextView_ = (AutoCompleteTextView) findViewById(R.id.location_departure);
//		LocationAdapter adapterDeparture = new LocationAdapter(this, R.layout.transport_locationentry, departureAutoCompleteTextView_, sbbProvider_);
//		departureAutoCompleteTextView_.setAdapter(adapterDeparture);
//
//		arrivalAutoCompleteTextView_ = (AutoCompleteTextView) findViewById(R.id.location_arrival);
//		LocationAdapter adapterArrival = new LocationAdapter(this, R.layout.transport_locationentry, arrivalAutoCompleteTextView_, sbbProvider_);
//		arrivalAutoCompleteTextView_.setAdapter(adapterArrival);

		dateButton_ = (Button) findViewById(R.id.date_button);
		updateDateDisplay();
		dateButton_.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});


		timeButton_ = (Button) findViewById(R.id.time_button);
		updateTimeDisplay();
		timeButton_.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(TIME_DIALOG_ID);
			}
		});
		
		Button button = (Button) findViewById(R.id.search_button);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
//					Location from = sbbProvider_.autocompleteStations(departureAutoCompleteTextView_.getText()).get(0);
//					Location to = sbbProvider_.autocompleteStations(arrivalAutoCompleteTextView_.getText()).get(0);
//					QueryConnectionsResult result = sbbProvider_.queryConnections(from, null, to, new Date(), true, null, WalkSpeed.NORMAL);
//					resultTextView_.setText(result.connections.toString());

				} catch (Exception e) {
					resultTextView_.setText(e.toString());
				}
			}
		});
		
		
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this,
					dateSetListener_,
					dateTime_.get(Calendar.YEAR), 
					dateTime_.get(Calendar.MONTH), 
					dateTime_.get(Calendar.DAY_OF_MONTH));
		case TIME_DIALOG_ID:
			return new TimePickerDialog(this,
					timeSetListener_, 
					dateTime_.get(Calendar.HOUR_OF_DAY), 
					dateTime_.get(Calendar.MINUTE), 
					false);
		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener dateSetListener_ =
		new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			dateTime_.set(Calendar.YEAR, year);
			dateTime_.set(Calendar.MONTH, monthOfYear);
			dateTime_.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			updateDateDisplay();
		}
	};

	private TimePickerDialog.OnTimeSetListener timeSetListener_ =
		new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			dateTime_.set(Calendar.HOUR_OF_DAY, hourOfDay);
			dateTime_.set(Calendar.MINUTE, minute);
			updateTimeDisplay();
		}
	};

	private void updateDateDisplay() {
		dateButton_.setText(
				new StringBuilder()
				// Month is 0 based so add 1
				.append(StringUtils.pad(dateTime_.get(Calendar.DAY_OF_MONTH), 2)).append("-")
				.append(StringUtils.pad(dateTime_.get(Calendar.MONTH) + 1, 2)).append("-")
				.append(dateTime_.get(Calendar.YEAR)).append(" "));
	}

	private void updateTimeDisplay() {
		timeButton_.setText(
				new StringBuilder()
				.append(StringUtils.pad(dateTime_.get(Calendar.HOUR_OF_DAY), 2)).append(":")
				.append(StringUtils.pad(dateTime_.get(Calendar.MINUTE), 2)));
	}
	
	@Override
	public PluginInfo getPluginInfo() {
		return new TransportInfo();
	}

	@Override
	public PluginPreference getPluginPreference() {
		return null;
	}

}