/**
 * 
 */
package com.herc.ewcm.core.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.mail.Header;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import com.herc.ewcm.core.common.HercServicesConstants;

/**
 * This service provides the implementation for sending email to Herc Buiness
 * Team members when User redeem the points through Mobile app/website.
 * 
 * @author VE332669
 *
 */

@Component(name = "com.herc.ewcm.core.services.EmailNotificationService", label = "Email Notification Service", description = "This Service send Email Notification", specVersion = "1.1", immediate = true, metatype = true)
@Service(value = { EmailNotificationService.class })
public class EmailNotificationService {

	private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

	/**
	 * This method return Default Charset
	 * 
	 * @param session
	 * @param emailTemplatepath
	 * @return
	 * @throws RepositoryException
	 */
	public String getCharSet(Session session, String emailTemplatePath) throws RepositoryException {
		String charSet = HercServicesConstants.DEFAULT_CHARSET;
		Node content = session.getNode(emailTemplatePath + "/" + JcrConstants.JCR_CONTENT);
		charSet = content.hasProperty(JcrConstants.JCR_ENCODING) ? content.getProperty(JcrConstants.JCR_ENCODING).getString() : HercServicesConstants.DEFAULT_CHARSET;
		log.debug("charSet : " + charSet);
		return charSet;
	}

	/**
	 * This method load the template path from AEM Repository. Notification
	 * template exist in CRX.
	 * 
	 * @param session
	 * @param emailTemplatePath
	 * @param charSet
	 * @return
	 * @throws IOException
	 * @throws PathNotFoundException
	 * @throws RepositoryException
	 */
	public String loadTemplate(Session session, String emailTemplatePath, String charSet) throws IOException, PathNotFoundException, RepositoryException {
		InputStream is = null;
		try {
			log.debug("Email template path : " + emailTemplatePath);
			if (StringUtils.isNotBlank(emailTemplatePath) && session.itemExists(emailTemplatePath)) {
				Node content = session.getNode(emailTemplatePath + "/" + JcrConstants.JCR_CONTENT);
				is = content.getProperty(JcrConstants.JCR_DATA).getBinary().getStream();
				InputStreamReader r = new InputStreamReader(is, charSet);
				StringWriter w = new StringWriter();
				IOUtils.copy(r, w);
				return w.toString();
			}

		} finally {
			IOUtils.closeQuietly(is);
		}
		return null;

	}

	public boolean sendEmailNotification(String emailTemplate, String charSet, MessageGatewayService messageGatewayService, String toList, String itemName, String itemQuantity,
			String receiverName, String receiverAddress, String receiverCompany, String receiverPhoneNumber, String receiverNotes, String subjectMessage) {
		log.debug("Entering sendEmailNotification method.");
		boolean isNotificationSent = false;
		try {
			if (null != emailTemplate) {
				Map<String, String> valuesMap = new HashMap<String, String>();
				valuesMap.put("support.id", toList);
				valuesMap.put("subject.message", subjectMessage);
				valuesMap.put("support.team.name", "Herc Sales Team");
				valuesMap.put("itemName", itemName);
				valuesMap.put("itemQuantity", itemQuantity);
				valuesMap.put("receiver.name", receiverName);
				valuesMap.put("receiver.company", receiverCompany);
				valuesMap.put("receiver.address", receiverAddress);
				valuesMap.put("receiver.phonenumber", receiverPhoneNumber);
				valuesMap.put("receiver.notes", receiverNotes);
				log.info("valuesMap : " + valuesMap);
				StrSubstitutor substitutor = new StrSubstitutor(valuesMap);
				Email email = createEmail(emailTemplate, charSet, substitutor);
				if (email.getToAddresses().size() > 0) {
					if (null != messageGatewayService) {
						MessageGateway<Email> messageGateway = messageGatewayService.getGateway(Email.class);
						DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
						Date date = new Date();
						log.info("Sending RedeemPoints Email on  : {} ", dateFormat.format(date));
						messageGateway.send((Email) email);
						isNotificationSent = true;
					}
				} else {
					log.warn("did not send email, no recipient addresses available.");
				}
			}
		} catch (Exception e) {
			log.error("Sending mail is failed due to exception", e);
		} finally {

		}
		log.debug("Exiting sendEmailNotification method");
		return isNotificationSent;

	}

	/**
	 * This method is to create Email Object
	 * 
	 * @param template
	 * @param charSet
	 * @param substitutor
	 * @return
	 * @throws Exception
	 */
	public Email createEmail(String template, String charSet, StrSubstitutor substitutor) throws Exception {
		/*
		 * First read/parse email headers Note that substitutions must be called
		 * after the mail headers are parsed, because they are expected to be
		 * US-ASCII only or specially encoded using MimeUtils class, but
		 * substitutions might introduce other chars, like e.g. Japanese
		 * characters. Further the CountingInputStream class does not seem to
		 * properly count when reading Japanese characters.
		 */
		log.info("In Create Email Method");
		CountingInputStream in = new CountingInputStream(new ByteArrayInputStream(template.getBytes(charSet)));
		InternetHeaders iHdrs = new InternetHeaders(in);
		Map<String, String[]> hdrs = new HashMap<String, String[]>();
		Enumeration<?> e = iHdrs.getAllHeaders();
		while (e.hasMoreElements()) {
			Header hdr = (Header) e.nextElement();
			String name = hdr.getName().toLowerCase();
			log.debug("Header: {} = {}", name, hdr.getValue());
			hdrs.put(name, iHdrs.getHeader(name));
		}

		// use the counting stream reader to read the mail body
		String templateBody = template.substring(in.getCount());
		// create email
		Email email = new SimpleEmail();
		email.setCharset(charSet);
		// spool headers
		StringBuilder rcpts = new StringBuilder();
		for (String to : saveRemoveHeader(hdrs, "to")) {
			to = substitutor.replace(to);
			InternetAddress[] afarray = InternetAddress.parse(to, true);
			for (InternetAddress af : afarray) {
				email.addTo(af.getAddress(), af.getPersonal());
			}
			rcpts.append(to).append(", ");
		}
		for (String cc : saveRemoveHeader(hdrs, "cc")) {
			cc = substitutor.replace(cc);
			InternetAddress af = new InternetAddress(cc);
			email.addCc(af.getAddress(), af.getPersonal());
			rcpts.append(cc).append(", ");
		}
		for (String bcc : saveRemoveHeader(hdrs, "bcc")) {
			bcc = substitutor.replace(bcc);
			InternetAddress af = new InternetAddress(bcc);
			email.addBcc(af.getAddress(), af.getPersonal());
			rcpts.append(bcc).append(", ");
		}
		for (String replyTo : saveRemoveHeader(hdrs, "Reply-To")) {
			replyTo = substitutor.replace(replyTo);
			InternetAddress af = new InternetAddress(replyTo);
			email.addReplyTo(af.getAddress(), af.getPersonal());
			rcpts.append(replyTo).append(", ");
		}
		for (String from : saveRemoveHeader(hdrs, "from")) {
			from = substitutor.replace(from);
			InternetAddress af = new InternetAddress(from);
			email.setFrom(af.getAddress(), af.getPersonal());
		}
		String[] subject = saveRemoveHeader(hdrs, "subject");
		if (subject.length > 0) {
			email.setSubject(substitutor.replace(subject[0]));
		}
		String[] bounceAddress = saveRemoveHeader(hdrs, "bounce-to");
		if (bounceAddress.length > 0) {
			email.setBounceAddress(substitutor.replace(bounceAddress[0]));
		}
		// add all remaining headers
		for (String name : hdrs.keySet()) {
			for (String value : hdrs.get(name)) {
				value = substitutor.replace(value);
				email.addHeader(name, value);
			}
		}
		// set message body
		templateBody = substitutor.replace(templateBody);
		log.debug("Substituted mail body: {}", templateBody);
		email.setContent(templateBody, Email.TEXT_HTML);
		log.info("Recipients {}", rcpts);
		return email;
	}

	/**
	 * method is to remove headers
	 * 
	 * @param hdrs
	 * @param name
	 * @return String[]
	 */
	private String[] saveRemoveHeader(Map<String, String[]> hdrs, String name) {
		String[] ret = hdrs.remove(name);
		return ret == null ? new String[0] : ret;
	}

	/**
	 * Default activate method called when bundle activated
	 * 
	 * @param context
	 * @throws Exception
	 */
	protected void activate(ComponentContext context) throws Exception {
		log.debug("activate method called.");
	}

	/**
	 * Default deactivate method called when bundle deactivated.
	 * 
	 * @param componentContext
	 * @throws Exception
	 */
	protected void deactivate(ComponentContext componentContext) throws Exception {
		log.debug("deactivate method called");
	}

}
