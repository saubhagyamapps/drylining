package com.app.drylining.data;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.data.AppConstant.HTTPResponseCode;
import com.app.drylining.database.DataBaseHelper;
import com.app.drylining.network.ConnectionDetector;
import com.app.drylining.R;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.os.Environment.getExternalStorageDirectory;
import static com.app.drylining.DRYLINING.getAppContext;

@SuppressLint("SimpleDateFormat")
public class ApplicationData
{
    private static ApplicationData sharedInstance;
    private DataBaseHelper dbManager;
    private User user;

    private File appExternalDirectory;
    private File imageDirectory;

    private HTTPResponseCode responseCode;

    private Context context;
    private SharedPreferences preferences;

    private ConnectionDetector connectionDetector;

    private String phoneNumber;

    private Locale locale = new Locale("en");
    private String language = "English(EN)";

    private ApplicationData() {

    }

    public static ApplicationData getSharedInstance()
    {
        if (sharedInstance == null) {
            sharedInstance = new ApplicationData();
            sharedInstance.initialize();
        }
        return sharedInstance;
    }

    private void initialize() {
        AppDebugLog.setFileLogMode(AppConstant.FILE_DEBUG);
        AppDebugLog.setProductionMode(AppConstant.PRODUCTION_DEBUG);

        dbManager = new DataBaseHelper(getAppContext());
        this.context = getAppContext();

        connectionDetector = new ConnectionDetector(context);

        phoneNumber = AppConstant.NULL_STRING;

        setApplicationPreferences();
        createDirectory();
    }

    public Context getAppcontext() {
        return getAppContext();
    }

    public ConnectionDetector getConnectionDetector() {
        return connectionDetector;
    }

    public DataBaseHelper getDbManager() {
        return dbManager;
    }

    public HTTPResponseCode getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(HTTPResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Locale getLocale()
    {
        return locale;
    }

    public void setLocale(Locale locale)
    {
        this.locale = locale;
    }

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    public void showUserAlert(Context context, String title, String message, OnClickListener listner) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(message)
                .setPositiveButton(context.getText(R.string.btn_ok), listner).show();
    }

    public void showConfirmationAlert(Context context, String title, String message, String btnPositiveTitle,
                                      String btnNegativeTitle, OnClickListener btnPositiveListner, OnClickListener btnNegativeListner) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(message)
                .setPositiveButton(btnPositiveTitle, btnPositiveListner)
                .setNegativeButton(btnNegativeTitle, btnNegativeListner).show();
    }

    public int getDrawableWidth(int drawableId) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) context.getResources().getDrawable(drawableId);

        return (bitmapDrawable.getBitmap().getWidth());
    }

    public int getDrawableHeight(int drawableId) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) context.getResources().getDrawable(drawableId);

        return (bitmapDrawable.getBitmap().getHeight());
    }

    public int getResId(String variableName, Context context, Class<?> c) {
        try {
            java.lang.reflect.Field idField = c.getField(variableName);
            return idField.getInt(null);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int getAndroidVersion() {
        int version = android.os.Build.VERSION.SDK_INT;
        System.out.println("Android Version : " + version);
        return version;
    }

    public boolean isXLargeScreen() {
        return ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE);
    }

    public boolean isLandscape(Activity activity) {
        boolean isLandscape = false;

        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isLandscape = true;
            return isLandscape;
        }
        return isLandscape;
    }

    public int getDisplayWidth(Activity activity) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;

        return width;
    }

    public int getDisplayHeight(Activity activity) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;

        return height;
    }

    public String getDateStringFromDate(SimpleDateFormat dateFormat, Date date) {
        return dateFormat.format(date);
    }

    public Date getDateFromDateString(SimpleDateFormat dateFormat, String dateStr) {
        Date date = null;
        try {
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return date;
    }

    @SuppressLint("InlinedApi")
    public void setActivityOrientation(Activity activity) {
        if (isXLargeScreen()) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return;// keep in portrait mode if a phone
        }
    }

    /**
     * Application Preference related functions
     */

    private void setApplicationPreferences() {
        preferences = PreferenceManager.getDefaultSharedPreferences(getAppContext());

        if (preferences.getString(AppConstant.GCMTokenId, AppConstant.defaultGCMTokenId).equalsIgnoreCase(
                AppConstant.defaultGCMTokenId)) {
            preferences.edit().putString(AppConstant.GCMTokenId, AppConstant.defaultGCMTokenId).commit();
        }

        if (preferences.getString(AppConstant.userType, AppConstant.defaultUserType).equalsIgnoreCase(
                AppConstant.defaultUserType)) {
            preferences.edit().putString(AppConstant.userType, AppConstant.defaultUserType).commit();
        }

        if (preferences.getString(AppConstant.isLoggedIn, AppConstant.defaultLoggedIn).equalsIgnoreCase(
                AppConstant.defaultLoggedIn)) {
            preferences.edit().putString(AppConstant.isLoggedIn, AppConstant.defaultLoggedIn).commit();
        }

        if (preferences.getString(AppConstant.location, AppConstant.defaultLocation).equalsIgnoreCase(
                AppConstant.defaultLocation)) {
            preferences.edit().putString(AppConstant.location, AppConstant.defaultLocation).commit();
        }

    }

    public void setGCMTokenId(String gcmTokenId) {
        preferences.edit().putString(AppConstant.GCMTokenId, gcmTokenId).commit();
    }

    public String getGCMTokenId() {
        return preferences.getString(AppConstant.GCMTokenId, AppConstant.defaultGCMTokenId);
    }

    public void setUserType(String userType) {
        preferences.edit().putString(AppConstant.userType, userType).commit();
    }

    public String getUserType() {
        return preferences.getString(AppConstant.userType, AppConstant.defaultUserType);
    }

    public void setUserId(String userId) {
        preferences.edit().putString(AppConstant.userId, userId).commit();
    }

    public String getUserId() {
        return preferences.getString(AppConstant.userId, "");
    }

    public void setUserName(String userName)
    {
        preferences.edit().putString(AppConstant.userName, userName).commit();
    }

    public String getUserName()
    {
        return preferences.getString(AppConstant.userName, "");
    }

    public void setUserEmail(String userEmail) {
        preferences.edit().putString(AppConstant.userEmail, userEmail).commit();
    }
    public String getUserEmail() { return preferences.getString(AppConstant.userEmail, ""); }

    public void setUserPassword(String userPassword) {
        preferences.edit().putString(AppConstant.userPassword, userPassword).commit();
    }
    public String getUserPassword() { return preferences.getString(AppConstant.userPassword, ""); }

    public void setCntMessages(String cnt)
    {
        preferences.edit().putString(AppConstant.cntMessages, cnt).commit();
    }

    public String getCntMessages()
    {
        return preferences.getString(AppConstant.cntMessages, "0");
    }

    public void setIsLoggedIn(String isLoggedIn) {
        preferences.edit().putString(AppConstant.isLoggedIn, isLoggedIn).commit();
    }

    public String getIsLoggedIn() {
        return preferences.getString(AppConstant.isLoggedIn, AppConstant.defaultLoggedIn);
    }

    public void setCurrentLocation(String location) {
        preferences.edit().putString(AppConstant.location, location).commit();
    }

    public String getCurrentLocation() {
        return preferences.getString(AppConstant.location, AppConstant.defaultLocation);
    }

    public String getPreCityName()
    {
        return preferences.getString(AppConstant.preSearchCity, "");
    }

    public void setPreCityName(String city)
    {
        preferences.edit().putString(AppConstant.preSearchCity, city).commit();
    }

    public String getPreRange()
    {
        return preferences.getString(AppConstant.preSearchRange, "");
    }

    public void setPreRange(String range)
    {
        preferences.edit().putString(AppConstant.preSearchRange, range).commit();
    }

    public String getPreProperty()
    {
        return preferences.getString(AppConstant.preSearchProperty, "");
    }

    public void setPreProperty(String property)
    {
        preferences.edit().putString(AppConstant.preSearchProperty, property).commit();
    }

    public String getPreRoom()
    {
        return preferences.getString(AppConstant.preSearchRoom, "");
    }

    public void setPreRoom(String room)
    {
        preferences.edit().putString(AppConstant.preSearchRoom, room).commit();
    }

    public String getPreGarage()
    {
        return preferences.getString(AppConstant.preSearchGarage, "");
    }

    public void setPreGarage(String garage)
    {
        preferences.edit().putString(AppConstant.preSearchGarage, garage).commit();
    }

    public int getPreMinPrice()
    {
        return preferences.getInt(AppConstant.preSearchMinPrice, 0);
    }

    public void setPreMinPrice(int minPrice)
    {
        preferences.edit().putInt(AppConstant.preSearchMinPrice, minPrice).commit();
    }

    public int getPreMaxPrice()
    {
        return preferences.getInt(AppConstant.preSearchMaxPrice, 0);
    }

    public void setPreMaxPrice(int maxPrice)
    {
        preferences.edit().putInt(AppConstant.preSearchMaxPrice, maxPrice).commit();
    }

    public String getPreCityLocation()
    {
        return preferences.getString(AppConstant.preSearchCityLocation, "");
    }

    public void setPreCityLocation(String location)
    {
        preferences.edit().putString(AppConstant.preSearchCityLocation, location).commit();
    }

    public Boolean getIsSearched()
    {
        return preferences.getBoolean(AppConstant.isSearched, false);
    }
    public void setIsSearched(Boolean isSearched)
    {
        preferences.edit().putBoolean(AppConstant.isSearched, isSearched).commit();
    }


    public String getRMB_email()
    {
        return preferences.getString(AppConstant.RMB_EMAIL, "");
    }

    public void setRMB_email(String email)
    {
        preferences.edit().putString(AppConstant.RMB_EMAIL, email).commit();
    }

    public String getRMB_password()
    {
        return preferences.getString(AppConstant.RMB_PASSWORD, "");
    }

    public void setRMB_password(String password)
    {
        preferences.edit().putString(AppConstant.RMB_PASSWORD, password).commit();
    }


    public String getAppVersionName() {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
            AppDebugLog.println("Exception In getAppVersionName : " + e.getLocalizedMessage());
        }
        return AppConstant.NULL_STRING;
    }

    public File getAppExternalDirectory() {
        return appExternalDirectory;
    }

    public void setAppExternalDirectory(File appExternalDirectory) {
        this.appExternalDirectory = appExternalDirectory;
    }

    public void setImageDirectory(File imageDirectory) {
        this.imageDirectory = imageDirectory;
    }

    public File getImageDirectory() {
        return imageDirectory;
    }

    private void createDirectory() {
        //AppDebugLog.println("ExternalStorageState in createDirectory : " + isExternalStorageReadable() + " : " + isExternalStorageWritable());

        String exterrnalImgDirPath = AppConstant.DIR_NAME;

        if (isExternalStorageWritable()) {
            appExternalDirectory = context.getExternalFilesDir(exterrnalImgDirPath);
        } else {
            appExternalDirectory = context.getFilesDir();
        }

        if (appExternalDirectory != null && !appExternalDirectory.exists()) {
            boolean isCreated = appExternalDirectory.mkdirs();
            //AppDebugLog.println("appExternalDirectory Created : " + isCreated);
        }
        if (appExternalDirectory != null)
            AppDebugLog.println("appExternalDirectory path : " + appExternalDirectory.exists() + " : " + appExternalDirectory.getAbsolutePath());


        String imgDirPath = AppConstant.DIR_NAME + File.separator
                + AppConstant.IMAGE_DIR_NAME;

        if (isExternalStorageWritable()) {
            imageDirectory = context.getExternalFilesDir(imgDirPath);
        } else {
            imageDirectory = context.getFilesDir();
        }

        if (imageDirectory != null && !imageDirectory.exists()) {
            boolean isCreated = imageDirectory.mkdirs();
            //AppDebugLog.println("imageDirectory Created : " + isCreated);
        }
        if (imageDirectory != null)
            AppDebugLog.println("imageDirectory path : " + imageDirectory.exists() + " : " + imageDirectory.getAbsolutePath());

    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public String getFilePathAsPerFileName(String fileName) {
        String filePath = AppConstant.NULL_STRING;
        File directory = new File(Environment.getExternalStorageDirectory() + File.separator + AppConstant.APP_FOLDER_NAME + File.separator);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        filePath = directory.getPath() + File.separator + fileName;

        return filePath;
    }

    public String getFileNameFromfilePath(String filePath) {
        return filePath.substring(filePath.lastIndexOf("/") + 1);
    }

    //v2
    @SuppressLint("NewApi")
    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.<br>
     * <br>
     * Callers should check whether the path is local before assuming it
     * represents a local file.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @see #isLocal(String)
     * @see #getFile(Context, Uri)
     * @author paulburke
     */
    public static String getPath(final Context context, final Uri uri) {
        AppDebugLog.println(" File -" +
                "Authority: " + uri.getAuthority() +
                ", Fragment: " + uri.getFragment() +
                ", Port: " + uri.getPort() +
                ", Query: " + uri.getQuery() +
                ", Scheme: " + uri.getScheme() +
                ", Host: " + uri.getHost() +
                ", Segments: " + uri.getPathSegments().toString()
        );

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
//            // LocalStorageProvider
//            if (isLocalStorageDocument(uri)) {
//                // The path is the id
//                return DocumentsContract.getDocumentId(uri);
//            }
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                AppDebugLog.println("In getPath : " + type + " : " + split[1]);
                if ("primary".equalsIgnoreCase(type)) {
                    return getExternalStorageDirectory() + "/" + split[1];
                } else {
                    return "/storage" + File.separator + split[0] + File.separator + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }


//    /**
//     * @param uri The Uri to check.
//     * @return Whether the Uri authority is {@linkLocalStorageProvider}.
//     * @author paulburke
//     */
//    public static boolean isLocalStorageDocument(Uri uri) {
//        return LocalStorageProvider.AUTHORITY.equals(uri.getAuthority());
//    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     * @author paulburke
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     * @author paulburke
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     * @author paulburke
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     * @author paulburke
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                if (!AppConstant.PRODUCTION_DEBUG)
                    DatabaseUtils.dumpCursor(cursor);

                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param mImageView
     * @param targetW
     * @param targetH
     * @param imagePath
     */
    public void setPic(ImageView mImageView, int targetW, int targetH, String imagePath) {
        Bitmap bitmap;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        final int REQUIRED_SIZE = targetH;

        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        mImageView.setImageBitmap(bitmap);
    }
}
