/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.app.drylining.fcm;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.app.drylining.R;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.ui.ActivityNotifications;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import static com.app.drylining.fcm.FirebaseConfig.NOTIFICATION_ID;


public class MyFcmListenerService extends FirebaseMessagingService {
    private static final String TAG = "MyFcmListenerService";

    private String KEY_DATA = "data";
    private String KEY_TITLE = "title";
    private String KEY_MESSAGE = "message";
    private String KEY_RECORD_ID = "recordID";
    private String KEY_RECORD_TYPE = "type";
    private NotificationUtils notificationUtils;
    private ApplicationData appData;
    private int type = 1;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        appData = ApplicationData.getSharedInstance();
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().toString());
            handleNotification(remoteMessage.getNotification().getBody());

        }
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                sendPushNotification(json);
                //handleNotification(remoteMessage.getNotification().getBody());

            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {

        // app is in foreground, broadcast the push message
        Intent pushNotification = new Intent("pushNotification");
        pushNotification.putExtra("message", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);


        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        final int icon =R.drawable.notification;
        Bitmap icon1 = BitmapFactory.decodeResource(getApplicationContext().getResources(), icon);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            String id = "id_product";
            CharSequence name = "Product";
            String description = "Notifications regarding our products";

            @SuppressLint("WrongConstant")
            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_MAX);


            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.setShowBadge(true);
            mChannel.setLightColor(Color.RED);
            notificationManager.createNotificationChannel(mChannel);
            Intent notificationIntent = new Intent(getApplication(), ActivityNotifications.class);

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
         /*   notificationIntent.putExtra("message", "yes");
            notificationIntent.putExtra("senderId", data.getInt("uid"));
            notificationIntent.putExtra("firstName", data.getString("uname"));*/
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 123, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), "id_product")

                    .setSmallIcon(icon)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                 //   .setLargeIcon(icon1)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setChannelId("id_product")
                    .setContentTitle("DRYLINING")
                    .setAutoCancel(true)
                    .setSound(Uri.parse(String.valueOf(Notification.DEFAULT_SOUND)))
                    .setContentIntent(pendingIntent)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setColor(getResources().getColor(R.color.primary_color))
                    .setContentText(message)
                    .setWhen(System.currentTimeMillis())
                    /*   .setGroup(GROUP_KEY_EMAILS)
                       .setGroupSummary(true)*/
                    .build();
            NotificationManagerCompat notificationManager1 =
                    NotificationManagerCompat.from(this);
            notificationManager1.notify(NOTIFICATION_ID, notificationBuilder);

        }
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        Intent notificationIntent = new Intent(getApplication(), ActivityNotifications.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putExtra("message", "yes");
     /*   notificationIntent.putExtra("senderId", data.getInt("uid"));
        notificationIntent.putExtra("firstName", data.getString("uname"));*/
        PendingIntent contentIntent = PendingIntent.getActivity(this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

      //  Log.e(TAG, "handleDataMessage: " + StringEscapeUtils.unescapeJava(message).replace("\n", ""));
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(icon)
                        .setTicker("DRYLINING").setWhen(0)
                        .setAutoCancel(true)
                        .setContentTitle("DRYLINING")
                        .setSound(Uri.parse(String.valueOf(android.app.Notification.DEFAULT_SOUND)))
                        //  .setStyle(inboxStyle)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setPriority(Notification.PRIORITY_MAX)
                        .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                        .setContentIntent(contentIntent)
                        .setWhen(System.currentTimeMillis())
                        // .setSmallIcon(R.mipmap.ttlg2)
                       // .setLargeIcon(icon1)
                        .setContentText(message);
      /*  NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        notificationUtils.playNotificationSound();*/

        android.app.Notification notification = new NotificationCompat.BigTextStyle(mBuilder)
                .bigText( message).build();

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(100, notification);
        Intent iq = new Intent();
        iq.setAction("countrefresh");
        this.sendBroadcast(iq);


        Intent i = new Intent();
        i.setAction("appendChatScreenMsg");
        this.sendBroadcast(i);

    }

    //this method will display the notification
    //We are passing the JSONObject that is received from
    //firebase cloud messaging
    private void sendPushNotification(JSONObject json) {
        //optionally we can display the json into log
        Log.e(TAG, "Notification JSON " + json.toString());

        JSONObject notifyInfo = null;


        try {
            notifyInfo = new JSONObject(json.getString("notification"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //JSONObject notifyInfo = json;
        try {

            //parsing json data
            String title = notifyInfo.getString("title");

            if (title.equals("DRYLINING")) {
                String message = notifyInfo.getString("body");
                String userId = notifyInfo.getString("userId");

                appData.setUserId(userId);
                Log.e(TAG, "sendPushNotification: " + userId);
                Toast.makeText(this, userId, Toast.LENGTH_LONG).show();
                //creating MyNotificationManager object
                MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());

                //creating an intent for the notification
                Intent intent = new Intent(getApplicationContext(), ActivityNotifications.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                //displaying small notification
                mNotificationManager.showSmallNotification(title, message, intent);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    /*    Log.e(TAG, "push json: " + json.toString());

        try {
            JSONObject data = json.getJSONObject("title");

            String title = data.getString("title");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);


            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(FirebaseConfig.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                resultIntent.putExtra("message", message);

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }*/
    }
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}
