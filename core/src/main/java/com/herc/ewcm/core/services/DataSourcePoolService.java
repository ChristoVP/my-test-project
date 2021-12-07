package com.herc.ewcm.core.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.commons.datasource.poolservice.DataSourceNotFoundException;
import com.day.commons.datasource.poolservice.DataSourcePool;

/**
 * This class is used to get the DataSourcePool service to obtain a
 * javax.sql.DataSource object for the My SQL Database configuraion.The
 * DataSourcePool service provides the getDataSource method that returns a
 * DataSource object for a given data source name.
 * 
 * @author
 * 
 */

@Component(name = "com.herc.poc.catalogsdisplay.core.services.DataSourcePoolService", specVersion = "1.1", metatype = true, immediate = true)
@Service(value = { DataSourcePoolService.class })
@Properties({
		@Property(name = "datasource.name", description = "JDBC Data Source Name", label = "Please enter the JDBC datasource name for the connection", value = "hercmysql"),
		@Property(name = "db2.datasource.name", description = "DB2 Data Source Name", label = "Please enter the JDBC DB2 datasource name for the connection", value = "hercdb2"),
		@Property(name = "dbhost", description = "Databse host", label = "Please enter the DB host", value = "10.219.244.111"),
		@Property(name = "dbport", description = "Databse port", label = "Please enter the DB port", value = "8080") })
public class DataSourcePoolService {

	private static Logger log = LoggerFactory
			.getLogger(DataSourcePoolService.class);
	@Reference
	private DataSourcePool dataSourcePool;

	String strDataSourceName = null;
	private String DB2DataSource = null;
	private String dbHost = null;
	private String dbPort = null;

	/**
	 * This methd get the DataSource from My SQL Database connection pool
	 * service and returns the connection.
	 * 
	 * @return
	 * @throws DataSourceNotFoundException
	 * @throws SQLException
	 */
	public Connection getDatabaseConnection()
			throws DataSourceNotFoundException, SQLException {
		log.debug("Entering getDatabaseConnection method");

		Connection connection = null;

		if (null != strDataSourceName && strDataSourceName.trim().length() > 0)
			try {
				DataSource dataSource = (DataSource) dataSourcePool
						.getDataSource(strDataSourceName);
				log.debug("dataSource :" + dataSource);

				if (null != dataSource) {
					log.debug("Got the DataSource");
					connection = dataSource.getConnection();
				}
			} catch (Exception e) {
				log.error("Error while getting dataSource ", e);
			}

		log.debug("Exiting getDatabaseConnection method");

		return connection;
	}

	/**
	 * This method close the oracle Database connection after completing the DB
	 * operations.
	 * 
	 * @param connection
	 * @throws SQLException
	 */
	public void closeConnection(Connection connection) throws SQLException {
		log.debug("Entering CloseConnection method");
		if (null != connection) {
			connection.close();
		}
		log.debug("Exiting CloseConnection method");
	}

	public String tomcatConnection(String URL) {
		DefaultHttpClient httpclient = new DefaultHttpClient();

		StringBuffer content = new StringBuffer();
		try {
			// specify the host, protocol, and port

			HttpHost target = new HttpHost(dbHost, Integer.parseInt(dbPort),
					"http");

			// HttpHost target = new HttpHost("10.219.225.106", 8080, "http");
			// HttpHost target = new HttpHost("10.219.194.31", 8080, "http");
			// HttpHost target = new HttpHost("10.219.232.202", 8080, "http");
			// HttpHost target = new HttpHost("localhost", 8085, "http");
			HttpGet getRequest = new HttpGet(URL);
			log.info("executing request to ****  " + target + URL);
			BufferedReader rd = null;
			HttpResponse httpResponse = httpclient.execute(target, getRequest);

			rd = new BufferedReader(new InputStreamReader(httpResponse
					.getEntity().getContent()));

			String line = "";
			while ((line = rd.readLine()) != null) {

				content.append(line);
			}
			log.info(content.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			httpclient.getConnectionManager().shutdown();

		}
		return content.toString();
	}

	/**
	 * To get DB2 connection
	 * 
	 * @return
	 * @throws DataSourceNotFoundException
	 * @throws SQLException
	 */
	public Connection getDB2DatabaseConnection()
			throws DataSourceNotFoundException, SQLException {
		log.debug("Entering getDB2DatabaseConnection method");

		Connection connection = null;

		if (null != DB2DataSource && DB2DataSource.trim().length() > 0)
			try {
				DataSource dataSource = (DataSource) dataSourcePool
						.getDataSource(DB2DataSource);
				log.debug("dataSource :" + dataSource);

				if (null != dataSource) {
					log.debug("Got the DB2 DataSource");
					connection = dataSource.getConnection();
				}
			} catch (Exception e) {
				log.error("Error while getting DB2 DataSource ", e);
			}

		log.debug("Exiting getDB2DatabaseConnection method");

		return connection;
	}

	public void closeDB2Connection(Connection connection) throws SQLException {
		log.debug("Entering CloseConnection method");
		if (null != connection) {
			connection.close();
		}
		log.debug("Exiting CloseConnection method");
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
		strDataSourceName = (String) context.getProperties().get(
				"datasource.name");
		DB2DataSource = (String) context.getProperties().get(
				"db2.datasource.name");

		dbHost = (String) context.getProperties().get("dbhost");
		dbPort = (String) context.getProperties().get("dbport");
		log.debug("strDataSourceName : " + strDataSourceName);
		log.debug("DB2DataSource : " + DB2DataSource);

		log.debug("dbHost : " + dbHost);
		log.debug("dbPort : " + dbPort);
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
