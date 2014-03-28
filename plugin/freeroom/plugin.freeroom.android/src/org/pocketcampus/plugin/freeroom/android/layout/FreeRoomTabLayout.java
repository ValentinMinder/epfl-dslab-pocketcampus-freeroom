package org.pocketcampus.plugin.freeroom.android.layout;

import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.FreeRoomCheckOccupancySearchView;
import org.pocketcampus.plugin.freeroom.android.FreeRoomHomeView;
import org.pocketcampus.plugin.freeroom.android.FreeRoomSearchRoomsView;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;

import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FreeRoomTabLayout extends StandardTitledLayout {

	/**
	 * global Layout is the layout contening all the others views tabLayout is
	 * the layout used to stored the elements related to the tab (button to
	 * switch tabs for instance) and the contentView is the rest of the view, it
	 * is where everything else should go.
	 */
	
	//size in dip
	private final int TAB_TEXT_SIZE = 18;
	
	private Context context;
	private IFreeRoomView callerView;

	private LinearLayout tabLayout;
	private LinearLayout globalLayout;
	private RelativeLayout contentView;

	// navigation is done through buttons
	private TextView homeButton;
	private TextView freeRoomButton;
	private TextView occupancyButton;

	private final Class<FreeRoomHomeView> homeView = FreeRoomHomeView.class;
	private final Class<FreeRoomSearchRoomsView> freeRoomSearchView = FreeRoomSearchRoomsView.class;
	private final Class<FreeRoomCheckOccupancySearchView> occupancySearchView = FreeRoomCheckOccupancySearchView.class;

	// TODO maybe find a different way of doing this (typically in a view this
	// is like ...(this, this);
	public FreeRoomTabLayout(Context context, IFreeRoomView caller) {
		super(context);
		this.context = context;
		this.callerView = caller;
		initializeLayout();
	}

	private void initializeLayout() {
		globalLayout = new LinearLayout(context);
		globalLayout.setOrientation(LinearLayout.VERTICAL);

		tabLayout = new LinearLayout(context);
		tabLayout.setOrientation(LinearLayout.HORIZONTAL);
		tabLayout.setGravity(Gravity.CENTER_HORIZONTAL);
		
		homeButton = new TextView(context);
		freeRoomButton = new TextView(context);
		occupancyButton = new TextView(context);
		
		//TODO adapt size in function of the size of the screen
		homeButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, TAB_TEXT_SIZE);
		freeRoomButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, TAB_TEXT_SIZE);
		occupancyButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, TAB_TEXT_SIZE);

		homeButton.setText(context.getString(R.string.freeroom_tab_home));
		freeRoomButton.setText(context
				.getString(R.string.freeroom_tab_freeroomsearch));
		occupancyButton.setText(context
				.getString(R.string.freeroom_tab_occupancysearch));

		addListenersToButtons();

		tabLayout.addView(homeButton);
		ImageView mSeparator = new ImageView(context);
		mSeparator.setImageResource(android.R.drawable.presence_invisible);
		tabLayout.addView(mSeparator);
		tabLayout.addView(freeRoomButton);
		ImageView mSeparator2 = new ImageView(context);
		mSeparator2.setImageResource(android.R.drawable.presence_invisible);
		tabLayout.addView(mSeparator2);
		tabLayout.addView(occupancyButton);

		contentView = new RelativeLayout(context);

		globalLayout.addView(tabLayout);
		globalLayout.addView(contentView);
		super.addFillerView(globalLayout);
	}

	private void addListenersToButtons() {
		homeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// if this is already the activity, don't restart it
				if (!context.getClass().toString().equals(homeView.toString())) {
					Intent i = new Intent(context, homeView);
					context.startActivity(i);
				}
			}
		});

		freeRoomButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!context.getClass().toString()
						.equals(freeRoomSearchView.toString())) {
					Intent i = new Intent(context, freeRoomSearchView);
					context.startActivity(i);
				}
			}
		});

		occupancyButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (context.getClass().toString()
						.equals(occupancySearchView.toString())) {
					callerView.initializeView();
				} else {
					Intent i = new Intent(context, occupancySearchView);
					context.startActivity(i);
				}
			}
		});
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
