package org.pocketcampus.plugin.test;

import java.util.Date;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TestPlugin extends PluginBase {
	private TextView textView_;
	private Date chrono_;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_main);
		setupActionBar(true);
		
		textView_ = (TextView) findViewById(R.id.TestTextView);
		
		Button button = (Button) findViewById(R.id.TestButton);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText editText = (EditText) findViewById(R.id.editText);
				String editTextContent = editText.getText().toString();
				
				remoteUpperCaseText(editTextContent);
			}
		});
	}
	
	/**
	 * HOW TO make a request to the server.
	 * Sample method transforming a String to uppercase on the server.
	 */
	private void remoteUpperCaseText(String text) {
		textView_.setText("");
		
		// Create a class for your request with...
		class UpperCaseRequest extends DataRequest {
			
			// ...what to do when the result is ready
			protected void doInUiThread(String result) {
				if(result != null) {
					int elapsed = stopTimer();
					
					textView_.setTextColor(Color.BLACK);
					textView_.setText(result + "\nPing:" + elapsed + " ms");
					
				} else {
					textView_.setTextColor(Color.RED);
					textView_.setText(exception_.toString());
				}
			}
		}
		
		// Create a RequestParameters object containing the parameters
		RequestParameters reqParams = new RequestParameters();
		reqParams.addParameter("text", text);
		
		startTimer();
		
		// Use a RequestHandler to execute your request.
		// You don't have to worry about which Servlet your talking to, it will automatically be the
		// one corresponding to the plugin you're in.
		// If you need to do a request from another class/Activity you can give it a RequestHandler instance.
		getRequestHandler().execute(new UpperCaseRequest(), "capitalize", reqParams);
		
		
		// To handle the loading another way, you can just use the RequestHandler to give you the complete
		// request URL, including the server and servlet address.
		System.out.println( getRequestHandler().getRequestUrl(reqParams, "capitalize") );
	}
	
	private void startTimer() {
		chrono_ = new Date();
	}
	
	private int stopTimer() {
		Date now = new Date();
		return (int) (now.getTime() - chrono_.getTime());
	}
	
	@Override
	public PluginPreference getPluginPreference() {
		return new TestPreference();
	}

	@Override
	public PluginInfo getPluginInfo() {
		return new TestInfo();
	}
}


















