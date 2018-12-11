package com.app.drylining.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.app.drylining.DRYLINING;
import com.quickblox.users.model.QBUser;

public class AppInfo {
    private static AppInfo instance = null;

    public static int JOBS      = 201;
    public static int MY_JOB    = 202;

    private boolean m_isLogined = false;

    private QBUser m_qbUser;
    private int m_jobType = JOBS;

    public static AppInfo getInstance() {
        if(instance == null) instance = new AppInfo();

        return instance;
    }

    public void setLogin() {
        m_isLogined = true;
        save();
    }
    public boolean isLogined() { return m_isLogined; }

    public void setQbUser(QBUser user) { m_qbUser = user; }
    public QBUser qbUser() { return m_qbUser; }

    public void setJobType(int jobType) { m_jobType = jobType; }
    public int jobType() { return m_jobType; }

    public void load() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(DRYLINING.getAppContext());

        m_isLogined = settings.getBoolean("isLogined", false);


    }

    public void save() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(DRYLINING.getAppContext());
        SharedPreferences.Editor prefEditor = settings.edit();

        prefEditor.putBoolean("isLogined", m_isLogined);

        prefEditor.commit();
    }
}
