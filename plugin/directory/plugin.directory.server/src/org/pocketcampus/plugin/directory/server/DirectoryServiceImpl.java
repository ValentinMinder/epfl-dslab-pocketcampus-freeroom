package org.pocketcampus.plugin.directory.server;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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

/**
 * Class that manages the services the server side of Directory provides to the client.
 * @author Pascal <pascal.scheiben@gmail.com>
 */
public class DirectoryServiceImpl implements DirectoryService.Iface {

	/** The connection to the EPFL ldap server */
	private LDAPConnection ldap;
	
	//LDAP SETTINGS
	/** hostname of the ldap server */
	private static final String LDAP_ADDRESS= "ldap.epfl.ch";
	/** port of the ldap server */
	private static int LDAP_PORT = 389;
	
	//LDAP search params
	/** Limit of max result */
	private static final int NB_RESULT_LIMIT = 500;
	/** Set of wanted attributes: first name, lastname, mail, url, phone number, office, gaspar account, sciper, organizational units*/
	private String[] attWanted = { "givenName", "sn", "mail", "labeledURI", "telephoneNumber", "roomNumber", "uniqueIdentifier", "uid", "ou" };
	/** Limited set of attributes */
	private String[] onlyNameWanted = {"givenName", "sn"};
	
	//stuff to get the picture
	private static final String pictureCamiproBase = "http://people.epfl.ch/cache/photos/camipro/";
	private static final String pictureExtBase = "http://people.epfl.ch/cache/photos/ext/";
	private static final String pictureExtension = ".jpg";

	/** Maximum number of result for the autocompletion*/
	private static final int MAX_SUGGESTION_NUMBER = 25;
	
	//list of names for autocompletion
	/** Given names at epfl, used for autocompletion*/
	private static ArrayList<String> given_names;
	/** Last names at epfl, used for autocompletion*/
	private static ArrayList<String> second_names;
	
	private static String date_last_fetched = "";
	
	//database stuff
	/** Directory database manager*/
	DirectoryDatabase connectionManager = new DirectoryDatabase();
	
	/**
	 * Constructor, no arguments needed
	 */
	public DirectoryServiceImpl(){
		System.out.println("Directory plugin server started");
		ldap = new LDAPConnection();
		
		
		getNamesFromDatabase();
		
		
		connectLdap();
	}
	
	/**
	 * Initiates the connection to the ldap server
	 */
	private void connectLdap(){
		try {
			ldap.connect(LDAP_ADDRESS, LDAP_PORT);
		}catch (LDAPException e) {
			System.err.println("Ldap exception");
		}
	}
	
	/**
	 * Obtain all the names (first and last) from the database
	 */
	private void getNamesFromDatabase(){

		if(date_last_fetched.equals(new SimpleDateFormat("d").format(new Date()))) {
			return;
		}
		date_last_fetched = new SimpleDateFormat("d").format(new Date());
		System.out.println("re-fetching names from DB");
		
		given_names = (ArrayList<String>) connectionManager.getFirstNames();
		second_names = (ArrayList<String>) connectionManager.getLastNames();
		
		System.out.println("Nb of first names: "+given_names.size());
		System.out.println("Nb of last names: "+second_names.size());
	}
	
	/**
	 * Method called by the client to search Persons.
	 * First it tries to interpret the parameter as a number to search for a sciper on the ldap.
	 * If this fails, it search the ldap for a complete first or last name. And then adds to the results
	 * any person who has the param in his display name. The display name include second given names.
	 * 
	 * The result will be composed of two concatened lists, the first containing alphabetically sorted high relevance result
	 * and the second containing partial matching result sorted as well.
	 * 
	 * @param param The parameter can be a sciper, a displayname (Lucky Luke), a first name (Lucky), a last name (Luke) or only a part (Lu)
	 * @return A list of <code>Persons</code> 
	 */
	@Override
	public List<Person> searchPersons(String param) throws TException, org.pocketcampus.plugin.directory.shared.LDAPException {
		getNamesFromDatabase();
		param = param.trim();
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
	
	/**
	 * Method called by the client to get the url of the picture of a Person via his/her sciper number
	 * @param sciper The sciper number of the person
	 * @return The url of the profile picture
	 * @throws <code>org.pocketcampus.plugin.directory.shared.NoPictureFound</code> if no picture is found for this user
	 */
	@Override
	public String getProfilePicture(String sciper) throws TException, NoPictureFound {
		getNamesFromDatabase();
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

	/**
	 * Method called by the client to get autocomplete on names.
	 * 
	 * If the constraint doesn't contain a space, uses the local list of first and last names to autocomplete.
	 * 
	 * If a space is present, check if the last name is given first (Luke Luc..). When this is the case, calls a specific method
	 * to look up for a full last name and a partial last name. Then it adds every one having the constraint in the display name.
	 * Someone "Lucky Luk" will be found at this time.
	 * 
	 * @param constraint The partial name of a <code>Person</code> you are looking for.
	 * @return A list of suggested Name corresponding to the constraint.
	 */
	@Override
	public List<String> autocomplete(String constraint) throws TException {
		getNamesFromDatabase();
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
//				System.out.println("looking for: " + partialFirstName+ "... " + lastName);
				suggestions = searchForLastNameAndPartialFirstName(lastName, partialFirstName);
			}
			
			//add a method searchForFirstNameAndPartialLast Name to include people with middle names
			//because someone like Alain Henri Dupuis will not be found if you search "Alain Dup"
			suggestions.addAll(searchForDisplayName(StringUtils.capitalize(constraint)));
			
			return suggestions;
			
		}else{
			for(String fname: given_names){
				if(StringUtils.removeAccents(fname).startsWith(StringUtils.capitalize(constraint)) ||
						fname.startsWith(StringUtils.capitalize(constraint))) {
					suggestions.add(fname);
				}
						
			}
			
			for(String lname: second_names){
				if(StringUtils.removeAccents(lname).startsWith(StringUtils.capitalize(constraint)) ||
						lname.startsWith(StringUtils.capitalize(constraint))) {
					suggestions.add(lname);
				}
			}
			
			return suggestions.subList(0, Math.min(suggestions.size(), MAX_SUGGESTION_NUMBER));
		}
	}
	
	
	/**
	 * This is one of the method called by all the others searchSomething of the class.
	 * It takes a ldap formatted search query as paramter and convert LDAP exception to pocketcampus exception that can be handed over to the client.
	 * 
	 * It also regroup different ldap entries for the same person to one with all the organizational units. E.g. for people who works in different labs at the same time.
	 *  
	 * @param searchQuery The ldap formatted query you want to search
	 * @return List of <code>Person</code>s without duplicates.
	 * @throws org.pocketcampus.plugin.directory.shared.LDAPException
	 */
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
							duplicatePerson.OrganisationalUnit.addAll(p.OrganisationalUnit);
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
	
	/**
	 * Search the ldap for the specified sciper using the searchOnLDAP method
	 * @param sciper sciper number
	 * @return List of person having exactly this sciper (normally only one result)
	 * @throws org.pocketcampus.plugin.directory.shared.LDAPException
	 */
	private LinkedList<Person> searchSciper(String sciper) throws org.pocketcampus.plugin.directory.shared.LDAPException{
		return searchOnLDAP("(uniqueIdentifier="+sciper+")");
	}
	
	/**
	 * This is the other method called by all the others searchSomething of the class.
	 * This one is used for all the autocomplete stuff.
	 * 
	 * The difference is that this one doesn't request the full set of people's attributes. Only the name are asked.
	 * 
	 * @param query The ldap formatted query you want to search
	 * @return A list of names
	 */
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
	
	/**
	 * Looks for Complete first name and partial last name. Doesn't include people having middle names.
	 * @param name Complete first name and partial last name
	 * @return List of names
	 */
	private ArrayList<String> searchForDisplayName(String name) {
		return searchForName("(displayName="+name+"*)");
	}

	/**
	 * Looks for a complete last name and partial first name.
	 * @param lastName Complete last name
	 * @param partialFirstName Partial first name
	 * @return List of names.
	 */
	private ArrayList<String> searchForLastNameAndPartialFirstName(String lastName, String partialFirstName) {
		if(partialFirstName.length() == 0)
			return searchForName("(sn=" +lastName+")");
		else
			return searchForName("(&(sn=" +lastName+")(givenName=" + partialFirstName +"*))");
	}
}
