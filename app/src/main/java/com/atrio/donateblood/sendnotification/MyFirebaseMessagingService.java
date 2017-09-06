package com.atrio.donateblood.sendnotification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.atrio.donateblood.R;
import com.atrio.donateblood.model.RecipientDetail;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Arpita Patel on 22-08-2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private DatabaseReference db_ref;
    private FirebaseDatabase db_instance;
   /* private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.REGISTRATION_COMPLETE);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();

            Intent resultIntent = new Intent(getApplicationContext(), NotifiyActivity.class);
            resultIntent.putExtra("message", message);
        }else{
            // If the app is in background, firebase itself handles the notification

        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.REGISTRATION_COMPLETE);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), NotifiyActivity.class);
                resultIntent.putExtra("message", message);

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            }

        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    *//**
     * Showing notification with text only
     *//*
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    *//**
     * Showing notification with text and image
     *//*
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}
*/
    private static final String TAG = "FirebaseMessageService";
    Bitmap bitmap;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        //


        Log.i("Checklog",""+remoteMessage.getData());
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.i("FCM**", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        //The message which i send will have keys named [message, image, AnotherActivity] and corresponding values.
        //You can change as per the requirement.

        //message will contain the Push Message
        String tittle = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();

        String click_action = remoteMessage.getNotification().getClickAction();
//        Log.i("message_id",""+message_id);
        //imageUri will contain URL of the image to be displayed with Notification
        String imageUri = remoteMessage.getData().get("image");
        String token_id = remoteMessage.getData().get("token_id");
//        String msg_id = remoteMessage.getData().get("msg_id");
        String EmailId = remoteMessage.getData().get("Email");
                String phoneNo = remoteMessage.getData().get("phoneNo");
        String bloodData = remoteMessage.getData().get("bloodData");
                String cityData = remoteMessage.getData().get("cityData");
        String stateData = remoteMessage.getData().get("stateData");
                String dateRequired = remoteMessage.getData().get("dateRequired");
                String other_detail = remoteMessage.getData().get("other_detail");
        String msg_id=remoteMessage.getMessageId();
        Log.i("EmailId44",""+token_id);
        Log.i("phoneNo",""+msg_id);
        Log.i("tittle44",""+tittle);
        Log.i("message44",""+body);
        //Log.i("EmailId44",""+EmailId);

        //To get a Bitmap image from the URL received
        bitmap = getBitmapfromUrl(imageUri);
        createRecipientDetail(stateData, bloodData, EmailId, phoneNo, dateRequired, cityData, other_detail,msg_id,body);

        sendNotification(tittle,body, bitmap ,token_id,msg_id,click_action);
//        storeNotification(remoteMessage.getData());
    }

/*
    private void storeNotification(Map<String, String> data) {
        Log.i("bloodData456",""+data.toString());

        Intent intentnoti = new Intent(this, NotificationActivity.class);
        intentnoti.putExtra("datamy", data.toString());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentnoti);
    }
*/


    /**
     * Create and show a simple notification containing the received FCM message.
     */

    private void sendNotification(String tittle, String messageBody, Bitmap image, String token_id, String msg_id,String click_action) {
        Intent intent = new Intent(click_action);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("token_id", token_id);
        intent.putExtra("msg_id", msg_id);
//        intent.putExtra("click_action",click_action);
        Log.i("msg_45",""+msg_id);
        Log.i("msg_45",""+intent);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder .setLargeIcon(image)
            .setSmallIcon(R.drawable.ic_explore_black_24dp)
                    .setContentTitle(tittle)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

        } else {

           notificationBuilder .setLargeIcon(image)
                    .setSmallIcon(R.drawable.ic_explore_black_24dp)
                    .setContentTitle(tittle)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
        }


        NotificationManager notificationManager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }
    private void createRecipientDetail(String state_data, String blood_data, String emailid, String phoneno, String date_req, String city_data, String other_detail, String msg_id,String body) {
//        msg_count++;
        db_instance = FirebaseDatabase.getInstance();
        db_ref = db_instance.getReference();
        RecipientDetail recipientDetail=new RecipientDetail();

        recipientDetail.setReq_date(date_req);
        recipientDetail.setEmailid(emailid);
        recipientDetail.setPhoneno(phoneno);
        recipientDetail.setMsg_id(msg_id);
        recipientDetail.setOther_detail(other_detail);
        recipientDetail.setBloodgroup(blood_data);
        recipientDetail.setState(state_data);
        recipientDetail.setCity(city_data);
        recipientDetail.setBody(body);

        db_ref.child("Notification").child("Recipient").child(msg_id).setValue(recipientDetail);
//        Log.i("Mainresult:45689 ", "" + store_list);

    }

}