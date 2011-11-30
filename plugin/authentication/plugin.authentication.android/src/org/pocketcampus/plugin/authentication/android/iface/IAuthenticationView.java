package org.pocketcampus.plugin.authentication.android.iface;

import org.pocketcampus.android.platform.sdk.core.IView;

public interface IAuthenticationView extends IView {
	void gotTequilaKey();
	void mustFinish();
}
