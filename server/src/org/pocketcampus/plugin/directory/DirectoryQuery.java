package org.pocketcampus.plugin.directory;

import java.util.LinkedList;

import org.pocketcampus.shared.plugin.directory.Person;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;


public class DirectoryQuery {

	
	
	private static LinkedList<Person> search(String sciper, String first_name, String last_name, String username, String password){
		
		LinkedList<Person> results = new LinkedList<Person>();
		
		LDAPConnection ldap;
		try {
			ldap = new LDAPConnection();
			ldap.connect("ldap.epfl.ch", 389);
		
//			SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());
//			SSLSocketFactory socketFactory = sslUtil.createSSLSocketFactory();
//			ldap = new LDAPConnection(socketFactory,
//			     "ldap.epfl.ch", 636);
		
		
					
			//auth part
//			SearchResult searchResult = ldap.search("o=epfl,c=ch", SearchScope.SUB, "(|(uid="+username+")(uniqueIdentifier="+username+"))");
//			List<SearchResultEntry> lresults = searchResult.getSearchEntries();
//
//			if(lresults.isEmpty()) {
//				System.out.println("Wrong username.");
//				ldap.close();
//				return results;
//			}
//
//			String dn = lresults.get(0).getDN();
//
//			BindResult bResult = ldap.bind(dn, password);
//				
//			if(bResult.getResultCode().intValue() == ResultCode.SUCCESS.intValue()) {
//				//
//				System.out.println("+*+");
//				
//			}else{
//				//wrong password
//				System.out.println("Wrong password.");
//				ldap.close();
//				return results; 
//			}
				
			//building the search query
			String searchQuery;
			if(sciper != null){
				searchQuery = "(uniqueIdentifier="+sciper+")";
			}else if(first_name != null && last_name != null){
				searchQuery = "(&(sn="+last_name+ ")(givenName="+first_name+"))";
			}else if(first_name != null){
				searchQuery = "(givenName="+first_name+")";
			}else if(last_name != null)
				searchQuery = "(sn="+last_name+")";
			else{
				//should not append, lastname and firstname are null
				ldap.close();
				return results;
				
			}
			
			// search part			
			// TODO add the sizeLimit param
			SearchResult searchResult = ldap.search("o=epfl,c=ch",
										SearchScope.SUB,
										searchQuery);
			
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
					
			
			ldap.close();
			
		} 
//		catch (GeneralSecurityException e1) {
//			// ssl problem
//			
//		}
		catch (LDAPException e) {
			//ldap problem
			System.out.println("ldap exception");
		} 
		return results;
	}
	
	
	public static LinkedList<Person> searchBySciper(String sciper, String username, String password){
		return search(sciper, null, null, username, password);
	}

	public static LinkedList<Person> searchByName(String first_name, String last_name, String username, String password){
		return search(null, first_name, last_name, username, password);
	}
	
		
}
