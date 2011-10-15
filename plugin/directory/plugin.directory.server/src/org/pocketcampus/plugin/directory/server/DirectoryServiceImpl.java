package org.pocketcampus.plugin.directory.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.thrift.TException;
import org.pocketcampus.plugin.directory.shared.DirectoryService;
import org.pocketcampus.plugin.directory.shared.Person;

import com.unboundid.ldap.sdk.DereferencePolicy;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;

public class DirectoryServiceImpl implements DirectoryService.Iface {

	private LDAPConnection ldap;
	
	public DirectoryServiceImpl(){
		System.out.println("Starting Directory plugin server");
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
	
	@Override
	public List<Person> search(String param) throws TException {
		LinkedList<Person> results;
		HashMap<String,Person> hashMap = new HashMap<String,Person>();
		String sciper;
		
		
		try{
			int sciperVal = Integer.valueOf(param);
			System.out.println("directory search via sciper:" + param);
			sciper = param;
			results = search(sciper, null, null, true);
			return results;
		}catch (NumberFormatException e) {
			//ok so the param wasn't just the sciper number
		}
		
		
		
		SearchResult searchResult;
		String searchQuery = buildGlobalSearch(param);
		try {
			if( !ldap.isConnected())
				ldap.reconnect();
			
			
			String[] attWanted = { "givenName", "sn", "mail", "labeledURI", "telephoneNumber", "roomNumber", "uniqueIdentifier", "uid", "ou" };
			int sizeLimit = 150;
			searchResult = ldap.search("o=epfl,c=ch", SearchScope.SUB, DereferencePolicy.FINDING, sizeLimit, 0, false, searchQuery, attWanted); 
			//System.out.println(searchResult.getSearchEntries().get(0).toLDIFString());
			
			String t[] = new String[2];
			for (SearchResultEntry e : searchResult.getSearchEntries())
			{
				String web = e.getAttributeValue("labeledURI");
				if(web != null){
					t =  web.split(" ");
					web = t[0];
				}
				sciper = e.getAttributeValue("uniqueIdentifier");
				Person p = new Person(
						e.getAttributeValue("givenName"),
						e.getAttributeValue("sn"),
						sciper);
				p.setMail(e.getAttributeValue("mail"));
				p.setWeb(web);
				p.setPhone_number(e.getAttributeValue("telephoneNumber"));
				p.setOffice(e.getAttributeValue("roomNumber"));
				p.setGaspar(e.getAttributeValue("uid"));
				p.setOu(e.getAttributeValue("ou"));
				
				
//				System.out.println(p.ou);
				
				hashMap.put(sciper, p);
				
//				if( !results.contains(p))
//					results.add(p);
				
			}

		
		} catch (LDAPSearchException e1) {
			System.out.println("ldap search problem: " + e1.getMessage());
		} catch (LDAPException e) {
			System.out.println("ldap reconnection problem");
		}
		
		results = new LinkedList<Person>(hashMap.values());
		//sorting the results alphabetatically
		Collections.sort(results);
		System.out.println("Directory: " + results.size() + "persons found for query: " + searchQuery);
		return results;
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
			
			
			//searchResult = ldap.search("o=epfl,c=ch", SearchScope.SUB, searchQuery);
			String[] attWanted = { "givenName", "sn", "mail", "labeledURI", "telephoneNumber", "roomNumber", "uniqueIdentifier" };
			int sizeLimit = 150;
			searchResult = ldap.search("o=epfl,c=ch", SearchScope.SUB, DereferencePolicy.FINDING, sizeLimit, 0, false, searchQuery, attWanted); 
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
						e.getAttributeValue("uniqueIdentifier"));
				p.setWeb(web);
				p.setPhone_number(e.getAttributeValue("telephoneNumber"));
				p.setOffice(e.getAttributeValue("roomNumber"));
				p.setGaspar(e.getAttributeValue("uid"));
				
				System.out.println(p);
				
				if( !results.contains(p))
					results.add(p);
				
			}
			//sorting the results alphabetatically
			Collections.sort(results);
		
		} catch (LDAPSearchException e1) {
			System.out.println("ldap search problem: " + e1.getMessage());
		} catch (LDAPException e) {
			System.out.println("ldap reconnection problem");
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
	
	private String buildGlobalSearch(String param){
		return "(displayName~="+param+")";
	}

	


}
