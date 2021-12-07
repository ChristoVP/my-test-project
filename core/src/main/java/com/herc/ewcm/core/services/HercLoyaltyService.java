package com.herc.ewcm.core.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.http.client.ClientProtocolException;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.commons.datasource.poolservice.DataSourceNotFoundException;
import com.day.cq.mailer.MessageGatewayService;
import com.herc.ewcm.core.common.HercCommonUtils;
import com.herc.ewcm.core.common.HercServicesConstants;

@Component(name = "com.herc.ewcm.core.services.HercLoyaltyService", label = "HercLoyaltyService", description = "Handles all api calls of HercLoyaltyService", specVersion = "1.1", immediate = true, metatype = true)
@Service(value = { HercLoyaltyService.class })
@Properties({
		@Property(name = "tolist", description = "This To list is used to send emaiil when user redeempoints.", label = "Email To List", value = "venkatramana.singaram@wipro.com,sridhar.nandipati@wipro.com"),
		@Property(name = "emailtemplatepath", value = "/etc/system/notification/email/email_notification_redeempoints.txt") })
public class HercLoyaltyService {
	@Reference
	private HercCommonUtils hercCommonUtils;
	@Reference
	private DataSourcePoolService dataSourcePoolService;
	@Reference
	private EmailNotificationService emailNotificationService;
	@Reference
	private MessageGatewayService messageGateWayService;
	private static String charSet = null;
	private static String emailTemplate = null;
	private String redeemPointsEmailTemplatePath;
	private String toList;
	private String contactEmail;
	private static final Logger log = LoggerFactory.getLogger(HercLoyaltyService.class);

	public void getSwagDetails(Node tablesNode, SlingHttpServletResponse response, Session adminSession) throws IOException {
		log.info("Entering getSwagDetails method");

		try {
			if (tablesNode.hasNode(HercServicesConstants.TABLE_NAME_SWAG_DETAILS)) {
				JSONObject jsonOb = new JSONObject();
				QueryManager queryManager = adminSession.getWorkspace().getQueryManager();
				String swagQuery = HercServicesConstants.QUERY_SWAG_DETAILS;
				log.info("swagQuery ->" + swagQuery);
				Query swagQueryexe = queryManager.createQuery(swagQuery, Query.XPATH);
				QueryResult swagQueryres = swagQueryexe.execute();
				NodeIterator nodes = swagQueryres.getNodes();
				log.info("no of nodes->" + nodes.getSize());
				JSONArray swag = new JSONArray();
				while (nodes.hasNext()) {
					log.info("in while");
					Node recordNode = (Node) nodes.next();
					JSONObject jsonObj = new JSONObject();
					String swagName = recordNode.getProperty("SWAG_NAME").getValue().getString();
					String swagDescription = recordNode.getProperty("SWAG_DESCRIPTION").getValue().getString();
					// jsonObj.put("SWAG_ID",
					// recordNode.getProperty("SWAG_ID").getValue().getString());
					jsonObj.put("SWAG_NAME", swagName);
					swagName = hercCommonUtils.getUnderScroreName(swagName);
					jsonObj.put("ITEM_IMAGE", HercServicesConstants.SWAG_ITEM_IMAGE_PATH_START + swagName + HercServicesConstants.DAM_PATH_IMAGES_ICON + swagName
							+ HercServicesConstants.SWAG_ITEM_IMAGE_PATH_END);
					jsonObj.put("SWAG_DESCRIPTION", swagDescription);
					jsonObj.put("SWAG_REDEEM_POINTS", recordNode.getProperty("SWAG_REDEEM_POINTS").getValue().getString());
					swag.put(jsonObj);
				}

				if (swag.length() == 0) {
					jsonOb.put(HercServicesConstants.STATUS_CODE, "500");
					jsonOb.put(HercServicesConstants.STATUS_MESSAGE, "Invalid user");
				} else {
					jsonOb.put("total records", swag.length());
					jsonOb.put("results", swag);
					jsonOb.put(HercServicesConstants.STATUS_CODE, "200");
					jsonOb.put(HercServicesConstants.STATUS_MESSAGE, "Success");
				}
				// response.getOutputStream().println(jsonObj.toString());
				hercCommonUtils.sendStatusResponse(response, jsonOb);
			}
		} catch (PathNotFoundException e) {
			log.error("PathNotFoundException Exception thrown", e);
		} catch (RepositoryException e) {
			log.error("RepositoryException  thrown ", e);
		} catch (IllegalStateException e) {
			log.error("RepositoryException Exception thrown", e);
		} catch (JSONException e) {
			log.error("JSONException Exception thrown", e);
		}
		log.info("Exiting getSolutions method");
	}

	public void addPoints(Node tablesNode, SlingHttpServletResponse response, Session adminSession, String RewardsTrigger, String UserName,String email) throws IOException {
		log.info("Entering addPoints method");

		long points = 0;
		log.info("Username  "+UserName);
		String user=UserName;
		UserName=hercCommonUtils.getAutoGenetratedNumber(UserName);
		log.info("auto generated number "+UserName);
		// getting level of user
		long pointsLevel = 0;
		try {

			String exp = "00/00/000";

			long redeemingPoints=0;

			// getting level of user
			
			if (tablesNode.hasNode(HercServicesConstants.TABLE_NAME_REWARDS_LOOKUP)) {

				String earnQuery = HercServicesConstants.QUERY_MY_LOYALTY_REWARDS + HercServicesConstants.QUERY_MY_LOYALTY_REWARDS_USERNAME + UserName
						+ HercServicesConstants.QUERY_ORDER_BY;
				log.info("earnQuery----" + earnQuery);
				QueryManager queryManager = adminSession.getWorkspace().getQueryManager();
				Query earnQueryexe = queryManager.createQuery(earnQuery, Query.XPATH);
				QueryResult earnQueryresult = earnQueryexe.execute();
				NodeIterator earnnodes = earnQueryresult.getNodes();
				log.info("total nodes = " + earnnodes.getSize());

				while (earnnodes.hasNext()) {
					log.info("in while of earn");
					Node earnnode = earnnodes.nextNode();

					if (earnnode.hasProperty(HercServicesConstants.MY_LOYALTY_REWARDS_COLUMN_POINTS)) {
						pointsLevel = pointsLevel + earnnode.getProperty(HercServicesConstants.REWARDS_LOOKUP_COLUMN_POINTS).getValue().getLong();
					}

				}
				log.info("PointsL: " + pointsLevel);
			}

			if (tablesNode.hasNode(HercServicesConstants.TABLE_NAME_REWARDS_LOOKUP)) {

				String earnQuery = HercServicesConstants.QUERY_REWARDS_LOOKUP + HercServicesConstants.QUERY_MY_LOYALTY_REWARDS_REWARDS_TRIGGER + RewardsTrigger
						+ HercServicesConstants.QUERY_ORDER_BY;
				log.info("earnQuery----" + earnQuery);
				QueryManager queryManager = adminSession.getWorkspace().getQueryManager();
				Query earnQueryexe = queryManager.createQuery(earnQuery, Query.XPATH);
				QueryResult earnQueryresult = earnQueryexe.execute();
				NodeIterator earnnodes = earnQueryresult.getNodes();
				log.info("total nodes = " + earnnodes.getSize());

				if (earnnodes.hasNext()) {
					log.info("in while of earn");
					Node earnnode = earnnodes.nextNode();

					if (earnnode.hasProperty(HercServicesConstants.REWARDS_LOOKUP_COLUMN_POINTS)) {
						points = earnnode.getProperty(HercServicesConstants.REWARDS_LOOKUP_COLUMN_POINTS).getValue().getLong();

						log.info("Points: " + points);

					}
				}

				if (pointsLevel < 5000) {

				} else if (pointsLevel < 10000) {
					points = (long) (points * 1.5);
				} else if (pointsLevel < 20000) {
					points = (long) (points * 2);
				} else {
					points = (long) (points * 2.5);
				}

				log.info("Points: " + points);
				redeemingPoints=points;
			}
			
		

			

			//To deduct points if trigger is for mark UNFAVOURITE
			if (RewardsTrigger.equalsIgnoreCase("Mark Branch as Unfavorite")) {
				
				JSONObject jobj = new JSONObject();
				if (tablesNode.hasNode(HercServicesConstants.TABLE_NAME_REWARDS_LOOKUP) && pointsLevel >= redeemingPoints) {
					String earnQuery = HercServicesConstants.QUERY_MY_LOYALTY_REWARDS + HercServicesConstants.QUERY_MY_LOYALTY_REWARDS_USERNAME + UserName
							+ HercServicesConstants.QUERY_ORDER_BY_EXP;
					log.info("earnQuery----" + earnQuery);
					QueryManager queryManager = adminSession.getWorkspace().getQueryManager();
					Query earnQueryexe = queryManager.createQuery(earnQuery, Query.XPATH);
					QueryResult earnQueryresult = earnQueryexe.execute();
					NodeIterator earnnodes = earnQueryresult.getNodes();
					log.info("total nodes = " + earnnodes.getSize());
					dataSourcePoolService.tomcatConnection("/MysqlAPIs/rest/SetConnection/deletePoints/"+UserName+"/"+redeemingPoints);
					log.info("deleted in mysql also"+"/MysqlAPIs/rest/SetConnection/deletePoints/"+UserName+"/"+redeemingPoints);					
					while (earnnodes.hasNext() && redeemingPoints > 0) {
						log.info("in while of earn");
						Node earnnode = earnnodes.nextNode();
						if (earnnode.hasProperty(HercServicesConstants.MY_LOYALTY_REWARDS_COLUMN_EXPIRATION_DATE)) {
							exp = earnnode.getProperty(HercServicesConstants.MY_LOYALTY_REWARDS_COLUMN_EXPIRATION_DATE).getValue().getString();
							log.info("exp: " + exp);

						}
						if (earnnode.hasProperty(HercServicesConstants.MY_LOYALTY_REWARDS_COLUMN_POINTS)) {
							points = earnnode.getProperty(HercServicesConstants.MY_LOYALTY_REWARDS_COLUMN_POINTS).getValue().getLong();
							log.info("Points: " + points);
							
							if (redeemingPoints >= points) {
								redeemingPoints = redeemingPoints - points;
								earnnode.remove();

							} else {
								long pointsFirst = points;
								points = points - redeemingPoints;
								redeemingPoints = 0;
								earnnode.setProperty(HercServicesConstants.MY_LOYALTY_REWARDS_COLUMN_POINTS, points);

							}
						}
					}

					jobj.put(HercServicesConstants.STATUS_CODE, "200");
					jobj.put(HercServicesConstants.STATUS_MESSAGE, "Successfully marked branch as Unfavorite");
					jobj.put(HercServicesConstants.MY_LOYALTY_REWARDS_COLUMN_POINTS, "-"+points+"");
					jobj.put("Balance", pointsLevel-points+"");

				} else {
					jobj.put(HercServicesConstants.STATUS_CODE, "500");
					jobj.put(HercServicesConstants.STATUS_MESSAGE, "No Enough Points for action Unfavourite");
					jobj.put("Balance", pointsLevel+"");
				}

				hercCommonUtils.sendStatusResponse(response, jobj);
				adminSession.save();
			} else {

				//To Add points if trigger is other than 'mark UNFAVOURITE'

				Node TableNameNode, TableNameNode1 = null;
				JSONObject jobj = new JSONObject();
				if (adminSession.itemExists(HercServicesConstants.MY_SQL_TABLES_PATH) && points != 0) {
					Node content1 = adminSession.getNode(HercServicesConstants.MY_SQL_TABLES_PATH);
					if (!content1.hasNode(HercServicesConstants.TABLE_NAME_JOBSITE)) {
						TableNameNode = content1.addNode(HercServicesConstants.TABLE_NAME_MY_LOYALTY_REWARDS);

					} else {
						TableNameNode = content1.getNode(HercServicesConstants.TABLE_NAME_MY_LOYALTY_REWARDS);

					}

					long nodesCount = TableNameNode.getNodes().getSize() + 1;
					TableNameNode1 = TableNameNode.addNode(nodesCount + "_" + System.currentTimeMillis(), "herc:Record");

					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					DateFormat dateFormatsql = new SimpleDateFormat("yyyy-MM-dd");

					int id = 0;
					Calendar cal = Calendar.getInstance();
					Date today = cal.getTime();
					cal.add(Calendar.YEAR, HercServicesConstants.MY_LOYALTY_REWARDS_EXPIRATION_PERIOD_TENNURE);
					Date exp1 = cal.getTime();
					
					
					dataSourcePoolService.tomcatConnection("/MysqlAPIs/rest/SetConnection/addPoints/"+user+"/"+points+"/"+dateFormatsql.format(today)+"/"+dateFormatsql.format(exp1)+"/"+UserName);
					log.info("/MysqlAPIs/rest/SetConnection/addPoints/"+user+"/"+points+"/"+dateFormatsql.format(today)+"/"+dateFormatsql.format(exp1)+UserName);

					TableNameNode1.setProperty(HercServicesConstants.MY_LOYALTY_REWARDS_COLUMN_MYREWARDS_ID, id);
					TableNameNode1.setProperty(HercServicesConstants.MY_LOYALTY_REWARDS_COLUMN_USERNAME, user);
					TableNameNode1.setProperty(HercServicesConstants.MY_LOYALTY_REWARDS_COLUMN_POINTS, points);
					TableNameNode1.setProperty(HercServicesConstants.MY_LOYALTY_REWARDS_COLUMN_START_DATE, dateFormat.format(today).toString());
					TableNameNode1.setProperty(HercServicesConstants.MY_LOYALTY_REWARDS_COLUMN_EXPIRATION_DATE, dateFormat.format(exp1).toString());
					TableNameNode1.setProperty("AUTO_GENERATED_NUMBER",UserName);
					adminSession.save();

					jobj.put(HercServicesConstants.STATUS_CODE, "200");
					
					jobj.put(HercServicesConstants.STATUS_MESSAGE, "Succesfully Earned Points");
					jobj.put(HercServicesConstants.MY_LOYALTY_REWARDS_COLUMN_USERNAME, user);
					jobj.put(HercServicesConstants.MY_LOYALTY_REWARDS_COLUMN_POINTS, points+"");
					jobj.put("Balance", pointsLevel+points+"");
					hercCommonUtils.sendStatusResponse(response, jobj);

				} else {
					jobj.put(HercServicesConstants.STATUS_CODE, "500");
					if (points == 0) {
						jobj.put(HercServicesConstants.STATUS_MESSAGE, "Invalid Trigger");

					} else {
						jobj.put(HercServicesConstants.STATUS_MESSAGE, "Unsuccessful");
					}
					hercCommonUtils.sendStatusResponse(response, jobj);
				}
			}

		} catch (RepositoryException e) {
			log.error("RepositoryException is caught", e);
		} catch (IllegalStateException e) {
			log.error("IllegalStateException is caught", e);
		} catch (JSONException e) {
			log.error("JSONException is caught", e);
		} 
	}

	public void redeemPoints(Session adminSession, Node tablesNode, SlingHttpServletResponse response, String userName, String redeemPoints, String itemName, String quantity,
			String receiverName, String receiverCompany, String receiverAddress, String receiverPhoneNumber, String receiverNotes, String email) throws IOException {
		log.info("Entering redeemPoints method");
		log.info("User name "+userName);
		String user=userName;
		userName=hercCommonUtils.getAutoGenetratedNumber(userName);
		log.info("Unique number "+userName);
		long points = 0;
		String exp = "00/00/000";
		long redeemingPoints = Integer.parseInt(redeemPoints);
		long toDeduct = Integer.parseInt(redeemPoints);

		try {

			// getting level of user
			long pointsLevel = 0;

			if (tablesNode.hasNode(HercServicesConstants.TABLE_NAME_REWARDS_LOOKUP)) {

				String earnQuery = HercServicesConstants.QUERY_MY_LOYALTY_REWARDS + HercServicesConstants.QUERY_MY_LOYALTY_REWARDS_USERNAME + userName
						+ HercServicesConstants.QUERY_ORDER_BY;
				log.info("earnQuery----" + earnQuery);
				QueryManager queryManager = adminSession.getWorkspace().getQueryManager();
				Query earnQueryexe = queryManager.createQuery(earnQuery, Query.XPATH);
				QueryResult earnQueryresult = earnQueryexe.execute();
				NodeIterator earnnodes = earnQueryresult.getNodes();
				log.info("total nodes = " + earnnodes.getSize());

				while (earnnodes.hasNext()) {
					log.info("in while of earn");
					Node earnnode = earnnodes.nextNode();

					if (earnnode.hasProperty(HercServicesConstants.MY_LOYALTY_REWARDS_COLUMN_POINTS)) {
						pointsLevel = pointsLevel + earnnode.getProperty(HercServicesConstants.REWARDS_LOOKUP_COLUMN_POINTS).getValue().getLong();
					}

				}
				log.info("PointsL: " + pointsLevel);
			}
			JSONObject jobj = new JSONObject();
			if (tablesNode.hasNode(HercServicesConstants.TABLE_NAME_REWARDS_LOOKUP) && pointsLevel >= redeemingPoints) {
				String earnQuery = HercServicesConstants.QUERY_MY_LOYALTY_REWARDS + HercServicesConstants.QUERY_MY_LOYALTY_REWARDS_USERNAME + userName
						+ HercServicesConstants.QUERY_ORDER_BY_EXP;
				log.info("earnQuery----" + earnQuery);
				QueryManager queryManager = adminSession.getWorkspace().getQueryManager();
				Query earnQueryexe = queryManager.createQuery(earnQuery, Query.XPATH);
				QueryResult earnQueryresult = earnQueryexe.execute();
				NodeIterator earnnodes = earnQueryresult.getNodes();
				log.info("total nodes = " + earnnodes.getSize());
				dataSourcePoolService.tomcatConnection("/MysqlAPIs/rest/SetConnection/deletePoints/"+userName+"/"+redeemingPoints);
				log.info("deleted in mysql also"+"/MysqlAPIs/rest/SetConnection/deletePoints/"+userName+"/"+redeemingPoints);
				
				while (earnnodes.hasNext() && redeemingPoints > 0) {
					log.info("in while of earn");
					Node earnnode = earnnodes.nextNode();
					if (earnnode.hasProperty(HercServicesConstants.MY_LOYALTY_REWARDS_COLUMN_EXPIRATION_DATE)) {
						exp = earnnode.getProperty(HercServicesConstants.MY_LOYALTY_REWARDS_COLUMN_EXPIRATION_DATE).getValue().getString();
						log.info("exp: " + exp);

					}
					if (earnnode.hasProperty(HercServicesConstants.MY_LOYALTY_REWARDS_COLUMN_POINTS)) {
						points = earnnode.getProperty(HercServicesConstants.MY_LOYALTY_REWARDS_COLUMN_POINTS).getValue().getLong();
						log.info("Points: " + points);
						if (redeemingPoints >= points) {
							redeemingPoints = redeemingPoints - points;
							
							/*
							 * if (mailSent(adminSession,
							 * emailNotificationService, messageGateWayService,
							 * redeemPointsEmailTemplatePath, itemName,
							 * quantity, receiverName, receiverCompany,
							 * receiverAddress, receiverPhoneNumber,
							 * receiverNotes)) {
							 * jobj.put(HercServicesConstants.STATUS_CODE,
							 * "200");
							 * jobj.put(HercServicesConstants.STATUS_MESSAGE,
							 * "Mail Sent, Redeem Successful, an Email has been sent to Business Team. Please contact "
							 * + contactEmail + " if you do not receive Item.");
							 */
							
							earnnode.remove();
							/*
							 * } else { // If Email send unsuccessful then
							 * return // failure // message.
							 * jobj.put(HercServicesConstants.STATUS_CODE,
							 * "500");
							 * jobj.put(HercServicesConstants.STATUS_MESSAGE,
							 * "Sending mail is failed due to exception"); }
							 */

						} else {
							long pointsFirst = points;
							points = points - redeemingPoints;
							redeemingPoints = 0;
							/*
							 * if (mailSent(adminSession,
							 * emailNotificationService, messageGateWayService,
							 * redeemPointsEmailTemplatePath, itemName,
							 * quantity, receiverName, receiverCompany,
							 * receiverAddress, receiverPhoneNumber,
							 * receiverNotes)) {
							 * jobj.put(HercServicesConstants.STATUS_CODE,
							 * "200");
							 * jobj.put(HercServicesConstants.STATUS_MESSAGE,
							 * "Mail Sent, Redeem Successful, an Email has been sent to Business Team. Please contact "
							 * + contactEmail + " if you do not receive Item.");
							 */
							
							earnnode.setProperty(HercServicesConstants.MY_LOYALTY_REWARDS_COLUMN_POINTS, points);
							/*
							 * } else { // If Email send unsuccessful then
							 * return // failure // message.
							 * jobj.put(HercServicesConstants.STATUS_CODE,
							 * "500");
							 * jobj.put(HercServicesConstants.STATUS_MESSAGE,
							 * "Sending mail is failed due to exception"); }
							 */
						}
					}
				}

				jobj.put(HercServicesConstants.STATUS_CODE, "200");
				jobj.put(HercServicesConstants.STATUS_MESSAGE, "Mail Sent");
				jobj.put("Balance",pointsLevel-toDeduct+"");
				//jobj.put("Balance Points", );
				

			} else {
				jobj.put(HercServicesConstants.STATUS_CODE, "500");
				jobj.put(HercServicesConstants.STATUS_MESSAGE, "No Enough Points");
				jobj.put("Balance",pointsLevel+"");
			}

			hercCommonUtils.sendStatusResponse(response, jobj);
			adminSession.save();

		} catch (RepositoryException e) {
			log.error("RepositoryException is caught", e);
		} catch (IllegalStateException e) {
			log.error("IllegalStateException is caught", e);
		} catch (JSONException e) {
			log.error("JSONException thrown", e);
		} 
	}

	public Boolean mailSent(Session adminSession, EmailNotificationService emailNotificationService, MessageGatewayService messageGateWayService,
			String redeemPointsEmailTemplatePath, String itemName, String quantity, String receiverName, String receiverCompany, String receiverAddress,
			String receiverPhoneNumber, String receiverNotes) {
		// * Send Email to Business Group to dispatch Item START
		// */
		// Get the Email Template.

		if (null != emailNotificationService && null != messageGateWayService && null != redeemPointsEmailTemplatePath) {
			if (null == charSet) {
				try {
					charSet = emailNotificationService.getCharSet(adminSession, redeemPointsEmailTemplatePath);
				} catch (RepositoryException e) {
					log.error("RepositoryException is caught", e);
				}
			}
			if (null == emailTemplate) {
				try {
					emailTemplate = emailNotificationService.loadTemplate(adminSession, redeemPointsEmailTemplatePath, charSet);
				} catch (PathNotFoundException e) {
					log.error("PathNotFoundException is caught", e);
				} catch (IOException e) {
					log.error("IOException is caught", e);
				} catch (RepositoryException e) {
					log.error("RepositoryException is caught", e);
				}
			}
		}

		boolean sendEmailNotification = emailNotificationService.sendEmailNotification(emailTemplate, charSet, messageGateWayService, toList, itemName, quantity, receiverName,
				receiverAddress, receiverCompany, receiverPhoneNumber, receiverNotes, HercServicesConstants.REDEEM_POINTS_NOTIFICATION);
		// Send Email to Business Group to dispatch Item END

		// Email send successful then return success response.

		return sendEmailNotification;
	}

	public void getEarnPoints(Node tablesNode, SlingHttpServletResponse response, Session adminSession, String UserName) throws IOException {
		log.info("Entering earnPoint method");
		UserName=hercCommonUtils.getAutoGenetratedNumber(UserName);
		long points = 0;
		try {
			if (tablesNode.hasNode(HercServicesConstants.TABLE_NAME_REWARDS_LOOKUP)) {

				String earnQuery = HercServicesConstants.QUERY_MY_LOYALTY_REWARDS + HercServicesConstants.QUERY_MY_LOYALTY_REWARDS_USERNAME + UserName
						+ HercServicesConstants.QUERY_ORDER_BY;
				log.info("earnQuery----" + earnQuery);
				QueryManager queryManager = adminSession.getWorkspace().getQueryManager();
				Query earnQueryexe = queryManager.createQuery(earnQuery, Query.XPATH);
				QueryResult earnQueryresult = earnQueryexe.execute();
				NodeIterator earnnodes = earnQueryresult.getNodes();
				log.info("total nodes = " + earnnodes.getSize());

				while (earnnodes.hasNext()) {
					log.info("in while of earn");
					Node earnnode = earnnodes.nextNode();

					if (earnnode.hasProperty(HercServicesConstants.MY_LOYALTY_REWARDS_COLUMN_POINTS)) {
						points = points + earnnode.getProperty(HercServicesConstants.REWARDS_LOOKUP_COLUMN_POINTS).getValue().getLong();
					}

				}

				JSONObject jobj = new JSONObject();
				jobj.put(HercServicesConstants.STATUS_CODE, "200");
				jobj.put(HercServicesConstants.STATUS_MESSAGE, "Recieved Earned Points");
				jobj.put("EARNED_POINTS", points);
				hercCommonUtils.sendStatusResponse(response, jobj);

			}
		} catch (RepositoryException e) {
			
			log.error("RepositoryException is caught", e);
		} catch (IllegalStateException e) {
			log.error("IllegalStateException is caught", e);
		} catch (JSONException e) {
			log.error("JSONException is caught", e);
		}

	}
	
	
	 public void getDistance(SlingHttpServletResponse response) throws ClientProtocolException, IOException, ServletException {
         log.info("******* called getDistance START ************");
 
        
           URL urldemo = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=17.433118399999998,78.32151069999999&radius=50000&name=hertz%20equipment%20rental&key=AIzaSyDdD-Di2m_o0gAkQ5RKUaydN-MGUgcL-U8");
          URLConnection yc = urldemo.openConnection();
          BufferedReader in=null;
          try{
           in = new BufferedReader(new InputStreamReader(
                  yc.getInputStream()));
        
          String inputLine;
          while ((inputLine = in.readLine()) != null)
           log.info("input line "+inputLine);
          in.close();
          }catch(Exception e){
                e.printStackTrace();
          }
         log.info("******* called getDistance END************");
  }

	public boolean sendRedeemEmailNotification(String toList) {
		log.info("Entering sendRedeemEmailNotification method.");
		boolean isEmailSent = false;

		log.info("Exiting sendRedeemEmailNotification method.");
		return isEmailSent;
	}

	/**
	 * Default activate method called when bundle activated
	 * 
	 * @param context
	 * @throws Exception
	 */
	protected void activate(ComponentContext context) throws Exception {
		log.debug("activate method called......");
		this.toList = (String) context.getProperties().get("tolist");
		this.redeemPointsEmailTemplatePath = (String) context.getProperties().get("redeempointsemailtemplate");
		log.debug("toList : " + toList);
		log.debug("redeemPointsEmailTemplatePath : " + redeemPointsEmailTemplatePath);
		String[] emailList = toList.split(",");
		contactEmail = emailList[0] + " or " + emailList[1];
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
