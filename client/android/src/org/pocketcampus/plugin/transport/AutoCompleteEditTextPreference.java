package org.pocketcampus.plugin.transport;

import org.pocketcampus.core.communication.RequestHandler;
import org.pocketcampus.shared.plugin.transport.Location;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

public class AutoCompleteEditTextPreference extends EditTextPreference { 
	private RequestHandler requestHandler_;
	private AlertDialog dialog_;

	public AutoCompleteEditTextPreference(Context context, RequestHandler requestHandler) {
		super(context);
		requestHandler_ = requestHandler;
	}

	private AutoCompleteEditTextPreference(Context context) { super(context); }

	private AutoCompleteEditTextPreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	private AutoCompleteEditTextPreference(Context context, AttributeSet attrs,
			int defStyle)
	{
		super(context, attrs, defStyle);
	}       

	@Override
	protected void showDialog(Bundle state) {
		Context context = getContext();

		AlertDialog.Builder mBuilder = new AlertDialog.Builder(context)
		.setNegativeButton("Cancel", this);

		View contentView = onCreateDialogView();
		if (contentView != null) {
			onBindDialogView(contentView);
			mBuilder.setView(contentView);
		}

		onPrepareDialogBuilder(mBuilder);

		//getPreferenceManager().registerOnActivityDestroyListener(this);

		// Create the dialog
		dialog_ = mBuilder.create();

		// requests input method
		Window window = dialog_.getWindow();
		window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

		dialog_.setOnDismissListener(this);
		dialog_.show();
	}


	/**
	 * the default EditTextPreference does not make it easy to
	 * use an AutoCompleteEditTextPreference field. By overriding this method
	 * we perform surgery on it to use the type of edit field that
	 * we want.
	 */
	protected void onBindDialogView(View view)
	{
		super.onBindDialogView(view);

		// find the current EditText object
		final EditText editText = (EditText)view.findViewById(android.R.id.edit);
		// copy its layout params
		LayoutParams params = editText.getLayoutParams();
		ViewGroup vg = (ViewGroup)editText.getParent();
		String curVal = editText.getText().toString();
		// remove it from the existing layout hierarchy
		vg.removeView(editText);        
		// construct a new editable autocomplete object with the appropriate params
		// and id that the TextEditPreference is expecting
		mACTV = new AutoCompleteTextView(getContext());
		mACTV.setLayoutParams(params);
		mACTV.setId(android.R.id.edit);
		mACTV.setText(curVal);


		ArrayAdapter<Location> adapter = new LocationAdapter(getContext(), android.R.layout.simple_dropdown_item_1line, mACTV, requestHandler_);
		mACTV.setAdapter(adapter);
		
		OnItemClickListener onItemSelectedListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				dialog_.dismiss();
				onDialogClosed(true);
			}

			
		};
		mACTV.setOnItemClickListener(onItemSelectedListener );
		
		// add the new view to the layout
		vg.addView(mACTV);
	}

	/**
	 * Because the baseclass does not handle this correctly
	 * we need to query our injected AutoCompleteTextView for
	 * the value to save 
	 */
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult && mACTV != null) 
		{           
			String value = mACTV.getText().toString();
			if (callChangeListener(value)) {
				setText(value);
			}
		}
	}

	/**
	 * again we need to override methods from the base class
	 */
	public EditText getEditText() 
	{
		return mACTV;
	}

	private AutoCompleteTextView mACTV = null;
}








