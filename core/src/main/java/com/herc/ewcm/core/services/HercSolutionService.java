package com.herc.ewcm.core.services;

/**
 * 
 */

import java.io.IOException;
import java.util.ArrayList;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.herc.ewcm.core.common.HercCommonUtils;
import com.herc.ewcm.core.common.HercServicesConstants;

/**
 * @author CR324558
 *
 */
@Component(name = "com.herc.ewcm.core.services.HercSolutionService", label = "HercSolutionService", description = "Handles all api calls of Herc Solutions", specVersion = "1.1", immediate = true, metatype = true)
@Service(value = { HercSolutionService.class })
@Properties({ @Property(name = "solutioncolumns", description = "solution names mapping with solution column names", label = "Enter Soultion Name and Solution column by ~ separeated", value = {
		"entertainment~SOLUTION_ENTERTAINMENT_ITEM_DETAIL", "climate_control~SOLUTION_CLIMATE_CONTROL_ITEM_DETAIL", "pump~SOLUTION_PUMP_ITEM_DETAIL",
		"power_generation~SOLUTION_POWER_GENERATION_ITEM_DETAIL", "government~SOLUTION_GOVERNMENT_ITEM_DETAIL", "industrial~SOLUTION_INDUSTRIAL_ITEM_DETAIL",
		"remediation_restoration~SOLUTION_REMEDIATION_AND_RESTORATION_ITEM_DETAIL", "pro_contractor_tools~SOLUTION_PRO_CONTRACTOR_TOOLS_ITEM_DETAIL", "floor_care_surface_prep~SOLUTION_FLOOR_CARE_SURFACE_PREP_ITEM_DETAIL" }) })
public class HercSolutionService {
	@Reference
	private HercCommonUtils hercCommonUtils;
	private static final Logger log = LoggerFactory.getLogger(HercSolutionService.class);
	private String[] SOLUTIONS_LIST;

	public void getSolutions(Node tablesNode, SlingHttpServletResponse response, String countrycode, String deviceType, Session adminSession) throws IOException {
		log.info("Entering getSolutions method");

		Node solTab;
		try {
			if (tablesNode.hasNode(HercServicesConstants.TABLE_NAME_SOLUTION)) {
				solTab = tablesNode.getNode(HercServicesConstants.TABLE_NAME_SOLUTION);
				log.info("solTab path : " + solTab.getPath());
				JSONObject jsonOb = new JSONObject();
				QueryManager queryManager = adminSession.getWorkspace().getQueryManager();
				String solutionQuery = HercServicesConstants.QUERY_GET_SOLUTION_SOLUTIONCOUNTRY + "'" + countrycode + "')] order by @SOLUTION_ID";
				// 'CN')] order by @SOLUTION_ID";
				log.info("Solution Query->" + solutionQuery);
				Query solquery = queryManager.createQuery(solutionQuery, Query.XPATH);
				QueryResult solutionresult = solquery.execute();
				NodeIterator nodes = solutionresult.getNodes();
				log.info("no of nodes->" + nodes.getSize());
				JSONArray solution = new JSONArray();
				while (nodes.hasNext()) {
					log.info("in while");
					Node recordNode = (Node) nodes.next();
					JSONObject jsonObj = new JSONObject();
					String solutionName = recordNode.getProperty("SOLUTION_NAME").getValue().getString();
					jsonObj.put("SOLUTION_ID", recordNode.getProperty("SOLUTION_ID").getValue().getString());
					jsonObj.put("SOLUTION_NAME", solutionName);
					jsonObj.put("DETAILS", recordNode.getProperty("SOLUTION_DESCRIPTION").getValue().getString());
					// get the Icon image from dam.
					String assetName;
					if (solutionName.contains(HercServicesConstants.SPACE)) {
						assetName = hercCommonUtils.getUnderScroreName(solutionName);
					} else {
						assetName = solutionName.toLowerCase();
					}
					String THUMBNAIL_ICON_IMAGE = null;
					boolean isIos = false;
					if (deviceType.equalsIgnoreCase(HercServicesConstants.IOS_DEVICE)) {
						THUMBNAIL_ICON_IMAGE = HercServicesConstants.DAM_PATH_SOLUTIONS + assetName + HercServicesConstants.DAM_PATH_IMAGES + assetName
								+ HercServicesConstants.JPG_EXTN + HercServicesConstants.JCRCONTENT_RENDITIONS + assetName + HercServicesConstants.IOS_PNG_308_308;
						isIos = true;
					}
					if (deviceType.equalsIgnoreCase(HercServicesConstants.ANDROID_DEVICE)) {
						THUMBNAIL_ICON_IMAGE = HercServicesConstants.DAM_PATH_SOLUTIONS + assetName + HercServicesConstants.DAM_PATH_IMAGES + assetName
								+ HercServicesConstants.JPG_EXTN + HercServicesConstants.JCRCONTENT_RENDITIONS + assetName + HercServicesConstants.ANDROID_PNG_360_360;
					}
					log.info("DAM-THUMBNAIL_ICON_IMAGE : " + THUMBNAIL_ICON_IMAGE);

					if (!adminSession.itemExists(THUMBNAIL_ICON_IMAGE)) {
						if (isIos) {
							THUMBNAIL_ICON_IMAGE = HercServicesConstants.SOLUTION_DEFAULT_THUMBNAIL_IOS_PNG_PATH;
						} else {
							THUMBNAIL_ICON_IMAGE = HercServicesConstants.SOLUTION_DEFAULT_THUMBNAIL_ANDROID_PNG_PATH;
						}
					}
					/*
					 * if(!adminSession.itemExists(THUMBNAIL_ICON_IMAGE)){
					 * THUMBNAIL_ICON_IMAGE =
					 * HercServicesConstants.DAM_PATH_SOLUTIONS_DEAULT_ICON_IMAGE
					 * ; }
					 */
					log.info("FINAL-THUMBNAIL_ICON_IMAGE : " + THUMBNAIL_ICON_IMAGE);
					jsonObj.put("THUMBNAIL_ICON_IMAGE ", THUMBNAIL_ICON_IMAGE);

					// Remove code
					// jsonObj.put("THUMBNAIL_ICON_IMAGE ",
					// recordNode.getProperty("SOLUTION_ICON_IMAGE").getValue().getString());

					solution.put(jsonObj);
				}
				log.info("jsonObj : " + jsonOb.toString());
				if (solution.length() == 0) {
					jsonOb.put("STATUS_CODE", "500");
					jsonOb.put("STATUS_MESSAGE", "Invalid country code");
				} else {
					jsonOb.put("total records", solution.length());
					jsonOb.put("SOLUTIONS", solution);
					jsonOb.put("STATUS_CODE", "200");
					jsonOb.put("STATUS_MESSAGE", "Success");
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

	public JSONObject getSolutionDetails(Node tablesNode, SlingHttpServletResponse response, String solutionName, String deviceType, Session adminSession) throws IOException {
		log.info("Entering getSolutionDetails method");
		
		Node solTab;
		JSONObject jsonObj = new JSONObject();
		ArrayList<String> equipName=new ArrayList<String>();
		try {
			if (tablesNode.hasNode(HercServicesConstants.TABLE_NAME_SOLUTION)) {
				solTab = tablesNode.getNode(HercServicesConstants.TABLE_NAME_SOLUTION);
				log.info("solTab path : " + solTab.getPath());
				QueryManager queryManager = adminSession.getWorkspace().getQueryManager();
				String SOLUTION_COLUMN_NAME = null;
				String SOLUTION_NAME = hercCommonUtils.getUnderScroreName(solutionName);
				log.info("SOLUTION_NAME : " + SOLUTION_NAME );
				for (int i = 0; i < SOLUTIONS_LIST.length; i++) {
					if (SOLUTIONS_LIST[i].contains(SOLUTION_NAME)) {
						String[] columnMappings = SOLUTIONS_LIST[i].split("~");
						SOLUTION_COLUMN_NAME = columnMappings[1];
						log.info("SOLUTION_COLUMN_NAME : " + SOLUTION_COLUMN_NAME);
						break;
					} else {
						continue;
					}
				}
				// String solutionQuery =
				// HercServicesConstants.QUERY_GET_SOL_PRODUCT_ITEM + value[1] +
				// HercServicesConstants.QUERY_GET_SOL_PRODUCT_ITEM_END;
				String solutionQuery = HercServicesConstants.QUERY_GET_SOL_PRODUCT_ITEM + SOLUTION_COLUMN_NAME + HercServicesConstants.QUERY_GET_SOL_PRODUCT_ITEM_END;
				log.info("Solution Query->" + solutionQuery);
				Query solquery = queryManager.createQuery(solutionQuery, Query.XPATH);
				QueryResult solutionresult = solquery.execute();
				NodeIterator nodes = solutionresult.getNodes();
				
				JSONArray jr = new JSONArray();
				String DAM_MAJORCATEGORY_NAME = null;
				String DAM_SUBCATEGORY_NAME = null;
				while (nodes.hasNext()) {
					log.info("in ProductItem table");
					Node nodemas = nodes.nextNode();
					log.info("record node : " + nodemas.getPath());
					JSONObject js = new JSONObject();
					// get Major Category Name
					DAM_MAJORCATEGORY_NAME = nodemas.getProperty(HercServicesConstants.COLUMN_MAJOR_CATEGORIES).getValue().getString();
					DAM_MAJORCATEGORY_NAME = hercCommonUtils.getUnderScroreName(DAM_MAJORCATEGORY_NAME);
					DAM_SUBCATEGORY_NAME = nodemas.getProperty(HercServicesConstants.PRODUCT_ITEM_COLUMN_SUB_CATEGORY_NAME).getValue().getString();
					DAM_SUBCATEGORY_NAME = hercCommonUtils.getUnderScroreName(DAM_SUBCATEGORY_NAME);
					String equipmentName = nodemas.getProperty("EQUIPMENT_NAME").getValue().getString();
					String EQUIP_NAME = hercCommonUtils.getUnderScroreName(equipmentName);
						
					String EQUIPMENT_ICON_IMAGE = null;
					boolean isIos = false;
					boolean isAndroid = false;
					if(!equipName.contains(equipmentName))
					{
						equipName.add(equipmentName);
						if (deviceType.equalsIgnoreCase(HercServicesConstants.IOS_DEVICE)) {
							isIos = true;
							if (null != DAM_SUBCATEGORY_NAME && !DAM_SUBCATEGORY_NAME.equalsIgnoreCase("")) {
								EQUIPMENT_ICON_IMAGE = HercServicesConstants.DAM_PATH_PRODUCT_ITEM_SPECIFICATION + DAM_MAJORCATEGORY_NAME + HercServicesConstants.SLASH
										+ DAM_SUBCATEGORY_NAME + HercServicesConstants.SLASH + EQUIP_NAME + HercServicesConstants.DAM_PATH_IMAGES + EQUIP_NAME
										+ HercServicesConstants.JPG_EXTN + HercServicesConstants.JCRCONTENT_RENDITIONS + EQUIP_NAME + HercServicesConstants.IOS_PNG_308_308;
							} else {
								EQUIPMENT_ICON_IMAGE = HercServicesConstants.DAM_PATH_PRODUCT_ITEM_SPECIFICATION + DAM_MAJORCATEGORY_NAME + HercServicesConstants.SLASH + EQUIP_NAME
										+ HercServicesConstants.DAM_PATH_IMAGES + EQUIP_NAME + HercServicesConstants.JPG_EXTN + HercServicesConstants.JCRCONTENT_RENDITIONS
										+ EQUIP_NAME + HercServicesConstants.IOS_PNG_308_308;
							}
						}
						if (deviceType.equalsIgnoreCase(HercServicesConstants.ANDROID_DEVICE)) {
							if (null != DAM_SUBCATEGORY_NAME && !DAM_SUBCATEGORY_NAME.equalsIgnoreCase("")) {
								isAndroid=true;
								EQUIPMENT_ICON_IMAGE = HercServicesConstants.DAM_PATH_PRODUCT_ITEM_SPECIFICATION + DAM_MAJORCATEGORY_NAME + HercServicesConstants.SLASH
										+ DAM_SUBCATEGORY_NAME + HercServicesConstants.SLASH + EQUIP_NAME + HercServicesConstants.DAM_PATH_IMAGES + EQUIP_NAME
										+ HercServicesConstants.JPG_EXTN + HercServicesConstants.JCRCONTENT_RENDITIONS + EQUIP_NAME + HercServicesConstants.ANDROID_PNG_360_360;
							} else {
								EQUIPMENT_ICON_IMAGE = HercServicesConstants.DAM_PATH_PRODUCT_ITEM_SPECIFICATION + DAM_MAJORCATEGORY_NAME + HercServicesConstants.SLASH + EQUIP_NAME
										+ HercServicesConstants.DAM_PATH_IMAGES + EQUIP_NAME + HercServicesConstants.JPG_EXTN + HercServicesConstants.JCRCONTENT_RENDITIONS
										+ EQUIP_NAME + HercServicesConstants.ANDROID_PNG_360_360;
							}

						}
						if (deviceType.equalsIgnoreCase("Website")) {
							if (null != DAM_SUBCATEGORY_NAME && !DAM_SUBCATEGORY_NAME.equalsIgnoreCase("")) {
								EQUIPMENT_ICON_IMAGE = HercServicesConstants.DAM_PATH_PRODUCT_ITEM_SPECIFICATION + DAM_MAJORCATEGORY_NAME + HercServicesConstants.SLASH
										+ DAM_SUBCATEGORY_NAME + HercServicesConstants.SLASH + EQUIP_NAME + HercServicesConstants.DAM_PATH_IMAGES + EQUIP_NAME
										+ HercServicesConstants.JPG_EXTN;
							} else {
								EQUIPMENT_ICON_IMAGE = HercServicesConstants.DAM_PATH_PRODUCT_ITEM_SPECIFICATION + DAM_MAJORCATEGORY_NAME + HercServicesConstants.SLASH + EQUIP_NAME
										+ HercServicesConstants.DAM_PATH_IMAGES + EQUIP_NAME + HercServicesConstants.JPG_EXTN;
							}

						}
						log.info("DAM-EQUIPMENT_ICON_IMAGE : " + EQUIPMENT_ICON_IMAGE);
						if (!adminSession.itemExists(EQUIPMENT_ICON_IMAGE)) {
							if (isIos) {
								EQUIPMENT_ICON_IMAGE = HercServicesConstants.EQUIPMENT_DEFAULT_THUMBNAIL_IOS_PNG_PATH;
							} else if(isAndroid) {
								EQUIPMENT_ICON_IMAGE = HercServicesConstants.EQUIPMENT_DEFAULT_THUMBNAIL_ANDROID_PNG_PATH;
							}else{
								EQUIPMENT_ICON_IMAGE = "/content/dam/herc/product_item_specification/aerial/articulated_boom_lifts/carouselimage/carousel_img.jpg";
							}
							// EQUIPMENT_ICON_IMAGE =
							// HercServicesConstants.DAM_PATH_EQUIPMENTS_DEFAULT_ICON_IMAGE;
						}
						log.info("FINAL-EQUIPMENT_ICON_IMAGE : " + EQUIPMENT_ICON_IMAGE);
						js.put("EQUIPMENT_ICON_IMAGE", EQUIPMENT_ICON_IMAGE);
						js.put("EQUIPMENT_NAME", equipmentName);
						js.put("EQUIPMENT_DESCRIPTION", nodemas.getProperty("EQUIPMENT_DESCRIPTION").getValue().getString());
						js.put("EQUIPMENT_LIST_VIEW_DESCRIPTION", nodemas.getProperty("EQUIPMENT_LIST_VIEW_DESCRIPTION").getValue().getString());
						js.put("EQUIPMENT_LIST_VIEW_SUB_DESCRIPTION", nodemas.getProperty("EQUIPMENT_LIST_VIEW_SUB_DESCRIPTION").getValue().getString());

						jr.put(js);
					}

				}
				if (solutionName.contains(" and ")) {
					solutionName = solutionName.replace(" and ", " & ");
				}
				String getSolutionQuery = HercServicesConstants.QUERY_GET_SOLUTIONS_SOL + solutionName + "'] order by @jcr:score";
				log.info("query for solution table : " + getSolutionQuery);
				Query getsolquery = queryManager.createQuery(getSolutionQuery, Query.XPATH);
				QueryResult getsolresult = getsolquery.execute();
				NodeIterator node1 = getsolresult.getNodes();
				while (node1.hasNext()) {
					Node recordNode = node1.nextNode();
					// String solutionName =
					// recordNode.getProperty("SOLUTION_NAME").getValue().getString();
					jsonObj.put("SOLUTION_ID", recordNode.getProperty("SOLUTION_ID").getValue().getString());
					jsonObj.put("SOLUTION_NAME", solutionName);
					jsonObj.put("SOLUTION_DESCRIPTION", recordNode.getProperty("SOLUTION_DESCRIPTION").getValue().getString());
					jsonObj.put("EQUIPMENTS", jr);
					//
					// get the Icon image from dam.
					String assetName;
				
					if (solutionName.contains(HercServicesConstants.SPACE)) {
						assetName = hercCommonUtils.getUnderScroreName(solutionName);
						String[] assetNames = assetName.split("_");
						String name  = "";
						for(int i=0;i<assetNames.length;i++){
							if(i==0){
							name = Character.toUpperCase(assetNames[i].charAt(0)) + assetNames[i].substring(1);
							}else{
								name = name+Character.toUpperCase(assetNames[i].charAt(0)) + assetNames[i].substring(1);
							}
						}
 						if(name.equals("")){
 							assetName = Character.toUpperCase(assetName.charAt(0)) +assetName.substring(1);
 						}else{
 							assetName = Character.toUpperCase(assetName.charAt(0)) +assetName.substring(1);
 							assetName = name;
 						}
					} else {
						assetName = solutionName.toLowerCase();
					}
						if(assetName.equalsIgnoreCase("ProContractorTools")){
							assetName="ProContractor";
						
					}
						if(assetName.equalsIgnoreCase("RemediationRestoration")){
							assetName="RestorationRemediation";
						
					}
						
						
						
						assetName = Character.toUpperCase(assetName.charAt(0)) +assetName.substring(1);
				//	/content/dam/herc/solutions/solutionLandingImages/Entertainment-Large-2117-547-Solutions-Web.jpg/jcr:content/renditions/Entertainment-Large-2117-547-Solutions-Web_1242.497_ios.png
					String SOLUTION_IMAGE = null;
					boolean isIos = false;
					boolean isAndroid=false;
				
					if (deviceType.equalsIgnoreCase(HercServicesConstants.IOS_DEVICE)) {
						/*SOLUTION_IMAGE = HercServicesConstants.DAM_PATH_SOLUTIONS + assetName + HercServicesConstants.DAM_PATH_BANNER + assetName + HercServicesConstants.JPG_EXTN
								+ HercServicesConstants.JCRCONTENT_RENDITIONS + assetName + HercServicesConstants.IOS_PNG_1242_497;*/
						SOLUTION_IMAGE = HercServicesConstants.DAM_PATH_SOLUTIONS + "solutionLandingImages/"+assetName+"-Large-2117-547-Solutions-Web" +HercServicesConstants.JPG_EXTN
								+ HercServicesConstants.JCRCONTENT_RENDITIONS + assetName + "-Large-2117-547-Solutions-Web_1242.497_ios.png";
						isIos = true;
					}
					else if (deviceType.equalsIgnoreCase(HercServicesConstants.ANDROID_DEVICE)) {
					/*	SOLUTION_IMAGE = HercServicesConstants.DAM_PATH_SOLUTIONS + assetName + HercServicesConstants.DAM_PATH_BANNER + assetName + HercServicesConstants.JPG_EXTN
								+ HercServicesConstants.JCRCONTENT_RENDITIONS + assetName + HercServicesConstants.ANDROID_PNG_1440_576;*/
						SOLUTION_IMAGE = HercServicesConstants.DAM_PATH_SOLUTIONS + "solutionLandingImages/"+assetName+"-Large-2117-547-Solutions-Web" +HercServicesConstants.JPG_EXTN
								+ HercServicesConstants.JCRCONTENT_RENDITIONS + assetName + "-Large-2117-547-Solutions-Web_1440.576_android.png";
						isAndroid=true;
					}
					else if (deviceType.equalsIgnoreCase("Website"))
					{
						SOLUTION_IMAGE = HercServicesConstants.DAM_PATH_SOLUTIONS + assetName + HercServicesConstants.DAM_PATH_BANNER + assetName + HercServicesConstants.JPG_EXTN;
								
						
					}
					log.info("DAM-SOLUTION_IMAGE : " + SOLUTION_IMAGE);
					if (!adminSession.itemExists(SOLUTION_IMAGE)) {
						if (isIos) {
							//SOLUTION_IMAGE = HercServicesConstants.SOLUTION_DEFAULT_THUMBNAIL_IOS_PNG_PATH;
							SOLUTION_IMAGE = HercServicesConstants.SOLUTION_DEFAULT_HEADER_IOS_PNG_PATH;
						} else if(isAndroid) {
							//SOLUTION_IMAGE = HercServicesConstants.SOLUTION_DEFAULT_THUMBNAIL_ANDROID_PNG_PATH;
							 SOLUTION_IMAGE = HercServicesConstants.SOLUTION_DEFAULT_HEADER_ANDROID_PNG_PATH;
							
						}else 
						{
							SOLUTION_IMAGE = "/content/dam/herc/solutions/entertainment/icons/entertainment.png";
						}
					}

					jsonObj.put("SOLUTION_IMAGE", SOLUTION_IMAGE);
					// Remove code
					// jsonObj.put("SOLUTION_IMAGE",
					// recordNode.getProperty("SOLUTION_IMAGE").getValue().getString());
					jsonObj.put("SOLUTION_WEBSITE_LINK", recordNode.getProperty("SOLUTION_WEBSITE_LINK").getValue().getString());
					jsonObj.put("SOLUTION_DOCUMENT_PATH", recordNode.getProperty("SOLUTION_DOCUMENT_PATH").getValue().getString());
					jsonObj.put("SOLUTION_VIDEO_PATH", recordNode.getProperty("SOLUTION_VIDEO_PATH").getValue().getString());
					jsonObj.put("SOLUTION_COUNTRY", recordNode.getProperty("SOLUTION_COUNTRY").getValue().getString());
					jsonObj.put("SOLUTION_ICON_IMAGE", recordNode.getProperty("SOLUTION_ICON_IMAGE").getValue().getString());

					jsonObj.put("Status_Code", "200");
					jsonObj.put("Status_message", "Success");
					break;

				}

				log.info("jsonObj : " + jsonObj.toString());

				if (jsonObj.length() == 0)

				{
					jsonObj.put("Status Code", "500");
					jsonObj.put("Status_message", "Invalid id or name");
				}
				
				//hercCommonUtils.sendStatusResponse(response, jsonObj);
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
		log.info("Exiting getSolutionDetails method");
		return jsonObj;
		
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
		SOLUTIONS_LIST = (String[]) context.getProperties().get("solutioncolumns");
		log.debug("SOLUTIONS_LIST : " + SOLUTIONS_LIST.length);
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
