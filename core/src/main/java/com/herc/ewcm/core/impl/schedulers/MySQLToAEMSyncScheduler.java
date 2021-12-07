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
package com.herc.ewcm.core.impl.schedulers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.PropertyIterator;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.commons.datasource.poolservice.DataSourceNotFoundException;
import com.herc.ewcm.core.common.HercCommonUtils;
import com.herc.ewcm.core.common.HercServicesConstants;
import com.herc.ewcm.core.services.DataSourcePoolService;
import com.herc.ewcm.core.services.HercJobSiteService;
//import com.mysql.jdbc.PreparedStatement.ParseInfo;



/**
 * A simple demo for cron-job like tasks that get executed regularly.
 * It also demonstrates how property values can be set. Users can
 * set the property values in /system/console/configMgr
 */
/**
 * @author YA316484
 *
 */
@Component(metatype = true, label = "Herc MYSQL-AEM Sync Scheduler", 
description = "Herc data sync scheduler to sync data from mysql to aem")
@Service(value = Runnable.class)
@Properties({
    @Property(name = "scheduler.expression", label = "Scheduled Time", value = "0 00 08 ? * * ",description = "Cron-job expression"),
  @Property(name = "scheduler.concurrent", boolValue=false,
      description = "Whether or not to schedule this task concurrently")
})

public class MySQLToAEMSyncScheduler implements Runnable {
	@Reference
	public DataSourcePoolService dataSourcePoolService;
	@Reference
	public HercJobSiteService  hercJobSiteService;
	@Reference
	public SlingRepository reSlingRepository;
	private static final TimeUnit SECONDS = null;

	private final ScheduledExecutorService scheduler = 
			Executors.newScheduledThreadPool(1);


	
	private static final Logger log = LoggerFactory.getLogger(MySQLToAEMSyncScheduler.class);
	
	@Reference
	private HercCommonUtils hercCommonUtils;
	Session adminSession;
	String schema = "DEV_HERC_MYSQL_DB";
	/**
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//private SlingRepository repository=null;
		//repository=null;
		log.info("inside run method");
		Connection connection=null;
		try
		{
			adminSession = reSlingRepository.loginAdministrative(null);
			String[] types = { "TABLE" };
			connection= dataSourcePoolService.getDatabaseConnection();
			ResultSet rs = connection.getMetaData().getTables(schema, null, "%", types);
			while(rs.next())
			{
				log.info("inside While method"+rs.getString(3));
				insertion(connection,rs.getString(3));				
				modification(connection, rs.getString(3));
			}
		}
		catch(SQLException e)
		{
			log.error("SQLException caught", e);
		} catch (LoginException e) {
			log.error("LoginException caught", e);
		} catch (RepositoryException e) {
			log.error("RepositoryException caught", e);
		} catch (DataSourceNotFoundException e) {
			log.error("DataSourceNotFoundException caught", e);
		}
		finally
		{
			try {
				dataSourcePoolService.closeConnection(connection);
			} catch (SQLException e) {
				log.error("SQLException caught", e);
			}
		}




	}

	public void insertion(Connection con,String tableName)
	{
		log.info("Inside INsertion table which change");		

		ArrayList<String> Col_name=new ArrayList<String>();
		ArrayList<String> Col_type=new ArrayList<String>();
		
		int tempfolderCount=1;
		int folderCount=1;
		Node folderNode = null;
		
		try {			
			//tableName = "PRODUCT_ITEM_SPECIFICATION";
			String selectQuery=HercServicesConstants.QUERY_SCHEDULER_SELECT+schema+HercServicesConstants.DOT+tableName;
			PreparedStatement preparedStatement=con.prepareStatement(selectQuery);
			ResultSet resultSet=preparedStatement.executeQuery();
			String aemFolderName=hercCommonUtils.getUnderScroreName(tableName);
			aemFolderName=HercServicesConstants.MY_SQL_TABLES_PATH+"/"+aemFolderName;
			
			//aemFolderName="/etc/system/mysqltables1/"+aemFolderName;
			String tempFolderName=aemFolderName;
			Node tableNodeName=adminSession.getNode(tempFolderName);
			
			
			if(!tableNodeName.hasNodes())
			{
				folderNode=tableNodeName.addNode(folderCount+"", "sling:Folder");
				aemFolderName=folderNode.getPath();
			}
			else
			{
				NodeIterator recordIterator=tableNodeName.getNodes();
				while(recordIterator.hasNext())
				{
					Node temp=recordIterator.nextNode();
					NodeIterator recordCount=temp.getNodes();
					if(recordCount.getSize() < HercServicesConstants.SIZE)
					{
						folderNode=adminSession.getNode(temp.getPath());
						folderCount=Integer.parseInt(folderNode.getName());
						log.info("foldernode path for single addition"+folderNode.getPath());
						break;
					}
				}
			}
			while(resultSet.next())
			{
				log.info("Inside while ");				
				String folderName=resultSet.getString(HercServicesConstants.FOLDER);
				log.info("Folder Name in mysql  "+folderName);
				
				log.info("Folder Name in AEM"+aemFolderName);
				if(tempfolderCount > HercServicesConstants.SIZE)
				{
				
					NodeIterator tableNodeIter=tableNodeName.getNodes();
					folderCount=(int) tableNodeIter.getSize()+1;
					folderNode=tableNodeName.addNode((folderCount)+"", "sling:Folder");
					aemFolderName=folderNode.getPath();					
					tempfolderCount=1;
				}
				
				if(folderName.length() < 1 || folderName.equalsIgnoreCase(HercServicesConstants.NULL_VALUE))
				{
					Col_name=hercCommonUtils.getColumnName(con, tableName);
					Col_type=hercCommonUtils.getColumnType(con, tableName);
					
					if(adminSession.itemExists(aemFolderName))
					{
						log.info("Inside if item exists");						
						tempfolderCount++;	
						log.info("temp folder coutn  "+tempfolderCount);
						log.info("folder node path "+folderNode.getPath());
						Node hercNode=folderNode.addNode("record_" + resultSet.getString(HercServicesConstants.ID), "herc:Record");
						Statement s=con.createStatement();
						log.info("Update Query  "+HercServicesConstants.QUERY_SCHEDULER_UPDATE+schema+HercServicesConstants.DOT+tableName+HercServicesConstants.QUERY_SCHEDULER_SET+folderCount +HercServicesConstants.QUERY_SCHEDULER_WHERE+resultSet.getString(HercServicesConstants.ID)+HercServicesConstants.QUERY_SCHEDULER_END);
						s.executeUpdate(HercServicesConstants.QUERY_SCHEDULER_UPDATE+schema+HercServicesConstants.DOT+tableName+HercServicesConstants.QUERY_SCHEDULER_SET+folderCount +HercServicesConstants.QUERY_SCHEDULER_WHERE+resultSet.getString(HercServicesConstants.ID)+HercServicesConstants.QUERY_SCHEDULER_END);
						log.info("folder count"+folderCount);
						
						for (int i = 0; i < Col_name.size(); i++) {
							
							String type = Col_type.get(i);
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
									hercNode.setProperty(Col_name.get(i), longrecord);
									log.info("added int property to hercNode " + longrecord);
								} else {
									
									hercNode.setProperty(Col_name.get(i), stringrecord);
									if(Col_name.get(i).equals(HercServicesConstants.PRODUCT_ITEM_COLUMN_SPEC_1))
									{
										double spec1value=evaluate(stringrecord);
										
										hercNode.setProperty("SPEC1_VALUE", spec1value);
										log.info("SPEC1_VALUE property added "+spec1value);
									}
									if(Col_name.get(i).equals("CN_SPEC_1"))
									{
										double spec1value=evaluate(stringrecord);
										
										hercNode.setProperty("SPEC1_VALUE_CN", spec1value);
										log.info("SPEC1_VALUE property added "+spec1value);
									}
									log.info("added string property to hercNode " + stringrecord);
								}
							}
						}


					}

				}
				

				
			}


		} catch (SQLException e) {
			log.error("SQLException Exception Caught",e);
		} catch (PathNotFoundException e) {
			log.error("PathNotFoundException Exception Caught",e);
		} catch (RepositoryException e) {
			log.error("RepositoryException Exception Caught",e);
		}
		finally
		{
			try {
				adminSession.save();
			} catch (RepositoryException e) {
				log.error("RepositoryException Exception Caught",e);
			}
		}
		

	}
	
	public void modification(Connection connection,String tableName)
	{

		//tableName = "PRODUCT_ITEM_SPECIFICATION";
		String aemFolderName=hercCommonUtils.getUnderScroreName(tableName);
		aemFolderName=HercServicesConstants.MY_SQL_TABLES_PATH+aemFolderName;
		//aemFolderName="/etc/system/mysqltables1/"+aemFolderName;
		log.info("Tablenode path"+aemFolderName);
		try {
			if(adminSession.itemExists(aemFolderName))
			{
				Node tableNode=adminSession.getNode(aemFolderName);
				log.info("Foldernode path"+tableNode.getPath()+"Child nodes"+tableNode.getDepth());
				NodeIterator folderIterator=tableNode.getNodes();
				while(folderIterator.hasNext())
				{
					Node folderNode=folderIterator.nextNode();
					log.info("Record Node  path"+folderNode.getPath() +"Child nodes"+folderNode.getDepth());
					NodeIterator recordIterator=folderNode.getNodes();
					while(recordIterator.hasNext())
					{
						Node record_Node=recordIterator.nextNode();
							
						Long id=record_Node.getProperty("ID").getValue().getLong();
						//tableName = "PRODUCT_ITEM_SPECIFICATION_TEMP";
						String selectQuery=HercServicesConstants.QUERY_SCHEDULER_SELECT+schema+HercServicesConstants.DOT+tableName+HercServicesConstants.QUERY_SCHEDULER_SELECT_WHERE+id+HercServicesConstants.QUERY_SCHEDULER_END;
						log.info(selectQuery);
						PreparedStatement preparedStatement=connection.prepareStatement(selectQuery);
						ResultSet resultSet=preparedStatement.executeQuery();
						if(resultSet.next())
						{
							PropertyIterator propIterator = record_Node.getProperties();
							while(propIterator.hasNext())
							{
								javax.jcr.Property record_property=propIterator.nextProperty();
								String prop_name=record_property.getName();	
								
								log.info("AEM RECORD ID value  "+id+"Property Name  ->"+prop_name);

								if(!(prop_name.equalsIgnoreCase("jcr:primaryType")))
								{
									if(!(prop_name.equalsIgnoreCase("SPEC1_VALUE") || prop_name.equalsIgnoreCase("SPEC1_VALUE_CN")))
									{
										if(record_Node.getProperty(record_property.getName()).getValue().getString().toString().equals(resultSet.getString(record_property.getName()).toString()))
										{
											log.info("Item not Updated");		
										}
										else
										{

											record_Node.setProperty(prop_name, resultSet.getString(prop_name));
											adminSession.save();
											log.info("Record updated "+prop_name+" with "+ resultSet.getString(prop_name));	
										}
									}
								}


							}

						}	
						else
						{
							record_Node.remove();
							adminSession.save();
							log.info("Item  deleted at"+id);
						}

					}
				}
			}



		} catch (RepositoryException e) {
			log.error("RepositoryException Exception Caught",e);
		} catch (SQLException e) {
			log.error("SQLException Exception Caught",e);
		}
//		finally
//		{
//			try {
//				adminSession.save();
//			} catch (RepositoryException e) {
//				log.error("RepositoryException Exception Caught",e);
//			}
//		}


	}
	
	private static double fractionValue(String src) {
		double result = 0;
		String fraction = src;
		
		String[] whole = src.split("[ -]", 2);
		if (whole.length > 1) {
			result = Double.parseDouble(whole[0]);
			fraction = whole[1];
		}
		String[] parts = fraction.split("/");
		double numerator = Double.parseDouble(parts[0]);
		double denominator = Double.parseDouble(parts[1]);
		result += numerator / denominator;
		return result;
	}
	
	private static double decimalValue(String src) {
		return Double.parseDouble(src);
	}
	
	public  static double evaluate(final String src){
		try
		{
			String matchDecimal = "-?(\\d+(\\.\\d*)?|\\.\\d+)(?!/)";
			String matchFraction = "-?((\\d+)[ \\-])?(\\d+)/(\\d+)";
			String matchNumberOrRange = 
					"((?<f1>" + matchFraction + ")|(?<d1>" + matchDecimal + "))" +
							"(\\s*-\\s*((?<f2>" + matchFraction + ")|(?<d2>" + matchDecimal +")))?";
			Pattern p = Pattern.compile(matchNumberOrRange);
			String cleanSrc = src.replace(",",""); // Remove thousands separators
			Matcher m = p.matcher(cleanSrc);
			if (m.find()){
				if (m.group("f2") != null) {
					return fractionValue(m.group("f2"));
				}
				if (m.group("d2") != null) {
					return decimalValue(m.group("d2"));
				}
				if (m.group("f1") != null) {
					return fractionValue(m.group("f1"));
				}
				if (m.group("d1") != null) {
					return decimalValue(m.group("d1"));
				}
			}
		}
		catch(NumberFormatException e)
		{
			return 0.0;
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			return 0.0;
		}
		return 0.0;
	}
	/**
	 * Default activate method called when bundle deactivated.
	 * 
	 * @param componentContext
	 * @throws Exception
	 */
	protected void activate(ComponentContext componentContext) throws Exception {
		log.info("activate method called");
		
	}
	/**
	 * Default deactivate method called when bundle deactivated.
	 * 
	 * @param componentContext
	 * @throws Exception
	 */
	protected void deactivate(ComponentContext componentContext) throws Exception {
		log.info("deactivate method called");
		
	}
}










