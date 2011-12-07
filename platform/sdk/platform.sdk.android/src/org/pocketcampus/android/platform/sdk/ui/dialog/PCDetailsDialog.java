package org.pocketcampus.android.platform.sdk.ui.dialog;

import org.pocketcampus.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A Dialog to display an Element consisting of a title, and a list of details
 * about this Element. The inner class Builder sets all the parameters sent from
 * the Application and then creates the corresponding Dialog.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public class PCDetailsDialog extends Dialog {

	/**
	 * A constructor
	 * 
	 * @param context
	 *            The Application context
	 * @param theme
	 *            The int value of the theme we want to use for this dialog
	 */
	public PCDetailsDialog(Context context, int theme) {
		super(context, theme);
	}

	/**
	 * A constructor that simply calls the super constructor.
	 * 
	 * @param context
	 *            The Application context
	 */
	public PCDetailsDialog(Context context) {
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

		/** The List of details about the current Object */
		private ListView mDetailsList;

		/**
		 * The constructor
		 * 
		 * @param context
		 *            The Application context
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
		 * Sets the outside touch action (dismiss or not)
		 * 
		 * @param cancel
		 */
		public void setCanceledOnTouchOutside(boolean cancel) {
			mCanceledOnTouchOutside = cancel;
		}

		/**
		 * Creates the custom dialog.
		 */
		public PCDetailsDialog create() {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			/** Dialog */
			final PCDetailsDialog dialog = new PCDetailsDialog(mContext,
					R.style.Dialog);
			final View layout = inflater.inflate(R.layout.sdk_dialog_details,
					null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			dialog.setCanceledOnTouchOutside(mCanceledOnTouchOutside);

			/** Title */
			if (mTitle != null) {
				((TextView) layout.findViewById(R.id.sdk_dialog_details_title))
						.setText(mTitle);
			} else {
				((LinearLayout) layout
						.findViewById(R.id.sdk_dialog_details_title_layout))
						.setVisibility(View.GONE);
			}

			dialog.setContentView(layout);
			return dialog;
		}

	}
}
