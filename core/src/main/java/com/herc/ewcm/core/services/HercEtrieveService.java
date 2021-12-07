package com.herc.ewcm.core.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.herc.ewcm.core.common.HercCommonUtils;

@Component(name = "com.herc.ewcm.core.services.HercEtrieve", label = "HercEtrieve", description = "Handles all api calls of HercEtrieve", specVersion = "1.1", immediate = true, metatype = true)
@Service(value = { HercEtrieveService.class })
public class HercEtrieveService {
	@Reference
	private HercCommonUtils hercCommonUtils;

	@Reference
	private DataSourcePoolService dataSourcePoolService;

	private static final Logger log = LoggerFactory.getLogger(HercEtrieveService.class);

	public void getDashBoardCustomerInfo(String url, String companyCode, String customerNumber, SlingHttpServletResponse response)
			throws IOException, ServletException {
		log.info("******* called getDashBoard START ************");
		log.info("url :" + url);
		log.info("companyCode :" + companyCode);
		log.info("customerNumber :" + customerNumber);
		String inputLine = "";
		String goo = "";

		URL urldemo = new URL(url + "?" + "companyCode=" + companyCode + "&" + "customerNumber=" + customerNumber);
		URLConnection yc = urldemo.openConnection();
		BufferedReader in = null;
		try
			{
				in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

				while ((inputLine = in.readLine()) != null)
					{
						log.info("inputLine - while : " + inputLine);
						goo = goo + inputLine;
					}

				JSONObject json = new JSONObject(goo);
				hercCommonUtils.sendStatusResponse(response, json);
				in.close();
			} catch (Exception e)
			{
				log.info(e + "");
			}

		log.info("inputLine = " + inputLine);
		log.info("******* called getDashBoardService END************");
	}

	

	public void getDashBoardLicenseInfo(String url, String companyCode, String licenseState, String licenseNumber, SlingHttpServletResponse response)
			throws IOException, ServletException {
		log.info("******* called getDashBoardLicenseInfo START ************");

		String inputLine = "";
		String goo = "";

		URL urldemo = new URL(url + "?" + "companyCode=" + companyCode + "&" + "licenseState=" + licenseState + "&" + "licenseNumber="
				+ licenseNumber);
		URLConnection yc = urldemo.openConnection();
		BufferedReader in = null;
		try
			{
				in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

				while ((inputLine = in.readLine()) != null)
					{
						log.info("inputLine - while : " + inputLine);
						goo = goo + inputLine;
					}

				JSONObject json = new JSONObject(goo);
				hercCommonUtils.sendStatusResponse(response, json);
				in.close();
			} catch (Exception e)
			{
				log.info(e + "");
			}

		log.info("inputLine = " + inputLine);
		log.info("******* called getDashBoardLicenseInfo END************");
	}

	public void eMailRentalContractService(String url, String companyCode, String contractNumber, String contactEmail, String contactFirstName,
			String contactLastName, String sequenceNumber, SlingHttpServletResponse response) throws IOException, ServletException {
		log.info("******* called eMailRentalContractService START ************");
		String inputLine = "";
		String goo = "";

		URL urldemo = new URL(url + "?" + "companyCode=" + companyCode + "&" + "contractNumber=" + contractNumber + "&" + "contactEmail="
				+ contactEmail + "&" + "contractNumber=" + contractNumber + "&" + "contactFirstName=" + contactFirstName + "&" + "contactLastName="
				+ contactLastName + "&" + "sequenceNumber=" + sequenceNumber);
		URLConnection yc = urldemo.openConnection();
		BufferedReader in = null;
		try
			{
				in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

				while ((inputLine = in.readLine()) != null)
					{
						log.info("inputLine - while : " + inputLine);
						goo = goo + inputLine;
					}

				JSONObject json = new JSONObject(goo);
				hercCommonUtils.sendStatusResponse(response, json);
				in.close();
			} catch (Exception e)
			{
				log.info(e + "");
			}

		log.info("inputLine = " + inputLine);
		log.info("******* called eMailRentalContractService END************");
	}

	public void getInvoiceAgingService(String url, String companyCode, String customerNumber, SlingHttpServletResponse response) throws IOException,
			ServletException {
		log.info("******* called getInvoiceAgingService START ************");
		log.info("url :" + url);
		log.info("companyCode :" + companyCode);
		log.info("customerNumber :" + customerNumber);
		String inputLine = "";
		String goo = "";

		URL urldemo = new URL(url + "?" + "companyCode=" + companyCode + "&" + "customerNumber=" + customerNumber);
		URLConnection yc = urldemo.openConnection();
		BufferedReader in = null;
		try
			{
				in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

				while ((inputLine = in.readLine()) != null)
					{
						log.info("inputLine - while : " + inputLine);
						goo = goo + inputLine;
					}

				JSONObject json = new JSONObject(goo);
				hercCommonUtils.sendStatusResponse(response, json);
				in.close();
			} catch (Exception e)
			{
				log.info(e + "");
			}

		log.info("inputLine = " + inputLine);
		log.info("******* called getInvoiceAgingService END************");
	}

	public void getUniqueContractDetailServiceCustomerInfo(String url, String companyCode, String customerNumber, String showOverdue,
			String showPickUp, SlingHttpServletResponse response) throws IOException, ServletException {
		log.info("******* called getUniqueContractDetailServiceCustomerInfo START ************");

		String inputLine = "";
		String goo = "";

		URL urldemo = new URL(url + "?" + "companyCode=" + companyCode + "&" + "customerNumber=" + customerNumber + "&" + "showOverdue="
				+ showOverdue + "&" + "showPickUp=" + showPickUp);
		URLConnection yc = urldemo.openConnection();
		BufferedReader in = null;
		try
			{
				in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

				while ((inputLine = in.readLine()) != null)
					{
						log.info("inputLine - while : " + inputLine);
						goo = goo + inputLine;
					}

				JSONObject json = new JSONObject(goo);
				hercCommonUtils.sendStatusResponse(response, json);
				in.close();
			} catch (Exception e)
			{
				log.info(e + "");
			}

		log.info("inputLine = " + inputLine);
		log.info("******* called getUniqueContractDetailServiceCustomerInfo END************");
	}

	public void getUniqueContractDetailServiceLicenseInfo(String url, String companyCode, String licenseState, String licenseNumber,
			String showOverdue, String showPickUp, SlingHttpServletResponse response) throws IOException, ServletException {
		log.info("******* called getUniqueContractDetailServiceLicenseInfo START ************");

		String inputLine = "";
		String goo = "";

		URL urldemo = new URL(url + "?" + "companyCode=" + companyCode + "&" + "licenseState=" + licenseState + "&" + "licenseNumber="
				+ licenseNumber + "&" + "showOverdue=" + showOverdue + "&" + "showPickUp=" + showPickUp);
		URLConnection yc = urldemo.openConnection();
		BufferedReader in = null;
		try
			{
				in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

				while ((inputLine = in.readLine()) != null)
					{
						log.info("inputLine - while : " + inputLine);
						goo = goo + inputLine;
					}

				JSONObject json = new JSONObject(goo);
				hercCommonUtils.sendStatusResponse(response, json);
				in.close();
			} catch (Exception e)
			{
				log.info(e + "");
			}

		log.info("inputLine = " + inputLine);
		log.info("******* called getUniqueContractDetailServiceLicenseInfo END************");
	}

	public void getOpenRentalsDetailsServiceCustomerInfo(String url, String companyCode, String customerNumber, SlingHttpServletResponse response)
			throws IOException, ServletException {
		log.info("******* called getOpenRentalsDetailsServiceCustomerInfo START ************");

		String inputLine = "";
		String goo = "";

		URL urldemo = new URL(url + "?" + "companyCode=" + companyCode + "&" + "customerNumber=" + customerNumber);
		URLConnection yc = urldemo.openConnection();
		BufferedReader in = null;
		try
			{
				in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

				while ((inputLine = in.readLine()) != null)
					{
						log.info("inputLine - while : " + inputLine);
						goo = goo + inputLine;
					}

				JSONObject json = new JSONObject(goo);
				hercCommonUtils.sendStatusResponse(response, json);
				in.close();
			} catch (Exception e)
			{
				log.info(e + "");
			}

		log.info("inputLine = " + inputLine);
		log.info("******* called getOpenRentalsDetailsServiceCustomerInfo END************");
	}

	public void getOpenRentalsDetailsServiceLicenseInfo(String url, String companyCode, String licenseState, String licenseNumber,
			String contractNumber, SlingHttpServletResponse response) throws IOException, ServletException {
		log.info("******* called getOpenRentalsDetailsServiceLicenseInfo START ************");

		String inputLine = "";
		String goo = "";

		URL urldemo = new URL(url + "?" + "companyCode=" + companyCode + "&" + "licenseState=" + licenseState + "&" + "licenseNumber="
				+ licenseNumber + "&" + "contractNumber=" + contractNumber);
		URLConnection yc = urldemo.openConnection();
		BufferedReader in = null;
		try
			{
				in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

				while ((inputLine = in.readLine()) != null)
					{
						log.info("inputLine - while : " + inputLine);
						goo = goo + inputLine;
					}

				JSONObject json = new JSONObject(goo);
				hercCommonUtils.sendStatusResponse(response, json);
				in.close();
			} catch (Exception e)
			{
				log.info(e + "");
			}

		log.info("inputLine = " + inputLine);
		log.info("******* called getOpenRentalsDetailsServiceLicenseInfo END************");
	}

	public void extendContractService(String url, String companyCode, String modeFlag, String contractNumber, String newReturnDate,
			String contactName, String contactPhone, String contactEmail, String contactComment, SlingHttpServletResponse response)
			throws IOException, ServletException {
		log.info("******* called extendContractService START ************");

		String inputLine = "";
		String goo = "";

		URL urldemo = new URL(url + "?" + "companyCode=" + companyCode + "&" + "modeFlag=" + modeFlag + "&" + "contractNumber=" + contractNumber
				+ "&" + "newReturnDate=" + newReturnDate + "&" + "contactEmail=" + contactEmail + "&" + "contactName=" + contactName + "&"
				+ "contactPhone=" + contactPhone + "&" + "contactEmail=" + contactEmail + "&" + "contactComment=" + contactComment);
		URLConnection yc = urldemo.openConnection();
		BufferedReader in = null;
		try
			{
				in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

				while ((inputLine = in.readLine()) != null)
					{
						log.info("inputLine - while : " + inputLine);
						goo = goo + inputLine;
					}

				JSONObject json = new JSONObject(goo);
				hercCommonUtils.sendStatusResponse(response, json);
				in.close();
			} catch (Exception e)
			{
				log.info(e + "");
			}

		log.info("inputLine = " + inputLine);
		log.info("******* called extendContractService END************");
	}

	public void releaseEquipmentService(String url, String companyCode, String contractNumber, String releaseDate, String releaseTime,
			String contactPhone, String contactEmail, String siteContact, String sitePhone, String equipPhyLoc, String contactComments,
			String lineNumbers, String lineQty, SlingHttpServletResponse response) throws IOException, ServletException {
		log.info("******* called releaseEquipmentService START ************");

		String inputLine = "";
		String goo = "";

		URL urldemo = new URL(url + "?" + "companyCode=" + companyCode + "&" + "contractNumber=" + contractNumber + "&" + "releaseDate="
				+ releaseDate + "&" + "releaseTime=" + releaseTime + "&" + "releaseDate=" + releaseDate + "&" + "contactPhone=" + contactPhone + "&"
				+ "contactEmail=" + contactEmail + "&" + "siteContact=" + siteContact + "&" + "sitePhone=" + sitePhone + "&" + "equipPhyLoc="
				+ equipPhyLoc + "&" + "contactComments=" + contactComments + "&" + "lineNumbers=" + lineNumbers + "&" + "lineQty=" + lineQty);
		URLConnection yc = urldemo.openConnection();
		BufferedReader in = null;
		try
			{
				in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

				while ((inputLine = in.readLine()) != null)
					{
						log.info("inputLine - while : " + inputLine);
						goo = goo + inputLine;
					}

				JSONObject json = new JSONObject(goo);
				hercCommonUtils.sendStatusResponse(response, json);
				in.close();
			} catch (Exception e)
			{
				log.info(e + "");
			}

		log.info("inputLine = " + inputLine);
		log.info("******* called releaseEquipmentService END************");
	}

	public JSONObject getRentalRates(String url, String companyCode, String branchCode, String startDate, String endDate, String equipCatg,
			String equipClass, String equipQty, String customerNumber, SlingHttpServletResponse response) throws IOException, ServletException {
		log.info("******* called getRentalRates START ************");

		String inputLine = "";
		String goo = "";

		URL urldemo = new URL(url + "?" + "companyCode=" + companyCode + "&" + "branchCode=" + branchCode + "&" + "startDate=" + startDate + "&"
				+ "endDate=" + endDate + "&" + "equipCatg=" + equipCatg + "&" + "equipClass=" + equipClass + "&" + "equipQty=" + equipQty + "&"
				+ "customerNumber=" + customerNumber);
		URLConnection yc = urldemo.openConnection();
		BufferedReader in = null;
		try
			{
				in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

				while ((inputLine = in.readLine()) != null)
					{
						log.info("inputLine - while : " + inputLine);
						goo = goo + inputLine;
					}

				JSONObject json = new JSONObject(goo);
			//	hercCommonUtils.sendStatusResponse(response, json);
				return json;
				
			} catch (Exception e)
			{
				log.info(e + "");
			} finally {
				in.close();
			}

		log.info("inputLine = " + inputLine);
		log.info("******* called getRentalRates END************");
		return null;
	}

	public void rentalReservationService(String url, String companyCode, String branchCode, String jobSite, String jobSiteContact,
			String jobSitePhone, String jobSiteAddress1, String jobSiteAddress2, String jobSiteCity, String jobSiteState, String jobSiteZip,
			String purchaseOrder, String startDate, String estReturnDate, String equipCatg, String equipClass, String equipQty, String emailAddress,
			String sourceID, String deliveryNotes, String orderedBy, SlingHttpServletResponse response) throws IOException, ServletException {
		log.info("******* called rentalReservationService START ************");

		String inputLine = "";
		String goo = "";
		URL urldemo = new URL(url + "?" + "companyCode=" + companyCode + "&" + "branchCode=" + branchCode + "&" + "jobSite=" + jobSite + "&"
				+ "jobSiteContact=" + jobSiteContact + "&" + "jobSitePhone=" + jobSitePhone + "&" + "jobSiteAddress1=" + jobSiteAddress1 + "&"
				+ "jobSiteAddress2=" + jobSiteAddress2 + "&" + "jobSiteCity=" + jobSiteCity + "&" + "jobSiteState=" + jobSiteState + "&"
				+ "jobSiteZip=" + jobSiteZip + "&" + "purchaseOrder=" + purchaseOrder + "&" + "startDate=" + startDate + "&" + "estReturnDate="
				+ estReturnDate + "&" + "equipCatg=" + equipCatg + "&" + "equipClass=" + equipClass + "&" + "equipQty=" + equipQty + "&"
				+ "emailAddress=" + emailAddress + "&" + "sourceID=" + sourceID + "&" + "deliveryNotes=" + deliveryNotes + "&" + "orderedBy="
				+ orderedBy);
		URLConnection yc = urldemo.openConnection();
		BufferedReader in = null;
		try
			{
				in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

				while ((inputLine = in.readLine()) != null)
					{
						log.info("inputLine - while : " + inputLine);
						goo = goo + inputLine;
					}

				JSONObject json = new JSONObject(goo);
				hercCommonUtils.sendStatusResponse(response, json);
				in.close();
			} catch (Exception e)
			{
				log.info(e + "");
			}

		log.info("inputLine = " + inputLine);
		log.info("******* called rentalReservationService END************");
	}

	public void verifyCustomerServiceCustomer(String url, String companyCode, String customerNumber, SlingHttpServletResponse response)
			throws IOException, ServletException {
		log.info("******* called verifyCustomerServiceCustomer START ************");
		log.info("url :" + url);
		log.info("companyCode :" + companyCode);
		log.info("customerNumber :" + customerNumber);
		String inputLine = "";
		String goo = "";

		URL urldemo = new URL(url + "?" + "companyCode=" + companyCode + "&" + "customerNumber=" + customerNumber);
		URLConnection yc = urldemo.openConnection();
		BufferedReader in = null;
		try
			{
				in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

				while ((inputLine = in.readLine()) != null)
					{
						log.info("inputLine - while : " + inputLine);
						goo = goo + inputLine;
					}

				JSONObject json = new JSONObject(goo);
				hercCommonUtils.sendStatusResponse(response, json);
				in.close();
			} catch (Exception e)
			{
				log.info(e + "");
			}

		log.info("inputLine = " + inputLine);
		log.info("******* called verifyCustomerServiceCustomer END************");
	}

	public void verifyCustomerServiceLicense(String url, String companyCode, String licenseState, String licenseNumber,
			SlingHttpServletResponse response) throws IOException, ServletException {
		log.info("******* called verifyCustomerServiceLicense START ************");

		String inputLine = "";
		String goo = "";

		URL urldemo = new URL(url + "?" + "companyCode=" + companyCode + "&" + "licenseState=" + licenseState + "&" + "licenseNumber="
				+ licenseNumber);
		URLConnection yc = urldemo.openConnection();
		BufferedReader in = null;
		try
			{
				in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

				while ((inputLine = in.readLine()) != null)
					{
						log.info("inputLine - while : " + inputLine);
						goo = goo + inputLine;
					}

				JSONObject json = new JSONObject(goo);
				hercCommonUtils.sendStatusResponse(response, json);
				in.close();
			} catch (Exception e)
			{
				log.info(e + "");
			}

		log.info("inputLine = " + inputLine);
		log.info("******* called verifyCustomerServiceLicense END************");
	}

	public void uniqueReservationNumbersServiceCustomer(String url, String companyCode, String customerNumber, SlingHttpServletResponse response)
			throws IOException, ServletException {
		log.info("******* called uniqueReservationNumbersServiceCustomer START ************");
		log.info("url :" + url);
		log.info("companyCode :" + companyCode);
		log.info("customerNumber :" + customerNumber);
		String inputLine = "";
		String goo = "";

		URL urldemo = new URL(url + "?" + "companyCode=" + companyCode + "&" + "customerNumber=" + customerNumber);
		URLConnection yc = urldemo.openConnection();
		BufferedReader in = null;
		try
			{
				in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

				while ((inputLine = in.readLine()) != null)
					{
						log.info("inputLine - while : " + inputLine);
						goo = goo + inputLine;
					}

				JSONObject json = new JSONObject(goo);
				hercCommonUtils.sendStatusResponse(response, json);
				in.close();
			} catch (Exception e)
			{
				log.info(e + "");
			}

		log.info("inputLine = " + inputLine);
		log.info("******* called uniqueReservationNumbersServiceCustomer END************");
	}

	public void uniqueReservationNumbersServiceLicense(String url, String companyCode, String licenseState, String licenseNumber,
			SlingHttpServletResponse response) throws IOException, ServletException {
		log.info("******* called uniqueReservationNumbersServiceLicense START ************");

		String inputLine = "";
		String goo = "";

		URL urldemo = new URL(url + "?" + "companyCode=" + companyCode + "&" + "licenseState=" + licenseState + "&" + "licenseNumber="
				+ licenseNumber);
		URLConnection yc = urldemo.openConnection();
		BufferedReader in = null;
		try
			{
				in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

				while ((inputLine = in.readLine()) != null)
					{
						log.info("inputLine - while : " + inputLine);
						goo = goo + inputLine;
					}

				JSONObject json = new JSONObject(goo);
				hercCommonUtils.sendStatusResponse(response, json);
				in.close();
			} catch (Exception e)
			{
				log.info(e + "");
			}

		log.info("inputLine = " + inputLine);
		log.info("******* called uniqueReservationNumbersServiceLicense END************");
	}

	public void retrieveOpenReservationDetailsCustomer(String url, String companyCode, String customerNumber, String resvNumber,
			SlingHttpServletResponse response) throws IOException, ServletException {
		log.info("******* called retrieveOpenReservationDetailsCustomer START ************");
		log.info("url :" + url);
		log.info("companyCode :" + companyCode);
		log.info("customerNumber :" + customerNumber);
		log.info("resvNumber:" + resvNumber);
		String inputLine = "";
		String goo = "";

		URL urldemo = new URL(url + "?" + "companyCode=" + companyCode + "&" + "customerNumber=" + customerNumber + "&" + "resvNumber=" + resvNumber);
		URLConnection yc = urldemo.openConnection();
		BufferedReader in = null;
		try
			{
				in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

				while ((inputLine = in.readLine()) != null)
					{
						log.info("inputLine - while : " + inputLine);
						goo = goo + inputLine;
					}

				JSONObject json = new JSONObject(goo);
				hercCommonUtils.sendStatusResponse(response, json);
				in.close();
			} catch (Exception e)
			{
				log.info(e + "");
			}

		log.info("inputLine = " + inputLine);
		log.info("******* called retrieveOpenReservationDetailsCustomer END************");
	}

	public void retrieveOpenReservationDetailsLicense(String url, String companyCode, String licenseState, String licenseNumber, String resvNumber,
			SlingHttpServletResponse response) throws IOException, ServletException {
		log.info("******* called retrieveOpenReservationDetailsLicense START ************");
		log.info("url :" + url);
		log.info("companyCode :" + companyCode);
		log.info("licenseState :" + licenseState);
		log.info("licenseNumber :" + licenseNumber);
		log.info("resvNumber:" + resvNumber);
		String inputLine = "";
		String goo = "";

		URL urldemo = new URL(url + "?" + "companyCode=" + companyCode + "&" + "licenseState=" + licenseState + "&" + "licenseNumber="
				+ licenseNumber + "&" + "resvNumber=" + resvNumber);
		URLConnection yc = urldemo.openConnection();
		BufferedReader in = null;
		try
			{
				in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

				while ((inputLine = in.readLine()) != null)
					{
						log.info("inputLine - while : " + inputLine);
						goo = goo + inputLine;
					}

				JSONObject json = new JSONObject(goo);
				hercCommonUtils.sendStatusResponse(response, json);
				in.close();
			} catch (Exception e)
			{
				log.info(e + "");
			}

		log.info("inputLine = " + inputLine);
		log.info("******* called retrieveOpenReservationDetailsLicense END************");
	}
}