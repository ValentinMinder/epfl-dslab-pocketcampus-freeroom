package org.pocketcampus.platform.android.ui.labeler;

import android.widget.LinearLayout;

/**
 * Interface to the methods provided by a <code>SubtitledFeedViewLabeler</code>.
 * A <code>SubtitledFeedView</code> should have a title, a subtitle, a
 * description, and a picture.
 * 
 * Defines where the information about an object that is to be displayed will be
 * fetched from.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public interface ISubtitledFeedViewLabeler<LabeledObjectType> {

	/**
	 * Returns the title of the labeled object.
	 * 
	 * @param obj
	 *            The object for which we want the title.
	 * @return The <code>String</code> object's title defined by the labeler.
	 */
	public String getTitle(LabeledObjectType obj);

	/**
	 * Returns the subtitle of the labeled object.
	 * 
	 * @param obj
	 *            The object for which we want the subtitle.
	 * @return The <code>String</code> subtitle defined by the labeler.
	 */
	public String getSubtitle(LabeledObjectType obj);

	/**
	 * Returns the description of the labeled object.
	 * 
	 * @param obj
	 *            The object for which we want the description.
	 * @return The <code>String</code> description defined by the labeler.
	 */
	public String getDescription(LabeledObjectType obj);

	/**
	 * Returns the picture of the labeled object.
	 * 
	 * @param obj
	 *            The object for which we want the picture.
	 * @return The <code>LinearLayout</code> picture defined by the labeler.
	 */
	public LinearLayout getPictureLayout(LabeledObjectType obj);

}
