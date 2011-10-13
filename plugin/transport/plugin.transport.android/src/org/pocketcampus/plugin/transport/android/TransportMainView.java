package org.pocketcampus.plugin.transport.android;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.element.LabeledListViewElement;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardLayout;
import org.pocketcampus.plugin.transport.android.iface.ITransportView;

import android.os.Bundle;

public class TransportMainView extends PluginView implements ITransportView {
	private StandardLayout mLayout;
	private LabeledListViewElement mListView;
	
	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		mLayout = new StandardLayout(this);
		mListView = new LabeledListViewElement(this);
		setContentView(mLayout);
		
		mLayout.setText("hello");
		
	}
}
