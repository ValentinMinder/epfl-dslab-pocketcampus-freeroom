package org.pocketcampus.plugin.library;

import org.pocketcampus.R;
import org.pocketcampus.plugin.logging.Tracker;
import org.pocketcampus.shared.plugin.library.BookBean;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class BookDetailDialog extends Dialog {

	public BookDetailDialog(Context context, BookBean book) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setCanceledOnTouchOutside(true);
		
		setContentView(R.layout.library_details_dialog);
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		
		Tracker.getInstance().trackPageView("library/detail");
		
		book_ = book;
		title_ = (TextView) findViewById(R.id.library_dialog_title);
		author_ = (TextView) findViewById(R.id.library_dialog_author);
		year_ = (TextView) findViewById(R.id.library_dialog_year);
		
		setDialogContent();
	}


	private TextView title_;
	private BookBean book_;
	private TextView author_;
	private TextView year_;
	
	private void setDialogContent() {
		title_.setText(book_.getTitle());
		author_.setText(book_.getAuthor());
		year_.setText(book_.getYear());
	}
	
}
