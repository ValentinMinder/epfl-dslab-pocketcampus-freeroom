package org.pocketcampus.plugin.directory.server;

import java.util.ArrayList;
import java.util.HashMap;
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
	
	/**
	 * Constructor, no arguments needed
	 */
	public DirectoryServiceImpl() {
		ldap = new LDAPConnection();

		try {
			ldap.connect("ldap.epfl.ch", 389);
		} catch (LDAPException e) {
			System.err.println("Ldap exception");
		}
		System.out.println("Directory plugin server started");

	}

	@Override
	public List<Person> searchPersons(String param) throws TException, org.pocketcampus.plugin.directory.shared.LDAPException {
		List<Person> result = new LinkedList<Person>();
		DirectoryResponse resp = searchDirectory(new DirectoryRequest(param));
		if(resp.getStatus() != 200) {
			throw new TException("searchDirectory returned status " + resp.getStatus());
		}
		for(Person p : resp.getResults()) {
			result.add(p);
		}
		return result;
	}

	@Override
	public String getProfilePicture(String sciper) throws TException, NoPictureFound {
		String pictureExtUrl = "http://people.epfl.ch/cgi-bin/people/getPhoto?id=" + sciper;
		if (NetworkUtils.checkUrlImage(pictureExtUrl)) {
			return pictureExtUrl;
		}
		throw new org.pocketcampus.plugin.directory.shared.NoPictureFound();
	}

	@Override
	public List<String> autocomplete(String constraint) throws TException {
		List<String> result = new LinkedList<String>();
		DirectoryResponse resp = searchDirectory(new DirectoryRequest(constraint));
		if(resp.getStatus() != 200) {
			throw new TException("searchDirectory returned status " + resp.getStatus());
		}
		for(Person p : resp.getResults()) {
			result.add(p.getFirstName() + " " + p.getLastName());
		}
		return result;
	}

	class Pagination {
		ASN1OctetString cookie = null;
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
			LinkedList<Person> tmp = searchOnLDAP("(|(&" + q2 + ")(mail=" + q3 + "*)(telephoneNumber=*" + q3 + ")(uid=" + q3 + ")(uniqueidentifier=" + q3 + "))", pag,
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

	private LinkedList<Person> searchOnLDAP(String searchQuery, Pagination pag, String lang) throws org.pocketcampus.plugin.directory.shared.LDAPException {
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
