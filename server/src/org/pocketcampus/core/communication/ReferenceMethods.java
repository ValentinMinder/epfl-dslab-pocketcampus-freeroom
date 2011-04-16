package org.pocketcampus.core.communication;

import org.pocketcampus.core.communication.annotation.Action;
import org.pocketcampus.core.communication.packet.Payload;
import org.pocketcampus.core.exception.ServerException;

/**
 * This class provides a set of reference methods intended to be used with the
 * <code>SignatureChecker</code> class. This class is not intended to be implemented.
 */
abstract public class ReferenceMethods {
	/**
	 * Basic PocketCampus module's action.<br />
	 * The returned object should be the same as the one written in the <code>Response</code>
	 * @param payload
	 * @param response the object used by the framework to retrieve the response of the
	 * action
	 * @return the return type is not fixed and the object returned by the method
	 * is not used by the PC Framework.
	 * @throws ServerException
	 */
	@Action
	abstract
	public Object pcModuleAction(Payload payload, Response response) throws ServerException;
}
