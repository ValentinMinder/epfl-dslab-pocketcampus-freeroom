package org.pocketcampus.android.platform.sdk.ui.labeler;

import java.util.Date;

/**
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 * @param <LabeledObjectType>
 */
public interface IRichLabeler<LabeledObjectType> extends ILabeler<LabeledObjectType>{

	/** Returns the title of the Labeled Object */
	public String getTitle(LabeledObjectType obj);

	/** Returns the description of the Labeled Object */
	public String getDescription(LabeledObjectType obj);

	/** Returns the value (e.g. price) of the Labeled Object */
	public double getValue(LabeledObjectType obj);

	/** Returns the date of the Labeled Object */
	public Date getDate(LabeledObjectType obj);

}
