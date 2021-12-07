package com.herc.ewcm.core.services;

import java.io.IOException;

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

@Component(name = "com.herc.ewcm.core.services.HercMasterProductListService", label = "HercMasterProductListService", description = "Handles all api calls of Herc HercMasterProductListService", specVersion = "1.1", immediate = true, metatype = true)
@Service(value = { HercMasterProductListService.class })
@Properties({ @Property(name = "solutioncolumns", description = "solution names mapping with solution column names", label = "Enter Soultion Name and Solution column by ~ separeated", value = {
		"entertainment~SOLUTION_ENTERTAINMENT_ITEM_DETAIL",
		"climate_control~SOLUTION_CLIMATE_CONTROL_ITEM_DETAIL",
		"pump~SOLUTION_PUMP_ITEM_DETAIL",
		"power_generation~SOLUTION_POWER_GENERATION_ITEM_DETAIL",
		"government~SOLUTION_GOVERNMENT_ITEM_DETAIL",
		"industrial~SOLUTION_INDUSTRIAL_ITEM_DETAIL",
		"remediation_restoration~SOLUTION_REMEDIATION_AND_RESTORATION_ITEM_DETAIL",
		"pro_contractor_tools~SOLUTION_PRO_CONTRACTOR_TOOLS_ITEM_DETAIL" }) })
public class HercMasterProductListService {

	private static final Logger log = LoggerFactory
			.getLogger(HercJobSiteService.class);

	@Reference
	private HercCommonUtils hercCommonUtils;
	private String[] SOLUTIONS_LIST;

	public void getMasterProductById(Node tableNode,
			SlingHttpServletResponse response, String ID, String type,
			String deviceType, String country, Session adminSession)
			throws IOException {

		Node tableNode1 = null;
		long flag = 0;
		log.info("Entering getMasterProductById method.....");
		log.info("Equipment Name===" + ID);
		String detailsQuery = null;
		String SOLUTION_COLUMN_NAME = null;
		Node mpl;
		String spec1 = null, spec2 = null, spec3 = null, cat = null, clas = null, orderby = null;
		if (null != country) {
			if (country.equalsIgnoreCase("CR")) {
				log.info("IN " + country);

				spec1 = HercServicesConstants.PRODUCT_ITEM_COLUMN_SPEC_1_CN;
				spec2 = HercServicesConstants.PRODUCT_ITEM_COLUMN_SPEC_2_CN;
				spec3 = HercServicesConstants.PRODUCT_ITEM_COLUMN_SPEC_3_CN;
				cat = HercServicesConstants.PRODUCT_ITEM_COLUMN_CAT_CN;
				clas = HercServicesConstants.PRODUCT_ITEM_COLUMN_CLASS_CN;
				orderby = "SPEC1_VALUE_CN";

			}
			if (country.equalsIgnoreCase("HG")) {
				log.info("IN " + country);
				spec1 = HercServicesConstants.PRODUCT_ITEM_COLUMN_SPEC_1;
				spec2 = HercServicesConstants.PRODUCT_ITEM_COLUMN_SPEC_2;
				spec3 = HercServicesConstants.PRODUCT_ITEM_COLUMN_SPEC_3;
				cat = HercServicesConstants.PRODUCT_ITEM_COLUMN_CAT_US;
				clas = HercServicesConstants.PRODUCT_ITEM_COLUMN_CLASS_US;
				orderby = "SPEC1_VALUE";

			}
		} else {
			log.info("IN Default");

			spec1 = HercServicesConstants.PRODUCT_ITEM_COLUMN_SPEC_1;
			spec2 = HercServicesConstants.PRODUCT_ITEM_COLUMN_SPEC_2;
			spec3 = HercServicesConstants.PRODUCT_ITEM_COLUMN_SPEC_3;
			cat = HercServicesConstants.PRODUCT_ITEM_COLUMN_CAT_US;
			clas = HercServicesConstants.PRODUCT_ITEM_COLUMN_CLASS_US;
			orderby = "SPEC1_VALUE";

		}
		try {
			if (tableNode
					.hasNode(HercServicesConstants.TABLE_NAME_PRODUCT_ITEM)) {
				tableNode1 = tableNode
						.getNode(HercServicesConstants.TABLE_NAME_PRODUCT_ITEM);
				log.info("node path : " + tableNode1.getPath());
				NodeIterator nodes = tableNode1.getNodes();
				String ID_CHANGE_NAME = ID;
				if (ID_CHANGE_NAME.contains(" and ")) {
					ID_CHANGE_NAME = ID_CHANGE_NAME.replace(" and ", " & ");
				}

				log.info("ID AFTER change : " + ID_CHANGE_NAME);
				QueryManager queryManager = adminSession.getWorkspace()
						.getQueryManager();
				if (type != null) {
					for (int i = 0; i < SOLUTIONS_LIST.length; i++) {
						if (SOLUTIONS_LIST[i].contains(type)) {
							String[] columnMappings = SOLUTIONS_LIST[i]
									.split("~");
							SOLUTION_COLUMN_NAME = columnMappings[1];
							log.info("SOLUTION_COLUMN_NAME"
									+ SOLUTION_COLUMN_NAME);
							break;
						} else {
							continue;
						}
					}
					detailsQuery = HercServicesConstants.QUERY_MASTER_PRODUCT_LIST_ID
							+ ID_CHANGE_NAME
							+ "' and @"
							+ SOLUTION_COLUMN_NAME
							+ "='Y'] order by @" + orderby;
				} else {
					detailsQuery = HercServicesConstants.QUERY_MASTER_PRODUCT_LIST_ID
							+ ID_CHANGE_NAME + "'] order by @" + orderby;
				}
				log.info("detailsQuery=====" + detailsQuery);
				Query masquery = queryManager.createQuery(detailsQuery,
						Query.XPATH);
				// iterate over results
				QueryResult masterresult = masquery.execute();
				NodeIterator masternodes = masterresult.getNodes();
				JSONArray jarray = new JSONArray();
				JSONObject finlob = new JSONObject();
				JSONArray jarray1 = new JSONArray();
				int count = 0;
				String DAM_MAJORCATEGORY_NAME = null;
				String DAM_SUBCATEGORY_NAME = null;
				String equipImagePath = null;
				while (masternodes.hasNext()) {
					Node recordNode = (Node) masternodes.next();
					if (!(recordNode.getProperty(cat).getValue().getString()
							.equalsIgnoreCase("") || recordNode
							.getProperty(clas).getValue().getString()
							.equalsIgnoreCase(""))) {
						JSONObject jsonObj = new JSONObject();
						if (count < 1) {
							count = 1;

							JSONObject jsonObj1 = new JSONObject();
							String equipmentName = recordNode
									.getProperty("EQUIPMENT_NAME").getValue()
									.getString();
							jsonObj1.put("EQUIPMENT_NAME", equipmentName);
							jsonObj1.put("EQUIPMENT_DESCRIPTION", recordNode
									.getProperty("EQUIPMENT_DESCRIPTION")
									.getValue().getString());

							DAM_MAJORCATEGORY_NAME = recordNode
									.getProperty(
											HercServicesConstants.COLUMN_MAJOR_CATEGORIES)
									.getValue().getString();
							DAM_MAJORCATEGORY_NAME = hercCommonUtils
									.getUnderScroreName(DAM_MAJORCATEGORY_NAME);
							DAM_MAJORCATEGORY_NAME = DAM_MAJORCATEGORY_NAME
									.replace("_", "-");
							DAM_SUBCATEGORY_NAME = recordNode
									.getProperty(
											HercServicesConstants.PRODUCT_ITEM_COLUMN_SUB_CATEGORY_NAME)
									.getValue().getString();
							equipmentName = hercCommonUtils
									.getUnderScroreName(equipmentName);
							equipmentName = equipmentName.replace("_", "-");
							if (DAM_SUBCATEGORY_NAME != null) {
								log.info("sub category name"
										+ DAM_SUBCATEGORY_NAME);
								DAM_SUBCATEGORY_NAME = hercCommonUtils
										.getUnderScroreName(DAM_SUBCATEGORY_NAME);
								DAM_SUBCATEGORY_NAME = DAM_SUBCATEGORY_NAME
										.replace("_", "-");
								log.info("Sub category name after change "
										+ DAM_SUBCATEGORY_NAME
										+ " dam equip name " + equipmentName);
								if (null != deviceType
										&& deviceType
												.equalsIgnoreCase(HercServicesConstants.IOS_DEVICE)) {
									equipImagePath = HercServicesConstants.DAM_PATH_PRODUCT_ITEM_SPECIFICATION
											+ DAM_MAJORCATEGORY_NAME
											+ HercServicesConstants.SLASH
											+ DAM_SUBCATEGORY_NAME
											+ HercServicesConstants.SLASH
											+ equipmentName
											+ HercServicesConstants.DAM_PATH_BANNER
											+ equipmentName
											+ HercServicesConstants.JPG_EXTN
											+ HercServicesConstants.JCRCONTENT_RENDITIONS
											// + equipmentName
											+ HercServicesConstants.IOS1_PNG_1242_497;
								} else if (null != deviceType
										&& deviceType
												.equalsIgnoreCase(HercServicesConstants.ANDROID_DEVICE)) {
									equipImagePath = HercServicesConstants.DAM_PATH_PRODUCT_ITEM_SPECIFICATION
											+ DAM_MAJORCATEGORY_NAME
											+ HercServicesConstants.SLASH
											+ DAM_SUBCATEGORY_NAME
											+ HercServicesConstants.SLASH
											+ equipmentName
											+ HercServicesConstants.DAM_PATH_BANNER
											+ equipmentName
											+ HercServicesConstants.JPG_EXTN
											+ HercServicesConstants.JCRCONTENT_RENDITIONS
											// + equipmentName
											+ HercServicesConstants.ANDROID1_PNG_1440_576;
								}
							}
							if (DAM_SUBCATEGORY_NAME
									.equalsIgnoreCase(HercServicesConstants.NULL_VALUE)
									|| DAM_SUBCATEGORY_NAME
											.equals(HercServicesConstants.EMPTY_STRING)
									|| DAM_SUBCATEGORY_NAME
											.equals(HercServicesConstants.BLANK_SPACE)) {
								log.info("sub category name is null"
										+ DAM_SUBCATEGORY_NAME);
								if (null != deviceType
										&& deviceType
												.equalsIgnoreCase(HercServicesConstants.IOS_DEVICE)) {
									equipImagePath = HercServicesConstants.DAM_PATH_PRODUCT_ITEM_SPECIFICATION
											+ DAM_MAJORCATEGORY_NAME
											+ HercServicesConstants.SLASH
											+ equipmentName
											+ HercServicesConstants.DAM_PATH_BANNER
											+ equipmentName
											+ HercServicesConstants.JPG_EXTN
											+ HercServicesConstants.JCRCONTENT_RENDITIONS
											// + equipmentName
											+ HercServicesConstants.IOS1_PNG_1242_497;
								} else if (null != deviceType
										&& deviceType
												.equalsIgnoreCase(HercServicesConstants.ANDROID_DEVICE)) {
									equipImagePath = HercServicesConstants.DAM_PATH_PRODUCT_ITEM_SPECIFICATION
											+ DAM_MAJORCATEGORY_NAME
											+ HercServicesConstants.SLASH
											+ equipmentName
											+ HercServicesConstants.DAM_PATH_BANNER
											+ equipmentName
											+ HercServicesConstants.JPG_EXTN
											+ HercServicesConstants.JCRCONTENT_RENDITIONS
											// + equipmentName
											+ HercServicesConstants.ANDROID1_PNG_1440_576;
								}
							}
							log.info("Equipment image path " + equipImagePath);
							if (null != equipImagePath
									&& !adminSession.itemExists(equipImagePath)) {
								if (deviceType
										.equalsIgnoreCase(HercServicesConstants.IOS)) {
									equipImagePath = HercServicesConstants.EQUIPMENT_DEFAULT_HEADER_IOS_PNG_PATH;
								} else {
									equipImagePath = HercServicesConstants.EQUIPMENT_DEFAULT_HEADER_ANDROID_PNG_PATH;
								}

							}

							jsonObj1.put("EQUIPMENT_IMAGE", equipImagePath);
							jarray1.put(jsonObj1);
						}

						jsonObj.put("SPEC1", recordNode.getProperty(spec1)
								.getValue().getString());
						jsonObj.put("SPEC2", recordNode.getProperty(spec2)
								.getValue().getString());
						jsonObj.put("SPEC3", recordNode.getProperty(spec3)
								.getValue().getString());
						jsonObj.put("EQUIPMENT_CAT", recordNode
								.getProperty(cat).getValue().getString());
						jsonObj.put("EQUIPMENT_CLASS",
								recordNode.getProperty(clas).getValue()
										.getString());
						// jsonObj.put("EQUIPMENT_QUANTITY",recordNode.getProperty("MODEL").getValue().getString()
						// );
						// jsonObj.put("E-MAIL_ADDRESS",recordNode.getProperty("MODEL").getValue().getString()
						// );
						// jsonObj.put("JOB_SITE", "");
						// jsonObj.put("JOB_SITE_CONTACT", "");
						// jsonObj.put("JOB_SITE_PHONE ", "");
						// jsonObj.put("JOB_SITE_ADDRESS", "");
						// jsonObj.put("JOB_SITE_CITY", "");
						// jsonObj.put("JOB_SITE_STATE", "");
						// jsonObj.put("JOB_SITE_ZIP", "");

						jarray.put(jsonObj);
					}

				}
				if (jarray.length() == 0)

				{

					finlob.put("Status Code", "500");
					finlob.put("Status_message", "Invalid id or name");

				} else {

					finlob.put("total Records", jarray.length());
					finlob.put("EQUIPMENT", jarray1);
					finlob.put("DETAILS", jarray);
					finlob.put("Status Code", "200");
					finlob.put("Status_message", "Success");
				}
				log.info("jsonObj : " + jarray);
				log.info("json response : " + jarray);

				hercCommonUtils.sendStatusResponse(response, finlob);
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
		log.info("Exiting masterProductList method");

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
		SOLUTIONS_LIST = (String[]) context.getProperties().get(
				"solutioncolumns");
		log.debug("SOLUTIONS_LIST : " + SOLUTIONS_LIST.length);
		log.debug("activate method end ");
	}

	/**
	 * Default deactivate method called when bundle deactivated.
	 * 
	 * @param componentContext
	 * @throws Exception
	 */
	protected void deactivate(ComponentContext componentContext)
			throws Exception {
		log.debug("deactivate method called");
		log.debug("deactivate method called");
	}

}
