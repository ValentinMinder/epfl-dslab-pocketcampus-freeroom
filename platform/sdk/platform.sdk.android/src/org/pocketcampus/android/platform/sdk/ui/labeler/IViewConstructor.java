package org.pocketcampus.android.platform.sdk.ui.labeler;

import android.content.Context;
import android.view.View;

public interface IViewConstructor {
	public View getNewView(Object currentObject, Context context,
			ILabeler<? extends Object> labeler, int position);
}
