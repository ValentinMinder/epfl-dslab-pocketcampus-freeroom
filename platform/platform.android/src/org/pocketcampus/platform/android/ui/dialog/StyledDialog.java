package org.pocketcampus.platform.android.ui.dialog;

import org.pocketcampus.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Florian <florian.laurent@epfl.ch>
 */
public class StyledDialog extends Dialog {

	public StyledDialog(Context context, int theme) {
		super(context, theme);
	}

	public StyledDialog(Context context) {
		super(context);
	}

	public static class Builder {
		private Context mContext;
		private CharSequence mTitle;
		private CharSequence mMessage;
		private View mContentView;
		private Integer mContentResId;
		private boolean mCanceledOnTouchOutside;

		private String mPositiveButtonText;
		private String mNegativeButtonText;
		private String mNeutralButtonText;
		private DialogInterface.OnClickListener mPositiveButtonClickListener;
		private DialogInterface.OnClickListener mNegativeButtonClickListener;
		private DialogInterface.OnClickListener mNeutralButtonClickListener;

		public Builder(Context context) {
			mContext = context;
		}

		/**
		 * Sets the dialog message.
		 * @param message
		 * @return
		 */
		public Builder setMessage(CharSequence message) {
			mMessage = message;
			return this;
		}

		/**
		 * Sets the dialog message from a resource.
		 * @param message
		 * @return
		 */
		public Builder setMessage(int message) {
			mMessage = mContext.getText(message);
			return this;
		}

		/**
		 * Sets the dialog title.
		 * @param title
		 * @return
		 */
		public Builder setTitle(CharSequence title) {
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
			mContentResId = null;
			return this;
		}
		
		/**
		 * Sets a custom content view for the Dialog, inflated from the specified layout resource.
		 * @param ressource ID for an XML resource to load.
		 * @return an instance of this builder, for methods chaining.
		 */
		public Builder setContentView(int ressource) {
			mContentView = null;
			mContentResId = ressource;
			return this;
		}

		/**
		 * Sets the positive button text from a resource and its listener
		 * @param positiveButtonText
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(int positiveButtonText, DialogInterface.OnClickListener listener) {
			mPositiveButtonText = (String) mContext.getText(positiveButtonText);
			mPositiveButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the positive button text and its listener
		 * @param positiveButtonText
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(String positiveButtonText, DialogInterface.OnClickListener listener) {
			mPositiveButtonText = positiveButtonText;
			mPositiveButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the negative button text from a resource and its listener
		 * @param negativeButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNegativeButton(int negativeButtonText, DialogInterface.OnClickListener listener) {
			mNegativeButtonText = (String) mContext.getText(negativeButtonText);
			mNegativeButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the negative button text and its listener
		 * @param negativeButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNegativeButton(String negativeButtonText, DialogInterface.OnClickListener listener) {
			mNegativeButtonText = negativeButtonText;
			mNegativeButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the neutral button text from a resource and its listener
		 * @param mNeutralButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNeutralButton(int neutralButtonText, DialogInterface.OnClickListener listener) {
			mNeutralButtonText = (String) mContext.getText(neutralButtonText);
			mNeutralButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the neutral button text and its listener
		 * @param mNeutralButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNeutralButton(String negativeButtonText, DialogInterface.OnClickListener listener) {
			mNeutralButtonText = negativeButtonText;
			mNeutralButtonClickListener = listener;
			return this;
		}

		public Builder setCanceledOnTouchOutside(boolean cancel) {
			mCanceledOnTouchOutside = cancel;
			return this;
		}

		/**
		 * Creates the custom dialog.
		 */
		public StyledDialog create() {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			// dialog
			final StyledDialog dialog = new StyledDialog(mContext, R.style.Dialog);
			View layout = inflater.inflate(R.layout.sdk_styled_dialog, null);
			dialog.addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			dialog.setCanceledOnTouchOutside(mCanceledOnTouchOutside);

			// title
			if(mTitle != null) {
				((TextView) layout.findViewById(R.id.title)).setText(mTitle);
			} else {
				((LinearLayout) layout.findViewById(R.id.title_layout)).setVisibility(View.GONE);
			}

			// TODO refactor

			// set the confirm button
			if (mPositiveButtonText != null) {
				((Button) layout.findViewById(R.id.positiveButton))
				.setText(mPositiveButtonText);
				if (mPositiveButtonClickListener != null) {
					((Button) layout.findViewById(R.id.positiveButton))
					.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							mPositiveButtonClickListener.onClick(
									dialog, 
									DialogInterface.BUTTON_POSITIVE);
						}
					});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.positiveButton).setVisibility(
						View.GONE);
			}

			// set the cancel button
			if (mNegativeButtonText != null) {
				((Button) layout.findViewById(R.id.negativeButton))
				.setText(mNegativeButtonText);
				if (mNegativeButtonClickListener != null) {
					((Button) layout.findViewById(R.id.negativeButton))
					.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							mNegativeButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
						}
					});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.negativeButton).setVisibility(
						View.GONE);
			}

			// set the neutral button
			if (mNeutralButtonText != null) {
				dialog.setCanceledOnTouchOutside(true);

				((Button) layout.findViewById(R.id.neutralButton))
				.setText(mNeutralButtonText);
				if (mNeutralButtonClickListener != null) {
					((Button) layout.findViewById(R.id.neutralButton))
					.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							mNeutralButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEUTRAL);
						}
					});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.neutralButton).setVisibility(View.GONE);
			}
			
			// Content
			if (mMessage != null) {
				((TextView) layout.findViewById(R.id.message)).setText(mMessage);
			}
			else if (mContentView != null) {
				((LinearLayout) layout.findViewById(R.id.content))
				.removeAllViews();
				((LinearLayout) layout.findViewById(R.id.content))
				.addView(mContentView, 
						new LayoutParams(
								LayoutParams.FILL_PARENT, 
								LayoutParams.WRAP_CONTENT));
			}
			else if (mContentResId != null) {
				((LinearLayout) layout.findViewById(R.id.content)).removeAllViews();
				LayoutInflater.from(mContext).inflate(mContentResId,
						(ViewGroup) layout.findViewById(R.id.content));
			}

			dialog.setContentView(layout);
			return dialog;
		}

	}
}
