package org.pocketcampus.tool.ldapExtractor;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.LinkedList;


import com.unboundid.ldap.sdk.DereferencePolicy;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;

public class LdapExtractor {

	private static LDAPConnection ldap;
	
	//LDAP SETTINGS
	private static final String LDAP_ADDRESS= "ldap.epfl.ch";
	private static int LDAP_PORT = 389;
	
	//LDAP search params
	private static final int NB_RESULT_LIMIT = 150000;
	private static String[] attWanted = { "givenName" };
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PrintWriter out;
		
		ldap = new LDAPConnection();
		connectLdap();
		
		LinkedList<String> gn_results = new LinkedList<String>();
		LinkedList<String> sn_results = new LinkedList<String>();
		int cpt = 0;
		
		
		
		/////////////////// given name
		try {
			out = new PrintWriter("../../platform/launcher/platform.launcher.server/data/EPFL-givenNames.txt");
			
			String searchQuery = "(givenName=*)";
			SearchResult searchResult;
			
			System.out.println("Searching all givenNames");
			searchResult = ldap.search("o=epfl,c=ch", SearchScope.SUB, DereferencePolicy.FINDING, NB_RESULT_LIMIT, 0, false, searchQuery, attWanted);

			for (SearchResultEntry e : searchResult.getSearchEntries())
			{		
				//no duplicates!
				String name = e.getAttributeValue("givenName");
				//pierre andré stéphane pastiche > pierre
				String[] splitted = name.split(" ");
				name = splitted[0];
				if( !gn_results.contains(name)){
					gn_results.add(name);
					out.println(name);
				}
				cpt++;
				if(cpt%250==0){
					System.out.print("x");
					if(cpt%2500 == 0)
						System.out.println();
				}
			}
			out.close();
			
		} catch (FileNotFoundException e2) {
			System.out.println("File problem: " + e2.getMessage());
		} catch (LDAPSearchException e) {
			System.out.println("Ldap problem: " + e.getMessage());
		} 
		
		
		/////////////////// last name
		cpt=0;
		try {
			out = new PrintWriter("../../platform/launcher/platform.launcher.server/data/EPFL-lastNames.txt");
			
			String searchQuery = "(sn=*)";
			SearchResult searchResult;
			
			System.out.println("\nSearching all surNames");
			attWanted[0] = "sn";
			searchResult = ldap.search("o=epfl,c=ch", SearchScope.SUB, DereferencePolicy.FINDING, NB_RESULT_LIMIT, 0, false, searchQuery, attWanted);

			for (SearchResultEntry e : searchResult.getSearchEntries())
			{		
				//no duplicates!
				String name = e.getAttributeValue("sn");
				if( !sn_results.contains(name)){
					sn_results.add(name);
					out.println(name);
				}
				cpt++;
				if(cpt%250==0){
					System.out.print("x");
					if(cpt%2500 == 0)
						System.out.println();
				}

			}
			out.close();
			
		} catch (FileNotFoundException e2) {
			System.out.println("File problem: " + e2.getMessage());
		} catch (LDAPSearchException e) {
			System.out.println("Ldap problem: " + e.getMessage());
		} 
		
		System.out.println("\nFound " + gn_results.size() + " given names");
		System.out.println("Found " + sn_results.size() + " last names");
		
		
		Collections.sort(gn_results);
		Collections.sort(sn_results);

	}
	
	private static void connectLdap(){
		try {
			ldap.connect(LDAP_ADDRESS, LDAP_PORT);
		}catch (LDAPException e) {
			System.out.println("Ldap exception");
		}
	}

}
