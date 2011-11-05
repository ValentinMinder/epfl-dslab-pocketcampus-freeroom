package org.pocketcampus.plugin.transport.android;

import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.adapter.LabeledArrayAdapter;
import org.pocketcampus.android.platform.sdk.ui.element.Labeler;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardLayout;
import org.pocketcampus.plugin.transport.android.iface.ITransportView;
import org.pocketcampus.plugin.transport.shared.Location;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.commonsware.cwac.tlv.TouchListView;

public class TransportMainView extends PluginView implements ITransportView {
	private TransportController mController;
	private TransportModel mModel;

	private StandardLayout mLayout;
	private TouchListView mListView;
	private Labeler<Location> mLocationLabeler = new Labeler<Location>() {
		@Override
		public String getLabel(Location obj) {
			return obj.name;
		}
	};

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return TransportController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		mController = (TransportController)controller;
		mModel = (TransportModel) mController.getModel();

		mLayout = new StandardLayout(this);
		setContentView(mLayout);

		mLayout.setText("Loading...");

		mController.getAutocompletions("epf");

	}

	@Override
	public void preferredDestinationsUpdated() {
		mLayout.hideText();

		List<Location> destinations = mModel.getPreferredDestinations();

		System.out.println("AHA!");
		System.out.println(destinations);

		final LabeledArrayAdapter adapter = new LabeledArrayAdapter(this, destinations, mLocationLabeler);

		LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		TouchListView mListView = (TouchListView) (inflater.inflate(R.layout.sdk_touchlistview, null));

		mListView.setDropListener(new TouchListView.DropListener() {
			@Override
			public void drop(int from, int to) {
				String item= adapter.getItem(from).toString();

				System.out.println("-------");
				System.out.println(item);
				System.out.println(to);
				
//				adapter.remove(item);
//				adapter.insert(item, to);
			}
		});

		mListView.setRemoveListener(new TouchListView.RemoveListener() {
			@Override
			public void remove(int which) {
//				adapter.remove(adapter.getItem(which));
			}
		});

		mListView.setAdapter(adapter);

		//			mListView = new LabeledListViewElement(this, destinations, mLocationLabeler);
		//			mListView.setOnItemClickListener(new OnItemClickListener() {
		//				@Override
		//				public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
		//					// TODO
		//				}
		//			});

		mLayout.addView(mListView);
	}

	@Override
	public void networkErrorHappened() {
		Toast toast = Toast.makeText(getApplicationContext(), "Network error!", Toast.LENGTH_SHORT);
		toast.show();
	}
}
