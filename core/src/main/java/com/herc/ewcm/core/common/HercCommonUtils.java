/**
 * 
 */
package com.herc.ewcm.core.common;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.herc.ewcm.core.services.DataSourcePoolService;

/**
 * @author VE332669
 *
 */
@Component(name = "com.herc.ewcm.core.common.HercCommonUtils", label = "HercCommonUtils", description = "Herc common utils", specVersion = "1.1", immediate = true, metatype = true)
@Service(value = { HercCommonUtils.class })
public class HercCommonUtils {

	public static ArrayList<String> SOLUTION_AL = new ArrayList<String>();
	public static ArrayList<String> PIS_AL = new ArrayList<String>();
	public static ArrayList<String> BD_AL = new ArrayList<String>();
	public static ArrayList<String> SOLUTIONTYPES_AL = new ArrayList<String>();
	public static ArrayList<String> PISTYPES_AL = new ArrayList<String>();
	public static ArrayList<String> BDTYPES_AL = new ArrayList<String>();
	@Reference
	private DataSourcePoolService dataSourcePoolService;

	private static final Logger log = LoggerFactory
			.getLogger(HercCommonUtils.class);

	public void sendStatusResponse(SlingHttpServletResponse response,
			JSONObject jsonObj) throws JSONException, IOException {
		long startTime = System.currentTimeMillis();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		jsonObj.write(response.getWriter());
		long endTime = System.currentTimeMillis();
		log.info("Time taken in the sendStatusResponse() method : "
				+ (endTime - startTime) / 1000 + " Secondss");

	}

	public String getUnderScroreName(String name) {
		if (name.contains(HercServicesConstants.COMMA)) {
			name = name.replace(HercServicesConstants.COMMA,
					HercServicesConstants.UNDERSCORE);
		}
		if (name.contains(HercServicesConstants.SPACE)) {
			name = name.replace(HercServicesConstants.SPACE,
					HercServicesConstants.UNDERSCORE);
		}
		if (name.contains(HercServicesConstants.UNDERSCORE_AND_UDERSCORE)) {
			name = name.replace(HercServicesConstants.UNDERSCORE_AND_UDERSCORE,
					HercServicesConstants.UNDERSCORE);
		}
		if (name.contains(HercServicesConstants.UNDERSCORE_UDERSCORE)) {
			log.info("name ::::::::::::::::::::::::::::::bfrore" + name);

			name = name.replace(HercServicesConstants.UNDERSCORE_UDERSCORE,
					HercServicesConstants.UNDERSCORE);
			log.info("name ::::::::::::::::::::::::::::::" + name);
		}
		if (name.contains(HercServicesConstants.DOUBLE_UNDERSCORE)) {
			name = name.replace(HercServicesConstants.DOUBLE_UNDERSCORE,
					HercServicesConstants.UNDERSCORE);
		}
		if (name.contains(HercServicesConstants.SPACE_AND_SPACE)) {
			name = name.replace(HercServicesConstants.SPACE_AND_SPACE,
					HercServicesConstants.UNDERSCORE);
		}
		name = name.toLowerCase();
		return name;
	}

	public String getFinalName(String name) {

		if (name.contains(HercServicesConstants.UNDERSCORE)) {
			name = name.replace(HercServicesConstants.UNDERSCORE,
					HercServicesConstants.SPACE);
		}
		if (name.contains(HercServicesConstants.UNDERSCORE_UDERSCORE)) {
			name = name.replace(HercServicesConstants.UNDERSCORE_UDERSCORE,
					HercServicesConstants.UNDERSCORE_AND_UDERSCORE);
		}
		if (name.contains(HercServicesConstants.UNDERSCORE_AND_UDERSCORE)) {
			name = name.replace(HercServicesConstants.UNDERSCORE_AND_UDERSCORE,
					HercServicesConstants.SPACE_AND_SPACE);
		}
		if (name.contains(HercServicesConstants.DOUBLE_UNDERSCORE)) {
			name = name.replace(HercServicesConstants.DOUBLE_UNDERSCORE,
					HercServicesConstants.COMMA + HercServicesConstants.SPACE);
		}
		name = name.toLowerCase();
		return name;
	}

	public ArrayList<String> getSolutionColumns(Connection connection,
			String tableName) throws SQLException {
		SOLUTION_AL = new ArrayList<String>();
		Statement stmt = connection.createStatement();
		ResultSet rsColumns = null;
		DatabaseMetaData meta = connection.getMetaData();
		rsColumns = meta.getColumns(null, null, tableName, null);
		while (rsColumns.next()) {
			SOLUTION_AL.add(rsColumns.getString("COLUMN_NAME"));
		}
		return SOLUTION_AL;

	}

	public ArrayList<String> getPISColumns(Connection connection,
			String tableName) throws SQLException {
		PIS_AL = new ArrayList<String>();
		Statement stmt = connection.createStatement();
		ResultSet rsColumns = null;
		DatabaseMetaData meta = connection.getMetaData();
		rsColumns = meta.getColumns(null, null, tableName, null);
		while (rsColumns.next()) {
			PIS_AL.add(rsColumns.getString("COLUMN_NAME"));
		}
		return PIS_AL;

	}

	public ArrayList<String> getBDColumns(Connection connection,
			String tableName) throws SQLException {
		BD_AL = new ArrayList<String>();
		Statement stmt = connection.createStatement();
		ResultSet rsColumns = null;
		DatabaseMetaData meta = connection.getMetaData();
		rsColumns = meta.getColumns(null, null, tableName, null);
		while (rsColumns.next()) {
			BD_AL.add(rsColumns.getString("COLUMN_NAME"));
		}
		return BD_AL;

	}

	public ArrayList<String> getSolutionColumnTypes(Connection connection,
			String tableName) throws SQLException {
		SOLUTIONTYPES_AL = new ArrayList<String>();
		Statement stmt = connection.createStatement();
		ResultSet rsColumns = null;
		DatabaseMetaData meta = connection.getMetaData();
		rsColumns = meta.getColumns(null, null, tableName, null);
		while (rsColumns.next()) {
			SOLUTIONTYPES_AL.add(rsColumns.getString("TYPE_NAME"));
		}
		return SOLUTIONTYPES_AL;

	}

	public ArrayList<String> getPISColumnTypes(Connection connection,
			String tableName) throws SQLException {
		PISTYPES_AL = new ArrayList<String>();
		Statement stmt = connection.createStatement();
		ResultSet rsColumns = null;
		DatabaseMetaData meta = connection.getMetaData();
		rsColumns = meta.getColumns(null, null, tableName, null);
		while (rsColumns.next()) {
			PISTYPES_AL.add(rsColumns.getString("TYPE_NAME"));
		}
		return PISTYPES_AL;

	}

	public ArrayList<String> getBDColumnTypes(Connection connection,
			String tableName) throws SQLException {
		BDTYPES_AL = new ArrayList<String>();
		Statement stmt = connection.createStatement();
		ResultSet rsColumns = null;
		DatabaseMetaData meta = connection.getMetaData();
		rsColumns = meta.getColumns(null, null, tableName, null);
		while (rsColumns.next()) {
			BDTYPES_AL.add(rsColumns.getString("TYPE_NAME"));
		}
		return BDTYPES_AL;

	}

	public ArrayList<String> getColumnName(Connection connection,
			String tableName) throws SQLException {
		BDTYPES_AL = new ArrayList<String>();
		Statement stmt = connection.createStatement();
		ResultSet rsColumns = null;
		DatabaseMetaData meta = connection.getMetaData();
		rsColumns = meta.getColumns(null, null, tableName, null);
		while (rsColumns.next()) {
			BDTYPES_AL.add(rsColumns.getString("COLUMN_NAME"));
		}
		return BDTYPES_AL;

	}

	public ArrayList<String> getColumnType(Connection connection,
			String tableName) throws SQLException {
		BDTYPES_AL = new ArrayList<String>();
		Statement stmt = connection.createStatement();
		ResultSet rsColumns = null;
		DatabaseMetaData meta = connection.getMetaData();
		rsColumns = meta.getColumns(null, null, tableName, null);
		while (rsColumns.next()) {
			BDTYPES_AL.add(rsColumns.getString("TYPE_NAME"));
		}
		return BDTYPES_AL;

	}

	public String getAutoGenetratedNumber(String userid) {
		String id = dataSourcePoolService
				.tomcatConnection("/MysqlAPIs/rest/SetConnection/getAutoGeneratedNumber/"
						+ userid);
		return id;
	}
}
