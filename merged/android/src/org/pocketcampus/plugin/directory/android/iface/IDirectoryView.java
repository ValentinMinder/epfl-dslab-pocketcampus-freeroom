package org.pocketcampus.plugin.directory.android.iface;

import org.pocketcampus.android.platform.sdk.core.IView;

public interface IDirectoryView extends IView {
	void resultsUpdated();
	void tooManyResults(int nb);
}
