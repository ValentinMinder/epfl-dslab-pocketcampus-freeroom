package org.pocketcampus.plugin.isacademia.android.iface;

import org.pocketcampus.android.platform.sdk.core.IView;

public interface IIsacademiaView extends IView {
	void coursesUpdated();
	void examsUpdated();
	void scheduleUpdated();
}
