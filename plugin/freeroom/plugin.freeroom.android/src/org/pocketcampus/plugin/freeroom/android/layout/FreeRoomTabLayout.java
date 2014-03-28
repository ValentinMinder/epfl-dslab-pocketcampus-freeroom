package org.pocketcampus.plugin.freeroom.android.layout;

import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.freeroom.R;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class FreeRoomTabLayout extends StandardTitledLayout {

	/**
	 * global Layout is the layout contening all the others views tabLayout is
	 * the layout used to stored the elements related to the tab (button to
	 * switch tabs for instance)
	 * and the contentView is the rest of the view, it is where everything else should go.
	 */
	private Context context;
	private LinearLayout tabLayout;
	private LinearLayout globalLayout;
	private RelativeLayout contentView;

	private Button homeButton;
	private Button freeRoomButton;
	private Button occupancyButton;

	public FreeRoomTabLayout(Context context) {
		super(context);
		this.context = context;
		initializeLayout();
	}

	private void initializeLayout() {
		globalLayout = new LinearLayout(context);
		globalLayout.setOrientation(LinearLayout.VERTICAL);

		tabLayout = new LinearLayout(context);
		tabLayout.setOrientation(LinearLayout.HORIZONTAL);

		homeButton = new Button(context);
		freeRoomButton = new Button(context);
		occupancyButton = new Button(context);

		homeButton.setText(context.getString(R.string.freeroom_tab_home));
		freeRoomButton.setText(context
				.getString(R.string.freeroom_tab_freeroomsearch));
		occupancyButton.setText(context
				.getString(R.string.freeroom_tab_occupancysearch));

		tabLayout.addView(homeButton);
		tabLayout.addView(freeRoomButton);
		tabLayout.addView(occupancyButton);

		contentView = new RelativeLayout(context);

		globalLayout.addView(tabLayout);
		globalLayout.addView(contentView);
		super.addFillerView(globalLayout);
	}

	@Override
	public void addFillerView(View child) {
		contentView.addView(child);
	}

	@Override
	public void removeFillerView() {
		contentView.removeAllViews();
	}

}
