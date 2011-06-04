package org.pocketcampus.plugin.library;

import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.shared.plugin.library.BookBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Adapter for the list of book. 
 * 
 * @author Florian
 *
 */
public class BookAdapter extends ArrayAdapter<BookBean> {

	private static List<BookBean> results_;
	private LayoutInflater layoutInflater_;
	private Context context_;
	private boolean moreResults_ = true;
	private boolean errorHappened_ = false;


	/**
	 * Adapter constructor
	 * @param context The Library plugin
	 * @param textViewResourceId Layout for a row in the list
	 * @param results List of books
	 */
	public BookAdapter(Context context, int textViewResourceId, List<BookBean> results) {
		super(context, textViewResourceId);
		
		results_ = results;
		layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		context_ = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		BookBean book = getItem(position);

		if(book == null) {
			// displays "load more" entry
			return getLoadMoreView();
		}

		View view = layoutInflater_.inflate(R.layout.library_book, null);
		
		TextView titleTextView = (TextView)view.findViewById(R.id.library_book_title);
		titleTextView.setText(book.getTitle());

		TextView titleAuthorView = (TextView)view.findViewById(R.id.library_book_author);
		titleAuthorView.setText(book.getAuthor() + " ("+book.getYear()+")");

		return view;
	}

	private View getLoadMoreView() {
		View view = layoutInflater_.inflate(R.layout.library_loadmore, null);
		TextView titleTextView = (TextView)view.findViewById(R.id.library_loadmore_message);
		
		if(!errorHappened_) {
			titleTextView.setText("Load more");
		} else {
			titleTextView.setText("Couldn't load next results. Retry?");
		}
		
		return view;
	}

	@Override
	public int getCount() {
		if(results_.size() == 0) {
			return 0;
		}

		if(moreResults_) {
			// adds one for the "load more" entry
			return results_.size() + 1;
		} else {
			return results_.size();
		}
	}

	@Override
	public BookBean getItem(int position) {
		// "load more" entry
		if(position == results_.size()) {
			return null;
		}

		return results_.get(position);
	}

	public void addResults(ArrayList<BookBean> results) {
		results_.addAll(results);
		notifyDataSetChanged();
	}

	public void setMoreResults(boolean more) {
		moreResults_  = more;
		notifyDataSetChanged();
	}

	public void setErrorHappened(boolean errorHappened) {
		errorHappened_ = errorHappened;
		notifyDataSetChanged();
	}
}












