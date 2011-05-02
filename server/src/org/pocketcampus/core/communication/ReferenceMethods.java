package org.pocketcampus.core.communication;

import org.pocketcampus.core.communication.annotations.Action;
import org.pocketcampus.core.communication.packet.Payload;
import org.pocketcampus.core.communication.pipes.IPipeWriter;
import org.pocketcampus.core.exception.ServerException;

/**
 * This class provides a set of reference methods intended to be used with the
 * <code>SignatureChecker</code> class. This class and its methods are not intended
 * to be implemented.
 */
abstract public class ReferenceMethods {
	/**
	 * Basic PocketCampus module's action.
	 * @param payload the actual arguments which are passed to the action, in a <code>
	 * Payload</code> wrapper along with some informations about the targetted action
	 * @param response a message-passing mechanism which is used to retrieve the response
	 * for the client
	 * @return not used by the communication service, nor for the signature comparison. Can
	 * be whatever, of any type (including <code>void</code>)
	 * @throws ServerException the only checked exception allowed to be thrown. If unchecked
	 * exceptions are thrown, they are converted into ServerExceptions. A thrown exception
	 * result in the 501 (Unrecoverable Module Error) PCP Error sent to the client.
	 */
	@Action
	abstract
	public Object pcModuleAction(Payload payload, IPipeWriter<Object> response) throws ServerException;
}
