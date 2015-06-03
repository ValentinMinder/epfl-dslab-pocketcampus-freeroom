package org.pocketcampus.plugin.food.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.food.android.*;
import org.pocketcampus.plugin.food.android.iface.IFoodView;
import org.pocketcampus.plugin.food.shared.*;
import org.pocketcampus.plugin.food.shared.FoodService.Iface;

/**
 * VoteRequest
 * 
 * This class sends an HttpRequest using Thrift to the PocketCampus server
 * in order to cast food vote.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class CastVoteRequest extends Request<FoodController, Iface, VoteRequest, VoteResponse> {

	private IFoodView caller;
	
	public CastVoteRequest(IFoodView caller) {
		this.caller = caller;
	}
	
	@Override
	protected VoteResponse runInBackground(Iface client, VoteRequest param) throws Exception {
		return client.vote(param);
	}

	@Override
	protected void onResult(FoodController controller, VoteResponse result) {
		caller.voteCastFinished(result.getSubmitStatus());
	}

	@Override
	protected void onError(FoodController controller, Exception e) {
		caller.networkErrorHappened();
		e.printStackTrace();
	}
	
}
