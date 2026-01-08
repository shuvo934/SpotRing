package ttit.com.shuvo.spotring.dashboard;

import static ttit.com.shuvo.spotring.user_auth.UserLogin.userInfoLists;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_GEO_ALERT_TYPE_ID;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_GEO_ALERT_TYPE_NAME;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_GEO_EVENT_NAME;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_GEO_EVENT_NOTES;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_GEO_EVENT_TYPE_ID;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_GEO_EVENT_TYPE_NAME;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_GEO_ID_FOR_TIME;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_GEO_IS_ACTIVE;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_GEO_REPEAT_BEGIN_DATE;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_GEO_REPEAT_BEGIN_TIME;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_GEO_REPEAT_END_DATE;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_GEO_REPEAT_END_TIME;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_GEO_REPEAT_ID;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_GEO_REPEAT_NAME;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_GEO_TIME_TABLE_NAME;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_GEO_TRIGGER_TYPE_ID;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_GEO_TRIGGER_TYPE_NAME;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_GEO_UPDATE_DATE;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_GEO_USER_NAME;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_LATITUDE;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_LOC_ADDRESS;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_LOC_ID;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_LOC_TABLE_NAME;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_LONGITUDE;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_RADIUS;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_USER_EMAIL;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_USER_EVENT_COUNT;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_USER_ID;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_USER_NAME;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_USER_PASSWORD;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_USER_PHONE;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_USER_SUBSCRIBE;
import static ttit.com.shuvo.spotring.utilities.Constants.LOGIN_ACTIVITY_FILE;
import static ttit.com.shuvo.spotring.utilities.Constants.LOGIN_TF;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.spotring.R;
import ttit.com.shuvo.spotring.dashboard.adapters.SavedLocationAdapter;
import ttit.com.shuvo.spotring.dashboard.interfaces.PictureChooseListener;
import ttit.com.shuvo.spotring.dashboard.model.SavedLocationList;
import ttit.com.shuvo.spotring.dashboard.user_image_preview.BitmapCallBack;
import ttit.com.shuvo.spotring.dashboard.user_image_preview.CameraPreview;
import ttit.com.shuvo.spotring.databinding.ActivityHomePageBinding;
import ttit.com.shuvo.spotring.geofences.AddGeoFences;
import ttit.com.shuvo.spotring.geofences.model.CustomRepetitionDataList;
import ttit.com.shuvo.spotring.geofences.model.DocumentDeleteList;
import ttit.com.shuvo.spotring.user_auth.UserLogin;
import ttit.com.shuvo.spotring.user_auth.model.UserInfoList;

public class HomePage extends AppCompatActivity implements PictureChooseListener, BitmapCallBack, OnMapReadyCallback, View.OnTouchListener, SavedLocationAdapter.ClickedMap, SavedLocationAdapter.ClickedEvent, SavedLocationAdapter.ClickedSwitch, SavedLocationAdapter.ClickedDelete {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    FloatingActionButton addLocation;

    ImageView userImage;
    TextView profileName;
    String p_name = "";
    String p_phone = "";
    ImageView logOut;

    private GoogleMap mMap;

    Spinner mapTypeSpinner;

    ImageView screenChanger;
    Boolean fullScreen = false;
    ImageView myLocation;

    LinearLayout slLayout;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationManager locationManager;

    SharedPreferences sharedPreferences;

    RecyclerView locationView;
    RecyclerView.LayoutManager layoutManager;
    SavedLocationAdapter savedLocationAdapter;
    ArrayList<SavedLocationList> savedLocationLists;
    String userEventCount = "";
    String userSubscribed = "";

    TextView noLocation;

    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean loading = false;
    String parsing_message = "";

    float dX;
    float dY;
    private static float downRawX, downRawY;
    private final static float CLICK_DRAG_TOLERANCE = 10;

    ActivityResultLauncher<Intent> someActivityResultLauncher;

    boolean needRefresh = true;
    ArrayList<DocumentDeleteList> deleteLists;
    boolean fromLogin = false;

//    Bitmap selectedBitmap;

    Logger logger = Logger.getLogger(HomePage.class.getName());

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(HomePage.this, R.color.white));

        ActivityHomePageBinding binding = ActivityHomePageBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        fullLayout = binding.homePageFullLayout;
        circularProgressIndicator = binding.progressIndicatorHomepage;
        circularProgressIndicator.setVisibility(View.GONE);

        addLocation = binding.floatingButtonAddLocation;

        userImage = binding.userImage;
        profileName = binding.profileName;
        logOut = binding.logOutIconHomepage;

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.location_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        mapTypeSpinner = binding.mapTypeSpinner;
        screenChanger = binding.fullScreenChanger;
        myLocation = binding.myLocationIcon;

        slLayout = binding.savedLocationLayout;

        locationView = binding.savedLocationListView;
        noLocation = binding.noLocationFoundMsg;
        noLocation.setVisibility(View.GONE);

        locationView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        locationView.setLayoutManager(layoutManager);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        savedLocationLists = new ArrayList<>();

        sharedPreferences = getSharedPreferences(LOGIN_ACTIVITY_FILE, MODE_PRIVATE);
        boolean loginfile = sharedPreferences.getBoolean(LOGIN_TF, false);

        if (loginfile) {
            String userName = sharedPreferences.getString(KEY_USER_NAME, null);
            String userPhone = sharedPreferences.getString(KEY_USER_PHONE, null);
            String userEmail = sharedPreferences.getString(KEY_USER_EMAIL, null);
            String userPass = sharedPreferences.getString(KEY_USER_PASSWORD, null);
            String userId = sharedPreferences.getString(KEY_USER_ID, null);
            userSubscribed = sharedPreferences.getString(KEY_USER_SUBSCRIBE, null);
            userEventCount = sharedPreferences.getString(KEY_USER_EVENT_COUNT, null);

            userInfoLists = new ArrayList<>();
            userInfoLists.add(new UserInfoList(userName, userPhone, userEmail, userPass, userSubscribed, "", userEventCount, userId, ""));
        }
        else {
            Toast.makeText(this, "User Data No Found. Please Login Again", Toast.LENGTH_SHORT).show();

            userInfoLists = new ArrayList<>();

            SharedPreferences.Editor editor1 = sharedPreferences.edit();
            editor1.remove(KEY_USER_NAME);
            editor1.remove(KEY_USER_PHONE);
            editor1.remove(KEY_USER_EMAIL);
            editor1.remove(KEY_USER_PASSWORD);
            editor1.remove(KEY_USER_ID);
            editor1.remove(KEY_USER_SUBSCRIBE);
            editor1.remove(KEY_USER_EVENT_COUNT);
            editor1.remove(LOGIN_TF);
            editor1.apply();
            editor1.commit();

            Intent intent = new Intent(HomePage.this, UserLogin.class);
            startActivity(intent);
            finish();
        }

        Intent intentData = getIntent();
        fromLogin = intentData.getBooleanExtra("FROM_LOGIN",false);

        p_name = userInfoLists.get(0).getP_name();
        p_phone = userInfoLists.get(0).getP_phone();
        profileName.setText(p_name);

        screenChanger.setOnClickListener(view -> {
            if (!fullScreen) {
                slLayout.setVisibility(View.GONE);
                addLocation.setVisibility(View.GONE);
                screenChanger.setImageResource(R.drawable.fullscreen_exit);
                fullScreen = true;
            } else {
                slLayout.setVisibility(View.VISIBLE);
                addLocation.setVisibility(View.VISIBLE);
                screenChanger.setImageResource(R.drawable.fullscreen);
                fullScreen = false;
            }
        });

        List<String> categories = new ArrayList<>();
        categories.add("NORMAL");
        categories.add("SATELLITE");
        categories.add("TERRAIN");
        categories.add("HYBRID");
        categories.add("TRAFFIC");
        categories.add("NO LANDMARK");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, categories);

        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        mapTypeSpinner.setAdapter(spinnerAdapter);

        addLocation.setOnTouchListener(HomePage.this);

        logOut.setOnClickListener(view -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            if (savedLocationLists.isEmpty()) {
                builder.setTitle("LOG OUT!")
                        .setMessage("Do you want to Log Out?")
                        .setPositiveButton("YES", (dialog, which) -> {
                            userInfoLists.clear();
                            userInfoLists = new ArrayList<>();

                            SharedPreferences.Editor editor1 = sharedPreferences.edit();
                            editor1.remove(KEY_USER_NAME);
                            editor1.remove(KEY_USER_PHONE);
                            editor1.remove(KEY_USER_EMAIL);
                            editor1.remove(KEY_USER_PASSWORD);
                            editor1.remove(KEY_USER_ID);
                            editor1.remove(KEY_USER_SUBSCRIBE);
                            editor1.remove(KEY_USER_EVENT_COUNT);
                            editor1.remove(LOGIN_TF);
                            editor1.apply();
                            editor1.commit();

                            Intent intent = new Intent(HomePage.this, UserLogin.class);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton("NO", (dialog, which) -> {

                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
            else {
                builder.setTitle("LOG OUT!")
                        .setMessage("If you log out, your all event will be deactivated. Do you want to continue?")
                        .setPositiveButton("YES", (dialog, which) -> deactivateAllLocation())
                        .setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
                AlertDialog alert = builder.create();
                alert.show();
            }

        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (loading) {
                    Toast.makeText(HomePage.this, "Please wait while loading", Toast.LENGTH_SHORT).show();
                }
                else {
                    MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(HomePage.this);
                    alertDialogBuilder.setTitle("Exit")
                            .setMessage("Do you want to exit ?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                finish();
                                dialog.dismiss();
                            })
                            .setNegativeButton("No",(dialog, which) -> dialog.dismiss());

                    AlertDialog alert = alertDialogBuilder.create();
                    alert.show();
                }
            }
        });

        createNotificationChannel();
//        addGeoFence();
//        addLocation.setOnClickListener(view -> {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
//                if (checkExactAlarmPermission()) {
//                    setAlarm();
//                } else {
//                    requestExactAlarmPermission();
//                }
//            } else {
//                setAlarm();
//            }
//        });

        addLocation.setOnClickListener(view -> {
            if (userEventCount != null && !userEventCount.isEmpty()) {
                if (Integer.parseInt(userEventCount) > savedLocationLists.size()) {
                    needRefresh = true;
                    Intent intent = new Intent(HomePage.this, AddGeoFences.class);
                    intent.putExtra("Type", "1");
                    startActivity(intent);
                }
                else {
                    MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(HomePage.this);
                    if (userSubscribed != null && !userSubscribed.isEmpty()) {
                        if (userSubscribed.equalsIgnoreCase("yes")) {
                            alertDialogBuilder.setTitle("Upgrade Subscription!")
                                    .setMessage("You have used all your subscribed event. Please upgrade your subscription pack to get extra event slot.")
                                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                    .setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());

                            AlertDialog alert = alertDialogBuilder.create();
                            alert.show();
                        }
                        else {
                            alertDialogBuilder.setTitle("Need Subscription!")
                                    .setMessage("You have used all your Free event. Please subscribe to get extra event slot.")
                                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                    .setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());

                            AlertDialog alert = alertDialogBuilder.create();
                            alert.show();
                        }
                    }
                    else {
                        alertDialogBuilder.setTitle("Need Subscription!")
                                .setMessage("You have used all your Free event. Please subscribe to get extra event slot.")
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                .setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());

                        AlertDialog alert = alertDialogBuilder.create();
                        alert.show();
                    }
                }
            }
            else {
                needRefresh = true;
                Intent intent = new Intent(HomePage.this, AddGeoFences.class);
                intent.putExtra("Type", "1");
                startActivity(intent);
            }
        });

        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        assert data != null;
                        System.out.println("EKHANE ASHE CHECK: " + data);
                    }
                });

        // to use we need to pay Firebase for storage
//        userImage.setOnClickListener(view -> {
//            needRefresh = false;
//            ImageTakerChoiceDialog imageTakerChoiceDialog = new ImageTakerChoiceDialog();
//            imageTakerChoiceDialog.show(getSupportFragmentManager(),"CH_IMAGE_DOC");
//        });

    }

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    try {
                        startCrop(uri);

                    } catch (Exception e) {
                        logger.log(Level.WARNING,e.getMessage(),e);
                        Toast.makeText(getApplicationContext(),"Failed to upload image",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),"Failed to get image",Toast.LENGTH_SHORT).show();
                }
            });

//    private boolean checkExactAlarmPermission() {
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            return alarmManager.canScheduleExactAlarms();
//        }
//        else {
//            return true;
//        }
//    }

//    @RequiresApi(api = Build.VERSION_CODES.S)
//    private void requestExactAlarmPermission() {
//        Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
//        intent.setData(android.net.Uri.parse("package:" + getPackageName()));
//        startActivity(intent);
//        Toast.makeText(this, "Please grant Exact Alarm permission", Toast.LENGTH_SHORT).show();
//    }

//    private void setAlarm() {
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(this, AlarmReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1010, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        // Set the alarm to go off 1 minute later
//        long triggerTime = SystemClock.elapsedRealtime() + (60 * 1000); // 1 minute
//        System.out.println(SystemClock.elapsedRealtime());
//        System.out.println(Calendar.getInstance().getTimeInMillis());
//
//        if (alarmManager != null) {
//            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pendingIntent);
//            Toast.makeText(this, "Alarm set for 1 minute later!", Toast.LENGTH_SHORT).show();
//        }
//    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    getString(R.string.notify_channel), "GeoFence Event",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Event Trigger for GeoFence");
            notificationChannel.enableVibration(true);
            notificationChannel.enableLights(true);
            notificationChannel.setShowBadge(true);
            notificationChannel.setBypassDnd(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);

//            NotificationChannel existingChannel = notificationManager.getNotificationChannel(getString(R.string.notify_channel));

            notificationManager.createNotificationChannel(notificationChannel);
//            if (existingChannel == null) {
//                notificationManager.createNotificationChannel(notificationChannel);
//            }
//            else {
//                System.out.println("Channel Exist");
//            }
//            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//            intent.setData(Uri.parse("package:" + getPackageName()));  // Open settings for your app
//            startActivity(intent);
//            PermissionsAll();
        }
    }

//    public void PermissionsAll() {
//        final Boolean[] paise = {false};
//        final Intent[] POWERMANAGER_INTENTS = {
//
//                new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
//                new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")),
//                new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
//                new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity")),
//                new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
//                new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
//                new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
//                new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
//                new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
//                new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
//                new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
//                new Intent().setComponent(new ComponentName("com.samsung.android.lool", "com.samsung.android.sm.battery.ui.BatteryActivity")),
//                new Intent().setComponent(new ComponentName("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity")),
//                new Intent().setComponent(new ComponentName("com.htc.pitroad", "com.htc.pitroad.landingpage.activity.LandingPageActivity")),
//                new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.MainActivity")),
//                new Intent().setComponent(new ComponentName("com.transsion.phonemanager", "com.itel.autobootmanager.activity.AutoBootMgrActivity"))
//        };
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Check Auto Start Permission!")
//                .setMessage("Check the App Auto Start Option is On or Off. Auto Start On will provide better solution for the service in the background.")
//                .setPositiveButton("Check", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
////                        for (Intent intent : POWERMANAGER_INTENTS)
////                            if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
////                                // show dialog to ask user action
////                                System.out.println("PAISE KISU MISU: "+ intent.getComponent().toString());
////
////
////                                break;
////                            }
//
//                        for (Intent intent : POWERMANAGER_INTENTS)
//                            if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
//                                System.out.println("PAISE KISU MISU: "+ intent.getComponent().toString());
//                                paise[0] = true;
//                                someActivityResultLauncher.launch(intent);
//                                break;
//                            }
//                        if (!paise[0]){
//                            Toast.makeText(getApplicationContext(),"Could not find Auto Start Permission Settings.",Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                })
//                .setNegativeButton("Don't Check", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//        AlertDialog alert = builder.create();
//        alert.show();
//
//
//    }

//    @SuppressLint("MissingPermission")
//    public void addGeoFence() {
//        String geo_id = "GEO_TEST_1";
//        ArrayList<String> ids = new ArrayList<>();
//        ids.add(geo_id);
//        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(this);
//
//        ArrayList<Geofence> geofenceList = new ArrayList<>();
//
//        geofencingClient.removeGeofences(ids)
//                .addOnSuccessListener(unused -> Toast.makeText(getApplicationContext(), "GeoFence Removed Successfully", Toast.LENGTH_SHORT).show())
//                .addOnFailureListener(e -> {
//                    String errorMessage = getErrorString(e);
//                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
//                });
////        LatLng latLng = new LatLng(23.79251, 90.40720);
////
////        float rad = 50;
////
////        Geofence geofence = new Geofence.Builder()
////                .setCircularRegion(latLng.latitude, latLng.longitude, rad)
////                .setRequestId(geo_id)
////                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT)
////                .setLoiteringDelay(120000)
////                .setExpirationDuration(Geofence.NEVER_EXPIRE)
////                .build();
////
////        geofenceList.add(geofence);
////
////        GeofencingRequest geofencingRequest = null;
////        try {
////            geofencingRequest = new GeofencingRequest.Builder()
////                    .addGeofences(geofenceList)
////                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
////                    .build();
////        } catch (Exception e) {
////            throw new RuntimeException(e);
////        }
////
////        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
////        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1001, intent, PendingIntent.FLAG_UPDATE_CURRENT);
////
////        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
////                .addOnSuccessListener(unused -> {
////                    Toast.makeText(getApplicationContext(), "Geofence Added", Toast.LENGTH_SHORT).show();
////                    System.out.println("HOISE");
////                })
////                .addOnFailureListener(e -> {
////                    System.out.println(e.getLocalizedMessage());
////                    String errorMsg = getErrorString(e);
////                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
////                    System.out.println("HOI NAI");
////                });
//    }

    public String getErrorString (Exception e) {
        if (e instanceof ApiException) {
            ApiException apiException = (ApiException) e;
            switch (apiException.getStatusCode()) {
                case GeofenceStatusCodes
                             .GEOFENCE_NOT_AVAILABLE:
                    return "GEOFENCE NOT AVAILABLE";
                case GeofenceStatusCodes
                             .GEOFENCE_TOO_MANY_GEOFENCES:
                    return "TOO MANY GEOFENCES";
                case GeofenceStatusCodes
                             .GEOFENCE_TOO_MANY_PENDING_INTENTS:
                    return "TOO MANY PENDING INTENTS";
                case GeofenceStatusCodes.GEOFENCE_INSUFFICIENT_LOCATION_PERMISSION:
                    return "INSUFFICIENT PERMISSIONS";
            }
        }
        return e.getLocalizedMessage();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mapTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String name = parent.getItemAtPosition(position).toString();
                switch (name) {
                    case "NORMAL":
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        mMap.setTrafficEnabled(false);
                        try {
                            // Customise the styling of the base map using a JSON object defined
                            // in a raw resource file.
                            boolean success = googleMap.setMapStyle(
                                    MapStyleOptions.loadRawResourceStyle(
                                            HomePage.this, R.raw.normal));

                            if (!success) {
                                Log.i("Failed ", "Style parsing failed.");
                            }
                        } catch (Resources.NotFoundException e) {
                            Log.e("Style ", "Can't find style. Error: ", e);
                        }
                        break;
                    case "SATELLITE":
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        mMap.setTrafficEnabled(false);
                        break;
                    case "TERRAIN":
                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        mMap.setTrafficEnabled(false);
                        break;
                    case "HYBRID":
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        mMap.setTrafficEnabled(false);
                        break;
                    case "TRAFFIC":
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        mMap.setTrafficEnabled(true);
                        break;
                    case "NO LANDMARK":
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        mMap.setTrafficEnabled(false);
                        try {
                            // Customise the styling of the base map using a JSON object defined
                            // in a raw resource file.
                            boolean success = googleMap.setMapStyle(
                                    MapStyleOptions.loadRawResourceStyle(
                                            HomePage.this, R.raw.no_landmark));

                            if (!success) {
                                Log.i("Failed ", "Style parsing failed.");
                            }
                        } catch (Resources.NotFoundException e) {
                            Log.e("Style ", "Can't find style. Error: ", e);
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mMap.setOnMyLocationButtonClickListener(() -> {

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                return false;
            } else {
                newEnableGps();
                return true;
            }
        });

        myLocation.setOnClickListener(view -> {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                newEnableGps();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (needRefresh) {
            getLocationData();
        }
    }

    private void newEnableGps() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(1000)
                .setMaxUpdateDelayMillis(2000)
                .build();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, locationSettingsResponse -> {
            myLocation.setVisibility(View.GONE);
            zoomToUserLocation();
            if (fromLogin) {
                fromLogin = false;
                if (!savedLocationLists.isEmpty()) {
                    MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);
                    materialAlertDialogBuilder.setTitle("EVENT ALERT!")
                            .setMessage("Your all GeoFence Event have been deactivated for log out. Please Enable it again.")
                            .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());
                    AlertDialog alert = materialAlertDialogBuilder.create();
                    alert.show();
                }
            }
        });

        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(HomePage.this,
                            1000);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error.
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                myLocation.setVisibility(View.GONE);
                zoomToUserLocation();
                Log.i("Hoise ", "1");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                myLocation.setVisibility(View.VISIBLE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(false);
                //Write your code if there's no result
                Log.i("Hoise ", "2");
            }

            if (fromLogin) {
                fromLogin = false;
                if (!savedLocationLists.isEmpty()) {
                    MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);
                    materialAlertDialogBuilder.setTitle("EVENT ALERT!")
                            .setMessage("Your all GeoFence Event have been deactivated for log out. Please Enable it again.")
                            .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());
                    AlertDialog alert = materialAlertDialogBuilder.create();
                    alert.show();
                }
            }
        }
//        else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
//            if (data != null) {
//                Uri croppedUri = UCrop.getOutput(data);
//                assert croppedUri != null;
//                selectedBitmap = getCorrectlyOrientedBitmap(croppedUri);
//                if (selectedBitmap != null) {
//                    updateUserImage(croppedUri);
////                    userImage.setImageBitmap(selectedBitmap);
//                }
//                else {
//                    Toast.makeText(getApplicationContext(),"Invalid image",Toast.LENGTH_SHORT).show();
//                }
//            }
//            else {
//                Toast.makeText(getApplicationContext(),"Failed to crop image",Toast.LENGTH_SHORT).show();
//            }
//        }
//        else if (resultCode == UCrop.RESULT_ERROR) {
//            Toast.makeText(getApplicationContext(), "Invalid Image", Toast.LENGTH_SHORT).show();
//        }
    }

    public void zoomToUserLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(location -> {
            LatLng latLng;

            if (location != null) {
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
            }
            else {
                latLng = new LatLng(23.6850, 90.3563);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7));
            }

        });
//        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }


    // Deactivating all Geofence
    public void deactivateAllLocation() {
        circularProgressIndicator.setVisibility(View.VISIBLE);
        fullLayout.setVisibility(View.GONE);
        addLocation.setVisibility(View.GONE);
        conn = false;
        connected = false;
        loading = true;

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(KEY_LOC_TABLE_NAME).whereEqualTo(KEY_GEO_USER_NAME,p_phone)
                .get()
                .addOnCompleteListener(task -> {
                    conn = true;
                    if (task.isSuccessful()) {
                        connected = true;
                        if (task.getResult() != null && !task.getResult().getDocuments().isEmpty()) {
                            List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                            deleteLists = new ArrayList<>();
                            for (int i = 0; i < documentSnapshots.size(); i++) {
                                deleteLists.add(new DocumentDeleteList(documentSnapshots.get(i).getId(),false));
                            }
                        }
                        checkToDeactivateLocation();
                    }
                    else {
                        connected = false;
                        parsing_message = "Failed to get Data.";
                        logOutLayout();
                    }
                })
                .addOnFailureListener(e -> {
                    conn = false;
                    connected = false;
                    parsing_message = e.getLocalizedMessage();
                    logOutLayout();
                });
    }

    public void checkToDeactivateLocation() {
        boolean allUpdated = deleteLists.isEmpty();
        for (int x = 0; x < deleteLists.size(); x++) {
            allUpdated = deleteLists.get(x).isDeleted();
            if (!deleteLists.get(x).isDeleted()) {
                deactivateAll(x, deleteLists.get(x).getDocumentId());
                break;
            }
        }
        if (allUpdated) {
            conn = true;
            connected = true;
            logOutLayout();
        }
    }

    private void deactivateAll(int index, String id) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection(KEY_LOC_TABLE_NAME).document(id).update(KEY_GEO_IS_ACTIVE,false)
                .addOnSuccessListener(unused -> {
                    deleteLists.get(index).setDeleted(true);
                    checkToDeactivateLocation();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to deactivate. Retrying...", Toast.LENGTH_SHORT).show();
                    checkToDeactivateLocation();
                });
    }

    private void logOutLayout() {
        loading = false;
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                addLocation.setVisibility(View.VISIBLE);

                conn = false;
                connected = false;

                userInfoLists.clear();
                userInfoLists = new ArrayList<>();

                SharedPreferences.Editor editor1 = sharedPreferences.edit();
                editor1.remove(KEY_USER_NAME);
                editor1.remove(KEY_USER_PHONE);
                editor1.remove(KEY_USER_EMAIL);
                editor1.remove(KEY_USER_PASSWORD);
                editor1.remove(KEY_USER_ID);
                editor1.remove(KEY_USER_SUBSCRIBE);
                editor1.remove(KEY_USER_EVENT_COUNT);
                editor1.remove(LOGIN_TF);
                editor1.apply();
                editor1.commit();

                Intent intent = new Intent(HomePage.this, UserLogin.class);
                startActivity(intent);
                finish();
            }
            else {
                alertMessageDeActive();
            }
        }
        else {
            alertMessageDeActive();
        }
    }

    public void alertMessageDeActive() {
        fullLayout.setVisibility(View.VISIBLE);
        circularProgressIndicator.setVisibility(View.GONE);
        addLocation.setVisibility(View.VISIBLE);
        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    deactivateAllLocation();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> dialog.dismiss());

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }


    // Getting GeoFence Data
    private void getLocationData() {
        circularProgressIndicator.setVisibility(View.VISIBLE);
        fullLayout.setVisibility(View.GONE);
        addLocation.setVisibility(View.GONE);
        conn = false;
        connected = false;
        loading = true;

        savedLocationLists = new ArrayList<>();

        FirebaseFirestore cloudDatabase = FirebaseFirestore.getInstance();
        cloudDatabase.collection(KEY_LOC_TABLE_NAME)
                .whereEqualTo(KEY_GEO_USER_NAME, p_phone)
                .orderBy(KEY_GEO_UPDATE_DATE, Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(task -> {
                    System.out.println("COMPLETED");
                    conn = true;
                    System.out.println("SUCCESS");
                    connected = true;
                    if (!task.getDocuments().isEmpty()) {
                        List<DocumentSnapshot> documentSnapshots = task.getDocuments();

                        for (int i = 0; i < documentSnapshots.size(); i++) {
                            String id = documentSnapshots.get(i).getString(KEY_LOC_ID);
                            String lat = String.valueOf(documentSnapshots.get(i).getDouble(KEY_LATITUDE));
                            String lng = String.valueOf(documentSnapshots.get(i).getDouble(KEY_LONGITUDE));
                            String radius = documentSnapshots.get(i).getString(KEY_RADIUS);
                            String eve_adds = documentSnapshots.get(i).getString(KEY_LOC_ADDRESS);
                            String eve_name = documentSnapshots.get(i).getString(KEY_GEO_EVENT_NAME);
                            String desc = documentSnapshots.get(i).getString(KEY_GEO_EVENT_NOTES);

                            String event_type_id = documentSnapshots.get(i).getString(KEY_GEO_EVENT_TYPE_ID);
                            String event_type_name = documentSnapshots.get(i).getString(KEY_GEO_EVENT_TYPE_NAME);
                            String alert_type_id = documentSnapshots.get(i).getString(KEY_GEO_ALERT_TYPE_ID);
                            String alert_type_name = documentSnapshots.get(i).getString(KEY_GEO_ALERT_TYPE_NAME);
                            String trigger_type_id = documentSnapshots.get(i).getString(KEY_GEO_TRIGGER_TYPE_ID);
                            String trigger_type_name = documentSnapshots.get(i).getString(KEY_GEO_TRIGGER_TYPE_NAME);
                            String repeat_id = documentSnapshots.get(i).getString(KEY_GEO_REPEAT_ID);
                            String repeat_name = documentSnapshots.get(i).getString(KEY_GEO_REPEAT_NAME);
                            Boolean isActive = documentSnapshots.get(i).getBoolean(KEY_GEO_IS_ACTIVE);

                            savedLocationLists.add(new SavedLocationList(id,lat,lng,radius,eve_name,eve_adds,desc,alert_type_id,alert_type_name,event_type_id,
                                    event_type_name,trigger_type_id,trigger_type_name,repeat_id,repeat_name,isActive,new ArrayList<>(),false));
                        }
                    }
                    checkToGetTime();
                })
                .addOnFailureListener(e -> {
                    System.out.println("FAILED");
                    conn = false;
                    connected = false;
                    parsing_message = e.getLocalizedMessage();
                    updateLayout();
                });
    }

    private void checkToGetTime() {
        boolean allUpdated = savedLocationLists.isEmpty();
        for (int i = 0; i < savedLocationLists.size(); i++) {
            if (savedLocationLists.get(i).getRepeat_type_id().equals("3")) {
                allUpdated = savedLocationLists.get(i).isUpdated();
                if (!savedLocationLists.get(i).isUpdated()) {
                    String id = savedLocationLists.get(i).getGeo_id();
                    getCustomDate(i, id);
                    break;
                }
            }
            else {
                savedLocationLists.get(i).setUpdated(true);
                allUpdated = true;
            }
        }

        if (allUpdated) {
            conn = true;
            connected = true;
            updateLayout();
        }
    }

    private void getCustomDate(int index, String id) {
        ArrayList<CustomRepetitionDataList> dataLists = new ArrayList<>();
        FirebaseFirestore cloudDatabase = FirebaseFirestore.getInstance();
        cloudDatabase.collection(KEY_GEO_TIME_TABLE_NAME)
                .whereEqualTo(KEY_GEO_ID_FOR_TIME, id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        connected = true;
                        if (task.getResult() != null && !task.getResult().getDocuments().isEmpty()) {
                            List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();

                            for (int i = 0; i < documentSnapshots.size(); i++) {
                                String bd = documentSnapshots.get(i).getString(KEY_GEO_REPEAT_BEGIN_DATE);
                                String ed = documentSnapshots.get(i).getString(KEY_GEO_REPEAT_END_DATE);
                                String bt = documentSnapshots.get(i).getString(KEY_GEO_REPEAT_BEGIN_TIME);
                                String et = documentSnapshots.get(i).getString(KEY_GEO_REPEAT_END_TIME);
                                dataLists.add(new CustomRepetitionDataList(bd,ed,bt,et,false));
                            }
                        }
                        savedLocationLists.get(index).setUpdated(true);
                        savedLocationLists.get(index).setCustomRepetitionDataLists(dataLists);
                        checkToGetTime();
                    }
                    else {
                        connected = false;
                        parsing_message = "Failed to get Data.";
                        updateLayout();
                    }
                })
                .addOnFailureListener(e -> {
                    System.out.println("FAILED");
                    conn = false;
                    connected = false;
                    parsing_message = e.getLocalizedMessage();
                    updateLayout();
                });


    }

    private void updateLayout() {
        loading = false;
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                addLocation.setVisibility(View.VISIBLE);

                conn = false;
                connected = false;

                if (savedLocationLists.isEmpty()) {
                    noLocation.setVisibility(View.VISIBLE);
                }
                else {
                    noLocation.setVisibility(View.GONE);
                }

                savedLocationAdapter = new SavedLocationAdapter(savedLocationLists,HomePage.this, HomePage.this,HomePage.this, HomePage.this, HomePage.this);
                locationView.setAdapter(savedLocationAdapter);
                locationView.setRecyclerListener(mRecycleListener);

                mMap.clear();
                for (int i = 0; i < savedLocationLists.size(); i++) {
                    mMap.addCircle(new CircleOptions()
                            .center(new LatLng(Float.parseFloat(savedLocationLists.get(i).getLat()), Float.parseFloat(savedLocationLists.get(i).getLng())))
                            .radius(Integer.parseInt(savedLocationLists.get(i).getRadius()))
                            .strokeColor(getColor(R.color.belize_hole))
                            .strokeWidth(4F)
                            .fillColor(getColor(R.color.belize_hole_a)));
                }

                needRefresh = false;
                newEnableGps();
            }
            else {
                alertMessage();
            }
        }
        else {
            alertMessage();
        }
    }

    public void alertMessage() {
        fullLayout.setVisibility(View.VISIBLE);
        circularProgressIndicator.setVisibility(View.GONE);
        addLocation.setVisibility(View.VISIBLE);
        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    getLocationData();
                    dialog.dismiss();
                })
                .setNegativeButton("Exit",(dialog, which) -> {
                    dialog.dismiss();
                    finish();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }


    // Clicked Function
    private final RecyclerView.RecyclerListener mRecycleListener = holder -> {
        SavedLocationAdapter.SLAHolder mapHolder = (SavedLocationAdapter.SLAHolder) holder;
        if (mapHolder.map != null) {
            // Clear the map and free up resources by changing the map type to none.
            // Also reset the map when it gets reattached to layout, so the previous map would
            // not be displayed.
            mapHolder.map.clear();
            mapHolder.map.setMapType(GoogleMap.MAP_TYPE_NONE);
        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            downRawX = event.getRawX();
            downRawY = event.getRawY();
            dX = v.getX() - downRawX;
            dY = v.getY() - downRawY;
            return true; // Consumed
        }
        else if (action == MotionEvent.ACTION_MOVE) {
            int viewWidth = v.getWidth();
            int viewHeight = v.getHeight();

            View viewParent = (View)v.getParent();
            int parentWidth = viewParent.getWidth();
            int parentHeight = viewParent.getHeight();

            float newX = event.getRawX() + dX;
            newX = Math.max(0, newX); // Don't allow the FAB past the left hand side of the parent
            newX = Math.min(parentWidth - viewWidth, newX); // Don't allow the FAB past the right hand side of the parent

            float newY = event.getRawY() + dY;
            newY = Math.max(0, newY); // Don't allow the FAB past the top of the parent
            newY = Math.min(parentHeight - viewHeight, newY); // Don't allow the FAB past the bottom of the parent

            v.animate()
                    .x(newX)
                    .y(newY)
                    .setDuration(0)
                    .start();

            return true; // Consumed

        }
        else if (action == MotionEvent.ACTION_UP) {

            float upRawX = event.getRawX();
            float upRawY = event.getRawY();

            float upDX = upRawX - downRawX;
            float upDY = upRawY - downRawY;

            if (Math.abs(upDX) < CLICK_DRAG_TOLERANCE && Math.abs(upDY) < CLICK_DRAG_TOLERANCE) { // A click
                return v.performClick();
            }
            else { // A drag
                return true; // Consumed
            }
        }
        else {
            return v.onTouchEvent(event);
        }
    }

    @Override
    public void onMapClicked(String lat, String lng) {
        LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
    }

    @Override
    public void onEventClicked(int position) {
        needRefresh = true;
        SavedLocationList savedLocationList = savedLocationLists.get(position);
        String lat = savedLocationList.getLat();
        String lng = savedLocationList.getLng();
        String rad = savedLocationList.getRadius();
        String address = savedLocationList.getEvent_address();
        Intent intent = new Intent(HomePage.this, AddGeoFences.class);
        intent.putExtra("Type","2");
        intent.putExtra("Lat",lat);
        intent.putExtra("Lng",lng);
        intent.putExtra("Radius",rad);
        intent.putExtra("Address",address);
        intent.putExtra("Event_Name",savedLocationList.getEvent_name());
        intent.putExtra("Event_Notes",savedLocationList.getNotes());
        intent.putExtra("Event_type_name",savedLocationList.getEvent_type_name());
        intent.putExtra("Event_type_id",savedLocationList.getEvent_type_id());
        intent.putExtra("Alert_type_name",savedLocationList.getAlert_type());
        intent.putExtra("Alert_type_id",savedLocationList.getAlert_type_id());
        intent.putExtra("Triggering_type_name",savedLocationList.getAlert_when());
        intent.putExtra("Triggering_type_id",savedLocationList.getAlert_when_id());
        intent.putExtra("Repetition_type_name",savedLocationList.getRepeat_type());
        intent.putExtra("Repetition_type_id",savedLocationList.getRepeat_type_id());
        intent.putParcelableArrayListExtra("custom_date",savedLocationList.getCustomRepetitionDataLists());
        intent.putExtra("geo_id",savedLocationList.getGeo_id());

        startActivity(intent);
    }

    @Override
    public void onSwitchClicked(int position, boolean isChecked, SwitchCompat geoSwitch) {
        SavedLocationList location = savedLocationLists.get(position);
        boolean canBeChanged = true;

        if (!location.isActive()) {
            if (location.getRepeat_type_id().equals("3")) {
                Calendar calendar = Calendar.getInstance();
                Date to_date = calendar.getTime();
                SimpleDateFormat da = new SimpleDateFormat("dd MMM, yy", Locale.ENGLISH);
                String n_date = da.format(to_date);

                ArrayList<CustomRepetitionDataList> customRepetitionDataLists = location.getCustomRepetitionDataLists();

                boolean ff = false;
                for (int i = 0; i < customRepetitionDataLists.size(); i++) {
                    String end_date = customRepetitionDataLists.get(i).getEnd_date();
                    Date date = null;
                    Date e_date = null;
                    try {
                        date = da.parse(n_date);
                        e_date = da.parse(end_date);
                    } catch (ParseException e) {
                        String er = e.getLocalizedMessage();
                        System.out.println(er);
                    }
                    if (date != null && e_date != null) {
                        if (date.before(e_date) || date.equals(e_date)) {
                            ff = true;
                            break;
                        }
                    }
                }
                canBeChanged = ff;
            }
        }

        if (canBeChanged) {
            geoSwitch.setChecked(!isChecked);
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
            alertDialogBuilder.setTitle(isChecked? "Activation":"De-Activation")
                    .setMessage(isChecked ? "Do you want to activate GeoFence Event?" : "Do you want to de-activate GeoFence Event?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        updateActiveLocation(location,isChecked,geoSwitch);
                        dialog.dismiss();
                    })
                    .setNegativeButton("No",(dialog, which) -> dialog.dismiss());

            AlertDialog alert = alertDialogBuilder.create();
            alert.show();

        }
        else {
            if (isChecked) {
                geoSwitch.setChecked(false);
            }
            Toast.makeText(this, "Your custom date has expired. Please update your event time and enable again.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDeleteClicked(int position) {
        String id = savedLocationLists.get(position).getGeo_id();
        String repeat_id = savedLocationLists.get(position).getRepeat_type_id();
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
            alertDialogBuilder.setTitle("Need to Set GPS ON")
                    .setMessage("To delete your GeoFence Event you need to Turn On your Location")
                    .setPositiveButton("OK", (dialog, which) -> {
                        newEnableGps();
                        dialog.dismiss();
                    })
                    .setNegativeButton("Cancel",(dialog, which) -> dialog.dismiss());

            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }
        else {
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
            alertDialogBuilder.setTitle("Delete Event")
                    .setMessage("Do you want to delete GeoFence Event?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        deleteLocation(id,repeat_id,position);
                        dialog.dismiss();
                    })
                    .setNegativeButton("No",(dialog, which) -> dialog.dismiss());

            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }

    }


    // Updating Activeness of Geofence
    public void updateActiveLocation(SavedLocationList locationList, boolean isChecked,SwitchCompat geoSwitch) {
        circularProgressIndicator.setVisibility(View.VISIBLE);
        fullLayout.setVisibility(View.GONE);
        addLocation.setVisibility(View.GONE);
        conn = false;
        connected = false;
        loading = true;

        String id = locationList.getGeo_id();

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(KEY_LOC_TABLE_NAME).whereEqualTo(KEY_LOC_ID,id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().getDocuments().isEmpty()) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        System.out.println(documentSnapshot.getId());

                        database.collection(KEY_LOC_TABLE_NAME).document(documentSnapshot.getId()).update(KEY_GEO_IS_ACTIVE,isChecked)
                                .addOnSuccessListener(unused -> {
                                    conn = true;
                                    connected = true;
                                    updateGeoLayout(locationList,isChecked,geoSwitch);
                                })
                                .addOnFailureListener(e -> {
                                    conn = false;
                                    connected = false;
                                    parsing_message = e.getLocalizedMessage();
                                    updateGeoLayout(locationList,isChecked,geoSwitch);
                                });
                    }
                    else {
                        conn = false;
                        connected = false;
                        parsing_message = "No Data Found. Please Try Again";
                        updateGeoLayout(locationList,isChecked,geoSwitch);
                    }
                })
                .addOnFailureListener(e -> {
                    conn = false;
                    connected = false;
                    parsing_message = e.getLocalizedMessage();
                    updateGeoLayout(locationList,isChecked,geoSwitch);
                });
    }

    private void updateGeoLayout(SavedLocationList locationList, boolean isChecked,SwitchCompat geoSwitch) {
        loading = false;
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                addLocation.setVisibility(View.VISIBLE);

                conn = false;
                connected = false;

                locationList.setActive(isChecked);
                geoSwitch.setChecked(isChecked);


            }
            else {
                alertMessage1(locationList,isChecked,geoSwitch);
            }
        }
        else {
            alertMessage1(locationList,isChecked,geoSwitch);
        }
    }

    public void alertMessage1(SavedLocationList locationList, boolean isChecked, SwitchCompat geoSwitch) {
        fullLayout.setVisibility(View.VISIBLE);
        circularProgressIndicator.setVisibility(View.GONE);
        addLocation.setVisibility(View.VISIBLE);
        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    updateActiveLocation(locationList,isChecked,geoSwitch);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    geoSwitch.setChecked(!isChecked);
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }


    // Deleting Geofence
    public void deleteLocation(String ge_id, String repeat_id, int position) {
        circularProgressIndicator.setVisibility(View.VISIBLE);
        fullLayout.setVisibility(View.GONE);
        addLocation.setVisibility(View.GONE);
        conn = false;
        connected = false;
        loading = true;

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(KEY_LOC_TABLE_NAME).whereEqualTo(KEY_LOC_ID,ge_id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().getDocuments().isEmpty()) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        database.collection(KEY_LOC_TABLE_NAME).document(documentSnapshot.getId()).delete()
                                .addOnSuccessListener(unused -> {
                                    if (repeat_id.equals("3")) {
                                        database.collection(KEY_GEO_TIME_TABLE_NAME).whereEqualTo(KEY_GEO_ID_FOR_TIME,ge_id)
                                                .get()
                                                .addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful() && task1.getResult() != null && !task1.getResult().getDocuments().isEmpty()) {
                                                        List<DocumentSnapshot> documentSnapshots = task1.getResult().getDocuments();
                                                        deleteLists = new ArrayList<>();
                                                        for (int i = 0; i < documentSnapshots.size(); i++) {
                                                            deleteLists.add(new DocumentDeleteList(documentSnapshots.get(i).getId(),false));
                                                        }
                                                        checkToDeleteCustomTime(ge_id, repeat_id, position);
                                                    }
                                                    else {
                                                        conn = true;
                                                        connected = true;
                                                        updateDeleteGeoLayout(ge_id, repeat_id, position);
                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    conn = true;
                                                    connected = true;
                                                    updateDeleteGeoLayout(ge_id, repeat_id, position);
                                                });
                                    }
                                    else {
                                        conn = true;
                                        connected = true;
                                        updateDeleteGeoLayout(ge_id, repeat_id, position);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    conn = false;
                                    connected = false;
                                    parsing_message = e.getLocalizedMessage();
                                    updateDeleteGeoLayout(ge_id, repeat_id, position);
                                });
                    }
                    else {
                        conn = false;
                        connected = false;
                        parsing_message = "No Data Found. Please Try Again";
                        updateDeleteGeoLayout(ge_id, repeat_id, position);
                    }
                })
                .addOnFailureListener(e -> {
                    conn = false;
                    connected = false;
                    parsing_message = e.getLocalizedMessage();
                    updateDeleteGeoLayout(ge_id, repeat_id, position);
                });
    }

    public void checkToDeleteCustomTime(String ge_id, String repeat_id, int position) {
        boolean allUpdated = deleteLists.isEmpty();
        for (int x = 0; x < deleteLists.size(); x++) {
            allUpdated = deleteLists.get(x).isDeleted();
            if (!deleteLists.get(x).isDeleted()) {
                deleteCustomDate(x, deleteLists.get(x).getDocumentId(), ge_id, repeat_id, position);
                break;
            }
        }
        if (allUpdated) {
            conn = true;
            connected = true;
            updateDeleteGeoLayout(ge_id, repeat_id, position);
        }
    }

    private void deleteCustomDate(int index, String id, String ge_id, String repeat_id, int position) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection(KEY_GEO_TIME_TABLE_NAME).document(id).delete()
                .addOnSuccessListener(unused1 -> {
                    deleteLists.get(index).setDeleted(true);
                    checkToDeleteCustomTime(ge_id, repeat_id, position);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to Delete. Retrying...", Toast.LENGTH_SHORT).show();
                    checkToDeleteCustomTime(ge_id, repeat_id, position);
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateDeleteGeoLayout(String ge_id, String repeat_id, int position) {
        loading = false;
        if (conn) {
            if (connected) {
                conn = false;
                connected = false;

                savedLocationLists.remove(position);
                savedLocationAdapter.notifyDataSetChanged();

                ArrayList<String> ids = new ArrayList<>();
                ids.add(ge_id);
                GeofencingClient geofencingClient = LocationServices.getGeofencingClient(this);
                geofencingClient.removeGeofences(ids)
                        .addOnSuccessListener(unused -> Toast.makeText(getApplicationContext(), "Event Deleted Successfully", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> {
                            String errorMessage = getErrorString(e);
                            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        });

                mMap.clear();
                for (int i = 0; i < savedLocationLists.size(); i++) {
                    mMap.addCircle(new CircleOptions()
                            .center(new LatLng(Float.parseFloat(savedLocationLists.get(i).getLat()), Float.parseFloat(savedLocationLists.get(i).getLng())))
                            .radius(Integer.parseInt(savedLocationLists.get(i).getRadius()))
                            .strokeColor(getColor(R.color.belize_hole))
                            .strokeWidth(4F)
                            .fillColor(getColor(R.color.belize_hole_a)));
                }

                if (savedLocationLists.isEmpty()) {
                    noLocation.setVisibility(View.VISIBLE);
                }
                else {
                    noLocation.setVisibility(View.GONE);
                }

                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                addLocation.setVisibility(View.VISIBLE);
            }
            else {
                alertMessage2(ge_id, repeat_id, position);
            }
        }
        else {
            alertMessage2(ge_id, repeat_id, position);
        }
    }

    public void alertMessage2(String ge_id, String repeat_id, int position) {
        fullLayout.setVisibility(View.VISIBLE);
        circularProgressIndicator.setVisibility(View.GONE);
        addLocation.setVisibility(View.VISIBLE);
        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    deleteLocation(ge_id, repeat_id, position);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> dialog.dismiss());

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    @Override
    public void onPictureChoose(int type) {
        if (type == 1) {
            Intent intent = new Intent(this, CameraPreview.class);
            CameraPreview.setBitmapCallback(this);
            startActivity(intent);
        }
        else if (type == 2) {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        }
    }

    @Override
    public void onBitmapReceived(Bitmap bitmap) {
        Uri uri = getImageUri(this,bitmap);
        startCrop(uri);
    }

    private void startCrop(Uri sourceUri) {
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped_image.jpg"));

        UCrop.of(sourceUri, destinationUri)
                //.withAspectRatio(1, 1)  // Optional: Set aspect ratio
                .withMaxResultSize(1080, 1080) // Optional: Set max resolution
                .start(this);
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        File file = new File(context.getCacheDir(), "temp_image.jpg"); // Store in cache
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (IOException e) {
            logger.log(Level.WARNING,e.getMessage(),e);
        }
        return FileProvider.getUriForFile(context, "ttit.com.shuvo.spotring.fileProvider", file);
    }

//    private Bitmap getCorrectlyOrientedBitmap(Uri uri) {
//        try {
//            InputStream inputStream = getContentResolver().openInputStream(uri);
//            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//
//            if (bitmap == null) {
//                return null;
//            }
//
//            // Get real file path (copying file if necessary)
//            String realPath = copyFileToInternalStorage(uri);
//
//            // Read EXIF data
//            if (realPath != null) {
//                return modifyOrientation(bitmap, realPath);
//            }
//            else {
//                return null;
//            }
//        }
//        catch (IOException e) {
//            logger.log(Level.WARNING,e.getMessage(),e);
//            Toast.makeText(getApplicationContext(),"Failed to upload image",Toast.LENGTH_SHORT).show();
//            return null;
//        }
//    }

//    private String copyFileToInternalStorage(Uri uri) {
//        File directory = getFilesDir(); // Internal storage
//        File file = new File(directory, "temp_image.jpg");
//
//        try (InputStream inputStream = getContentResolver().openInputStream(uri);
//             OutputStream outputStream = new FileOutputStream(file)) {
//
//            byte[] buffer = new byte[1024];
//            int bytesRead;
//            if (inputStream != null) {
//                while ((bytesRead = inputStream.read(buffer)) != -1) {
//                    outputStream.write(buffer, 0, bytesRead);
//                }
//            }
//
//            return file.getAbsolutePath(); // Now you have the file path
//
//        } catch (IOException e) {
//            logger.log(Level.WARNING,e.getMessage(),e);
//            Toast.makeText(getApplicationContext(),"Failed to get image path",Toast.LENGTH_SHORT).show();
//        }
//        return null;
//    }

//    public static Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path) throws IOException {
//        ExifInterface ei = new ExifInterface(image_absolute_path);
//        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//
//        switch (orientation) {
//            case ExifInterface.ORIENTATION_ROTATE_90:
//                return rotate(bitmap, 90);
//
//            case ExifInterface.ORIENTATION_ROTATE_180:
//                return rotate(bitmap, 180);
//
//            case ExifInterface.ORIENTATION_ROTATE_270:
//                return rotate(bitmap, 270);
//
//            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
//                return flip(bitmap, true, false);
//
//            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
//                return flip(bitmap, false, true);
//
//            default:
//                return bitmap;
//        }
//    }

//    public static Bitmap rotate(Bitmap bitmap, float degrees) {
//        Matrix matrix = new Matrix();
//        matrix.postRotate(degrees);
//        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//    }

//    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
//        Matrix matrix = new Matrix();
//        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
//        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//    }

//    public Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//
//        float scaleWidth = ((float) maxWidth) / width;
//        float scaleHeight = ((float) maxHeight) / height;
//        float scale = Math.min(scaleWidth, scaleHeight); // Maintain aspect ratio
//
//        int newWidth = Math.round(width * scale);
//        int newHeight = Math.round(height * scale);
//
//        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
//    }

//    public byte[] compressBitmap(Bitmap bitmap, int maxSizeKB) {
//        int quality = 100; // Start at highest quality
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//
//        do {
//            outputStream.reset(); // Clear the stream
//            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
//            quality -= 5; // Reduce quality in steps of 5
//        } while (outputStream.toByteArray().length / 1024 > maxSizeKB && quality > 5);
//
//        return outputStream.toByteArray();
//    }

//    public void updateUserImage(Uri imageUri) {
//        circularProgressIndicator.setVisibility(View.VISIBLE);
//        fullLayout.setVisibility(View.GONE);
//        addLocation.setVisibility(View.GONE);
//        conn = false;
//        connected = false;
//        loading = true;
//
//        selectedBitmap = resizeBitmap(selectedBitmap, 1080,1080);
//        byte[] finalBArray = compressBitmap(selectedBitmap,1024);
//
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        storage.setMaxUploadRetryTimeMillis(60000);
//        StorageReference storageRef = storage.getReference().child("users/" + p_phone + "/profile.jpg");
//
//        Log.d("Upload", "URI: " + imageUri.toString());
//        Log.d("Upload", "Path: users/" + p_phone + "/profile.jpg");
//
//        UploadTask uploadTask =  storageRef.putBytes(finalBArray);
//        uploadTask.addOnSuccessListener(taskSnapshot -> {
//                    // Get URL
//                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                        String imageUrl = uri.toString();
//                        saveImageUrlToFirestore(imageUrl,imageUri);
//                    });
//                })
//                .addOnFailureListener(e -> {
//                    conn = false;
//                    connected = false;
//                    parsing_message = e.getLocalizedMessage();
//                    updateImageLayout(imageUri);
//                });
//
//        Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
//            if (!task.isSuccessful()) {
//                throw Objects.requireNonNull(task.getException());
//            }
//            // Continue with the task to get the download URL
//            return storageRef.getDownloadUrl();
//        }).addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                Uri downloadUri = task.getResult();
//                saveImageUrlToFirestore(downloadUri.toString(),imageUri);
//            } else {
//                conn = false;
//                connected = false;
//                parsing_message = "Failed to Upload Picture";
//                updateImageLayout(imageUri);
//            }
//        });
//    }

//    private void saveImageUrlToFirestore(String imageUrl, Uri imageUri) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        HashMap<String , Object> data = new HashMap<>();
//        data.put(KEY_USER_IMAGE, imageUrl);
//
//        db.collection(KEY_USER_TABLE_NAME).whereEqualTo(KEY_GEO_USER_NAME,p_phone)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().getDocuments().isEmpty()) {
//                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
//
//                        db.collection(KEY_USER_TABLE_NAME).document(documentSnapshot.getId())
//                                .set(data, SetOptions.merge())
//                                .addOnSuccessListener(unused -> {
//                                    conn = true;
//                                    connected = true;
//                                    updateImageLayout(imageUri);
//                                })
//                                .addOnFailureListener(e -> {
//                                    conn = false;
//                                    connected = false;
//                                    parsing_message = e.getLocalizedMessage();
//                                    updateImageLayout(imageUri);
//                                });
//                    }
//                    else {
//                        conn = false;
//                        connected = false;
//                        parsing_message = "No Data Found. Please Try Again";
//                        updateImageLayout(imageUri);
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    conn = false;
//                    connected = false;
//                    parsing_message = e.getLocalizedMessage();
//                    updateImageLayout(imageUri);
//                });
//
//    }

//    private void updateImageLayout(Uri imageUri) {
//        loading = false;
//        if (conn) {
//            if (connected) {
//                conn = false;
//                connected = false;
//
//                Toast.makeText(getApplicationContext(), "Picture Uploaded", Toast.LENGTH_SHORT).show();
//
//                Glide.with(getApplicationContext())
//                        .load(selectedBitmap)
//                        .fitCenter()
//                        .into(userImage);
//
//            }
//            else {
//                alertMessageImage(imageUri);
//            }
//        }
//        else {
//            alertMessageImage(imageUri);
//        }
//    }

//    public void alertMessageImage(Uri imageUri) {
//        fullLayout.setVisibility(View.VISIBLE);
//        circularProgressIndicator.setVisibility(View.GONE);
//        addLocation.setVisibility(View.VISIBLE);
//        if (parsing_message != null) {
//            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
//                parsing_message = "Server problem or Internet not connected";
//            }
//        }
//        else {
//            parsing_message = "Server problem or Internet not connected";
//        }
//        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
//        alertDialogBuilder.setTitle("Error!")
//                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
//                .setPositiveButton("Retry", (dialog, which) -> {
//                    updateUserImage(imageUri);
//                    dialog.dismiss();
//                })
//                .setNegativeButton("Cancel",(dialog, which) -> dialog.dismiss());
//
//        AlertDialog alert = alertDialogBuilder.create();
//        alert.setCancelable(false);
//        alert.setCanceledOnTouchOutside(false);
//        alert.show();
//    }
}