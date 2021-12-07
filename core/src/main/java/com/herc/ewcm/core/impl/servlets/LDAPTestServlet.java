package com.herc.ewcm.core.impl.servlets;

import java.io.IOException;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@SlingServlet(paths = "/c/aem/servlets/services/ldap", methods = "GET")
public class LDAPTestServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(LDAPTestServlet.class);

	/**
	 * This method handle all get requests.
	 * 
	 */
	public void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
		log.debug("Entering into doPost method");
		RequestPathInfo requestPathInfo = request.getRequestPathInfo();
		String resourcePath = requestPathInfo.getResourcePath();
		String selector = requestPathInfo.getSelectorString();
		String extension = requestPathInfo.getExtension();
		String suffix = requestPathInfo.getSuffix();
		String zipcode = null;
		String cat[] = null;
		String spec1 = null;
		log.info("ZipCode : " + zipcode);
		log.info("resourcePath : " + resourcePath);
		log.info("selector : " + selector);
		log.info("extension : " + extension);
		log.info("suffix : " + suffix);
		Hashtable<String, String> ldapdetails = new Hashtable<String, String>();
		ldapdetails.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		ldapdetails.put(Context.PROVIDER_URL, "ldap://hertz1880.ecom.ad.hertz.com:389");
		ldapdetails.put(Context.SECURITY_AUTHENTICATION, "simple");
		ldapdetails.put(Context.SECURITY_PRINCIPAL, "cn=root");
		ldapdetails.put(Context.SECURITY_CREDENTIALS, "ldaptest");
		// ldapdetails.put("com.sun.jndi.ldap.connect.pool", "true");
		String MY_SEARCHBASE = "cn=Member_Profile,cn=Herc_Profile,ou=Users,o=hertz";

		String MY_FILTER = "(objectClass=top)";

		String MY_ATTRS[] = { "dc", "com" };
		try {
			DirContext dirContext = new InitialDirContext(ldapdetails);
			if (null == dirContext) {
				log.debug("dirContext is null");
			} else {
				log.debug("dirContext is not  null");
			}

			log.debug("***************** start ***************** ");
			// SearchControls constraints = new SearchControls();

			SearchControls searchControls = new SearchControls();
			log.debug("***************** searchControls ***************** ");
			searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			log.debug("----------------------- " + SearchControls.SUBTREE_SCOPE + " ***************** ");

			SearchControls searchcontrols = new SearchControls(SearchControls.SUBTREE_SCOPE, 1L, // count
																									// limit
					0, // time limit
					null,// attributes (null = all)
					false,// return object ?
					false);// dereference links?
			log.debug("before search");
			SearchControls constraints = new SearchControls();
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<SearchResult> results = dirContext.search("o=hertz", "(ObjectClass=top)", constraints);
			log.debug("got restults");
			while (results.hasMore()) {
				log.debug("in while");
				SearchResult searchResult = (SearchResult) results.next();
				Attributes attributes = searchResult.getAttributes();
				Attribute attr = attributes.get("hercProfilePostalCode");
				String cn = (String) attr.get();
				log.debug(" Person Common Name = " + cn);

			}

		}

		catch (NamingException e) {
			throw new RuntimeException(e);
		}
		/*
		 * catch (NamingException e) { throw new
		 * ServletException(e.getMessage()); }
		 */finally {
		}

		log.debug("Exiting into doPost method");
	}
}
