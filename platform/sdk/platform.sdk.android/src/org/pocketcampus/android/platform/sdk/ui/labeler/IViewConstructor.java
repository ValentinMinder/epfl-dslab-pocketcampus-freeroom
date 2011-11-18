package org.pocketcampus.android.platform.sdk.ui.labeler;

import android.content.Context;
import android.view.View;

/**
 * 
 * @author elodie <elodienilane.triponez@epfl.ch>
 *
 */
public interface IViewConstructor {
	public View getNewView(Object currentObject, Context context,
			ILabeler<? extends Object> labeler, int position);
}
