package org.pocketcampus.platform.server.launcher;

import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * This code is a copy-paste of TServlet, with one important modification, the receivedRequestHeaders field.
 * It's a horrible hack that lets us view the HTTP headers per-request. Unfortunately, there's apparently no other way to do it...
 */
@SuppressWarnings("serial")
public class TrackingThriftServlet extends HttpServlet {

    private final TProcessor processor;

    private final TProtocolFactory inProtocolFactory;

    private final TProtocolFactory outProtocolFactory;

    public static final ThreadLocal<Map<String, String>> receivedRequestHeaders;

    static {
        receivedRequestHeaders = new ThreadLocal<>();
    }

    /**
     * @see HttpServlet#HttpServlet()
     */
    public TrackingThriftServlet(TProcessor processor, TProtocolFactory inProtocolFactory,
                                 TProtocolFactory outProtocolFactory) {
        super();
        this.processor = processor;
        this.inProtocolFactory = inProtocolFactory;
        this.outProtocolFactory = outProtocolFactory;
    }

    /**
     * @see HttpServlet#HttpServlet()
     */
    public TrackingThriftServlet(TProcessor processor, TProtocolFactory protocolFactory) {
        this(processor, protocolFactory, protocolFactory);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        TTransport inTransport;
        TTransport outTransport;

        try {
            response.setContentType("application/x-thrift");

            InputStream in = request.getInputStream();
            OutputStream out = response.getOutputStream();

            TTransport transport = new TIOStreamTransport(in, out);
            inTransport = transport;
            outTransport = transport;

            TProtocol inProtocol = inProtocolFactory.getProtocol(inTransport);
            TProtocol outProtocol = outProtocolFactory.getProtocol(outTransport);

            // log the request headers
            // getHeaderNames returns an instance of the horribly obsolete interface Enumeration<E>...
            Map<String, String> headers = new HashMap<>();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String key = headerNames.nextElement();
                headers.put(key, request.getHeader(key));
            }
            receivedRequestHeaders.set(headers);

            processor.process(inProtocol, outProtocol);
            out.flush();
        } catch (TException te) {
            throw new ServletException(te);
        } finally {
            receivedRequestHeaders.set(null);
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}