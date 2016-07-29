package com.ibm.mfpstarterandroid;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPush;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushException;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationListener;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushResponseListener;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPSimplePushNotification;
import com.worklight.wlclient.api.*;
import com.worklight.wlclient.auth.AccessToken;

public class ServerConnectActivity extends AppCompatActivity implements OnClickListener, MFPPushNotificationListener {

    private static final String TAG = "ServerConnectActivity";

    private WLClient client;
    private MFPPush push;

    private WebView webViewLabel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupMobileFirst();

        setContentView(R.layout.content_webview);
        webViewLabel = (WebView) findViewById(R.id.webview_id);

        // Enable remote debug (in Chrome DevTool)
        webViewLabel.setWebContentsDebuggingEnabled(true);

        // Enable hardware acceleration
        webViewLabel.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        // Enable Javascript
        WebSettings webSettings = webViewLabel.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Set user agent string (pretend we are Worklight)
        String userAgent = webSettings.getUserAgentString();
        webSettings.setUserAgentString(userAgent + "/Worklight/8.0.0.0");

        // Force links and redirects to open in the WebView instead of in a browser
        webViewLabel.setWebViewClient(new WebViewClient());

        // Load remote URL
        webViewLabel.loadUrl("http://graysonline.ibmcollabcloud.com/wps/portal/Home/home");
    }

    private void setupMobileFirst() {
        client = WLClient.createInstance(this);

        MFPPush.getInstance().initialize(this);

        push = MFPPush.getInstance();
        push.listen(this);

        push.registerDevice(null, new MFPPushResponseListener<String>() {
            @Override
            public void onSuccess(String s) {
                Log.i(TAG, "Registered successfully: " + s);
            }

            @Override
            public void onFailure(MFPPushException e) {
                Log.i(TAG, "Failed to register with error: " + e.toString());
            }
        });

        WLAuthorizationManager.getInstance().obtainAccessToken("", new WLAccessTokenListener() {
            @Override
            public void onSuccess(AccessToken token) {
                Log.i(TAG, "Received the following access token value: " + token);
            }

            @Override
            public void onFailure(WLFailResponse wlFailResponse) {
                Log.i(TAG, "Did not receive an access token from server: " + wlFailResponse.getErrorMsg());
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (webViewLabel.canGoBack()) {
            webViewLabel.goBack();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ping_mobilefirst_btn) {
            // do something
        }
    }

    @Override
    public void onReceive(MFPSimplePushNotification mfpSimplePushNotification) {
        Log.i(TAG, "received push notification: " + mfpSimplePushNotification.getAlert());

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, ServerConnectActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_ONE_SHOT);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setLargeIcon(icon)
                        .setSmallIcon(R.drawable.push_notification_icon)
                        .setContentTitle("GraysOnline - Auction Notification")
                        .setContentText(mfpSimplePushNotification.getAlert())
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setFullScreenIntent(pendingIntent, false)
                        .setAutoCancel(true);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(ServerConnectActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }
}