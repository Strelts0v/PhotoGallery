package com.vg.photogallery.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.vg.photogallery.R;
import com.vg.photogallery.controller.activities.PhotoGalleryActivity;
import com.vg.photogallery.model.GalleryItem;
import com.vg.photogallery.network.FlickrFetchr;
import com.vg.photogallery.util.QueryPreferences;

import java.util.List;

public class PollService extends IntentService {

    private static final String TAG = "PollService";

    private static final int POLL_INTERVAL = 1000 * 60; // 60 seconds

    public static final String ACTION_SHOW_NOTIFICATION =
            "com.vg.photogallery.SHOW_NOTIFICATION";

    public static final String PERM_PRIVATE =
            "com.vg.photogallery.PRIVATE";

    public static Intent newIntent(Context context) {
        return new Intent(context, PollService.class);
    }

    public PollService() {
        super(TAG);
    }

    /**
     * checks query string for searching photos, if it isn't null that called
     * search method from Flickr api, else uploads last 100 photos. Also it checks
     * last result of uploading photos, if they are different method configure
     * notification on top of the home screen
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isNetworkAvailableAndConnected()) {
            return;
        }

        Log.i(TAG, "Received an intent: " + intent);

        String query = QueryPreferences.getStoredQuery(this);
        String lastResultId = QueryPreferences.getLastResultId(this);

        List<GalleryItem> items;

        if (query == null) {
            items = new FlickrFetchr().fetchRecentPhotos();
        } else {
            items = new FlickrFetchr().searchPhotos(query);
        }

        if (items.size() == 0) {
            return;
        }
        String resultId = items.get(0).getId();
        if (resultId.equals(lastResultId)) {
            Log.i(TAG, "Got an old result: " + resultId);
        } else {
            Log.i(TAG, "Got a new result: " + resultId);

            // Create and configure notification on home screen of user phone
            Resources resources = getResources();
            Intent i = PhotoGalleryActivity.newIntent(this);
            PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
            Notification notification = new NotificationCompat.Builder(this)
                    // set text of running string
                    .setTicker(resources.getString(R.string.new_pictures_title))
                    // set icon on the top of user phone
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(resources.getString(R.string.new_pictures_title))
                    .setContentText(resources.getString(R.string.new_pictures_text))
                    .setContentIntent(pi)
                    // after calling of top service panel notification will be deleted
                    .setAutoCancel(true)
                    .build();

            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(this);
            notificationManager.notify(0, notification);
        }

        QueryPreferences.setLastResultId(this, resultId);
    }

    /**
     * Sets flag of alarm manager for notifications
     */
    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent i = PollService.newIntent(context);

        // Create special intent which will be executed later
        // 2nd param - code of request (with which it differences from others)
        // 4nd param - set of flags
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {
            // configure execution of pending intent
            // 1st param - constant for describing time base signal
            // 2nd - execution time of signal
            // 3rd - interval for repeating signal
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), POLL_INTERVAL, pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }

    /**
     * checks available network and valid connection of device with it
     * @return true - everything with connection is all right
     *         false - not
     */
    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable &&
                cm.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;
    }

    /**
     * checks flag of alarm for notifications
     * @return true - flag is set ON
     *         false - flag is set OFF
     */
    public static boolean isServiceAlarmOn(Context context) {
        Intent i = PollService.newIntent(context);
        PendingIntent pi = PendingIntent
                .getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }
}