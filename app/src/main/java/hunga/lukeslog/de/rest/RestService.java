package hunga.lukeslog.de.rest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import hunga.lukeslog.de.support.HungaConstants;
import hunga.lukeslog.de.support.Logger;

public class RestService extends Service {

    private static RestService context;
    public static final String TAG = HungaConstants.TAG;

    private static boolean loading = false;

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

    public static boolean isLoading() {
        return loading;
    }

    public static void setLoading(boolean l) {
        loading = l;
    }

    public static void updateLists() {
        if(context!=null) {
            setLoading(true);
            Downloader.downloadFoodInformation();
        }
    }
}
