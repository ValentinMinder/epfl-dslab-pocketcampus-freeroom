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
public interface IRatableViewLabeler<LabeledObjectType> {

	/**
	 * Returns the title of the object passed in parameter
	 * 
	 * @param obj
	 *            the object of which we want the title
	 * @return the String title
	 */
	public String getLabel(LabeledObjectType obj);

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
	public float getRating(LabeledObjectType obj);

	/**
	 * Returns the Number of Votes of the object passed in parameter
	 * 
	 * @param obj
	 *            the object of which we want the Number of Votes
	 */
	public int getNumberOfVotes(LabeledObjectType obj);

	/**
	 * Returns the Place at which the object passed in parameter is available
	 * 
	 * @param obj
	 *            the object of which we want the place
	 * @return the String place
	 */
	public String getPlaceName(LabeledObjectType obj);

}
