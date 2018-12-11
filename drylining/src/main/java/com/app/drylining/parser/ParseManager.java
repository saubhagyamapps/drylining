package com.app.drylining.parser;

import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.data.AppConstant.HTTPResponseCode;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.data.User;

import static com.app.drylining.parser.XMLManualParser.getTagValue;
import static java.lang.Integer.parseInt;

public final class ParseManager {

    private static final String TAG_CODE = "code";
    private static final String TAG_ID = "id";

    // User Tags
    private static final String TAG_USER = "userid";
    private static final String TAG_USER_NAME = "username";
    private static final String TAG_USER_PAN = "pan";
    private static final String TAG_USER_DOB = "userdob";
    private static final String TAG_USER_GENDER = "usergender";
    private static final String TAG_USER_ORGANIZATION = "userorganization";
    private static final String TAG_USER_EMAIL = "useremail";
    private static final String TAG_USER_MOB_NO = "usermobileno";
    private static final String TAG_USER_REFERRAL_CODE = "referralcode";
    private static final String TAG_USER_REFERRAL_BY = "referredby";

    public static void parseLogInResponse(String response) {
        AppDebugLog.println("Response in parseLogInResponse : " + response);
        ApplicationData appdata = ApplicationData.getSharedInstance();
        if (response.length() > 0) {
            String codeStr = getTagValue(TAG_CODE, response);
            if (codeStr.length() > 0) {
                int responseCode = parseInt(codeStr);

                appdata.setResponseCode((HTTPResponseCode.values())[responseCode]);

                if (appdata.getResponseCode() == HTTPResponseCode.Success) {
                    String id = getTagValue(TAG_ID, response);
                    if (id.length() > 0) {

                        User user = new User();
                        AppDebugLog.println("New User Created : " + user);

                        user.setId(parseInt(id));

                        user.setPan(getTagValue(TAG_USER, response));
                        user.setName(getTagValue(TAG_USER_NAME, response));
                        user.setGender(getTagValue(TAG_USER_GENDER, response));
                        user.setDob(getTagValue(TAG_USER_DOB, response));
                        user.setOrganization(getTagValue(TAG_USER_ORGANIZATION, response));
                        user.setEmail(getTagValue(TAG_USER_EMAIL, response));
                        user.setMobileNo(getTagValue(TAG_USER_MOB_NO, response));
                        user.setReferralCode(getTagValue(TAG_USER_REFERRAL_CODE, response));
                        user.setReferralBy(getTagValue(TAG_USER_REFERRAL_BY, response));

                        appdata.setUser(user);

                    }
                }
            }
        }
    }

    public static void parseGeneralResponse(String response) {
        AppDebugLog.println("Response in parseGeneralResponse : " + response);
        ApplicationData appdata = ApplicationData.getSharedInstance();
        if (response.length() > 0) {
            String codeStr = getTagValue(TAG_CODE, response);
            if (codeStr.length() > 0) {
                int responseCode = parseInt(codeStr);
                appdata.setResponseCode((HTTPResponseCode.values())[responseCode]);
            }
            return;
        }
        appdata.setResponseCode(HTTPResponseCode.ServerError);
    }

}
