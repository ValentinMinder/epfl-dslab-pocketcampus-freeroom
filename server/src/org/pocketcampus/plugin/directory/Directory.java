package org.pocketcampus.plugin.directory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.pocketcampus.shared.plugin.directory.*;
import org.pocketcampus.shared.plugin.map.MapElementBean;
import org.pocketcampus.shared.plugin.map.MapLayerBean;
import org.pocketcampus.core.plugin.IPlugin;
import org.pocketcampus.core.plugin.PublicMethod;
import org.pocketcampus.plugin.directory.DirectoryQuery;
import org.pocketcampus.provider.mapelements.IMapElementsProvider;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;


/**
 * Servlet implementation class Directory
 */
public class Directory implements IPlugin{
	private static final long serialVersionUID = 14545643453L;
    
	LDAPConnection ldap;
	
	public Directory(){
		try {
			ldap = new LDAPConnection();
			ldap.connect("ldap.epfl.ch", 389);
		}catch (LDAPException e) {
			System.out.println("Ldap exception");
		}
	}
	
	@PublicMethod
	public LinkedList<Person> bla(HttpServletRequest request){

		String firstName = request.getParameter("firstName");
    	String lastName = request.getParameter("lastName");
		String sciper = request.getParameter("sciper");
    	
//		only if we want the auth part
//		String username = request.getParameter("username");
//    	String pwd = request.getParameter("password");
    	
		
		LinkedList<Person> res;
//		if(sciper != null){
////			res = DirectoryQuery.searchBySciper(sciper, username, pwd);
//			res = DirectoryQuery.searchBySciper(sciper, null, null);
//		}else{
////			res = DirectoryQuery.searchByName(firstName, lastName, username, pwd);
//			res = DirectoryQuery.searchByName(firstName, lastName, null, null);
//		}
		
		
//		Gson gson = new Gson();
//		
//		Type listType = new TypeToken<ArrayList<Person>>() {}.getType();
//		System.out.println( gson.toJson(res, listType) );

		res = search(sciper, firstName, lastName, true);
		
		return res;
	}
	
	@PublicMethod
	public LinkedList<Person> idrkhn(HttpServletRequest request){
		String firstName = request.getParameter("firstName");
    	String lastName = request.getParameter("lastName");
		String sciper = request.getParameter("sciper");
		
		LinkedList<Person> res = search(sciper, firstName, lastName, false);
		
		return res;
	}
	
	private LinkedList<Person> search(String sciper, String first_name, String last_name, boolean accurate){
		LinkedList<Person> results = new LinkedList<Person>();
		
		
		// search part			
		// TODO add the sizeLimit param
		SearchResult searchResult;
		String searchQuery = buildSearchQuery(sciper, first_name, last_name, accurate);
		try {
			searchResult = ldap.search("o=epfl,c=ch", SearchScope.SUB, searchQuery);

			//System.out.println(searchResult.getSearchEntries().get(0).toLDIFString());
			String t[] = new String[2];
			for (SearchResultEntry e : searchResult.getSearchEntries())
			{
				String web = e.getAttributeValue("labeledURI");
				if(web != null){
					t =  web.split(" ");
					web = t[0];
				}
				
				Person p = new Person(
						e.getAttributeValue("givenName"),
						e.getAttributeValue("sn"),
						e.getAttributeValue("mail"),
						web,
						e.getAttributeValue("telephoneNumber"),
						e.getAttributeValue("roomNumber"),
						e.getAttributeValue("uniqueIdentifier"));
				
				if( !results.contains(p))
					results.add(p);
			}
		} catch (LDAPSearchException e1) {
			System.out.println("ldap search problem");
		}
		
		return results;
		
		
		
		
	}
	
	
	private String buildSearchQuery(String sciper, String first_name, String last_name, boolean accurate){
		String searchQuery = null;
		
		String equal;
		if(accurate)
			equal = "=";
		else
			equal = "~=";
		
		if(sciper != null){
			searchQuery = "(uniqueIdentifier="+sciper+")";
		}else if(first_name != null && last_name != null){
			searchQuery = "(&(sn" + equal +last_name+ ")(givenName" + equal +first_name+"))";
		}else if(first_name != null){
			searchQuery = "(givenName" + equal + first_name+")";
		}else if(last_name != null)
			searchQuery = "(sn" + equal + last_name+")";
		else{
			
		}
		System.out.println(searchQuery);
		return searchQuery;
	}
}

