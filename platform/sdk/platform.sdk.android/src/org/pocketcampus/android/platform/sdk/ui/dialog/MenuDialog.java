package org.pocketcampus.android.platform.sdk.ui.dialog;

import org.pocketcampus.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

public class MenuDialog extends Dialog {

	public MenuDialog(Context context, int theme) {
		super(context, theme);
	}

	public MenuDialog(Context context) {
		super(context);
	}

	public static class Builder {
		private Context mContext;
		private String mTitle;
		private String mDescription;
		private float mRating;
		private float mMyRating;
		private int mNbVotes;
		private View mContentView;
		private boolean mCanceledOnTouchOutside;

		private String mFirstButtonText;
		private String mSecondButtonText;
		private String mThirdButtonText;
		private DialogInterface.OnClickListener mFirstButtonClickListener;
		private DialogInterface.OnClickListener mSecondButtonClickListener;
		private DialogInterface.OnClickListener mThirdButtonClickListener;

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
		 * Sets the dialog description.
		 * @param description
		 * @return
		 */
		public Builder setDescription(String description) {
			mDescription = description;
			return this;
		}

		/**
		 * Sets the dialog description from a resource.
		 * @param description
		 * @return
		 */
		public Builder setDescription(int description) {
			mDescription = (String) mContext.getText(description);
			return this;
		}

		/**
		 * Sets the rating in the Rating Bar
		 * @param rating
		 * @return
		 */
		public Builder setRating(float rating, int nbVotes) {
			mNbVotes = nbVotes;
			mRating = rating;
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
		public Builder setFirstButton(int fristButtonText, OnClickListener listener) {
			mFirstButtonText = (String) mContext.getText(fristButtonText);
			mFirstButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the first button text and its listener
		 * @param firstButtonText
		 * @param listener
		 * @return
		 */
		public Builder setFirstButton(String firstButtonText, OnClickListener listener) {
			mFirstButtonText = firstButtonText;
			mFirstButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the second button text from a resource and its listener
		 * @param secondButtonText
		 * @param listener
		 * @return
		 */
		public Builder setSecondButton(int secondButtonText, DialogInterface.OnClickListener listener) {
			mSecondButtonText = (String) mContext.getText(secondButtonText);
			mSecondButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the second button text and its listener
		 * @param secondButtonText
		 * @param listener
		 * @return
		 */
		public Builder setSecondButton(String secondButtonText, DialogInterface.OnClickListener listener) {
			mSecondButtonText = secondButtonText;
			mSecondButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the third button text from a resource and its listener
		 * @param mThirdButtonText
		 * @param listener
		 * @return
		 */
		public Builder setThirdButton(int thirdButtonText, DialogInterface.OnClickListener listener) {
			mThirdButtonText = (String) mContext.getText(thirdButtonText);
			mThirdButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the third button text and its listener
		 * @param mThirdButtonText
		 * @param listener
		 * @return
		 */
		public Builder setThirdButton(String thirdButtonText, DialogInterface.OnClickListener listener) {
			mThirdButtonText = thirdButtonText;
			mThirdButtonClickListener = listener;
			return this;
		}

		public void setCanceledOnTouchOutside(boolean cancel) {
			mCanceledOnTouchOutside = cancel;
		}
		
		/**
		 * Since I don't see how to send another information than an int, I did a getter for the rating
		 */
		public float getSubmittedRating() {
			return mMyRating;
		}

		/**
		 * Creates the custom dialog.
		 */
		public MenuDialog create() {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			// dialog
			final MenuDialog dialog = new MenuDialog(mContext, R.style.Dialog);
			final View layout = inflater.inflate(R.layout.sdk_dialog_menu, null);
			dialog.addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			dialog.setCanceledOnTouchOutside(mCanceledOnTouchOutside);

			// title
			if(mTitle != null) {
				((TextView) layout.findViewById(R.id.sdk_dialog_menu_title)).setText(mTitle);
			} else {
				((LinearLayout) layout.findViewById(R.id.sdk_dialog_menu_title_layout)).setVisibility(View.GONE);
			}

			// set the first button (which confirms the rating)
			if (mFirstButtonText != null) {
				((Button) layout.findViewById(R.id.sdk_dialog_menu_firstButton))
				.setText(mFirstButtonText);
				((Button) layout.findViewById(R.id.sdk_dialog_menu_firstButton))
				.setEnabled(false);
				if (mFirstButtonClickListener != null) {
					((Button) layout.findViewById(R.id.sdk_dialog_menu_firstButton))
					.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							mFirstButtonClickListener.onClick(dialog, 
									DialogInterface.BUTTON1);
						}
					});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.sdk_dialog_menu_firstButton).setVisibility(
						View.GONE);
			}

			// set the second button
			if (mSecondButtonText != null) {
				((Button) layout.findViewById(R.id.sdk_dialog_menu_secondButton))
				.setText(mSecondButtonText);
				if (mSecondButtonClickListener != null) {
					((Button) layout.findViewById(R.id.sdk_dialog_menu_secondButton))
					.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							mSecondButtonClickListener.onClick(dialog, 
									DialogInterface.BUTTON2);
						}
					});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.sdk_dialog_menu_secondButton).setVisibility(
						View.GONE);
			}

			// set the third button
			if (mThirdButtonText != null) {
				((Button) layout.findViewById(R.id.sdk_dialog_menu_thirdButton))
				.setText(mThirdButtonText);
				if (mThirdButtonClickListener != null) {
					((Button) layout.findViewById(R.id.sdk_dialog_menu_thirdButton))
					.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							mThirdButtonClickListener.onClick(dialog, 
									DialogInterface.BUTTON3);
						}
					});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.sdk_dialog_menu_thirdButton).setVisibility(View.GONE);
			}

			// message
			if (mDescription != null) {
				((TextView) layout.findViewById(R.id.sdk_dialog_menu_description)).setText(mDescription);

			} else if (mContentView != null) {
				((LinearLayout) layout.findViewById(R.id.sdk_dialog_menu_description_layout))
				.removeAllViews();
				((LinearLayout) layout.findViewById(R.id.sdk_dialog_menu_description_layout))
				.addView(mContentView, 
						new LayoutParams(
								LayoutParams.WRAP_CONTENT, 
								LayoutParams.WRAP_CONTENT));
			}

			// rating bar and nb of votes
			((RatingBar) layout.findViewById(R.id.sdk_dialog_menu_ratingBarIndicator)).setRating(mRating);;
			((RatingBar) layout.findViewById(R.id.sdk_dialog_menu_ratingBarIndicator)).setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View arg0, MotionEvent rating) {
					if(rating.getAction() == MotionEvent.ACTION_UP) {
						mMyRating = ((RatingBar) layout.findViewById(R.id.sdk_dialog_menu_ratingBarIndicator))
								.getRating();
						((Button) layout.findViewById(R.id.sdk_dialog_menu_firstButton))
								.setEnabled(true);
						return true;
					}
					return false;
				}
			});
			
//			String nbVotes = "";
//
//			if(mNbVotes == 1) {
//				nbVotes = mNbVotes + " " + mContext.getResources().getString(R.string.nb_votes_singular);
//			} else {
//				nbVotes = mNbVotes + " " + mContext.getResources().getString(R.string.nb_votes_plural);
//			}
//
//			((TextView) layout.findViewById(R.id.sdk_dialog_menu_nbvotes)).setText(nbVotes);

			dialog.setContentView(layout);
			return dialog;
		}

	}
}
