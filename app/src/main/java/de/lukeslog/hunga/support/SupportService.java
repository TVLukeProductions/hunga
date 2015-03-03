package de.lukeslog.hunga.support;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

public class SupportService extends Service {

    private static SupportService context;
    public static final String TAG = HungaConstants.TAG;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(TAG, "SERVICE On Start Command");
        context = this;
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.d(TAG, "SERVICE on create");
    }

    public void stop()
    {
        Logger.d(TAG, "Service stop()");
        stopSelf();
    }

    public static void stopService() {
        if(serviceRunning()) {
            context.stop();
        }
    }

    public static SharedPreferences getDefaultSettings() {
        if(SupportService.serviceRunning()) {
            return PreferenceManager.getDefaultSharedPreferences(SupportService.getContext());
        }
        return null;
    }

    private static boolean serviceRunning() {
        return context!=null;
    }

    public static Context getContext() {
        return context;
    }
}
