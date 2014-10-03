package org.pocketcampus.plugin.directory.server;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.pocketcampus.platform.shared.utils.NetworkUtils;
import org.pocketcampus.platform.shared.utils.StringUtils;
import org.pocketcampus.plugin.directory.shared.DirectoryPersonRole;
import org.pocketcampus.plugin.directory.shared.DirectoryRequest;
import org.pocketcampus.plugin.directory.shared.DirectoryResponse;
import org.pocketcampus.plugin.directory.shared.DirectoryService;
import org.pocketcampus.plugin.directory.shared.NoPictureFound;
import org.pocketcampus.plugin.directory.shared.Person;

import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.DereferencePolicy;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.controls.SimplePagedResultsControl;

/**
 * Class that manages the services the server side of Directory provides to the client.
 * 
 * @author amer
 * 
 */
public class DirectoryServiceImpl implements DirectoryService.Iface {

	/** The connection to the EPFL ldap server */
	private LDAPConnection ldap;

	// LDAP search params
	/** Limit of max result */
	private static final int NB_RESULT_LIMIT = 500;
	/** Page size */
	private static final int PAGE_SIZE = 30;
	/** Set of wanted attributes: first name, lastname, mail, url, phone number, office, gaspar account, sciper, organizational units */
	private String[] attWanted = { "givenName", "sn", "mail", "labeledURI", "telephoneNumber", "roomNumber", "uniqueIdentifier", "uid", "ou", "ou;lang-en", "description",
			"description;lang-en", "employeeType" };
	/** Limited set of attributes */
	private String[] onlyNameWanted = { "givenName", "sn" };

	/** Maximum number of result for the autocompletion */
	private static final int MAX_SUGGESTION_NUMBER = 25;

	// list of names for autocompletion
	/** Given names at epfl, used for autocompletion */
	private static ArrayList<String> given_names;
	/** Last names at epfl, used for autocompletion */
	private static ArrayList<String> second_names;

	private static String date_last_fetched = "";

	// database stuff
	/** Directory database manager */
	DirectoryDatabase connectionManager = new DirectoryDatabase();

	/**
	 * Constructor, no arguments needed
	 */
	public DirectoryServiceImpl() {
		System.out.println("Directory plugin server started");
		ldap = new LDAPConnection();

		getNamesFromDatabase();
		try {
			ldap.connect("ldap.epfl.ch", 389);
		} catch (LDAPException e) {
			System.err.println("Ldap exception");
		}

	}

	/**
	 * Obtain all the names (first and last) from the database
	 */
	private void getNamesFromDatabase() {

		if (date_last_fetched.equals(new SimpleDateFormat("d").format(new Date()))) {
			return;
		}
		date_last_fetched = new SimpleDateFormat("d").format(new Date());
		System.out.println("re-fetching names from DB");

		given_names = (ArrayList<String>) connectionManager.getFirstNames();
		second_names = (ArrayList<String>) connectionManager.getLastNames();

		System.out.println("Nb of first names: " + given_names.size());
		System.out.println("Nb of last names: " + second_names.size());
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
	 * @param param
	 *            The parameter can be a sciper, a displayname (Lucky Luke), a first name (Lucky), a last name (Luke) or only a part (Lu)
	 * @return A list of <code>Persons</code>
	 */
	@Override
	public List<Person> searchPersons(String param) throws TException, org.pocketcampus.plugin.directory.shared.LDAPException {
		getNamesFromDatabase();
		param = param.trim();
		LinkedList<Person> results = new LinkedList<Person>();
		String sciper;

		// sciper search - only one (or no) result
		try {
			Integer.valueOf(param);
			System.out.println("directory search via sciper:" + param);
			sciper = param;
			results = searchSciper(sciper);
			return results;
		} catch (NumberFormatException e) {
			// so it's not a sciper, continuing
		}

		// exact first or last name search
		String searchQuery = "(|(sn=" + param + ")(givenName=" + param + "))";
		results = searchOnLDAP2(searchQuery, null, null);
		Collections.sort(results);

		// adding more people with *param* in their diplay name
		searchQuery = "(displayName=*" + param + "*)";
		LinkedList<Person> tmp = searchOnLDAP2(searchQuery, null, null);
		Collections.sort(tmp);
		for (Person sup : tmp) {
			if (!results.contains(sup))
				results.add(sup);
			else {
				Iterator<Person> it = results.iterator();

				while (it.hasNext()) {
					Person duplicatePerson = it.next();

					if (duplicatePerson.equals(sup)) {
						sup.getOrganisationalUnits().addAll(duplicatePerson.getOrganisationalUnits());
						break;
					}
				}
			}
		}

		// adding a test person
		ArrayList<String> ouList = new ArrayList<String>();
		ouList.add("Stark Labs");
		ouList.add("S.H.I.E.L.D.");
		if (param.equals("ironman"))
			results.add(new Person("Iron", "Man", ">9000").setEmail("Tony@Stark.com").setWeb("http://www.google.ch").setPrivatePhoneNumber("0765041343")
					.setOffice("Villa near Malibu").setGaspar("ironman").setOrganisationalUnits(ouList));

		System.out.println("Directory: " + results.size() + " person(s) found for param: " + param);
		return results;
	}

	/**
	 * Method called by the client to get the url of the picture of a Person via his/her sciper number
	 * 
	 * @param sciper
	 *            The sciper number of the person
	 * @return The url of the profile picture
	 * @throws <code>org.pocketcampus.plugin.directory.shared.NoPictureFound</code> if no picture is found for this user
	 */
	@Override
	public String getProfilePicture(String sciper) throws TException, NoPictureFound {
		getNamesFromDatabase();

		// special case for ironman
		if (sciper.equals(">9000"))
			return "http://1.bp.blogspot.com/_8GJbAAr1DY8/TLymwhZs-5I/AAAAAAAABzA/11t3g4HmSuI/s1600/ironman_movie.jpg";

		String pictureExtUrl = "http://people.epfl.ch/cgi-bin/people/getPhoto?id=" + sciper;

		if (NetworkUtils.checkUrlImage(pictureExtUrl)) {
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
	 * @param constraint
	 *            The partial name of a <code>Person</code> you are looking for.
	 * @return A list of suggested Name corresponding to the constraint.
	 */
	@Override
	public List<String> autocomplete(String constraint) throws TException {
		getNamesFromDatabase();
		ArrayList<String> suggestions = new ArrayList<String>();
		// suggestions.add("Pascal Scheiben");
		// suggestions.add("Florian Laurent");
		// suggestions.add("ironman");

		if (constraint.contains(" ")) {
			String name = constraint.substring(0, constraint.indexOf(" "));
			name = StringUtils.capitalize(name);
			if (second_names.contains(name)) {
				String partialFirstName = constraint.substring(constraint.indexOf(" ") + 1);
				partialFirstName = StringUtils.capitalize(partialFirstName);
				String lastName = StringUtils.capitalize(name);
				// System.out.println("looking for: " + partialFirstName+ "... " + lastName);
				suggestions = searchForLastNameAndPartialFirstName(lastName, partialFirstName);
			}

			// add a method searchForFirstNameAndPartialLast Name to include people with middle names
			// because someone like Alain Henri Dupuis will not be found if you search "Alain Dup"
			suggestions.addAll(searchForDisplayName(StringUtils.capitalize(constraint)));

			return suggestions;

		} else {
			for (String fname : given_names) {
				if (StringUtils.removeAccents(fname).startsWith(StringUtils.capitalize(constraint)) ||
						fname.startsWith(StringUtils.capitalize(constraint))) {
					suggestions.add(fname);
				}

			}

			for (String lname : second_names) {
				if (StringUtils.removeAccents(lname).startsWith(StringUtils.capitalize(constraint)) ||
						lname.startsWith(StringUtils.capitalize(constraint))) {
					suggestions.add(lname);
				}
			}

			return suggestions.subList(0, Math.min(suggestions.size(), MAX_SUGGESTION_NUMBER));
		}
	}

	class Pagination {
		ASN1OctetString cookie = null;
	}

	/**
	 * Search the ldap for the specified sciper using the searchOnLDAP method
	 * 
	 * @param sciper
	 *            sciper number
	 * @return List of person having exactly this sciper (normally only one result)
	 * @throws org.pocketcampus.plugin.directory.shared.LDAPException
	 */
	private LinkedList<Person> searchSciper(String sciper) throws org.pocketcampus.plugin.directory.shared.LDAPException {
		return searchOnLDAP2("(uniqueIdentifier=" + sciper + ")", null, null);
	}

	/**
	 * This is the other method called by all the others searchSomething of the class.
	 * This one is used for all the autocomplete stuff.
	 * 
	 * The difference is that this one doesn't request the full set of people's attributes. Only the name are asked.
	 * 
	 * @param query
	 *            The ldap formatted query you want to search
	 * @return A list of names
	 */
	private ArrayList<String> searchForName(String query) {
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
				String nameToDisplay = e.getAttributeValue("givenName") + " " + e.getAttributeValue("sn");
				if (!results.contains(nameToDisplay))
					results.add(nameToDisplay);
			}

		} catch (LDAPException e) {
			e.printStackTrace();
		}

		return results;
	}

	/**
	 * Looks for Complete first name and partial last name. Doesn't include people having middle names.
	 * 
	 * @param name
	 *            Complete first name and partial last name
	 * @return List of names
	 */
	private ArrayList<String> searchForDisplayName(String name) {
		return searchForName("(displayName=" + name + "*)");
	}

	/**
	 * Looks for a complete last name and partial first name.
	 * 
	 * @param lastName
	 *            Complete last name
	 * @param partialFirstName
	 *            Partial first name
	 * @return List of names.
	 */
	private ArrayList<String> searchForLastNameAndPartialFirstName(String lastName, String partialFirstName) {
		if (partialFirstName.length() == 0)
			return searchForName("(sn=" + lastName + ")");
		else
			return searchForName("(&(sn=" + lastName + ")(givenName=" + partialFirstName + "*))");
	}

	@Override
	public DirectoryResponse searchDirectory(DirectoryRequest req) throws TException {
		System.out.println("searchDirectory: " + req.getQuery());
		try {
			Pagination pag = new Pagination();
			if (req.isSetResultSetCookie())
				pag.cookie = new ASN1OctetString(req.getResultSetCookie());
			String q = StringUtils.removeAccents(req.getQuery()).replaceAll("[\\s]+", " ").trim();

            if(q.length()==0){
            	DirectoryResponse resp=new DirectoryResponse(200);
            	resp.setResults(new ArrayList<Person>());
            	return resp;
            }
			String[] query = q.split(" ");
			StringBuilder q2Builder = new StringBuilder();
			for (int i = 0; i < query.length; i++)
				q2Builder.append("(|(cn=" + query[i] + "*)(cn=* " + query[i] + "*)(ou=" + query[i] + "))");
			String q2 = q2Builder.toString();
			String q3 = q.replaceAll(" ", "").replaceAll("^[0]+", "");
			LinkedList<Person> tmp = searchOnLDAP2("(|(&" + q2 + ")(mail=" + q3 + "*)(telephoneNumber=*" + q3 + ")(uid=" + q3 + ")(uniqueidentifier=" + q3 + "))", pag,
					req.getLanguage());
			DirectoryResponse resp = new DirectoryResponse(200);
			resp.setResults(tmp);
			if (pag.cookie != null)
				resp.setResultSetCookie(pag.cookie.getValue());
			return resp;
		} catch (org.pocketcampus.plugin.directory.shared.LDAPException e) {
			e.printStackTrace();
			return new DirectoryResponse(500);
		}
	}

	private LinkedList<Person> searchOnLDAP2(String searchQuery, Pagination pag, String lang) throws org.pocketcampus.plugin.directory.shared.LDAPException {
		// LinkedList<Person> results = new LinkedList<Person>();
		HashMap<String, Person> results = new HashMap<String, Person>();

		if (lang == null || !lang.equals("fr"))
			lang = "en";

		String attributeKeyAppendix = (lang.equals("en") ? ";lang-en" : "");

		SearchResult searchResult;
		do {
			try {
				if (!ldap.isConnected())
					ldap.reconnect();

				if (pag != null) {

					// search with pagination
					// http://snipplr.com/view/52024/
					SearchRequest searchRequest = new SearchRequest("o=epfl,c=ch", SearchScope.SUB, searchQuery, attWanted);
					searchRequest.setControls(new Control[] { new SimplePagedResultsControl(PAGE_SIZE, pag.cookie) });
					searchResult = ldap.search(searchRequest);
					pag.cookie = null;
					for (Control c : searchResult.getResponseControls()) {
						if (c instanceof SimplePagedResultsControl) {
							pag.cookie = ((SimplePagedResultsControl) c).getCookie();
							if ((pag.cookie != null) && (pag.cookie.getValueLength() == 0))
								pag.cookie = null;
						}
					}

				} else {

					// search the ldap
					searchResult = ldap.search("o=epfl,c=ch", SearchScope.SUB, DereferencePolicy.FINDING, NB_RESULT_LIMIT, 0, false, searchQuery, attWanted);
					// if attWanted is null, this will print out all the info the ldap can give
					// System.out.println(searchResult.getSearchEntries().get(0).toLDIFString());

				}

				// adding persons from the search to our result list
				for (SearchResultEntry e : searchResult.getSearchEntries())
				{
					if (!e.hasAttribute("employeeType") || "Ignore".equals(e.getAttributeValue("employeeType")))
						continue;

					// getting the interessant part of the url
					String t[] = new String[2];
					String web = e.getAttributeValue("labeledURI");
					if (web != null) {
						t = web.split(" ");
						web = t[0];
					}

					// creating the new person
					Person p = new Person(
							e.getAttributeValue("givenName"),
							e.getAttributeValue("sn"),
							e.getAttributeValue("uniqueIdentifier"));
					if (p.getFirstName() == null || p.getLastName() == null || p.getSciper() == null)
						continue;
					p.setEmail(e.getAttributeValue("mail"));
					p.setWeb(web);
					p.setOfficePhoneNumber(e.getAttributeValue("telephoneNumber"));
					p.setOffice(e.getAttributeValue("roomNumber"));
					p.setGaspar(e.getAttributeValue("uid"));
					ArrayList<String> ouList = new ArrayList<String>();
					ouList.add(e.getAttributeValue("ou"));
					p.setOrganisationalUnits(ouList);
					p.setPictureUrl("http://people.epfl.ch/cgi-bin/people/getPhoto?id=" + p.getSciper());

					String unitAcro = e.getAttributeValue("ou");
					String [] units = e.getAttributeValues("ou" + attributeKeyAppendix);
					String [] titles = e.getAttributeValues("description" + attributeKeyAppendix);
					DirectoryPersonRole role = new DirectoryPersonRole(last(units), titles != null ? last(titles) : "");
					Map<String, DirectoryPersonRole> roles = new HashMap<String, DirectoryPersonRole>();
					roles.put(unitAcro, role);
					p.setRoles(roles);
					p.setHomepages(cleanHomepages(e.getAttributeValues("labeledURI")));

					// no duplicates!
					if (!results.containsKey(p.getSciper()))
						results.put(p.getSciper(), p);
					else {
						Person op = results.get(p.getSciper());
						op.getOrganisationalUnits().addAll(p.getOrganisationalUnits());
						op.getRoles().putAll(p.getRoles());
						// TODO should make all these Lists
						if(!op.isSetOffice())
							op.setOffice(p.getOffice());
						if(!op.isSetOfficePhoneNumber())
							op.setOfficePhoneNumber(p.getOfficePhoneNumber());
					}

				}

			} catch (LDAPSearchException e1) {
				if (e1.getMessage().equals("size limit exceeded")) {
					System.err.println("ldap search problem: " + e1.getMessage());
					throw new org.pocketcampus.plugin.directory.shared.LDAPException("too many results");
				}

			} catch (LDAPException e) {
				System.err.println("ldap reconnection problem");
				throw new org.pocketcampus.plugin.directory.shared.LDAPException("EPFL LDAP Problem, try again later");
			}

		} while (results.size() < PAGE_SIZE && pag != null && pag.cookie != null);

		return new LinkedList<Person>(results.values());

	}

	private static String last(String[] ss) {
		return ss[ss.length - 1];
	}

	private static Map<String, String> cleanHomepages(String[] ss) {
		if (ss == null)
			return null;
		Map<String, String> homepages = new HashMap<String, String>();
		for (String s : ss) {
			String[] e = s.split(" ", 2);
			if (e.length == 2)
				homepages.put(e[1], e[0]);
		}
		return homepages;
	}
}
