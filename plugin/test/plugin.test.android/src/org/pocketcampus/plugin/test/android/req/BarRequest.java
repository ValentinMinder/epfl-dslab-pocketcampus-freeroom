package org.pocketcampus.plugin.test.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.test.android.TestController;
import org.pocketcampus.plugin.test.android.TestModel;
import org.pocketcampus.plugin.test.shared.TestService.Iface;

public class BarRequest extends Request<TestController, Iface, Object, Integer> {
	@Override
	protected Integer runInBackground(Iface client, Object param) throws Exception {
		System.out.println("run");
		return client.getBar();
	}

	@Override
	protected void onResult(TestController controller, Integer result) {
		System.out.println("onResult");
		((TestModel) controller.getModel()).setBar(result);
	}
	
	@Override
	protected void onError(TestController controller, Exception e) {
		System.out.println("onError");
		((TestModel) controller.getModel()).notifyNetworkError();
		e.printStackTrace();
	}
}