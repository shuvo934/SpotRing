package ttit.com.shuvo.spotring.geofences.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ttit.com.shuvo.spotring.geofences.services.GeoFenceService;

public class StopAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, GeoFenceService.class);
        context.stopService(serviceIntent);
    }
}
