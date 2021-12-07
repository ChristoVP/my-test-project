/*
 *  Copyright 2014 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.herc.ewcm.core.impl.servlets;

import java.io.IOException;
import java.security.AccessControlException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HtmlResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.commons.datasource.poolservice.DataSourceNotFoundException;
import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.herc.ewcm.core.services.DataSourcePoolService;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */
@SuppressWarnings("serial")
@SlingServlet(paths = "/herc/servlets/catalogs", methods = "POST")
public class CreateUpdateCatalogsServlet extends SlingAllMethodsServlet {

	@Reference
	private DataSourcePoolService dataSourcePoolService;

	private static final long serialVersionUID = 7921759817203835647L;
	private Logger log = LoggerFactory.getLogger(CreateUpdateCatalogsServlet.class);

	protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {
		log.debug("Entering doPost method.");
		Session session = null;
		try {

			TagManager tagManager = request.getResourceResolver().adaptTo(TagManager.class);

			session = request.getResourceResolver().adaptTo(Session.class);
			HtmlResponse htmlResponse;
			htmlResponse = createCatalog(tagManager, request, session);
			if (null != htmlResponse) {
				htmlResponse.send(response, true);
			}

		} catch (Exception e) {
			HtmlResponse htmlResponse;
			log.error("Exception thrown in doPost method", e);
			htmlResponse = createResponse(500, e.getMessage());
			if (null != htmlResponse) {
				htmlResponse.send(response, true);
			}
		} finally {
			log.debug("Releasing the session.");
			if (null != session) {
				session.logout();
			}
		}
		log.debug("Exiting doPost method.");
	}

	/**
	 * This method create the Catalog based on cmd parameter.
	 * 
	 * @param tagManager
	 * @param request
	 * @return
	 */
	private HtmlResponse createCatalog(TagManager tagManager, SlingHttpServletRequest request, Session session) {

		String cmd = request.getParameter("cmd");
		String parent_category = request.getParameter("parentTagID");
		String catalog_name = null;
		String catalog_title = null;
		String cataglog_description = null;
		String catalog_id = null;
		String[] images = null;
		String subCat2Image = null;
		boolean isCategory = false;
		boolean isSubCategory = false;
		boolean isSubCategoryTwo = false;
		boolean isEquipment = false;
		Connection databaseConnection = null;
		HashMap<String, String> propertiesMap = new HashMap<String, String>();
		if (cmd.equals("createCategory")) {
			isCategory = true;
			catalog_title = request.getParameter("category_title");
			catalog_name = request.getParameter("category_name");
			cataglog_description = request.getParameter("category_description");
			catalog_id = request.getParameter("category_id");
			images = request.getParameterValues("category_images");
			log.debug("Number if category_images : " + images.length);

		} else if (cmd.equals("createSubCategory")) {
			isSubCategory = true;
			catalog_id = request.getParameter("subcategory_id");
			catalog_title = request.getParameter("subcategory_title");
			catalog_name = request.getParameter("subcategory_name");
			cataglog_description = request.getParameter("subcategory_description");
		} else if (cmd.equals("createSubCategoryTwo")) {
			isSubCategoryTwo = true;
			catalog_id = request.getParameter("subcategorytwo_id");
			catalog_title = request.getParameter("subcategorytwo_title");
			catalog_name = request.getParameter("subcategorytwo_name");
			cataglog_description = request.getParameter("subcategorytwo_description");
			subCat2Image = request.getParameter("subcategorytwo_image");
			log.debug("subcategorytwo_image path: " + subCat2Image);

		} else if (cmd.equals("createEquipment")) {
			isEquipment = true;
			catalog_id = request.getParameter("equipment_id");
			cataglog_description = request.getParameter("equipment_description");
			catalog_title = request.getParameter("equipment_title");
			catalog_name = request.getParameter("equipment_name");
		}
		log.debug("catalog_id : " + catalog_id);
		log.debug("catalog_title : " + catalog_title);
		log.debug("catalog_name : " + catalog_name);
		log.debug("cataglog_description : " + cataglog_description);
		log.debug("parent_category : " + parent_category);
		if (null != catalog_name) {
			String categoryID = null;
			String type = null;
			if ((parent_category != null) && (!"".equals(parent_category))) {
				type = "Tag";
				String tagID;
				if ((parent_category.endsWith(":")) || (parent_category.endsWith("/")))
					categoryID = parent_category + catalog_name;
				else
					categoryID = parent_category + "/" + catalog_name;
			} else {
				type = "Namespace";

				if ((catalog_name.contains("/")) || (catalog_name.contains(":"))) {
					return createResponse(400, "Parameter 'tag' must not contain ':' or '/' if parameter 'parentTagID' is not set (and a 'tag' denotes a namespace)");
				}

				categoryID = catalog_name + ":";
			}
			try {
				Tag tag = tagManager.createTag(categoryID, catalog_title, cataglog_description);
				String tableName = null;
				Node node = session.getNode(tag.getPath());
				if (isCategory) {
					node.setProperty("category_id", catalog_id);
					node.setProperty("category_title", catalog_title);
					node.setProperty("category_name", catalog_name);
					node.setProperty("category_description", cataglog_description);
					node.setProperty("category_images", images);
					node.setProperty("type", "category");

					propertiesMap.put("category_id ", catalog_id);
					propertiesMap.put("category_name", catalog_title);
					for (int i = 0; i < images.length; i++) {
						int j = i;
						j++;
						String imagecount = String.valueOf(j);
						String propname = "category_image" + imagecount;
						propertiesMap.put(propname, images[i]);
					}
					propertiesMap.put("category_description", cataglog_description);
					tableName = "category";
				} else if (isSubCategory) {
					node.setProperty("subcategory_id", catalog_id);
					node.setProperty("subcategory_title", catalog_title);
					node.setProperty("subcategory_name", catalog_name);
					node.setProperty("subcategory_description", cataglog_description);
					node.setProperty("type", "subcategory");

					propertiesMap.put("sub_cat_id ", catalog_id);
					propertiesMap.put("sub_cat_name", catalog_title);

					// get the parent tag name and id and put in properties map
					propertiesMap.put("category_id", session.getNode(tag.getParent().getPath()).getProperty("category_id").getString());
					propertiesMap.put("category_name", tag.getParent().getTitle());
					propertiesMap.put("sub_cat_description", cataglog_description);
					tableName = "subcategory";
				} else if (isSubCategoryTwo) {
					node.setProperty("subcategorytwo_id", catalog_id);
					node.setProperty("subcategorytwo_title", catalog_title);
					node.setProperty("subcategorytwo_name", catalog_name);
					node.setProperty("subcategorytwo_description", cataglog_description);
					node.setProperty("subcategorytwo_image", subCat2Image);
					node.setProperty("type", "subcategorytwo");

					// get the parent tag name and id and put in properties map
					propertiesMap.put("sub_cat2_id", catalog_id);
					propertiesMap.put("sub_cat2_name", catalog_title);
					propertiesMap.put("sub_cat_id", session.getNode(tag.getParent().getPath()).getProperty("subcategory_id").getString());
					propertiesMap.put("sub_cat_name", tag.getParent().getTitle());
					propertiesMap.put("sub_cat2_image", subCat2Image);
					propertiesMap.put("sub_cat2_description", cataglog_description);
					tableName = "subcategorytwo";
				} else if (isEquipment) {
					node.setProperty("equipment_id", catalog_id);
					node.setProperty("spec1", request.getParameter("specone"));
					node.setProperty("spec2", request.getParameter("spectwo"));
					node.setProperty("cat", request.getParameter("us_cat"));
					node.setProperty("class", request.getParameter("us_class"));
					node.setProperty("type", "equipment");

					// get the parent tag name and id and put in properties map
					propertiesMap.put("subcattwo_details_id", catalog_id);
					propertiesMap.put("spec1", request.getParameter("specone"));
					propertiesMap.put("spec2", request.getParameter("spectwo"));
					propertiesMap.put("us_cat", request.getParameter("us_cat"));
					propertiesMap.put("us_class", request.getParameter("us_class"));
					propertiesMap.put("ca_cat", request.getParameter("us_cat"));
					propertiesMap.put("ca_class", request.getParameter("us_class"));
					propertiesMap.put("sub_cat2_id", session.getNode(tag.getParent().getPath()).getProperty("subcategorytwo_id").getString());
					tableName = "subcattwo_details";
				} else {
					log.debug("cmd not found");
				}
				session.save();
				/**
				 * Insert catalogs data into My SQL DB start.
				 */
				log.debug("-------- Insert catalogs data into My SQL DB start------------");
				if (null != tableName && propertiesMap.size() > 0) {
					// Statement stmt = getStatement();
					databaseConnection = dataSourcePoolService.getDatabaseConnection();
					Statement stmt = databaseConnection.createStatement();
					String query = prepareQuery(tableName, propertiesMap);
					log.debug("got the stmt........");
					stmt.executeUpdate(query);
					log.debug("Records inserted successfully into TABLE : " + tableName);
				}
				log.debug("--------Insert catalogs data into My SQL DB end.------------");

				/**
				 * Insert catalogs data into My SQL SB end.
				 */
				return createResponse(200, type + " created", tag.getTagID(), tag.getPath());
			} catch (AccessControlException e) {
				return createResponse(403, "User '" + request.getUserPrincipal().getName() + "' is not allowed to create " + type + " '" + catalog_name + "'");
			} catch (InvalidTagFormatException e) {
				return createResponse(400, e.getMessage());
			} catch (PathNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RepositoryException e) {
				return createResponse(400, e.getMessage());
			} catch (SQLException e) {
				return createResponse(400, e.getMessage());
			} catch (DataSourceNotFoundException e) {
				return createResponse(400, e.getMessage());
			} finally {
				if (null != databaseConnection) {
					try {
						dataSourcePoolService.closeConnection(databaseConnection);
					} catch (SQLException e) {
						return createResponse(400, e.getMessage());
					}
				}

			}
		}
		return null;
	}

	/**
	 * This method creates HtmlResponse and return
	 * 
	 * @param statusCode
	 * @param message
	 * @param path
	 * @param location
	 * @return
	 */
	private HtmlResponse createResponse(int statusCode, String message, String path, String location) {
		HtmlResponse htmlResponse = new HtmlResponse();
		htmlResponse.setStatus(statusCode, message);
		htmlResponse.setTitle(message);

		if (location != null) {
			htmlResponse.setLocation(location);
			int pos = location.lastIndexOf('/');
			htmlResponse.setParentLocation(location.substring(0, pos));
		}
		if (path != null) {
			htmlResponse.setPath(path);
		}

		return htmlResponse;
	}

	/**
	 * This method creates HtmlResponse and return.
	 * 
	 * @param statusCode
	 * @param message
	 * @return
	 */
	private HtmlResponse createResponse(int statusCode, String message) {
		return createResponse(statusCode, message, null, null);
	}

	private String prepareQuery(String tableName, HashMap<String, String> propertiesMap) {
		StringBuffer querysbPropNames = new StringBuffer();
		String query;
		StringBuffer querysbPropValues = new StringBuffer();
		querysbPropNames.append("INSERT INTO " + tableName + " ( ");
		querysbPropValues.append("VALUES( ");
		Set<Entry<String, String>> entrySet = propertiesMap.entrySet();
		Iterator<Entry<String, String>> iterator = entrySet.iterator();
		int count = 0;
		while (iterator.hasNext()) {
			count++;
			Entry<String, String> propEntry = (Entry<String, String>) iterator.next();
			String propName = (String) propEntry.getKey();
			String propValue = (String) propEntry.getValue();
			log.debug("propName: " + propName);
			querysbPropNames.append(propName);
			if (count <= propertiesMap.size() - 1) {
				querysbPropNames.append(", ");
			} else {
				querysbPropNames.append(" )");
			}
			log.debug("propValue: " + propValue);
			if (propName.endsWith("id") || propName.endsWith("cat") || propName.endsWith("class")) {
				querysbPropValues.append(Integer.parseInt(propValue));
			} else {
				querysbPropValues.append("'");
				querysbPropValues.append(propValue);
				querysbPropValues.append("'");
			}
			if (count <= propertiesMap.size() - 1) {
				querysbPropValues.append(",");
			} else {
				querysbPropValues.append(" )");
			}
		}
		query = querysbPropNames.toString() + querysbPropValues.toString();
		log.debug("final insert QUERY : " + query);
		return query;
	}

	/**
	 * This method return sql statement.
	 * 
	 * @return
	 */
	private Statement getStatement() {
		Statement stmt = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			log.debug("MySQL JDBC Driver Registered!");
			Connection connection = null;
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/catalogs", "root", "poc1234");
			if (connection != null) {
				log.debug("Got the MySQl DB connection");
				stmt = connection.createStatement();
			} else {
				log.debug("Connection is null");
			}
		} catch (ClassNotFoundException e) {
			log.debug("ClassNotFoundException  thrown in getStatement", e);
		} catch (SQLException e) {
			log.debug("SQLException  thrown in getStatement", e);
		}
		return stmt;
	}
}
