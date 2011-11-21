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

/**
 * A Dialog to display a Rating Bar that the user can set, along with a confirm
 * button and a cancel button.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public class RatingDialog extends Dialog {

	/**
	 * A constructor
	 * 
	 * @param context
	 *            The Application context
	 * @param theme
	 *            The int value of the theme we want to use
	 */
	public RatingDialog(Context context, int theme) {
		super(context, theme);
	}

	/**
	 * A constructor that simply calls the super constructor
	 * 
	 * @param context
	 *            The Application context
	 */
	public RatingDialog(Context context) {
		super(context);
	}

	/**
	 * Inner class Builder. Sets all the Dialog parameters and creates it.
	 * 
	 * @author Oriane <oriane.rodriguez@epfl.ch>
	 * 
	 */
	public static class Builder {
		/** The Application context */
		private Context mContext;
		/** The content View */
		private View mContentView;
		/** Decides whether clicking outside the dialog dismisses it or not */
		private boolean mCanceledOnTouchOutside;
		/** The element's title to be displayed in the dialog */
		private String mTitle;
		/** The rating bar displayed in the dialog */
		private RatingBar mRatingBar;
		/** The rating the user can set in the dialog */
		private float mMyRating;
		/** The positive button */
		private Button mPositiveButton;
		/** The negative button */
		private Button mNegativeButton;
		/** The positive button's text */
		private String mPositiveButtonText;
		/** the negative button's text */
		private String mNegativeButtonText;
		/** The positive button's listener */
		private DialogInterface.OnClickListener mPositiveButtonClickListener;
		/** The negative button's listener */
		private DialogInterface.OnClickListener mNegativeButtonClickListener;

		/**
		 * The constructor
		 * 
		 * @param context
		 *            The Application context
		 */
		public Builder(Context context) {
			mContext = context;
		}

		/**
		 * Sets the dialog title.
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(String title) {
			mTitle = title;
			return this;
		}

		/**
		 * Sets the dialog title from a resource.
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(int title) {
			mTitle = (String) mContext.getText(title);
			return this;
		}

		/**
		 * Sets a custom content view for the Dialog. Only used if no
		 * description is set.
		 * 
		 * @param view
		 * @return
		 */
		public Builder setContentView(View view) {
			mContentView = view;
			return this;
		}

		/**
		 * Sets the positive button text from a resource and its listener
		 * 
		 * @param positiveButtonText
		 * @param listener
		 * @return
		 */
		public Builder setOkButton(int positiveButtonText,
				DialogInterface.OnClickListener listener) {
			mPositiveButtonText = (String) mContext.getText(positiveButtonText);
			mPositiveButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the positive button text and its listener
		 * 
		 * @param positiveButtonText
		 * @param listener
		 * @return
		 */
		public Builder setOkButton(String positiveButtonText,
				DialogInterface.OnClickListener listener) {
			mPositiveButtonText = positiveButtonText;
			mPositiveButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the negative button text from a resource and its listener
		 * 
		 * @param negativeButtonText
		 * @param listener
		 * @return
		 */
		public Builder setCancelButton(int negativeButtonText,
				DialogInterface.OnClickListener listener) {
			mNegativeButtonText = (String) mContext.getText(negativeButtonText);
			mNegativeButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the negative button text and its listener
		 * 
		 * @param negativeButtonText
		 * @param listener
		 * @return
		 */
		public Builder setCancelButton(String negativeButtonText,
				DialogInterface.OnClickListener listener) {
			mNegativeButtonText = negativeButtonText;
			mNegativeButtonClickListener = listener;
			return this;
		}

		/**
		 * Decide whether the dialog is dismissed when touching outside
		 * 
		 * @param cancel
		 */
		public void setCanceledOnTouchOutside(boolean cancel) {
			mCanceledOnTouchOutside = cancel;
		}

		/**
		 * Creates the custom dialog.
		 */
		public RatingDialog create() {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			/** Dialog */
			final RatingDialog dialog = new RatingDialog(mContext,
					R.style.Dialog);
			final View layout = inflater.inflate(R.layout.sdk_dialog_rating,
					null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			dialog.setCanceledOnTouchOutside(mCanceledOnTouchOutside);

			/** title */
			if (mTitle != null) {
				((TextView) layout.findViewById(R.id.sdk_dialog_rating_title))
						.setText(mTitle);
			} else {
				((LinearLayout) layout
						.findViewById(R.id.sdk_dialog_rating_title_layout))
						.setVisibility(View.GONE);
			}

			/** positive button and its listener */
			if (mPositiveButtonText != null) {
				mPositiveButton = ((Button) layout
						.findViewById(R.id.sdk_dialog_rating_positiveButton));
				mPositiveButton.setText(mPositiveButtonText);
				mPositiveButton.setEnabled(false);
				if (mPositiveButtonClickListener != null) {
					mPositiveButton
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									mPositiveButtonClickListener.onClick(
											dialog,
											DialogInterface.BUTTON_POSITIVE);
								}
							});
				}
			} else {
				/** if no positive button just set the visibility to GONE */
				layout.findViewById(R.id.sdk_dialog_rating_positiveButton)
						.setVisibility(View.GONE);
			}

			/** negative button and its listener */
			if (mNegativeButtonText != null) {
				mNegativeButton = ((Button) layout
						.findViewById(R.id.sdk_dialog_rating_negativeButton));
				mNegativeButton.setText(mNegativeButtonText);
				if (mNegativeButtonClickListener != null) {
					((Button) layout
							.findViewById(R.id.sdk_dialog_rating_negativeButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									mNegativeButtonClickListener.onClick(
											dialog,
											DialogInterface.BUTTON_NEGATIVE);
								}
							});
				}
			} else {
				/** if no negative button just set the visibility to GONE */
				layout.findViewById(R.id.sdk_dialog_rating_negativeButton)
						.setVisibility(View.GONE);
			}

			/** Rating bar */
			mRatingBar = ((RatingBar) layout
					.findViewById(R.id.sdk_dialog_rating_ratingBarIndicator));
			mRatingBar.setRating(0);
			mRatingBar
					.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
						public void onRatingChanged(RatingBar ratingBar,
								float rating, boolean fromUser) {
							((Button) layout
									.findViewById(R.id.sdk_dialog_rating_positiveButton))
									.setEnabled(true);
							mMyRating = rating;
						}
					});

			dialog.setContentView(layout);
			return dialog;
		}

		/**
		 * To send the submitted rating to the Application
		 * 
		 * @return The submitted rating
		 */
		public float getSubmittedRating() {
			return mMyRating;
		}
	}
}
