package org.pocketcampus.plugin.test.android.req;

import org.apache.thrift.TException;
import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.test.android.TestController;
import org.pocketcampus.plugin.test.android.TestModel;
import org.pocketcampus.plugin.test.shared.TestService.Iface;

public class BarRequest extends Request<TestController, Iface, Object, Integer> {
	@Override
	protected Integer run(Iface client, Object param) throws TException {
		System.out.println("run");
		return client.getBar();
	}

	@Override
	protected void onResult(TestController controller, Integer result) {
		System.out.println("onResult");
		((TestModel) controller.getModel()).setBar(result);
	}
	
	@Override
	protected void onError(TException e) {
		System.out.println("onError");
		e.printStackTrace();
	}
}