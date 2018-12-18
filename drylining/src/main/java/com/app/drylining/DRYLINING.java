package com.app.drylining;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.data.ApplicationData;

public class DRYLINING extends Application {
    private static Context context;
    private static DRYLINING instance = null;

    public DRYLINING()
    {

    }
    public void onCreate() {
        super.onCreate();
        instance = this;
        context = getApplicationContext();
    }

    public static DRYLINING getInstance() {
        return instance;
    }

    public static Context getAppContext() {

        AppDebugLog.println("context : " + context);
        return context;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ApplicationData.getSharedInstance().getDbManager().close();
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
