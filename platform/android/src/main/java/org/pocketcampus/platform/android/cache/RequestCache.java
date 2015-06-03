package org.pocketcampus.platform.android.cache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TIOStreamTransport;

import android.content.Context;

/**
 * RequestCache
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 */
public class RequestCache {
	
	public static void invalidateCache(Context context, String prefix, Object request) {
		if(request != null)
			prefix += request.hashCode();
		if(context.getFileStreamPath(prefix).exists())
			context.deleteFile(prefix);
	}
	
	public static void invalidateCache(Context context, String prefix) {
		for(String filename : context.fileList()) {
			if(filename.startsWith(prefix))
				if(context.getFileStreamPath(filename).exists())
					context.deleteFile(filename);
		}
	}
	
	public static void pushToCache(Context context, String prefix, Object request, TBase reply) {
		try {
			if(request != null)
				prefix += request.hashCode();
			context.openFileOutput(prefix, Context.MODE_PRIVATE).close();
			BufferedOutputStream bufferedOut = new BufferedOutputStream(new FileOutputStream(context.getFileStreamPath(prefix)), 2048);
			TBinaryProtocol binaryOut = new TBinaryProtocol(new TIOStreamTransport(bufferedOut));
			ThriftObjectClassName className = new ThriftObjectClassName(reply.getClass().getCanonicalName());
			className.write(binaryOut);
			reply.write(binaryOut);
		    //bufferedOut.flush();
		    bufferedOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TException e) {
			e.printStackTrace();
		}
	}
	
	public static TBase queryCache(Context context, String prefix, Object request) {
		if(request != null)
			prefix += request.hashCode();
		if(!context.getFileStreamPath(prefix).exists()) {
			return null;
		}
		try {
			//FileInputStream fin = context.openFileInput(prefix);
			BufferedInputStream bufferedIn = new BufferedInputStream(new FileInputStream(context.getFileStreamPath(prefix)));
			TBinaryProtocol binaryIn = new TBinaryProtocol(new TIOStreamTransport(bufferedIn));
			ThriftObjectClassName className = new ThriftObjectClassName();
			className.read(binaryIn);
			TBase cachedObject = (TBase) Class.forName(className.getCanonicalName()).newInstance();
			cachedObject.read(binaryIn);
			bufferedIn.close();
			return cachedObject;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
