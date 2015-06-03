package org.pocketcampus.platform.android.ui.dialog;

import org.pocketcampus.platform.android.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * A Dialog to display an object consisting of a title, a description, a rating
 * bar that the user can use to rate the element and 1 to 3 customizable
 * buttons. The inner class <code>Builder</code> sets all the parameters sent
 * from the application and then creates the corresponding
 * <code>MenuDialog</code>.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public class MenuDialog extends Dialog {

	/**
	 * Class constructor calling the super constructor.
	 * 
	 * @param context
	 *            The application context.
	 * @param theme
	 *            The integer value of the theme we want to use for this dialog.
	 */
	public MenuDialog(Context context, int theme) {
		super(context, theme);
	}

	/**
	 * CLass constructor calling the super constructor.
	 * 
	 * @param context
	 *            The application context.
	 */
	public MenuDialog(Context context) {
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
		/** The content view. */
		private View mContentView;
		/**
		 * <code>Boolean</code> deciding whether clicking outside the dialog
		 * dismisses it or not.
		 */
		private boolean mCanceledOnTouchOutside;
		/** The object's title to be displayed in the dialog. */
		private String mTitle;
		/** The object's description to be displayed in the dialog. */
		private String mDescription;
		/** The object's rating. */
		private float mRating;
		/** The rating the user can set in the dialog. */
		private float mMyRating;
		/** <code>Boolean</code> saying whether the user can still vote. */
		private boolean mHasVoted;
		/** The first button's text. */
		private String mFirstButtonText;
		/** The second button's text. */
		private String mSecondButtonText;
		/** The third button's text. */
		private String mThirdButtonText;
		/** The first button's click listener. */
		private DialogInterface.OnClickListener mFirstButtonClickListener;
		/** The second button's click listener. */
		private DialogInterface.OnClickListener mSecondButtonClickListener;
		/** The third button's click listener. */
		private DialogInterface.OnClickListener mThirdButtonClickListener;

		/**
		 * Class constructor instantiating the context.
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
		 * Sets the dialog description.
		 * 
		 * @param description
		 *            The dialog's description.
		 * @return this The <code>Builder</code> instance.
		 */
		public Builder setDescription(String description) {
			mDescription = description;
			return this;
		}

		/**
		 * Sets the dialog description from a resource.
		 * 
		 * @param description
		 *            The dialog's description resource value.
		 * @return this The <code>Builder</code> instance.
		 */
		public Builder setDescription(int description) {
			mDescription = (String) mContext.getText(description);
			return this;
		}

		/**
		 * Sets the current rating to be displayed in the action bar.
		 * 
		 * @param isVisible
		 *            <code>Boolean</code> saying if the rating bar is visible
		 *            or not.
		 * @param rating
		 *            The rating value.
		 * @param nbVotes
		 *            The number of votes.
		 * @return this The <code>Builder</code> instance.
		 */
		public Builder setRating(boolean isVisible, float rating, int nbVotes) {
			mHasVoted = isVisible;
			mRating = rating;
			return this;
		}

		/**
		 * Sets a custom content view for the dialog. Only used if no
		 * description is set.
		 * 
		 * @param view
		 *            The <code>ContentView</code> for the dialog.
		 * @return this The <code>Builder</code> instance.
		 */
		public Builder setContentView(View view) {
			mContentView = view;
			return this;
		}

		/**
		 * Sets the first button text from a resource and its listener.
		 * 
		 * @param fristButtonText
		 *            The text resource value for the first button.
		 * @param listener
		 *            The listener for the first button.
		 * @return this The <code>Builder</code> instance.
		 */
		public Builder setFirstButton(int fristButtonText,
				OnClickListener listener) {
			mFirstButtonText = (String) mContext.getText(fristButtonText);
			mFirstButtonClickListener = listener;
			return this;
		}

		/**
		 * Sets the first button text and its listener.
		 * 
		 * @param firstButtonText
		 *            The text for the first button.
		 * @param listener
		 *            The listener for the first button.
		 * @return this The <code>Builder</code> instance.
		 */
		public Builder setFirstButton(String firstButtonText,
				OnClickListener listener) {
			mFirstButtonText = firstButtonText;
			mFirstButtonClickListener = listener;
			return this;
		}

		/**
		 * Sets the second button text from a resource and its listener.
		 * 
		 * @param secondButtonText
		 *            The text resource value for the second button.
		 * @param listener
		 *            The listener for the second button.
		 * @return this The <code>Builder</code> instance.
		 */
		public Builder setSecondButton(int secondButtonText,
				DialogInterface.OnClickListener listener) {
			mSecondButtonText = (String) mContext.getText(secondButtonText);
			mSecondButtonClickListener = listener;
			return this;
		}

		/**
		 * Sets the second button text and its listener.
		 * 
		 * @param secondButtonText
		 *            The text for the second button.
		 * @param listener
		 *            The listener for the second button.
		 * @return this The <code>Builder</code> instance.
		 */
		public Builder setSecondButton(String secondButtonText,
				DialogInterface.OnClickListener listener) {
			mSecondButtonText = secondButtonText;
			mSecondButtonClickListener = listener;
			return this;
		}

		/**
		 * Sets the third button text from a resource and its listener.
		 * 
		 * @param mThirdButtonText
		 *            The text resource value for the third button.
		 * @param listener
		 *            The listener for the third button.
		 * @return this The <code>Builder</code> instance.
		 */
		public Builder setThirdButton(int thirdButtonText,
				DialogInterface.OnClickListener listener) {
			mThirdButtonText = (String) mContext.getText(thirdButtonText);
			mThirdButtonClickListener = listener;
			return this;
		}

		/**
		 * Sets the third button text and its listener.
		 * 
		 * @param mThirdButtonText
		 *            The text for the third button.
		 * @param listener
		 *            The listener for the third button.
		 * @return this The <code>Builder</code> instance.
		 */
		public Builder setThirdButton(String thirdButtonText,
				DialogInterface.OnClickListener listener) {
			mThirdButtonText = thirdButtonText;
			mThirdButtonClickListener = listener;
			return this;
		}

		/**
		 * Sets the outside touch action (dismiss the dialog or not).
		 * 
		 * @param cancel
		 *            A <code>Boolean</code> saying if the dialog is dismissed when
		 *            touching outside.
		 */
		public void setCanceledOnTouchOutside(boolean cancel) {
			mCanceledOnTouchOutside = cancel;
		}

		/**
		 * Sends the rating set by the user.
		 * 
		 * @return mMyRating The rating set by the user.
		 */
		public float getSubmittedRating() {
			return mMyRating;
		}

		/**
		 * Creates the custom dialog. If some values or texts are not set, then
		 * the corresponding element is not visible in the dialog.
		 */
		public MenuDialog create() {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			// Dialog
			final MenuDialog dialog = new MenuDialog(mContext, R.style.Dialog);
			final View layout = inflater
					.inflate(R.layout.sdk_dialog_menu, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			dialog.setCanceledOnTouchOutside(mCanceledOnTouchOutside);

			// Title
			if (mTitle != null) {
				((TextView) layout.findViewById(R.id.sdk_dialog_menu_title))
						.setText(mTitle);
			} else {
				((LinearLayout) layout
						.findViewById(R.id.sdk_dialog_menu_title_layout))
						.setVisibility(View.GONE);
			}

			// First button and its Listener
			if (mFirstButtonText != null) {
				((Button) layout.findViewById(R.id.sdk_dialog_menu_firstButton))
						.setText(mFirstButtonText);
				((Button) layout.findViewById(R.id.sdk_dialog_menu_firstButton))
						.setEnabled(false);
				if (mFirstButtonClickListener != null) {
					((Button) layout
							.findViewById(R.id.sdk_dialog_menu_firstButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									mFirstButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON1);
								}
							});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.sdk_dialog_menu_firstButton)
						.setVisibility(View.GONE);
			}

			// Second button and its Listener
			if (mSecondButtonText != null) {
				((Button) layout
						.findViewById(R.id.sdk_dialog_menu_secondButton))
						.setText(mSecondButtonText);
				if (mSecondButtonClickListener != null) {
					((Button) layout
							.findViewById(R.id.sdk_dialog_menu_secondButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									mSecondButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON2);
								}
							});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.sdk_dialog_menu_secondButton)
						.setVisibility(View.GONE);
			}

			// Third button and its Listener
			if (mThirdButtonText != null) {
				((Button) layout.findViewById(R.id.sdk_dialog_menu_thirdButton))
						.setText(mThirdButtonText);
				if (mThirdButtonClickListener != null) {
					((Button) layout
							.findViewById(R.id.sdk_dialog_menu_thirdButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									mThirdButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON3);
								}
							});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.sdk_dialog_menu_thirdButton)
						.setVisibility(View.GONE);
			}

			// Description
			if (mDescription != null) {
				((TextView) layout
						.findViewById(R.id.sdk_dialog_menu_description))
						.setText(mDescription);

			} else if (mContentView != null) {
				((LinearLayout) layout
						.findViewById(R.id.sdk_dialog_menu_description_layout))
						.removeAllViews();
				((LinearLayout) layout
						.findViewById(R.id.sdk_dialog_menu_description_layout))
						.addView(mContentView, new LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT));
			}

			// Rating Bar
			RatingBar ratingBar = ((RatingBar) layout
					.findViewById(R.id.sdk_dialog_menu_ratingBarIndicator));
			mHasVoted = false;
			if (!mHasVoted) {
				ratingBar.setRating(mRating);
				ratingBar.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View arg0, MotionEvent rating) {
						if (rating.getAction() == MotionEvent.ACTION_UP) {
							mMyRating = ((RatingBar) layout
									.findViewById(R.id.sdk_dialog_menu_ratingBarIndicator))
									.getRating();
							((Button) layout
									.findViewById(R.id.sdk_dialog_menu_firstButton))
									.setEnabled(true);
							return true;
						}
						return false;
					}
				});
			} 
//			else {
//				ratingBar.setVisibility(View.GONE);
//			}
			dialog.setContentView(layout);
			return dialog;
		}

	}
}
