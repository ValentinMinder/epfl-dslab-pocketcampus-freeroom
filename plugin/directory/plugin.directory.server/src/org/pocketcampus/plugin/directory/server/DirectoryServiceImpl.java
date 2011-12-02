package org.pocketcampus.plugin.directory.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.thrift.TException;
import org.pocketcampus.platform.sdk.shared.utils.NetworkUtil;
import org.pocketcampus.platform.sdk.shared.utils.StringUtils;
import org.pocketcampus.plugin.directory.shared.DirectoryService;
import org.pocketcampus.plugin.directory.shared.NoPictureFound;
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
	
	//LDAP SETTINGS
	private static final String LDAP_ADDRESS= "ldap.epfl.ch";
	private static int LDAP_PORT = 389;
	
	//LDAP search params
	private static final int NB_RESULT_LIMIT = 500;
	private String[] attWanted = { "givenName", "sn", "mail", "labeledURI", "telephoneNumber", "roomNumber", "uniqueIdentifier", "uid", "ou" };
	
	//stuff to get the picture
	private static final String pictureCamiproBase = "http://people.epfl.ch/cache/photos/camipro/";
	private static final String pictureExtBase = "http://people.epfl.ch/cache/photos/ext/";
	private static final String pictureExtension = ".jpg";
	
	//list of names for autocompletion
	private static ArrayList<String> given_names;
	private static ArrayList<String> second_names;
	
	
	public DirectoryServiceImpl(){
		System.out.println("Starting Directory plugin server");
		ldap = new LDAPConnection();
		
		
		getNamesForDisk();
		
		connectLdap();
	}
	
	private void connectLdap(){
		try {
			ldap.connect(LDAP_ADDRESS, LDAP_PORT);
		}catch (LDAPException e) {
			System.out.println("Ldap exception");
		}
	}
	
	private void getNamesForDisk(){
		given_names = new ArrayList<String>();
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader("EPFL-givenNames.txt"));
			String line;
			while(true){
				line = br.readLine();
				given_names.add(line);
				
				if(line == null)
					break;
			}
		}catch (FileNotFoundException e) {
			System.out.println("please run tool.LdapExtractor to get autocomplete");
		}catch (IOException e) {
			System.out.println("IO exception while getting name for auto complete: " + e.getMessage());
		} 
		////////////////////////////////////////////////////////////////////
		second_names = new ArrayList<String>();
		try {
			br = new BufferedReader(new FileReader("EPFL-lastNames.txt"));
			String line;
			while(true){
				line = br.readLine();
				second_names.add(line);
				
				if(line == null)
					break;
			}
		}catch (FileNotFoundException e) {
			System.out.println("please run tool.LdapExtractor to get autocomplete");
		}catch (IOException e) {
			System.out.println("IO exception while getting name for auto complete: " + e.getMessage());
		} 
		
	}
	
	@Override
	public List<Person> search(String param) throws TException, org.pocketcampus.plugin.directory.shared.LDAPException {
		LinkedList<Person> results = new LinkedList<Person>();
		String sciper;
		
		//sciper search - only one (or no) result
		try{
			Integer.valueOf(param);
			System.out.println("directory search via sciper:" + param);
			sciper = param;
			results = searchSciper(sciper);
			return results;
		}catch (NumberFormatException e) {
			//so it's not a sciper, continuing
		}
		
		
		//exact first or last name search
		String searchQuery = "(|(sn=" +param+ ")(givenName=" +param+ "))";
		results = searchOnLDAP(searchQuery);
		Collections.sort(results);
		
		//adding more people with *param* in their diplay name
		searchQuery = "(displayName=*"+param+"*)";
		LinkedList<Person> tmp = searchOnLDAP(searchQuery);
		Collections.sort(tmp);
		for(Person sup : tmp){
			if(!results.contains(sup))
				results.add(sup);
		}
		
		//adding a test person
		if(param.equals("ironman"))results.add(new Person("Iron", "Man", ">9000").setMail("Tony@Stark.com").setWeb("http://www.google.ch").setPhone_number("0765041343").setOffice("Villa near Malibu").setGaspar("ironman").setOu("StarkLabs"));
					

		System.out.println("Directory: " + results.size() + "persons found for param: " + param);
		return results;
	}
	
	private LinkedList<Person> searchSciper(String sciper) throws org.pocketcampus.plugin.directory.shared.LDAPException{
		return searchOnLDAP("(uniqueIdentifier="+sciper+")");
	}
	
	private LinkedList<Person> searchOnLDAP(String searchQuery) throws org.pocketcampus.plugin.directory.shared.LDAPException{
		LinkedList<Person> results = new LinkedList<Person>();
		
		SearchResult searchResult;
		try {
			if( !ldap.isConnected())
				ldap.reconnect();
			
			//search the ldap
			searchResult = ldap.search("o=epfl,c=ch", SearchScope.SUB, DereferencePolicy.FINDING, NB_RESULT_LIMIT, 0, false, searchQuery, attWanted); 
			//if attWanted is null, this will print out all the info the ldap can give
			//System.out.println(searchResult.getSearchEntries().get(0).toLDIFString()); 
			
			//adding persons from the search to our result list
			for (SearchResultEntry e : searchResult.getSearchEntries())
			{
				//getting the interessant part of the url
				String t[] = new String[2];
				String web = e.getAttributeValue("labeledURI");
				if(web != null){
					t =  web.split(" ");
					web = t[0];
				}
				
				//creating the new person
				Person p = new Person(
						e.getAttributeValue("givenName"),
						e.getAttributeValue("sn"),
						e.getAttributeValue("uniqueIdentifier"));
				p.setMail(e.getAttributeValue("mail"));
				p.setWeb(web);
				p.setPhone_number(e.getAttributeValue("telephoneNumber"));
				p.setOffice(e.getAttributeValue("roomNumber"));
				p.setGaspar(e.getAttributeValue("uid"));
				p.setOu(e.getAttributeValue("ou"));
				
				//no duplicates!
				if( !results.contains(p))
					results.add(p);
				
			}
			
		} catch (LDAPSearchException e1) {
			if(e1.getMessage().equals("size limit exceeded")){
				System.out.println("ldap search problem: " + e1.getMessage());
				throw new org.pocketcampus.plugin.directory.shared.LDAPException("too many results");
			}
					
		} catch (LDAPException e) {
			System.out.println("ldap reconnection problem");
			throw new org.pocketcampus.plugin.directory.shared.LDAPException("EPFL LDAP Problem, try again later");
		}
		

		return results;
	
	}
	
	@Override
	public String getProfilePicture(String sciper) throws TException, NoPictureFound {
		byte[] sciperBytes = null;
		byte[] digest = null;
		
		//special case for ironman
		if(sciper.equals(">9000"))
			return "http://1.bp.blogspot.com/_8GJbAAr1DY8/TLymwhZs-5I/AAAAAAAABzA/11t3g4HmSuI/s1600/ironman_movie.jpg";
		
		//normal people part
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
		
		if(NetworkUtil.checkUrlStatus(pictureCamiproUrl)) {
			System.out.println(pictureCamiproUrl);
			return pictureCamiproUrl;
		}
		
		if(NetworkUtil.checkUrlStatus(pictureExtUrl)) {
			System.out.println(pictureExtUrl);
			return pictureExtUrl;
		}
		
		System.out.println("sorry exception");
		throw new org.pocketcampus.plugin.directory.shared.NoPictureFound().setMessage("sorry");
		
	}

	@Override
	public List<String> autocompleteGivenName(String arg0) throws TException {
		return autoComplete(arg0, given_names);
	}

	@Override
	public List<String> autocompleteSecondName(String arg0) throws TException {
		return autoComplete(arg0, second_names);
	}
	
	private List<String> autoComplete(String constraint, List<String> list){
		ArrayList<String> prop = new ArrayList<String>();
		
		for(String s : list){
			StringUtils.capitalize(s);
			if(s.startsWith(constraint))
				prop.add(s);
		}
		
		return prop;
	}

	

	


}
