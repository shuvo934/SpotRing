package ttit.com.shuvo.spotring.geofences.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;

import ttit.com.shuvo.spotring.geofences.services.GeoFenceService;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
// Wake up the screen
//        PowerManager.WakeLock screenWakeLock = powerManager.newWakeLock(
//                PowerManager.ACQUIRE_CAUSES_WAKEUP,
//                "AlarmReceiver:ScreenWakeLock");
//        screenWakeLock.acquire(3000);

// Keep the CPU awake
        PowerManager.WakeLock cpuWakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "AlarmReceiver:WakeLock");
        cpuWakeLock.acquire(3000);

//        Intent alarmIntent = new Intent(context, AlarmStartUp.class);
//        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        context.startActivity(alarmIntent);

        // Release wake lock
//        cpuWakeLock.release();
        Intent serviceIntent = new Intent(context, GeoFenceService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent); // Required for Android 8+
        } else {
            context.startService(serviceIntent);
        }
    }
}
