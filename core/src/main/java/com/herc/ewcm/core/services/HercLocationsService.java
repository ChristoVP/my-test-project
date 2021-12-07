/**
 * 
 */
package com.herc.ewcm.core.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.RowIterator;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.herc.ewcm.core.common.HercCommonUtils;
import com.herc.ewcm.core.common.HercServicesConstants;
import com.herc.ewcm.core.common.OpenTrustManager;

/**
 * @author MRAMIN Murali
 */
@Component(name = "com.herc.ewcm.core.services.HercLocationsService", label = "HercLocationsService", description = "Handles all api calls of Herc Locations", specVersion = "1.1", immediate = true, metatype = true)
@Service(value = { HercLocationsService.class })
@Properties({ @Property(name = "categories", description = "Categories list", label = "Please enter categories", value = { "PUMP", "ENERGY", "EQUIPMENT_SALES", "GENERAL_RENTAL",
		"AERIAL", "ENTERTAINMENT", "TRUCK", "EARTH", "MOVER", "LIFT", "HEAVY", "ROAD" }) })
public class HercLocationsService {

	private static final Logger log = LoggerFactory.getLogger(HercLocationsService.class);
	@Reference
	private HercCommonUtils hercCommonUtils;
	@Reference
	private DataSourcePoolService dataSourcePoolService;

	private String[] CATEGORIES_LIST;

	public void getBranchInfo(String zipcode, Node tablesNode, SlingHttpServletRequest request, SlingHttpServletResponse response, String deviceType, Session adminSession) {

		log.info("Entering getBranchInfo method");
		log.info("admin session : " + adminSession.getUserID());
		
		String zipQuery = null;

		try {
			if (tablesNode.hasNode(HercServicesConstants.TABLE_NAME_BRANCH_DETAILS)) {

				zipQuery = HercServicesConstants.QUERY_ZIPCODE + zipcode + HercServicesConstants.QUERY_ORDER_BY;
				log.info("final " + zipQuery);
				QueryManager queryManager = adminSession.getWorkspace().getQueryManager();

				// query for all nodes with tag "JCR"
				Query query = queryManager.createQuery(zipQuery, Query.XPATH);

				// iterate over results
				QueryResult result = query.execute();
				NodeIterator nodes = result.getNodes();
				RowIterator rowIterator = result.getRows();
				log.info("rows size==" + rowIterator.getSize());
				log.info("nodes size==" + nodes.getSize());

				long sizeTot = 0;

				JSONObject jsonObj = new JSONObject();
				JSONArray jarray = new JSONArray();
				if (nodes.hasNext()) {
					log.info("inside while");
					Node node = nodes.nextNode();
					sizeTot++;
					log.info("inside branch nodes=====");
					String v = node.getProperty(HercServicesConstants.BRANCH_COLUMN_BRANCH_TYPE).getValue().getString();
					String[] catValue = v.split(",");
					for (int i = 0; i < catValue.length; i++) {
						jarray.put(catValue[i].trim());
					}
					jsonObj.put("totalresults", sizeTot);
					String branchNumber = node.getProperty(HercServicesConstants.BRANCH_COLUMN_BRANCH_NUMBER).getValue().getString();
					jsonObj.put("BRANCH_CODE", branchNumber);			
					String BRANCH_IMG_URL = null;
					boolean isIos = false;
					if (deviceType.equalsIgnoreCase(HercServicesConstants.IOS_DEVICE)) {
						BRANCH_IMG_URL = HercServicesConstants.DAM_PATH_BRANCH_DETAILS + branchNumber + HercServicesConstants.DAM_PATH_IMAGES + branchNumber
								+ HercServicesConstants.JPG_EXTN + HercServicesConstants.JCRCONTENT_RENDITIONS + branchNumber + HercServicesConstants.IOS_PNG_308_308;
						isIos = true;
					}
					if (deviceType.equalsIgnoreCase(HercServicesConstants.ANDROID_DEVICE)) {
						BRANCH_IMG_URL = HercServicesConstants.DAM_PATH_BRANCH_DETAILS + branchNumber + HercServicesConstants.DAM_PATH_IMAGES + branchNumber
								+ HercServicesConstants.JPG_EXTN + HercServicesConstants.JCRCONTENT_RENDITIONS + branchNumber + HercServicesConstants.ANDROID_PNG_360_360;
					}
					if (!adminSession.itemExists(BRANCH_IMG_URL)) {
						if (isIos) {
							if(branchNumber.startsWith("9")){
								BRANCH_IMG_URL = HercServicesConstants.BRANCH_DEFAULT_THUMBNAIL_IOS_PNG_PATH_US;
							} else {
								BRANCH_IMG_URL = HercServicesConstants.BRANCH_DEFAULT_THUMBNAIL_IOS_PNG_PATH_CA;
							}						
						} else {
							if(branchNumber.startsWith("9")){
								BRANCH_IMG_URL = HercServicesConstants.BRANCH_DEFAULT_THUBNNAIL_ANDROID_PNG_PATH_US;
							} else {
								BRANCH_IMG_URL = HercServicesConstants.BRANCH_DEFAULT_THUBNNAIL_ANDROID_PNG_PATH_CA;
							}						
						}
					}
					jsonObj.put("BRANCH_NAME", node.getProperty(HercServicesConstants.BRANCH_COLUMN_BRANCH_NAME).getValue().getString());
					jsonObj.put("CATEGORIES", jarray);
					jsonObj.put("BRANCH_IMG_URL", BRANCH_IMG_URL);
					log.info("branch image url " + BRANCH_IMG_URL);
					jsonObj.put("BRANCH_DESCRIPTION", node.getProperty(HercServicesConstants.BRANCH_COLUMN_INFO_CONTENT).getValue().getString());
					jsonObj.put("CITY", node.getProperty(HercServicesConstants.BRANCH_COLUMN_CITY).getValue().getString());
					jsonObj.put("STATE", node.getProperty(HercServicesConstants.BRANCH_COLUMN_STATE_NAME).getValue().getString());
					jsonObj.put("ADRESS1", node.getProperty(HercServicesConstants.BRANCH_COLUMN_ADRESS1).getValue().getString());
					jsonObj.put("ADRESS2", node.getProperty(HercServicesConstants.BRANCH_COLUMN_ADRESS2).getValue().getString());
					jsonObj.put("BRANCH_PHONE", node.getProperty(HercServicesConstants.BRANCH_COLUMN_PHONE1).getValue().getString());
					jsonObj.put("Status", "200");
					jsonObj.put("Status_message", "success");

				} else {
					jsonObj.put("Status", "500");
					jsonObj.put("Status_message", "could not find any Branches");
				}

				log.info("jsonObj : " + jsonObj.toString());
				log.info("json response : " + jsonObj.toString());
				hercCommonUtils.sendStatusResponse(response, jsonObj);

			}

		} catch (PathNotFoundException e) {
			log.error("PathNotFoundException Exception thrown", e);
		} catch (RepositoryException e) {
			log.error("RepositoryException  thrown ", e);
		} catch (IllegalStateException e) {
			log.error("RepositoryException Exception thrown", e);
		} catch (JSONException e) {
			log.error("JSONException Exception thrown", e);
		} catch (IOException e) {
			log.error("IOException Exception thrown", e);
		}
		log.info("Exiting getBranchInfo method");
	}

	public void getNearByBranch(String zipcode, Node tablesNode, SlingHttpServletRequest request, SlingHttpServletResponse response, String deviceType, Session adminSession) {
		log.info("Entering getNearByBranch method");

		String zipQuery = null;
		JSONObject jobj = new JSONObject();

		JSONArray jarrayTop = new JSONArray();
		try {
			if (tablesNode.hasNode(HercServicesConstants.TABLE_NAME_BRANCH_DETAILS)) {
				String[] zipValues = zipcode.split(",");
				zipQuery = HercServicesConstants.QUERY_ZIPCODE;
				log.info("zipQuery=====" + zipQuery);
				for (int i = 0; i < zipValues.length; i++) {

					if (i < zipValues.length - 1) {
						zipQuery = zipQuery + zipValues[i] + HercServicesConstants.QUERY_ZIPCODES;

					}

					else {

						zipQuery = zipQuery + zipValues[i];

					}
				}
				zipQuery = zipQuery + HercServicesConstants.QUERY_ORDER_BY;

				log.info("zipQuery=====" + zipQuery);
				QueryManager queryManager = adminSession.getWorkspace().getQueryManager();

				// query for all nodes with tag "JCR"
				Query query = queryManager.createQuery(zipQuery, Query.XPATH);

				// iterate over results
				QueryResult result = query.execute();
				NodeIterator nodes = result.getNodes();

				long sizeTot = 0;

				while (nodes.hasNext()) {
					Node node = nodes.nextNode();

					sizeTot++;
					JSONObject jsonObj = new JSONObject();
					JSONArray jarray = new JSONArray();
					log.info("inside branch nodes=====");

					String v = node.getProperty(HercServicesConstants.BRANCH_COLUMN_BRANCH_TYPE).getValue().getString();
					String[] catValue = v.split(",");
					for (int i = 0; i < catValue.length; i++) {
						jarray.put(catValue[i].trim());
					}
					String branchNumber = node.getProperty(HercServicesConstants.BRANCH_COLUMN_BRANCH_NUMBER).getValue().getString();
					jsonObj.put("BRANCH_CODE", branchNumber);
					jsonObj.put("BRANCH_NAME", node.getProperty(HercServicesConstants.BRANCH_COLUMN_BRANCH_NAME).getValue().getString());
					jsonObj.put("CATEGORIES", jarray);
					//
					String BRANCH_IMG_URL = null;
					boolean isIos = false;
					if (deviceType.equalsIgnoreCase(HercServicesConstants.IOS_DEVICE)) {
						BRANCH_IMG_URL = HercServicesConstants.DAM_PATH_BRANCH_DETAILS + branchNumber + HercServicesConstants.DAM_PATH_IMAGES + branchNumber + HercServicesConstants.JPG_EXTN +
								HercServicesConstants.JCRCONTENT_RENDITIONS  + branchNumber + HercServicesConstants.IOS_PNG_308_308;
						isIos = true;
					}
					if (deviceType.equalsIgnoreCase(HercServicesConstants.ANDROID_DEVICE)) {
						BRANCH_IMG_URL = HercServicesConstants.DAM_PATH_BRANCH_DETAILS + branchNumber + HercServicesConstants.DAM_PATH_IMAGES + branchNumber + HercServicesConstants.JPG_EXTN +
								HercServicesConstants.JCRCONTENT_RENDITIONS + branchNumber + HercServicesConstants.ANDROID_PNG_360_360;
					}
					log.info("branch image url " + BRANCH_IMG_URL);
					if (!adminSession.itemExists(BRANCH_IMG_URL)) {
						if(isIos){
							if(branchNumber.startsWith("9")){
								BRANCH_IMG_URL = HercServicesConstants.BRANCH_DEFAULT_THUMBNAIL_IOS_PNG_PATH_US;
							} else {
								BRANCH_IMG_URL = HercServicesConstants.BRANCH_DEFAULT_THUMBNAIL_IOS_PNG_PATH_CA;
							}
						}else {
							if(branchNumber.startsWith("9")){
								BRANCH_IMG_URL = HercServicesConstants.BRANCH_DEFAULT_THUBNNAIL_ANDROID_PNG_PATH_US;
							} else {
								BRANCH_IMG_URL = HercServicesConstants.BRANCH_DEFAULT_THUBNNAIL_ANDROID_PNG_PATH_CA;
							}
						}
						
					}
					//
					jsonObj.put("BRANCH_IMG_URL", BRANCH_IMG_URL);
					jsonObj.put("BRANCH_DESCRIPTION", node.getProperty(HercServicesConstants.BRANCH_COLUMN_INFO_CONTENT).getValue().getString());
					jsonObj.put("CITY", node.getProperty(HercServicesConstants.BRANCH_COLUMN_CITY).getValue().getString());
					jsonObj.put("STATE", node.getProperty(HercServicesConstants.BRANCH_COLUMN_STATE_NAME).getValue().getString());
					jsonObj.put("ADRESS1", node.getProperty(HercServicesConstants.BRANCH_COLUMN_ADRESS1).getValue().getString());
					jsonObj.put("ADRESS2", node.getProperty(HercServicesConstants.BRANCH_COLUMN_ADRESS2).getValue().getString());
					jsonObj.put("ZIP_CODE", node.getProperty(HercServicesConstants.BRANCH_COLUMN_ZIP).getValue().getString());
					jarrayTop.put(jsonObj);
				}

				jobj.put("totalresults", sizeTot);
				jobj.put("records", jarrayTop);
				if (jarrayTop.length() == 0) {
					jobj.put("Status", "500");
					jobj.put("Status_message", "could not find any Branches");
				} else {
					jobj.put("Status", "200");
					jobj.put("Status_message", "success");
				}
				log.info("jsonObj : " + jobj.toString());
				log.info("json response : " + jobj.toString());
				hercCommonUtils.sendStatusResponse(response, jobj);

			}
		} catch (PathNotFoundException e) {
			log.error("PathNotFoundException Exception thrown", e);
		} catch (RepositoryException e) {
			log.error("RepositoryException  thrown ", e);
		} catch (IllegalStateException e) {
			log.error("RepositoryException Exception thrown", e);
		} catch (JSONException e) {
			log.error("JSONException Exception thrown", e);
		} catch (IOException e) {
			log.error("IOException Exception thrown", e);
		}
		log.info("Exiting getNearByBranch method");
	}

	public void getStateWiseBranchList(Node tablesNode, SlingHttpServletRequest request, SlingHttpServletResponse response,String deviceType, Session adminSession,String country) {
		log.info("Entering getStateWiseBranchList method");
		// String branchs_qry=HercServicesConstants.QUERY_STATEWISE_BRANCH_LIST;
		Node locTab, brnchTab;
		String brnchQuery = null;
		if(null!=country)
		{
			if(country.equals("CR"))
			{
				country="Canada";
				brnchQuery = "/jcr:root/etc/system/mysqltables/branch_details//element(*,herc:Record)[@COUNTRY = '"+country+"'] order by @jcr:score";
			}
			else
			{
				country="United States";
				brnchQuery = "/jcr:root/etc/system/mysqltables/branch_details//element(*,herc:Record)[@COUNTRY = '"+country+"'] order by @jcr:score";
			}
		}
		else
		{
				country="United States";
				brnchQuery = "/jcr:root/etc/system/mysqltables/branch_details//element(*,herc:Record)[@COUNTRY = '"+country+"'] order by @jcr:score";
			
		}
		try {
			if (tablesNode.hasNode(HercServicesConstants.TABLE_NAME_BRANCH_DETAILS)) {
				//brnchQuery = "/jcr:root/etc/system/mysqltables/branch_details//element(*,herc:Record) order by @jcr:score";
				log.info("brnchQuery=====" + brnchQuery);
				QueryManager queryManager = adminSession.getWorkspace().getQueryManager();
				// query for all nodes with tag "JCR"
				Query query = queryManager.createQuery(brnchQuery, Query.XPATH);
				// iterate over results
				QueryResult result = query.execute();
				NodeIterator nodes = result.getNodes();
				long size = nodes.getSize();
				log.info(" Total Numbder of Branches : " + size);
				JSONObject jobj = new JSONObject();				
				JSONArray jarray = new JSONArray();
				/**
				 * while start
				 */
				while (nodes.hasNext()) {
					Node branchRecNode = (Node) nodes.next();
					if (branchRecNode.hasProperty("STATE_NAME")) {
						String STATE_NAME = branchRecNode.getProperty("STATE_NAME").getValue().getString();
						/**
						 * Get Categories
						 */
						JSONArray categories = new JSONArray();
						String v = branchRecNode.getProperty(HercServicesConstants.BRANCH_COLUMN_BRANCH_TYPE).getValue().getString();
						String[] catValue = v.split(",");
						for (int i = 0; i < catValue.length; i++) {
							categories.put(catValue[i].trim());
						}
						JSONObject jStateWiseObj = null;
						if (null != jarray && jarray.length() > 0) {
							boolean isStateExist = false;
							for (int i = 0; i < jarray.length(); i++) {
								jStateWiseObj = jarray.getJSONObject(i);
								if (jStateWiseObj.has("STATE")) {
									String stateName = (String) jStateWiseObj.get("STATE");
									if (stateName.equalsIgnoreCase(STATE_NAME)) {
										isStateExist = true;
										break;
									} else {
										continue;
									}
								}
							}
						//	log.info("-----------isStateExist----------" + isStateExist);
							if (isStateExist) {
								jarray = getStateWiseJsonArray(branchRecNode, jarray, jStateWiseObj, categories, isStateExist,deviceType, adminSession);

							} else {
								jStateWiseObj = new JSONObject();
								jarray = getStateWiseJsonArray(branchRecNode, jarray, jStateWiseObj, categories, isStateExist,deviceType, adminSession);
							}
						} else {
							jStateWiseObj = new JSONObject();
							jarray = getStateWiseJsonArray(branchRecNode, jarray, jStateWiseObj, categories, false,deviceType, adminSession);
						}

					}
				}
				/**
				 * while end
				 */
				jobj.put("totalresults", jarray.length());
				jobj.put("statewiselist", jarray);
				jobj.put("Status", "200");
				jobj.put("Status_message", "success");
			//	log.info("Final JSONObject String  : " + jobj.toString());
				hercCommonUtils.sendStatusResponse(response, jobj);
			}

		} catch (PathNotFoundException e) {
			log.error("PathNotFoundException Exception thrown", e);
		} catch (RepositoryException e) {
			log.error("RepositoryException  thrown ", e);
		} catch (IllegalStateException e) {
			log.error("RepositoryException Exception thrown", e);
		} catch (JSONException e) {
			log.error("JSONException Exception thrown", e);
		} catch (IOException e) {
			log.error("IOException Exception thrown", e);
		}
		log.info("Exiting getStateWiseBranchList method");
	}

	public void getFavouriteBranch(Node tablesNode, SlingHttpServletResponse response, String USERNAME, String categories,String deviceType, Session adminSession) {
		log.info("Entering getFavouriteBranch method");
		String favQuery = null, prefQuery = null;
		JSONObject jobj = new JSONObject();
		JSONArray jarrayTop = new JSONArray();
		int nodeSize=0;
		USERNAME=hercCommonUtils.getAutoGenetratedNumber(USERNAME);
		log.info("Auto GEnerated number )"+USERNAME);
		try {
			if (tablesNode.hasNode(HercServicesConstants.TABLE_NAME_MY_PREFERENCE)) {
				favQuery = HercServicesConstants.TABLE_MY_PREFERENCE_FAV_QRY + USERNAME + HercServicesConstants.QUERY_ORDER_BY;
				log.info("favQuery=====" + favQuery);
				QueryManager queryManager = adminSession.getWorkspace().getQueryManager();

				// query for all nodes with tag "JCR"
				Query query = queryManager.createQuery(favQuery, Query.XPATH);

				// iterate over results
				QueryResult result = query.execute();
				NodeIterator nodes = result.getNodes();
				log.info("Total records"+result.getRows());
				NodeIterator temp=result.getNodes();;
				while(temp.hasNext())//if the total count is greater than 100 then nodes.getSize() will return -1
				{
					temp.nextNode();
					nodeSize++;
				}
				log.info("nodes size==" + nodeSize);
				String[] catvalues = categories.split(",");
				String branchListQuery = HercServicesConstants.QUERY_BRANCH_LIST;
				int count = 0;
				while (nodes.hasNext()) {
					log.info("inside while" + count);
					
					Node node = nodes.nextNode();
					String branchNumber = node.getProperty(HercServicesConstants.BRANCH_COLUMN_BRANCH_NUMBER).getValue().getString();
					log.info("branchList : " + branchNumber);
					// for (int i = 0; i < nodes.getSize(); i++) {
					if (count < nodeSize - 1) {
						branchListQuery = branchListQuery + HercServicesConstants.BRANCH_NUMBER_PROPERTY_EQUALTO + branchNumber + "' or ";
					} else {
						branchListQuery = branchListQuery + HercServicesConstants.BRANCH_NUMBER_PROPERTY_EQUALTO + branchNumber + "'";
					}
					// }
					count++;
				}
				branchListQuery = branchListQuery + HercServicesConstants.ORDER_BY_JCR_SCORE;
				log.info("BRANCH_LIST FOR GIVEN USER Query :" + branchListQuery);
				if (null != branchListQuery) {
					Query brnachesQuery = queryManager.createQuery(branchListQuery, Query.XPATH);
					// iterate over results
					QueryResult branchList = brnachesQuery.execute();
					NodeIterator branchNodes = branchList.getNodes();
					while (branchNodes.hasNext()) {
						log.info("In while branchNodes ");
						Node branchNode = branchNodes.nextNode();
						/**
						 * Check if the category exist Branch Types.
						 */
						String branchTypes = branchNode.getProperty(HercServicesConstants.BRANCH_COLUMN_BRANCH_TYPE).getValue().getString();
						JSONArray catListArray = new JSONArray();
						boolean isBrTypeExistInCatList = false;
						log.info("******** before for ********* " + isBrTypeExistInCatList);
						log.info("catvalues length : " + catvalues.length);
						for (int i = 0; i < catvalues.length; i++) {
							if (branchTypes.contains(catvalues[i])) {
								log.info("category contains in branchtypes list");
								catListArray.put(catvalues[i].trim());
								isBrTypeExistInCatList = true;
							} else {
								log.info("category does not contains in branchtypes list");
							}
						}
						log.info("******** after for ********* " + isBrTypeExistInCatList);
						if (isBrTypeExistInCatList) {
							JSONObject jsonObj = new JSONObject();
							String branchNumber = branchNode.getProperty(HercServicesConstants.BRANCH_COLUMN_BRANCH_NUMBER).getValue().getString();
							jsonObj.put("BRANCH_CODE", branchNumber);
							//
							//
							String BRANCH_IMG_URL = null;
							boolean isIos = false;
							if (deviceType.equalsIgnoreCase(HercServicesConstants.IOS_DEVICE)) {
								BRANCH_IMG_URL = HercServicesConstants.DAM_PATH_BRANCH_DETAILS + branchNumber + HercServicesConstants.DAM_PATH_IMAGES + branchNumber
										+ HercServicesConstants.JPG_EXTN + HercServicesConstants.JCRCONTENT_RENDITIONS + branchNumber + HercServicesConstants.IOS_PNG_308_308;
								isIos = true;
							}
							if (deviceType.equalsIgnoreCase(HercServicesConstants.ANDROID_DEVICE)) {
								BRANCH_IMG_URL = HercServicesConstants.DAM_PATH_BRANCH_DETAILS + branchNumber + HercServicesConstants.DAM_PATH_IMAGES + branchNumber
										+ HercServicesConstants.JPG_EXTN + HercServicesConstants.JCRCONTENT_RENDITIONS + branchNumber + HercServicesConstants.ANDROID_PNG_360_360;
							}
							if (!adminSession.itemExists(BRANCH_IMG_URL)) {
								if (isIos) {
									if(branchNumber.startsWith("9")){
										BRANCH_IMG_URL = HercServicesConstants.BRANCH_DEFAULT_THUMBNAIL_IOS_PNG_PATH_US;
									} else {
										BRANCH_IMG_URL = HercServicesConstants.BRANCH_DEFAULT_THUMBNAIL_IOS_PNG_PATH_CA;
									}
								} else {
									if(branchNumber.startsWith("9")){
										BRANCH_IMG_URL = HercServicesConstants.BRANCH_DEFAULT_THUBNNAIL_ANDROID_PNG_PATH_US;
									} else {
										BRANCH_IMG_URL = HercServicesConstants.BRANCH_DEFAULT_THUBNNAIL_ANDROID_PNG_PATH_CA;
									}
								}

							}
							//
							/*String BRANCH_IMG_URL = HercServicesConstants.DAM_PATH_BRANCH_DETAILS + branchNumber + HercServicesConstants.DAM_PATH_IMAGES_ICON + branchNumber
									+ HercServicesConstants.DAM_PATH_ICON_PNG_FILE_EXTENSION;
							log.info("branch image url " + BRANCH_IMG_URL);
							if (!adminSession.itemExists(BRANCH_IMG_URL)) {
								BRANCH_IMG_URL = HercServicesConstants.DAM_PATH_BRANCH_DETAILS_DEFAULT_ICON_IMAGE;
							}*/
							//
							jsonObj.put("BRANCH_IMG_URL", BRANCH_IMG_URL);
							jsonObj.put("BRANCH_NAME", branchNode.getProperty(HercServicesConstants.BRANCH_COLUMN_BRANCH_NAME).getValue().getString());
							jsonObj.put("BRANCH_DESCRIPTION", branchNode.getProperty(HercServicesConstants.BRANCH_COLUMN_INFO_CONTENT).getValue().getString());
							jsonObj.put("CATEGORIES", catListArray);
							JSONArray jOpenHoursTimeArr = new JSONArray();
							JSONArray jCloseHoursTimeArr = new JSONArray();
							jOpenHoursTimeArr.put(branchNode.getProperty("BRANCH_MON_OPEN_HRS").getValue().getString());
							jOpenHoursTimeArr.put(branchNode.getProperty("BRANCH_TUE_OPEN_HRS").getValue().getString());
							jOpenHoursTimeArr.put(branchNode.getProperty("BRANCH_WED_OPEN_HRS").getValue().getString());
							jOpenHoursTimeArr.put(branchNode.getProperty("BRANCH_THU_OPEN_HRS").getValue().getString());
							jOpenHoursTimeArr.put(branchNode.getProperty("BRANCH_FRI_OPEN_HRS").getValue().getString());
							jOpenHoursTimeArr.put(branchNode.getProperty("BRANCH_SAT_OPEN_HRS").getValue().getString());
							jOpenHoursTimeArr.put(branchNode.getProperty("BRANCH_SUN_OPEN_HRS").getValue().getString());
							jCloseHoursTimeArr.put(branchNode.getProperty("BRANCH_MON_CLOSE_HRS").getValue().getString());
							jCloseHoursTimeArr.put(branchNode.getProperty("BRANCH_TUE_CLOSE_HRS").getValue().getString());
							jCloseHoursTimeArr.put(branchNode.getProperty("BRANCH_WED_CLOSE_HRS").getValue().getString());
							jCloseHoursTimeArr.put(branchNode.getProperty("BRANCH_THU_CLOSE_HRS").getValue().getString());
							jCloseHoursTimeArr.put(branchNode.getProperty("BRANCH_FRI_CLOSE_HRS").getValue().getString());
							jCloseHoursTimeArr.put(branchNode.getProperty("BRANCH_SAT_CLOSE_HRS").getValue().getString());
							jCloseHoursTimeArr.put(branchNode.getProperty("BRANCH_SUN_CLOSE_HRS").getValue().getString());
							jsonObj.put("OPEN_HOURS", jOpenHoursTimeArr);
							jsonObj.put("CLOSE_HOURS", jCloseHoursTimeArr);
							jsonObj.put("CITY", branchNode.getProperty(HercServicesConstants.BRANCH_COLUMN_CITY).getValue().getString());
							jsonObj.put("STATE", branchNode.getProperty(HercServicesConstants.BRANCH_COLUMN_STATE_NAME).getValue().getString());
							jsonObj.put("ADRESS1", branchNode.getProperty(HercServicesConstants.BRANCH_COLUMN_ADRESS1).getValue().getString());
							jsonObj.put("ADRESS2", branchNode.getProperty(HercServicesConstants.BRANCH_COLUMN_ADRESS2).getValue().getString());
							jsonObj.put("ZIPCODE", branchNode.getProperty(HercServicesConstants.BRANCH_COLUMN_ZIP).getValue().getString());
							jarrayTop.put(jsonObj);
						}
					}
					jobj.put("totalresults", jarrayTop.length());
					jobj.put("records", jarrayTop);
				}
				if (jarrayTop.length() == 0) {
					jobj.put("Status", "500");
					jobj.put("Status_message", "could not find any locations");
				} else {
					jobj.put("Status", "200");
					jobj.put("Status_message", "success");
				}
				log.info("jsonObj : " + jobj.toString());
				log.info("json response : " + jobj.toString());
				hercCommonUtils.sendStatusResponse(response, jobj);

			}

		} catch (PathNotFoundException e) {
			log.error("PathNotFoundException Exception thrown", e);
		} catch (RepositoryException e) {
			log.error("RepositoryException  thrown ", e);
		} catch (IllegalStateException e) {
			log.error("RepositoryException Exception thrown", e);
		} catch (JSONException e) {
			log.error("JSONException Exception thrown", e);
		} catch (IOException e) {
			log.error("IOException Exception thrown", e);
		}
		log.info("Exiting getBranchDetailByZipCode method");
	}

	public void getBranchDetails(Node tableNode, SlingHttpServletResponse response, String Branch_Number, String deviceType,Session adminSession, String UserName) {
		log.info("Entering getBranchDetails method");

		Node tableNode1 = null;

		String detailsQuery = null;
		try {
			if (tableNode.hasNode(HercServicesConstants.TABLE_NAME_BRANCH_DETAILS)) {
				tableNode1 = tableNode.getNode(HercServicesConstants.TABLE_NAME_BRANCH_DETAILS);
				log.info("node path : " + tableNode1.getPath());
				NodeIterator nodes = tableNode1.getNodes();
				JSONObject jsonObj = new JSONObject();
				QueryManager queryManager = adminSession.getWorkspace().getQueryManager();
				if(Character.isLetter(Branch_Number.charAt(1)))
				{
					detailsQuery = HercServicesConstants.QUERY_BRANCH_DETAILS_CA_BRANCH_NUMBER + Branch_Number + HercServicesConstants.QUERY_ORDER_BY;
				}
				else
				{
					detailsQuery = HercServicesConstants.QUERY_BRANCH_DETAILS_BRANCH_NUMBER + Branch_Number + HercServicesConstants.QUERY_ORDER_BY;
				}
				
				log.info("detailsQuery=====" + detailsQuery);
				Query brnquery = queryManager.createQuery(detailsQuery, Query.XPATH);
				// iterate over results
				QueryResult brnchresult = brnquery.execute();
				NodeIterator brnchnodes = brnchresult.getNodes();
				log.info("total nodes = " + brnchnodes.getSize());
				while (brnchnodes.hasNext()) {
					Node branchRecNode = brnchnodes.nextNode();
					log.info("node name" + branchRecNode.getName());
					// JSONArray jarrayTimings = new JSONArray();
					JSONArray jOpenHoursTimeArr = new JSONArray();
					JSONArray jCloseHoursTimeArr = new JSONArray();
					JSONArray jarrayLocationInfo = new JSONArray();
					JSONArray jarrayBranchPhone = new JSONArray();
					JSONArray jarray = new JSONArray();
					JSONArray jarr_phone = new JSONArray();
					String v = branchRecNode.getProperty(HercServicesConstants.BRANCH_COLUMN_BRANCH_TYPE).getValue().getString();
					String[] catValue = v.split(",");
					for (int i = 0; i < catValue.length; i++) {
						jarray.put(catValue[i]);
					}
					String v1 = branchRecNode.getProperty(HercServicesConstants.BRANCH_COLUMN_BRANCH_PHONE).getValue().getString();
					String[] catValue1 = v1.split(",");
					for (int i = 0; i < catValue1.length; i++) {
						jarr_phone.put(catValue1[i].trim());
					}
					jOpenHoursTimeArr.put(branchRecNode.getProperty("BRANCH_MON_OPEN_HRS").getValue().getString());
					jOpenHoursTimeArr.put(branchRecNode.getProperty("BRANCH_TUE_OPEN_HRS").getValue().getString());
					jOpenHoursTimeArr.put(branchRecNode.getProperty("BRANCH_WED_OPEN_HRS").getValue().getString());
					jOpenHoursTimeArr.put(branchRecNode.getProperty("BRANCH_THU_OPEN_HRS").getValue().getString());
					jOpenHoursTimeArr.put(branchRecNode.getProperty("BRANCH_FRI_OPEN_HRS").getValue().getString());
					jOpenHoursTimeArr.put(branchRecNode.getProperty("BRANCH_SAT_OPEN_HRS").getValue().getString());
					jOpenHoursTimeArr.put(branchRecNode.getProperty("BRANCH_SUN_OPEN_HRS").getValue().getString());
					jCloseHoursTimeArr.put(branchRecNode.getProperty("BRANCH_MON_CLOSE_HRS").getValue().getString());
					jCloseHoursTimeArr.put(branchRecNode.getProperty("BRANCH_TUE_CLOSE_HRS").getValue().getString());
					jCloseHoursTimeArr.put(branchRecNode.getProperty("BRANCH_WED_CLOSE_HRS").getValue().getString());
					jCloseHoursTimeArr.put(branchRecNode.getProperty("BRANCH_THU_CLOSE_HRS").getValue().getString());
					jCloseHoursTimeArr.put(branchRecNode.getProperty("BRANCH_FRI_CLOSE_HRS").getValue().getString());
					jCloseHoursTimeArr.put(branchRecNode.getProperty("BRANCH_SAT_CLOSE_HRS").getValue().getString());
					jCloseHoursTimeArr.put(branchRecNode.getProperty("BRANCH_SUN_CLOSE_HRS").getValue().getString());
					jarrayLocationInfo.put(branchRecNode.getProperty(HercServicesConstants.BRANCH_COLUMN_ADRESS1).getValue().getString());
					jarrayLocationInfo.put(branchRecNode.getProperty(HercServicesConstants.BRANCH_COLUMN_ADRESS2).getValue().getString());
					jarrayLocationInfo.put(branchRecNode.getProperty(HercServicesConstants.BRANCH_COLUMN_CITY).getValue().getString());
					jarrayLocationInfo.put(branchRecNode.getProperty(HercServicesConstants.BRANCH_COLUMN_STATE_NAME).getValue().getString());
					jarrayBranchPhone.put(branchRecNode.getProperty(HercServicesConstants.BRANCH_COLUMN_PHONE1).getValue().getString());
					jarrayBranchPhone.put(branchRecNode.getProperty(HercServicesConstants.BRANCH_COLUMN_PHONE2).getValue().getString());

					jsonObj.put("BRANCH_ZIPCODE", branchRecNode.getProperty(HercServicesConstants.BRANCH_COLUMN_ZIP).getValue().getString());
					String branchNumber = branchRecNode.getProperty(HercServicesConstants.BRANCH_COLUMN_BRANCH_NUMBER).getValue().getString();
                    //
					String BRANCH_IMG_URL = null;
					boolean isIos = false;
					if (deviceType.equalsIgnoreCase(HercServicesConstants.IOS_DEVICE)) {
						BRANCH_IMG_URL = HercServicesConstants.DAM_PATH_BRANCH_DETAILS + branchNumber + HercServicesConstants.DAM_PATH_BANNER + branchNumber
								+ HercServicesConstants.JPG_EXTN + HercServicesConstants.JCRCONTENT_RENDITIONS + branchNumber + HercServicesConstants.IOS_PNG_1242_497;
						isIos = true;
					}
					if (deviceType.equalsIgnoreCase(HercServicesConstants.ANDROID_DEVICE)) {
						BRANCH_IMG_URL = HercServicesConstants.DAM_PATH_BRANCH_DETAILS + branchNumber + HercServicesConstants.DAM_PATH_BANNER + branchNumber
								+ HercServicesConstants.JPG_EXTN + HercServicesConstants.JCRCONTENT_RENDITIONS + branchNumber + HercServicesConstants.ANDROID_PNG_1440_576;
					}
					if (!adminSession.itemExists(BRANCH_IMG_URL)) {
						if (isIos) {

							if(branchNumber.startsWith("9")){
								BRANCH_IMG_URL = HercServicesConstants.BRANCH_DEFAULT_HEADER_IOS_PNG_PATH_US;
							} else {
								BRANCH_IMG_URL = HercServicesConstants.BRANCH_DEFAULT_THUMBNAIL_IOS_PNG_PATH_CA;
							}
							
						} else {
							if(branchNumber.startsWith("9")){
								BRANCH_IMG_URL = HercServicesConstants.BRANCH_DEFAULT_THUBNNAIL_ANDROID_PNG_PATH_US;
							} else {
									BRANCH_IMG_URL = HercServicesConstants.BRANCH_DEFAULT_HEADER_ANDROID_PNG_PATH_CA;
							}
						}

					}
					//
				/*	String BRANCH_IMG_URL = HercServicesConstants.DAM_PATH_BRANCH_DETAILS + branchNumber + HercServicesConstants.DAM_PATH_IMAGES + branchNumber
							+ HercServicesConstants.DAM_PATH_IMAGE_PNG_FILE_EXTENSION;*/

					log.info("branch image url " + BRANCH_IMG_URL);
					jsonObj.put("BRANCH_IMAGE", BRANCH_IMG_URL);
					jsonObj.put("BRANCH_NAME", branchRecNode.getProperty(HercServicesConstants.BRANCH_COLUMN_BRANCH_NAME).getValue().getString());
					// jsonObj.put("BRANCH_CODE",
					// branchRecNode.getProperty("BRANCH_ID").getValue().getString());
					jsonObj.put("PHONE_NUMBERS", jarrayBranchPhone);

					jsonObj.put("BRANCH_FAX", branchRecNode.getProperty(HercServicesConstants.BRANCH_COLUMN_FAX).getValue().getString());
					jsonObj.put("BRANCH_DESCRIPTION", branchRecNode.getProperty(HercServicesConstants.BRANCH_COLUMN_INFO_CONTENT).getValue().getString());

					jsonObj.put("BRANCH_TYPE", jarray);
					jsonObj.put("BRANCH_TYPE_PHONE", jarr_phone);
					jsonObj.put("OPEN_HOURS", jOpenHoursTimeArr);
					jsonObj.put("CLOSE_HOURS", jCloseHoursTimeArr);
					// jsonObj.put("OPEN/CLOSE_TIME", jarrayTimings);

					jsonObj.put("LOCATION_INFO_DESCRIPTION", jarrayLocationInfo);
					jsonObj.put("BRANCH_MANAGER", branchRecNode.getProperty(HercServicesConstants.BRANCH_COLUMN_BRANCH_MANAGER).getValue().getString());
					jsonObj.put("BRANCH_MANAGER_PHONE", branchRecNode.getProperty(HercServicesConstants.BRANCH_COLUMN_BRANCH_MANAGER_PHONE).getValue().getString());
					jsonObj.put("BRANCH_MANAGER_EMAIL", branchRecNode.getProperty(HercServicesConstants.BRANCH_COLUMN_BRANCH_MANAGER_EMAIL).getValue().getString());
					if (null != UserName) {
						UserName=hercCommonUtils.getAutoGenetratedNumber(UserName);
						String PreferenceQuery = HercServicesConstants.TABLE_MY_PREFERENCE_FAV_QRY + UserName + "' and @ BRANCH_NUMBER='"
								+ branchRecNode.getProperty("BRANCH_NUMBER").getValue().getString() + "'] order by @jcr:score";
						log.info("PreferenceQuery=====" + PreferenceQuery);
						Query PreferenceQueryc = queryManager.createQuery(PreferenceQuery, Query.XPATH);
						// iterate over results
						QueryResult PreferenceQueryResult = PreferenceQueryc.execute();
						NodeIterator favBranchNodes = PreferenceQueryResult.getNodes();
						if (favBranchNodes.getSize() > 0) {
							jsonObj.put("InFavorites", "Yes");
						} else {
							jsonObj.put("InFavorites", "No");
						}
					}
					if (jsonObj.length() == 0) {
						jsonObj.put("Status", "500");
						jsonObj.put("Status_message", "could not find any locations");
					} else {
						jsonObj.put("Status", "200");
						jsonObj.put("Status_message", "success");
					}
					log.info("jsonObj : " + jsonObj.toString());
					log.info("json response : " + jsonObj.toString());
					hercCommonUtils.sendStatusResponse(response, jsonObj);
				}
			}
		} catch (PathNotFoundException e) {
			log.error("PathNotFoundException Exception thrown", e);
		} catch (RepositoryException e) {
			log.error("RepositoryException  thrown ", e);
		} catch (IllegalStateException e) {
			log.error("RepositoryException Exception thrown", e);
		} catch (JSONException e) {
			log.error("JSONException Exception thrown", e);
		} catch (IOException e) {
			log.error("IOException Exception thrown", e);
		}
		log.info("Exiting getSolutions method");

		log.info("Exiting getSolutions method");
	}

	/**
	 * Get JSONArray for statewise list.
	 * 
	 * @param jarray
	 * @param jStateWiseObj
	 * @param STATE_NAME
	 * @param BRANCH_ID
	 * @param BRANCH_IMG_URL
	 * @param BRANCH_INFO_CONTENT
	 * @param categories
	 * @param isStateExist
	 * @return
	 * @throws JSONException
	 * @throws RepositoryException
	 * @throws PathNotFoundException
	 * @throws IllegalStateException
	 * @throws ValueFormatException
	 */
	public JSONArray getStateWiseJsonArray(Node branchRecNode, JSONArray jarray, JSONObject jStateWiseObj, JSONArray categories, boolean isStateExist,String deviceType, Session adminSession)
			throws JSONException, ValueFormatException, IllegalStateException, PathNotFoundException, RepositoryException {
		//log.info("Entering getStateWiseJsonArray method. ");
		JSONArray jBranchesArray = null;
		JSONObject jBranchObj = new JSONObject();
		String branchNumber = null;
		if (branchRecNode.hasProperty(HercServicesConstants.BRANCH_COLUMN_BRANCH_NUMBER)) {
			branchNumber = branchRecNode.getProperty(HercServicesConstants.BRANCH_COLUMN_BRANCH_NUMBER).getValue().getString();
			jBranchObj.put("BRANCH_CODE", branchNumber);
		}
		if (branchRecNode.hasProperty(HercServicesConstants.BRANCH_COLUMN_BRANCH_NAME)) {
			jBranchObj.put("BRANCH_NAME", branchRecNode.getProperty(HercServicesConstants.BRANCH_COLUMN_BRANCH_NAME).getValue().getString());
		}

		jBranchObj.put("CATEGORIES", categories);
		if (branchRecNode.hasProperty(HercServicesConstants.BRANCH_COLUMN_BRANCH_IMG_URL)) {
			String BRANCH_IMG_URL = null;
			boolean isIos = false;
			if (deviceType.equalsIgnoreCase(HercServicesConstants.IOS_DEVICE)) {
				BRANCH_IMG_URL = HercServicesConstants.DAM_PATH_BRANCH_DETAILS + branchNumber + HercServicesConstants.DAM_PATH_IMAGES + branchNumber + HercServicesConstants.JPG_EXTN +
						HercServicesConstants.JCRCONTENT_RENDITIONS  + branchNumber + HercServicesConstants.IOS_PNG_308_308;
				isIos = true;
			}
			if (deviceType.equalsIgnoreCase(HercServicesConstants.ANDROID_DEVICE)) {
				BRANCH_IMG_URL = HercServicesConstants.DAM_PATH_BRANCH_DETAILS + branchNumber + HercServicesConstants.DAM_PATH_IMAGES + branchNumber + HercServicesConstants.JPG_EXTN +
						HercServicesConstants.JCRCONTENT_RENDITIONS + branchNumber + HercServicesConstants.ANDROID_PNG_360_360;
			}
			log.info("DAM-BRANCH_IMG_URL " + BRANCH_IMG_URL);
			if (!adminSession.itemExists(BRANCH_IMG_URL)) {
				if(isIos){
					if(branchNumber.startsWith("9")){
						BRANCH_IMG_URL = HercServicesConstants.BRANCH_DEFAULT_THUMBNAIL_IOS_PNG_PATH_US;
					} else {
						BRANCH_IMG_URL = HercServicesConstants.BRANCH_DEFAULT_THUMBNAIL_IOS_PNG_PATH_CA;
					}					
				}else {
					if(branchNumber.startsWith("9")){
						BRANCH_IMG_URL = HercServicesConstants.BRANCH_DEFAULT_THUBNNAIL_ANDROID_PNG_PATH_US;
					} else {
						BRANCH_IMG_URL = HercServicesConstants.BRANCH_DEFAULT_THUBNNAIL_ANDROID_PNG_PATH_CA;
					}
					
				}
				
			}
			jBranchObj.put("BRANCH_IMG_URL", BRANCH_IMG_URL);
		}

		if (branchRecNode.hasProperty(HercServicesConstants.BRANCH_COLUMN_INFO_CONTENT)) {
			jBranchObj.put("BRANCH_DESCRIPTION", branchRecNode.getProperty(HercServicesConstants.BRANCH_COLUMN_INFO_CONTENT).getValue().getString());
		}

		if (branchRecNode.hasProperty(HercServicesConstants.BRANCH_COLUMN_CITY)) {
			jBranchObj.put("CITY", branchRecNode.getProperty(HercServicesConstants.BRANCH_COLUMN_CITY).getValue().getString());
		}

		if (branchRecNode.hasProperty(HercServicesConstants.BRANCH_COLUMN_STATE_NAME)) {
			jBranchObj.put("STATE", branchRecNode.getProperty(HercServicesConstants.BRANCH_COLUMN_STATE_NAME).getValue().getString());
		}

		if (branchRecNode.hasProperty(HercServicesConstants.BRANCH_COLUMN_ADRESS1)) {
			jBranchObj.put("ADRESS1", branchRecNode.getProperty(HercServicesConstants.BRANCH_COLUMN_ADRESS1).getValue().getString());
		}

		if (branchRecNode.hasProperty(HercServicesConstants.BRANCH_COLUMN_ADRESS2)) {
			jBranchObj.put("ADRESS2", branchRecNode.getProperty(HercServicesConstants.BRANCH_COLUMN_ADRESS2).getValue().getString());
		}

		if (branchRecNode.hasProperty(HercServicesConstants.BRANCH_COLUMN_ZIP)) {
			jBranchObj.put("ZIPCODE", branchRecNode.getProperty(HercServicesConstants.BRANCH_COLUMN_ZIP).getValue().getString());
		}
		if (!isStateExist) {
			if (branchRecNode.hasProperty(HercServicesConstants.BRANCH_COLUMN_STATE_NAME)) {
				jStateWiseObj.put("STATE", branchRecNode.getProperty(HercServicesConstants.BRANCH_COLUMN_STATE_NAME).getValue().getString());
			}
			jBranchesArray = new JSONArray();
			jBranchesArray.put(jBranchObj);
			jStateWiseObj.put("branches", jBranchesArray);
			jarray.put(jStateWiseObj);

		} else {
			if (jStateWiseObj.has("STATE")) {
				if (jStateWiseObj.has("branches")) {
					//log.info("jStateWiseObj contatins branches array");
					jBranchesArray = (JSONArray) jStateWiseObj.get("branches");
					jBranchesArray.put(jBranchObj);
				}
			}
		}
		/**
		 * create and add branch object end
		 */
		//log.info("Exiting getStateWiseJsonArray method. ");
		return jarray;

	}

	/**
	 * Set the favorite branch in MY_PREFERENCE table for each USER_NAME
	 * 
	 * @param tablesNode
	 * @param response
	 * @param USERNAME
	 * @param Branch_Id
	 * @param adminSession
	 * @param flag
	 * @throws SQLException
	 */
	public void setFavorite(Node tablesNode, SlingHttpServletResponse response, String userName, String BranchNum, String flag, Session adminSession) {
		log.info("Entering setFavorite method");
		log.info("flag : " + flag);
		String favQuery;
		String user=userName;
		userName=hercCommonUtils.getAutoGenetratedNumber(userName);
		log.info("AutoGenerated Number "+userName);
		try {
			JSONObject myprejobj = new JSONObject();
			if (tablesNode.hasNode(HercServicesConstants.TABLE_NAME_MY_PREFERENCE)) {
				
				favQuery = HercServicesConstants.TABLE_MY_PREFERENCE_FAV_QRY + userName + HercServicesConstants.QUERY_BRANCH_NUMBER_FAV + BranchNum
						+ HercServicesConstants.QUERY_ORDER_BY;
				log.info("favQuery=====" + favQuery);
				QueryManager queryManager = adminSession.getWorkspace().getQueryManager();

				// query for all nodes with tag "JCR"
				Query query = queryManager.createQuery(favQuery, Query.XPATH);

				// iterate over results
				QueryResult result = query.execute();
				NodeIterator nodes = result.getNodes();
				StringBuilder output = new StringBuilder();
				
				if (flag.equals("false") && nodes.getSize() > 0) {
					while (nodes.hasNext()) {
						
						dataSourcePoolService.tomcatConnection("/MysqlAPIs/rest/SetConnection/setFavorite/"+userName+"/"+BranchNum+"/"+flag);
						log.info("Favourite deleted in mysql URL used"+"/MysqlAPIs/rest/SetConnection/setFavorite/"+userName+"/"+BranchNum+"/"+flag);
						nodes.nextNode().remove();
					}
					myprejobj.put("FAVOURITES", "DISABLED");
					myprejobj.put("Status Code", "200");
					myprejobj.put("Status_message", "deleted " + BranchNum + " for " + userName);
				} else if (flag.equals("false") && nodes.getSize() == 0) {
					myprejobj.put("Status Code", "200");
					myprejobj.put("Status_message", "No Favorite exist to delete " + BranchNum + " for " + userName);

				} else if (flag.equals("true")) {
					log.info("flag value : " + flag);
					if (nodes.getSize() > 0) {
						log.info("Number of Results : " + nodes.getSize());
						myprejobj.put("Status Code", "200");
						myprejobj.put("Status_message", "Favorite already exist with BranchNumber = " + BranchNum + " for " + userName);
					} else {
						if (tablesNode.hasNode(HercServicesConstants.TABLE_NAME_MY_PREFERENCE)) {

							log.info("entering my preference table");
							
							int id = 0;
							id=Integer.parseInt(dataSourcePoolService.tomcatConnection("/MysqlAPIs/rest/SetConnection/setFavorite/"+user+"/"+BranchNum+"/"+flag+"/"+userName));
									log.info("ADDED to mypreference table with id " + id);
							Node pref = tablesNode.getNode(HercServicesConstants.TABLE_NAME_MY_PREFERENCE);
							long nodesCount = pref.getNodes().getSize() + 1;
							String depth = "" + nodesCount;
							String nodename = "record_" + System.currentTimeMillis();
							Node pref1 = pref.addNode(nodename, "herc:Record");
							pref1.setProperty("MY_PREFERENCE_ID", id + "", 3);
							pref1.setProperty("USERNAME", user, 1);
							pref1.setProperty("BRANCH_NUMBER", BranchNum, 3);
							pref1.setProperty("AUTO_GENERATED_NUMBER", userName, 3);
							
							myprejobj.put("FAVOURITES", "ENABLED");
							myprejobj.put("Status Code", "200");
							myprejobj.put("Status_message", "Added to Favourites");

						}
					}
				}
			}

			adminSession.save();
			hercCommonUtils.sendStatusResponse(response, myprejobj);

		} catch (PathNotFoundException e) {
			log.error("PathNotFoundException Exception thrown", e);
		} catch (RepositoryException e) {
			log.error("RepositoryException  thrown ", e);
		} catch (IllegalStateException e) {
			log.error("RepositoryException Exception thrown", e);
		} catch (JSONException e) {
			log.error("JSONException Exception thrown", e);
		} catch (IOException e) {
			log.error("IOException Exception thrown", e);
		} 
		log.info("Exiting setFavorite method");
	}
	
	
	public void getNearByBranchNew(SlingHttpServletResponse response, String cityorzip,String deviceType,Session adminSession)  {
		log.info("******* called Test getNearByBranchNew ************");
		if(cityorzip.startsWith("00"))		
			cityorzip=cityorzip.substring(2);
		else if(cityorzip.startsWith("0"))
			cityorzip=(cityorzip.substring(1));
		log.info("updated zip "+cityorzip);
		String is = dataSourcePoolService.tomcatConnection("/MysqlAPIs/rest/SetConnection/getNearByBranchNew/" + cityorzip);
		JSONObject j;
		String BRANCH_IMG_URL = null;
		String BRANCH_THUMBNAIL_IMG = null;
		Boolean isIos=false;
		try {
			j = new JSONObject(is);
			
			String branchNumber = j.getJSONArray("records").getJSONObject(0).getString("BRANCH_CODE");
			if (deviceType.equalsIgnoreCase(HercServicesConstants.IOS_DEVICE)) {
				BRANCH_IMG_URL = HercServicesConstants.DAM_PATH_BRANCH_DETAILS + branchNumber + HercServicesConstants.DAM_PATH_BANNER + branchNumber
						+ HercServicesConstants.JPG_EXTN + HercServicesConstants.JCRCONTENT_RENDITIONS + branchNumber + HercServicesConstants.IOS_PNG_1242_497;
				BRANCH_THUMBNAIL_IMG = HercServicesConstants.DAM_PATH_BRANCH_DETAILS + branchNumber + HercServicesConstants.DAM_PATH_IMAGES + branchNumber + HercServicesConstants.JPG_EXTN +
						HercServicesConstants.JCRCONTENT_RENDITIONS  + branchNumber + HercServicesConstants.IOS_PNG_308_308;
				
				isIos=true;
			}
			if (deviceType.equalsIgnoreCase(HercServicesConstants.ANDROID_DEVICE)) {
				BRANCH_IMG_URL = HercServicesConstants.DAM_PATH_BRANCH_DETAILS + branchNumber + HercServicesConstants.DAM_PATH_BANNER + branchNumber
						+ HercServicesConstants.JPG_EXTN + HercServicesConstants.JCRCONTENT_RENDITIONS + branchNumber + HercServicesConstants.ANDROID_PNG_1440_576;
				BRANCH_THUMBNAIL_IMG = HercServicesConstants.DAM_PATH_BRANCH_DETAILS + branchNumber + HercServicesConstants.DAM_PATH_IMAGES + branchNumber
						+ HercServicesConstants.JPG_EXTN + HercServicesConstants.JCRCONTENT_RENDITIONS + branchNumber + HercServicesConstants.ANDROID_PNG_360_360;
			}
			
			if (!adminSession.itemExists(BRANCH_IMG_URL)) {
				if(isIos){
					if(branchNumber.startsWith("9")){
						BRANCH_THUMBNAIL_IMG = HercServicesConstants.BRANCH_DEFAULT_THUMBNAIL_IOS_PNG_PATH_US;
					} else {
						BRANCH_THUMBNAIL_IMG = HercServicesConstants.BRANCH_DEFAULT_THUMBNAIL_IOS_PNG_PATH_CA;
					}
					if(branchNumber.startsWith("9")){
						BRANCH_IMG_URL = HercServicesConstants.BRANCH_DEFAULT_HEADER_IOS_PNG_PATH_US;
					} else {
						BRANCH_IMG_URL = HercServicesConstants.BRANCH_DEFAULT_THUMBNAIL_IOS_PNG_PATH_CA;
					}
																	
				}else {
					if(branchNumber.startsWith("9")){
						BRANCH_THUMBNAIL_IMG = HercServicesConstants.BRANCH_DEFAULT_THUBNNAIL_ANDROID_PNG_PATH_US;
					} else {
						BRANCH_THUMBNAIL_IMG = HercServicesConstants.BRANCH_DEFAULT_HEADER_ANDROID_PNG_PATH_CA;
					}
					if(branchNumber.startsWith("9")){
						BRANCH_IMG_URL = HercServicesConstants.BRANCH_DEFAULT_THUBNNAIL_ANDROID_PNG_PATH_US;
					} else {
						BRANCH_IMG_URL = HercServicesConstants.BRANCH_DEFAULT_THUBNNAIL_ANDROID_PNG_PATH_CA;
					}								
				}
				
			}
			
			j.getJSONArray("records").getJSONObject(0).put("BRANCH_IMG_URL", BRANCH_IMG_URL);
			j.getJSONArray("records").getJSONObject(0).put("BRANCH_IMG_URL_THUMBNAIL", BRANCH_THUMBNAIL_IMG);
			
			hercCommonUtils.sendStatusResponse(response, j);
		} catch (JSONException e) {
			log.error("JSONException Exception thrown", e);
		} catch (IOException e) {
			log.error("IOException Exception thrown", e);
		} catch (RepositoryException e) {
			log.error("RepositoryException Exception thrown", e);
		}
	

	}

	public JSONObject getProximityBranches(String cityorzipparam, String filter1, SlingHttpServletResponse response, Session adminSession) {
		log.info("******* called getProximityBranches START ************");
		String filter;
		if (filter1 != null)
			{
				log.info("filter not null :" + filter1);
				filter = filter1.replaceAll(" ", "_");
			} else
			{
				log.info("filter null :" + filter1);
				filter = "null";
			}

		try
			{
				//response from lookup cached
				String result = dataSourcePoolService.tomcatConnection("/MysqlAPIs/rest/SetConnection/lookup/" + cityorzipparam);

				JSONObject jobj = new JSONObject();

				jobj = new JSONObject(result);
				log.info("Result object " + jobj.toString());
				String status = jobj.getString("status");

				//check if zipcode or city available in the cached result.
				if (status.equals("OK"))
					{
						log.info("found in look up ");
						// call to tomcat service to get 10 resultant branches that are nearer to the provided city filtered based on categories selected. 
						String resultant = dataSourcePoolService.tomcatConnection("/MysqlAPIs/rest/SetConnection/getnearby/" + cityorzipparam + "/"
								+ jobj.getDouble("lat") + "/" + jobj.getDouble("lng") + "/" + filter);
						JSONObject jsonObj = new JSONObject(resultant);

						//hercCommonUtils.sendStatusResponse(response, jsonObj);

						return jsonObj;
					} else
					{
						log.info("*************** before maps calling ************");
						String inputLine = "";
						String goo = "";
						OpenTrustManager openTrustManager = new OpenTrustManager();
						//URL requestUrl = new URL("https://localhost:20443/HttpsClients/doRequest");
						//URLConnection conn = requestUrl.openConnection();
						URL urldemo = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + cityorzipparam
								+ "&sensor=true&key=AIzaSyCjv2EJOXpkd4GBAYMPBXQ-fPAmkQU3xh4");
						URLConnection yc = urldemo.openConnection();
						if (yc instanceof HttpsURLConnection)
							{
								log.info("applying trusted policy");
								OpenTrustManager.apply((HttpsURLConnection) yc);
							}

						BufferedReader in = null;
						try
							{
								in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

								while ((inputLine = in.readLine()) != null)
									{
										log.info("inputLine - while : " + inputLine);
										goo = goo + inputLine;
									}

								JSONObject json = new JSONObject(goo);

								if (!json.getString("status").equals("OK"))
									return json;

								// get the first result
								JSONObject res = json.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");

								double lat = res.getDouble("lat");
								double lng = res.getDouble("lng");

								// call to tomcat service to cache zipcode of the city provided
								dataSourcePoolService.tomcatConnection("/MysqlAPIs/rest/SetConnection/addtolookup/" + cityorzipparam + "/" + lat
										+ "/" + lng);

								// call to tomcat service to get 10 resultant branches that are nearer to the provided city filtered based on categories selected. 
								String resultant = dataSourcePoolService.tomcatConnection("/MysqlAPIs/rest/SetConnection/getnearby/" + cityorzipparam
										+ "/" + lat + "/" + lng + "/" + filter);

								JSONObject jsonObj = new JSONObject(resultant);

								//hercCommonUtils.sendStatusResponse(response, jsonObj);

								in.close();
								return jsonObj;
							} catch (Exception e)
							{
								log.error("exception occured", e);
							}

					}

			} catch (JSONException e1)
			{

				log.error("JSONException ", e1);
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyManagementException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return null;

	}
	
	
	public JSONObject getTopBranchZip(String cityorzipparam, SlingHttpServletResponse response, Session adminSession)  {
		log.info("******* called getTopBranchZip START ************");
		String filter = "null";

		try
			{
				//response from lookup cached
				String result = dataSourcePoolService.tomcatConnection("/MysqlAPIs/rest/SetConnection/lookup/" + cityorzipparam);

				JSONObject jobj = new JSONObject();

				jobj = new JSONObject(result);
				log.info("Result object " + jobj.toString());
				String status = jobj.getString("status");

				//check if zipcode or city available in the cached result.
				JSONObject Faulurejobjzip = new JSONObject();
				if (status.equals("OK"))
					{
						// call to tomcat service to get 10 resultant branches that are nearer to the provided city filtered based on categories selected. 
						String resultant = dataSourcePoolService.tomcatConnection("/MysqlAPIs/rest/SetConnection/getnearby/" + cityorzipparam + "/"
								+ jobj.getDouble("lat") + "/" + jobj.getDouble("lng") + "/" + filter);
						JSONObject jsonObj = new JSONObject(resultant);

						if (jsonObj.getString("status").equals("OK"))
							{

							String BranchCode = jsonObj.getJSONArray("records").getJSONObject(0).getString("BRANCH_CODE");
							 JSONObject jobjzip = new JSONObject();
							
							if(BranchCode.charAt(0)=='9')
							{
								 jobjzip.put("COUNTRY_CODE", "HG");
							}  
							else if(BranchCode.charAt(0)=='8')
							{
								 jobjzip.put("COUNTRY_CODE", "CR");
							} 
                            
							jobjzip.put("BRANCH_CODE", BranchCode.substring(1));
                            jobjzip.put("Status Code", "200");
                            jobjzip.put("Status Message", "success");
                          //  hercCommonUtils.sendStatusResponse(response, jobjzip);
                            return jobjzip;
							} else
							{
								
								
								Faulurejobjzip.put("Status Code", "500");
								Faulurejobjzip.put("Status Message", "failed");
								//hercCommonUtils.sendStatusResponse(response, jobjzip);
								return Faulurejobjzip;
							}
					} else
					{
						log.info("*************** before maps calling ************");
						String inputLine = "";
						String goo = "";
						OpenTrustManager openTrustManager = new OpenTrustManager();
						//URL requestUrl = new URL("https://localhost:20443/HttpsClients/doRequest");
						//URLConnection conn = requestUrl.openConnection();
						URL urldemo = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + cityorzipparam
								+ "&sensor=true&key=AIzaSyCjv2EJOXpkd4GBAYMPBXQ-fPAmkQU3xh4");
						URLConnection yc = urldemo.openConnection();
						if (yc instanceof HttpsURLConnection)
							{
								log.info("applying truseted policy");
								OpenTrustManager.apply((HttpsURLConnection) yc);
							}

						BufferedReader in = null;
						try
							{
								in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

								while ((inputLine = in.readLine()) != null)
									{
										log.info("inputLine - while : " + inputLine);
										goo = goo + inputLine;
									}

								JSONObject json = new JSONObject(goo);

								if (!json.getString("status").equals("OK")){
									Faulurejobjzip.put("Status Code", "500");
								      Faulurejobjzip.put("Status Message", "failed");
									return Faulurejobjzip ;
								} 

								// get the first result
								JSONObject res = json.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");

								double lat = res.getDouble("lat");
								double lng = res.getDouble("lng");

								// call to tomcat service to cache zipcode of the city provided
								log.info("	// call to tomcat service to cache zipcode of the city provided");
								dataSourcePoolService.tomcatConnection("/MysqlAPIs/rest/SetConnection/addtolookup/" + cityorzipparam + "/" + lat
										+ "/" + lng);

								log.info("// call to tomcat service for 10 resultant branches that are nearer to the provided city filtered based on categories selected. ");
								// call to tomcat service to get 10 resultant branches that are nearer to the provided city filtered based on categories selected. 
								String resultant = dataSourcePoolService.tomcatConnection("/MysqlAPIs/rest/SetConnection/getnearby/" + cityorzipparam
										+ "/" + lat + "/" + lng + "/" + filter);

								JSONObject jsonObj = new JSONObject(resultant);

								if (jsonObj.getString("status").equals("OK"))
									{

										String BranchZip = jsonObj.getJSONArray("records").getJSONObject(0).getString("ZIP_CODE");

										JSONObject jobjzip = new JSONObject();

										jobjzip.put("ZIP_CODE", BranchZip);
										jobjzip.put("Status Code", "200");
										jobjzip.put("Status Message", "success");

									//	hercCommonUtils.sendStatusResponse(response, jobjzip);
										return jobjzip;
									} else
									{
										JSONObject jobjzip = new JSONObject();

										jobjzip.put("Status Code", "500");
										jobjzip.put("Status Message", "failed");
										//hercCommonUtils.sendStatusResponse(response, jobjzip);
										return jobjzip;
									}
							} catch (Exception e)
							{
								log.error("exception occured", e);
							}

					}

			} catch (JSONException e1)
			{

				log.error("JSONException ", e1);
			} catch (IOException e) {
				log.error("IOException ", e);
			} catch (KeyManagementException e) {
				log.error("KeyManagementException ", e);
			} catch (NoSuchAlgorithmException e) {
				log.error("NoSuchAlgorithmException ", e);
			}
		return null;

	}

	public void getSurroundingCities(String branchNumber, SlingHttpServletResponse response, Session adminSession)  {
		log.info("******* called getSurroundingCities START ************");
		String filter;

		try
			{
				//response from lookup cached
				String result = dataSourcePoolService.tomcatConnection("/MysqlAPIs/rest/SetConnection/surroundingcities/" + branchNumber);

				JSONObject jobj = new JSONObject(result);

				log.info("Result object " + jobj.toString());
				String status = jobj.getString("status");

				//check if zipcode or city available in the cached result.
				if (status.equals("OK"))
					{
						jobj.put("Status Code", "200");
						jobj.put("Status Message", "success");
						hercCommonUtils.sendStatusResponse(response, jobj);

						return;
					} else
					{
						jobj.put("Status Code", "500");
						jobj.put("Status Message", "could not find any cities");
						hercCommonUtils.sendStatusResponse(response, jobj);

					}

			} catch (JSONException e1)
			{
				log.error("JSONException ", e1);
			} catch (IOException e) {
				log.error("IOException ", e);
			}		

	}
	
	public JSONObject getNearByBranchNewPro(String cityorzipparam, String deviceType, String filter1, SlingHttpServletResponse response,
			Session adminSession) {
		log.info("******* called getNearByBranchNewPro START ************");
		String filter;
		JSONObject j;
		String BRANCH_IMG_URL = null;
		String BRANCH_THUMBNAIL_IMG = null;
		Boolean isIos = false;
		String mandatorybranch = "";
		if (filter1 != null)
			{
				log.info("filter not null :" + filter1);
				filter = filter1.replaceAll(" ", "_");
			} else
			{
				log.info("filter not null :" + filter1);
				filter = "null";
			}
		cityorzipparam=cityorzipparam.toUpperCase();
		log.info("Origianl value  "+cityorzipparam);
		if(cityorzipparam.matches("[ABCEGHJKLMNPRSTVXY][0-9][ABCEGHJKLMNPRSTVWXYZ] ?[0-9][ABCEGHJKLMNPRSTVWXYZ][0-9]"))
		{
			log.info("Zip before truncating "+cityorzipparam);
			cityorzipparam=cityorzipparam.substring(0,3);
			log.info("Zip after truncating "+cityorzipparam);
		}
		if(cityorzipparam.contains(" "))
		{
			cityorzipparam=cityorzipparam.replace(" ", "%20");
			log.info("Zip after replacing "+cityorzipparam);
		}
		try
			{
				//response from lookup cached
			
				String result = dataSourcePoolService.tomcatConnection("/MysqlAPIs/rest/SetConnection/checkup/" + cityorzipparam);

				JSONObject jobj = new JSONObject();

				jobj = new JSONObject(result);
				log.info("Result object " + jobj.toString());
				String status = jobj.getString("status");

				//check if zipcode or city available in the cached result.
				if (status.equals("OK"))
					{
						log.info("found in look up ");
						if (jobj.getString("mandatorybranch").equals("") || jobj.getString("mandatorybranch") == null)
							{
								mandatorybranch = "null";
							} else
							{
								mandatorybranch = jobj.getString("mandatorybranch");
							}
						// call to tomcat service to get 10 resultant branches that are nearer to the provided city filtered based on categories selected. 
						String resultant = dataSourcePoolService.tomcatConnection("/MysqlAPIs/rest/SetConnection/getnearbypro/" + cityorzipparam
								+ "/" + mandatorybranch + "/" + jobj.getDouble("lat") + "/" + jobj.getDouble("lng") + "/" + filter);

						j = new JSONObject(resultant);
						for (int i = 0; i < 10; i++)
							{
								String branchNumber = j.getJSONArray("records").getJSONObject(i).getString("BRANCH_CODE");
								if (deviceType.equalsIgnoreCase(HercServicesConstants.IOS_DEVICE))
									{
										BRANCH_IMG_URL = HercServicesConstants.DAM_PATH_BRANCH_DETAILS + branchNumber
												+ HercServicesConstants.DAM_PATH_BANNER + branchNumber + HercServicesConstants.JPG_EXTN
												+ HercServicesConstants.JCRCONTENT_RENDITIONS + branchNumber + HercServicesConstants.IOS_PNG_1242_497;
										BRANCH_THUMBNAIL_IMG = HercServicesConstants.DAM_PATH_BRANCH_DETAILS + branchNumber
												+ HercServicesConstants.DAM_PATH_IMAGES + branchNumber + HercServicesConstants.JPG_EXTN
												+ HercServicesConstants.JCRCONTENT_RENDITIONS + branchNumber + HercServicesConstants.IOS_PNG_308_308;

										isIos = true;
									}
								if (deviceType.equalsIgnoreCase(HercServicesConstants.ANDROID_DEVICE))
									{
										BRANCH_IMG_URL = HercServicesConstants.DAM_PATH_BRANCH_DETAILS + branchNumber
												+ HercServicesConstants.DAM_PATH_BANNER + branchNumber + HercServicesConstants.JPG_EXTN
												+ HercServicesConstants.JCRCONTENT_RENDITIONS + branchNumber
												+ HercServicesConstants.ANDROID_PNG_1440_576;
										BRANCH_THUMBNAIL_IMG = HercServicesConstants.DAM_PATH_BRANCH_DETAILS + branchNumber
												+ HercServicesConstants.DAM_PATH_IMAGES + branchNumber + HercServicesConstants.JPG_EXTN
												+ HercServicesConstants.JCRCONTENT_RENDITIONS + branchNumber
												+ HercServicesConstants.ANDROID_PNG_360_360;
									}					
								if (!adminSession.itemExists(BRANCH_IMG_URL)) {
									if(isIos){
										if(branchNumber.startsWith("9")){
											BRANCH_THUMBNAIL_IMG = HercServicesConstants.BRANCH_DEFAULT_THUMBNAIL_IOS_PNG_PATH_US;
										} else {
											BRANCH_THUMBNAIL_IMG = HercServicesConstants.BRANCH_DEFAULT_THUMBNAIL_IOS_PNG_PATH_CA;
										}
										if(branchNumber.startsWith("9")){
											BRANCH_IMG_URL = HercServicesConstants.BRANCH_DEFAULT_HEADER_IOS_PNG_PATH_US;
										} else {
											BRANCH_IMG_URL = HercServicesConstants.BRANCH_DEFAULT_THUMBNAIL_IOS_PNG_PATH_CA;
										}
																						
									}else {
										if(branchNumber.startsWith("9")){
											BRANCH_THUMBNAIL_IMG = HercServicesConstants.BRANCH_DEFAULT_THUBNNAIL_ANDROID_PNG_PATH_US;
										} else {
											BRANCH_THUMBNAIL_IMG = HercServicesConstants.BRANCH_DEFAULT_HEADER_ANDROID_PNG_PATH_CA;
										}
										if(branchNumber.startsWith("9")){
											BRANCH_IMG_URL = HercServicesConstants.BRANCH_DEFAULT_THUBNNAIL_ANDROID_PNG_PATH_US;
										} else {
											BRANCH_IMG_URL = HercServicesConstants.BRANCH_DEFAULT_THUBNNAIL_ANDROID_PNG_PATH_CA;
										}								
									}
									
								}

								j.getJSONArray("records").getJSONObject(i).put("BRANCH_IMG_URL", BRANCH_IMG_URL);
								j.getJSONArray("records").getJSONObject(i).put("BRANCH_IMG_URL_THUMBNAIL", BRANCH_THUMBNAIL_IMG);
							}

						//hercCommonUtils.sendStatusResponse(response, jsonObj);

						return j;
					} else if (status.equals("NO"))
					{
						JSONObject jsonObj = new JSONObject();
						int size = 0;
						jsonObj.put("totalresults", size);
						jsonObj.put("records", "data not found");
						jsonObj.put("Status Code", "200");
						jsonObj.put("Status Message", "Please Enter valid and available zipcode");
						return jsonObj;

					}

			} catch (JSONException e1)
			{

				log.error("JSONException ", e1);
			} catch (RepositoryException e)
			{
				log.error("Repository Exception ", e);
			}
		return null;

	}

	/**
	 * Default activate method called when bundle activated
	 * 
	 * @param context
	 * @throws Exception
	 */
	protected void activate(ComponentContext context) throws Exception {
		log.debug("activate method called......");
		// Dictionary<String,String> properties = context.getProperties();
		// strDataSourceName = (String) properties.get("datasource.name");
		CATEGORIES_LIST = (String[]) context.getProperties().get("categories");
		log.debug("CATEGORIES_LIST : " + CATEGORIES_LIST.length);
		log.debug("activate method end ");
	}

	/**
	 * Default deactivate method called when bundle deactivated.
	 * 
	 * @param componentContext
	 * @throws Exception
	 */
	protected void deactivate(ComponentContext componentContext) throws Exception {
		log.debug("deactivate method called");
		log.debug("deactivate method called");
	}

}
