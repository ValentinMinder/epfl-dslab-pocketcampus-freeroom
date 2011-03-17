package org.pocketcampus.plugin.food;

import org.pocketcampus.R;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FoodTabIndicator extends LinearLayout {

	public FoodTabIndicator(Context context, String label) {
		super(context);

		View tab = View.inflate(context, R.layout.food_tab_indicator, this);

		TextView tv = (TextView) tab.findViewById(R.id.food_tab_label);
		tv.setText(label);
	}
}
