package org.pocketcampus.android.platform.sdk.ui.dialog;

import org.pocketcampus.R;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;

/**
 * Class will represent a rating dialog, that will be opened when rating a meal.
 * 
 */
public class RatingDialog extends Dialog {
	private Button mOkButton;
	private Button mCancelButton;
	private RatingBar mRatingbar;
	private Object mToRate;
	private Context mContext;
	private OnItemClickListener mOnRatingClickListener;
	private int mPosition;

	public RatingDialog(Object toRate, Context context, OnItemClickListener l, int position) {
		super(context);
		this.mToRate = toRate;
		this.mContext = context;
		this.mOnRatingClickListener = l;
		mPosition = position;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		/** Design the dialog in dialog.xml file */
		setContentView(R.layout.sdk_dialog_rating);

		mOkButton = (Button) findViewById(R.id.sdk_rating_submit);
		mOkButton.setEnabled(false);
		mOkButton.setOnClickListener(new OKListener());

		mCancelButton = (Button) findViewById(R.id.sdk_rating_cancel);
		mCancelButton.setOnClickListener(new CancelListener());

		mRatingbar = (RatingBar) findViewById(R.id.sdk_rating_ratebar);
		mRatingbar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				mOkButton.setEnabled(true);
			}
		});
	}

	/**
	 * Called when OK button is clicked
	 * 
	 */
	private class OKListener implements android.view.View.OnClickListener {
		public void onClick(View v) {
			
			double rating = mRatingbar.getRating();
			
			if(mOnRatingClickListener != null) {				
				mOnRatingClickListener.onItemClick(null, v, mPosition, (long)rating);
			}else{
				Log.d("RATING", "Listener was null");
			}
			RatingDialog.this.dismiss();
		}
	}

	/**
	 * Called when cancel button is clicked - simply dismiss the dialog.
	 * 
	 */
	private class CancelListener implements android.view.View.OnClickListener {
		public void onClick(View v) {
			RatingDialog.this.dismiss();
		}
	}
}