package de.lukeslog.hunga.chromecast;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.RemoteMediaPlayer;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.ArrayList;
import java.util.List;

import de.lukeslog.hunga.R;
import de.lukeslog.hunga.support.HungaConstants;
import de.lukeslog.hunga.support.Logger;

public class ChromecastService extends Service {

    public static final String ACTION_DISPLAY_DATA = "displaydata";
    public static final String ACTION_FIND_DEVICES = "finddevices";
    public static final String ACTION_STOP_DISPLAYING = "stopdisplaydata";

    public static final String TAG = HungaConstants.TAG;
    private static ChromecastService ctx;

    private MediaRouter mMediaRouter;
    private MediaRouteSelector mMediaRouteSelector;
    private MediaRouter.Callback mMediaRouterCallback;
    private CastDevice mSelectedDevice;
    private GoogleApiClient.ConnectionCallbacks connectionCallbacks;
    private GoogleApiClient mApiClient;
    private String mSessionId;
    private boolean mApplicationStarted;
    private RemoteMediaPlayer mRemoteMediaPlayer;
    private boolean isPlaying;
    MediaMetadata mMediaMetadata;

    private static List<InfoOnRoute> infolist = new ArrayList<>();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ctx = this;
        findroute();
        return START_STICKY;

    }

    @Override
    public void onCreate() {
        Logger.d(TAG, "chromecast service");
        ctx = this;
        super.onCreate();

        registerIntentFilters();

        mMediaRouter = MediaRouter.getInstance(getApplicationContext());
        // Create a MediaRouteSelector for the type of routes your app supports
        mMediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(
                        CastMediaControlIntent.categoryForCast(getResources()
                                .getString(R.string.app_id))).build();
        // Create a MediaRouter callback for discovery events
        mMediaRouterCallback = new MyMediaRouterCallback();

        mRemoteMediaPlayer = new RemoteMediaPlayer();
        mRemoteMediaPlayer.setOnStatusUpdatedListener(
                new RemoteMediaPlayer.OnStatusUpdatedListener()
                {
                    @Override
                    public void onStatusUpdated()
                    {
                        MediaStatus mediaStatus = mRemoteMediaPlayer.getMediaStatus();
                        Logger.i(TAG, "Statusupdate->"+mediaStatus.getPlayerState());
                        if(mediaStatus.getPlayerState() == MediaStatus.PLAYER_STATE_PLAYING || mediaStatus.getPlayerState() == MediaStatus.PLAYER_STATE_BUFFERING)
                        {
                            Logger.d(TAG, "TRUE");
                            isPlaying = true;
                        }
                        if(mediaStatus.getPlayerState() == MediaStatus.PLAYER_STATE_IDLE)
                        {
                            Logger.d(TAG, "FALSE");
                            isPlaying = false;
                        }
                    }
                });
        Logger.d(TAG, "chromecast Service onCreate is done");

    }

    private void registerIntentFilters()
    {
        Logger.d(TAG, "register intent filters");
        IntentFilter inf = new IntentFilter(ACTION_DISPLAY_DATA);
        IntentFilter inf2 = new IntentFilter(ACTION_FIND_DEVICES);
        IntentFilter inf3 = new IntentFilter(ACTION_STOP_DISPLAYING);
        registerReceiver(mReceiver, inf);
        registerReceiver(mReceiver, inf2);
        registerReceiver(mReceiver, inf3);
    }

    @Override
    public void onDestroy()
    {
        endSession();
        super.onDestroy();
    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (action.equals(ACTION_DISPLAY_DATA))
            {
                Logger.d(TAG, "ACTION DISPLAY DATA");
                String devicename = intent.getStringExtra("ChromeCastDeviceName");
                selectRoute(devicename);
            }
            if(action.equals(ACTION_FIND_DEVICES))
            {
                Logger.d(TAG, "ACTION FIND DEVICES");
                findroute();
            }
            if(action.equals(ACTION_STOP_DISPLAYING))
            {
                endSession();
            }
        }
    };

    public static void stop()
    {
        if(ctx!=null)
        {
            ctx.endSession();
            ctx.stopSelf();
        }
    }

    private void selectRoute(String devicename)
    {
        for(InfoOnRoute info : infolist)
        {
            if(info.getInfo().getName().equals(devicename))
            {
                mMediaRouterCallback.onRouteSelected(info.getRouter(), info.getInfo());
            }
        }
    }

    private void endSession()
    {
        Logger.d(TAG, "endSession");
        try
        {
            mMediaRouter.removeCallback(mMediaRouterCallback);
        }
        catch (Exception e)
        {
            Logger.e(TAG, "endSession exception 0: "+e.getLocalizedMessage());
        }
        try
        {
            Cast.CastApi.stopApplication(mApiClient);
        }
        catch(Exception e)
        {
            Logger.e(TAG, "endSession exception 1: "+e.getLocalizedMessage());
        }
        try
        {
            mApiClient.disconnect();
            mApiClient = null;
            mSelectedDevice = null;
        }
        catch(Exception e)
        {
            Logger.e(TAG, "endSession exception 2: "+e.getLocalizedMessage());
        }
    }

    private void findroute()
    {
        Logger.d(TAG, "finroute()");
        mMediaRouter = MediaRouter.getInstance(getApplicationContext());
        mMediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(
                        CastMediaControlIntent
                                .categoryForCast(CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID)).build();
        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback,
                MediaRouter.CALLBACK_FLAG_PERFORM_ACTIVE_SCAN);
    }

    private void launchdevice()
    {
        Logger.d(TAG, "launchdev");
        Cast.Listener castlistener = new Cast.Listener()
        {

            @Override
            public void onApplicationDisconnected(int errorCode)
            {
                Logger.d(TAG, "application has stopped");
                endSession();
            }

        };
        Logger.d(TAG, "now for the callbacks...");
        connectionCallbacks = new ConnectionCallbacks();
        ConnectionFailedListener mConnectionFailedListener = new ConnectionFailedListener();
        Cast.CastOptions.Builder apiOptionsBuilder = Cast.CastOptions
                .builder(mSelectedDevice, castlistener);
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Cast.API, apiOptionsBuilder.build())
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(mConnectionFailedListener)
                .build();
        Logger.d(TAG, "now we try to connect...");
        mApiClient.connect();
    }

    /**
     * Google Play services callbacks
     */
    private class ConnectionCallbacks implements GoogleApiClient.ConnectionCallbacks
    {

        @Override
        public void onConnected(Bundle bundle)
        {
            Logger.d(TAG, "onConnected");
            try
            {
                if (mApiClient != null)
                {
                    Logger.d(TAG, "mApi is not null");
                    Cast.CastApi.launchApplication(mApiClient, CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID) .setResultCallback(
                            new ResultCallback<Cast.ApplicationConnectionResult>()
                            {
                                @Override
                                public void onResult(Cast.ApplicationConnectionResult applicationConnectionResult)
                                {
                                    try
                                    {
                                        Status status = applicationConnectionResult.getStatus();
                                        if (status.isSuccess())
                                        {
                                            displayimages();
                                        } else
                                        {
                                            Logger.d(TAG, "status was not succes...");
                                            endSession();
                                        }
                                    }
                                    catch(Exception e)
                                    {
                                        Logger.e(TAG, "Erroro in onResolt of Callback of the MastApi...");
                                    }
                                }
                            }
                    );
                }
            }
            catch(Exception e)
            {
                Logger.e(TAG, "exception!!! "+e.getLocalizedMessage());
                endSession();
            }
        }


        @Override
        public void onConnectionSuspended(int i)
        {
            Logger.d(TAG, "on connection suspended for callback");
        }
    }

    private void displayimages()
    {
        new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    Cast.CastApi.setMessageReceivedCallbacks(mApiClient, mRemoteMediaPlayer.getNamespace(), mRemoteMediaPlayer);
                    ArrayList<MediaInfo> minfo = new ArrayList<MediaInfo>();
                    minfo.add(createMediaInfo("WACKEN", "https://farm8.staticflickr.com/7337/13089923454_c71416ee95_o.jpg"));
                    minfo.add(createMediaInfo("website", "https://gist.github.com/jloutsenhizer/8855258"));
                    minfo.add(createMediaInfo("Who let the dog out", "https://farm3.staticflickr.com/2330/13087938985_92ba837732_o.jpg"));
                    minfo.add(createMediaInfo("Audio test", "http://ondemand-mp3.dradio.de/file/dradio/2014/05/02/dlf_20140502_1417_ecd04bcd.mp3"));
                    minfo.add(createMediaInfo("Seconds Audio Test", "http://ondemand-mp3.dradio.de/file/dradio/2014/05/02/dlf_20140502_1400_20ce31d2.mp3"));
                    minfo.add(createMediaInfo("pic1", "https://farm8.staticflickr.com/7440/13814963745_751edf24b1_o.jpg"));

                    for(MediaInfo mediaInfo : minfo)
                    {
                        loadMediaToRemotePlayer(mediaInfo);
                        Thread.sleep(5000); //stuff needs to load first... and media type has to change
                        if(mediaInfo.getMetadata().getMediaType()==MediaMetadata.MEDIA_TYPE_PHOTO)
                        {
                            Logger.d(TAG, "PHOTO...");
                            Thread.sleep(15000);
                        }
                        else
                        {
                            Logger.d(TAG, "not a photo...");
                            while(isPlaying)
                            {
                                Logger.d(TAG, "wait for stop playing...");
                                Thread.sleep(500);
                            }
                        }
                    }
                    Logger.d(TAG, "end session...?");
                    new Handler().post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            endSession();
                        }
                    });
                }
                catch(Exception e)
                {
                    Logger.e(TAG, "error...");
                    //endSession();

                }
            }
        }).start();
    }

    private MediaInfo createMediaInfo(String name, String url)
    {
        String contenttype="image/jpg";
        mMediaMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_PHOTO);
        if(url.endsWith("mp4"))
        {
            contenttype="video/mp4";
            mMediaMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        }
        if(url.endsWith("gif"))
        {
            contenttype="image/gif";
            mMediaMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_PHOTO);
        }
        if(url.endsWith("jpeg"))
        {
            contenttype="image/jpeg";
            mMediaMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_PHOTO);
        }
        if(url.endsWith("mpeg"))
        {
            contenttype="video/mpeg";
            mMediaMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        }
        if(url.endsWith("mp3"))
        {
            contenttype="audio/mpeg";
            mMediaMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MUSIC_TRACK);
        }
        mMediaMetadata.putString(MediaMetadata.KEY_TITLE, name);
        MediaInfo mediaInfo = new MediaInfo.Builder(
                url)
                .setContentType(contenttype)
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setMetadata(mMediaMetadata)
                .build();
        return mediaInfo;
    }

    private void loadMediaToRemotePlayer(MediaInfo mediaInfo)
    {
        try
        {
            mRemoteMediaPlayer.load(mApiClient, mediaInfo, true)
                    .setResultCallback(new ResultCallback<RemoteMediaPlayer.MediaChannelResult>()
                    {
                        @Override
                        public void onResult(RemoteMediaPlayer.MediaChannelResult mediaChannelResult)
                        {
                            Status status = mediaChannelResult.getStatus();
                            Logger.d(TAG, "on Result from image thing..." + status.getStatus());
                        }
                    });
        }
        catch (Exception e)
        {
            Logger.e(TAG, "Problem while loading media");
        }
    }


    private class MyMediaRouterCallback extends MediaRouter.Callback
    {

        @Override
        public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo info)
        {
            Logger.d(TAG, "onRouteSelected");
            // Handle route selection.

            mSelectedDevice = CastDevice.getFromBundle(info.getExtras());
            Logger.d(TAG, mSelectedDevice.getFriendlyName());
            launchdevice();

        }

        @Override
        public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo info)
        {
            Logger.d(TAG, "onRouteUnselected: info=" + info);
            mSelectedDevice = null;
        }

        @Override
        public void onRouteAdded(MediaRouter router, MediaRouter.RouteInfo info)
        {
            Logger.d(TAG, "Route Added");
            Logger.d(TAG, router.getDefaultRoute().getName());
            Logger.d(TAG, router.getDefaultRoute().getId());
            Logger.d(TAG, info.getName());
            Logger.d(TAG, info.getId());
            InfoOnRoute ior = new InfoOnRoute(router, info);
            for(InfoOnRoute ix : infolist)
            {
                if(ix.getInfo().getName().equals(info.getName()))
                {
                    infolist.remove(ix);
                }
            }
            infolist.add(ior);
            selectRoute(info.getName());
        }
    }

    /**
     * Google Play services callbacks
     */
    private class ConnectionFailedListener implements GoogleApiClient.OnConnectionFailedListener
    {
        @Override
        public void onConnectionFailed(ConnectionResult result)
        {
            Logger.e(TAG, "onConnectionFailed ");

            endSession();
        }
    }

    private class InfoOnRoute
    {
        private MediaRouter router;
        private MediaRouter.RouteInfo info;

        public InfoOnRoute(MediaRouter router, MediaRouter.RouteInfo info)
        {
            this.router = router;
            this.info = info;
        }

        public MediaRouter getRouter()
        {
            return router;
        }

        public MediaRouter.RouteInfo getInfo()
        {
            return info;
        }
    }
}