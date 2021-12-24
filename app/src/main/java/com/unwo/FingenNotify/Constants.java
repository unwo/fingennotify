package com.unwo.FingenNotify;

public class Constants {
    public static final String FINGEN_ACTION_NAME="com.yoshione.fingen.intent.action.CREATE_SMS";
    public static final String FINGEN_PACKAGE_NAME="com.yoshione.fingen";
    public static final String SENDER="sender";
    public static final String BODY="body";

    public static final int EDIT_PACKAGE=101;
    public static final int DELETE_PACKAGE=102;
    public static final int SEND_NOTIFY_FINGEN=103;
    public static final int DELETE_NOTIFY=104;
    public static final int DELETE_ALL_NOTIFY=105;

    public static final String DB_NAME="db";

    public static final String TABLE_PACKAGES="package";
    public static final String TABLE_NOTIFY="notify";
    public static final String TABLE_PREFERENCE="preference";

    public static final String TABLE_PACKAGES_COLUMN_ID="id";
    public static final String TABLE_PACKAGES_COLUMN_PACKAGE ="package";
    public static final String TABLE_PACKAGES_COLUMN_SENDER="sender";

    public static final String TABLE_NOTIFY_COLUMN_ID="id";
    public static final String TABLE_NOTIFY_COLUMN_PACKAGE ="package";
    public static final String TABLE_NOTIFY_COLUMN_SENDER="sender";
    public static final String TABLE_NOTIFY_COLUMN_MESSAGE="message";
    public static final String TABLE_NOTIFY_COLUMN_DATETIME="dt";

    public static final String TABLE_PREFERENCE_COLUMN_ID="id";
    public static final String TABLE_PREFERENCE_COLUMN_NAME ="name";
    public static final String TABLE_PREFERENCE_COLUMN_VALUE ="value";

    public static final String PREFERENCE_SAVE_NOTIFY ="savenotify";
    public static final String PREFERENCE_SEND_FINGEN ="sendfingen";

    public static final String DATETIME_FORMAT="HH:mm:ss dd.MM.yyyy";

    public final static String BROADCAST_ACTION = "com.unwo.FingenNotify.servicebackbroadcast";
}
