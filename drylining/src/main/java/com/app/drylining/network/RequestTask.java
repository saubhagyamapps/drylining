package com.app.drylining.network;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;

import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.data.AppConstant;
import com.app.drylining.data.AppConstant.HttpRequestType;
import com.app.drylining.parser.ParseManager;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class RequestTask extends AsyncTask<String, RequestTaskDelegate, HttpURLConnection> {
    public RequestTaskDelegate delegate;
    private String requestURL;
    private String responseContent;
    private HttpRequestType currentRequest;
    private Object extraInfo;
    private final String USER_AGENT = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2";
    private final String CHAR_SET = "UTF-8";
    private Activity activity;

    public RequestTask(String requestURL, HttpRequestType userRequest) {
        this.requestURL = requestURL;
        this.currentRequest = userRequest;
        this.responseContent = AppConstant.NULL_STRING;
    }

    public RequestTask(String requestURL, HttpRequestType userRequest, Object extraInfo) {
        this.requestURL = requestURL;
        this.currentRequest = userRequest;
        this.extraInfo = extraInfo;
        this.responseContent = AppConstant.NULL_STRING;
    }


    public static final int POST=0;
    public static final int GET=1;
    private int reqMethod = POST;

    public void setRequestMethod(int method){
        reqMethod=method;
    }

    private HttpURLConnection getSimpleRequest(String... params) {
        try {
            for (String param : params) {
                AppDebugLog.println("Params in getSimpleRequest : " + param);
            }

            String urlStr = params[0];
            URL url = new URL(urlStr);
            AppDebugLog.println("url in  getSimpleRequest : " + url);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            if (params.length == 2&& reqMethod == POST)  // Post
            {
                // add request header
                con.setRequestMethod("POST");
                con.setRequestProperty("User-Agent", USER_AGENT);
                con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

                String postParam = params[1];
                AppDebugLog.println("Sending 'POST' request to URL : " + url);
                AppDebugLog.println("Post parameters : " + postParam);

                // Send post request
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(postParam);

                wr.flush();
                wr.close();

                int responseCode = con.getResponseCode();

                AppDebugLog.println("Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                responseContent = response.toString();
                AppDebugLog.println("responseContent in post of getSimpleRequest : " + responseContent);

            } else {// GET
                // optional default is GET
                con.setRequestMethod("GET");

                // add request header
                con.setRequestProperty("User-Agent", USER_AGENT);

                int responseCode = con.getResponseCode();
                AppDebugLog.println("\nSending 'GET' request to URL in getSimpleRequest: " + url);
                AppDebugLog.println("Response Code in getSimpleRequest : " + responseCode);

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                responseContent = response.toString();
                AppDebugLog.println("responseContent in getSimpleRequest : " + responseContent);
            }

            return con;
        } catch (UnsupportedEncodingException ue) {
            ue.printStackTrace();
        } catch (MalformedURLException me) {
            me.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }

        return null;
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


    private HttpURLConnection getMultipPartRequest(String... params) {
        try {
            String crlf = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";

            for (String param : params) {
                AppDebugLog.println("Params in getMultipPartRequest : " + param);
            }

            String urlStr = params[0];
            URL url = new URL(urlStr);
            //AppDebugLog.println("url in  getMultipPartRequest : " + url);

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
            con.setRequestProperty(
                    "Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream request = new DataOutputStream(
                    con.getOutputStream());


            String STRING_PART_NAME = "xmlContent";
            request.writeBytes(twoHyphens + boundary + crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"" + STRING_PART_NAME + "\"" + crlf + crlf);
            request.writeBytes(params[1] + crlf);

            if (params.length > 2) {
                for (int i = 2; i < params.length; ++i) {
                    String multiPartKeyValues = params[i];
                    String[] parts = multiPartKeyValues.split(AppConstant.MULTIPART_REQ_KEY_VALUE_SEPERATOR);
                    AppDebugLog.println("parts.length  in getMultipPartRequest :" + parts.length);
                    String FILE_PART_NAME = parts[0];
                    AppDebugLog.println("FILE_PART_NAME  in getMultipPartRequest :" + FILE_PART_NAME);
                    if (parts.length > 1) {
                        String path = parts[1];
                        AppDebugLog.println("path  in getMultipPartRequest :" + path);
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
            AppDebugLog.println("Response Code in getMultipPartRequest : " + responseCode);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String line = null;
            StringBuffer response = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            responseContent = response.toString();
            AppDebugLog.println("responseContent in post of getMultipPartRequest : " + responseContent);
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

    private void getContinuesRequest(String... params) {
        int count;
        try {
            LocalFileManager fileManager = LocalFileManager.sharedFileManager(null);

            URL url = new URL(params[0]);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
                    url.getQuery(), url.getRef());
            url = uri.toURL();
            URLConnection conection = url.openConnection();
            conection.connect();
            // getting file length
            int lenghtOfFile = conection.getContentLength();

            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            // Output stream to write file
            String fileURL = fileManager.getAbsolutePathForURL(requestURL, true);
            OutputStream output = new FileOutputStream(new File(fileURL));

            byte data[] = new byte[1024];

            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                if (this.isCancelled()) {
                    throw new Exception("Task Cancelled");
                }
                if (delegate != null) {
                    delegate.percentageDownloadCompleted((int) ((total * 100) / lenghtOfFile), extraInfo);
                }
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

        } catch (Exception e) {
            LocalFileManager.sharedFileManager(null).deleteFileForURL(requestURL);
        }
    }


    protected HttpURLConnection doInBackground(String... params)
    {
        HttpURLConnection httpURLConnection = null;
        /*if (currentRequest == HttpRequestType.addProperty) {
            httpURLConnection = getMultipPartRequest(params);
        } else {*/
            httpURLConnection = this.getSimpleRequest(params);

        //}
        return httpURLConnection;
    }

    protected void onProgressUpdate(String sResponse) {

    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onPostExecute(HttpURLConnection httpURLConnection)
    {
        super.onPostExecute(httpURLConnection);

        switch (currentRequest) {
            case LogInRequest:
                AppDebugLog.println(responseContent);
                // ParseManager.parseLogInResponse(responseContent);
                break;
            case RegisterRequest:
                ParseManager.parseGeneralResponse(responseContent);
                break;
            case EditProfileRequest:
                ParseManager.parseGeneralResponse(responseContent);
                break;
            case ForgotPwdRequest:
                ParseManager.parseGeneralResponse(responseContent);
                break;
            case ChangePwdRequest:
                ParseManager.parseGeneralResponse(responseContent);
                break;
            case addProperty:
                break;
            case getPropertyInfo:
                break;
            default:
                break;
        }

        delegate.backgroundActivityComp(responseContent, currentRequest);
    }
}