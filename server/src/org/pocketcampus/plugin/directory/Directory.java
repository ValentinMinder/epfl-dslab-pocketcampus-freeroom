package org.pocketcampus.plugin.directory;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.LinkedList;
import javax.servlet.http.HttpServletRequest;
import org.pocketcampus.shared.plugin.directory.*;
import org.pocketcampus.core.plugin.IPlugin;
import org.pocketcampus.core.plugin.PublicMethod;
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
    
	private static final String pictureCamiproBase = "http://people.epfl.ch/cache/photos/camipro/";
	private static final String pictureExtBase = "http://people.epfl.ch/cache/photos/ext/";
	private static final String pictureExtension = ".jpg";
	
	LDAPConnection ldap;
	
	public Directory(){
		ldap = new LDAPConnection();
		
		connectLdap();
	}
	
	private void connectLdap(){
		try {
			ldap.connect("ldap.epfl.ch", 389);
		}catch (LDAPException e) {
			System.out.println("Ldap exception");
		}
	}
	
	@PublicMethod
	public String photo(HttpServletRequest request){
		String sciper = request.getParameter("sciper");
		byte[] sciperBytes = null;
		byte[] digest = null;
		
		try {
			sciperBytes = sciper.getBytes("UTF-8");
			
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			digest = md.digest(sciperBytes);
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		BigInteger bigInt = new BigInteger(1, digest);
		String hashedSciper = bigInt.toString(16);
		
		while(hashedSciper.length() < 32 ){
		  hashedSciper = "0"+hashedSciper;
		}
		
		String pictureCamiproUrl = pictureCamiproBase + hashedSciper + pictureExtension;
		String pictureExtUrl = pictureExtBase + hashedSciper + pictureExtension;
		
		if(checkUrl(pictureCamiproUrl)) {
			System.out.println(pictureCamiproUrl);
			return pictureCamiproUrl;
		}
		
		if(checkUrl(pictureExtUrl)) {
			System.out.println(pictureExtUrl);
			return pictureExtUrl;
		}
		
		return null;
	}
	
	private boolean checkUrl(String pictureUrl) {
		URL u;
		try {
			u = new URL(pictureUrl);
			HttpURLConnection huc =  (HttpURLConnection)  u.openConnection(); 
			huc.setRequestMethod("GET"); 
			huc.connect(); 
			return (huc.getResponseCode()==200);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
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
			if( !ldap.isConnected())
				ldap.reconnect();
			
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
		} catch (LDAPException e) {
			System.out.println("ldap reconnection problem");
		}
		
		//sorting the results alphabetatically
		Collections.sort(results);		
		
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

