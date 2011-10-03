package org.pocketcampus.android.platform.sdk.io.multiplex;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TMessageType;
import org.apache.thrift.protocol.TProtocol;

public class ThriftMultiplexInvocationHandler implements InvocationHandler {
	private String mServiceName;
	private TProtocol mOprot;
	private Set<Class<?>> mInterfaces = new HashSet<Class<?>>();
	private Object mTarget;

	public <TInterface> ThriftMultiplexInvocationHandler(String serviceName,
			TProtocol oprot,
			Class<?> iface,
			TServiceClient target) {
		mServiceName = serviceName;
		mOprot = oprot;
		addInterface(iface);
		mTarget = target;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object result;
		if (mInterfaces.contains(method.getDeclaringClass())) {
			mOprot.writeMessageBegin(new TMessage(mServiceName, TMessageType.SERVICE_SELECTION, 0));
			try {
				result = method.invoke(mTarget, args);
			} catch (InvocationTargetException e) {
				throw e.getCause();
			}
			mOprot.writeMessageEnd();
			mOprot.getTransport().flush();
		} else {
			try {
				result = method.invoke(mTarget, args);
			} catch (InvocationTargetException e) {
				throw e;
			}
		}
		return result;
	}

	private void addInterface(Class<?> iface) {
		mInterfaces.add(iface);
		for (Class<?> superiface : iface.getInterfaces()) {
			addInterface(superiface);
		}
	}
}