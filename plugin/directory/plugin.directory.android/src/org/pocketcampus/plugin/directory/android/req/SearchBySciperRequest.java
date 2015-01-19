package org.pocketcampus.plugin.directory.android.req;

import java.util.Iterator;

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
public class SearchBySciperRequest extends Request<DirectoryController, Iface, DirectoryRequest, DirectoryResponse> {

	private IDirectoryView caller;
	
	public SearchBySciperRequest(IDirectoryView caller) {
		this.caller = caller;
	}
	
	@Override
	protected DirectoryResponse runInBackground(Iface client, DirectoryRequest param) throws Exception {
		if("".equals(param.getQuery()))
			return new DirectoryResponse(200);
		DirectoryResponse result = client.searchDirectory(param);
		// sometime the query matches more than one person (e.g., on phone number)
		Iterator<Person> i = result.getResults().iterator();
		while(i.hasNext()) {
			Person p = i.next();
			if(!p.getSciper().equals(param.getQuery()))
				i.remove();
		}
		return result;
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
