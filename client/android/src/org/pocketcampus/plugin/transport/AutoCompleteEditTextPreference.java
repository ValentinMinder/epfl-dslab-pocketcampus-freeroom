package org.pocketcampus.plugin.transport;

import java.lang.reflect.Type;
import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.communication.RequestHandler;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.plugin.transport.LocationAdapter.AutocompleteRequest;
import org.pocketcampus.shared.plugin.transport.Location;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

/**
 * EditTextPreference dialog with autocompletion capabilities.
 * @author Florian
 * @status working
 */
public class AutoCompleteEditTextPreference extends EditTextPreference { 
	private AutoCompleteTextView AutoCompleteTextView_ = null;
	private RequestHandler requestHandler_;
	private AlertDialog dialog_;
	private boolean validInput_ = false;

	/**
	 * AutoCompleteEditTextPreference constructor.
	 * @param context Context of the PreferenceActivity
	 * @param requestHandler RequestHandler for the Activity
	 */
	public AutoCompleteEditTextPreference(Context context, RequestHandler requestHandler) {
		super(context);
		requestHandler_ = requestHandler;
	}

	@Override
	protected void showDialog(Bundle state) {
		Context context = getContext();

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setNegativeButton(context.getResources().getString(R.string.cancel), this);
		builder.setPositiveButton(context.getResources().getString(R.string.ok), this);
		
		View contentView = onCreateDialogView();
		
		if (contentView != null) {
			onBindDialogView(contentView);
			builder.setView(contentView);
		}

		onPrepareDialogBuilder(builder);
		dialog_ = builder.create();

		// Requests input method
		Window window = dialog_.getWindow();
		window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

		dialog_.setOnDismissListener(this);
		dialog_.show();
	}


	/**
	 * Modifies the default EditText to make it an AutoComplete input.
	 */
	protected void onBindDialogView(View view)
	{
		super.onBindDialogView(view);

		// Finds the current EditText object
		final EditText editText = (EditText)view.findViewById(android.R.id.edit);
		
		// Copies its layout params
		LayoutParams params = editText.getLayoutParams();
		ViewGroup group = (ViewGroup)editText.getParent();
		String curVal = editText.getText().toString();
		
		// Removes it
		group.removeView(editText);
		
		// Builds a new autocomplete imitating the original one
		AutoCompleteTextView_ = new AutoCompleteTextView(getContext());
		AutoCompleteTextView_.setLayoutParams(params);
		AutoCompleteTextView_.setId(android.R.id.edit);
		AutoCompleteTextView_.setText(curVal);
		AutoCompleteTextView_.setSingleLine(true);

		// Sets the Adapter
		ArrayAdapter<Location> adapter = new LocationAdapter(getContext(), android.R.layout.simple_dropdown_item_1line, AutoCompleteTextView_, requestHandler_);
		AutoCompleteTextView_.setAdapter(adapter);
		
		// Registers OnItemClick listener
		OnItemClickListener onItemSelectedListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				validInput_ = true;
				dialog_.dismiss();
				onDialogClosed(true);
			}
		};
		AutoCompleteTextView_.setOnItemClickListener(onItemSelectedListener );
		
		// Adds the new view to the layout
		group.addView(AutoCompleteTextView_);
	}

	/**
	 * Because the baseclass does not handle this correctly
	 * we need to query our injected AutoCompleteTextView for
	 * the value to save .
	 */
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult && AutoCompleteTextView_!=null) {
			String value = AutoCompleteTextView_.getText().toString();
			
			if(value.equals("")) {
				return;
			}
			
			if(validInput_) {
				if (callChangeListener(value)) {
					setText(value);
				}
			} else {
				validateInput();
			}
		}
	}

	private void validateInput() {
		RequestParameters reqParam = new RequestParameters();
		reqParam.addParameter("constraint", AutoCompleteTextView_.getText().toString());
		requestHandler_.execute(new AutocompleteRequest(), "autocomplete", reqParam);
	}
	
	class AutocompleteRequest extends DataRequest {
		@Override
		protected int timeoutDelay() {
			// Needs to be fast.
			return 7;
		}
		
		@Override
		protected int expirationDelay() {
			// Not likely to change.
			return 6 * 60 * 60;
		}
		
		@Override
		protected void doInUiThread(String result) {
			Gson gson = new Gson();
			Type AutocompleteType = new TypeToken<List<Location>>(){}.getType();
			List<Location> locations = gson.fromJson(result, AutocompleteType);
			
			if(locations!=null && locations.size()>0) {
				String value = locations.get(0).toString();
				
				if (callChangeListener(value)) {
					setText(value);
				}
				
			} else {
				TransportPlugin.makeToast(R.string.transport_destNotFound);
			}
		}
		
		@Override
		protected void onCancelled() {
			TransportPlugin.makeToast(R.string.server_connection_error);
		}
	}
	
	/**
	 * Again we need to override methods from the base class
	 */
	public EditText getEditText() {
		return AutoCompleteTextView_;
	}
}








