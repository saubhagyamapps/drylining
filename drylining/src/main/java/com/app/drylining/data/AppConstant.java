package com.app.drylining.data;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public abstract class AppConstant
{
    public static final int DB_VERSION = 1;

    public static final boolean PRODUCTION_DEBUG = false;
    public static final boolean FILE_DEBUG = true;

    public static final boolean IS_PUSH_NOTIFICATION_REG = false;
    public static final boolean IS_VALIDATION_REQ = true;

    public static final String NULL_STRING = "";

    public static final long SPLASH_SCREEN_TIMEOUT = 1000;// SplashScreen TimeOut

    public static final int ADMIN_BAR_OPEN_TIME = 300;// Animation TimeOut

    //Payment enable/disable
    public static final boolean IS_PAYMENT_ENABLE = false;
    public static final double  PAYMENT_PERCENT = 0.1;
    public static final String  KEY_PAY_AMOUNT  = "payAmount";


    //Log In Remember me
    public static final String RMB_EMAIL = "remember_email";
    public static final String RMB_PASSWORD = "remember_password";

    //Maximum no. of characters enter in form fields constants
    public static final int PAN_MAX_CHAR = 10;
    public static final int MOBILE_MAX_CHAR = 10;
    public static final int IFSC_MAX_CHAR = 11;
    public static final int AADHAR_CARD_MAX_CHAR = 12;
    public static final int PINCODE_MAX_CHAR = 6;
    public static final int BANK_ACC_NO_MIN_CHAR = 9;

    public static final double MAX_UPLOAD_FILE_SIZE = 5.0;//size in mb

    //String Separator Constants
    public static final String MULTIPART_REQ_KEY_VALUE_SEPERATOR = "~";
    public static final String SEPRATOR_STRING = "~";
    public static final String COMMA_SEPRATOR_STRING = ",";
    public static final String DOWNLOAD_SAMPLE_URL_HTTPS_PART = "https://";
    public static final String URL_HTTPS_PART = "http://";

    // Shared Preference Key
    public static final String GCMTokenId = "gcmTokenId";
    public static final String isLoggedIn = "";
    public static final String location = "currentLocation";
    public static final String userEmail = "userEmail";
    public static final String userName  = "userName";
    public static final String userPassword = "userPassword";
    public static final String userType = "userType";
    public static final String userId = "userID";
    public static final String cntMessages = "cntMessages";
    public static final String isRenter = "isRememberUser";
    public static final String KEY_LATEST_APP_CONTENT = "latestAppContent";
    public static final String KEY_APP_VERSION_CONTENT = "appVersionContent";

    public static final String preSearchCity = "preCityName";
    public static final String preSearchRange = "preRange";
    public static final String preSearchProperty = "preProperty";
    public static final String preSearchRoom = "preRoom";
    public static final String preSearchGarage = "preGarage";
    public static final String preSearchMinPrice = "preMinPrice";
    public static final String preSearchMaxPrice = "preMaxPrice";
    public static final String preSearchCityLocation = "preCityLocation";
    public static final String isSearched   =   "isSearched";

    //Default values for Preference Variables
    public static final String defaultGCMTokenId = "-1";
    public static final String defaultLoggedIn = "N";
    public static final String defaultLocation = "0,0";
    public static final String defaultUserType = "R";
    public static final String defaultUserPwd = "";
    public static final boolean defaultIsRenter = false;

    //Grid View Constants
    public static final int MAIN_SCREEN_COLUMN_COUNT = 2;
    public static final int MAIN_SCREEN_COLUMN_COUNT_TABLET_LANDSCAPE = 4;
    public static final int SHADOW_WIDTH = 15;

    //Intent Keys Constants
    public static final String KEY_URL = "url";
    public static final String KEY_TITLE = "title";
    public static final String KEY_PAN = "pan";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_EMAIL = "email";

    //View History Activity  Tab Indexes
    public static final int TAB_LOGIN = 0;
    public static final int TAB_SIGNUP = 1;

    //View History Activity  Tab Indexes
    public static final int TAB_ADD_OFFER_BY_ADDRESS = 0;
    public static final int TAB_ADD_OFFER_PICK_FROM_MAP = 1;

    //View History Activity  Tab Indexes
    public static final int TAB_ADD_NEW_OFFER = 0;
    public static final int TAB_ADD_NEW_TOOL = 1;

    public static final int TAB_MODIFY_OFFER_BY_ADDRESS = 0;
    public static final int TAB_MODIFY_OFFER_PICK_FROM_MAP = 1;

    //View History Activity  Tab Indexes
    public static final int TAB_SEARCH_OFFER_BY_ADDRESS = 0;
    public static final int TAB_SEARCH_OFFER_PICK_FROM_MAP = 1;

    //Directory Related Constants
    public static final String APP_FOLDER_NAME = "DRYLINING";
    public static final String DIR_NAME = "DRYLINING";
    public static final String IMAGE_DIR_NAME = "Images";

    //Payment code.
    public static final int PAYMENT_REQUEST = 50;
    public static final int PayPalPay = 100;
    public static final int PayTrustPay = 200;

    //Validation strings constants
    public static final String EMAIL_VALIDATION = "[a-zA-Z0-9._-]+@[a-zA-Z]+\\..+[a-zA-Z]+";
    public static final String PHONE_VALIDATION = "^[+]?[0-9]{10,14}$";
    public static final String PAN_VALIDATION = "^([a-zA-Z]{3})([pP]{1})([a-zA-Z]{1})[0-9]{4}([a-zA-Z]{1})$";
    public static final String IFSC_VALIDATION = "[A-Z|a-z]{4}[0][\\d]{6}$";

    public static final int[] EMPTY_STATE_SET = {};
    public static final int[] GROUP_EXPANDED_STATE_SET = {android.R.attr.state_expanded};
    public static final int[][] GROUP_STATE_SETS = {EMPTY_STATE_SET, // 0
            GROUP_EXPANDED_STATE_SET // 1
    };


    //public static final String WEBSERVICE_PATH = "http://estato.eu/estato_server/";
    public static final String WEBSERVICE_PATH =            "http://dryliningapp.com/admin/api/";
//    public static final String WEBSERVICE_PATH =            "http://192.168.1.128/drylining_club/api/";
    public static final String AUTOLOGIN =                  WEBSERVICE_PATH + "autologin.php";
    public static final String LOGIN =                      WEBSERVICE_PATH + "login.php";
    public static final String SIGNUP =                     WEBSERVICE_PATH + "signup.php";
    public static final String FORGOT_PASSWORD =            WEBSERVICE_PATH + "forgot_password.php";
    public static final String GET_PROPERTIES =             WEBSERVICE_PATH + "get_added_properties.php?user=";
    public static final String GET_TOOLS =                  WEBSERVICE_PATH + "get_added_tools.php?user=";
    public static final String GET_ACCOUNT =                WEBSERVICE_PATH + "get_account.php";
    public static final String UPDATE_ACCOUNT =             WEBSERVICE_PATH + "update_account.php";
    public static final String ADD_PROPERTY =               WEBSERVICE_PATH + "add_property.php";
    public static final String ADD_TOOL =                   WEBSERVICE_PATH + "add_tool.php";
    public static final String REMOVE_PROPERTY =            WEBSERVICE_PATH + "remove_property.php?offerId=";
    public static final String REMOVE_TOOL =                WEBSERVICE_PATH + "remove_tool.php?toolId=";
    public static final String GET_PROPERTY_INFO_LESSEE =   WEBSERVICE_PATH + "get_property_info_lessee.php?";
    public static final String GET_TOOL_INFO_LESSEE =       WEBSERVICE_PATH + "get_tool_info_lessee.php?";
    public static final String GET_PROPERTY_INFO_RENTER =   WEBSERVICE_PATH + "get_property_info_renter.php?propertyId=";
    public static final String GET_TOOL_INFO_RENTER =       WEBSERVICE_PATH + "get_tool_info_renter.php?toolId=";
    public static final String SEARCH_OFFER =               WEBSERVICE_PATH + "search_offer.php";
    public static final String SEARCH_TOOL =                WEBSERVICE_PATH + "search_tool.php";
    public static final String GET_LAST_SEARCH =            WEBSERVICE_PATH + "get_last_search.php";
    public static final String GET_RECENT_TOOLS =           WEBSERVICE_PATH + "get_recent_tools.php";
    public static final String GET_FAVORITE_OFFERS =        WEBSERVICE_PATH + "get_favorite_offers.php";
    public static final String SET_OFFER_CONFIRM =          WEBSERVICE_PATH + "set_offer_confirm.php";
    public static final String EDIT_IMAGE =                 WEBSERVICE_PATH + "offer_image_delete.php?";
    public static final String NEW_CONVERSATION =           WEBSERVICE_PATH + "new_conversation.php";
    public static final String NEW_INTERESTED =             WEBSERVICE_PATH + "new_interested.php";
    public static final String GET_CONVERSATION_LESSEE =    WEBSERVICE_PATH + "get_conversation_lessee.php";
    public static final String GET_CONVERSATION_RENTER =    WEBSERVICE_PATH + "get_conversation_renter.php";
    public static final String GET_NOTIFICATIONS =          WEBSERVICE_PATH + "get_notifications.php";
    public static final String REMOVE_NOTIFICATIONS =       WEBSERVICE_PATH + "remove_notifications.php";
    public static final String UPDATE_NOTIFICATIONS =       WEBSERVICE_PATH + "update_notifications.php?notificationId=";
    public static final String GET_NOTIFY_SETTINGS =        WEBSERVICE_PATH + "notify_settings_get.php?userId=";
    public static final String SET_NOTIFY_SETTINGS =        WEBSERVICE_PATH + "notify_settings_set.php?userId=";
    public static final String GET_OFFER_LESSEE =           WEBSERVICE_PATH + "get_offers_lessee.php?userId=";

    public static final String UPDATE_PROPERTY =            WEBSERVICE_PATH + "update_property.php";
    public static final String UPDATE_TOOL =                WEBSERVICE_PATH + "update_tool.php";
    public static final String FAVORITE =                   WEBSERVICE_PATH + "favorite.php";
    public static final String SET_FINISH_JOB =             WEBSERVICE_PATH + "set_finish_job.php?jobId=";
    public static final String SET_REVIEW_JOB =             WEBSERVICE_PATH + "set_review_job.php?jobId=";

    //Date&Time Format constants
    public static final SimpleDateFormat serverDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    public static final SimpleDateFormat datetimeFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    public static final SimpleDateFormat birthDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    public static final SimpleDateFormat rewardDateTimeFormat = new SimpleDateFormat("dd MMM, hh:mm a");
    public static final SimpleDateFormat captureImgDateTimeFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");


    //Number Format constants
    public static final DecimalFormat numberFormat = new DecimalFormat("#.##");

    public static final String DEVICE_TYPE = "2";

    public enum HttpRequestType
    {
        LogInRequest(1), RegisterRequest(2), EditProfileRequest(3), SendNewInterested(4), ForgotPwdRequest(5),
        ChangePwdRequest(6), NewConversation(7), GetConversations(8), RemoveProperty(9), GetNotifications(10),
        RemoveNotifications(11), UpdateNotification(12),getProperties(13),addProperty(14),getPropertyInfo(15),
        searchOfferRequest(16),VERIFY_EMAIL_OTP(17),GetFavorites(18),GetAccount(19), UpdateAccount(20),
        DELETE_IMAGE(21),UPDATE_PROPERTY(22), FavoriteRequest(23), GetLastSearch(24), GetNotifySettings(25), SetNotifySettings(26),
        getTools(27),getToolInfo(28),addTool(29), RemoveTOOL(30), UPDATE_TOOL(31),searchToolRequest(32), RecentTools(33), SetOfferConfirm(34)
        ,AutoLogInRequest(35),FinishJobRequest(36), ReviewJobRequest(37);


        private final int value;

        private HttpRequestType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

    }

    public enum HTTPResponseCode
    {
        ServerError(0), Success(1), InvaildInputDetails(2), ExistingEmail(3), ExistingPAN(4), ExistingPhone(5), NetworkError(6),
        PromocodeEmpty(7),  InvalidPromoCode(8), PromocodeExpired(9),PromoCodeNotApplicable(10),PromoCodeNotValid(11),
        AlreadyReferred(12), WrongReferrelCode(13), NoRewardPoints(14);

        private final int value;

        private HTTPResponseCode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}

