package com.unwo.FingenNotify;

import android.content.Context;
import android.content.Intent;

public class SendFingen {

    public SendFingen(Context context, String sender, String text)
    {
        Intent intent = new Intent();
        intent.setAction(Constants.FINGEN_ACTION_NAME);
        intent.setPackage(Constants.FINGEN_PACKAGE_NAME);
        intent.putExtra(Constants.SENDER, sender);
        intent.putExtra(Constants.BODY, text);
        context.sendBroadcast(intent);
    }
}
