package org.pocketcampus.plugin.authentication.android.iface;

import java.util.List;

import org.pocketcampus.platform.android.core.IView;

/**
 * IAuthenticationView
 * 
 * Interface for the Views of the Authentication plugin.
 * 
 * It contains the method that are called by the Model
 * when some data is updated, as well as the methods that
 * are called by the "HttpRequest" classes when some usual
 * behavior occurs.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public interface IAuthenticationView extends IView {
	
	void shouldFinish();
	void gotUserAttributes(List<String> attr);
	
}
