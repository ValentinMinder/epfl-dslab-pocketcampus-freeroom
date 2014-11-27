package org.pocketcampus.platform.android.ui.dialog;

import java.util.Arrays;
import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.platform.android.ui.element.ButtonElement;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

/**
 * @author Florian <florian.laurent@epfl.ch>
 *
 */
public class PopupDialog extends Dialog implements OnClickListener {
	
	private TextView mTitleTextView;
	private RelativeLayout mContentView;
	private LinearLayout mButtonsView;
	private TextView mDetailsTextView;

	// TODO find something betters for the buttons?
	// eg List<ButtonElement>?
	public PopupDialog(Context context, String title, View content, List<ButtonElement> buttons) {
		super(context);
		initialize(title, content, buttons);
	}
	
	public PopupDialog(Context context, String title, View content, ButtonElement button) {
		super(context);
		initialize(title, content, Arrays.asList(button));
	}
	
	public PopupDialog(Context context) {
		super(context);
		initialize(null, null, null);
	}
	
	private void initialize(String title, View content, List<ButtonElement> buttons) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sdk_popup_layout);
		getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		setCanceledOnTouchOutside(true);
		
		mTitleTextView = (TextView) findViewById(R.id.sdk_popup_title);
		mDetailsTextView = (TextView) findViewById(R.id.sdk_popup_details);
		mContentView = (RelativeLayout) findViewById(R.id.sdk_popup_content);
		mButtonsView = (LinearLayout) findViewById(R.id.sdk_popup_buttons);
		
		setTitle(title);
		setContent(content);
		setButtons(buttons);
	}
	
	@Override
	public void show() {
		hideViewIfEmpty(mTitleTextView);
		hideViewIfEmpty(mDetailsTextView);
		hideViewIfEmpty(mContentView);
		hideViewIfEmpty(mButtonsView);
		
		super.show();
	}
	
	private void hideViewIfEmpty(ViewGroup viewGroup) {
		// TODO doesn't seem to work
		viewGroup.setVisibility(getViewVisibility(viewGroup.getChildCount() == 0));
	}

	private void hideViewIfEmpty(TextView view) {
		view.setVisibility(getViewVisibility(view.getText().equals("")));
	}

	private int getViewVisibility(boolean hidden) {
		return hidden ? View.GONE : View.VISIBLE;
	}
	
	@Override
	public void setTitle(CharSequence title) {
		mTitleTextView.setText(title);
	}
	
	public void setDetails(String details) {
		mDetailsTextView.setText(details);
	}
	
	public void setButtons(List<ButtonElement> buttons) {
		if(buttons == null) {
			return;
		}
		
		mButtonsView.removeAllViews();
		
		for(ButtonElement button : buttons) {
			mButtonsView.addView(button);
		}
	}

	public void setContent(View content) {
		if(content == null) {
			return;
		}
		
		mContentView.removeAllViews();
		mContentView.addView(content);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			default:
				break;
		}
	}

	public void close() {
		super.dismiss();
	}

}
