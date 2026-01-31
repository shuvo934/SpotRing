package ttit.com.shuvo.spotring.geofences.receiver;

import static ttit.com.shuvo.spotring.utilities.Constants.KEY_GEO_ALERT_TYPE_ID;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_GEO_EVENT_NAME;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_GEO_EVENT_NOTES;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_GEO_EVENT_TYPE_NAME;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_GEO_ID_FOR_TIME;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_GEO_IS_ACTIVE;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_GEO_REPEAT_BEGIN_DATE;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_GEO_REPEAT_BEGIN_TIME;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_GEO_REPEAT_END_DATE;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_GEO_REPEAT_END_TIME;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_GEO_REPEAT_ID;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_GEO_TIME_TABLE_NAME;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_GEO_TRIGGER_TYPE_ID;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_LOC_ID;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_LOC_TABLE_NAME;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ttit.com.shuvo.spotring.R;
import ttit.com.shuvo.spotring.geofences.model.CustomRepetitionDataList;
import ttit.com.shuvo.spotring.geofences.services.GeoFenceService;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceBroadcastRcv";
    ArrayList<CustomRepetitionDataList> dataLists;
    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//// Wake up the screen
//        PowerManager.WakeLock screenWakeLock = powerManager.newWakeLock(
//                PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
//                "AlarmReceiver:ScreenWakeLock");
//        screenWakeLock.acquire(3000);

// Keep the CPU awake
        PowerManager.WakeLock cpuWakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "AlarmReceiver:WakeLock");
        cpuWakeLock.acquire(3000);

        if (geofencingEvent != null) {
            if (geofencingEvent.hasError()) {
                String errorMessage = GeofenceStatusCodes
                        .getStatusCodeString(geofencingEvent.getErrorCode());
                Log.e(TAG, errorMessage);
                return;
            }


            // Get the transition type.
            int geofenceTransition = geofencingEvent.getGeofenceTransition();

            List<Geofence> triggeringGeoFences = geofencingEvent.getTriggeringGeofences();
            ArrayList<String> geoIDs = new ArrayList<>();
            if (triggeringGeoFences != null) {
                for (int i = 0; i < triggeringGeoFences.size(); i++) {
                    geoIDs.add(triggeringGeoFences.get(i).getRequestId());
                }
            }

            getGeoData(geoIDs, geofenceTransition,context);

        }
    }

    public void getGeoData(ArrayList<String> geo_ids, int transition, Context context) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        for (int i = 0; i < geo_ids.size(); i++) {
            String id = geo_ids.get(i);
            database.collection(KEY_LOC_TABLE_NAME).whereEqualTo(KEY_LOC_ID,id)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null && !task.getResult().getDocuments().isEmpty()) {
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            String event_name = documentSnapshot.getString(KEY_GEO_EVENT_NAME);
                            String notes = documentSnapshot.getString(KEY_GEO_EVENT_NOTES);
                            String event_type = documentSnapshot.getString(KEY_GEO_EVENT_TYPE_NAME);

                            String trigger_id = documentSnapshot.getString(KEY_GEO_TRIGGER_TYPE_ID);
                            String alarm_id = documentSnapshot.getString(KEY_GEO_ALERT_TYPE_ID);
                            String repeat_id = documentSnapshot.getString(KEY_GEO_REPEAT_ID);
                            Boolean isActive = documentSnapshot.getBoolean(KEY_GEO_IS_ACTIVE);

                            if (Boolean.TRUE.equals(isActive)) {
                                if (repeat_id != null) {
                                    if (repeat_id.equals("3")) {
                                        dataLists = new ArrayList<>();
                                        database.collection(KEY_GEO_TIME_TABLE_NAME).whereEqualTo(KEY_GEO_ID_FOR_TIME,id)
                                                .get()
                                                .addOnCompleteListener(task1 -> {
                                                    if (task1.getResult() != null && !task1.getResult().getDocuments().isEmpty()) {
                                                        List<DocumentSnapshot> documentSnapshots = task1.getResult().getDocuments();
                                                        for (int j = 0; j < documentSnapshots.size(); j++) {
                                                            String bd = documentSnapshots.get(j).getString(KEY_GEO_REPEAT_BEGIN_DATE);
                                                            String ed = documentSnapshots.get(j).getString(KEY_GEO_REPEAT_END_DATE);
                                                            String bt = documentSnapshots.get(j).getString(KEY_GEO_REPEAT_BEGIN_TIME);
                                                            String et = documentSnapshots.get(j).getString(KEY_GEO_REPEAT_END_TIME);
                                                            dataLists.add(new CustomRepetitionDataList(bd,ed,bt,et,false));
                                                        }
                                                        setAlert(id,event_name,notes,event_type,trigger_id,alarm_id,repeat_id,transition,context,dataLists);
                                                    }
                                                });
                                    }
                                    else {
                                        setAlert(id,event_name,notes,event_type,trigger_id,alarm_id,repeat_id,transition,context,new ArrayList<>());
                                    }
                                }
                            }
                        }
                    });
        }

    }

    @SuppressLint("MissingPermission")
    public void setAlert(String geo_id, String event_name, String notes, String event_type, String trigger_id, String alarm_id, String repeat_id, int transition, Context context, ArrayList<CustomRepetitionDataList> customRepetitionDataLists) {
        switch (repeat_id) {
            case "3":
                if (!customRepetitionDataLists.isEmpty()) {
                    int expire = 0;
                    for (int i = 0; i < customRepetitionDataLists.size(); i++) {
                        String b_d = customRepetitionDataLists.get(i).getBegin_date();
                        String e_d = customRepetitionDataLists.get(i).getEnd_date();
                        String b_t = customRepetitionDataLists.get(i).getBegin_time();
                        String e_t = customRepetitionDataLists.get(i).getEnd_time();

                        Calendar calendar = Calendar.getInstance();
                        Date to_date = calendar.getTime();

                        if (!b_d.isEmpty() && !e_d.isEmpty()) {

                            SimpleDateFormat da = new SimpleDateFormat("dd MMM, yy", Locale.ENGLISH);
                            String n_date = da.format(to_date);

                            Date date = null;
                            Date b_date = null;
                            Date e_date = null;

                            try {
                                date = da.parse(n_date);
                                b_date = da.parse(b_d);
                                e_date = da.parse(e_d);
                            } catch (ParseException e) {
                                String er = e.getLocalizedMessage();
                                System.out.println(er);
                            }

                            if (date != null && b_date != null && e_date != null) {
                                if (date.equals(b_date) || date.equals(e_date) || (date.after(b_date) && date.before(e_date))) {

                                    if (!b_t.isEmpty() && !e_t.isEmpty()) {
                                        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                                        String t_n_date = timeFormat.format(to_date);

                                        Date date_time = null;
                                        Date b_time = null;
                                        Date e_time = null;

                                        try {
                                            date_time = timeFormat.parse(t_n_date);
                                            b_time = timeFormat.parse(b_t);
                                            e_time = timeFormat.parse(e_t);
                                        } catch (ParseException e) {
                                            String er = e.getLocalizedMessage();
                                            System.out.println(er);
                                        }

                                        if (date_time != null && b_time != null && e_time != null) {
                                            if (date_time.equals(b_time) || date_time.equals(e_time) || (date_time.after(b_time) && date_time.before(e_time))) {

                                                switch (transition) {
                                                    case Geofence.GEOFENCE_TRANSITION_ENTER:
                                                        if (trigger_id.equals("1") || trigger_id.equals("4") || trigger_id.equals("5") || trigger_id.equals("7")) {
                                                            if (alarm_id.equals("1")) {
                                                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notify_channel))
                                                                        .setSmallIcon(R.drawable.spotring_icon_new)
                                                                        .setContentTitle("You have entered your Area for: " + event_name)
                                                                        .setContentText(notes.isEmpty() ? event_type : notes)
                                                                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                                                                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                                                                notificationManagerCompat.notify(1212, builder.build());
                                                            } else {
                                                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notify_channel))
                                                                        .setSmallIcon(R.drawable.spotring_icon_new)
                                                                        .setContentTitle("You have entered your Area for: " + event_name)
                                                                        .setContentText(notes.isEmpty() ? event_type : notes)
                                                                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                                                                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                                                                notificationManagerCompat.notify(1212, builder.build());
                                                                Intent serviceIntent = new Intent(context, GeoFenceService.class);
                                                                serviceIntent.putExtra("EV_NA", event_name);
                                                                serviceIntent.putExtra("EV_NO", notes);
                                                                serviceIntent.putExtra("EV_TY", event_type);
                                                                serviceIntent.putExtra("TR_TY", "You have entered your Area for");
                                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                                    context.startForegroundService(serviceIntent); // Required for Android 8+
                                                                } else {
                                                                    context.startService(serviceIntent);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    case Geofence.GEOFENCE_TRANSITION_EXIT:
                                                        if (trigger_id.equals("2") || trigger_id.equals("4") || trigger_id.equals("6") || trigger_id.equals("7")) {
                                                            if (alarm_id.equals("1")) {
                                                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notify_channel))
                                                                        .setSmallIcon(R.drawable.spotring_icon_new)
                                                                        .setContentTitle("You have exited your Area for: " + event_name)
                                                                        .setContentText(notes.isEmpty() ? event_type : notes)
                                                                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                                                                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                                                                notificationManagerCompat.notify(1212, builder.build());
                                                            } else {
                                                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notify_channel))
                                                                        .setSmallIcon(R.drawable.spotring_icon_new)
                                                                        .setContentTitle("You have exited your Area for: " + event_name)
                                                                        .setContentText(notes.isEmpty() ? event_type : notes)
                                                                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                                                                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                                                                notificationManagerCompat.notify(1212, builder.build());
                                                                Intent serviceIntent = new Intent(context, GeoFenceService.class);
                                                                serviceIntent.putExtra("EV_NA", event_name);
                                                                serviceIntent.putExtra("EV_NO", notes);
                                                                serviceIntent.putExtra("EV_TY", event_type);
                                                                serviceIntent.putExtra("TR_TY", "You have exited your Area for");
                                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                                    context.startForegroundService(serviceIntent); // Required for Android 8+
                                                                } else {
                                                                    context.startService(serviceIntent);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    case Geofence.GEOFENCE_TRANSITION_DWELL:
                                                        if (trigger_id.equals("3") || trigger_id.equals("5") || trigger_id.equals("6") || trigger_id.equals("7")) {
                                                            if (alarm_id.equals("1")) {
                                                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notify_channel))
                                                                        .setSmallIcon(R.drawable.spotring_icon_new)
                                                                        .setContentTitle("You are in your Area for: " + event_name)
                                                                        .setContentText(notes.isEmpty() ? event_type : notes)
                                                                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                                                                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                                                                notificationManagerCompat.notify(1212, builder.build());
                                                            } else {
                                                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notify_channel))
                                                                        .setSmallIcon(R.drawable.spotring_icon_new)
                                                                        .setContentTitle("You are in your Area for: " + event_name)
                                                                        .setContentText(notes.isEmpty() ? event_type : notes)
                                                                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                                                                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                                                                notificationManagerCompat.notify(1212, builder.build());
                                                                Intent serviceIntent = new Intent(context, GeoFenceService.class);
                                                                serviceIntent.putExtra("EV_NA", event_name);
                                                                serviceIntent.putExtra("EV_NO", notes);
                                                                serviceIntent.putExtra("EV_TY", event_type);
                                                                serviceIntent.putExtra("TR_TY", "You are in your Area for");
                                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                                    context.startForegroundService(serviceIntent); // Required for Android 8+
                                                                } else {
                                                                    context.startService(serviceIntent);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                }
                                            }
                                        }
                                    } else {

                                        switch (transition) {
                                            case Geofence.GEOFENCE_TRANSITION_ENTER:
                                                if (trigger_id.equals("1") || trigger_id.equals("4") || trigger_id.equals("5") || trigger_id.equals("7")) {
                                                    if (alarm_id.equals("1")) {
                                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notify_channel))
                                                                .setSmallIcon(R.drawable.spotring_icon_new)
                                                                .setContentTitle("You have entered your Area for: " + event_name)
                                                                .setContentText(notes.isEmpty() ? event_type : notes)
                                                                .setPriority(NotificationCompat.PRIORITY_HIGH);

                                                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                                                        notificationManagerCompat.notify(1212, builder.build());
                                                    } else {
                                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notify_channel))
                                                                .setSmallIcon(R.drawable.spotring_icon_new)
                                                                .setContentTitle("You have entered your Area for: " + event_name)
                                                                .setContentText(notes.isEmpty() ? event_type : notes)
                                                                .setPriority(NotificationCompat.PRIORITY_HIGH);

                                                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                                                        notificationManagerCompat.notify(1212, builder.build());
                                                        Intent serviceIntent = new Intent(context, GeoFenceService.class);
                                                        serviceIntent.putExtra("EV_NA", event_name);
                                                        serviceIntent.putExtra("EV_NO", notes);
                                                        serviceIntent.putExtra("EV_TY", event_type);
                                                        serviceIntent.putExtra("TR_TY", "You have entered your Area for");
                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                            context.startForegroundService(serviceIntent); // Required for Android 8+
                                                        } else {
                                                            context.startService(serviceIntent);
                                                        }
                                                    }
                                                }
                                                break;
                                            case Geofence.GEOFENCE_TRANSITION_EXIT:
                                                if (trigger_id.equals("2") || trigger_id.equals("4") || trigger_id.equals("6") || trigger_id.equals("7")) {
                                                    if (alarm_id.equals("1")) {
                                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notify_channel))
                                                                .setSmallIcon(R.drawable.spotring_icon_new)
                                                                .setContentTitle("You have exited your Area for: " + event_name)
                                                                .setContentText(notes.isEmpty() ? event_type : notes)
                                                                .setPriority(NotificationCompat.PRIORITY_HIGH);

                                                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                                                        notificationManagerCompat.notify(1212, builder.build());
                                                    } else {
                                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notify_channel))
                                                                .setSmallIcon(R.drawable.spotring_icon_new)
                                                                .setContentTitle("You have exited your Area for: " + event_name)
                                                                .setContentText(notes.isEmpty() ? event_type : notes)
                                                                .setPriority(NotificationCompat.PRIORITY_HIGH);

                                                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                                                        notificationManagerCompat.notify(1212, builder.build());
                                                        Intent serviceIntent = new Intent(context, GeoFenceService.class);
                                                        serviceIntent.putExtra("EV_NA", event_name);
                                                        serviceIntent.putExtra("EV_NO", notes);
                                                        serviceIntent.putExtra("EV_TY", event_type);
                                                        serviceIntent.putExtra("TR_TY", "You have exited your Area for");
                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                            context.startForegroundService(serviceIntent); // Required for Android 8+
                                                        } else {
                                                            context.startService(serviceIntent);
                                                        }
                                                    }
                                                }
                                                break;
                                            case Geofence.GEOFENCE_TRANSITION_DWELL:
                                                if (trigger_id.equals("3") || trigger_id.equals("5") || trigger_id.equals("6") || trigger_id.equals("7")) {
                                                    if (alarm_id.equals("1")) {
                                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notify_channel))
                                                                .setSmallIcon(R.drawable.spotring_icon_new)
                                                                .setContentTitle("You are in your Area for: " + event_name)
                                                                .setContentText(notes.isEmpty() ? event_type : notes)
                                                                .setPriority(NotificationCompat.PRIORITY_HIGH);

                                                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                                                        notificationManagerCompat.notify(1212, builder.build());
                                                    } else {
                                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notify_channel))
                                                                .setSmallIcon(R.drawable.spotring_icon_new)
                                                                .setContentTitle("You are in your Area for: " + event_name)
                                                                .setContentText(notes.isEmpty() ? event_type : notes)
                                                                .setPriority(NotificationCompat.PRIORITY_HIGH);

                                                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                                                        notificationManagerCompat.notify(1212, builder.build());
                                                        Intent serviceIntent = new Intent(context, GeoFenceService.class);
                                                        serviceIntent.putExtra("EV_NA", event_name);
                                                        serviceIntent.putExtra("EV_NO", notes);
                                                        serviceIntent.putExtra("EV_TY", event_type);
                                                        serviceIntent.putExtra("TR_TY", "You are in your Area for");
                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                            context.startForegroundService(serviceIntent); // Required for Android 8+
                                                        } else {
                                                            context.startService(serviceIntent);
                                                        }
                                                    }
                                                }
                                                break;
                                        }
                                    }
                                } else if (date.after(e_date)) {
                                    expire++;
                                }
                            }
                        } else {
                            if (!b_t.isEmpty() && !e_t.isEmpty()) {
                                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                                String t_n_date = timeFormat.format(to_date);

                                Date date_time = null;
                                Date b_time = null;
                                Date e_time = null;

                                try {
                                    date_time = timeFormat.parse(t_n_date);
                                    b_time = timeFormat.parse(b_t);
                                    e_time = timeFormat.parse(e_t);
                                } catch (ParseException e) {
                                    String er = e.getLocalizedMessage();
                                    System.out.println(er);
                                }

                                if (date_time != null && b_time != null && e_time != null) {
                                    if (date_time.equals(b_time) || date_time.equals(e_time) || (date_time.after(b_time) && date_time.before(e_time))) {

                                        switch (transition) {
                                            case Geofence.GEOFENCE_TRANSITION_ENTER:
                                                if (trigger_id.equals("1") || trigger_id.equals("4") || trigger_id.equals("5") || trigger_id.equals("7")) {
                                                    if (alarm_id.equals("1")) {
                                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notify_channel))
                                                                .setSmallIcon(R.drawable.spotring_icon_new)
                                                                .setContentTitle("You have entered your Area for: " + event_name)
                                                                .setContentText(notes.isEmpty() ? event_type : notes)
                                                                .setPriority(NotificationCompat.PRIORITY_HIGH);

                                                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                                                        notificationManagerCompat.notify(1212, builder.build());
                                                    } else {
                                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notify_channel))
                                                                .setSmallIcon(R.drawable.spotring_icon_new)
                                                                .setContentTitle("You have entered your Area for: " + event_name)
                                                                .setContentText(notes.isEmpty() ? event_type : notes)
                                                                .setPriority(NotificationCompat.PRIORITY_HIGH);

                                                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                                                        notificationManagerCompat.notify(1212, builder.build());
                                                        Intent serviceIntent = new Intent(context, GeoFenceService.class);
                                                        serviceIntent.putExtra("EV_NA", event_name);
                                                        serviceIntent.putExtra("EV_NO", notes);
                                                        serviceIntent.putExtra("EV_TY", event_type);
                                                        serviceIntent.putExtra("TR_TY", "You have entered your Area for");
                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                            context.startForegroundService(serviceIntent); // Required for Android 8+
                                                        } else {
                                                            context.startService(serviceIntent);
                                                        }
                                                    }
                                                }
                                                break;
                                            case Geofence.GEOFENCE_TRANSITION_EXIT:
                                                if (trigger_id.equals("2") || trigger_id.equals("4") || trigger_id.equals("6") || trigger_id.equals("7")) {
                                                    if (alarm_id.equals("1")) {
                                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notify_channel))
                                                                .setSmallIcon(R.drawable.spotring_icon_new)
                                                                .setContentTitle("You have exited your Area for: " + event_name)
                                                                .setContentText(notes.isEmpty() ? event_type : notes)
                                                                .setPriority(NotificationCompat.PRIORITY_HIGH);

                                                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                                                        notificationManagerCompat.notify(1212, builder.build());
                                                    } else {
                                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notify_channel))
                                                                .setSmallIcon(R.drawable.spotring_icon_new)
                                                                .setContentTitle("You have exited your Area for: " + event_name)
                                                                .setContentText(notes.isEmpty() ? event_type : notes)
                                                                .setPriority(NotificationCompat.PRIORITY_HIGH);

                                                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                                                        notificationManagerCompat.notify(1212, builder.build());
                                                        Intent serviceIntent = new Intent(context, GeoFenceService.class);
                                                        serviceIntent.putExtra("EV_NA", event_name);
                                                        serviceIntent.putExtra("EV_NO", notes);
                                                        serviceIntent.putExtra("EV_TY", event_type);
                                                        serviceIntent.putExtra("TR_TY", "You have exited your Area for");
                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                            context.startForegroundService(serviceIntent); // Required for Android 8+
                                                        } else {
                                                            context.startService(serviceIntent);
                                                        }
                                                    }
                                                }
                                                break;
                                            case Geofence.GEOFENCE_TRANSITION_DWELL:
                                                if (trigger_id.equals("3") || trigger_id.equals("5") || trigger_id.equals("6") || trigger_id.equals("7")) {
                                                    if (alarm_id.equals("1")) {
                                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notify_channel))
                                                                .setSmallIcon(R.drawable.spotring_icon_new)
                                                                .setContentTitle("You are in your Area for: " + event_name)
                                                                .setContentText(notes.isEmpty() ? event_type : notes)
                                                                .setPriority(NotificationCompat.PRIORITY_HIGH);

                                                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                                                        notificationManagerCompat.notify(1212, builder.build());
                                                    } else {
                                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notify_channel))
                                                                .setSmallIcon(R.drawable.spotring_icon_new)
                                                                .setContentTitle("You are in your Area for: " + event_name)
                                                                .setContentText(notes.isEmpty() ? event_type : notes)
                                                                .setPriority(NotificationCompat.PRIORITY_HIGH);

                                                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                                                        notificationManagerCompat.notify(1212, builder.build());
                                                        Intent serviceIntent = new Intent(context, GeoFenceService.class);
                                                        serviceIntent.putExtra("EV_NA", event_name);
                                                        serviceIntent.putExtra("EV_NO", notes);
                                                        serviceIntent.putExtra("EV_TY", event_type);
                                                        serviceIntent.putExtra("TR_TY", "You are in your Area for");
                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                            context.startForegroundService(serviceIntent); // Required for Android 8+
                                                        } else {
                                                            context.startService(serviceIntent);
                                                        }
                                                    }
                                                }
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (expire == customRepetitionDataLists.size()) {
                        updateGeoFence(geo_id);
                    }
                }
                break;
            case "1":
                switch (transition) {
                    case Geofence.GEOFENCE_TRANSITION_ENTER:
                        if (trigger_id.equals("1") || trigger_id.equals("4") || trigger_id.equals("5") || trigger_id.equals("7")) {
                            if (alarm_id.equals("1")) {
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notify_channel))
                                        .setSmallIcon(R.drawable.spotring_icon_new)
                                        .setContentTitle("You have entered your Area for: " + event_name)
                                        .setContentText(notes.isEmpty() ? event_type : notes)
                                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                                notificationManagerCompat.notify(1212, builder.build());
                            } else {
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notify_channel))
                                        .setSmallIcon(R.drawable.spotring_icon_new)
                                        .setContentTitle("You have entered your Area for: " + event_name)
                                        .setContentText(notes.isEmpty() ? event_type : notes)
                                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                                notificationManagerCompat.notify(1212, builder.build());
                                Intent serviceIntent = new Intent(context, GeoFenceService.class);
                                serviceIntent.putExtra("EV_NA", event_name);
                                serviceIntent.putExtra("EV_NO", notes);
                                serviceIntent.putExtra("EV_TY", event_type);
                                serviceIntent.putExtra("TR_TY", "You have entered your Area for");
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    context.startForegroundService(serviceIntent); // Required for Android 8+
                                } else {
                                    context.startService(serviceIntent);
                                }
                            }
                            updateGeoFence(geo_id);
                        }
                        break;
                    case Geofence.GEOFENCE_TRANSITION_EXIT:
                        if (trigger_id.equals("2") || trigger_id.equals("4") || trigger_id.equals("6") || trigger_id.equals("7")) {
                            if (alarm_id.equals("1")) {
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notify_channel))
                                        .setSmallIcon(R.drawable.spotring_icon_new)
                                        .setContentTitle("You have exited your Area for: " + event_name)
                                        .setContentText(notes.isEmpty() ? event_type : notes)
                                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                                notificationManagerCompat.notify(1212, builder.build());
                            } else {
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notify_channel))
                                        .setSmallIcon(R.drawable.spotring_icon_new)
                                        .setContentTitle("You have exited your Area for: " + event_name)
                                        .setContentText(notes.isEmpty() ? event_type : notes)
                                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                                notificationManagerCompat.notify(1212, builder.build());
                                Intent serviceIntent = new Intent(context, GeoFenceService.class);
                                serviceIntent.putExtra("EV_NA", event_name);
                                serviceIntent.putExtra("EV_NO", notes);
                                serviceIntent.putExtra("EV_TY", event_type);
                                serviceIntent.putExtra("TR_TY", "You have exited your Area for");
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    context.startForegroundService(serviceIntent); // Required for Android 8+
                                } else {
                                    context.startService(serviceIntent);
                                }
                            }
                            updateGeoFence(geo_id);
                        }
                        break;
                    case Geofence.GEOFENCE_TRANSITION_DWELL:
                        if (trigger_id.equals("3") || trigger_id.equals("5") || trigger_id.equals("6") || trigger_id.equals("7")) {
                            if (alarm_id.equals("1")) {
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notify_channel))
                                        .setSmallIcon(R.drawable.spotring_icon_new)
                                        .setContentTitle("You are in your Area for: " + event_name)
                                        .setContentText(notes.isEmpty() ? event_type : notes)
                                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                                notificationManagerCompat.notify(1212, builder.build());
                            } else {
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notify_channel))
                                        .setSmallIcon(R.drawable.spotring_icon_new)
                                        .setContentTitle("You are in your Area for: " + event_name)
                                        .setContentText(notes.isEmpty() ? event_type : notes)
                                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                                notificationManagerCompat.notify(1212, builder.build());
                                Intent serviceIntent = new Intent(context, GeoFenceService.class);
                                serviceIntent.putExtra("EV_NA", event_name);
                                serviceIntent.putExtra("EV_NO", notes);
                                serviceIntent.putExtra("EV_TY", event_type);
                                serviceIntent.putExtra("TR_TY", "You are in your Area for");
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    context.startForegroundService(serviceIntent); // Required for Android 8+
                                } else {
                                    context.startService(serviceIntent);
                                }
                            }
                            updateGeoFence(geo_id);
                        }

//
////                    MediaPlayer mediaPlayer = MediaPlayer.create(context, android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI);
////                    mediaPlayer.start();
//
//                    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//                    Ringtone ringtone = null;
//                    // Play ringtone sound
//                    ringtone = RingtoneManager.getRingtone(context, alarmSound);
////                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
////                        ringtone.setLooping(true);
////                    }
//                    ringtone.play();
////
//                    Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//                    if (vibrator != null && vibrator.hasVibrator()) {
//                        vibrator.vibrate(5000); // Vibrate for 5 seconds
//                    }
//                    Intent alarmIntent = new Intent(context, AlarmStartUp.class);
//                    alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(alarmIntent);
                        break;
                }
                break;
            case "2":
                switch (transition) {
                    case Geofence.GEOFENCE_TRANSITION_ENTER:
                        if (trigger_id.equals("1") || trigger_id.equals("4") || trigger_id.equals("5") || trigger_id.equals("7")) {
                            if (alarm_id.equals("1")) {
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notify_channel))
                                        .setSmallIcon(R.drawable.spotring_icon_new)
                                        .setContentTitle("You have entered your Area for: " + event_name)
                                        .setContentText(notes.isEmpty() ? event_type : notes)
                                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                                notificationManagerCompat.notify(1212, builder.build());
                            } else {
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notify_channel))
                                        .setSmallIcon(R.drawable.spotring_icon_new)
                                        .setContentTitle("You have entered your Area for: " + event_name)
                                        .setContentText(notes.isEmpty() ? event_type : notes)
                                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                                notificationManagerCompat.notify(1212, builder.build());
                                Intent serviceIntent = new Intent(context, GeoFenceService.class);
                                serviceIntent.putExtra("EV_NA", event_name);
                                serviceIntent.putExtra("EV_NO", notes);
                                serviceIntent.putExtra("EV_TY", event_type);
                                serviceIntent.putExtra("TR_TY", "You have entered your Area for");
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    context.startForegroundService(serviceIntent); // Required for Android 8+
                                } else {
                                    context.startService(serviceIntent);
                                }
                            }
                        }
                        break;
                    case Geofence.GEOFENCE_TRANSITION_EXIT:
                        if (trigger_id.equals("2") || trigger_id.equals("4") || trigger_id.equals("6") || trigger_id.equals("7")) {
                            if (alarm_id.equals("1")) {
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notify_channel))
                                        .setSmallIcon(R.drawable.spotring_icon_new)
                                        .setContentTitle("You have exited your Area for: " + event_name)
                                        .setContentText(notes.isEmpty() ? event_type : notes)
                                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                                notificationManagerCompat.notify(1212, builder.build());
                            } else {
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notify_channel))
                                        .setSmallIcon(R.drawable.spotring_icon_new)
                                        .setContentTitle("You have exited your Area for: " + event_name)
                                        .setContentText(notes.isEmpty() ? event_type : notes)
                                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                                notificationManagerCompat.notify(1212, builder.build());
                                Intent serviceIntent = new Intent(context, GeoFenceService.class);
                                serviceIntent.putExtra("EV_NA", event_name);
                                serviceIntent.putExtra("EV_NO", notes);
                                serviceIntent.putExtra("EV_TY", event_type);
                                serviceIntent.putExtra("TR_TY", "You have exited your Area for");
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    context.startForegroundService(serviceIntent); // Required for Android 8+
                                } else {
                                    context.startService(serviceIntent);
                                }
                            }
                        }
                        break;
                    case Geofence.GEOFENCE_TRANSITION_DWELL:
                        if (trigger_id.equals("3") || trigger_id.equals("5") || trigger_id.equals("6") || trigger_id.equals("7")) {
                            if (alarm_id.equals("1")) {
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notify_channel))
                                        .setSmallIcon(R.drawable.spotring_icon_new)
                                        .setContentTitle("You are in your Area for: " + event_name)
                                        .setContentText(notes.isEmpty() ? event_type : notes)
                                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                                notificationManagerCompat.notify(1212, builder.build());
                            } else {
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notify_channel))
                                        .setSmallIcon(R.drawable.spotring_icon_new)
                                        .setContentTitle("You are in your Area for: " + event_name)
                                        .setContentText(notes.isEmpty() ? event_type : notes)
                                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                                notificationManagerCompat.notify(1212, builder.build());
                                Intent serviceIntent = new Intent(context, GeoFenceService.class);
                                serviceIntent.putExtra("EV_NA", event_name);
                                serviceIntent.putExtra("EV_NO", notes);
                                serviceIntent.putExtra("EV_TY", event_type);
                                serviceIntent.putExtra("TR_TY", "You are in your Area for");
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    context.startForegroundService(serviceIntent); // Required for Android 8+
                                } else {
                                    context.startService(serviceIntent);
                                }
                            }
                        }
                        break;
                }
                break;
        }
    }

    public void updateGeoFence(String geo_id) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(KEY_LOC_TABLE_NAME).whereEqualTo(KEY_LOC_ID,geo_id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().getDocuments().isEmpty()) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);

                        database.collection(KEY_LOC_TABLE_NAME).document(documentSnapshot.getId()).update(KEY_GEO_IS_ACTIVE,false)
                                .addOnSuccessListener(unused -> {

                                })
                                .addOnFailureListener(e -> {

                                });
                    }
                })
                .addOnFailureListener(e -> {
                });
    }
}
