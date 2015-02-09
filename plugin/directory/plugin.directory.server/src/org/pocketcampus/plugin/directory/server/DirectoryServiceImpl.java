package org.pocketcampus.plugin.directory.server;

import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.*;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.controls.SimplePagedResultsControl;
import org.apache.commons.io.IOUtils;
import org.apache.thrift.TException;
import org.pocketcampus.platform.server.StateChecker;
import org.pocketcampus.platform.shared.utils.NetworkUtils;
import org.pocketcampus.platform.shared.utils.StringUtils;
import org.pocketcampus.plugin.directory.shared.*;

import java.io.IOException;
import java.util.*;

/**
 * Class that manages the services the server side of Directory provides to the client.
 * 
 * @author amer
 * 
 */
public class DirectoryServiceImpl implements DirectoryService.Iface, StateChecker {

	/** The connection to the EPFL ldap server */
	private LDAPInterface ldap;

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
		System.out.println("Starting Directory plugin server...");
		
		try {
			ldap =  new LDAPConnectionPool(new LDAPConnection("ldap.epfl.ch", 389), 1, 1); // need to have only 1 connection, because of ldap cookie crap
		} catch (LDAPException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int checkState() throws IOException {
		Process proc = Runtime.getRuntime().exec(new String[]{ "/bin/sh", "-c", "lsof | grep ldap.epfl.ch | wc -l" });
		String status = IOUtils.toString(proc.getInputStream(), "UTF-8").trim();
		return (Integer.parseInt(status) > 10 ? 500 : 200 );
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
		} catch (LDAPException e) {
			e.printStackTrace();
			return new DirectoryResponse(500);
		}
	}

	private LinkedList<Person> searchOnLDAP(String searchQuery, Pagination pag, String lang) throws LDAPException {
		// LinkedList<Person> results = new LinkedList<Person>();
		HashMap<String, Person> results = new HashMap<String, Person>();

		if (lang == null || !lang.equals("fr"))
			lang = "en";

		String attributeKeyAppendix = (lang.equals("en") ? ";lang-en" : "");

	
		do {
			SearchResult searchResult;

			if (pag != null) {

				// search with pagination
				// http://snipplr.com/view/52024/
				SearchRequest searchRequest = new SearchRequest("c=ch", SearchScope.SUB, searchQuery, attWanted);
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
				searchResult = ldap.search("c=ch", SearchScope.SUB, DereferencePolicy.FINDING, NB_RESULT_LIMIT, 0, false, searchQuery, attWanted);
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
				String [] unit = e.getAttributeValues("ou" + attributeKeyAppendix);
				String title = e.getAttributeValue("description" + attributeKeyAppendix);
				DirectoryPersonRole role = new DirectoryPersonRole(unit != null ? last(unit) : unitAcro, title != null ? title : "");
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
