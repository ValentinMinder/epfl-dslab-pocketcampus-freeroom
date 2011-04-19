package org.pocketcampus.plugin.transport;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.RequestHandler;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.ui.ActionBar;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class TransportResults extends Activity {
	private RequestHandler requestHandler_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ActionBar.setup(this, findViewById(R.layout.actionbar), true);
		setContentView(R.layout.transport_travelplan);
		
		Bundle extras = getIntent().getExtras();
		String result = (String) extras.getSerializable("result");
	}
}
