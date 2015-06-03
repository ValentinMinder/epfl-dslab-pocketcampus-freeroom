package org.pocketcampus.platform.android.ui.labeler;

import android.widget.LinearLayout;

/**
 * Interface to the methods provided by a FeedViewLabeler
 * 
 * A FeedView should have a title, a description, and a picture
 * 
 * Defines where the information about an object that is to be displayed will be
 * fetched from
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public interface IFeedViewLabeler<LabeledObjectType> {

	/**
	 * Returns the title of the object passed in parameter
	 * 
	 * @param obj
	 *            the object of which we want the title
	 * @return the String title
	 */
	public String getTitle(LabeledObjectType obj);

	/**
	 * Returns the description of the object passed in parameter
	 * 
	 * @param obj
	 *            the object of which we want the description
	 * @return the String description
	 */
	public String getDescription(LabeledObjectType obj);

	/**
	 * Returns the rating of the object passed in parameter
	 * 
	 * @param obj
	 *            the object of which we want the rating
	 * @return the rating
	 */
	public LinearLayout getPictureLayout(LabeledObjectType obj);

}
