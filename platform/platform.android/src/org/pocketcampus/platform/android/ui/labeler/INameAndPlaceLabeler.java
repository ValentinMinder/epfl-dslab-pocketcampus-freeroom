package org.pocketcampus.platform.android.ui.labeler;

/**
 * Interface to the methods provided by a RatableViewLabeler
 * 
 * A RatableView should have a title, a description, a rating and its
 * corresponding number of votes, and a place at which it can be purchased
 * 
 * Defines where the information about an object that is to be displayed will be
 * fetched from
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public interface INameAndPlaceLabeler<LabeledObjectType> extends
		ILabeler<LabeledObjectType> {

	/**
	 * Returns the place at which of the object passed in parameter is available
	 * 
	 * @param obj
	 *            the object of which we want the place
	 * @return the String place
	 */
	public String getPlaceName(LabeledObjectType obj);

}
