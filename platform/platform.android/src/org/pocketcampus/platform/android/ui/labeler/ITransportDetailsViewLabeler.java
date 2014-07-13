package org.pocketcampus.platform.android.ui.labeler;

import android.widget.LinearLayout;

/**
 * Interface to the methods provided by a
 * <code>TransportDetailsViewLabeler</code>. A
 * <code>TransportDetailsViewLabeler</code> should have a departure time, an
 * arrival time, a departure place and an arrival place.
 * 
 * Defines where the information about an object that is to be displayed will be
 * fetched from.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public interface ITransportDetailsViewLabeler<LabeledObjectType> {

	/**
	 * Returns the departure time of the labeled object in the form HH:mm.
	 * 
	 * @param obj
	 *            The object of which we want the departure time.
	 * @return The <code>String</code> departure time.
	 */
	public String getDepartureTime(LabeledObjectType obj);

	/**
	 * Returns the departure place of the labeled object.
	 * 
	 * @param obj
	 *            The object of which we want the departure place.
	 * @return The <code>String</code> departure place.
	 */
	public String getDeparturePlace(LabeledObjectType obj);

	/**
	 * Returns the arrival time of the labeled object in the form HH:mm.
	 * 
	 * @param obj
	 *            The object of which we want the arrival time.
	 * @return The <code>String</code> arrival time.
	 */
	public String getArrivalTime(LabeledObjectType obj);

	/**
	 * Returns the arrival place of the labeled object.
	 * 
	 * @param obj
	 *            The object of which we want the arrival place.
	 * @return The <code>String</code> arrival place.
	 */
	public String getArrivalPlace(LabeledObjectType obj);

	/**
	 * Returns the picture of the labeled object.
	 * 
	 * @param obj
	 *            The object of which we want the picture
	 * @return The <code>LinearLayout</code> picture.
	 */
	public LinearLayout getPictureLayout(LabeledObjectType obj);

}
