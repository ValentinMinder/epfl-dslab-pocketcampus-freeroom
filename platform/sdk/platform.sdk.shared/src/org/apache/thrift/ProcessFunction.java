/**
 * 
 */
package org.apache.thrift;

import javax.servlet.http.HttpServletRequest;

import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TMessageType;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.server.TServlet;

public abstract class ProcessFunction<I, T extends TBase> {
  private final String methodName;

  public ProcessFunction(String methodName) {
    this.methodName = methodName;
  }

  public final void process(int seqid, TProtocol iprot, TProtocol oprot, I iface, HttpServletRequest request) throws TException {
    T args = getEmptyArgsInstance();
    try {
      args.read(iprot);
    } catch (TProtocolException e) {
      iprot.readMessageEnd();
      TApplicationException x = new TApplicationException(TApplicationException.PROTOCOL_ERROR, e.getMessage());
      oprot.writeMessageBegin(new TMessage(getMethodName(), TMessageType.EXCEPTION, seqid));
      x.write(oprot);
      oprot.writeMessageEnd();
      oprot.getTransport().flush();
      return;
    }
    TFieldIdEnum tfe = null;
    Object firstArg = null;
    if((tfe = args.fieldForId(1)) != null && (firstArg = args.getFieldValue(tfe)) != null)
      TServlet.requestsMap.put(firstArg, request);
    try {
      iprot.readMessageEnd();
      TBase result = getResult(iface, args);
      oprot.writeMessageBegin(new TMessage(getMethodName(), TMessageType.REPLY, seqid));
      result.write(oprot);
      oprot.writeMessageEnd();
      oprot.getTransport().flush();
    } finally {
      if(firstArg != null)
    	  TServlet.requestsMap.remove(firstArg);
    }
  }

  protected abstract TBase getResult(I iface, T args) throws TException;

  protected abstract T getEmptyArgsInstance();

  public String getMethodName() {
    return methodName;
  }
}
