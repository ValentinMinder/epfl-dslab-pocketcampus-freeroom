package org.pocketcampus.core.plugin;

import org.pocketcampus.R;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * Represents an icon.
 * 
 * @status working
 * @author florian
 * @license 
 */

public class Icon {
	private int resourceId_;
	
	private final static int DEFAULT_RESOURCE_ID = R.drawable.missing;
	
	public Icon(int resourceId) {
		resourceId_ = resourceId;
	}

	public Drawable getDrawable(Context ctx) {
		if(resourceId_ == -1) {
			return ctx.getResources().getDrawable(DEFAULT_RESOURCE_ID);
		}
		
		return ctx.getResources().getDrawable(resourceId_);
	}
	
	public static Drawable getDefaultDrawable(Context ctx) {
		return ctx.getResources().getDrawable(DEFAULT_RESOURCE_ID);
	}
}
