package org.pocketcampus.plugin.test;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.plugin.mainscreen.MainscreenPlugin;

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
		
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle("PocketCampus EPFL");
		actionBar.addAction(new ActionBar.IntentAction(this, MainscreenPlugin.createIntent(this), R.drawable.mini_home));
		
		textView_ = (TextView) findViewById(R.id.TestTextView);
		
		Button button = (Button) findViewById(R.id.TestButton);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				remoteUpperCaseText();
			}
		});
	}
	
	private void remoteUpperCaseText() {
		class UpperCaseRequest extends RawTextRequest {
			@Override
			protected void onPostExecute(String result) {
				textView_.setText(result);
				System.out.println(result);
				System.out.println("Done!");
			}
		}
		
		textView_.setText("");
		
		EditText editText = (EditText) findViewById(R.id.editText);
		new UpperCaseRequest().execute(editText.getText().toString());
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


















