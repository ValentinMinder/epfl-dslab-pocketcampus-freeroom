package org.pocketcampus.plugin.directory.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.directory.android.*;
import org.pocketcampus.plugin.directory.android.iface.IDirectoryView;
import org.pocketcampus.plugin.directory.shared.*;
import org.pocketcampus.plugin.directory.shared.DirectoryService.Iface;

/**
 * SearchUniqueDirectoryRequest
 * 
 * searches EPFL  directory
 * for a uniquely identified people.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class SearchUniqueDirectoryRequest extends Request<DirectoryController, Iface, DirectoryRequest, DirectoryResponse> {

	private IDirectoryView caller;
	
	public SearchUniqueDirectoryRequest(IDirectoryView caller) {
		this.caller = caller;
	}
	
	@Override
	protected DirectoryResponse runInBackground(Iface client, DirectoryRequest param) throws Exception {
		if("".equals(param.getQuery()))
			return new DirectoryResponse(200);
		return client.searchDirectory(param);
	}

	@Override
	protected void onResult(DirectoryController controller, DirectoryResponse result) {
		if(result.getStatus() == 200) {
			if(result.isSetResults() && result.getResultsSize() == 1)
				caller.gotPerson(result.getResultsIterator().next());
			else
				caller.ambiguousQuery();
		} else {
			caller.ldapServersDown();
		}
	}

	@Override
	protected void onError(DirectoryController controller, Exception e) {
		caller.networkErrorHappened();
		e.printStackTrace();
	}
	
}
