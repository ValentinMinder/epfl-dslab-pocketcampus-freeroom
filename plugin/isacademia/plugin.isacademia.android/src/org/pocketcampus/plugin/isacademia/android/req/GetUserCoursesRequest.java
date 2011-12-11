package org.pocketcampus.plugin.isacademia.android.req;

import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.authentication.shared.SessionId;
import org.pocketcampus.plugin.isacademia.android.IsacademiaController;
import org.pocketcampus.plugin.isacademia.android.IsacademiaModel;
import org.pocketcampus.plugin.isacademia.shared.IsacademiaService.Iface;
import org.pocketcampus.plugin.isacademia.shared.Course;

public class GetUserCoursesRequest extends Request<IsacademiaController, Iface, SessionId, List<Course>> {

	@Override
	protected List<Course> runInBackground(Iface client, SessionId param) throws Exception {
		return client.getUserCourses(param);
	}

	@Override
	protected void onResult(IsacademiaController controller, List<Course> result) {
		((IsacademiaModel) controller.getModel()).setCourses(result);
	}

	@Override
	protected void onError(IsacademiaController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
}
