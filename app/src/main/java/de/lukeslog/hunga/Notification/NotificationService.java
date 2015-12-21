package de.lukeslog.hunga.Notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.joda.time.DateTime;

import java.text.DecimalFormat;

import de.lukeslog.hunga.R;
import de.lukeslog.hunga.support.HungaConstants;
import de.lukeslog.hunga.ui.Search;

public class NotificationService extends Service {

    private static final int COLOR_GREEN = 0x99cc00;
    private static final int COLOR_YELLOW = 0xFFFF66;
    private static final int COLOR_RED = 0xB22222;

    DecimalFormat df = new DecimalFormat("0.00");
    public static final String TAG = HungaConstants.TAG;
    private Updater updater;
    private Context ctx;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ctx=this;
        startUpdater();
        return START_STICKY;
    }

    private void startUpdater() {
        updater = new Updater();
        updater.run();
    }

    private void stopUpdater() {
        if (updater != null) {
            updater.onPause();
            updater = null;
        }
    }

    private class Updater implements Runnable {

        private Handler handler = new Handler();
        public static final int delay = 1000;
        long counter = 0;

        @Override
        public void run() {
            if(counter % 300 == 0) {
                //TODO: get values
            }
            if (counter % 60 == 0) {
                Log.d(TAG, "--");
                final SharedPreferences settings = ctx.getSharedPreferences("currentkcal", 0);
                DateTime d = new DateTime();
                String liquidName = "liquid"+d.getYear()+""+d.getMonthOfYear()+""+d.getDayOfMonth();
                String kcalName = "kcal"+d.getYear()+""+d.getMonthOfYear()+""+d.getDayOfMonth();
                Float kcal = settings.getFloat(kcalName, 0.0f);
                Float liquid = settings.getFloat(liquidName, 0.0f);
                Log.d(TAG, ""+kcal);
                PendingIntent pi = intentToMyActivity();
                int color = selectAppropriateColor(kcal);
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(ctx)
                                .setSmallIcon(R.drawable.hunga72)
                                .setContentTitle("Ernährung")
                                .setContentIntent(pi)
                                .setColor(color)
                                .setContentText("Aktueller Stand: "+df.format(kcal)+" kcal. ("+df.format(liquid)+" ml)");
                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                // mId allows you to update the notification later on.
                mNotificationManager.notify(99559, mBuilder.build());
            }
            counter++;
            handler.removeCallbacks(this);
            handler.postDelayed(this, delay);
        }

        public void onPause() {
            handler.removeCallbacks(this);
        }

        public void onResume() {
            handler.removeCallbacks(this); // remove the old callback
            handler.postDelayed(this, delay); // register a new one
        }
    }

    private int selectAppropriateColor(float kcal) {
        SharedPreferences defsettings = PreferenceManager.getDefaultSharedPreferences(this);
        String kcalgoalString = defsettings.getString("goal_kcal", "0");
        int kcalgoal = Integer.parseInt(kcalgoalString);
        if(kcalgoal>0) {
            if(kcal>kcalgoal) {
                return COLOR_RED;
            }
            if(kcal>(kcalgoal/100*90)) {
                return COLOR_YELLOW;
            }
        }
        return COLOR_GREEN;
    }

    private PendingIntent intentToMyActivity() {
        Intent intent=new Intent(ctx, Search.class);
        return PendingIntent.getActivity(ctx, 0, intent, 0);
    }
}
