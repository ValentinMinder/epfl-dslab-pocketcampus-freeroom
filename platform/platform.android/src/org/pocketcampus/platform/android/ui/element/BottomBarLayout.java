package org.pocketcampus.platform.android.ui.element;

import org.pocketcampus.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

// TODO have a superclass shared by this and StandardLayout?
/**
 * @author Florian <florian.laurent@epfl.ch>
 *
 */
public class BottomBarLayout extends RelativeLayout implements Element {
	private RelativeLayout mInnerLayout;
	private TextView mBottomBarTextView;

	public BottomBarLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	public BottomBarLayout(Context context) {
		super(context);
		initialize(context);
	}

	private void initialize(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		RelativeLayout bottomBarLayout = (RelativeLayout) inflater.inflate(
				R.layout.sdk_bottombar_layout, null);
		super.addView(bottomBarLayout);

		mInnerLayout = (RelativeLayout) bottomBarLayout
				.findViewById(R.id.sdk_bottombar_layout_inner);
		mBottomBarTextView = (TextView) bottomBarLayout
				.findViewById(R.id.sdk_bottombar_layout_bottombar);
	}

	@Override
	public void addView(View child) {
		mInnerLayout.addView(child);
	}

	public TextView getBottomBarTextView() {
		return mBottomBarTextView;
	}
}
