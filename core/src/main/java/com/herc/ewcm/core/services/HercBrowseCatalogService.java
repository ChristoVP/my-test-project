package com.herc.ewcm.core.services;

import java.io.IOException;
import java.util.ArrayList;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.herc.ewcm.core.common.HercCommonUtils;
import com.herc.ewcm.core.common.HercServicesConstants;

@Component(name = "com.herc.ewcm.core.services.HercBrowseCatalogService", label = "HercBrowseCatalogService", description = "Handles all api calls of Herc BrowseCatalogService", specVersion = "1.1", immediate = true, metatype = true)
@Service(value = { HercBrowseCatalogService.class })
public class HercBrowseCatalogService {
	@Reference
	private HercCommonUtils hercCommonUtils;
	private static final Logger log = LoggerFactory
			.getLogger(HercBrowseCatalogService.class);

	public void getCatalogDetails(Node tablesNode,
			SlingHttpServletResponse response, String category, String type,
			String deviceType, String country, Session adminSession)
			throws IOException {
		log.info("Entering getCatalogDetails method");
		log.info("CategoryName : " + category);
		log.info("Type : " + type);
		if (category.contains(" and ")) {
			category = category.replace(" and ", " & ");
		}
		String DAM_NAME = hercCommonUtils.getUnderScroreName(category);

		log.info("DAM_NAME :" + DAM_NAME);
		DAM_NAME = DAM_NAME.replace("_", "-");

		String spec1 = null;
		String spec2 = null;
		String spec3 = null;
		String order = null;

		if (null != country) {
			if (country.equalsIgnoreCase("CR")) {
				log.info("IN " + country);

				spec1 = HercServicesConstants.PRODUCT_ITEM_COLUMN_SPEC_1_CN;
				spec2 = HercServicesConstants.PRODUCT_ITEM_COLUMN_SPEC_2_CN;
				spec3 = HercServicesConstants.PRODUCT_ITEM_COLUMN_SPEC_3_CN;
				order = HercServicesConstants.QUERY_ORDER_BY_SPEC1_CN;
			}
			if (country.equalsIgnoreCase("HG")) {
				log.info("IN " + country);

				spec1 = HercServicesConstants.PRODUCT_ITEM_COLUMN_SPEC_1;
				spec2 = HercServicesConstants.PRODUCT_ITEM_COLUMN_SPEC_2;
				spec3 = HercServicesConstants.PRODUCT_ITEM_COLUMN_SPEC_3;
				order = HercServicesConstants.QUERY_ORDER_BY_SPEC1;
			}
		} else {
			log.info("IN Default");

			spec1 = HercServicesConstants.PRODUCT_ITEM_COLUMN_SPEC_1;
			spec2 = HercServicesConstants.PRODUCT_ITEM_COLUMN_SPEC_2;
			spec3 = HercServicesConstants.PRODUCT_ITEM_COLUMN_SPEC_3;
			order = HercServicesConstants.QUERY_ORDER_BY_SPEC1;
		}

		String productquery = HercServicesConstants.QUERY_PRODUCT_ITEM;
		try {
			if (tablesNode
					.hasNode(HercServicesConstants.TABLE_NAME_PRODUCT_ITEM)) {
				if (type.equalsIgnoreCase(HercServicesConstants.PARAMETER_VALUE_CATEGORY)) {
					JSONArray jsonEquipArray = new JSONArray();
					JSONArray jsonSubCatArray = new JSONArray();
					JSONObject finalJobj = new JSONObject();
					ArrayList<String> catList = new ArrayList<String>();
					ArrayList<String> equipList = new ArrayList<String>();
					Boolean flag = false;
					String categoryQuery = productquery
							+ HercServicesConstants.QUERY_PRODUCT_ITEM_CATEGORY
							+ category
							+ HercServicesConstants.QUERY_ORDER_BY_SUBCAT;
					log.info("Category query----" + categoryQuery);
					QueryManager queryManager = adminSession.getWorkspace()
							.getQueryManager();
					Query catquery = queryManager.createQuery(categoryQuery,
							Query.XPATH);
					QueryResult categoryresult = catquery.execute();
					NodeIterator catnodes = categoryresult.getNodes();
					log.info("total nodes = " + catnodes.getSize());
					int count = 0;
					while (catnodes.hasNext()) {
						log.info("in while of categories");
						Node catnode = catnodes.nextNode();
						JSONObject jobj = new JSONObject();
						// log.info("Category - Node path  : " +
						// catnode.getPath());
						if (catnode
								.hasProperty(HercServicesConstants.PRODUCT_ITEM_COLUMN_SUB_CATEGORY_NAME)) {
							String sub_cat_name = catnode
									.getProperty(
											HercServicesConstants.PRODUCT_ITEM_COLUMN_SUB_CATEGORY_NAME)
									.getValue().getString();
							String recordSubCatName = sub_cat_name;
							log.info("recordSubCatName: " + recordSubCatName);
							if (sub_cat_name
									.equalsIgnoreCase(HercServicesConstants.NULL_VALUE)
									|| sub_cat_name
											.equals(HercServicesConstants.EMPTY_STRING)
									|| sub_cat_name
											.equals(HercServicesConstants.BLANK_SPACE)) {
								String equipmentName = catnode
										.getProperty(
												HercServicesConstants.PRODUCT_ITEM_COLUMN_EQUIPMENT_NAME)
										.getValue().getString();
								log.info("equipmentName : " + equipmentName);
								if (!equipList.contains(equipmentName)) {
									jobj.put("EQUIPMENT_NAME", equipmentName);
									equipmentName = hercCommonUtils
											.getUnderScroreName(equipmentName);
									equipmentName = equipmentName.replace("_",
											"-");
									log.info("equipmentName : " + equipmentName);
									/*
									 * String equipImagePath =
									 * HercServicesConstants
									 * .DAM_PATH_PRODUCT_ITEM_SPECIFICATION +
									 * DAM_NAME + HercServicesConstants.SLASH +
									 * equipmentName +
									 * HercServicesConstants.DAM_PATH_IMAGES_ICON
									 * + equipmentName + HercServicesConstants.
									 * DAM_PATH_ICON_PNG_FILE_EXTENSION;
									 */
									String equipImagePath = null;
									boolean isIos = false;
									if (deviceType
											.equalsIgnoreCase(HercServicesConstants.IOS_DEVICE)) {
										isIos = true;
										equipImagePath = HercServicesConstants.DAM_PATH_PRODUCT_ITEM_SPECIFICATION
												+ DAM_NAME
												+ HercServicesConstants.SLASH
												+ equipmentName
												+ HercServicesConstants.DAM_PATH_IMAGES
												+ equipmentName
												+ HercServicesConstants.JPG_EXTN
												+ HercServicesConstants.JCRCONTENT_RENDITIONS
												// + equipmentName
												+ HercServicesConstants.IOS1_PNG_308_308;
										log.info("equipImagePath : "
												+ equipImagePath);
									}
									if (deviceType
											.equals(HercServicesConstants.ANDROID_DEVICE)) {
										equipImagePath = HercServicesConstants.DAM_PATH_PRODUCT_ITEM_SPECIFICATION
												+ DAM_NAME
												+ HercServicesConstants.SLASH
												+ equipmentName
												+ HercServicesConstants.DAM_PATH_IMAGES
												+ equipmentName
												+ HercServicesConstants.JPG_EXTN
												+ HercServicesConstants.JCRCONTENT_RENDITIONS
												// + equipmentName
												+ HercServicesConstants.ANDROID1_PNG_360_360;
										log.info("equipImagePath : "
												+ equipImagePath);
									}
									log.info("cat-image path : "
											+ equipImagePath);
									try {
										if (!adminSession
												.itemExists(equipImagePath)) {
											if (isIos) {
												equipImagePath = HercServicesConstants.EQUIPMENT_DEFAULT_THUMBNAIL_IOS_PNG_PATH;
											} else {
												equipImagePath = HercServicesConstants.EQUIPMENT_DEFAULT_THUMBNAIL_ANDROID_PNG_PATH;
											}

										}
									} catch (RepositoryException e) {
										log.error("Repository Exception thrown due to invalid path.");
										if (isIos) {
											equipImagePath = HercServicesConstants.EQUIPMENT_DEFAULT_THUMBNAIL_IOS_PNG_PATH;
										} else {
											equipImagePath = HercServicesConstants.EQUIPMENT_DEFAULT_THUMBNAIL_ANDROID_PNG_PATH;
										}
									}
									jobj.put("EQUIPMENT_IMAGE", equipImagePath);
									jobj.put(
											"EQUIPMENT_DESCRIPTION",
											catnode.getProperty(
													HercServicesConstants.PRODUCT_ITEM_COLUMN_EQUIPMENT_LIST_VIEW_DESCRIPTION)
													.getValue().getString());
									jobj.put(
											"EQUIPMENT_SUB_DESCRIPTION",
											catnode.getProperty(
													HercServicesConstants.PRODUCT_ITEM_COLUMN_EQUIPMENT_LIST_VIEW_SUB_DESCRIPTION)
													.getValue().getString());
									jsonEquipArray.put(jobj);
									equipList
											.add(catnode
													.getProperty(
															HercServicesConstants.PRODUCT_ITEM_COLUMN_EQUIPMENT_NAME)
													.getValue().getString());
								}

							} else {
								flag = true;
								log.info("catList size : " + catList.size());
								if (!catList.contains(sub_cat_name)) {
									count++;
									log.info("count :" + count);
									catList.add(sub_cat_name);
									log.info("Added SubCategory Name : "
											+ sub_cat_name);
									sub_cat_name = hercCommonUtils
											.getUnderScroreName(sub_cat_name);
									log.info("sub_cat_name :" + sub_cat_name);
									sub_cat_name = sub_cat_name.replace("_",
											"-");
									log.info("sub_cat_name :" + sub_cat_name);
									String image_file_extension = null;
									/*
									 * String subCatImagePath =
									 * HercServicesConstants
									 * .DAM_PATH_PRODUCT_ITEM_SPECIFICATION +
									 * sub_cat_name +
									 * HercServicesConstants.DAM_PATH_IMAGES_ICON
									 * + sub_cat_name + HercServicesConstants.
									 * DAM_PATH_ICON_PNG_FILE_EXTENSION;
									 */
									String subCatImagePath = null;
									boolean isIos = false;
									if (deviceType
											.equals(HercServicesConstants.IOS_DEVICE)) {
										isIos = true;
										subCatImagePath = HercServicesConstants.DAM_PATH_PRODUCT_ITEM_SPECIFICATION
												+ DAM_NAME
												+ HercServicesConstants.SLASH
												+ sub_cat_name
												+ HercServicesConstants.DAM_PATH_IMAGES
												+ sub_cat_name
												+ HercServicesConstants.JPG_EXTN
												+ HercServicesConstants.JCRCONTENT_RENDITIONS
												// + sub_cat_name
												+ HercServicesConstants.IOS1_PNG_308_308;
									}
									if (deviceType
											.equals(HercServicesConstants.ANDROID_DEVICE)) {
										subCatImagePath = HercServicesConstants.DAM_PATH_PRODUCT_ITEM_SPECIFICATION
												+ DAM_NAME
												+ HercServicesConstants.SLASH
												+ sub_cat_name
												+ HercServicesConstants.DAM_PATH_IMAGES
												+ sub_cat_name
												+ HercServicesConstants.JPG_EXTN
												+ HercServicesConstants.JCRCONTENT_RENDITIONS
												// + sub_cat_name
												+ HercServicesConstants.ANDROID1_PNG_360_360;
									}
									log.info("cat-no-subcat image path :"
											+ subCatImagePath);
									if (!adminSession
											.itemExists(subCatImagePath)) {
										if (isIos) {
											subCatImagePath = HercServicesConstants.SUB_CATEGORY_DEFAULT_THUMBNAIL_IOS_PNG_PATH;
										} else {
											subCatImagePath = HercServicesConstants.SUB_CATEGORY_DEFAULT_THUMBNAIL_ANDROID_PNG_PATH;
										}

									}
									log.info("subCatImagePath : "
											+ subCatImagePath);
									log.info("recordSubCatName : "
											+ recordSubCatName);
									jobj.put("SUBCATEGORY_NAME",
											recordSubCatName);
									jobj.put("SUBCATEGORY_IMAGE",
											subCatImagePath);
									jsonSubCatArray.put(jobj);
								}

							}
						}
					}
					log.info("jsonEquipArray" + jsonEquipArray);
					log.info("jsonSubCatArray" + jsonSubCatArray);

					/*
					 * for (String temp : catList) { jsonSubCatArray.put(temp);
					 * }
					 */
					finalJobj.put("totalRecords", jsonSubCatArray.length()
							+ jsonEquipArray.length());
					finalJobj.put("SUB_CATEGORIES", jsonSubCatArray);

					finalJobj.put("EQUIPMENTS", jsonEquipArray);

					log.info("final json " + finalJobj);
					hercCommonUtils.sendStatusResponse(response, finalJobj);

				}
				if (type.equals(HercServicesConstants.PARAMETER_VALUE_SUBCATEGORY)) {
					log.info("Requested for sub category.");
					JSONObject finalobj = new JSONObject();
					String subcategoryQuery = productquery
							+ HercServicesConstants.QUERY_PRODUCT_ITEM_SUBCATEGORY
							+ category
							+ HercServicesConstants.QUERY_ORDER_BY_EQUIPMENT;
					log.info("subcategoryQuery : " + subcategoryQuery);
					QueryManager queryManager = adminSession.getWorkspace()
							.getQueryManager();
					Query subCatquery = queryManager.createQuery(
							subcategoryQuery, Query.XPATH);
					QueryResult subcatresult = subCatquery.execute();
					NodeIterator subcatnodes = subcatresult.getNodes();
					ArrayList<String> equipList = new ArrayList<String>();

					JSONArray subcatarray = new JSONArray();
					// get Sub Category Name as per DAM.
					String DAM_MAJORCATEGORY_NAME = null;
					while (subcatnodes.hasNext()) {
						Node subcatnode = subcatnodes.nextNode();
						JSONObject jobj = new JSONObject();
						String EQUIP_NAME = subcatnode
								.getProperty(
										HercServicesConstants.PRODUCT_ITEM_COLUMN_EQUIPMENT_NAME)
								.getValue().getString();
						if (!equipList.contains(EQUIP_NAME)) {
							equipList.add(EQUIP_NAME);
							// get Major Category Name
							/*
							 * log.info("subcatnode path : " +
							 * subcatnode.getPath());
							 */

							DAM_MAJORCATEGORY_NAME = subcatnode
									.getProperty(
											HercServicesConstants.COLUMN_MAJOR_CATEGORIES)
									.getValue().getString();
							log.info("DAM_MAJORCATEGORY_NAME : "
									+ DAM_MAJORCATEGORY_NAME);
							log.info("sub_cat_name :" + DAM_MAJORCATEGORY_NAME);
							DAM_MAJORCATEGORY_NAME = hercCommonUtils
									.getUnderScroreName(DAM_MAJORCATEGORY_NAME);
							log.info("sub_cat_name :" + DAM_MAJORCATEGORY_NAME);
							DAM_MAJORCATEGORY_NAME = DAM_MAJORCATEGORY_NAME
									.replace("_", "-");
							log.info("sub_cat_name :" + DAM_MAJORCATEGORY_NAME);
							jobj.put(spec1, subcatnode.getProperty(spec1)
									.getValue().getString());
							jobj.put(spec2, subcatnode.getProperty(spec2)
									.getValue().getString());
							jobj.put(spec3, subcatnode.getProperty(spec3)
									.getValue().getString());
							log.info("sub_cat_name :" + EQUIP_NAME);
							jobj.put("EQUIPMENT_NAME", EQUIP_NAME);
							EQUIP_NAME = hercCommonUtils
									.getUnderScroreName(EQUIP_NAME);
							log.info("sub_cat_name :" + EQUIP_NAME);
							EQUIP_NAME = EQUIP_NAME.replace("_", "-");
							log.info("sub_cat_name :" + EQUIP_NAME);
							/*
							 * String equipImagePath = HercServicesConstants.
							 * DAM_PATH_PRODUCT_ITEM_SPECIFICATION +
							 * DAM_MAJORCATEGORY_NAME +
							 * HercServicesConstants.SLASH + DAM_NAME +
							 * HercServicesConstants.DAM_PATH_IMAGES_ICON +
							 * EQUIP_NAME + HercServicesConstants.
							 * DAM_PATH_ICON_PNG_FILE_EXTENSION;
							 */
							String equipImagePath = null;
							boolean isIos = false;
							if (deviceType
									.equals(HercServicesConstants.IOS_DEVICE)) {
								isIos = true;
								equipImagePath = HercServicesConstants.DAM_PATH_PRODUCT_ITEM_SPECIFICATION
										+ DAM_MAJORCATEGORY_NAME
										+ HercServicesConstants.SLASH
										+ DAM_NAME
										+ HercServicesConstants.SLASH
										+ EQUIP_NAME
										+ HercServicesConstants.DAM_PATH_IMAGES
										+ EQUIP_NAME
										+ HercServicesConstants.JPG_EXTN
										+ HercServicesConstants.JCRCONTENT_RENDITIONS
										// + EQUIP_NAME
										+ HercServicesConstants.IOS1_PNG_308_308;
							}
							if (deviceType
									.equals(HercServicesConstants.ANDROID_DEVICE)) {
								equipImagePath = HercServicesConstants.DAM_PATH_PRODUCT_ITEM_SPECIFICATION
										+ DAM_MAJORCATEGORY_NAME
										+ HercServicesConstants.SLASH
										+ DAM_NAME
										+ HercServicesConstants.SLASH
										+ EQUIP_NAME
										+ HercServicesConstants.DAM_PATH_IMAGES
										+ EQUIP_NAME
										+ HercServicesConstants.JPG_EXTN
										+ HercServicesConstants.JCRCONTENT_RENDITIONS
										// + EQUIP_NAME
										+ HercServicesConstants.ANDROID1_PNG_360_360;
							}
							log.info("subcat-image path : " + equipImagePath);
							if (!adminSession.itemExists(equipImagePath)) {
								if (isIos) {
									equipImagePath = HercServicesConstants.EQUIPMENT_DEFAULT_THUMBNAIL_IOS_PNG_PATH;
								} else {
									equipImagePath = HercServicesConstants.EQUIPMENT_DEFAULT_THUMBNAIL_ANDROID_PNG_PATH;
								}

							}
							log.info("equipImagePath : " + equipImagePath);
							jobj.put("EQUIPMENT_IMAGE", equipImagePath);
							jobj.put(
									"EQUIPMENT_DESCRIPTION",
									subcatnode
											.getProperty(
													HercServicesConstants.PRODUCT_ITEM_COLUMN_EQUIPMENT_LIST_VIEW_DESCRIPTION)
											.getValue().getString());
							jobj.put(
									"EQUIPMENT_SUB_DESCRIPTION",
									subcatnode
											.getProperty(
													HercServicesConstants.PRODUCT_ITEM_COLUMN_EQUIPMENT_LIST_VIEW_SUB_DESCRIPTION)
											.getValue().getString());
							subcatarray.put(jobj);
						}

					}
					log.info("final array" + subcatarray);
					finalobj.put("totalRecords", subcatarray.length());
					finalobj.put("EQUIPMENTS", subcatarray);
					log.info("final response" + finalobj);
					hercCommonUtils.sendStatusResponse(response, finalobj);
				}
				if (type.equals(HercServicesConstants.PARAMETER_VALUE_EQUIPMENT)) {

					log.info("Equipment");
					JSONArray finalarray = new JSONArray();
					JSONObject finalobj = new JSONObject();
					String equipmentQuery = productquery
							+ HercServicesConstants.QUERY_PRODUCT_ITEM_EQUIPMENT
							+ category + order;
					log.info("equipmentQuery : " + equipmentQuery);
					QueryManager queryManager = adminSession.getWorkspace()
							.getQueryManager();
					Query equipQuery = queryManager.createQuery(equipmentQuery,
							Query.XPATH);
					QueryResult equipresult = equipQuery.execute();
					NodeIterator equipnodes = equipresult.getNodes();
					String DAM_MAJORCATEGORY_NAME = null;
					String DAM_SUBCATEGORY_NAME = null;
					while (equipnodes.hasNext()) {
						Node equipnode = equipnodes.nextNode();
						JSONObject jobj = new JSONObject();
						// get Major Category Name
						if (null == DAM_MAJORCATEGORY_NAME) {
							DAM_MAJORCATEGORY_NAME = equipnode
									.getProperty(
											HercServicesConstants.COLUMN_MAJOR_CATEGORIES)
									.getValue().getString();
							log.info("sub_cat_name :" + DAM_MAJORCATEGORY_NAME);
							DAM_MAJORCATEGORY_NAME = hercCommonUtils
									.getUnderScroreName(DAM_MAJORCATEGORY_NAME);
							log.info("sub_cat_name :" + DAM_MAJORCATEGORY_NAME);
							DAM_MAJORCATEGORY_NAME = DAM_MAJORCATEGORY_NAME
									.replace("_", "-");
							log.info("sub_cat_name :" + DAM_MAJORCATEGORY_NAME);
						}
						if (null == DAM_SUBCATEGORY_NAME) {
							DAM_SUBCATEGORY_NAME = equipnode
									.getProperty(
											HercServicesConstants.PRODUCT_ITEM_COLUMN_SUB_CATEGORY_NAME)
									.getValue().getString();
							log.info("sub_cat_name :" + DAM_SUBCATEGORY_NAME);
							DAM_SUBCATEGORY_NAME = hercCommonUtils
									.getUnderScroreName(DAM_SUBCATEGORY_NAME);
							log.info("sub_cat_name :" + DAM_SUBCATEGORY_NAME);
							DAM_SUBCATEGORY_NAME = DAM_SUBCATEGORY_NAME
									.replace("_", "-");
							log.info("sub_cat_name :" + DAM_SUBCATEGORY_NAME);
						}
						jobj.put(spec1, equipnode.getProperty(spec1).getValue()
								.getString());
						jobj.put(spec2, equipnode.getProperty(spec2).getValue()
								.getString());
						jobj.put(spec3, equipnode.getProperty(spec3).getValue()
								.getString());
						String equip_name = equipnode
								.getProperty(
										HercServicesConstants.PRODUCT_ITEM_COLUMN_EQUIPMENT_NAME)
								.getValue().getString();
						jobj.put("EQUIPMENT_NAME", equip_name);
						equip_name = hercCommonUtils
								.getUnderScroreName(equip_name);
						equip_name = equip_name.replace("_", "-");

						/*
						 * String equipImagePath =
						 * HercServicesConstants.DAM_PATH_PRODUCT_ITEM_SPECIFICATION
						 * + DAM_MAJORCATEGORY_NAME +
						 * HercServicesConstants.SLASH + DAM_SUBCATEGORY_NAME +
						 * equip_name + HercServicesConstants.DAM_PATH_IMAGES +
						 * equip_name +
						 * HercServicesConstants.DAM_PATH_IMAGE_PNG_FILE_EXTENSION
						 * ;
						 */
						String equipImagePath = null;
						boolean isIos = false;
						if (deviceType.equals(HercServicesConstants.IOS_DEVICE)) {
							isIos = true;
							if (null != DAM_SUBCATEGORY_NAME
									&& !DAM_SUBCATEGORY_NAME
											.equalsIgnoreCase("")) {
								equipImagePath = HercServicesConstants.DAM_PATH_PRODUCT_ITEM_SPECIFICATION
										+ DAM_MAJORCATEGORY_NAME
										+ HercServicesConstants.SLASH
										+ DAM_SUBCATEGORY_NAME
										+ HercServicesConstants.SLASH
										+ equip_name
										+ HercServicesConstants.DAM_PATH_IMAGES
										+ equip_name
										+ HercServicesConstants.JPG_EXTN
										+ HercServicesConstants.JCRCONTENT_RENDITIONS
										// + equip_name
										+ HercServicesConstants.IOS1_PNG_1242_497;
							} else {
								equipImagePath = HercServicesConstants.DAM_PATH_PRODUCT_ITEM_SPECIFICATION
										+ DAM_MAJORCATEGORY_NAME
										+ HercServicesConstants.SLASH
										+ equip_name
										+ HercServicesConstants.DAM_PATH_IMAGES
										+ equip_name
										+ HercServicesConstants.JPG_EXTN
										+ HercServicesConstants.JCRCONTENT_RENDITIONS
										// + equip_name
										+ HercServicesConstants.IOS1_PNG_1242_497;
							}
						}
						if (deviceType
								.equals(HercServicesConstants.ANDROID_DEVICE)) {
							if (null != DAM_SUBCATEGORY_NAME
									&& !DAM_SUBCATEGORY_NAME
											.equalsIgnoreCase("")) {
								equipImagePath = HercServicesConstants.DAM_PATH_PRODUCT_ITEM_SPECIFICATION
										+ DAM_MAJORCATEGORY_NAME
										+ HercServicesConstants.SLASH
										+ DAM_SUBCATEGORY_NAME
										+ HercServicesConstants.SLASH
										+ equip_name
										+ HercServicesConstants.DAM_PATH_IMAGES
										+ equip_name
										+ HercServicesConstants.JPG_EXTN
										+ HercServicesConstants.JCRCONTENT_RENDITIONS
										// + equip_name
										+ HercServicesConstants.ANDROID1_PNG_1440_576;
							} else {
								equipImagePath = HercServicesConstants.DAM_PATH_PRODUCT_ITEM_SPECIFICATION
										+ DAM_MAJORCATEGORY_NAME
										+ HercServicesConstants.SLASH
										+ equip_name
										+ HercServicesConstants.DAM_PATH_IMAGES
										+ equip_name
										+ HercServicesConstants.JPG_EXTN
										+ HercServicesConstants.JCRCONTENT_RENDITIONS
										// + equip_name
										+ HercServicesConstants.ANDROID1_PNG_1440_576;
							}
						}
						log.info("equipment-image path : " + equipImagePath);
						if (!adminSession.itemExists(equipImagePath)) {
							if (isIos) {
								equipImagePath = HercServicesConstants.EQUIPMENT_DEFAULT_HEADER_IOS_PNG_PATH;
							} else {
								equipImagePath = HercServicesConstants.EQUIPMENT_DEFAULT_HEADER_ANDROID_PNG_PATH;
							}

						}
						log.info("equipImagePath : " + equipImagePath);
						jobj.put("EQUIPMENT_IMAGE", equipImagePath);
						jobj.put(
								"EQUIPMENT_DESCRIPTION",
								equipnode
										.getProperty(
												HercServicesConstants.PRODUCT_ITEM_COLUMN_EQUIPMENT_LIST_VIEW_DESCRIPTION)
										.getValue().getString());
						jobj.put(
								"EQUIPMENT_SUB_DESCRIPTION",
								equipnode
										.getProperty(
												HercServicesConstants.PRODUCT_ITEM_COLUMN_EQUIPMENT_LIST_VIEW_SUB_DESCRIPTION)
										.getValue().getString());
						finalarray.put(jobj);

					}
					log.info("final array" + finalarray);
					finalobj.put("totalRecords", finalarray.length());
					finalobj.put("EQUIPMENTS", finalarray);
					log.info("final response" + finalobj);
					hercCommonUtils.sendStatusResponse(response, finalobj);

				}
			}
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			log.error("RepositoryException is caught", e);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			log.error("IllegalStateException is caught", e);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			log.error("JSONException is caught", e);
		}

	}

	public void getEquipSpecs(Node tablesNode,
			SlingHttpServletResponse response, String cat1, String class1,
			String country, Session adminSession) throws IOException {
		log.info("Entering getEquipSpecs method");
		log.info("Cat : " + cat1);
		log.info("Class : " + class1);
		String cat = null;
		String classs = null;
		String spec1 = null;
		String spec2 = null;
		String spec3 = null;

		String productquery = HercServicesConstants.QUERY_PRODUCT_ITEM;
		try {
			if (tablesNode
					.hasNode(HercServicesConstants.TABLE_NAME_PRODUCT_ITEM)) {

				if (country != null) {
					if (country.equalsIgnoreCase("CR")) {
						log.info("IN " + country);
						cat = HercServicesConstants.QUERY_PRODUCT_ITEM_CAT_CN;
						classs = HercServicesConstants.QUERY_PRODUCT_ITEM_CLASS_CN;
						spec1 = HercServicesConstants.PRODUCT_ITEM_COLUMN_SPEC_1_CN;
						spec2 = HercServicesConstants.PRODUCT_ITEM_COLUMN_SPEC_2_CN;
						spec3 = HercServicesConstants.PRODUCT_ITEM_COLUMN_SPEC_3_CN;
					}
					if (country.equalsIgnoreCase("HG")) {
						log.info("IN " + country);
						cat = HercServicesConstants.QUERY_PRODUCT_ITEM_CAT;
						classs = HercServicesConstants.QUERY_PRODUCT_ITEM_CLASS;
						spec1 = HercServicesConstants.PRODUCT_ITEM_COLUMN_SPEC_1;
						spec2 = HercServicesConstants.PRODUCT_ITEM_COLUMN_SPEC_2;
						spec3 = HercServicesConstants.PRODUCT_ITEM_COLUMN_SPEC_3;
					}
				} else {
					log.info("IN Default");
					cat = HercServicesConstants.QUERY_PRODUCT_ITEM_CAT;
					classs = HercServicesConstants.QUERY_PRODUCT_ITEM_CLASS;
					spec1 = HercServicesConstants.PRODUCT_ITEM_COLUMN_SPEC_1;
					spec2 = HercServicesConstants.PRODUCT_ITEM_COLUMN_SPEC_2;
					spec3 = HercServicesConstants.PRODUCT_ITEM_COLUMN_SPEC_3;
				}

				String categoryQuery = productquery + cat + cat1 + "' and "
						+ classs + class1
						+ HercServicesConstants.QUERY_ORDER_BY;
				log.info("Category query----" + categoryQuery);
				QueryManager queryManager = adminSession.getWorkspace()
						.getQueryManager();
				Query catquery = queryManager.createQuery(categoryQuery,
						Query.XPATH);
				QueryResult categoryresult = catquery.execute();
				NodeIterator catnodes = categoryresult.getNodes();
				log.info("total nodes = " + catnodes.getSize());
				int count = 0;
				JSONObject jobj = new JSONObject();
				if (catnodes.hasNext()) {
					log.info("in while of categories");
					Node catnode = catnodes.nextNode();

					// log.info("Category - Node path  : " + catnode.getPath());
					if ((catnode.hasProperty(spec1)
							&& catnode.hasProperty(spec2) && catnode
								.hasProperty(spec3))) {

						jobj.put("EQUIPMENT_NAME",
								catnode.getProperty("EQUIPMENT_NAME")
										.getValue().getString());
						jobj.put(spec1, catnode.getProperty(spec1).getValue()
								.getString());
						jobj.put(spec2, catnode.getProperty(spec2).getValue()
								.getString());
						jobj.put(spec3, catnode.getProperty(spec3).getValue()
								.getString());
						jobj.put(HercServicesConstants.STATUS_CODE, "200");
						jobj.put(HercServicesConstants.STATUS_MESSAGE,
								"Success");

					}
				} else {
					jobj.put(HercServicesConstants.STATUS_CODE, "500");
					jobj.put(HercServicesConstants.STATUS_MESSAGE,
							"Could not find EquipSpecs");
				}
				hercCommonUtils.sendStatusResponse(response, jobj);
			}

		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			log.error("RepositoryException is caught", e);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			log.error("IllegalStateException is caught", e);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			log.error("JSONException is caught", e);
		}

	}
}