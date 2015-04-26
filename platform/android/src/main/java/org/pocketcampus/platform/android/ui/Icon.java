package org.pocketcampus.platform.android.ui;

import org.pocketcampus.R;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;

/**
 * Represents an icon.
 * 
 * @author Florian
 * @license 
 */

public class Icon {
	public int mResourceId;//XXX
	
	private final static int DEFAULT_RESOURCE_ID = R.drawable.sdk_missing_icon;
	
	public Icon(int resourceId) {
		mResourceId = resourceId;
	}

	public Drawable getDrawable(Context ctx) {
		Drawable drawable = ctx.getResources().getDrawable(DEFAULT_RESOURCE_ID);
		
		if(mResourceId == 0) {
			return drawable;
		}
		
		try {
			drawable = ctx.getResources().getDrawable(mResourceId);
			
		} catch (NotFoundException e) {
			System.out.println("Icon resource not found! " + mResourceId);
			// This shouldn't happen, resourceId should either be a valid id or 0.
			// We'll keep the default.
		}
		
		return drawable;
	}
	
	public static Drawable getDefaultDrawable(Context ctx) {
		return ctx.getResources().getDrawable(DEFAULT_RESOURCE_ID);
	}
}
















