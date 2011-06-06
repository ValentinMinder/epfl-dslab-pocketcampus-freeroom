package org.pocketcampus.plugin.test;

import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.pocketcampus.core.plugin.IPlugin;
import org.pocketcampus.core.plugin.PublicMethod;
import org.pocketcampus.provider.newsfeed.INewsFeedProvider;

/**
 * Servlet implementation class Test
 */
public class Test implements IPlugin, INewsFeedProvider {

	private static final long serialVersionUID = -3719959899511131113L;

	@SuppressWarnings("unchecked")
	@PublicMethod
	public String capitalize(HttpServletRequest request) {
    	Enumeration<String> attrNames = request.getParameterNames();
		
		if(!attrNames.hasMoreElements())
			return "vide";
		
		String ret = new String();
		while(attrNames.hasMoreElements()){
			String s = (String)request.getParameter( attrNames.nextElement() );
			ret += s.toUpperCase();
		}
		
		return ret;
    }
	
	@PublicMethod
	public String uploadimage(HttpServletRequest request) throws FileUploadException {
		System.out.println("----- " + new Date() + " -----");
		
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		print("Multipart: " + isMultipart);
		if (!isMultipart)
			return "Must upload as HTTP multipart POST";
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		@SuppressWarnings("unchecked")
		List<FileItem> items = upload.parseRequest(request);
		
		Iterator<FileItem> it = items.iterator();
		while (it.hasNext()) {
			DiskFileItem item = (DiskFileItem) it.next();
			
			print("-FILE- " + item.toString());
		}
		
		return print("Image received, thank you :)");
	}

	private <O> O print(O obj) {
		System.out.println(obj);
		return obj;
	}

}
