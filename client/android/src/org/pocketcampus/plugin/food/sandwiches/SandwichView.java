package org.pocketcampus.plugin.food.sandwiches;

import org.pocketcampus.R;
import org.pocketcampus.plugin.food.FoodPlugin;
import org.pocketcampus.shared.plugin.food.Sandwich;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SandwichView extends LinearLayout {
	TextView sandwichName_;
	View convertView;
	Sandwich currentSandwich_;
	FoodPlugin ctx_;
	LayoutInflater mInflater_;

	SandwichView(FoodPlugin context, Sandwich currentSandwich) {
		super(context);
		convertView = LayoutInflater.from(context.getApplicationContext())
				.inflate(R.layout.food_sandwich_list_item, null);

		// Creates a ViewHolder and store references to the two children
		// views we want to bind data to.
		this.sandwichName_ = (TextView) convertView.findViewById(R.id.food_sandwich_name);
		this.currentSandwich_ = currentSandwich;
		this.ctx_ = context;

		initializeView();
	}

	public void initializeView() {
		sandwichName_.setText(currentSandwich_.getName());
		
		sandwichName_.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// menuDialog(position);
			}
		});

		addView(convertView);
	}

}
