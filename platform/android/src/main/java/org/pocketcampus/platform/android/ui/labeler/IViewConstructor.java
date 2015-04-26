package org.pocketcampus.platform.android.ui.labeler;

import android.content.Context;
import android.view.View;

/**
 * 
 * Interface to the methods provided by a ViewConstructor. It should define the
 * constructor used to create such a View, usually to be put in a List.
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public interface IViewConstructor {
	/**
	 * Construct a new View to be put in an Adapter
	 * 
	 * @param currentObject
	 *            the object which the view represents
	 * @param context
	 *            the context of the calling Activity
	 * @param labeler
	 *            the labeler from which the information on the object will be
	 *            fetched
	 * @param position
	 *            the position at which the object is
	 * @return the new View
	 */
	public View getNewView(Object currentObject, Context context,
			ILabeler<? extends Object> labeler, int position);
}
