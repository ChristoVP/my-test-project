/**
 * 
 */
package com.herc.ewcm.core.common;

/**
 * @author VE332669
 *
 */
public class HercServicesConstants {
	public static final String MY_SQL_TABLES_PATH = "/etc/system/mysqltables";
	public static final String TABLE_NAME_SOLUTION = "solution";
	public static final String API_GETSOLUTIONS = "getSolutions";
	public static final String API_JOBSITECREATION = "JobSiteCreation";
	public static final String TABLE_NAME_SOLUTION_EQUIPMENT = "solution_equipment";
	public static final String TABLE_NAME_JOBSITE = "my_jobsite_albums";
	public static final String TABLE_NAME_MASTER_PRODUCT_LIST = "master_product_list";
	public static final String TABLE_NAME_BRANCH_DETAILS = "branch_details";
	public static final String DAM_HERC_PATH = "/content/dam/herc";
	public static final String TABLE_LOCATION_ZIP_QRY = "/jcr:root/etc/system/mysqltables/locations//element(*,herc:Record)[@ZIP_POSTAL_CODE ='";
	public static final String QUERY_BRANCH_TYPE = "/jcr:root/etc/system/mysqltables/branch_details//element(*,herc:Record)[@BRANCH_TYPE='";
	public static final String QUERY_BRANCH_TYPE_LOC = "/jcr:root/etc/system/mysqltables/locations//element(*,herc:Record)[@BRANCH_TYPE='";
	public static final String QUERY_BRANCH_ID = "/jcr:root/etc/system/mysqltables/branch_details//element(*,herc:Record)[@BRANCH_ID='";
	public static final String QUERY_BRANCH_DETAILS_BRANCH_NUMBER = "/jcr:root/etc/system/mysqltables/branch_details//element(*,herc:Record)[@BRANCH_NUMBER='";
	public static final String QUERY_BRANCH_DETAILS_CA_BRANCH_NUMBER = "/jcr:root/etc/system/mysqltables/branch_details//element(*,herc:Record)[@CA_BRANCH_NUMBER='";
	public static final String QUERY_MASTER_PRODUCT_LIST_ID = "/jcr:root/etc/system/mysqltables/product_item_specification//element(*,herc:Record)[@EQUIPMENT_NAME='";
	public static final String QUERY_MASTER_CAT_CLASS = "/jcr:root/etc/system/mysqltables/product_item_specification//element(*,herc:Record)[@";
	public static final String QUERY_ITEM_DETAILS = "/jcr:root/etc/system/mysqltables/item_details//element(*,herc:Record)[@SUBCATEGORY2='";
	public static final String MAKE_MODEL_DETAILS = "/jcr:root/etc/system/mysqltables/makes_and_models//element(*,herc:Record)[@CAT='";
	public static final String QUERY_JOBSITE_ALBUM_NAME = "/jcr:root/etc/system/mysqltables/my_jobsite_albums//element(*,herc:Record)[@AUTO_GENERATED_NUMBER='";
	public static final String QUERY_GET_SOLUTION = "/jcr:root/etc/system/mysqltables/solution//element(*,herc:Record)[@SOLUTION_COUNTRY='";
	public static final String QUERY_GET_SOLUTION_SOLUTIONCOUNTRY = "/jcr:root/etc/system/mysqltables/solution//element(*,herc:Record)[jcr:contains('SOLUTION_COUNTRY',";

	public static final String QUERY_GET_SOLUTIONS_SOL = "/jcr:root/etc/system/mysqltables/solution//element(*,herc:Record)[@SOLUTION_NAME='";
	public static final String TABLE_MY_PREFERENCE_FAV_QRY = "/jcr:root/etc/system/mysqltables/my_preference//element(*,herc:Record)[@AUTO_GENERATED_NUMBER = '";
	public static final String TABLE_NAME_ITEM_DETAILS = "item_details";
	public static final String TABLE_NAME_LOCATIONS_EXISTS = "/jcr:root/etc/system/mysqltables/locations//element(*,herc:Record) order by @jcr:score";
	public static final String TABLE_NAME_MY_PREFERENCE = "my_preference";
	public static final String QUERY_STATEWISE_BRANCH_LIST = "/jcr:root/etc/system/mysqltables/branch_details//element(*,herc:Record)[";
	public static final String CATEGORY_VALUE_YES = "Y";
	public static final String CATEGORY_VALUE_NO = "N";
	public static final String JOBSITE_USERNAME = "UserName";
	public static final String JOBSITE_FOLDERNAME = "FolderName";
	public static final String JOBSITE_NEW_FOLDERNAME = "NewFolderName";
	public static final String JOBSITE_ACTION = "Action";
	public static final String FILE_PATH = "FilePath";
	public static final String JOBSITE_ACTION_MODIFY = "modify";
	public static final String BRANCH_ID = "BranchID";
	public static final String FLAG = "Flag";
	public static final String PHONENUMBER = "_PHONE_NUMBER";

	// For getCatalogDetails
	public static final String BROWSECATALOG_CATALOGNAME = "Name";
	public static final String BROWSECATALOG_TYPE = "Type";
	public static final String DEVICE_TYPE = "DeviceType";
	public static final String BROWSECATALOG_CAT = "EquipCat";
	public static final String BROWSECATALOG_CLASS = "EquipClass";
	public static final String BROWSECATALOG_COUNTRY = "Country";

	public static final String TABLE_NAME_PRODUCT_ITEM = "product_item_specification";
	public static final String PARAMETER_VALUE_CATEGORY = "category";
	public static final String PARAMETER_VALUE_SUBCATEGORY = "subcategory";
	public static final String PARAMETER_VALUE_EQUIPMENT = "equipment";

	public static final String QUERY_PRODUCT_ITEM = "/jcr:root/etc/system/mysqltables/product_item_specification//element(*,herc:Record)";
	public static final String QUERY_PRODUCT_ITEM_CATEGORY = "[@MAJOR_CATEGORIES='";
	public static final String QUERY_PRODUCT_ITEM_SUBCATEGORY = "[@SUB_CATEGORY_NAME='";
	public static final String QUERY_PRODUCT_ITEM_EQUIPMENT = "[@EQUIPMENT_NAME='";

	public static final String QUERY_PRODUCT_ITEM_CAT_CN = "[@CAT_CN='";
	public static final String QUERY_PRODUCT_ITEM_CAT = "[@CAT_US='";
	public static final String QUERY_PRODUCT_ITEM_CLASS_CN = "@CLASS_CN='";
	public static final String QUERY_PRODUCT_ITEM_CLASS = "@CLASS_US='";

	public static final String PRODUCT_ITEM_COLUMN_SUB_CATEGORY_NAME = "SUB_CATEGORY_NAME";
	public static final String PRODUCT_ITEM_COLUMN_EQUIPMENT_NAME = "EQUIPMENT_NAME";
	public static final String PRODUCT_ITEM_COLUMN_EQUIPMENT_IMAGE = "EQUIPMENT_IMAGE";
	public static final String PRODUCT_ITEM_COLUMN_EQUIPMENT_LIST_VIEW_DESCRIPTION = "EQUIPMENT_LIST_VIEW_DESCRIPTION";
	public static final String PRODUCT_ITEM_COLUMN_EQUIPMENT_LIST_VIEW_SUB_DESCRIPTION = "EQUIPMENT_LIST_VIEW_SUB_DESCRIPTION";
	public static final String PRODUCT_ITEM_COLUMN_EQUIPMENT_DESCRIPTION = "EQUIPMENT_DESCRIPTION";

	public static final String PRODUCT_ITEM_COLUMN_SPEC_1 = "US_SPEC_1";
	public static final String PRODUCT_ITEM_COLUMN_SPEC_2 = "US_SPEC_2";
	public static final String PRODUCT_ITEM_COLUMN_SPEC_3 = "US_SPEC_3";

	public static final String PRODUCT_ITEM_COLUMN_SPEC_1_CN = "CN_SPEC_1";
	public static final String PRODUCT_ITEM_COLUMN_SPEC_2_CN = "CN_SPEC_2";
	public static final String PRODUCT_ITEM_COLUMN_SPEC_3_CN = "CN_SPEC_3";
	public static final String PRODUCT_ITEM_COLUMN_CLASS_CN = "CLASS_CN";
	public static final String PRODUCT_ITEM_COLUMN_CAT_CN = "CAT_CN";
	public static final String PRODUCT_ITEM_COLUMN_CAT_US = "CAT_US";
	public static final String PRODUCT_ITEM_COLUMN_CLASS_US = "CLASS_US";

	public static final String NULL_VALUE = "NULL";
	public static final String EMPTY_STRING = "";
	public static final String BLANK_SPACE = " ";
	public static final String SCHEDULER_QUERY_SELECT = "Select MAJOR_CATEGORIES,SUB_CATEGORY_NAME,EQUIPMENT_NAME,SPECIFICATION_ID from PRODUCT_ITEM_SPECIFICATION where SPECIFICATION_ID != 2148";
	public static final String SCHEDULER_DAM_PATH = "/content/dam/herc/product-item-specification/";
	public static final String SCHEDULER_DAM_PATH_EXTENSION = ".jpg";
	public static final String SCHEDULER_QUERY_UPDATE = "update DEV_HERC_MYSQL_DB.PRODUCT_ITEM_SPECIFICATION set SOLR_EQUIP_IMAGE='";
	public static final String SCHEDULER_QUERY_UPDATE1 = "' where SPECIFICATION_ID='";
	public static final String SCHEDULER_QUERY_UPDATE2 = "';";
	public static final String SCHEDULER_QUERY_UPDATE_DEFAULT = "	update DEV_HERC_MYSQL_DB.PRODUCT_ITEM_SPECIFICATION set SOLR_EQUIP_IMAGE='/content/dam/herc/product-item-specification/default/image/equipment_default.jpg' where SPECIFICATION_ID='";
	public static final String SCHEDULER_SUBCAT_QUERY_UPDATE = "update DEV_HERC_MYSQL_DB.PRODUCT_ITEM_SPECIFICATION set SOLR_SUBCAT_IMAGE='";

	public static final String SCHEDULER_SUBCAT_QUERY_UPDATE_DEFAULT = "	update DEV_HERC_MYSQL_DB.PRODUCT_ITEM_SPECIFICATION set SOLR_SUBCAT_IMAGE='/content/dam/herc/product-item-specification/default/image/subcategory_default.jpg' where SPECIFICATION_ID='";
	//

	// For getBranchInfo API
	public static final String PARAMETER_ZIPCODE = "ZipCode";
	public static final String QUERY_ZIPCODE = "/jcr:root/etc/system/mysqltables/branch_details//element(*,herc:Record)[@ZIP='";
	public static final String QUERY_ORDER_BY = "'] order by @jcr:score";
	public static final String QUERY_ORDER_BY_SUBCAT = "'] order by @SUB_CATEGORY_NAME";

	public static final String QUERY_ORDER_BY_EQUIPMENT = "'] order by @EQUIPMENT_NAME";
	public static final String QUERY_ORDER_BY_SPEC1 = "'] order by @SPEC1_VALUE";
	public static final String QUERY_ORDER_BY_SPEC1_CN = "'] order by @SPEC1_VALUE_CN";
	public static final String QUERY_ORDER_BY_EXP = "'] order by @EXPIRATION_DATE";

	// for getNearBYBranch API
	public static final String PARAMETER_ZIPCODES = "ZipCodes";
	public static final String QUERY_ZIPCODES = "' or @ZIP ='";

	// for getFavouriteBranch
	public static final String PARAMETER_USERNAME = "UserName";
	public static final String PARAMETER_CATEGORIES = "Categories";
	public static final String QUERY_BRANCH_LIST = "/jcr:root/etc/system/mysqltables/branch_details//element(*,herc:Record)[";
	public static final String BRANCH_NUMBER_PROPERTY_EQUALTO = "@BRANCH_NUMBER='";
	public static final String ORDER_BY_JCR_SCORE = "] order by @jcr:score";
	// for getBranchDetails API
	public static final String PARAMETER_BRANCH_NUMBER = "BranchNumber";

	// setFavoriteBRanch
	public static final String QUERY_BRANCH_NUMBER_FAV = "' and @BRANCH_NUMBER='";
	public static final String MYSQL_QUERY_MYPREFERENCE = "Insert into DEV_HERC_MYSQL_DB.MY_PREFERENCE (MY_PREFERENCE_ID,USERNAME,BRANCH_NUMBER) VALUES('";
	public static final String MYSQL_QUERY_MYPREFERENCE1 = "','";
	public static final String MYSQL_QUERY_MYPREFERENCE_DELETE = "delete from DEV_HERC_MYSQL_DB.MY_PREFERENCE where BRANCH_NUMBER=";
	public static final String MYSQL_QUERY_MYPREFERENCE_DELETE1 = " and USERNAME='";
	public static final String MYSQL_QUERY_MYPREFERENCE_DELETE2 = "'";

	// createJobsiteMobile API
	public static final String MYSQL_QUERY_JOBSITEALBUMS = "insert into DEV_HERC_MYSQL_DB.MY_JOBSITE_ALBUMS (JOBSITE_ALBUMS_ID,USERNAME,JOBSITE_ALBUM_NAME) values('";
	public static final String MYSQL_QUERY_JOBSITEALBUMS1 = "','";
	public static final String MYSQL_QUERY_JOBSITEALBUMS2 = "');";
	public static final String MYSQL_QUERY_JOBSITEALBUMS_DEL = "delete from DEV_HERC_MYSQL_DB.MY_JOBSITE_ALBUMS where USERNAME='";
	public static final String MYSQL_QUERY_JOBSITEALBUMS_DEL1 = "'and JOBSITE_ALBUM_NAME='";
	public static final String MYSQL_QUERY_JOBSITEALBUMS_DEL2 = "'";
	public static final String MYSQL_QUERY_JOBSITEALBUMS_MOD = "update DEV_HERC_MYSQL_DB.MY_JOBSITE_ALBUMS set JOBSITE_ALBUM_NAME='";
	public static final String MYSQL_QUERY_JOBSITEALBUMS_MOD1 = "' where JOBSITE_ALBUM_NAME='";
	public static final String MYSQL_QUERY_JOBSITEALBUMS_MOD2 = "' and USERNAME='";

	// getSolutions API
	public static final String PARAMETER_COUNTRY_CODE = "CountryCode";

	// getsolutionDetails API
	public static final String PARAMETER_SOLUTION_NAME = "SolutionName";
	public static final String QUERY_GET_SOL_PRODUCT_ITEM = "/jcr:root/etc/system/mysqltables/product_item_specification//element(*,herc:Record)[@";
	public static final String QUERY_GET_SOL_PRODUCT_ITEM_END = "='Y'] order by @jcr:score";

	public static final String BRANCH_COLUMN_BRANCH_NUMBER = "BRANCH_NUMBER";
	public static final String BRANCH_COLUMN_BRANCH_NAME = "BRANCH_NAME";
	public static final String BRANCH_COLUMN_BRANCH_IMG_URL = "BRANCH_IMG_URL";
	public static final String BRANCH_COLUMN_INFO_CONTENT = "INFO_CONTENT";
	public static final String BRANCH_COLUMN_CITY = "CITY";
	public static final String BRANCH_COLUMN_STATE_NAME = "STATE_NAME";
	public static final String BRANCH_COLUMN_ADRESS1 = "ADRESS1";
	public static final String BRANCH_COLUMN_BRANCH_TYPE = "BRANCH_TYPE";
	public static final String BRANCH_COLUMN_BRANCH_PHONE = "BRANCH_PHONE";
	public static final String BRANCH_COLUMN_ADRESS2 = "ADRESS2";
	public static final String BRANCH_COLUMN_ZIP = "ZIP";
	public static final String BRANCH_COLUMN_PHONE1 = "PHONE1";
	public static final String BRANCH_COLUMN_PHONE2 = "PHONE2";
	public static final String BRANCH_COLUMN_FAX = "FAX";
	public static final String BRANCH_COLUMN_BRANCH_MANAGER = "BRANCH_MANAGER";
	public static final String BRANCH_COLUMN_BRANCH_MANAGER_PHONE = "BRANCH_MANAGER_PHONE";
	public static final String BRANCH_COLUMN_BRANCH_MANAGER_EMAIL = "BRANCH_MANAGER_EMAIL";

	// constants for constructing the image paths.

	public static final String DAM_PATH_SOLUTIONS = "/content/dam/herc/solutions/";
	public static final String DAM_PATH_SOLUTIONS_DEAULT_ICON_IMAGE = "/content/dam/herc/solution/default/solution_default_icon.png";
	public static final String DAM_PATH_EQUIPMENTS = "/content/dam/herc/product-item-specification/category/sub_category/equipments/";
	public static final String DAM_PATH_EQUIPMENTS_DEFAULT_IMAGE = "/content/dam/herc/product-item-specification/default/equipment_default.png";
	public static final String DAM_PATH_EQUIPMENTS_DEFAULT_ICON_IMAGE = "/content/dam/herc/product-item-specification/default/equipment_default_icon.png";
	public static final String DAM_PATH_SUBCATEGORIES = "/content/dam/herc/product-item-specification/category/sub_category";
	public static final String DAM_PATH_SUBCATEGORIES_DEFAULT_IMAGE = "/content/dam/herc/product-item-specification/category/sub_category/default/sub_category_default_icon.png";
	public static final String DAM_PATH_SUBCATEGORIES_DEFAULT_ICON_IMAGE = "/content/dam/herc/product-item-specification/default/subcategory_default_icon.png";
	public static final String DAM_PATH_BRANCH_DETAILS_DEFAULT_IMAGE = "/content/dam/herc/branch_details/default/branch_default.png";
	public static final String DAM_PATH_BRANCH_DETAILS_DEFAULT_ICON_IMAGE = "/content/dam/herc/branch_details/default/branch_deault_icon.png";
	public static final String DAM_PATH_PRODUCT_ITEM_SPECIFICATION = "/content/dam/herc/product-item-specification/";
	public static final String COLUMN_MAJOR_CATEGORIES = "MAJOR_CATEGORIES";
	public static final String DAM_PATH_BRANCH_DETAILS = "/content/dam/herc/branch-details/";
	public static final String HERC_LABEL = "herc";
	public static final String DAM_PATH_IMAGES_ICON = "/images/icons/";
	public static final String DAM_PATH_IMAGES = "/image/";
	public static final String DAM_PATH_BANNER = "/banner/";
	public static final String DAM_PATH_ICON_PNG_FILE_EXTENSION = "_icon.png";
	public static final String IOS_DEVICE = "ios";
	public static final String ANDROID_DEVICE = "android";
	public static final String IOS_PNG_308_308 = "_308.308_ios.png";
	public static final String IOS1_PNG_308_308 = "cq5dam.thumbnail.308.308.png";
	public static final String ANDROID_PNG_360_360 = "_360.360_android.png";
	public static final String ANDROID1_PNG_360_360 = "cq5dam.thumbnail.360.360.png";

	public static final String IOS_PNG_1242_497 = "_1242.497_ios.png";
	public static final String IOS1_PNG_1242_497 = "cq5dam.thumbnail.1242.497.png";
	public static final String ANDROID_PNG_1440_576 = "_1440.576_android.png";
	public static final String ANDROID1_PNG_1440_576 = "cq5dam.thumbnail.1440.576.png";
	public static final String DAM_PATH_IMAGE_PNG_FILE_EXTENSION = ".png";
	public static final String SPACE = " ";
	public static final String COMMA = ",";
	public static final String DOUBLE_UNDERSCORE = "__";
	public static final String HYPEN = "-";
	public static final String UNDERSCORE = "_";
	public static final String UNDERSCORE_AND_UDERSCORE = "_and_";
	public static final String SPACE_AND_SPACE = " and ";
	public static final String UNDERSCORE_UDERSCORE = "_&_";
	public static final String SLASH = "/";
	public static final String IOS = "ios";
	/**
	 * underscore replaced with hyphen for mobile and web.
	 */
    public static final String DOUBLE_HYPHEN = "--";      
    public static final String HYPHEN = "-";
    public static final String HYPHEN_AND_HYPHEN = "-and-";     
    public static final String HYPHEN_HYPHEN = "-&-";

	
	/**
	 * 
	 */

	public static final String SIZE_CHECK_QUERY = "/jcr:root/content/dam/herc/my_jobsite_albums/";

	// for Loyalty service API's

	// for earnPoint and getEarnPoints
	public static final String PARAMETER_VALUE_LOYALTY_REWARDS_TRIGGER = "RewardsTrigger";
	public static final String PARAMETER_VALUE_LOYALTY_USER_NAME = "UserName";
	public static final String PARAMETER_VALUE_LOYALTY_POINTS = "Points";

	public static final String TABLE_NAME_MY_LOYALTY_REWARDS = "my_loyalty_rewards";
	public static final String TABLE_NAME_REWARDS_LOOKUP = "rewards_lookup";
	public static final String REWARDS_LOOKUP_COLUMN_POINTS = "POINTS";
	public static final String MY_LOYALTY_REWARDS_COLUMN_MYREWARDS_ID = "MYREWARDS_ID";
	public static final String MY_LOYALTY_REWARDS_COLUMN_USERNAME = "USERNAME";
	public static final String MY_LOYALTY_REWARDS_COLUMN_POINTS = "POINTS";
	public static final String MY_LOYALTY_REWARDS_COLUMN_START_DATE = "START_DATE";
	public static final String MY_LOYALTY_REWARDS_COLUMN_EXPIRATION_DATE = "EXPIRATION_DATE";
	public static final int MY_LOYALTY_REWARDS_EXPIRATION_PERIOD_TENNURE = 5;

	public static final String MY_LOYALTY_REWARDS_COLUMN_REDEEMED = "REDEEMED";
	public static final String QUERY_REWARDS_LOOKUP = "/jcr:root/etc/system/mysqltables/rewards_lookup//element(*,herc:Record)";
	public static final String QUERY_MY_LOYALTY_REWARDS = "/jcr:root/etc/system/mysqltables/my_loyalty_rewards//element(*,herc:Record)";
	public static final String QUERY_MY_LOYALTY_REWARDS_REWARDS_TRIGGER = "[@REWARDS_TRIGGER='";
	public static final String QUERY_MY_LOYALTY_REWARDS_USERNAME = "[@AUTO_GENERATED_NUMBER='";

	// for redeemPoint

	public static final String TABLE_NAME_SWAG_DETAILS = "swag_details";
	public static final String QUERY_SWAG_DETAILS = "/jcr:root/etc/system/mysqltables/swag_details//element(*,herc:Record)";

	public static final String SWAG_ITEM_IMAGE_PATH_START = "/content/dam/herc/incentive/swag_details/";
	public static final String SWAG_ITEM_IMAGE_PATH_END = "_icon.png";
	public static final String SWAG_ITEM_NAME = "ItemName";
	public static final String SWAG_QUANTITY = "Quantity";
	public static final String SWAG_RECEIVER_NAME = "ReceiverName";
	public static final String SWAG_RECEIVER_COMPANY = "ReceiverCompany";
	public static final String SWAG_RECEIVER_ADDRESS = "ReceiverAddress";
	public static final String SWAG_RECEIVER_PHONE_NUMBER = "ReceiverPhoneNumber";
	public static final String SWAG_RECEIVER_NOTES = "ReceiverNotes";

	// Email Notification.
	public static final String DEFAULT_CHARSET = "utf-8";
	public static final String REDEEM_POINTS_NOTIFICATION = "Redeem Points Notification";
	public static final String STATUS_CODE = "Status Code";
	public static final String STATUS_MESSAGE = "Status Message";

	// Images Rendering

	public static final String BRANCH_DEFAULT_THUMBNAIL_IOS_PNG_PATH_US = "/content/dam/herc/branch-details/default/image/branch-default_us.jpg/jcr:content/renditions/branch-default_us_308.308_ios.png";
	public static final String BRANCH_DEFAULT_THUMBNAIL_IOS_PNG_PATH_CA = "/content/dam/herc/branch-details/default/image/branch-default_ca.jpg/jcr:content/renditions/branch-default_ca_308.308_ios.png";
	public static final String BRANCH_DEFAULT_THUBNNAIL_ANDROID_PNG_PATH_US = "/content/dam/herc/branch-details/default/image/branch-default_us.jpg/jcr:content/renditions/branch-default_us_360.360_android.png";
	public static final String BRANCH_DEFAULT_THUBNNAIL_ANDROID_PNG_PATH_CA = "/content/dam/herc/branch-details/default/image/branch-default_ca.jpg/jcr:content/renditions/branch-default_ca_360.360_android.png";
	public static final String BRANCH_DEFAULT_HEADER_ANDROID_PNG_PATH_US = "/content/dam/herc/branch-details/default/banner/branch-default_us.jpg/jcr:content/renditions/branch-default_us_1440.576_android.png";
	public static final String BRANCH_DEFAULT_HEADER_ANDROID_PNG_PATH_CA = "/content/dam/herc/branch-details/default/banner/branch-default_ca.jpg/jcr:content/renditions/branch-default_ca_1440.576_android.png";
	public static final String BRANCH_DEFAULT_HEADER_IOS_PNG_PATH_US = "/content/dam/herc/branch-details/default/banner/branch-default_us.jpg/jcr:content/renditions/branch-default_us_1242.497_ios.png";
	public static final String BRANCH_DEFAULT_HEADER_IOS_PNG_PATH_CA = "/content/dam/herc/branch-details/default/banner/branch-default_ca.jpg/jcr:content/renditions/branch-default_ca_1242.497_ios.png";

	public static final String SOLUTION_DEFAULT_THUMBNAIL_IOS_PNG_PATH = "/content/dam/herc/solutions/default/image/solution-default.jpg/jcr:content/renditions/solution-default_308.308_ios.png";
	public static final String SOLUTION_DEFAULT_THUMBNAIL_ANDROID_PNG_PATH = "/content/dam/herc/solutions/default/image/solution-default.jpg/jcr:content/renditions/solution-default_360.360_android.png";
	public static final String SOLUTION_DEFAULT_HEADER_IOS_PNG_PATH = "/content/dam/herc/solutions/default/banner/solution-default.jpg/jcr:content/renditions/solution-default_1242.497_ios.png";
	public static final String SOLUTION_DEFAULT_HEADER_ANDROID_PNG_PATH = "/content/dam/herc/solutions/default/banner/solution-default.jpg/jcr:content/renditions/solution-default_1440.576_android.png";

	public static final String CATEGORY_DEFAULT_THUMBNAIL_ANDROID_PNG_PATH = "/content/dam/herc/product-item-specification/default/image/equipment-default.jpg/jcr:content/renditions/cq5dam.thumbnail.360.360.png";
	public static final String CATEGORY_DEFAULT_THUMBNAIL_IOS_PNG_PATH = "/content/dam/herc/product-item-specification/default/image/equipment-default.jpg/jcr:content/renditions/cq5dam.thumbnail.308.308.png";
	public static final String SUB_CATEGORY_DEFAULT_THUMBNAIL_ANDROID_PNG_PATH = "/content/dam/herc/product-item-specification/default/image/equipment-default.jpg/jcr:content/renditions/cq5dam.thumbnail.360.360.png";
	public static final String SUB_CATEGORY_DEFAULT_THUMBNAIL_IOS_PNG_PATH = "/content/dam/herc/product-item-specification/default/image/equipment-default.jpg/jcr:content/renditions/cq5dam.thumbnail.308.308.png";
	public static final String EQUIPMENT_DEFAULT_THUMBNAIL_ANDROID_PNG_PATH = "/content/dam/herc/product-item-specification/default/image/equipment-default.jpg/jcr:content/renditions/cq5dam.thumbnail.360.360.png";
	public static final String EQUIPMENT_DEFAULT_THUMBNAIL_IOS_PNG_PATH = "/content/dam/herc/product-item-specification/default/image/equipment-default.jpg/jcr:content/renditions/cq5dam.thumbnail.308.308.png";
	public static final String CATEGORY_DEFAULT_HEADER_ANDROID_PNG_PATH = "/content/dam/herc/product-item-specification/default/image/equipment-default.jpg/jcr:content/renditions/equipment-default_1440.576_android.png";
	public static final String CATEGORY_DEFAULT_HEADER_IOS_PNG_PATH = "/content/dam/herc/product-item-specification/default/image/equipment-default.jpg/jcr:content/renditions/equipment-default_1242.497_ios.png";
	public static final String SUB_CATEGORY_DEFAULT_HEADER_ANDROID_PNG_PATH = "/content/dam/herc/product-item-specification/default/image/equipment-default.jpg/jcr:content/renditions/equipment-default_1440.576_android.png";
	public static final String SUB_CATEGORY_DEFAULT_HEADER_IOS_PNG_PATH = "/content/dam/herc/product-item-specification/default/image/equipment-default.jpg/jcr:content/renditions/equipment-default_1242.497_ios.png";
	public static final String EQUIPMENT_DEFAULT_HEADER_ANDROID_PNG_PATH = "/content/dam/herc/product-item-specification/default/image/equipment-default.jpg/jcr:content/renditions/equipment-default_1440.576_android.png";
	public static final String EQUIPMENT_DEFAULT_HEADER_IOS_PNG_PATH = "/content/dam/herc/product-item-specification/default/image/equipment-default.jpg/jcr:content/renditions/equipment-default_1242.497_ios.png";

	// public static final String BRANCH_DEFAULT_THUMBNAIL_IOS_PNG_PATH =
	// "/content/dam/herc/branch_details/default/image/branch_default.jpg/jcr:content/renditions/branch_default_308.308_ios.png";
	// public static final String BRANCH_DEFAULT_THUBNNAIL_ANDROID_PNG_PATH =
	// "/content/dam/herc/branch_details/default/image/branch_default.jpg/jcr:content/renditions/branch_default_360.360_android.png";
	// public static final String BRANCH_DEFAULT_HEADER_ANDROID_PNG_PATH =
	// "/content/dam/herc/branch_details/default/banner/branch_default.jpg/jcr:content/renditions/branch_default_1440.576_android.png";
	// public static final String BRANCH_DEFAULT_HEADER_IOS_PNG_PATH =
	// "/content/dam/herc/branch_details/default/banner/branch_default.jpg/jcr:content/renditions/branch_default_1242.497_ios.png";

	// public static final String SOLUTION_DEFAULT_THUMBNAIL_IOS_PNG_PATH =
	// "/content/dam/herc/solution/default/image/solution_default.jpg/jcr:content/renditions/solution_default_308.308_ios.png";
	// public static final String SOLUTION_DEFAULT_THUMBNAIL_ANDROID_PNG_PATH =
	// "/content/dam/herc/solution/default/image/solution_default.jpg/jcr:content/renditions/solution_default_360.360_android.png";
	// public static final String SOLUTION_DEFAULT_HEADER_IOS_PNG_PATH =
	// "/content/dam/herc/solution/default/solution_default.jpg/jcr:content/renditions/solution_default_1242.497_ios.png";
	// public static final String SOLUTION_DEFAULT_HEADER_ANDROID_PNG_PATH =
	// "/content/dam/herc/solution/default/solution_default.jpg/jcr:content/renditions/solution_default_1440.576_android.png";

	// public static final String CATEGORY_DEFAULT_THUMBNAIL_ANDROID_PNG_PATH =
	// "/content/dam/herc/product-item-specification/default/category-default.jpg/jcr:content/renditions/category-default_360.360_android.png";
	// public static final String CATEGORY_DEFAULT_THUMBNAIL_IOS_PNG_PATH =
	// "/content/dam/herc/product-item-specification/default/category-default.jpg/jcr:content/renditions/category-default_308.308_ios.png";
	// public static final String
	// SUB_CATEGORY_DEFAULT_THUMBNAIL_ANDROID_PNG_PATH =
	// "/content/dam/herc/product-item-specification/default/subcategory-default.jpg/jcr:content/renditions/subcategory-default_360.360_android.png";
	// public static final String SUB_CATEGORY_DEFAULT_THUMBNAIL_IOS_PNG_PATH =
	// "/content/dam/herc/product-item-specification/default/subcategory-default.png/jcr:content/renditions/cq5dam.thumbnail.308.308.png";
	// public static final String EQUIPMENT_DEFAULT_THUMBNAIL_ANDROID_PNG_PATH =
	// "/content/dam/herc/product-item-specification/default/equipment-default.jpg/jcr:content/renditions/equipment-default_360.360_android.png";
	// public static final String EQUIPMENT_DEFAULT_THUMBNAIL_IOS_PNG_PATH =
	// "/content/dam/herc/product-item-specification/default/equipment-default.jpg/jcr:content/renditions/equipment-default_308.308_ios.png";
	// public static final String CATEGORY_DEFAULT_HEADER_ANDROID_PNG_PATH =
	// "/content/dam/herc/product-item-specification/default/category-default.jpg/jcr:content/renditions/category-default_1440.576_android.png";
	// public static final String CATEGORY_DEFAULT_HEADER_IOS_PNG_PATH =
	// "/content/dam/herc/product-item-specification/default/category-default.jpg/jcr:content/renditions/category-default_1242.497_ios.png";
	// public static final String SUB_CATEGORY_DEFAULT_HEADER_ANDROID_PNG_PATH =
	// "/content/dam/herc/product-item-specification/default/subcategory-default.jpg/jcr:content/renditions/subcategory-default_1440.576_android.png";
	// public static final String SUB_CATEGORY_DEFAULT_HEADER_IOS_PNG_PATH =
	// "/content/dam/herc/product-item-specification/default/subcategory-default.jpg/jcr:content/renditions/subcategory-default_1242.497_ios.png";
	// public static final String EQUIPMENT_DEFAULT_HEADER_ANDROID_PNG_PATH =
	// "/content/dam/herc/product-item-specification/default/banner/equipment-default.jpg/jcr:content/renditions/equipment-default_1440.576_android.png";
	// public static final String EQUIPMENT_DEFAULT_HEADER_IOS_PNG_PATH =
	// "/content/dam/herc/product-item-specification/default/banner/equipment-default.jpg/jcr:content/renditions/equipment-default_1242.497_ios.png";
	public static final String JPG_EXTN = ".jpg";
	public static final String JCRCONTENT_RENDITIONS = "/jcr:content/renditions/";

	// Scheduler
	public static final String QUERY_SCHEDULER_UPDATE = "update ";
	public static final String QUERY_SCHEDULER_SET = " set FOLDER='";
	public static final String QUERY_SCHEDULER_WHERE = "' where ID='";
	public static final String QUERY_SCHEDULER_END = "'";
	public static final String FOLDER = "FOLDER";
	public static final String ID = "ID";
	public static final String QUERY_SCHEDULER_SELECT = "select * from ";
	public static final String DOT = ".";
	public static final int SIZE = 500;
	public static final String QUERY_SCHEDULER_SELECT_WHERE = " where ID='";

	public static final String ETRIVE_GETDASHBOARDSERVICE_CUSTOMERINFO_URL = "http://qaetrieve.hercrentals.com/EtrieveItRestService/getDashBoardService/customerInfo";
	public static final String ETRIVE_GETDASHBOARDSERVICE_LICENSEINFO_URL = "http://qaetrieve.hercrentals.com/EtrieveItRestService/getDashBoardService/licenseInfo";
	public static final String ETRIVE_GETINVOICEAGINGSERVICE_URL = "http://qaetrieve.hercrentals.com/EtrieveItRestService/getInvoiceAgingService";
	public static final String ETRIVE_GETUNIQUECONTRACTDETAILSERVICE_CUSTOMERINFO_URL = "http://qaetrieve.hercrentals.com/EtrieveItRestService/getUniqueContractDetailService/customerInfo";
	public static final String ETRIVE_GETUNIQUECONTRACTDETAILSERVICE_LICENSEINFO_URL = "http://qaetrieve.hercrentals.com/EtrieveItRestService/getUniqueContractDetailService/licenseInfo";
	public static final String ETRIVE_GETOPENRENTALSDETAILSSERVICE_CUSTOMERINFO_URL = "http://qaetrieve.hercrentals.com/EtrieveItRestService/getOpenRentalsDetailsService/customerInfo";
	public static final String ETRIVE_GETOPENRENTALSDETAILSSERVICE_LICENSEINFO_URL = "http://qaetrieve.hercrentals.com/EtrieveItRestService/getOpenRentalsDetailsService/licenseInfo";
	public static final String ETRIVE_EXTENDCONTRACTSERVICE_URL = "http://qaetrieve.hercrentals.com/EtrieveItRestService/extendContractService";
	public static final String ETRIVE_RELEASEEQUIPMENTSERVICE_URL = "http://qaetrieve.hercrentals.com/EtrieveItRestService/releaseEquipmentService";
	public static final String ETRIVE_EMAILRENTALCONTRACTSERVICE_URL = "http://qaetrieve.hercrentals.com/EtrieveItRestService/eMailRentalContractService";
	public static final String ETRIVE_GETRENTALRATES_URL = "http://qaetrieve.hercrentals.com/EtrieveItRestService/getRentalRates";
	public static final String ETRIVE_RENTALRESERVATIONSERVICE_URL = "http://qaetrieve.hercrentals.com/EtrieveItRestService/rentalReservationService";
	public static final String ETRIVE_VERIFYCUSTOMERSERVICE_URL = "http://qaetrieve.hercrentals.com/EtrieveItRestService/verifyCustomerService";
	public static final String ETRIVE_UNIQUERESERVATIONNUMBERSSERVICE_URL = "http://qaetrieve.hercrentals.com/EtrieveItRestService/uniqueReservationNumbersService";
	public static final String ETRIVE_RETRIEVEOPENRESERVATIONDETAILS_URL = "http://qaetrieve.hercrentals.com/EtrieveItRestService/retrieveOpenReservationDetails";

	public static final String ETRIVE_COLUMN_COMPANYCODE = "companyCode";
	public static final String ETRIVE_COLUMN_CUSTOMERNUMBER = "customerNumber";
	public static final String ETRIVE_COLUMN_LICENSESTATE = "licenseState";
	public static final String ETRIVE_COLUMN_LICENSENUMBER = "licenseNumber";
	public static final String ETRIVE_COLUMN_SHOWOVERDUE = "showOverdue";
	public static final String ETRIVE_COLUMN_SHOWPICKUP = "showPickUp";
	public static final String ETRIVE_COLUMN_CONTRACTNUMBER = "contractNumber";
	public static final String ETRIVE_COLUMN_MODEFLAG = "modeFlag";
	public static final String ETRIVE_COLUMN_NEWRETURNDATE = "newReturnDate";
	public static final String ETRIVE_COLUMN_CONTACTNAME = "contactName";
	public static final String ETRIVE_COLUMN_CONTACTPHONE = "contactPhone";
	public static final String ETRIVE_COLUMN_CONTACTEMAIL = "contactEmail";
	public static final String ETRIVE_COLUMN_CONTACTCOMMENTS = "contactComments";
	public static final String ETRIVE_COLUMN_RELEASEDATE = "releaseDate";
	public static final String ETRIVE_COLUMN_RELEASETIME = "releaseTime";
	public static final String ETRIVE_COLUMN_SITECONTACT = "siteContact";
	public static final String ETRIVE_COLUMN_SITEPHONE = "sitePhone";
	public static final String ETRIVE_COLUMN_EQUIPPHYLOC = "equipPhyLoc";
	public static final String ETRIVE_COLUMN_LINENUMBERS = "lineNumbers";
	public static final String ETRIVE_COLUMN_LINEQTY = "lineQty";
	public static final String ETRIVE_COLUMN_SEQUENCENUM = "sequenceNumber";
	public static final String ETRIVE_COLUMN_CONTACTFIRSTNAME = "contactFirstName";
	public static final String ETRIVE_COLUMN_CONTACTLASTNAME = "contactLastName";
	public static final String ETRIVE_COLUMN_BRANCHCODE = "branchCode";
	public static final String ETRIVE_COLUMN_STARTDATE = "startDate";
	public static final String ETRIVE_COLUMN_ENDDATE = "endDate";
	public static final String ETRIVE_COLUMN_EQUIPCATG = "equipCatg";
	public static final String ETRIVE_COLUMN_EQUIPCLASS = "equipClass";
	public static final String ETRIVE_COLUMN_EQUIPQTY = "equipQty";
	public static final String ETRIVE_COLUMN_JOBSITE = "jobSite";
	public static final String ETRIVE_COLUMN_JOBSITECONTACT = "jobSiteContact";
	public static final String ETRIVE_COLUMN_JOBSITEPHONE = "jobSitePhone";
	public static final String ETRIVE_COLUMN_JOBSITEADDRESS1 = "jobSiteAddress1";
	public static final String ETRIVE_COLUMN_JOBSITEADDRESS2 = "jobSiteAddress2";
	public static final String ETRIVE_COLUMN_JOBSITECITY = "jobSiteCity";
	public static final String ETRIVE_COLUMN_JOBSITESTATE = "jobSiteState";
	public static final String ETRIVE_COLUMN_JOBSITEZIP = "jobSiteZip";
	public static final String ETRIVE_COLUMN_PURCHASEORDER = "purchaseOrder";
	public static final String ETRIVE_COLUMN_ESTRETURNDATE = "estReturnDate";
	public static final String ETRIVE_COLUMN_EMAILADDRESS = "emailAddress";
	public static final String ETRIVE_COLUMN_SOURCEID = "sourceID";
	public static final String ETRIVE_COLUMN_DELIVERYNOTES = "deliveryNotes";
	public static final String ETRIVE_COLUMN_ORDEREDBY = "orderedBy";
	public static final String ETRIVE_COLUMN_RESVNUMBER = "resvNumber";
	public static final String ETRIVE_COLUMN_CONTACTCOMMENT = "contactComment";

}
