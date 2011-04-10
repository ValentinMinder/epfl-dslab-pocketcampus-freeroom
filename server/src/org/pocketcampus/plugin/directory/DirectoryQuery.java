package org.pocketcampus.plugin.directory;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import org.pocketcampus.shared.directory.Person;

import com.unboundid.ldap.sdk.BindResult;
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





public class DirectoryQuery {

	
	
	private static LinkedList<Person> search(String sciper, String first_name, String last_name, String username, String password){
		
		LinkedList<Person> results = new LinkedList<Person>();
		
		LDAPConnection ldap;
		try {
			//ldap = new LDAPConnection();
		
			SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());
			SSLSocketFactory socketFactory = sslUtil.createSSLSocketFactory();
			ldap = new LDAPConnection(socketFactory,
			     "ldap.epfl.ch", 636);
		
		
					
			//auth part
			SearchResult searchResult = ldap.search("o=epfl,c=ch", SearchScope.SUB, "(uid="+username+")");
			

			List<SearchResultEntry> lresults = searchResult.getSearchEntries();

			if(lresults.isEmpty()) {
				System.out.println("Wrong username.");
				ldap.close();
				return results;
			}

			String dn = lresults.get(0).getDN();

			BindResult bResult = ldap.bind(dn, password);
				
			if(bResult.getResultCode().intValue() == ResultCode.SUCCESS.intValue()) {
			}else{
				//wrong password
				System.out.println("Wrong password.");
				ldap.close();
				return results; 
			}
				
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
			searchResult = ldap.search("o=epfl,c=ch",
										SearchScope.SUB,
										searchQuery);
			
			for (SearchResultEntry e : searchResult.getSearchEntries())
			{
				Person p = new Person(
						  e.getAttributeValue("givenName"),
						  e.getAttributeValue("sn"),
						  e.getAttributeValue("mail"),
						  e.getAttributeValue("labeledURI"),
						  e.getAttributeValue("telephoneNumber"),
						  e.getAttributeValue("roomNumber"),
						  e.getAttributeValue("uniqueIdentifier"));
				
				if( !results.contains(p))
					results.add(p);
			}
					
			
			ldap.close();
			
		} catch (GeneralSecurityException e1) {
			// ssl problem
			
		} catch (LDAPException e) {
			//ldap problem
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
