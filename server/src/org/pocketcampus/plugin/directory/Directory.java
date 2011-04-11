package org.pocketcampus.plugin.directory;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.extensions.StartTLSExtendedRequest;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;

//import org.pocketcampus.core.router.IServerBase;
//import org.pocketcampus.core.router.PublicMethod;
import org.pocketcampus.shared.plugin.directory.*;
import org.pocketcampus.shared.plugin.map.MapElementBean;
import org.pocketcampus.shared.plugin.map.MapLayerBean;
import org.pocketcampus.core.plugin.IPlugin;
import org.pocketcampus.core.plugin.PublicMethod;
import org.pocketcampus.plugin.directory.DirectoryQuery;
import org.pocketcampus.provider.mapelements.IMapElementsProvider;


/**
 * Servlet implementation class Directory
 */
public class Directory implements IPlugin, IMapElementsProvider {
	private static final long serialVersionUID = 14545643453L;
     


	public void init() throws ServletException {
	}
	
	@PublicMethod
	public LinkedList<Person> bla(HttpServletRequest request){
		// Set a cookie for the user, so that the counter does not increate
		// everytime the user press refresh
//		HttpSession session = request.getSession(true);
		// Set the session valid for 5 secs
//		session.setMaxInactiveInterval(5);
//		response.setContentType("text/plain");
//		PrintWriter out = response.getWriter();
		String firstName = request.getParameter("firstName");
    	String lastName = request.getParameter("lastName");
		String sciper = request.getParameter("sciper");
    	String username = request.getParameter("username");
    	String pwd = request.getParameter("password");
    	
    	LinkedList<Person> res;
		if(sciper != null)
			res = DirectoryQuery.searchBySciper(sciper, username, pwd);
		else
			res = DirectoryQuery.searchByName(firstName, lastName, username, pwd);
		
		
//		Gson gson = new Gson();
//		
//		Type listType = new TypeToken<ArrayList<Person>>() {}.getType();
//		System.out.println( gson.toJson(res, listType) );
//		
		
		
		return res;
		
		
		
		
		
	}

	public void destroy() {
		
	}

	@Override
	public List<MapElementBean> getLayerItems() {
		// TODO Auto-generated method stub
		return new ArrayList<MapElementBean>();
	}

	@Override
	public MapLayerBean getLayer() {
		// TODO Auto-generated method stub
		return new MapLayerBean("Person", "", 131232654, 60, false);
	}
	
	


}
