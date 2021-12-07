/**
 * 
 */
package com.herc.ewcm.core.services;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.commons.datasource.poolservice.DataSourceNotFoundException;
import com.herc.ewcm.core.common.HercCommonUtils;
import com.herc.ewcm.core.common.HercServicesConstants;
import com.herc.ewcm.core.common.NodeNotFoundException;

/**
 * @author VE332669
 *
 */
@Component(name = "com.herc.ewcm.core.services.HercJobSiteService", label = "HercJobSiteService", description = "", specVersion = "1.1", immediate = true, metatype = true)
@Service(value = { HercJobSiteService.class })
public class HercJobSiteService {

	private static final Logger log = LoggerFactory.getLogger(HercJobSiteService.class);

	@Reference
	private HercCommonUtils hercCommonUtils;
	@Reference
	private DataSourcePoolService dataSourcePoolService;
	
	

	@SuppressWarnings("deprecation")
	public void createJobsiteMobile(SlingHttpServletResponse response, String username, String folder, String newFolder, String cmd, RequestParameter file, String filePath,
			Session adminSession) {
		log.debug("Entering CreateJobSite.. method");
		log.info("cmd value in service : " + cmd);
		int flag = 0;
		Node tableNode1 = null;
		Node tableNode = null;
		Node userNode = null;
		Node folderNode = null;
		Node photoNode = null;
		Node photoNode1 = null;
		String imageName = null;
		String firstPhotoCheck=null,firstAlbumCheck=null;
		JSONObject jsonObj = new JSONObject();
		long noImageFlag = 0;
		Binary val = null;
		String fileName = null;		
		long maxUploadSize = 0;		
		boolean isNodeCreated = false;
		int userMaxUploadSize = 0;
		
		try {
			log.info("UserName : " + username);
			String user=hercCommonUtils.getAutoGenetratedNumber(username);
			log.info("Corresponding autoGenerated number : " + user);
			if (cmd.equalsIgnoreCase("post")) {
				if (null == file) {
					log.info("file is null");
					noImageFlag = 1;
				} else {
					log.info("file is not null");
					fileName = file.getFileName();
					log.info("fileName : " + fileName);
					if (null == fileName) {
						log.info("fileName is null");
					}
					byte[] fileContent = file.get();
					file.getInputStream();
					imageName = fileName;
					if (null != fileContent) {
						firstPhotoCheck=dataSourcePoolService.tomcatConnection("/MysqlAPIs/rest/SetConnection/isFirstPhoto/"+user);
						log.info("first photo check value -->>"+firstPhotoCheck);
						log.info("fileContent is not null : ");
					}
					InputStream is = new ByteArrayInputStream(fileContent);
					// InputStream is = file.getInputStream();
					val = adminSession.getValueFactory().createBinary(is);
				}
				
				if (null != folder) {
					
					firstAlbumCheck=dataSourcePoolService.tomcatConnection("/MysqlAPIs/rest/SetConnection/isFirstAlbum/"+user);
					log.info("first Album check value -->>"+firstAlbumCheck);
					log.info("FolderName : " + folder);
					folder=folder.replace(" ", "_");
					log.info("Encoded folder "+folder);
					if (adminSession.itemExists(HercServicesConstants.DAM_HERC_PATH)) {
						Node content = adminSession.getNode(HercServicesConstants.DAM_HERC_PATH);
						if (!content.hasNode(HercServicesConstants.TABLE_NAME_JOBSITE)) {
							tableNode = content.addNode(HercServicesConstants.TABLE_NAME_JOBSITE);
							isNodeCreated = true;
						} else {
							tableNode = content.getNode(HercServicesConstants.TABLE_NAME_JOBSITE);
						}
						if (!tableNode.hasNode(user)) {
							userNode = tableNode.addNode(user);
							isNodeCreated = true;
						} else {
							userNode = tableNode.getNode(user);
						}
						log.info("User " + user);
						if (!userNode.hasNode(folder)) {

							folderNode = userNode.addNode(folder);
						} else {
							folderNode = userNode.getNode(folder);

						}
					}
					/**
					 * create JobSite record in MYSQL MY_JOBSITE_ALBUMS table
					 * 
					 */
					if (adminSession.itemExists(HercServicesConstants.MY_SQL_TABLES_PATH)) {
						Node content1 = adminSession.getNode(HercServicesConstants.MY_SQL_TABLES_PATH);
						if (!content1.hasNode(HercServicesConstants.TABLE_NAME_JOBSITE)) {
							tableNode1 = content1.addNode(HercServicesConstants.TABLE_NAME_JOBSITE);
							adminSession.save();

						} else {
							tableNode1 = content1.getNode(HercServicesConstants.TABLE_NAME_JOBSITE);

						}
					}
					if (null != tableNode1) {
						QueryManager queryManager = adminSession.getWorkspace().getQueryManager();
						String jobQuery = HercServicesConstants.QUERY_JOBSITE_ALBUM_NAME + user + "'and @JOBSITE_ALBUM_NAME='" + folder + "'] order by @jcr:score";
						log.info("detailsQuery=====" + jobQuery);
						Query jobquery = queryManager.createQuery(jobQuery, Query.XPATH);
						// iterate over results
						QueryResult jobresult = jobquery.execute();
						NodeIterator jobnodes = jobresult.getNodes();
						long nodesCount = tableNode1.getNodes().getSize() + 1;
						if (jobnodes.getSize() > 0) {
							log.info("My_JOBSITE folder exist for User" + user);
							flag = 1;
						}
						log.info("final flag value : " + flag);
						if (flag == 0) {
							log.info("flag value is 0 ");
							int id = 0;
							
							
							if(null != file)
							{
								id=Integer.parseInt(dataSourcePoolService.tomcatConnection("/MysqlAPIs/rest/SetConnection/jobsiteCreation/"+username+"/"+folder+"/"+"1"+"/"+user));
							}
							else
							{
								id=Integer.parseInt(dataSourcePoolService.tomcatConnection("/MysqlAPIs/rest/SetConnection/jobsiteCreation/"+username+"/"+folder+"/"+"0"+"/"+user));								
							}
							
							log.info("Inserted in mysql with id "+id);
							// ** INSERT IN AEM till the syn job scheduled -
							// Ater sync job this will be deleted //
							
							photoNode1 = tableNode1.addNode(nodesCount + "_" + System.currentTimeMillis(), "herc:Record");
							photoNode1.setProperty("JOBSITE_ALBUMS_ID", id);
							photoNode1.setProperty("USER_NAME", username, 1);
							photoNode1.setProperty("JOBSITE_ALBUM_NAME", folder, 1);
							photoNode1.setProperty("AUTO_GENERATED_NUMBER", user, 3);
							adminSession.save();
							jsonObj.put("JOBSITE_ALBUMS_ID", id);
							jsonObj.put("USER_NAME", username);
							String foldertemp=folder;
							foldertemp=foldertemp.replace("_", " ");
							jsonObj.put("JOBSITE_ALBUM_NAME", foldertemp);

						}
					}
					//
					// check if user can upload the photo by checking photos
					// size
					if (null != file && userNode.hasProperty("usermaxuploadsize")) {
						maxUploadSize = userNode.getProperty("usermaxuploadsize").getLong();
						userMaxUploadSize = getUserMaxUploadSize(maxUploadSize + file.getSize());

					} else {
						if (file != null) {
							userNode.setProperty("usermaxuploadsize", file.getSize());
						}

					}
					if (userMaxUploadSize >= 50) {
						log.info("User Reached maximum upload limit.Can not upload photo");
						jsonObj.put("isMaxReached", "TRUE");
						jsonObj.put("Status Code", "200");
						jsonObj.put("Status_message", "Cannot Upload Photo. Maximum limit reached");

					} else {
						// create Photo if photo send.
						if (file != null) {
							userNode.setProperty("usermaxuploadsize", maxUploadSize + file.getSize());
						}
						if (null != imageName && !folderNode.hasNode(imageName)) {
							log.info("Folder does not contain photo - Creating photo");
							String parentPath = HercServicesConstants.DAM_HERC_PATH + "/" + HercServicesConstants.TABLE_NAME_JOBSITE + "/" + user + "/" + folder;
							log.info("imageName : " + imageName);
							photoNode = folderNode.addNode(imageName, "dam:Asset");
							Node jcr = photoNode.addNode("jcr:content", "dam:AssetContent");
							jcr.setProperty("cq:name", imageName);
							jcr.setProperty("cq:parentPath", parentPath);
							Node metadata = jcr.addNode("metadata", "nt:unstructured");
							Node renditions = jcr.addNode("renditions", "nt:folder");
							Node original = renditions.addNode("original", "nt:file");
							Node jcrResource = original.addNode("jcr:content", "nt:resource");
							jcrResource.setProperty("jcr:data", val);
							adminSession.save();
							if (photoNode != null) {
								jsonObj.put("IMAGE_PATH", photoNode.getPath());
							}
							if(null!=file)
							{
								dataSourcePoolService.tomcatConnection("/MysqlAPIs/rest/SetConnection/jobsiteCreation/"+username+"/"+folder+"/"+"1"+"/"+user);
							}
							jsonObj.put("isMaxReached", "FALSE");
							jsonObj.put("isFirstPhoto", firstPhotoCheck);
							jsonObj.put("isFirstAlbum", firstAlbumCheck);
							jsonObj.put("Status Code", "200");
							jsonObj.put("Status_message", "Folder Created with Photo");
						} else if (noImageFlag == 1) {
							jsonObj.put("isFirstAlbum", firstAlbumCheck);
							jsonObj.put("Status Code", "200");
							jsonObj.put("Status_message", "Folder Created without photo");
						} else if (null != imageName && folderNode.hasNode(imageName)) {
							jsonObj.put("Status Code", "500");
							jsonObj.put("Status_message", "Already Exist");
						}
					}
				}

			}
			if (cmd.equalsIgnoreCase("delete")) {
				log.info("entering to delete ");

				if (adminSession.itemExists(filePath)) {
					Node node = adminSession.getNode(filePath);
					String path = node.getPath();
					Node deletenode=adminSession.getNode(path+"/jcr:content/renditions/original/jcr:content");
					long size=deletenode.getProperty("jcr:data").getBinary().getSize();
					log.info("AEM image size="+size);
					Node temp=node;
					while(!temp.hasProperty("usermaxuploadsize"))
					{
						temp=temp.getParent();
						if(temp.hasProperty("usermaxuploadsize"))
						{
							temp.setProperty("usermaxuploadsize", temp.getProperty("usermaxuploadsize").getValue().getLong()-size);
							break;
						}
					}
					node.remove();
					adminSession.save();
					jsonObj.put("Status Code", "200");
					jsonObj.put("Status_message", "Photo Deleted at" + path);
					log.info("jdelete object : " + jsonObj.toString());
				} else {
					jsonObj.put("Status Code", "200");
					jsonObj.put("Status_message", "Did not find photo to delete at" + filePath);
					log.info("jdelete object : " + jsonObj.toString());
				}

			}
			/**
			 * to modify the folder to newFolder
			 */
			if (cmd.equalsIgnoreCase(HercServicesConstants.JOBSITE_ACTION_MODIFY)) {
				log.info("entering for the cmd  : " + cmd);
				newFolder=newFolder.replace(" ", "_");
				log.info("encoded folder name "+newFolder);
				folder=folder.replace(" ", "_");
				log.info("encoded folder  "+folder);
				if (adminSession.itemExists(HercServicesConstants.DAM_HERC_PATH)) {
					Node content = adminSession.getNode(HercServicesConstants.DAM_HERC_PATH);
					if (!content.hasNode(HercServicesConstants.TABLE_NAME_JOBSITE)) {
						tableNode = content.addNode(HercServicesConstants.TABLE_NAME_JOBSITE);
					} else {
						tableNode = content.getNode(HercServicesConstants.TABLE_NAME_JOBSITE);
					}
					if (!tableNode.hasNode(user)) {
						userNode = tableNode.addNode(user);
					} else {
						userNode = tableNode.getNode(user);
					}
					log.info(user+"FOlder name "+folder);
					if (userNode.hasNode(folder)) {
						log.info("Asd");
						adminSession.move(HercServicesConstants.DAM_HERC_PATH + "/" + HercServicesConstants.TABLE_NAME_JOBSITE + "/" + user + "/" + folder,
								HercServicesConstants.DAM_HERC_PATH + "/" + HercServicesConstants.TABLE_NAME_JOBSITE + "/" + user + "/" + newFolder);
					}

				} else {
					log.info("content------------------------------------->path doesn't Exist");
				}
				adminSession.save();
				if (adminSession.itemExists(HercServicesConstants.MY_SQL_TABLES_PATH)) {
					Node content1 = adminSession.getNode(HercServicesConstants.MY_SQL_TABLES_PATH);

					if (!content1.hasNode(HercServicesConstants.TABLE_NAME_JOBSITE)) {
						tableNode1 = content1.addNode(HercServicesConstants.TABLE_NAME_JOBSITE);

					} else {
						tableNode1 = content1.getNode(HercServicesConstants.TABLE_NAME_JOBSITE);
					}
					/**
					 * change the record in myjobsite_albums table in AEM
					 */
					QueryManager queryManager = adminSession.getWorkspace().getQueryManager();
					Query query = queryManager.createQuery("/jcr:root/etc/system/mysqltables/my_jobsite_albums//element(*,herc:Record)[@JOBSITE_ALBUM_NAME ='" + folder
							+ "' and @AUTO_GENERATED_NUMBER='"+user+"'] order by @jcr:score", Query.XPATH);
					log.info("query "+query);
					QueryResult results = query.execute();
					NodeIterator nodes = (NodeIterator) results.getNodes();
					while (nodes.hasNext()) {
						Node phNode = (Node) nodes.nextNode();
						phNode.setProperty("JOBSITE_ALBUM_NAME", newFolder);
					}
				} else {
					log.info("table------------------------------------->item doesn't Exist");
				}
				adminSession.save();
					dataSourcePoolService.tomcatConnection("/MysqlAPIs/rest/SetConnection/jobsiteModification/"+user+"/"+folder+"/"+newFolder);
				
				
				jsonObj.put("Status", "200");
				jsonObj.put("Status_message", "success");
				jsonObj.put("path", "Folder moved from " + folder + " to " + newFolder.replace("_", " "));
				log.info("jsonObj : " + jsonObj.toString());
				// hercCommonUtils.sendStatusResponse(response, modifyjsonObj);

				log.info("Requested createJobsiteMobile API call.");

				log.info("Exiting getJobsite.. method");

			}

		} catch (ValueFormatException e) {
			log.error(" ValueFormatException Exception Thrown", e);
		} catch (PathNotFoundException e) {
			log.error(" PathNotFoundException Exception Thrown", e);
		} catch (RepositoryException e) {
			log.error(" RepositoryException Exception Thrown", e);
		} catch (JSONException e) {
			log.error(" JSONException Exception Thrown", e);
		} catch (IOException e) {
			log.error(" IOException Exception Thrown", e);
		} finally {
			try {

				if (jsonObj != null) {
					hercCommonUtils.sendStatusResponse(response, jsonObj);
				}
				
			} catch (JSONException e) {
				log.error(" JSONException Exception Thrown", e);
			} catch (IOException e) {
				log.error(" IOException Exception Thrown", e);
			}

		}

	}

	public void getJobSiteAlbums(Session adminSession, SlingHttpServletResponse response, String userName) throws IOException {
		log.info("Entering getJobSiteAlbums method");
		Node jobsite_albums_tableNode = null;
		Node etcTables = null;
		Node damMyjobsiteAlbumsFolder = null;
		String firstAlbumCheck=null;
		String name=userName;
		log.info("User name"+userName);
		userName=hercCommonUtils.getAutoGenetratedNumber(userName);
		log.info("Corresponding autogen num "+userName);
		try {

			// Check whether the path upto "/etc/system/mysqltables" exits
			if (adminSession.itemExists(HercServicesConstants.MY_SQL_TABLES_PATH)) {
				etcTables = adminSession.getNode(HercServicesConstants.MY_SQL_TABLES_PATH); // get
				// node
				// /etc/system/mysqltables
				if (etcTables.hasNode(HercServicesConstants.TABLE_NAME_JOBSITE)) {
					jobsite_albums_tableNode = etcTables.getNode(HercServicesConstants.TABLE_NAME_JOBSITE);
				} else {
					log.info("No Such Table Found");

				}
			} else {
				log.info("#############################################" + HercServicesConstants.MY_SQL_TABLES_PATH + " Doesn't Exist "
						+ "############################################");
			}
			if (adminSession.itemExists(HercServicesConstants.DAM_HERC_PATH)) {

				Node damHercFolderNode = adminSession.getNode(HercServicesConstants.DAM_HERC_PATH);
				log.info("");

				if (damHercFolderNode.hasNode(HercServicesConstants.TABLE_NAME_JOBSITE)) {
					damMyjobsiteAlbumsFolder = damHercFolderNode.getNode(HercServicesConstants.TABLE_NAME_JOBSITE);
				}
			}

			JSONObject finalJObj = new JSONObject();
			JSONArray finalArray = new JSONArray();
			NodeIterator nodes = jobsite_albums_tableNode.getNodes();
			long numberOfFolders = 0;
			while (nodes.hasNext()) {
				Node recordNode = (Node) nodes.next();
				String USER_NAME = recordNode.getProperty("AUTO_GENERATED_NUMBER").getValue().getString();
				if (userName.equals(USER_NAME)) {
					JSONObject foldersJsonObj = new JSONObject();
					log.info(" ******* Requested username match with record **************");
					long JOBSITE_ALBUMS_ID = recordNode.getProperty("JOBSITE_ALBUMS_ID").getValue().getLong();
					String JOBSITE_ALBUM_NAME = recordNode.getProperty("JOBSITE_ALBUM_NAME").getValue().getString();
					String tempAlbumName=JOBSITE_ALBUM_NAME;
					if(JOBSITE_ALBUM_NAME.contains("_"))
					{
						tempAlbumName=JOBSITE_ALBUM_NAME.replace("_", " ");
					}
					log.info("adding values to foldersJsonObj");
					foldersJsonObj.put("JOBSITE_ALBUMS_ID", JOBSITE_ALBUMS_ID);
					foldersJsonObj.put("USER_NAME", name);
					foldersJsonObj.put("JOBSITE_ALBUM_NAME", tempAlbumName);
					
					foldersJsonObj.put("isFirstAlbum", firstAlbumCheck);
					// get the folder Names for user
					if (damMyjobsiteAlbumsFolder.hasNode(userName)) {
						Node userNameFolder = damMyjobsiteAlbumsFolder.getNode(userName);
						NodeIterator userFolders = userNameFolder.getNodes();
						numberOfFolders = userFolders.getSize();
						while (userFolders.hasNext()) {
							Node folderNode = (Node) userFolders.next();
							if (!(folderNode.getName().equalsIgnoreCase("jcr:content")) && (JOBSITE_ALBUM_NAME.equalsIgnoreCase(folderNode.getName()))) {
								log.info("user folder contains folder with album name : " + JOBSITE_ALBUM_NAME + "---" + folderNode.getName());
								NodeIterator photos = folderNode.getNodes();
								JSONArray myjobsitealbumsArr = new JSONArray();
								while (photos.hasNext()) {
									Node photoNode = (Node) photos.next();
									if (!photoNode.getName().equalsIgnoreCase("jcr:content")) {
										String photoPath = photoNode.getPath();
										log.info("photoPath : " + photoPath);
										myjobsitealbumsArr.put(photoPath);
									}

								}
								foldersJsonObj.put("JOBSITE_ALBUMS", myjobsitealbumsArr);
							}
						}

					}
					finalArray.put(foldersJsonObj);
				}
			}
			finalJObj.put("totalFolders", finalArray.length());
			firstAlbumCheck = dataSourcePoolService.tomcatConnection("/MysqlAPIs/rest/SetConnection/isFirstAlbum/"+userName);
			log.info("first Album check value -->>"+firstAlbumCheck);
			String firstPhotoCheck = dataSourcePoolService.tomcatConnection("/MysqlAPIs/rest/SetConnection/isFirstPhoto/"+userName);
			log.info("first photo check value -->>"+firstPhotoCheck);
			finalJObj.put("isFirstAlbum", firstAlbumCheck);
			finalJObj.put("isFirstPhoto", firstPhotoCheck);
			finalJObj.put("albums", finalArray);
			if(finalArray.length()>0)
			{
				finalJObj.put(HercServicesConstants.STATUS_CODE,"200");
				finalJObj.put(HercServicesConstants.STATUS_MESSAGE,"Success");
			}
			else
			{
				finalJObj.put(HercServicesConstants.STATUS_CODE,"200");
				finalJObj.put(HercServicesConstants.STATUS_MESSAGE,"Folders not Found");
			}
			hercCommonUtils.sendStatusResponse(response, finalJObj);

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

	public void deleteJob(Session adminsession, SlingHttpServletResponse response, String user, String folder) {
		log.info("Entering deleteJob method");
		log.info("user : " + user);
		log.info("folder : " + folder);
		
		
		Node tableNode = null;
		Node userNode = null;
		Node folderNode = null;
		String username=user;
		int count=0;
		user=hercCommonUtils.getAutoGenetratedNumber(user);
		log.info("auto generated num "+user);
		try {
			folder=folder.replace(" ", "_");
			log.info("Decoded folder : " + folder);
			log.info("entering to delete ");

			if (adminsession.itemExists(HercServicesConstants.DAM_HERC_PATH)) {
				Node content = adminsession.getNode(HercServicesConstants.DAM_HERC_PATH);
				if (content.hasNode(HercServicesConstants.TABLE_NAME_JOBSITE)) {
					tableNode = content.getNode(HercServicesConstants.TABLE_NAME_JOBSITE);
					log.info("tableNode Path : " + tableNode.getPath());
				} else {
					throw new NodeNotFoundException(HercServicesConstants.TABLE_NAME_JOBSITE + " Node Not Found");
				}
				if (tableNode.hasNode(user)) {
					log.info("table node contain " + user + "node");
					userNode = tableNode.getNode(user);
				} else {
					log.info("table node does not contain " + user + "node");
					count=0;
					//throw new NodeNotFoundException(user + " Node Not Found");
				}
				if (userNode.hasNode(folder)) {
					log.info("userNode node does contain " + folder + "node");
					folderNode = userNode.getNode(folder);
					
					long size=0;
					NodeIterator folderNodes=folderNode.getNodes();
					while(folderNodes.hasNext())
					{
						Node temp=folderNodes.nextNode();
						log.info("temp node path "+temp.getPath());
						String imageNodepath=temp.getPath().concat("/jcr:content/renditions/original/jcr:content");
						
						Node ImageNode=adminsession.getNode(imageNodepath);
						size=ImageNode.getProperty("jcr:data").getBinary().getSize();
						if(userNode.hasProperty("usermaxuploadsize"))
						{
							userNode.setProperty("usermaxuploadsize",userNode.getProperty("usermaxuploadsize").getValue().getLong()-size);
							log.info("user size value "+userNode.getProperty("usermaxuploadsize").getValue().getLong());
						}
					}
					
					
					folderNode.remove();
				} else {
					log.info("userNode node does not contain " + folder + "node");
					count=0;
				}
			} else {
				log.info("content------------------------------------->path doesn't Exist");
			}
			adminsession.save();

			Node tableNode1 = null;

			if (adminsession.itemExists(HercServicesConstants.MY_SQL_TABLES_PATH)) {
				Node content1 = adminsession.getNode(HercServicesConstants.MY_SQL_TABLES_PATH);
				adminsession.save();
				if (content1.hasNode(HercServicesConstants.TABLE_NAME_JOBSITE)) {
					tableNode1 = content1.getNode(HercServicesConstants.TABLE_NAME_JOBSITE);
					adminsession.save();
				} else {
					count=0;
				}
				NodeIterator nodes = tableNode1.getNodes();
				JSONObject jsonObj = new JSONObject();
				
				while (nodes.hasNext()) {
					log.info("in while");
					Node recordNode = (Node) nodes.next();
					if (user.equals(recordNode.getProperty("AUTO_GENERATED_NUMBER").getString()) && folder.equals(recordNode.getProperty("JOBSITE_ALBUM_NAME").getString().toString())) {
						jsonObj.put("USER_NAME", username);
						jsonObj.put("JOBSITE_ALBUM_NAME", recordNode.getProperty("JOBSITE_ALBUM_NAME").getValue().getString());
						jsonObj.put("JOBSITE_ALBUMS_ID", recordNode.getProperty("JOBSITE_ALBUMS_ID").getValue().getString());
						String id=dataSourcePoolService.tomcatConnection("/MysqlAPIs/rest/SetConnection/deleteJob/"+user+"/"+folder);
						log.info("record deleted in Mysql with id" + id);
						
						recordNode.remove();
						jsonObj.put("Status Code", "200");
						jsonObj.put("Status_message", "deleted");
						count=1;
						break;
					}
					else
					{
						count=0;
					}
					log.info("jsonObj : " + jsonObj.toString());
				}
				if (count == 0) {
					jsonObj.put("Status Code", "500");
					jsonObj.put("Status_message", "Invalid id or name");
				}
				hercCommonUtils.sendStatusResponse(response, jsonObj);

				adminsession.save();
			} else {
				log.info("table------------------------------------->item doesn't Exist");
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
		} catch (NodeNotFoundException e) {
			log.error("NodeNotFoundException Exception thrown", e);
		} 
		log.info("Exiting deleteJob method");
	}

	public void insertDataToMySQL(SlingHttpServletResponse response, String tableName, Session adminSession) throws DataSourceNotFoundException, SQLException {
		log.info("Entering into insertDataToMySQL");
		Connection connection = null;
		try {

			ArrayList<String> tableColumns = null;
			ArrayList<String> tableColumnTypes = null;
			connection = dataSourcePoolService.getDatabaseConnection();
			if (null != connection) {
				log.info("Got the connection");
			} else {
				log.info("connection is null");
			}
			log.info("Requested TABLE NAME : " + tableName);
			if (tableName.equalsIgnoreCase("SOLUTION")) {
				tableColumns = hercCommonUtils.getSolutionColumns(connection, tableName);

			} else if (tableName.equalsIgnoreCase("PRODUCT_ITEM_SPECIFICATION")) {
				tableColumns = hercCommonUtils.getPISColumns(connection, tableName);
			} else if (tableName.equalsIgnoreCase("BRANCH_DETAILS")) {
				tableColumns = hercCommonUtils.getBDColumns(connection, tableName);
			}
			if (tableName.equalsIgnoreCase("SOLUTION")) {
				tableColumnTypes = hercCommonUtils.getSolutionColumnTypes(connection, tableName);

			} else if (tableName.equalsIgnoreCase("PRODUCT_ITEM_SPECIFICATION")) {
				tableColumnTypes = hercCommonUtils.getPISColumnTypes(connection, tableName);
			} else if (tableName.equalsIgnoreCase("BRANCH_DETAILS")) {
				tableColumnTypes = hercCommonUtils.getBDColumnTypes(connection, tableName);
			}

			log.info("tableColumns : " + tableColumns.size());
			log.info("tableColumnTypes : " + tableColumnTypes.size());
			int size = tableColumns.size();
			int typessize = tableColumnTypes.size();
			Statement selectStmt = connection.createStatement();
			log.info("creatd statement");
			ResultSet resultSet = selectStmt.executeQuery("SELECT * FROM " + tableName);

			// get the values from Database table.
			int count = 0;
			if (adminSession.itemExists(HercServicesConstants.MY_SQL_TABLES_PATH)) {
				Node mysqltablesNode = adminSession.getNode(HercServicesConstants.MY_SQL_TABLES_PATH);
				while (resultSet.next()) {
					log.info("******* IN resultSet While ************");
					// for (int j = 0; j < typessize; j++) {
					// create Node
					Node hercNode = null;
					if (mysqltablesNode.hasNode(tableName.toLowerCase())) {
						Node tableNameNode = mysqltablesNode.getNode(tableName.toLowerCase());
						// start adding records.
						hercNode = tableNameNode.addNode("record_" + count++, "herc:Record");
					}
					for (int i = 0; i < size; i++) {
						// for (int i = 0; i < size; i++) {
						String type = tableColumnTypes.get(i);
						log.info("COLUMN TYPE = " + type);
						long longrecord = 0;
						String stringrecord = null;
						int index = i;
						if (type.equalsIgnoreCase("int")) {
							index = index + 1;
							log.info(" i  value = " + i);
							log.info("index value = " + index);
							longrecord = resultSet.getInt(index);
							log.info("COLUMN TYPE is integer and value =  " + longrecord);
						} else if (type.equalsIgnoreCase("varchar")) {
							index = index + 1;
							log.info(" i  value = " + i);
							log.info("index value = " + index);

							stringrecord = resultSet.getString(index);
							log.info("COLUMN TYPE is varchar and value " + stringrecord);
						}
						if (null != hercNode) {
							log.info("added hercNode " + hercNode.getPath());
							if (null == stringrecord) {
								hercNode.setProperty(tableColumns.get(i), longrecord);
								log.info("added int property to hercNode " + longrecord);
							} else {
								// stringrecord = stringrecord.replace("^",
								// ",");
								hercNode.setProperty(tableColumns.get(i), stringrecord);
								log.info("added string property to hercNode " + stringrecord);
							}
						}
					}
				}
			}
			adminSession.save();
			JSONObject jobj = new JSONObject();
			jobj.put("Status", "200");
			jobj.put("Status_Message", "Data synced successfully");
			hercCommonUtils.sendStatusResponse(response, jobj);
		} catch (Exception e) {
			log.error("Exception thrown in insertDataToMySQL method", e);
		} finally {
			dataSourcePoolService.closeConnection(connection);
		}
		log.info("Exiting into insertDataToMySQL");
	}

	private int getUserMaxUploadSize(long l) {
		int size = 0;
		if (l != 0) {
			String numbers = String.valueOf(l);
			float MEGABYTE = 1024 * 1024;
			if (numbers.length() >= 7) {
				size = (int) (l / MEGABYTE);
				log.info("size : " + size);
			}
		}
		return size;
	}

	@SuppressWarnings("deprecation")
	public void createBranches(SlingHttpServletResponse response, Session session) throws PathNotFoundException, RepositoryException {
		log.info("************** Entering createFolders method ********************* ");
		String csv_file_path = "/etc/system/readcsv/BRANCHES.csv/jcr:content";
		String BRANCH_DETAILS = "/content/dam/herc/branch_details";
		String COMMA = ",";
		String SLING_ORDEREDFOLDER = "sling:OrderedFolder";
		String IMAGES_PATH = "/content/dam/testdata";
		Node branchDeatailsNode = session.getNode(BRANCH_DETAILS);
		boolean isSave = false;
		BufferedReader br = null;
		int count = 0;
		try {
			Node csvNode = session.getNode(csv_file_path);
			InputStream stream = csvNode.getProperty("jcr:data").getValue().getStream();
			br = new BufferedReader(new InputStreamReader(stream));
			String line = "";
			while ((line = br.readLine()) != null) {
				if (count == 0) {
					log.info("Reading First Line : " + line);
				} else {
					log.info("Processing Row : " + count);
					String[] values = line.split(COMMA);
					String BRANCH_NUMBER = values[0].trim();
					String BRANCH_IMG_NAME = values[2];
					String folderCreated = "";
					log.info("BRANCH_NUMBER : " + BRANCH_NUMBER);
					if (null != branchDeatailsNode) {
						if (!branchDeatailsNode.hasNode(BRANCH_NUMBER)) {
							Node branchNode = branchDeatailsNode.addNode(BRANCH_NUMBER, SLING_ORDEREDFOLDER);
							Node jcr_content_node = branchNode.addNode(JcrConstants.JCR_CONTENT, JcrConstants.NT_UNSTRUCTURED);
							jcr_content_node.setProperty("jcr:title", BRANCH_NUMBER);
							if (session.itemExists(branchNode.getPath())) {
								folderCreated = "yes";
							}
							Node imagesNode = branchNode.addNode("images", SLING_ORDEREDFOLDER);
							Node images_jcr_content = imagesNode.addNode(JcrConstants.JCR_CONTENT, JcrConstants.NT_UNSTRUCTURED);
							images_jcr_content.setProperty("jcr:title", "Images");
							Node videos_node = branchNode.addNode("videos", SLING_ORDEREDFOLDER);
							Node vidoes_jcr_content = videos_node.addNode(JcrConstants.JCR_CONTENT, JcrConstants.NT_UNSTRUCTURED);
							vidoes_jcr_content.setProperty("jcr:title", "Videos");
							Node docs_node = branchNode.addNode("docs", SLING_ORDEREDFOLDER);
							Node docs_jcr_content = docs_node.addNode(JcrConstants.JCR_CONTENT, JcrConstants.NT_UNSTRUCTURED);
							docs_jcr_content.setProperty("jcr:title", "Docs");
							log.info(" BRANCH FOLDER CREATED with NAME : " + branchNode.getName());
							// get the image from your local and check if the
							// image name matches.
							Node imageJcrBINNode = session.getNode(IMAGES_PATH + "/" + BRANCH_IMG_NAME + "/jcr:content/renditions/original/jcr:content");
							InputStream stream2 = imageJcrBINNode.getProperty("jcr:data").getValue().getBinary().getStream();
							Binary imageBinary = session.getValueFactory().createBinary(stream2);
							if (null != imageBinary) {
								log.info("Creating asset started for BRANCH_NUMBER : " + BRANCH_NUMBER);
								Node assetNode = imagesNode.addNode(BRANCH_NUMBER + ".jpg", "dam:Asset");
								Node assetContentNode = assetNode.addNode("jcr:content", "dam:AssetContent");
								assetContentNode.addNode("metadata", JcrConstants.NT_UNSTRUCTURED);
								assetContentNode.addNode("related", JcrConstants.NT_UNSTRUCTURED);
								Node renditions = assetContentNode.addNode("renditions", JcrConstants.NT_FOLDER);
								Node original = renditions.addNode("original", JcrConstants.NT_FILE);
								Node resource = original.addNode("jcr:content", JcrConstants.NT_RESOURCE);
								resource.setProperty("jcr:mimeType", "image/jpeg");
								resource.setProperty(JcrConstants.JCR_DATA, imageBinary);
								String ImageCreated = "";
								if (session.itemExists(resource.getPath())) {
									ImageCreated = "yes";
									isSave = true;
								}
							}

						} else {
							log.info("Branch Numbder  : " + "" + "folder exist");

						}
					}
				}
				count++;
				if (count == 100) {
					if (isSave) {
						session.save();
					}
					log.info("********** saving every 100 nodes creation ************");
				}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (isSave) {
				session.save();
			}
			JSONObject jobj = new JSONObject();
			try {
				jobj.put("Status", "200");
				jobj.put("Status_Message", "Branches creatd successfully");
				hercCommonUtils.sendStatusResponse(response, jobj);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			log.info("total Number of Rows processed : " + count);
		}
		log.info("************** Exiting createFolders method ********************* ");
	}

}
