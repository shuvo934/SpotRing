package ttit.com.shuvo.spotring;

import static ttit.com.shuvo.spotring.utilities.Constants.LOGIN_ACTIVITY_FILE;
import static ttit.com.shuvo.spotring.utilities.Constants.LOGIN_TF;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import ttit.com.shuvo.spotring.dashboard.HomePage;
import ttit.com.shuvo.spotring.user_auth.UserLogin;

public class MainActivity extends AppCompatActivity {

    private final Handler mHandler = new Handler();
    public boolean allPerm = false;
    SharedPreferences sharedPreferences;
    boolean loginfile = false;
//    private static final int REQUEST_CODE_OVERLAY = 112234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(LOGIN_ACTIVITY_FILE,MODE_PRIVATE);
        loginfile = sharedPreferences.getBoolean(LOGIN_TF,false);

        enableLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (allPerm) {
            allPerm = false;
            System.out.println("LETS SEE");
            enableLocation();
        }
    }

    private void enableLocation() {
        if (Build.VERSION.SDK_INT >= 29) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                Log.i("Ekhane", "1");

                enableBackgroundLocation();
            }
            else {
                Log.i("Ekhane", "2");
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {

                    Log.i("Ekhane", "3");
                    showDialog("Location Permission!", "This app needs the location permission for functioning.", "OK", (dialogInterface, i) -> locationResultLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}));
                }
                else {
                    Log.i("Ekhane", "4");
                    locationResultLauncher.launch(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION});
                }
            }
        }

        else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                Log.i("Ekhane", "5");

                checkAlertWindow();
            }
            else {
                Log.i("Ekhane", "6");
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {

                    Log.i("Ekhane", "7");
                    showDialog("Location Permission!", "This app needs the location permission for functioning.", "OK", (dialogInterface, i) -> locationResultLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}));
                }
                else {
                    Log.i("Ekhane", "8");
                    locationResultLauncher.launch(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION});
                }
            }
        }
    }

    private final ActivityResultLauncher<String[]> locationResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
        System.out.println("OnActivityResult: " +result);
        boolean allGranted = true;
        for (String key: result.keySet()) {
            allGranted = allGranted && Boolean.TRUE.equals(result.get(key));
        }
        if (allGranted) {
            System.out.println("HOLA1");
            if (Build.VERSION.SDK_INT >= 29) {
                enableBackgroundLocation();
            }
            else {
                checkAlertWindow();
            }
        }
        else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                showDialog("Location Permission!", "This app needs the precise location permission to function. Please Allow that permission from settings.", "Go to Settings", (dialogInterface, i) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+ getPackageName()));
                    startActivity(intent);
                    allPerm = true;
                });
            }
            else {
                System.out.println("HOLA2");
                enableLocation();
            }
        }
    });

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void enableBackgroundLocation() {
        if (Build.VERSION.SDK_INT < 33) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                Log.i("Ekhane", "9");

                checkAlertWindow();
            }
            else {
                Log.i("Ekhane", "10");
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {

                    Log.i("Ekhane", "11");
                    showDialog("Background Location Permission!", "This app needs the background location permission for functioning.", "OK", (dialogInterface, i) -> backLocationResultLauncher.launch(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}));
                }
                else {
                    Log.i("Ekhane", "12");
                    backLocationResultLauncher.launch(new String[]{android.Manifest.permission.ACCESS_BACKGROUND_LOCATION});
                }
            }
        }

        else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                Log.i("Ekhane", "13");

                enableNotification();
            }
            else {
                Log.i("Ekhane", "14");
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {

                    Log.i("Ekhane", "15");
                    showDialog("Background Location Permission!", "This app needs the background location permission for functioning.", "OK", (dialogInterface, i) -> backLocationResultLauncher.launch(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}));
                }
                else {
                    Log.i("Ekhane", "16");
                    backLocationResultLauncher.launch(new String[]{android.Manifest.permission.ACCESS_BACKGROUND_LOCATION});
                }
            }
        }
    }

    private final ActivityResultLauncher<String[]> backLocationResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
        System.out.println("OnActivityResult: " +result);
        boolean allGranted = true;
        for (String key: result.keySet()) {
            allGranted = allGranted && Boolean.TRUE.equals(result.get(key));
        }
        if (allGranted) {
            System.out.println("HOLA3");
            if (Build.VERSION.SDK_INT < 33) {
                checkAlertWindow();
            }
            else {
                enableNotification();
            }
        }
        else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                showDialog("Background Location Permission!", "This app needs the background location permission to function. Please Allow that permission from settings.", "Go to Settings", (dialogInterface, i) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+ getPackageName()));
                    startActivity(intent);
                    allPerm = true;
                });
            }
            else {
                System.out.println("HOLA2");
                enableLocation();
            }
        }
    });

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void enableNotification() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {

            Log.i("Ekhane", "17");
            checkAlertWindow();
        }
        else {
            Log.i("Ekhane", "18");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.POST_NOTIFICATIONS)) {
                Log.i("Ekhane", "19");
                showDialog("Notification Permission!", "This app needs the Notification permission for functioning.", "OK", (dialogInterface, i) -> notifyPermResultLauncher.launch(new String[]{Manifest.permission.POST_NOTIFICATIONS}));
            }
            else {
                Log.i("Ekhane", "20");
                notifyPermResultLauncher.launch(new String[]{android.Manifest.permission.POST_NOTIFICATIONS});
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private final ActivityResultLauncher<String[]> notifyPermResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
        System.out.println("OnActivityResult: " +result);
        boolean allGranted = true;
        for (String key: result.keySet()) {
            allGranted = allGranted && Boolean.TRUE.equals(result.get(key));
        }
        if (allGranted) {
            System.out.println("HOLA9");
            checkAlertWindow();
        }
        else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.POST_NOTIFICATIONS)) {
                showDialog("Notification Permission!", "This app needs the Notification permission to function. Please Allow that permission from settings.", "Go to Settings", (dialogInterface, i) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+ getPackageName()));
                    startActivity(intent);
                    allPerm = true;
                });
            }
            else {
                System.out.println("HOLA10");
                enableNotification();
            }
        }
    });

    public void showDialog(String title, String message, String positiveButtonTitle, DialogInterface.OnClickListener positiveListener) {
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonTitle, positiveListener);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public void checkAlertWindow() {
        if (!Settings.canDrawOverlays(this)) {
            Intent overlayIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            overlayPermissionLauncher.launch(overlayIntent);
//            startActivityForResult(overlayIntent, REQUEST_CODE_OVERLAY);
        }
        else {
//            if (!isBatteryOptimizationDisabled(this)) {
//                // Battery optimization is enabled, ask user to disable it
//                System.out.println("BATTERY");
//                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
//                alertDialogBuilder
//                        .setTitle("Please Turn Off Battery Optimization!")
//                        .setMessage("To access some background works you need to disable battery optimization.")
//                        .setPositiveButton("OK", (dialogInterface, i) -> requestDisableBatteryOptimization(this));
//                AlertDialog alertDialog = alertDialogBuilder.create();
//                alertDialog.setCancelable(false);
//                alertDialog.setCanceledOnTouchOutside(false);
//                alertDialog.show();
//            }
//            else {
//                enableCameraPermission();
//            }
//            enableCameraPermission();
            goToDashboard();
        }
    }

//    public static boolean isBatteryOptimizationDisabled(Context context) {
//        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//        return powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
//    }
//
//    public void requestDisableBatteryOptimization(Context context) {
//        Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
//        context.startActivity(intent);
//        allPerm = true;
//    }

//    private void enableCameraPermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//                == PackageManager.PERMISSION_GRANTED) {
//
//            Log.i("Ekhane", "13");
//            goToDashboard();
//        }
//        else {
//            Log.i("Ekhane", "14");
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
//                Log.i("Ekhane", "15");
//                showDialog("Camera Permission!", "This app needs the Camera permission for functioning.", "OK", (dialogInterface, i) -> cameraPermResultLauncher.launch(Manifest.permission.CAMERA));
//            }
//            else {
//                Log.i("Ekhane", "16");
//                cameraPermResultLauncher.launch(Manifest.permission.CAMERA);
//            }
//        }
//    }
//
//    ActivityResultLauncher<String> cameraPermResultLauncher  = registerForActivityResult(
//            new ActivityResultContracts.RequestPermission(), result -> {
//                System.out.println("OnActivityResult: " +result);
//                if (result) {
//                    System.out.println("HOLA5");
//                    goToDashboard();
//                }
//                else {
//                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
//                        showDialog("Camera Permission!", "This app needs the Camera permission to function. Please Allow that permission from settings.", "Go to Settings", (dialogInterface, i) -> {
//                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+ getPackageName()));
//                            startActivity(intent);
//                            allPerm = true;
//                        });
//                    }
//                    else {
//                        System.out.println("HOLA6");
//                        enableCameraPermission();
//                    }
//                }
//            });

//    public void checkBatteryOptimization() {
//        Intent intent = new Intent();
//        String packageName = getPackageName();
//        intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
//        intent.setData(Uri.parse("package:" + packageName));
//        startActivityForResult(intent,1478);
//    }

    ActivityResultLauncher<Intent> overlayPermissionLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(), result -> {
                        if (Settings.canDrawOverlays(this)) {
                            goToDashboard();
                        } else {
                            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
                            alertDialogBuilder
                                    .setTitle("Overlay Permission Denied!")
                                    .setMessage("You need to Allow Overlay Permission for apps feature.")
                                    .setPositiveButton("OK", (dialogInterface, i) -> checkAlertWindow());
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.setCancelable(false);
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.show();
                        }
                    });

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE_OVERLAY) {
//            if (Settings.canDrawOverlays(this)) {
//                checkAlertWindow();
//            }
//            else {
//                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
//                alertDialogBuilder
//                        .setTitle("Overlay Permission Denied!")
//                        .setMessage("You need to Allow Overlay Permission for apps feature.")
//                        .setPositiveButton("OK", (dialogInterface, i) -> checkAlertWindow());
//                AlertDialog alertDialog = alertDialogBuilder.create();
//                alertDialog.setCancelable(false);
//                alertDialog.setCanceledOnTouchOutside(false);
//                alertDialog.show();
//            }
//        }
//        else if (requestCode == 1478) {
//            if (isBatteryOptimizationDisabled(this)) {
//                enableCameraPermission();
//            }
//            else {
//                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
//                alertDialogBuilder
//                        .setTitle("Please Turn Off Battery Optimization!")
//                        .setMessage("To access some background works you need to disable battery optimization.")
//                        .setPositiveButton("OK", (dialogInterface, i) -> checkAlertWindow());
//                AlertDialog alertDialog = alertDialogBuilder.create();
//                alertDialog.setCancelable(false);
//                alertDialog.setCanceledOnTouchOutside(false);
//                alertDialog.show();
//            }
//        }
//    }

    private void goToDashboard() {
        mHandler.postDelayed(() -> {

            Intent intent;
            if (loginfile) {
                intent = new Intent(MainActivity.this, HomePage.class);
                intent.putExtra("FROM_LOGIN",false);
            } else {
                intent = new Intent(MainActivity.this, UserLogin.class);
            }
            startActivity(intent);
            finish();
        }, 2000);
    }
}