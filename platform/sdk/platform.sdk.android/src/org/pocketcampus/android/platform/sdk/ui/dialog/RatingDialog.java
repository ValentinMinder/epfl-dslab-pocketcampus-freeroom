package org.pocketcampus.android.platform.sdk.ui.dialog;

import org.pocketcampus.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RatingBar.OnRatingBarChangeListener;

public class RatingDialog extends Dialog {

	public RatingDialog(Context context, int theme) {
		super(context, theme);
	}

	public RatingDialog(Context context) {
		super(context);
	}

	public static class Builder {
		private Context mContext;
		private View mContentView;
		private boolean mCanceledOnTouchOutside;
		
		private String mTitle;
		private float mMyRating;
		private RatingBar mRatingBar;
		
		private Button mOkButton;
		private Button mCancelButton;
		private String mOkButtonText;
		private String mCancelButtonText;
		
		private DialogInterface.OnClickListener mOkButtonClickListener;
		private DialogInterface.OnClickListener mCancelButtonClickListener;

		public Builder(Context context) {
			mContext = context;
		}

		/**
		 * Sets the dialog title.
		 * @param title
		 * @return
		 */
		public Builder setTitle(String title) {
			mTitle = title;
			return this;
		}

		/**
		 * Sets the dialog title from a resource.
		 * @param title
		 * @return
		 */
		public Builder setTitle(int title) {
			mTitle = (String) mContext.getText(title);
			return this;
		}

		/**
		 * Sets a custom content view for the Dialog.
		 * Only used if no message is set.
		 * @param view
		 * @return
		 */
		public Builder setContentView(View view) {
			mContentView = view;
			return this;
		}

		/**
		 * Sets the first button text from a resource and its listener
		 * @param fristButtonText
		 * @param listener
		 * @return
		 */
		public Builder setOkButton(int fristButtonText, DialogInterface.OnClickListener listener) {
			mOkButtonText = (String) mContext.getText(fristButtonText);
			mOkButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the first button text and its listener
		 * @param firstButtonText
		 * @param listener
		 * @return
		 */
		public Builder setOkButton(String firstButtonText, DialogInterface.OnClickListener listener) {
			mOkButtonText = firstButtonText;
			mOkButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the second button text from a resource and its listener
		 * @param secondButtonText
		 * @param listener
		 * @return
		 */
		public Builder setCancelButton(int secondButtonText, DialogInterface.OnClickListener listener) {
			mCancelButtonText = (String) mContext.getText(secondButtonText);
			mCancelButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the second button text and its listener
		 * @param secondButtonText
		 * @param listener
		 * @return
		 */
		public Builder setCancelButton(String secondButtonText, DialogInterface.OnClickListener listener) {
			mCancelButtonText = secondButtonText;
			mCancelButtonClickListener = listener;
			return this;
		}

		public void setCanceledOnTouchOutside(boolean cancel) {
			mCanceledOnTouchOutside = cancel;
		}

		/**
		 * Creates the custom dialog.
		 */
		public RatingDialog create() {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			// dialog
			final RatingDialog dialog = new RatingDialog(mContext, R.style.Dialog);
			final View layout = inflater.inflate(R.layout.sdk_dialog_rating, null);
			dialog.addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			dialog.setCanceledOnTouchOutside(mCanceledOnTouchOutside);

			// title
			if(mTitle != null) {
				((TextView) layout.findViewById(R.id.sdk_dialog_rating_title)).setText(mTitle);
			} else {
				((LinearLayout) layout.findViewById(R.id.sdk_dialog_rating_title_layout)).setVisibility(View.GONE);
			}

			// set the confirm button
			if (mOkButtonText != null) {
				mOkButton = ((Button) layout.findViewById(R.id.sdk_dialog_rating_okButton));
				mOkButton.setText(mOkButtonText);
				mOkButton.setEnabled(false);
				if (mOkButtonClickListener != null) {
					mOkButton.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							mOkButtonClickListener.onClick(
									dialog, DialogInterface.BUTTON_POSITIVE);
						}
					});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.sdk_dialog_rating_okButton).setVisibility(
						View.GONE);
			}

			// set the cancel button
			if (mCancelButtonText != null) {
				((Button) layout.findViewById(R.id.sdk_dialog_rating_cancelButton))
				.setText(mCancelButtonText);
				if (mCancelButtonClickListener != null) {
					((Button) layout.findViewById(R.id.sdk_dialog_rating_cancelButton))
					.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							mCancelButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
						}
					});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.sdk_dialog_rating_cancelButton).setVisibility(
						View.GONE);
			}

			// rating bar and nb of votes
			mRatingBar = ((RatingBar) layout.findViewById(R.id.sdk_dialog_rating_ratingBarIndicator));
			mRatingBar.setRating(0);
			mRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
				public void onRatingChanged(RatingBar ratingBar, float rating,
						boolean fromUser) {
					((Button) layout.findViewById(R.id.sdk_dialog_rating_okButton)).setEnabled(true);
					mMyRating = rating;
				}
			});
			
			dialog.setContentView(layout);
			return dialog;
		}

		public float getSubmittedRating() {
			return mMyRating;
		}
	}
}
