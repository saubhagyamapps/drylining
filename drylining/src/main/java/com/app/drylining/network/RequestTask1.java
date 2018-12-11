package com.app.drylining.network;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.data.AppConstant;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)

public class RequestTask1 extends AsyncTask<String, RequestTaskDelegate, HttpURLConnection> {

    public RequestTaskDelegate delegate;

    private String requestURL, responseContent;

    private final String USER_AGENT = "Mozilla/5.0";

    private final String CHAR_SET = "UTF-8";

    private AppConstant.HttpRequestType currentRequest;

    private Object extraInfo, objectInfo;
    private ArrayList<String> attachments;
    private boolean isMultipleAttachments = false;

    private Context context;

    JSONObject jsonObjReceive = null;

    private String resultString;
    private int status;

    public RequestTask1(String requestURL, AppConstant.HttpRequestType userRequest) {
        this.requestURL = requestURL;
        this.currentRequest = userRequest;
        this.responseContent = AppConstant.NULL_STRING;
    }

    public RequestTask1(String requestURL, AppConstant.HttpRequestType userRequest, Object extraInfo) {
        this.requestURL = requestURL;
        this.currentRequest = userRequest;
        this.extraInfo = extraInfo;
        this.responseContent = AppConstant.NULL_STRING;
    }

    public RequestTask1(String requestURL, AppConstant.HttpRequestType userRequest, Object extraInfo, ArrayList<String> attachments, boolean isMultipleAttachments) {
        this.requestURL = requestURL;
        this.currentRequest = userRequest;
        this.extraInfo = extraInfo;
        this.responseContent = AppConstant.NULL_STRING;
        this.attachments = attachments;
        this.isMultipleAttachments = isMultipleAttachments;
    }

    public RequestTask1(String requestURL, AppConstant.HttpRequestType userRequest, Object extraInfo, ArrayList<String> attachments, boolean isMultipleAttachments, Object extraObject) {
        this.requestURL = requestURL;
        this.currentRequest = userRequest;
        this.extraInfo = extraInfo;
        this.responseContent = AppConstant.NULL_STRING;
        this.attachments = attachments;
        this.isMultipleAttachments = isMultipleAttachments;
        this.objectInfo = extraObject;
    }

    private HttpURLConnection getSimpleRequest(String... params) {
        try {
            for (String param : params) {
                AppDebugLog.println("Params in getSimpleRequest : " + param);
            }

            String urlStr = params[0];
            URL url = new URL(urlStr);
            AppDebugLog.println("url in  getSimpleRequest : " + url);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (params.length >= 2) {// Post

                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("connection", "close");
                conn.setRequestProperty("Accept-Charset", "utf-8");
                Log.e("Server test :", " " + "false" + params[1]);
                conn.setReadTimeout(60000);
                conn.setConnectTimeout(15000);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                writer.write(params[1]);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

                if (conn != null) {
                    InputStream inputStream = conn.getInputStream();
                    resultString = convertStreamToString(inputStream);
                    inputStream.close();
                    resultString = resultString.substring(0, resultString.length() - 1);
                    Log.e("response data", resultString);
                    jsonObjReceive = new JSONObject(resultString);

                    if (!jsonObjReceive.isNull("status")) {
                        switch (jsonObjReceive.getInt("status")) {
                            case 0: // failed
                                status = 0;
                                // ApplicationData.getSharedInstance().setStatus(status);
                                break;
                            case 1: // success
                                status = 1;
                                // ApplicationData.getSharedInstance().setStatus(status);
                                break;
                            default:
                                break;
                        }

                        //Log.e("status",jsonObjReceive.getInt("authStatus") + " : "+session.getIsActive());
                        jsonObjReceive = null;
                    }
                }
            } else {// GET
                // optional default is GET
                conn.setRequestMethod("GET");
                // add request header
                conn.setRequestProperty("User-Agent", USER_AGENT);

                int responseCode = conn.getResponseCode();
                AppDebugLog.println("\nSending 'GET' request to URL in getSimpleRequest: " + url);
                AppDebugLog.println("Response Code in getSimpleRequest : " + responseCode);

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                resultString = response.toString();
                jsonObjReceive = new JSONObject(resultString);
                AppDebugLog.println("responseContent in getSimpleRequest : " + resultString);
                if (!jsonObjReceive.isNull("status")) {
                    switch (jsonObjReceive.getInt("status")) {
                        case 0: // failed
                            status = 0;
                            // ApplicationData.getSharedInstance().setStatus(status);
                            break;
                        case 1: // success
                            status = 1;
                            // ApplicationData.getSharedInstance().setStatus(status);
                            break;
                        default:
                            break;
                    }

                    //Log.e("status",jsonObjReceive.getInt("authStatus") + " : "+session.getIsActive());
                    jsonObjReceive = null;
                }
            }
            return conn;
        } catch (UnsupportedEncodingException e) {
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        } catch (Exception e) {
            AppDebugLog.println("Exception in getSimpleRequest : " + e.getLocalizedMessage());
        }

        return null;
    }

    // converting InputStream to string
    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
//	    	Toast.makeText(getActivity().getApplicationContext(), "Error : Please try agian later!", Toast.LENGTH_LONG).show();
//	         GetLog.e(AppConstant.TAG_ERROR_TRACE,"convertStreamToString", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
//	        	Toast.makeText(getActivity().getApplicationContext(), "Error : Please try agian later!", Toast.LENGTH_LONG).show();
            }
        }
        return sb.toString();
    }

    /**
     * Adds a form field to the request
     *
     * @param name  field name
     * @param value field value
     */
    public void addFormField(String name, String value, PrintWriter writer) {

        writer.append(value).append(name);
        writer.flush();
    }

    /**
     * Adds a upload file section to the request
     *
     * @param fieldName  name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */
    public void addFilePart(String fieldName, File uploadFile, OutputStream outputStream, PrintWriter writer)
            throws IOException {
        String fileName = uploadFile.getName();
        writer.append(
                "Content-Type: "
                        + URLConnection.guessContentTypeFromName(fileName));
        writer.append(
                "Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fileName + "\"");
        writer.flush();

        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();

        writer.flush();
    }

    private HttpURLConnection getMultiPartRequest1(String... params) {
        try {
            String crlf = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";

            for (String param : params) {
                AppDebugLog.println("Params in getMultiPartRequest : " + param);
            }

            String urlStr = params[0];
            URL url = new URL(urlStr);
            //AppDebugLog.println("url in  getMultiPartRequest : " + url);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            // Send post request
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Cache-Control", "no-cache");
            con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream request = new DataOutputStream(
                    con.getOutputStream());

            String STRING_PART_NAME = "";  //"xmlContent";
            request.writeBytes(twoHyphens + boundary + crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"" + STRING_PART_NAME + "\"" + crlf + crlf);

            request.writeBytes(params[1] + crlf);

            if (params.length > 2) {
                for (int i = 2; i < params.length; ++i) {
                    String multiPartKeyValues = params[i];
                    String[] parts = multiPartKeyValues.split(AppConstant.MULTIPART_REQ_KEY_VALUE_SEPERATOR);
                    AppDebugLog.println("parts.length  in getMultiPartRequest :" + parts.length);
                    String FILE_PART_NAME = parts[0];
                    AppDebugLog.println("FILE_PART_NAME  in getMultiPartRequest :" + FILE_PART_NAME);
                    if (parts.length > 1) {
                        String path = parts[1];
                        AppDebugLog.println("path  in getMultiPartRequest :" + path);
                        File file = new File(path);

                        if (path.length() > 0) {
                            //String FILE_PART_NAME = parts[0];
                            //String[] pathComponent = path.split(AppConstant.MULTIPART_REQ_KEY_VALUE_SEPERATOR);
                            // if (pathComponent.length == 2) {
                            //File file = new File(pathComponent[1]);

                            ;//ApplicationData.getSharedInstance().getFixedSizeFile(path);//
                            AppDebugLog.println("file length :" + file.length());
                            double fileSizeInKB = file.length() / 1000.0;
                            AppDebugLog.println("file in getMultipPartRequest : " + file + " : " + fileSizeInKB);
                            request.writeBytes(twoHyphens + boundary + crlf);
                            request.writeBytes("Content-Disposition: form-data; name=\"" + FILE_PART_NAME + "\";filename=\"" + path + "\"" + crlf);
                            request.writeBytes(crlf);

                            FileInputStream fStream = new FileInputStream(file);
                            int bufferSize = 1024;
                            byte[] buffer = new byte[bufferSize];
                            int length = -1;

                            while ((length = fStream.read(buffer)) != -1) {
                                request.write(buffer, 0, length);
                            }
                            request.writeBytes(crlf);
                            request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
                              /* close streams */
                            fStream.close();
                            // }
                        }
                    }
                }
            }

            int responseCode = con.getResponseCode();
            AppDebugLog.println("Response Code in getMultiPartRequest : " + responseCode);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String line = null;
            StringBuffer response = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            responseContent = response.toString();
            AppDebugLog.println("responseContent in post of getMultiPartRequest : " + responseContent);
            reader.close();
            con.disconnect();

        } catch (UnsupportedEncodingException e) {
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        } catch (Exception e) {
            AppDebugLog.println("Exception : " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /*
    *  Multipart Request for TopJASA
    * */
    private HttpURLConnection getMultiPartRequest(String... params) {
        try {
            for (String param : params) {
                AppDebugLog.println("Params in getMultiPartRequest : " + param);
            }

            MultipartUtility multipart = new MultipartUtility(params[0], CHAR_SET);
            multipart.addHeaderField("User-Agent", USER_AGENT);
            multipart.addHeaderField("Header", "Header-Value");

            Context context = (Context) extraInfo;

            // beanJsonData ="TaskBean~" + taskListToJson + "&CustomerBean~"+customerListToJson;
            AppDebugLog.println("Parameter Length :" + params.length);
            if (params.length > 1) {
                for (int i = 1; i < params.length; i++) {
                    String beanJsonData = params[i];
                    String[] beanData = beanJsonData.split("~");
                    multipart.addFormField(beanData[0], beanData[1]);
                    Log.e(beanData[0],beanData[1]);
                    AppDebugLog.println("beanData key  in getMultiPartRequest :" + beanData[0]);
                    AppDebugLog.println("beanData value  in getMultiPartRequest :" + beanData[1]);
                }
            }

            if (attachments != null && attachments.size() != 0) {
                AppDebugLog.println("attachments size :" + attachments.size());
                for (int i = 0; i < attachments.size(); i++) {
                    if (isMultipleAttachments) {
                        String multiPartKeyValues = attachments.get(i);
                        String[] parts = multiPartKeyValues.split(AppConstant.MULTIPART_REQ_KEY_VALUE_SEPERATOR);
                        AppDebugLog.println("attachments size  in getMultiPartRequest :" + attachments.size());
                        String FILE_PART_NAME = parts[0];

                        if (parts.length > 1) {
                            String path = parts[1];
                            if (path.length() > 0) {
                                AppDebugLog.println("path  in getMultiPartRequest :" + path);
                                File file = new File(path);

                                int temp = i + 1;
                                AppDebugLog.println("file Name :" + (FILE_PART_NAME + temp));
                                AppDebugLog.println("file :" + file);
                                multipart.addFilePart(FILE_PART_NAME + temp, file);
                            }
                        }
                    } else {
                        AppDebugLog.println("isMultipleAttachments" + isMultipleAttachments);
                        String multiPartKeyValues = attachments.get(0);
                        String[] parts = multiPartKeyValues.split(AppConstant.MULTIPART_REQ_KEY_VALUE_SEPERATOR);
                        AppDebugLog.println("parts.length  in getMultiPartRequest :" + parts.length);
                        String FILE_PART_NAME = parts[0];

                        if (parts.length > 1) {
                            String path = parts[1];
                            if (path.length() > 0) {
                                AppDebugLog.println("path  in getMultiPartRequest :" + path);
                                File file = new File(path);

                                AppDebugLog.println("file Name :" + FILE_PART_NAME);
                                AppDebugLog.println("file :" + file);
                                multipart.addFilePart(FILE_PART_NAME, file);
                            }
                        }
                    }
                }
            }

            List<String> response = multipart.finish();
            System.out.println("SERVER REPLIED:");
            for (String line : response) {
                resultString = line;
                AppDebugLog.println("response data :" + resultString);
            }

            jsonObjReceive = new JSONObject(resultString);
            AppDebugLog.println("responseContent in getSimpleRequest : " + resultString);
            if (!jsonObjReceive.isNull("status")) {
                switch (jsonObjReceive.getInt("status")) {
                    case 0: // failed
                        status = 0;
                        // ApplicationData.getSharedInstance().setStatus(status);
                        break;
                    case 1: // success
                        status = 1;
                        // ApplicationData.getSharedInstance().setStatus(status);
                        break;
                    default:
                        break;
                }

                //Log.e("status",jsonObjReceive.getInt("authStatus") + " : "+session.getIsActive());
                jsonObjReceive = null;
            }
        } catch (UnsupportedEncodingException e) {
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        } catch (Exception e) {
            AppDebugLog.println("Exception : " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    protected HttpURLConnection doInBackground(String... params) {

        HttpURLConnection httpURLConnection = null;
        if (currentRequest == AppConstant.HttpRequestType.addTool||currentRequest == AppConstant.HttpRequestType.UPDATE_TOOL) {
            httpURLConnection = getMultiPartRequest(params);
        } else {
            httpURLConnection = getSimpleRequest(params);
        }
        return httpURLConnection;
    }

    protected void onProgressUpdate(String sResponse) {

    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onPostExecute(HttpURLConnection httpURLConnection) {
        super.onPostExecute(httpURLConnection);

        /*AppDebugLog.println("response data :" + resultString);
        AppDebugLog.println("status :" + ApplicationData.getSharedInstance().getStatus());*/

        Log.e("Request Task 1 Resp:",""+resultString);

        switch (currentRequest) {
            case addProperty:
                AppDebugLog.println("response data in Login request:" + resultString);
                break;
            default:
                break;
        }
        delegate.backgroundActivityComp(resultString, currentRequest);
    }
}