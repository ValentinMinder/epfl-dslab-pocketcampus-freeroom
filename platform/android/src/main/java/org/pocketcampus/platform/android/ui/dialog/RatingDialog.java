package org.pocketcampus.platform.android.ui.dialog;

import org.pocketcampus.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

/**
 * A dialog to display a <code>RatingBar</code> that the user can set, along
 * with a confirm button and a cancel button. The inner class
 * <code>Builder</code> sets all the parameters sent from the application and
 * then creates the corresponding <code>RatingDialog</code>.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public class RatingDialog extends Dialog {

	/**
	 * Class constructor calling the super constructor.
	 * 
	 * @param context
	 *            The application context.
	 * @param theme
	 *            The integer value of the theme we want to use.
	 */
	public RatingDialog(Context context, int theme) {
		super(context, theme);
	}

	/**
	 * A constructor calling the super constructor.
	 * 
	 * @param context
	 *            The application context.
	 */
	public RatingDialog(Context context) {
		super(context);
	}

	/**
	 * Inner class <code>Builder</code>. Sets all the dialog parameters and
	 * creates it.
	 * 
	 * @author Oriane <oriane.rodriguez@epfl.ch>
	 */
	public static class Builder {
		/** The application context. */
		private Context mContext;
		/**
		 * <code>Boolean</code> deciding whether clicking outside the dialog
		 * dismisses it or not.
		 */
		private boolean mCanceledOnTouchOutside;
		/** The element's title to be displayed in the dialog. */
		private String mTitle;
		/** The rating bar displayed in the dialog. */
		private RatingBar mRatingBar;
		/** The rating value that the user can set in the dialog. */
		private float mMyRating;
		/** The dialog's positive button. */
		private Button mPositiveButton;
		/** The dialog's negative button. */
		private Button mNegativeButton;
		/** The positive button's text. */
		private String mPositiveButtonText;
		/** the negative button's text. */
		private String mNegativeButtonText;
		/** The positive button's click listener. */
		private DialogInterface.OnClickListener mPositiveButtonClickListener;
		/** The negative button's click listener. */
		private DialogInterface.OnClickListener mNegativeButtonClickListener;

		/**
		 * The constructor instantiates the context.
		 * 
		 * @param context
		 *            The application context.
		 * @throws IllegalArgumentException
		 *             Thrown if the context is null.
		 */
		public Builder(Context context) {
			if (context == null) {
				new IllegalArgumentException("Context cannot be null!");
			}
			mContext = context;
		}

		/**
		 * Sets the dialog title.
		 * 
		 * @param title
		 *            The dialog's title.
		 * @return this The <code>Builder</code> instance.
		 */
		public Builder setTitle(String title) {
			mTitle = title;
			return this;
		}

		/**
		 * Sets the dialog title from a resource.
		 * 
		 * @param title
		 *            The dialog's title resource value.
		 * @return this The <code>Builder</code> instance.
		 */
		public Builder setTitle(int title) {
			mTitle = (String) mContext.getText(title);
			return this;
		}

		/**
		 * Sets the positive button text from a resource and its listener.
		 * 
		 * @param positiveButtonText
		 *            The resource value of the positive button.
		 * @param listener
		 *            The listener for the positive button.
		 * @return this The <code>Builder</code> instance.
		 */
		public Builder setOkButton(int positiveButtonText,
				DialogInterface.OnClickListener listener) {
			mPositiveButtonText = (String) mContext.getText(positiveButtonText);
			mPositiveButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the positive button text and its listener.
		 * 
		 * @param positiveButtonText
		 *            The text of the positive button.
		 * @param listener
		 *            The listener for the positive button.
		 * @return this The <code>Builder</code> instance.
		 */
		public Builder setOkButton(String positiveButtonText,
				DialogInterface.OnClickListener listener) {
			mPositiveButtonText = positiveButtonText;
			mPositiveButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the negative button text from a resource and its listener.
		 * 
		 * @param negativeButtonText
		 *            The resource value of the negative button.
		 * @param listener
		 *            The listener for the negative button.
		 * @return this The <code>Builder</code> instance.
		 */
		public Builder setCancelButton(int negativeButtonText,
				DialogInterface.OnClickListener listener) {
			mNegativeButtonText = (String) mContext.getText(negativeButtonText);
			mNegativeButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the negative button text and its listener.
		 * 
		 * @param negativeButtonText
		 *            The text of the negative button.
		 * @param listener
		 *            The listener for the negative button.
		 * @return this The <code>Builder</code> instance.
		 */
		public Builder setCancelButton(String negativeButtonText,
				DialogInterface.OnClickListener listener) {
			mNegativeButtonText = negativeButtonText;
			mNegativeButtonClickListener = listener;
			return this;
		}

		/**
		 * Sets the outside touch action (dismiss the dialog or not).
		 * 
		 * @param cancel
		 *            A <code>Boolean</code> saying if the dialog is dismissed
		 *            when touching outside.
		 */
		public void setCanceledOnTouchOutside(boolean cancel) {
			mCanceledOnTouchOutside = cancel;
		}

		/**
		 * Creates the custom dialog. If some values or texts are not set, then
		 * the corresponding element is not visible in the dialog.
		 */
		public RatingDialog create() {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			// Dialog
			final RatingDialog dialog = new RatingDialog(mContext,
					R.style.Dialog);
			final View layout = inflater.inflate(R.layout.sdk_dialog_rating,
					null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			dialog.setCanceledOnTouchOutside(mCanceledOnTouchOutside);

			// Title
			if (mTitle != null) {
				((TextView) layout.findViewById(R.id.sdk_dialog_rating_title))
						.setText(mTitle);
			} else {
				((LinearLayout) layout
						.findViewById(R.id.sdk_dialog_rating_title_layout))
						.setVisibility(View.GONE);
			}

			// Positive button and its listener
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
				// If no positive button just set the visibility to GONE
				layout.findViewById(R.id.sdk_dialog_rating_positiveButton)
						.setVisibility(View.GONE);
			}

			// Negative button and its listener
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
			} 
//			else {
//				// If no negative button just set the visibility to GONE
//				layout.findViewById(R.id.sdk_dialog_rating_negativeButton)
//						.setVisibility(View.GONE);
//			}

			// Rating bar
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
		 * Sends the submitted rating to the application.
		 * 
		 * @return mMyRating The submitted rating.
		 */
		public float getSubmittedRating() {
			return mMyRating;
		}
	}
}
