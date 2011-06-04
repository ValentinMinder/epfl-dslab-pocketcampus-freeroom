package org.pocketcampus.plugin.library;

import java.lang.reflect.Type;
import java.util.ArrayList;
import org.pocketcampus.R;
import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.parser.Json;
import org.pocketcampus.core.parser.JsonException;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.plugin.logging.Tracker;
import org.pocketcampus.shared.plugin.library.BookBean;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Library plugin.
 * Allows user to search for books available at a library using the NEBIS website.
 * Data is scraped from HTML.
 * 
 * @status WIP
 * @author Florian
 *
 */
public class LibraryPlugin extends PluginBase {
	private ActionBar actionBar_;
	private EditText searchInput_;
	private int progressCount_;
	private ListView resultListView_;
	private Context context_;
	private TextView centeredMsg_;
	private int resultPageNb_;
	private BookAdapter bookAdapter_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.library_main);
		actionBar_ = (ActionBar) findViewById(R.id.actionbar);
		setupActionBar(true);

		Tracker.getInstance().trackPageView("library/home");

		context_ = this;
		searchInput_ = (EditText) findViewById(R.id.library_search_input);
		centeredMsg_ = (TextView) findViewById(R.id.library_centered_msg);

		// Search Button
		Button searchButton = (Button) findViewById(R.id.library_search_button);
		OnClickListener searchOnClickListener = new OnClickListener() {
			@Override
			public void onClick(View clicked) {
				resultPageNb_ = 1;
				search();
			}
		};

		searchButton.setOnClickListener(searchOnClickListener );

		// Result ListView
		resultListView_ = (ListView) findViewById(R.id.library_mainlist);
		resultListView_.setAdapter(new BookAdapter(this, R.layout.library_book, new ArrayList<BookBean>()));

		OnItemClickListener resultOnClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				BookAdapter adapter = (BookAdapter)parent.getAdapter();
				BookBean book =  (BookBean)adapter.getItem(position);

				if(book != null) {
					displayBook(book);
				} else {
					loadMore(view);
				}
			}
		};

		resultListView_.setOnItemClickListener(resultOnClickListener);
	}

	private void loadMore(View view) {
		resultPageNb_++;
		System.out.println("Loading more! page " + resultPageNb_);
		
		TextView msgTextView = (TextView)view.findViewById(R.id.library_loadmore_message);
		msgTextView.setText("Loading...");
		
		View spinnerView = (View)view.findViewById(R.id.library_loadmore_spinner);
		spinnerView.setVisibility(View.VISIBLE);
		
		search();
	}
	
	private void displayBook(BookBean book) {
		BookDetailDialog dialog = new BookDetailDialog(this, book);
		dialog.show();
	}

	@Override
	public PluginInfo getPluginInfo() {
		return new LibraryInfo();
	}

	@Override
	public PluginPreference getPluginPreference() {
		return null;
	}

	private void search() {
		String searchTerms = searchInput_.getText().toString();

		if(searchTerms.equals("")) {
			return;
		}
		
		// only display for the initial results
		if(isNewSearch()) {
			showMessage("Searching...");
		}
		
		incrementProgressCounter();
		hideKeyboard();

		RequestParameters params = new RequestParameters();
		params.addParameter("terms", searchTerms);
		params.addParameter("pageNumber", ""+resultPageNb_);
		getRequestHandler().execute(new BookSearchRequest(), "search", params);

	}

	private boolean isNewSearch() {
		return (resultPageNb_ == 1);
	}

	private void hideKeyboard() {
		InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(searchInput_.getWindowToken(), 0);
	}

	private class BookSearchRequest extends DataRequest {
		ArrayList<BookBean> results = null;

		@Override
		protected int expirationDelay() {
			// nebis server is freaking slow
			return 15;
		}

		@Override
		protected void doInBackgroundThread(String result) {
			Type BookResultType = new TypeToken<ArrayList<BookBean>>(){}.getType();

			try {
				results = Json.fromJson(result, BookResultType);
			} catch (JsonException e) {
				return;
			}
		}

		@Override
		protected void doInUiThread(String ignored) {
			decrementProgressCounter();

			if(results != null) {
				if(results.size() == 0) {
					if(isNewSearch()) {
						showMessage("No result found.");
						return;
						
					} else {
						bookAdapter_.setMoreResults(false);
						return;
					}
				}
				
				hideMessage();
				
				if(isNewSearch()) {
					bookAdapter_ = new BookAdapter(context_, R.layout.library_book, results);
					resultListView_.setAdapter(bookAdapter_);
					
				} else {
					bookAdapter_.addResults(results);
					bookAdapter_.setErrorHappened(false);
				}
				
				if(results.size() < 10) {
					bookAdapter_.setMoreResults(false);
				}
				
			} else {
				
				if(isNewSearch()) {
					showMessage("An error occured, please try again later.");
				} else {
					bookAdapter_.setErrorHappened(true);
				}
			}
		}

		@Override
		protected void onCancelled() {
			decrementProgressCounter();
			showMessage("Couldn't load your results.");
		}
	}

	private void showMessage(String msg) {
		resultListView_.setVisibility(View.GONE);
		centeredMsg_.setVisibility(View.VISIBLE);
		centeredMsg_.setText(msg);
	}

	private void hideMessage() {
		resultListView_.setVisibility(View.VISIBLE);
		centeredMsg_.setVisibility(View.GONE);
	}

	/**
	 * Increments the progressCounter. It displays the progress bar
	 * of the action bar. It allows several parallel threads doing background
	 * work.
	 */
	private synchronized void incrementProgressCounter() {
		progressCount_++;
		actionBar_.setProgressBarVisibility(View.VISIBLE);
	}

	/**
	 * Decrements the progressCounter. Called when a thread has finished
	 * doing some background work.
	 */
	private synchronized void decrementProgressCounter() {
		progressCount_--;
		if(progressCount_ < 0) { //Should never happen!
			Log.e(this.getClass().toString(), "ERROR progresscount is negative!");
		}

		if(progressCount_ <= 0) {
			actionBar_.setProgressBarVisibility(View.GONE);
		}
	}
}















