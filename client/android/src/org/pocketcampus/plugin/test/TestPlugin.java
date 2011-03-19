package org.pocketcampus.plugin.test;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.communication.ServerRequest;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TestPlugin extends PluginBase {
	private TextView textView_;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
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
		class UpperCaseRequest extends ServerRequest {
			
			// ...what to do when the result is ready
			@Override
			protected void onPostExecute(String result) {
				textView_.setText(result);
			}
			
			// ...what to do if cancelled
			@Override
			protected void onCancelled() {
				System.out.println("Cancelled!");
			}
		}
		
		// Create a RequestParameters object containing the parameters
		RequestParameters reqParams = new RequestParameters();
		reqParams.addParameter("text", text);
		
		// Use a RequestHandler to execute your request.
		// You don't have to worry about which Servlet your talking to, it will automatically be the
		// one corresponding to the plugin you're in.
		// If you need to do a request from another class/Activity you can give it a RequestHandler instance.
		getRequestHandler().execute(new UpperCaseRequest(), reqParams);
		
		
		// To handle the loading another way, you can just use the RequestHandler to give you the complete
		// request URL, including the server and servlet address.
		System.out.println(getRequestHandler().getRequestUrl(reqParams));
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


















