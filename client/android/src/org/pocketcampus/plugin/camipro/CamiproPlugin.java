package org.pocketcampus.plugin.camipro;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * PluginBase class for the Camipro plugin.
 * This is a first version to test if it is worth coding a plugin or just putting a WebView
 * that shows the mobile version of the Camipro website. 
 * 
 * @status WIP
 * 
 * @author Jonas
 *
 */
public class CamiproPlugin extends PluginBase {
	private WebView webView_;
	private ActionBar actionBar_;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camipro_main);
		setupActionBar(true);
		
		actionBar_ = (ActionBar) findViewById(R.id.actionbar);
		
		setupWebview();
	}
	
	private void setupWebview() {
		webView_ = (WebView) findViewById(R.id.camipro_webview);
	    webView_.setWebViewClient(new HelloWebViewClient());
	    webView_.getSettings().setJavaScriptEnabled(true);
	    
	    String page = getResources().getString(R.string.camipro_page);
	    webView_.loadUrl(getResources().getString(R.string.camipro_website_url) + page);
	}

	/**
	 * Allows to handle the back button on the browser
	 * Otherwise do the normal behavior
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// -2 fixes the bug where you had to press back twice to go back to mainscreen:
		// if only 1 page in history then it's the login page, which will redirect to the balance page...
	    if ((keyCode == KeyEvent.KEYCODE_BACK) && webView_.canGoBackOrForward(-2)) {
	        webView_.goBackOrForward(-2);
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * Custom WebBrowser to allow navigation inside the app
	 * (Other wise it launches an Intent on the first click and
	 * the user goes to the regular browser)
	 */
	private class HelloWebViewClient extends WebViewClient {
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        view.loadUrl(url);
	        return true;
	    }

		@Override
		public void onPageFinished(WebView view, String url) {

			actionBar_.setProgressBarVisibility(View.GONE);
			
			super.onPageFinished(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {

			actionBar_.setProgressBarVisibility(View.VISIBLE);
			
			super.onPageStarted(view, url, favicon);
		}
	    
	}

	@Override
	public PluginInfo getPluginInfo() {
		return new CamiproInfo();
	}

	@Override
	public PluginPreference getPluginPreference() {
		return null;
	}

}
