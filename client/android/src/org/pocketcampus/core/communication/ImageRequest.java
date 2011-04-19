package org.pocketcampus.core.communication;

import android.graphics.drawable.Drawable;

public abstract class ImageRequest extends Request<Drawable> {
	
	protected int expirationDelay() {
		// default to 1 hour
		return 60 * 60 * 1;
	}
	
	@Override
	protected int timeoutDelay() {
		// default 10 seconds
		return 10;
	}
	
	protected final String getUrl() {
		return serverUrl_ + pluginInfo_.getId() + "/image/" + command_;
	}
	
	@Override
	protected final Drawable loadFromServer(String url) {
		Drawable result = null;
		
		try {
			result = Drawable.createFromStream(((java.io.InputStream)new java.net.URL(url).getContent()), url);
		} catch (Exception e) {
			exception_ = e;
		}
		
		doInBackgroundThread(result);
		
		return result;
	}
}



















