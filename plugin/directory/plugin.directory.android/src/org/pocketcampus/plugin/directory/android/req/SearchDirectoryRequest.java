package org.pocketcampus.plugin.directory.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.directory.android.*;
import org.pocketcampus.plugin.directory.android.iface.IDirectoryView;
import org.pocketcampus.plugin.directory.shared.*;
import org.pocketcampus.plugin.directory.shared.DirectoryService.Iface;

/**
 * SearchDirectoryRequest
 * 
 * searches EPFL  directory
 * for people.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class SearchDirectoryRequest extends Request<DirectoryController, Iface, DirectoryRequest, DirectoryResponse> {

	private IDirectoryView caller;
	
	public SearchDirectoryRequest(IDirectoryView caller) {
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
			if(result.isSetResults())
				((DirectoryModel) controller.getModel()).setResults(result.getResults());
			else
				((DirectoryModel) controller.getModel()).clearResults();
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
