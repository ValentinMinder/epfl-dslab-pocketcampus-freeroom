package org.pocketcampus.plugin.directory.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.directory.android.DirectoryController;
import org.pocketcampus.plugin.directory.android.DirectoryMainView;
import org.pocketcampus.plugin.directory.android.DirectoryModel;
import org.pocketcampus.plugin.directory.shared.DirectoryRequest;
import org.pocketcampus.plugin.directory.shared.DirectoryResponse;
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

	private DirectoryMainView caller;
	
	public SearchDirectoryRequest(DirectoryMainView caller) {
		this.caller = caller;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		caller.showLoading();
	}
	
	@Override
	protected DirectoryResponse runInBackground(Iface client, DirectoryRequest param) throws Exception {
		if("".equals(param.getQuery()))
			return new DirectoryResponse(200);
		return client.searchDirectory(param);
	}

	@Override
	protected void onResult(DirectoryController controller, DirectoryResponse result) {
		caller.hideLoading();
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
		caller.hideLoading();
		caller.networkErrorHappened();
		e.printStackTrace();
	}
	
}
