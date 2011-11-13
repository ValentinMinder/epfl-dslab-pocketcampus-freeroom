package org.pocketcampus.plugin.transport.android;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.adapter.LabeledArrayAdapter;
import org.pocketcampus.android.platform.sdk.ui.element.InputBarElement;
import org.pocketcampus.android.platform.sdk.ui.element.OnKeyPressedListener;
import org.pocketcampus.android.platform.sdk.ui.labeler.ILabeler;
import org.pocketcampus.android.platform.sdk.ui.list.LabeledListViewElement;
import org.pocketcampus.plugin.transport.android.iface.ITransportView;
import org.pocketcampus.plugin.transport.shared.Location;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class TransportMainView extends PluginView implements ITransportView {
	private TransportController mController;
	private TransportModel mModel;

	private InputBarElement mLayout;
	private LabeledListViewElement mListView;
	
	private ILabeler<Location> mLocationLabeler = new ILabeler<Location>() {
		@Override
		public String getLabel(Location obj) {
			return obj.name;
		}
	};

	LabeledArrayAdapter mAdapter;
	
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return TransportController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		mController = (TransportController)controller;
		mModel = (TransportModel) mController.getModel();
		
		mLayout = new InputBarElement(this);
		mLayout.setInputHint("Destination");
		
		mLayout.setOnKeyPressedListener(new OnKeyPressedListener() {
			@Override
			public void onKeyPressed(String text) {
				mController.getAutocompletions(text);
			}
		});
		
		setContentView(mLayout);
		
		createDestinationsList();
	}

	private void createDestinationsList() {
		mListView = new LabeledListViewElement(this);
		mLayout.addView(mListView);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
				// TODO
			}
		});
	}

	@Override
	public void preferredDestinationsUpdated() {
		mAdapter = new LabeledArrayAdapter(this, mModel.getPreferredDestinations(), mLocationLabeler);
		
		mListView.setAdapter(mAdapter);
		mListView.invalidate();
	}

	@Override
	public void networkErrorHappened() {
		Toast toast = Toast.makeText(getApplicationContext(), "Network error!", Toast.LENGTH_SHORT);
		toast.show();
	}
}
