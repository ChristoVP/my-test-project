package com.herc.ewcm.core.impl.servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Timer;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.commons.datasource.poolservice.DataSourceNotFoundException;
import com.herc.ewcm.core.common.HercCommonUtils;
import com.herc.ewcm.core.common.HercServicesConstants;
import com.herc.ewcm.core.services.HercBrowseCatalogService;
import com.herc.ewcm.core.services.HercEtrieveService;
import com.herc.ewcm.core.services.HercJobSiteService;
import com.herc.ewcm.core.services.HercLocationsService;
import com.herc.ewcm.core.services.HercLoyaltyService;
import com.herc.ewcm.core.services.HercMasterProductListService;
import com.herc.ewcm.core.services.HercSolutionService;

@Service
@SlingServlet(paths = "/c/aem/servlets/services/herc", methods = "POST")
public class HercServicesServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory
			.getLogger(HercServicesServlet.class);

	@Reference
	private SlingRepository repository;
	@Reference
	private HercJobSiteService hercJobSiteService;
	@Reference
	private HercSolutionService hercSolutionService;
	@Reference
	private HercLocationsService hercLocationsService;
	@Reference
	private HercBrowseCatalogService hercBrowseCatalogService;
	@Reference
	private HercMasterProductListService hercMasterProductListService;
	@Reference
	private HercLoyaltyService hercLoyaltyService;
	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	@Reference
	private HercEtrieveService hercEtrieveService;

	@Reference
	private HercCommonUtils hercCommonUtils;

	/**
	 * This method handle all get requests.
	 * 
	 */
	public void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		log.info("Entering doGet method()");
		doPost(request, response);
		log.info("Exiting doGet method()");
	}

	public void doPost(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		log.info("Entering doPost method.............");

		long startTime = System.currentTimeMillis();
		Session adminSession = null;
		RequestPathInfo requestPathInfo = request.getRequestPathInfo();
		String resourcePath = requestPathInfo.getResourcePath();
		String selector = requestPathInfo.getSelectorString();
		String extension = requestPathInfo.getExtension();
		String suffix = requestPathInfo.getSuffix();
		String zipcode = null;
		String cat[] = null;
		String categories = null;

		log.info("resourcePath : " + resourcePath);
		log.info("selector : " + selector);
		log.info("extension : " + extension);
		log.info("suffix : " + suffix);
		Timer timer = new Timer();
		if (null != repository) {
			try {
				// adminSession =
				// repository.loginService("authentication-handler", null);
				adminSession = repository.loginAdministrative(null);
				ResourceResolver administrativeResourceResolver;
				try {
					administrativeResourceResolver = resourceResolverFactory
							.getAdministrativeResourceResolver(null);
					Session whichSession = administrativeResourceResolver
							.adaptTo(Session.class);
					log.info("********* whichSession ************ "
							+ whichSession);
				} catch (org.apache.sling.api.resource.LoginException e1) {
					log.error("LoginException thrown while geting adminSession");
				}
				if (adminSession
						.itemExists(HercServicesConstants.MY_SQL_TABLES_PATH)) {
					Node mySqlTablesNode = adminSession
							.getNode(HercServicesConstants.MY_SQL_TABLES_PATH);
					// Christo
					if (selector.equalsIgnoreCase("insertDataToMySQL")) {
						log.info("Requested insertDataToMySQL API");
						String tableName = request.getParameter("TableName");
						try {
							hercJobSiteService.insertDataToMySQL(response,
									tableName, adminSession);
						} catch (DataSourceNotFoundException e) {
							e.printStackTrace();
						} catch (SQLException e) {

							e.printStackTrace();
						}
					}

					if (selector.equalsIgnoreCase("getDistance")) {
						log.info("Requested getDistance API ");
						hercLoyaltyService.getDistance(response);
					}
					if (selector.equalsIgnoreCase("getSolutions")) {
						log.info("Requested getSolutions API ");
						log.info("client ip" + request.getRemoteAddr());
						String countrycode = request
								.getParameter(HercServicesConstants.PARAMETER_COUNTRY_CODE);
						String deviceType = request
								.getParameter(HercServicesConstants.DEVICE_TYPE);

						hercSolutionService
								.getSolutions(mySqlTablesNode, response,
										countrycode, deviceType, adminSession);
					}
					// Christo
					if (selector.equalsIgnoreCase("getSolutionDetails")) {
						log.info("Requested getSolutionDetails API ");
						String id = request.getParameter("solution_id");
						String name = request
								.getParameter(HercServicesConstants.PARAMETER_SOLUTION_NAME);
						String deviceType = request
								.getParameter(HercServicesConstants.DEVICE_TYPE);
						JSONObject finalobj = hercSolutionService
								.getSolutionDetails(mySqlTablesNode, response,
										name, deviceType, adminSession);

						hercCommonUtils.sendStatusResponse(response, finalobj);

					}

					// sridhar
					if (selector.equalsIgnoreCase("createJobsiteMobile")) {
						log.info("Requested createJobsiteMobile API call.");
						RequestParameter file = request
								.getRequestParameter("content");
						String user = request
								.getParameter(HercServicesConstants.JOBSITE_USERNAME);
						String folder = request
								.getParameter(HercServicesConstants.JOBSITE_FOLDERNAME);
						String newFolder = request
								.getParameter(HercServicesConstants.JOBSITE_NEW_FOLDERNAME);
						String cmd = request
								.getParameter(HercServicesConstants.JOBSITE_ACTION);
						String filePath = request
								.getParameter(HercServicesConstants.FILE_PATH);
						// String fileName = request.getParameter("FileName");
						// log.info("fileName : " + fileName);
						log.info("cmd : " + cmd);
						log.info("user : " + user);
						log.info("folder : " + folder);
						log.info("newFolder : " + newFolder);
						log.info("filePath : " + filePath);
						if (null != file) {
							log.info("file content is not null in selecor block");
						}
						hercJobSiteService.createJobsiteMobile(response, user,
								folder, newFolder, cmd, file, filePath,
								adminSession);
					}

					// sridhar
					if (selector.equalsIgnoreCase("getJobSiteAlbums")) {
						log.info("Requested getJobSiteAlbums API call.");
						hercJobSiteService
								.getJobSiteAlbums(
										adminSession,
										response,
										request.getParameter(HercServicesConstants.JOBSITE_USERNAME));
					}

					// sridhar//edited on 12th Jan -Christo
					if (selector.equalsIgnoreCase("getBranchDetails")) {
						log.info("Requested getBranchDetails API");
						String deviceType = request
								.getParameter(HercServicesConstants.DEVICE_TYPE);
						hercLocationsService
								.getBranchDetails(
										mySqlTablesNode,
										response,
										request.getParameter(HercServicesConstants.PARAMETER_BRANCH_NUMBER),
										deviceType,
										adminSession,
										request.getParameter(HercServicesConstants.PARAMETER_USERNAME));
					}
					// sridhar //added on 12/28/2015/
					if (selector.equalsIgnoreCase("deleteJob")) {
						log.info("Requested deleteJob API call.");
						String JOBSITE_USERNAME = request
								.getParameter(HercServicesConstants.JOBSITE_USERNAME);
						String JOBSITE_FOLDERNAME = request
								.getParameter(HercServicesConstants.JOBSITE_FOLDERNAME);
						log.info("JOBSITE_USERNAME : " + JOBSITE_USERNAME);
						log.info("JOBSITE_FOLDERNAME : " + JOBSITE_FOLDERNAME);
						hercJobSiteService.deleteJob(adminSession, response,
								JOBSITE_USERNAME, JOBSITE_FOLDERNAME);

					}

					// sridhar //added on 12/28/2015/
					if (selector.equalsIgnoreCase("getMasterProductById")) {
						log.info("Requested getMasterProductList API call.");
						String id = request.getParameter("EquipmentName");
						String deviceType = request
								.getParameter(HercServicesConstants.DEVICE_TYPE);
						String type = request.getParameter("SolutionType");
						String country = request
								.getParameter(HercServicesConstants.BROWSECATALOG_COUNTRY);

						hercMasterProductListService.getMasterProductById(
								mySqlTablesNode, response, id, type,
								deviceType, country, adminSession);
					}
					// sridhar //added on 01/05/2016/
					if (selector.equalsIgnoreCase("setFavorite")) {
						log.info("Requested setFavorite API call.");
						String userName = request
								.getParameter(HercServicesConstants.JOBSITE_USERNAME);
						String branchnum = request
								.getParameter(HercServicesConstants.PARAMETER_BRANCH_NUMBER);
						String flag = request
								.getParameter(HercServicesConstants.FLAG);
						hercLocationsService.setFavorite(mySqlTablesNode,
								response, userName, branchnum, flag,
								adminSession);
					}

					// murali//edited on 11th Jan -Christo
					if (selector.equalsIgnoreCase("getBranchInfo")) {
						log.info("Requested getBranchInfo API");
						zipcode = request
								.getParameter(HercServicesConstants.PARAMETER_ZIPCODE);
						String deviceType = request
								.getParameter(HercServicesConstants.DEVICE_TYPE);
						hercLocationsService.getBranchInfo(zipcode,
								mySqlTablesNode, request, response, deviceType,
								adminSession);

					}
					// murali//edited on 12th Jan -Christo
					if (selector.equalsIgnoreCase("getNearByBranch")) {
						log.info("Requested getNearByBranch API");
						zipcode = request
								.getParameter(HercServicesConstants.PARAMETER_ZIPCODES);
						String deviceType = request
								.getParameter(HercServicesConstants.DEVICE_TYPE);
						hercLocationsService.getNearByBranch(zipcode,
								mySqlTablesNode, request, response, deviceType,
								adminSession);
					}
					// murali//edited on 12th Jan -Christo
					if (selector.equalsIgnoreCase("getStateWiseBranchList")) {
						// categories=request.getParameter("categories");
						String deviceType = request
								.getParameter(HercServicesConstants.DEVICE_TYPE);
						String country = request
								.getParameter(HercServicesConstants.BROWSECATALOG_COUNTRY);
						hercLocationsService.getStateWiseBranchList(
								mySqlTablesNode, request, response, deviceType,
								adminSession, country);
					}
					// Christo//edited on 12th Jan -Christo
					if (selector.equalsIgnoreCase("getFavouriteBranch")) {
						log.info("Requested getSolutions API call.");
						String countrycode = request
								.getParameter(HercServicesConstants.PARAMETER_USERNAME);
						categories = request
								.getParameter(HercServicesConstants.PARAMETER_CATEGORIES);
						String deviceType = request
								.getParameter(HercServicesConstants.DEVICE_TYPE);
						hercLocationsService.getFavouriteBranch(
								mySqlTablesNode, response, countrycode,
								categories, deviceType, adminSession);
					}

					// sridhar
					if (selector.equalsIgnoreCase("getCatalogDetails")) {
						log.info("Requested getCatalogDetails API call.");

						String CategoryName = request
								.getParameter(HercServicesConstants.BROWSECATALOG_CATALOGNAME);

						if (CategoryName
								.equalsIgnoreCase("Pro Contractor Tools")) {
							CategoryName = "ProContractor";
						}
						log.info(HercServicesConstants.BROWSECATALOG_CATALOGNAME
								+ ": " + CategoryName);
						String Type = request
								.getParameter(HercServicesConstants.BROWSECATALOG_TYPE);
						log.info(HercServicesConstants.BROWSECATALOG_TYPE
								+ ": " + Type);
						String deviceType = request
								.getParameter(HercServicesConstants.DEVICE_TYPE);
						String country = request
								.getParameter(HercServicesConstants.BROWSECATALOG_COUNTRY);

						hercBrowseCatalogService.getCatalogDetails(
								mySqlTablesNode, response, CategoryName, Type,
								deviceType, country, adminSession);
					}

					// sridhar on 1/21
					if (selector.equalsIgnoreCase("getSwagDetails")) {
						log.info("Requested redeemPoint API call.");

						hercLoyaltyService.getSwagDetails(mySqlTablesNode,
								response, adminSession);
					}

					if (selector.equalsIgnoreCase("redeemPoints")) {
						log.info("Requested redeemPoint API call.");
						String userName = request
								.getParameter(HercServicesConstants.PARAMETER_VALUE_LOYALTY_USER_NAME);
						String points = request
								.getParameter(HercServicesConstants.PARAMETER_VALUE_LOYALTY_POINTS);
						String itemName = request
								.getParameter(HercServicesConstants.SWAG_ITEM_NAME);
						String quantity = request
								.getParameter(HercServicesConstants.SWAG_QUANTITY);
						String receiverName = request
								.getParameter(HercServicesConstants.SWAG_RECEIVER_NAME);
						String receiverCompany = request
								.getParameter(HercServicesConstants.SWAG_RECEIVER_COMPANY);
						String receiverAddress = request
								.getParameter(HercServicesConstants.SWAG_RECEIVER_ADDRESS);
						String receiverPhoneNumber = request
								.getParameter(HercServicesConstants.SWAG_RECEIVER_PHONE_NUMBER);
						String receiverNotes = request
								.getParameter(HercServicesConstants.SWAG_RECEIVER_NOTES);
						String email = request.getParameter("Email");
						hercLoyaltyService.redeemPoints(adminSession,
								mySqlTablesNode, response, userName, points,
								itemName, quantity, receiverName,
								receiverCompany, receiverAddress,
								receiverPhoneNumber, receiverNotes, email);
					}
					// sridhar on 1/21
					if (selector.equalsIgnoreCase("addPoints")) {
						log.info("Requested earnPoint API call.");
						String RewardsTrigger = request
								.getParameter(HercServicesConstants.PARAMETER_VALUE_LOYALTY_REWARDS_TRIGGER);
						String UserName = request
								.getParameter(HercServicesConstants.PARAMETER_VALUE_LOYALTY_USER_NAME);
						String email = request.getParameter("Email");
						hercLoyaltyService.addPoints(mySqlTablesNode, response,
								adminSession, RewardsTrigger, UserName, email);
					}

					// sridhar on 1/21
					if (selector.equalsIgnoreCase("getEarnPoints")) {
						log.info("Requested getEarnPoints API call.");

						String UserName = request
								.getParameter(HercServicesConstants.PARAMETER_VALUE_LOYALTY_USER_NAME);

						hercLoyaltyService.getEarnPoints(mySqlTablesNode,
								response, adminSession, UserName);
					}

					if (selector.equalsIgnoreCase("getEquipSpecs")) {
						log.info("getEquipSpecs called");
						String equip_cat = request
								.getParameter(HercServicesConstants.BROWSECATALOG_CAT);
						String equip_class = request
								.getParameter(HercServicesConstants.BROWSECATALOG_CLASS);
						String country = request
								.getParameter(HercServicesConstants.BROWSECATALOG_COUNTRY);
						hercBrowseCatalogService.getEquipSpecs(mySqlTablesNode,
								response, equip_cat, equip_class, country,
								adminSession);
					}
					if (selector.equalsIgnoreCase("createBranches")) {
						log.info("createBranches called");
						hercJobSiteService.createBranches(response,
								adminSession);
					}

					if (selector.equalsIgnoreCase("getSurroundingCities")) {
						log.info("Requested getSurroundingCities API");
						String BranchNumber = request
								.getParameter("BranchNumber");
						hercLocationsService.getSurroundingCities(BranchNumber,
								response, adminSession);
					}
					if (selector.equalsIgnoreCase("getTopBranchZip")) {
						log.info("Requested getTopBranchZip API");
						String cityorzipparam = request.getParameter(
								"cityorzip").toLowerCase();
						JSONObject topBranchZip = hercLocationsService
								.getTopBranchZip(cityorzipparam, response,
										adminSession);
						try {
							hercCommonUtils.sendStatusResponse(response,
									topBranchZip);
						} catch (JSONException e) {
							log.error(
									"JSONException thrown in sendStatusResponse of getTopBranchZip ",
									e);
						}
					}
					if (selector.equalsIgnoreCase("getProximityBranches")) {
						log.info("Requested getProximityBranches API");
						String cityorzipparam = request.getParameter(
								"cityorzip").toLowerCase();
						String filter = request.getParameter("filter");

						JSONObject jsonObj = hercLocationsService
								.getProximityBranches(cityorzipparam, filter,
										response, adminSession);
						try {
							hercCommonUtils.sendStatusResponse(response,
									jsonObj);
							log.info("getProximityBranches response sent");
						} catch (JSONException e) {
							log.error(
									"Exception thrown while sending response from servlet",
									e);
						}
					}
					/*
					 * if (selector.equalsIgnoreCase("getNearByBranchNew")) {
					 * log.info("Requested getNearByBranchNew API");
					 * 
					 * String cityorzipparam =
					 * request.getParameter("Zipcode").toLowerCase(); String
					 * devicetype =
					 * request.getParameter(HercServicesConstants.DEVICE_TYPE);
					 * 
					 * hercLocationsService.getNearByBranchNew(response,
					 * cityorzipparam,devicetype,adminSession);
					 * 
					 * }
					 */
					// getDashBoardServiceCustomerInfo
					if (selector
							.equalsIgnoreCase("getDashBoardServiceCustomerInfo")) {
						log.info("Requested getDashBoardService API ");
						String url = HercServicesConstants.ETRIVE_GETDASHBOARDSERVICE_CUSTOMERINFO_URL;
						String companyCode = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_COMPANYCODE);
						String customerNumber = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_CUSTOMERNUMBER);
						hercEtrieveService.getDashBoardCustomerInfo(url,
								companyCode, customerNumber, response);
					}
					// getDashBoardServiceLicenseInfo
					if (selector
							.equalsIgnoreCase("getDashBoardServiceLicenseInfo")) {
						log.info("Requested getDashBoardServiceLicenseInfo API ");
						String url = HercServicesConstants.ETRIVE_GETDASHBOARDSERVICE_LICENSEINFO_URL;
						String companyCode = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_COMPANYCODE);
						String licenseState = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_LICENSESTATE);
						String licenseNumber = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_LICENSENUMBER);
						hercEtrieveService.getDashBoardLicenseInfo(url,
								companyCode, licenseState, licenseNumber,
								response);
					}
					// eMailRentalContractService
					if (selector.equalsIgnoreCase("eMailRentalContractService")) {
						log.info("Requested eMailRentalContractService API ");
						String url = HercServicesConstants.ETRIVE_EMAILRENTALCONTRACTSERVICE_URL;
						String companyCode = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_COMPANYCODE);
						String contractNumber = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_CONTRACTNUMBER);
						String contactEmail = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_CONTACTEMAIL);
						String contactFirstName = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_CONTACTFIRSTNAME);
						String contactLastName = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_CONTACTLASTNAME);
						String sequenceNumber = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_SEQUENCENUM);
						hercEtrieveService.eMailRentalContractService(url,
								companyCode, contractNumber, contactEmail,
								contactFirstName, contactLastName,
								sequenceNumber, response);
					}
					// getInvoiceAgingService
					if (selector.equalsIgnoreCase("getInvoiceAgingService")) {
						log.info("Requested getInvoiceAgingService API");
						String url = HercServicesConstants.ETRIVE_GETINVOICEAGINGSERVICE_URL;
						String companyCode = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_COMPANYCODE);
						String customerNumber = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_CUSTOMERNUMBER);
						hercEtrieveService.getInvoiceAgingService(url,
								companyCode, customerNumber, response);
					}
					// getUniqueContractDetailServiceCustomerInfo
					if (selector
							.equalsIgnoreCase("getUniqueContractDetailServiceCustomerInfo")) {
						log.info("Requested getUniqueContractDetailServiceCustomerInfo API");
						String url = HercServicesConstants.ETRIVE_GETUNIQUECONTRACTDETAILSERVICE_CUSTOMERINFO_URL;
						String companyCode = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_COMPANYCODE);
						String customerNumber = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_CUSTOMERNUMBER);
						String showOverdue = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_SHOWOVERDUE);
						String showPickUp = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_SHOWPICKUP);
						hercEtrieveService
								.getUniqueContractDetailServiceCustomerInfo(
										url, companyCode, customerNumber,
										showOverdue, showPickUp, response);
					}
					// getUniqueContractDetailServiceLicenseInfo
					if (selector
							.equalsIgnoreCase("getUniqueContractDetailServiceLicenseInfo")) {
						log.info("Requested getUniqueContractDetailServicelicenseInfo API");
						String url = HercServicesConstants.ETRIVE_GETUNIQUECONTRACTDETAILSERVICE_LICENSEINFO_URL;
						String companyCode = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_COMPANYCODE);
						String licenseState = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_LICENSESTATE);
						String licenseNumber = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_LICENSENUMBER);
						String showOverdue = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_SHOWOVERDUE);
						String showPickUp = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_SHOWPICKUP);
						hercEtrieveService
								.getUniqueContractDetailServiceLicenseInfo(url,
										companyCode, licenseState,
										licenseNumber, showOverdue, showPickUp,
										response);
					}
					// getOpenRentalsDetailsServiceCustomerInfo
					if (selector
							.equalsIgnoreCase("getOpenRentalsDetailsServiceCustomerInfo")) {
						log.info("Requested getOpenRentalsDetailsServicecustomerInfo API");
						String url = HercServicesConstants.ETRIVE_GETOPENRENTALSDETAILSSERVICE_CUSTOMERINFO_URL;
						String companyCode = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_COMPANYCODE);
						String customerNumber = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_CUSTOMERNUMBER);
						hercEtrieveService
								.getOpenRentalsDetailsServiceCustomerInfo(url,
										companyCode, customerNumber, response);
					}
					// getOpenRentalsDetailsServiceLicenseInfo
					if (selector
							.equalsIgnoreCase("getOpenRentalsDetailsServiceLicenseInfo")) {
						log.info("Requested getOpenRentalsDetailsServicelicenseInfo API");
						String url = HercServicesConstants.ETRIVE_GETOPENRENTALSDETAILSSERVICE_LICENSEINFO_URL;
						String companyCode = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_COMPANYCODE);
						String licenseState = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_LICENSESTATE);
						String licenseNumber = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_LICENSENUMBER);
						String contractNumber = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_CONTRACTNUMBER);
						hercEtrieveService
								.getOpenRentalsDetailsServiceLicenseInfo(url,
										companyCode, licenseState,
										licenseNumber, contractNumber, response);

					}
					// extendContractService
					if (selector.equalsIgnoreCase("extendContractService")) {
						log.info("Requested extendContractService API");
						String url = HercServicesConstants.ETRIVE_EXTENDCONTRACTSERVICE_URL;
						String companyCode = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_COMPANYCODE);
						String modeFlag = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_MODEFLAG);
						String contractNumber = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_CONTRACTNUMBER);
						String newReturnDate = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_NEWRETURNDATE);
						String contactName = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_CONTACTNAME);
						String contactPhone = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_CONTACTPHONE);
						String contactEmail = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_CONTACTEMAIL);
						String contactComment = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_CONTACTCOMMENT);
						hercEtrieveService.extendContractService(url,
								companyCode, modeFlag, contractNumber,
								newReturnDate, contactName, contactPhone,
								contactEmail, contactComment, response);
					}
					// releaseEquipmentService
					if (selector.equalsIgnoreCase("releaseEquipmentService")) {
						log.info("Requested releaseEquipmentService API");
						String url = HercServicesConstants.ETRIVE_RELEASEEQUIPMENTSERVICE_URL;
						String companyCode = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_COMPANYCODE);
						String contractNumber = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_CONTRACTNUMBER);
						String releaseDate = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_RELEASEDATE);
						String releaseTime = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_RELEASETIME);
						String contactPhone = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_CONTACTPHONE);
						String contactEmail = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_CONTACTEMAIL);
						String siteContact = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_SITECONTACT);
						String sitePhone = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_SITEPHONE);
						String equipPhyLoc = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_EQUIPPHYLOC);
						String contactComments = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_CONTACTCOMMENTS);
						String lineNumbers = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_LINENUMBERS);
						String lineQty = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_LINEQTY);
						hercEtrieveService
								.releaseEquipmentService(url, companyCode,
										contractNumber, releaseDate,
										releaseTime, contactPhone,
										contactEmail, siteContact, sitePhone,
										equipPhyLoc, contactComments,
										lineNumbers, lineQty, response);
					}
					// getRentalRates
					if (selector.equalsIgnoreCase("getRentalRates")) {
						log.info("Requested getRentalRates API");
						// get Branch Number for given ZIP code.
						String cityorzipparam = request
								.getParameter(HercServicesConstants.PARAMETER_ZIPCODE);
						log.info("cityorzipparam : " + cityorzipparam);
						JSONObject topBranchZip = hercLocationsService
								.getTopBranchZip(cityorzipparam, response,
										adminSession);
						if (null != topBranchZip) {
							String countryCode = null;
							String branchNumber = null;
							if (topBranchZip.has("COUNTRY_CODE")) {
								try {
									countryCode = topBranchZip
											.getString("COUNTRY_CODE");
								} catch (JSONException e) {
									log.error(
											"JSONException thrown in getRenatalRates call",
											e);
								}
							}
							if (topBranchZip.has("BRANCH_CODE")) {
								try {
									branchNumber = topBranchZip
											.getString("BRANCH_CODE");
								} catch (JSONException e) {
									log.error(
											"JSONException thrown in getRenatalRates call",
											e);
								}

							}
							String url = HercServicesConstants.ETRIVE_GETRENTALRATES_URL;
							String startDate = request
									.getParameter(HercServicesConstants.ETRIVE_COLUMN_STARTDATE);
							String endDate = request
									.getParameter(HercServicesConstants.ETRIVE_COLUMN_ENDDATE);
							String equipCatg = request
									.getParameter(HercServicesConstants.ETRIVE_COLUMN_EQUIPCATG);
							String equipClass = request
									.getParameter(HercServicesConstants.ETRIVE_COLUMN_EQUIPCLASS);
							String equipQty = request
									.getParameter(HercServicesConstants.ETRIVE_COLUMN_EQUIPQTY);
							String customerNumber = request
									.getParameter(HercServicesConstants.ETRIVE_COLUMN_CUSTOMERNUMBER);
							if (equipCatg.charAt('0') == 0) {
								equipCatg = equipCatg.substring(1);
							}
							if (equipClass.charAt('0') == 0) {
								equipClass = equipClass.substring(1);

							}
							log.info("equipCatg : " + equipCatg);
							log.info("equipClass : " + equipClass);
							JSONObject rentalRates = hercEtrieveService
									.getRentalRates(url, countryCode,
											branchNumber, startDate, endDate,
											equipCatg, equipClass, equipQty,
											customerNumber, response);
							try {
								hercCommonUtils.sendStatusResponse(response,
										rentalRates);
							} catch (JSONException e) {
								log.error(
										"JSONException thrown in sendStatusResponse of getTopBranchZip ",
										e);
							}
						}
					}
					// rentalReservationService
					if (selector.equalsIgnoreCase("rentalReservationService")) {
						log.info("Requested rentalReservationService API");
						String url = HercServicesConstants.ETRIVE_RENTALRESERVATIONSERVICE_URL;
						String customerNumber = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_CUSTOMERNUMBER);
						String branchCode = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_BRANCHCODE);
						String jobSite = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_JOBSITE);
						String jobSiteContact = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_JOBSITECONTACT);
						String jobSitePhone = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_JOBSITEPHONE);
						String jobSiteAddress1 = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_JOBSITEADDRESS1);
						String jobSiteAddress2 = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_JOBSITEADDRESS2);
						String jobSiteCity = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_JOBSITECITY);
						String jobSiteState = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_JOBSITESTATE);
						String jobSiteZip = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_JOBSITEZIP);
						String purchaseOrder = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_PURCHASEORDER);
						String startDate = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_STARTDATE);
						String estReturnDate = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_ESTRETURNDATE);
						String equipCatg = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_EQUIPCATG);
						String equipClass = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_EQUIPCLASS);
						String equipQty = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_EQUIPQTY);
						String emailAddress = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_EMAILADDRESS);
						String sourceID = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_SOURCEID);
						String deliveryNotes = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_DELIVERYNOTES);
						String orderedBy = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_ORDEREDBY);
						hercEtrieveService.rentalReservationService(url,
								customerNumber, branchCode, jobSite,
								jobSiteContact, jobSitePhone, jobSiteAddress1,
								jobSiteAddress2, jobSiteCity, jobSiteState,
								jobSiteZip, purchaseOrder, startDate,
								estReturnDate, equipCatg, equipClass, equipQty,
								emailAddress, sourceID, deliveryNotes,
								orderedBy, response);
					}
					// verifyCustomerServiceCustomer
					if (selector
							.equalsIgnoreCase("verifyCustomerServiceCustomer")) {
						log.info("Requested verifyCustomerServiceCustomer API");
						String url = HercServicesConstants.ETRIVE_VERIFYCUSTOMERSERVICE_URL;
						String companyCode = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_COMPANYCODE);
						String customerNumber = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_CUSTOMERNUMBER);
						hercEtrieveService.verifyCustomerServiceCustomer(url,
								companyCode, customerNumber, response);
					}
					// verifyCustomerServiceLicense
					if (selector
							.equalsIgnoreCase("verifyCustomerServiceLicense")) {
						log.info("Requested verifyCustomerServiceLicense API ");
						String url = HercServicesConstants.ETRIVE_VERIFYCUSTOMERSERVICE_URL;
						String companyCode = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_COMPANYCODE);
						String licenseState = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_LICENSESTATE);
						String licenseNumber = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_LICENSENUMBER);
						hercEtrieveService.verifyCustomerServiceLicense(url,
								companyCode, licenseState, licenseNumber,
								response);
					}
					// uniqueReservationNumbersServiceCustomer
					if (selector
							.equalsIgnoreCase("uniqueReservationNumbersServiceCustomer")) {
						log.info("Requested uniqueReservationNumbersServiceCustomer API");
						String url = HercServicesConstants.ETRIVE_UNIQUERESERVATIONNUMBERSSERVICE_URL;
						String companyCode = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_COMPANYCODE);
						String customerNumber = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_CUSTOMERNUMBER);
						hercEtrieveService
								.uniqueReservationNumbersServiceCustomer(url,
										companyCode, customerNumber, response);
					}
					// uniqueReservationNumbersServiceLicense
					if (selector
							.equalsIgnoreCase("uniqueReservationNumbersServiceLicense")) {
						log.info("Requested uniqueReservationNumbersServiceLicense API ");
						String url = HercServicesConstants.ETRIVE_UNIQUERESERVATIONNUMBERSSERVICE_URL;
						String companyCode = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_COMPANYCODE);
						String licenseState = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_LICENSESTATE);
						String licenseNumber = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_LICENSENUMBER);
						hercEtrieveService
								.uniqueReservationNumbersServiceLicense(url,
										companyCode, licenseState,
										licenseNumber, response);
					}
					// retrieveOpenReservationDetailsCustomer
					if (selector
							.equalsIgnoreCase("retrieveOpenReservationDetailsCustomer")) {
						log.info("Requested retrieveOpenReservationDetailsCustomer API");
						String url = HercServicesConstants.ETRIVE_RETRIEVEOPENRESERVATIONDETAILS_URL;
						String companyCode = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_COMPANYCODE);
						String customerNumber = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_CUSTOMERNUMBER);
						String resvNumber = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_RESVNUMBER);
						hercEtrieveService
								.retrieveOpenReservationDetailsCustomer(url,
										companyCode, customerNumber,
										resvNumber, response);
					}
					// retrieveOpenReservationDetailsLicense
					if (selector
							.equalsIgnoreCase("retrieveOpenReservationDetailsLicense")) {
						log.info("Requested retrieveOpenReservationDetailsLicense API ");
						String url = HercServicesConstants.ETRIVE_UNIQUERESERVATIONNUMBERSSERVICE_URL;
						String companyCode = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_COMPANYCODE);
						String licenseState = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_LICENSESTATE);
						String licenseNumber = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_LICENSENUMBER);
						String resvNumber = request
								.getParameter(HercServicesConstants.ETRIVE_COLUMN_RESVNUMBER);
						hercEtrieveService
								.retrieveOpenReservationDetailsLicense(url,
										companyCode, licenseState,
										licenseNumber, resvNumber, response);
					}
					if (selector.equalsIgnoreCase("getNearByBranchNew")) {
						log.info("Requested getNearByBranchNewPro API");
						String zip = request.getParameter("Zipcode")
								.toLowerCase();
						String devicetype = request
								.getParameter(HercServicesConstants.DEVICE_TYPE);
						String filter = request.getParameter("filter");

						JSONObject jsonObj = hercLocationsService
								.getNearByBranchNewPro(zip, devicetype, filter,
										response, adminSession);
						try {
							hercCommonUtils.sendStatusResponse(response,
									jsonObj);
						} catch (JSONException e) {
							log.error(
									"Exception thrown while sending response from servlet",
									e);
						}
					}

				}
			} catch (LoginException e) {
				log.error("Exception during repository login:", e);
			} catch (RepositoryException e) {
				log.error("Exception during repository login:", e);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (null != adminSession) {
					adminSession.logout();
				}
			}

		}

		long endTime = System.currentTimeMillis();
		log.info("Time taken in doPost() method :" + (endTime - startTime)
				+ "MilliSeconds");
		log.info("Exiting doPost method...................");

	}
}
