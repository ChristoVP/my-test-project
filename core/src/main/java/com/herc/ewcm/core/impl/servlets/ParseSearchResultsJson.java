package com.herc.ewcm.core.impl.servlets;

import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParseSearchResultsJson {
   private String someText;

    private static final Logger log = LoggerFactory.getLogger(ParseSearchResultsJson.class);
    
    public String totalResults = null;
    
    Map<String,  Map<String, String>> myHashMap;

    ArrayList<String> branchCode ;
    
    String branchId = null;
    
    Map<String, String>  myValueMap ;
    
    String locationFilter = null;
    
    String categories = null;
    
	/*
	 * @Override public void activate() throws Exception {
	 */
		/*
		 * try{ // TODO Auto-generated method stub log.error("activate called");
		 * branchId = get("branchCode", String.class); locationFilter =
		 * getRequest().getParameter("locationFilterText"); categories =
		 * getRequest().getParameter("categories");
		 * log.error("************* locationFilter"+locationFilter);
		 * log.error("************* categories"+categories); JSONObject jObject = new
		 * JSONObject(); // HercLocationsServlet service =
		 * getSlingScriptHelper().getService(HercLocationsServlet.class);
		 * HercLocationsService service =
		 * getSlingScriptHelper().getService(HercLocationsService.class); if(null !=
		 * service){ log.error("service is not null"); ResourceResolverFactory
		 * resourceResolverFactory =
		 * getSlingScriptHelper().getService(ResourceResolverFactory.class);
		 * ResourceResolver administrativeResourceResolver =
		 * resourceResolverFactory.getAdministrativeResourceResolver(null); Session
		 * adminSession = administrativeResourceResolver.adaptTo(Session.class); jObject
		 * = service.getProximityBranches(locationFilter,categories,getResponse(),
		 * adminSession); } else { log.error("service is null");
		 * 
		 * } branchCode = new ArrayList<String>(); myHashMap = new LinkedHashMap<String,
		 * Map<String, String> >(); // JSONObject jObject =
		 * service.getNearByBranches(getRequest(), getResponse());;
		 * //log.error("activate end"+jObject.toString()); totalResults =
		 * jObject.getString("totalresults");
		 * log.error("totalresults end  ==== "+totalResults); JSONArray
		 * arr=jObject.getJSONArray("records"); for(int i=0;i<arr.length();i++) {
		 * JSONObject jobj=(JSONObject) arr.get(i); Map<String, String> valueMap = new
		 * HashMap<String, String>();
		 * 
		 * log.error(jobj.get("BRANCH_CODE")+""); log.error(jobj.get("BRANCH_NAME")+"");
		 * log.error(jobj.get("CATEGORIES")+""); valueMap.put("BRANCH_CODE",
		 * jobj.getString("BRANCH_CODE")); valueMap.put("BRANCH_NAME",
		 * jobj.getString("BRANCH_NAME"));
		 * valueMap.put("BRANCH_DESCRIPTION",jobj.getString("BRANCH_DESCRIPTION"));
		 * valueMap.put("CITY",jobj.getString("CITY"));
		 * valueMap.put("STATE",jobj.getString("STATE"));
		 * valueMap.put("ADRESS1",jobj.getString("ADRESS1"));
		 * valueMap.put("ADRESS2",jobj.getString("ADRESS2"));
		 * valueMap.put("LATITUDE",jobj.getString("LATITUDE"));
		 * valueMap.put("LONGITUDE",jobj.getString("LONGITUDE"));
		 * valueMap.put("DISTANCE",jobj.getString("DISTANCE"));
		 * 
		 * JSONArray categoryArr=jobj.getJSONArray("CATEGORIES"); for(int
		 * k=0;k<categoryArr.length();k++) {
		 * log.error("*************"+categoryArr.getString(k)); }
		 * log.error(jobj.get("BRANCH_DESCRIPTION")+""); log.error(jobj.get("CITY")+"");
		 * log.error(jobj.get("STATE")+""); log.error(jobj.get("ADRESS1")+"");
		 * log.error(jobj.get("ADRESS2")+""); log.error(jobj.get("LATITUDE")+"");
		 * log.error(jobj.get("LONGITUDE")+""); log.error(jobj.get("DISTANCE")+"");
		 * myHashMap.put(jobj.getString("BRANCH_CODE"), valueMap);
		 * branchCode.add(jobj.getString("BRANCH_CODE")); } if(branchId!=null){
		 * myValueMap = myHashMap.get(branchId); } } catch(Exception e) {
		 * log.error("Exception thrown in WCMUSE"); }
		 *///}

	public String getTotalResults(){
		return totalResults ;
	}

    public  Map<String,  Map<String, String>> getResultsMap(){
        return myHashMap;
    }
    
    public  ArrayList<String> getBranchCode(){
        return branchCode;
    }
    
    public  Map<String, String> getValueMap(){
    	return myValueMap;
    }

}
