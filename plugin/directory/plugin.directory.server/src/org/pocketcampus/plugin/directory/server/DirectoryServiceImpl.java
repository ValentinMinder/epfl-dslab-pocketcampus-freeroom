package org.pocketcampus.plugin.directory.server;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
	private String[] onlyNameWanted = {"givenName", "sn"};
	
	//stuff to get the picture
	private static final String pictureCamiproBase = "http://people.epfl.ch/cache/photos/camipro/";
	private static final String pictureExtBase = "http://people.epfl.ch/cache/photos/ext/";
	private static final String pictureExtension = ".jpg";
	
	//list of names for autocompletion
	private static ArrayList<String> given_names;
	private static ArrayList<String> second_names;
	
	//database stuff
	DirectoryDatabase connectionManager = new DirectoryDatabase();
	
	public DirectoryServiceImpl(){
		System.out.println("Directory plugin server started");
		ldap = new LDAPConnection();
		
		
		getNamesForDisk();
		
		connectLdap();
	}
	
	private void connectLdap(){
		try {
			ldap.connect(LDAP_ADDRESS, LDAP_PORT);
		}catch (LDAPException e) {
			System.err.println("Ldap exception");
		}
	}
	
	private void getNamesForDisk(){
//		given_names = new ArrayList<String>();
//		
//		BufferedReader br;
//		try {
//			br = new BufferedReader(new FileReader("data" +File.separator+ "EPFL-givenNames.txt"));
//			String line;
//			while(true){
//				line = br.readLine();
//				
//				if(line == null)
//					break;
//				else
//					given_names.add(line);
//			}
//			br.close();
//		}catch (FileNotFoundException e) {
//			System.out.println("please run tool.LdapExtractor to get given name autocomplete");
//		}catch (IOException e) {
//			System.out.println("IO exception while getting name for auto complete: " + e.getMessage());
//		}
//		////////////////////////////////////////////////////////////////////
//		second_names = new ArrayList<String>();
//		try {
//			br = new BufferedReader(new FileReader("data" +File.separator+ "EPFL-lastNames.txt"));
//			String line;
//			while(true){
//				line = br.readLine();
//				
//				if(line == null)
//					break;
//				else
//					second_names.add(line);
//					
//			}
//			br.close();
//		}catch (FileNotFoundException e) {
//			System.out.println("please run tool.LdapExtractor to get autocomplete");
//		}catch (IOException e) {
//			System.out.println("IO exception while getting name for auto complete: " + e.getMessage());
//		} 
		
		given_names = (ArrayList<String>) connectionManager.getFirstNames();
		second_names = (ArrayList<String>) connectionManager.getLastNames();
		
		System.out.println("Nb of first names: "+given_names.size());
		System.out.println("Nb of last names: "+second_names.size());
	}
	
	@Override
	public List<Person> searchPersons(String param) throws TException, org.pocketcampus.plugin.directory.shared.LDAPException {
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
			else{
				Iterator<Person> it = results.iterator();
				
				while(it.hasNext()){
					Person duplicatePerson = it.next();
					
					if(duplicatePerson.equals(sup)){
						sup.OrganisationalUnit.addAll(duplicatePerson.OrganisationalUnit);
						break;
					}
				}
			}
		}
		
		//adding a test person
		ArrayList<String> ouList = new ArrayList<String>();
		ouList.add("Stark Labs");
		ouList.add("S.H.I.E.L.D.");
		if(param.equals("ironman"))results.add(new Person("Iron", "Man", ">9000").setEmail("Tony@Stark.com").setWeb("http://www.google.ch").setPrivatePhoneNumber("0765041343").setOffice("Villa near Malibu").setGaspar("ironman").setOrganisationalUnit(ouList));
					

		System.out.println("Directory: " + results.size() + " person(s) found for param: " + param);
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
				p.setEmail(e.getAttributeValue("mail"));
				p.setWeb(web);
				p.setOfficePhoneNumber(e.getAttributeValue("telephoneNumber"));
				p.setOffice(e.getAttributeValue("roomNumber"));
				p.setGaspar(e.getAttributeValue("uid"));
				ArrayList<String> ouList = new ArrayList<String>();
				ouList.add(e.getAttributeValue("ou"));
				p.setOrganisationalUnit(ouList);
				
				//no duplicates!
				if( !results.contains(p))
					results.add(p);
				else{
					Iterator<Person> it = results.iterator();
					
					while(it.hasNext()){
						Person duplicatePerson = it.next();
						
						if(duplicatePerson.equals(p)){
							p.OrganisationalUnit.addAll(duplicatePerson.OrganisationalUnit);
							break;
						}
					}
				}
				
			}
			
		} catch (LDAPSearchException e1) {
			if(e1.getMessage().equals("size limit exceeded")){
				System.err.println("ldap search problem: " + e1.getMessage());
				throw new org.pocketcampus.plugin.directory.shared.LDAPException("too many results");
			}
					
		} catch (LDAPException e) {
			System.err.println("ldap reconnection problem");
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
		
		System.err.println("sorry exception");
		throw new org.pocketcampus.plugin.directory.shared.NoPictureFound().setMessage("sorry");
		
	}


	@Override
	public List<String> autocomplete(String constraint) throws TException {
		ArrayList<String> suggestions = new ArrayList<String>();
//		suggestions.add("Pascal Scheiben");
//		suggestions.add("Florian Laurent");
//		suggestions.add("ironman");
		
		if(constraint.contains(" ")){
			String name = constraint.substring(0, constraint.indexOf(" "));
			name = StringUtils.capitalize(name);
			if(second_names.contains(name)){
				String partialFirstName = constraint.substring(constraint.indexOf(" ")+1);
				partialFirstName = StringUtils.capitalize(partialFirstName);
				String lastName = StringUtils.capitalize(name);
				System.out.println("looking for: " + partialFirstName+ "... " + lastName);
				suggestions = searchForLastNameAndPartialFirstName(lastName, partialFirstName);
			}
			
			suggestions.addAll(searchForDisplayName(StringUtils.capitalize(constraint)));
			
			return suggestions;
			
		}else{
			for(String fname: given_names){
				if(StringUtils.removeAccents(fname).startsWith(StringUtils.capitalize(constraint))) {
					suggestions.add(fname);
				}
						
			}
			
			for(String lname: second_names){
				if(StringUtils.removeAccents(lname).startsWith(StringUtils.capitalize(constraint))) {
					suggestions.add(lname);
				}
			}
			
			return suggestions.subList(0, 25);
		}
	}

	private ArrayList<String> searchForDisplayName(String name) {
		return searchForName("(displayName="+name+"*)");
	}

	private ArrayList<String> searchForLastNameAndPartialFirstName(String lastName, String partialFirstName) {
		if(partialFirstName.length() == 0)
			return searchForName("(sn=" +lastName+")");
		else
			return searchForName("(&(sn=" +lastName+")(givenName=" + partialFirstName +"*))");
	}
	
	private ArrayList<String> searchForName(String query){
		ArrayList<String> results = new ArrayList<String>();
		try {
			if (!ldap.isConnected())
				ldap.reconnect();

			// search the ldap
			SearchResult searchResult = ldap.search("o=epfl,c=ch",
					SearchScope.SUB, DereferencePolicy.FINDING,
					NB_RESULT_LIMIT, 0, false, query, onlyNameWanted);
			
			for (SearchResultEntry e : searchResult.getSearchEntries())
			{
				String nameToDisplay= e.getAttributeValue("givenName") + " " + e.getAttributeValue("sn");
				if(!results.contains(nameToDisplay))
					results.add(nameToDisplay);
			}
			
		} catch (LDAPException e) {
			e.printStackTrace();
		}

		return results;
	}

}
