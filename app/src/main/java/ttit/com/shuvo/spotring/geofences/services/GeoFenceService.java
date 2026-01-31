package ttit.com.shuvo.spotring.geofences.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import ttit.com.shuvo.spotring.R;
import ttit.com.shuvo.spotring.alarm_ui.AlarmStartUp;
import ttit.com.shuvo.spotring.geofences.receiver.StopAlarmReceiver;

public class GeoFenceService extends Service {

    private Vibrator vibrator;
    private Ringtone ringtone;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        new Handler(Looper.getMainLooper()).postDelayed(this::openAlarmActivity, 1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String event_name = intent.getStringExtra("EV_NA");
        String event_notes = intent.getStringExtra("EV_NO");
        String event_type = intent.getStringExtra("EV_TY");
        String tr_type = intent.getStringExtra("TR_TY");

        Notification notification = buildNotification(event_name, event_notes, event_type,tr_type);

        startForeground(1, notification);

        // Wake up the screen before starting activity
//        wakeUpScreen();

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            long[] pattern = {0, 1000, 1000}; // Vibrate for 1s, pause for 1s
            vibrator.vibrate(pattern, 0); // Loop indefinitely
        }

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmSound);
        if (ringtone != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ringtone.setLooping(true);
            }
            ringtone.play();
        }

//        Intent activityIntent = new Intent(this, AlarmStartUp.class);
//        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(activityIntent);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (vibrator != null) {
            vibrator.cancel(); // Stop vibration
        }
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop(); // Stop ringtone
        }
    }

    private Notification buildNotification(String ev_name, String ev_notes, String ev_type, String tr_ty) {
        Intent stopIntent = new Intent(this, StopAlarmReceiver.class);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(
                this, 1110, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Intent activityIntent = new Intent(this, AlarmStartUp.class);
        activityIntent.putExtra("EV_NA", ev_name);
        activityIntent.putExtra("EV_NO",ev_notes);
        activityIntent.putExtra("EV_TY",ev_type);
        activityIntent.putExtra("TR_TY",tr_ty);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingActivityIntent = PendingIntent.getActivity(
                this, 2525, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Intent fullScreenIntent = new Intent(this, AlarmStartUp.class);
        fullScreenIntent.putExtra("EV_NA", ev_name);
        fullScreenIntent.putExtra("EV_NO",ev_notes);
        fullScreenIntent.putExtra("EV_TY",ev_type);
        fullScreenIntent.putExtra("TR_TY",tr_ty);
        fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(
                this, 1233, fullScreenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build the notification with the STOP button
        return new NotificationCompat.Builder(this, getString(R.string.notify_channel))
                .setContentTitle(tr_ty +": "+ev_name)
                .setContentText(ev_notes.isEmpty() ? ev_type : ev_notes)
                .setSmallIcon(R.drawable.spotring_icon_new)
                .addAction(R.drawable.ic_stop_circle, "STOP", stopPendingIntent) // STOP button
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(pendingActivityIntent)
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setOngoing(true) // Prevents user from swiping away notification
                .build();
    }

//    private void openAlarmActivity() {
//        Intent intent = new Intent(this, AlarmStartUp.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//    }

//    private void wakeUpScreen() {
//        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        if (powerManager != null) {
//            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
//                    PowerManager.ACQUIRE_CAUSES_WAKEUP,
//                    "AlarmService:WakeLock");
//            wakeLock.acquire(5000); // Wake up screen for 5 seconds
//            wakeLock.release();
//        }
//    }
}
