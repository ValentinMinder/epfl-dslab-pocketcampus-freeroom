package org.pocketcampus.platform.android.ui.labeler;

import java.util.Date;

/**
 * Interface to the methods provided by a <code>RichLabeler</code>. A
 * <code>RichLabeler</code> should have a title, a description, a value and a
 * <code>Date</code>.
 * 
 * Defines where the information about an object that is to be displayed will be
 * fetched from.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public interface IRichLabeler<LabeledObjectType> extends
		ILabeler<LabeledObjectType> {

	/**
	 * Returns the title of the labeled object.
	 * 
	 * @param obj
	 *            The object for which we want the title.
	 * @return The <code>String</code> object's title defined by the labeler.
	 */
	public String getTitle(LabeledObjectType obj);

	/**
	 * Returns the description of the labeled object.
	 * 
	 * @param obj
	 *            The object for which we want the description.
	 * @return The <code>String</code> object's description defined by the
	 *         labeler.
	 */
	public String getDescription(LabeledObjectType obj);

	/**
	 * Returns the value (e.g. price) of the labeled object.
	 * 
	 * @param obj
	 *            The object for which we want the value.
	 * @return The <code>double</code> object's value defined by the labeler.
	 */
	public double getValue(LabeledObjectType obj);

	/**
	 * Returns the <code>Date</code> of the labeled object.
	 * 
	 * @param obj
	 *            The object for which we want the <code>Date</code>.
	 * @return The <code>Date</code> object's <code>Date</code> defined by the
	 *         labeler.
	 */
	public Date getDate(LabeledObjectType obj);

}
