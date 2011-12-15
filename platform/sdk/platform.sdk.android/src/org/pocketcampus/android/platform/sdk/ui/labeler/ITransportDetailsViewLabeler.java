package org.pocketcampus.android.platform.sdk.ui.labeler;

import android.widget.LinearLayout;

/**
 * Interface to the methods provided by a TransportDetailsViewLabeler
 * 
 * A FeedView should have a departure time, an arrival time, a departure place
 * and an arrival place.
 * 
 * Defines where the information about an object that is to be displayed will be
 * fetched from
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public interface ITransportDetailsViewLabeler<LabeledObjectType> {

	/**
	 * Returns the departure time of the object passed in parameter in the form
	 * HH:mm.
	 * 
	 * @param obj
	 *            the object of which we want the departure time
	 * @return the String departure time
	 */
	public String getDepartureTime(LabeledObjectType obj);

	/**
	 * Returns the departure place of the object passed in parameter.
	 * 
	 * @param obj
	 *            the object of which we want the departure place
	 * @return the String departure place
	 */
	public String getDeparturePlace(LabeledObjectType obj);

	/**
	 * Returns the arrival time of the object passed in parameter in the form
	 * HH:mm.
	 * 
	 * @param obj
	 *            the object of which we want the arrival time
	 * @return the String arrival time
	 */
	public String getArrivalTime(LabeledObjectType obj);

	/**
	 * Returns the arrival place of the object passed in parameter.
	 * 
	 * @param obj
	 *            the object of which we want the arrival place
	 * @return the String arrivale place
	 */
	public String getArrivalPlace(LabeledObjectType obj);

	/**
	 * Returns the picture of the object passed in parameter.
	 * 
	 * @param obj
	 *            the object of which we want the picture
	 * @return the LinearLayout picture
	 */
	public LinearLayout getPictureLayout(LabeledObjectType obj);

}
