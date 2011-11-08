package org.pocketcampus.android.platform.sdk.ui.labeler;

public interface IRatableLabeler<LabeledObjectType> {
	
	public String getTitle(LabeledObjectType obj);
	public String getDescription(LabeledObjectType obj);
	public float getRating(LabeledObjectType obj);
	public int getNbVotes(LabeledObjectType obj);

}
