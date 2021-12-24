package com.unwo.FingenNotify;

import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import java.util.List;

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
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String pack, title, text;
        Bundle extras;


        try {
            pack = sbn.getPackageName();
            extras = sbn.getNotification().extras;
            title=extras.getString("android.title").toString();
            text=extras.getCharSequence("android.text").toString();
            Db db=new Db(this);
            List<Package> applications=db.getApplications();
            for (Package application :applications)
            {
                if (application.getName().equals(pack) || application.getName().equals("*"))
                {
                    if (db.getPreferenceValueBool(Constants.PREFERENCE_SAVE_NOTIFY,true))
                    {
                        db.addNotify(pack, application.getSender(), title + " " + text);
                    }
                    if (db.getPreferenceValueBool(Constants.PREFERENCE_SEND_FINGEN,true)) {
                        runIntent(pack, application.getSender(), title, text);
                    }

                    Intent intent = new Intent(Constants.BROADCAST_ACTION);
                    sendBroadcast(intent);
                }
            }


        }catch (Exception e)
        {
        }
    }

    private void runIntent(String pack,String sender,String title, String text)
    {
        SendFingen sf=new SendFingen(this,sender,text);
        //Toast.makeText(this,"pack: "+pack+" title: "+title+" text: "+text,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
