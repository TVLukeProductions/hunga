package de.lukeslog.hunga.support;

import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import org.joda.time.DateTime;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger
{
    public static void i(String TAG, String m)
    {
        SharedPreferences settings = SupportService.getDefaultSettings();
        if(settings!=null)
        {
            if (settings.getBoolean("pref_looging", false))
            {
                Log.i(TAG, m);
            }
            if (settings.getBoolean("pref_looging_file", false))
            {
                store(TAG, m);
            }
        }
        else
        {
            Log.i(TAG, m);
        }
    }

    public static void d(String TAG, String m)
    {
        SharedPreferences settings = SupportService.getDefaultSettings();
        if(settings!=null)
        {
            if (settings.getBoolean("pref_looging", false))
            {
                Log.d(TAG, m);
            }
            if (settings.getBoolean("pref_looging_file", false))
            {
                store(TAG, m);
            }
        }
        else
        {
            Log.d(TAG, m);
        }
    }

    public static void e(String TAG, String m)
    {
        SharedPreferences settings = SupportService.getDefaultSettings();
        if(settings!=null)
        {
            if (settings.getBoolean("pref_looging", false))
            {
                Log.e(TAG, m);
            }
            if (settings.getBoolean("pref_looging_file", false))
            {
                store(TAG, m);
            }
        }
        else
        {
            Log.e(TAG, m);
        }
    }

    public static void v(String TAG, String m)
    {
        SharedPreferences settings = SupportService.getDefaultSettings();
        if(settings!=null)
        {
            if (settings.getBoolean("pref_looging", false))
            {
                Log.v(TAG, m);
            }
            if (settings.getBoolean("pref_looging_file", false))
            {
                store(TAG, m);
            }
        }
        else
        {
            Log.v(TAG, m);
        }
    }

    public static void w(String TAG, String m)
    {
        SharedPreferences settings = SupportService.getDefaultSettings();
        if(settings!=null)
        {
            if (settings.getBoolean("pref_looging", false))
            {
                Log.w(TAG, m);
            }
            if (settings.getBoolean("pref_looging_file", false))
            {
                store(TAG, m);
            }
        }
        else
        {
            Log.w(TAG, m);
        }
    }

    public static void wtf(String TAG, String m)
    {
        SharedPreferences settings = SupportService.getDefaultSettings();
        if(settings!=null)
        {
            if (settings.getBoolean("pref_looging", false))
            {
                Log.wtf(TAG, m);
            }
            if (settings.getBoolean("pref_looging_file", false))
            {
                store(TAG, m);
            }
        }
        else
        {
            Log.wtf(TAG, m);
        }
    }

    private static boolean store(String TAG, String m)
    {
        DateTime d = new DateTime();
        String log="TrashPlayLogFile.txt";
        File Root = Environment.getExternalStorageDirectory();
        if(Root.canWrite())
        {
            File logFile = new File(Root, log);
            if (!logFile.exists())
            {
                try
                {
                    logFile.createNewFile();
                }
                catch (IOException e)
                {

                    e.printStackTrace();
                }
            }
            try
            {
                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                //BufferedWriter for performance, true to set append to file flag
                String logString = d.getDayOfMonth()+"-"+d.getMonthOfYear()+"-"+d.getYear()+" "+d.getHourOfDay()+":"+d.getMinuteOfHour()+":"+d.getSecondOfMinute()+"  "+TAG+" "+m+"\n";
                buf.append(logString);
                buf.close();
            }
            catch (IOException e)
            {

                e.printStackTrace();
            }
        }
        return true;
    }
}