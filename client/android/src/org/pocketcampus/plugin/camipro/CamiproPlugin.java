package org.pocketcampus.plugin.camipro;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class CamiproPlugin extends PluginBase {
	
	private WebView mWebView_;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.camipro_main);
		setupActionBar(true);
		
		mWebView_ = (WebView) findViewById(R.id.camipro_webview);
	    mWebView_.setWebViewClient(new HelloWebViewClient());
	    mWebView_.getSettings().setJavaScriptEnabled(true);
	    mWebView_.loadUrl("http://camipro.epfl.ch/");
	    mWebView_.scrollBy(0, 20);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView_.canGoBack()) {
	        mWebView_.goBack();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	private class HelloWebViewClient extends WebViewClient {
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        view.loadUrl(url);
	        return true;
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
