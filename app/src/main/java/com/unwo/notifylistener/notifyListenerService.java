package com.unwo.notifylistener;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

public class notifyListenerService extends NotificationListenerService {
    public notifyListenerService() {
    }
    /*https://stackoverflow.com/questions/51468985/android-notification-listener-service-starting-by-itself
    */
/*
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }*/

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("********* SERVICE STARTED ***********");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        System.out.println("***** notification *****");
        String pack,title,text;
        Bundle extras;

        try {
            pack = sbn.getPackageName();
            extras = sbn.getNotification().extras;
            title = extras.getString("android.title").toString();
            text = extras.getCharSequence("android.text").toString();
        }catch (Exception e)
        {
            System.out.println("**** HATA NOTIFYSERVICE CLASS ****");
            pack="empty1";
            title="empty1";
            text="empty1";
            System.out.println("**** "+pack+" ****");
        }

        Log.i("Package",pack);
        Log.i("Title",title);
        Log.i("Text",text);

        Toast.makeText(this,"title: "+title+" text: "+text,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        System.out.println("***** destroyed *****");
        super.onDestroy();
    }
}
