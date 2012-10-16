package org.apache.thrift;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TMessageType;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolUtil;
import org.apache.thrift.protocol.TType;

public abstract class TBaseProcessor<I> implements TProcessor {
  private final I iface;
  private final Map<String,ProcessFunction<I, ? extends TBase>> processMap;

  protected TBaseProcessor(I iface, Map<String, ProcessFunction<I, ? extends TBase>> processFunctionMap) {
    this.iface = iface;
    this.processMap = processFunctionMap;
  }

  @Override
  public boolean process(TProtocol in, TProtocol out, String clientIP) throws TException {
	  //System.out.println("HOORRAY");
    TMessage msg = in.readMessageBegin();
    try {
    	// security check 
    	// IP = clientIP
    	// method name = msg.name
    	//System.out.println("client ip " + clientIP + " method name " + msg.name);
    	Class[] args = new Class[2];
    	args[0] = String.class;
    	args[1] = String.class;
		Method m = iface.getClass().getMethod("securityCheck", args);
		Boolean b = (Boolean) m.invoke(iface, clientIP, msg.name);
		if(!b)
			throw new SecurityException("Client IP " + clientIP + " is not allowed to access method " + msg.name);
	} catch (NoSuchMethodException e) {
		// class does not implement securityCheck
		// fail silently, i.e., assume all methods are accessible by all IPs
		System.out.println("No security check");
	} catch (IllegalAccessException e) {
		// securityCheck invocation failed, we should throw exception
		e.printStackTrace();
		throw new TException("securityCheck invocation failed, we fail-safe, we die");
	} catch (InvocationTargetException e) {
		// securityCheck invocation failed, we should throw exception
		e.printStackTrace();
		throw new TException("securityCheck invocation failed, we fail-safe, we die");
	}
    ProcessFunction fn = processMap.get(msg.name);
    if (fn == null) {
      TProtocolUtil.skip(in, TType.STRUCT);
      in.readMessageEnd();
      TApplicationException x = new TApplicationException(TApplicationException.UNKNOWN_METHOD, "Invalid method name: '"+msg.name+"'");
      out.writeMessageBegin(new TMessage(msg.name, TMessageType.EXCEPTION, msg.seqid));
      x.write(out);
      out.writeMessageEnd();
      out.getTransport().flush();
      return true;
    }
    fn.process(msg.seqid, in, out, iface);
    return true;
  }
  public void hello(String str) {System.out.println(str);}
}
