package com.herc.ewcm.core.impl.schedulers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.commons.datasource.poolservice.DataSourceNotFoundException;
import com.herc.ewcm.core.common.HercCommonUtils;
import com.herc.ewcm.core.common.HercServicesConstants;
import com.herc.ewcm.core.services.DataSourcePoolService;


@Component(metatype = true, label = "SOLR Image Path Update scheduler", 
description = "Simple demo for cron-job like task with properties")
@Service(value = Runnable.class)

@Properties({@Property(name = "scheduler.expression", label ="Image Scheduler", value = "0 00 08 ? * * ",description = "Cron-job expression"),
			@Property(name = "scheduler.concurrent", boolValue=false,description = "Whether or not to schedule this task concurrently")
})
public class ImageProductScheduler implements Runnable{
	
	private static final Logger log = LoggerFactory.getLogger(ImageProductScheduler.class);
	@Reference
	public DataSourcePoolService dataSourcePoolService;
	@Reference
	public HercCommonUtils hercCommonUtils;
	
	@Reference
	public SlingRepository reSlingRepository;
	Session adminSession;
	Connection connection=null;
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		log.info("Entering Scheduler's run method");
			try {
				adminSession = reSlingRepository.loginAdministrative(null);
				connection=dataSourcePoolService.getDatabaseConnection();
				if(connection!=null)
				{
					Statement statement=connection.createStatement();
					ResultSet rs=statement.executeQuery(HercServicesConstants.SCHEDULER_QUERY_SELECT);
					while(rs.next())
					{
						String category=rs.getString(1);
						String subCategory=rs.getString(2);
						String equipment=rs.getString(3);
						String id=rs.getString(4);
						
						String aemImagePath=HercServicesConstants.SCHEDULER_DAM_PATH;
						String subcatImagePath="";
						category=hercCommonUtils.getUnderScroreName(category);
						subCategory=hercCommonUtils.getUnderScroreName(subCategory);
						equipment=hercCommonUtils.getUnderScroreName(equipment);
						if(subCategory==null || subCategory.equals("") ||subCategory.equals(" "))
						{
							aemImagePath=aemImagePath+category+HercServicesConstants.SLASH+equipment+HercServicesConstants.DAM_PATH_IMAGES+equipment+HercServicesConstants.SCHEDULER_DAM_PATH_EXTENSION;
							log.info("AEM image path "+aemImagePath);
						}
						else
						{
							subcatImagePath=aemImagePath+category+HercServicesConstants.SLASH+subCategory+HercServicesConstants.DAM_PATH_IMAGES+subCategory+HercServicesConstants.SCHEDULER_DAM_PATH_EXTENSION;
							aemImagePath=aemImagePath+category+HercServicesConstants.SLASH+subCategory+HercServicesConstants.SLASH+equipment+HercServicesConstants.DAM_PATH_IMAGES+equipment+HercServicesConstants.SCHEDULER_DAM_PATH_EXTENSION;
							log.info("AEM image path "+aemImagePath);
						}
						if(adminSession.itemExists(aemImagePath) )
						{
							PreparedStatement preparedStatement=connection.prepareStatement(HercServicesConstants.SCHEDULER_QUERY_UPDATE+
									aemImagePath+HercServicesConstants.SCHEDULER_QUERY_UPDATE1+id+HercServicesConstants.SCHEDULER_QUERY_UPDATE2);
							log.info("Query_>>>>>"+HercServicesConstants.SCHEDULER_QUERY_UPDATE+
									aemImagePath+HercServicesConstants.SCHEDULER_QUERY_UPDATE1+id+HercServicesConstants.SCHEDULER_QUERY_UPDATE2);
							preparedStatement.execute();
							log.info("Imagepath uplodaed successfully");
						}
						else
						{
							PreparedStatement preparedStatement=connection.prepareStatement(HercServicesConstants.SCHEDULER_QUERY_UPDATE_DEFAULT+id+HercServicesConstants.SCHEDULER_QUERY_UPDATE2);
							log.info("Query_>>>>>"+HercServicesConstants.SCHEDULER_QUERY_UPDATE_DEFAULT+id+HercServicesConstants.SCHEDULER_QUERY_UPDATE2);
							preparedStatement.execute();
							log.info("default Image path uplodaed successfully");
						}						
						log.info("subcat image path uplodaed successfully subCategory:="+subcatImagePath);
						if(subcatImagePath.length()>0)
						{
							if( adminSession.itemExists(subcatImagePath)){

							PreparedStatement preparedStatement=connection.prepareStatement(HercServicesConstants.SCHEDULER_SUBCAT_QUERY_UPDATE+subcatImagePath
									+HercServicesConstants.SCHEDULER_QUERY_UPDATE1+id+HercServicesConstants.SCHEDULER_QUERY_UPDATE2);
							log.info("Query_>>>>>"+HercServicesConstants.SCHEDULER_SUBCAT_QUERY_UPDATE+subcatImagePath
									+HercServicesConstants.SCHEDULER_QUERY_UPDATE1+id+HercServicesConstants.SCHEDULER_QUERY_UPDATE2);
							preparedStatement.execute();
							log.info("subcat image path uplodaed successfully");
							}else{
							PreparedStatement preparedStatement=connection.prepareStatement(HercServicesConstants.SCHEDULER_SUBCAT_QUERY_UPDATE_DEFAULT+id+HercServicesConstants.SCHEDULER_QUERY_UPDATE2);
							log.info("Query_>>>>>"+HercServicesConstants.SCHEDULER_SUBCAT_QUERY_UPDATE_DEFAULT+id+HercServicesConstants.SCHEDULER_QUERY_UPDATE2);
							preparedStatement.execute();
							log.info("subcat default Image path uplodaed successfully");
						}
						
					}else
					{
						log.info("Subcategory does not exists for this Path");
					}
				}
				}	
			} catch (LoginException e) {
				log.error("LoginException Exception Caught",e);
			} catch (RepositoryException e) {
				log.error("RepositoryException Exception Caught",e);
				} catch (DataSourceNotFoundException e) {
					log.error("DataSourceNotFoundException Exception Caught",e);
			} catch (SQLException e) {
				log.error("SQLException Exception Caught",e);
			}
			finally
			{
				try {
					connection.close();
				} catch (SQLException e) {
					log.error("SQLException Exception Caught",e);
				}
			}
			log.info("Exiting  scheduler method");
	}
}
