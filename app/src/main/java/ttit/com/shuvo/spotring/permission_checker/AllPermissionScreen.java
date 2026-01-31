package ttit.com.shuvo.spotring.permission_checker;

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
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import ttit.com.shuvo.spotring.dashboard.HomePage;
import ttit.com.shuvo.spotring.databinding.ActivityAllPermissionScreenBinding;
import ttit.com.shuvo.spotring.user_auth.UserLogin;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class AllPermissionScreen extends AppCompatActivity {

    private ActivityAllPermissionScreenBinding binding;

    boolean foreground_permission = false;
    boolean background_permission = false;
    boolean notification_permission = false;
    boolean over_other_app_permission = false;

    int os_version;

    SharedPreferences sharedPreferences;
    boolean loginFile = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllPermissionScreenBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        os_version = Build.VERSION.SDK_INT;
        sharedPreferences = getSharedPreferences(LOGIN_ACTIVITY_FILE,MODE_PRIVATE);
        loginFile = sharedPreferences.getBoolean(LOGIN_TF,false);

        binding.foregroundLocActivationButton.setOnClickListener(v1 -> {
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
            alertDialogBuilder.setTitle("Location access!")
                    .setMessage("This app uses your location while you are using it to help you create and manage geofence events.\n" +
                            "Location access allows you to select places, set boundaries, and configure location-based alerts\n\n"+
                            "Please select 'While using the app' when prompted for location permission")
                    .setPositiveButton("Continue", (dialog, which) -> {
                        dialog.dismiss();
                        enableForegroundLocation();
                    })
                    .setNegativeButton("Cancel",(dialog, which) -> dialog.dismiss());

            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        });

        binding.backgroundLocActivationButton.setOnClickListener(v1 -> {
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
            alertDialogBuilder.setTitle("Background location access!")
                    .setMessage("This app uses background location to detect when you enter or exit your saved locations and trigger alerts, even when the app is closed or not in use.\n" +
                            "Location data is used only for this feature and is not shared.\n\n"+
                            "Please select 'Allow all the time' from location permission")
                    .setPositiveButton("Continue", (dialog, which) -> {
                        dialog.dismiss();
                        enableBackgroundLocation();
                    })
                    .setNegativeButton("Cancel",(dialog, which) -> dialog.dismiss());

            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        });

        binding.notificationActivationButton.setOnClickListener(v1 -> {
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
            alertDialogBuilder.setTitle("Notification permission!")
                    .setMessage("Notifications are used to alert you when a geofence event is triggered.\n" +
                            "This includes sound, vibration, and important alerts related to your location-based events.\n\n"+
                            "Please select 'Allow' when prompted for notification permission")
                    .setPositiveButton("Continue", (dialog, which) -> {
                        dialog.dismiss();
                        enableNotification();
                    })
                    .setNegativeButton("Cancel",(dialog, which) -> dialog.dismiss());

            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        });

        binding.displayOverActivationButton.setOnClickListener(v1 -> {
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
            alertDialogBuilder.setTitle("Display over other apps!")
                    .setMessage("This permission allows the app to show an alert screen on top of other apps when a geofence event is triggered.\n" +
                            "This helps ensure you don’t miss important alerts.\n\n"+
                            "Please allow display over other apps for this app from settings.")
                    .setPositiveButton("Go to settings", (dialog, which) -> {
                        dialog.dismiss();
                        enableOverOther();
                    })
                    .setNegativeButton("Cancel",(dialog, which) -> dialog.dismiss());

            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        });

        binding.nextStepButton.setOnClickListener(v1 -> {
            Intent intent;
            if (loginFile) {
                intent = new Intent(AllPermissionScreen.this, HomePage.class);
                intent.putExtra("FROM_LOGIN",false);
            } else {
                intent = new Intent(AllPermissionScreen.this, UserLogin.class);
            }
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermissionStat();
    }

    public void checkPermissionStat() {
        boolean all_permitted = false;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

            foreground_permission = true;
        }


        if (os_version >= 29 ) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                background_permission = true;
            }
        }

        if (os_version >= 33 ) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {

                notification_permission = true;
            }
        }

        if (Settings.canDrawOverlays(this)) {
            over_other_app_permission = true;
        }

        if (os_version < 29) {
            binding.foregroundLocLayout.setVisibility(View.VISIBLE);
            binding.displayOverLayout.setVisibility(View.VISIBLE);
            binding.backgroundLocLayout.setVisibility(View.GONE);
            binding.notificationLayout.setVisibility(View.GONE);

            if (foreground_permission && over_other_app_permission) {
                all_permitted = true;
            }
        }
        else if (os_version < 33) {
            binding.foregroundLocLayout.setVisibility(View.VISIBLE);
            binding.displayOverLayout.setVisibility(View.VISIBLE);
            binding.backgroundLocLayout.setVisibility(View.VISIBLE);
            binding.notificationLayout.setVisibility(View.GONE);

            if (foreground_permission && background_permission && over_other_app_permission) {
                all_permitted = true;
            }
        }
        else {
            binding.foregroundLocLayout.setVisibility(View.VISIBLE);
            binding.displayOverLayout.setVisibility(View.VISIBLE);
            binding.backgroundLocLayout.setVisibility(View.VISIBLE);
            binding.notificationLayout.setVisibility(View.VISIBLE);
            if (foreground_permission && background_permission && notification_permission && over_other_app_permission) {
                all_permitted = true;
            }
        }

        if (foreground_permission) {
            binding.foregroundLocActivationButton.setVisibility(View.GONE);
            binding.foregroundLocActivationCard.setVisibility(View.VISIBLE);
        }
        else {
            binding.foregroundLocActivationButton.setVisibility(View.VISIBLE);
            binding.foregroundLocActivationCard.setVisibility(View.GONE);
        }

        if (background_permission) {
            binding.backgroundLocActivationButton.setVisibility(View.GONE);
            binding.backgroundLocActivationCard.setVisibility(View.VISIBLE);
        }
        else {
            binding.backgroundLocActivationButton.setVisibility(View.VISIBLE);
            binding.backgroundLocActivationCard.setVisibility(View.GONE);
        }

        if (notification_permission) {
            binding.notificationActivationButton.setVisibility(View.GONE);
            binding.notificationActivationCard.setVisibility(View.VISIBLE);
        }
        else {
            binding.notificationActivationButton.setVisibility(View.VISIBLE);
            binding.notificationActivationCard.setVisibility(View.GONE);
        }

        if (over_other_app_permission) {
            binding.displayOverActivationButton.setVisibility(View.GONE);
            binding.displayOverActivationCard.setVisibility(View.VISIBLE);
        }
        else {
            binding.displayOverActivationButton.setVisibility(View.VISIBLE);
            binding.displayOverActivationCard.setVisibility(View.GONE);
        }


        binding.nextStepButton.setEnabled(all_permitted);
    }

    public void checkNextButtonValidation() {
        boolean all_permitted = false;
        if (os_version < 29) {
            if (foreground_permission && over_other_app_permission) {
                all_permitted = true;
            }
        }
        else if (os_version < 33) {
            if (foreground_permission && background_permission && over_other_app_permission) {
                all_permitted = true;
            }
        }
        else {
            if (foreground_permission && background_permission && notification_permission && over_other_app_permission) {
                all_permitted = true;
            }
        }

        binding.nextStepButton.setEnabled(all_permitted);
    }

    public void enableForegroundLocation() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {

            Log.i("Ekhane", "3");
            locationResultLauncher.launch(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION});
        }
        else {
            Log.i("Ekhane", "4");
            locationResultLauncher.launch(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION});
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
            foreground_permission = true;
            binding.foregroundLocActivationButton.setVisibility(View.GONE);
            binding.foregroundLocActivationCard.setVisibility(View.VISIBLE);
            checkNextButtonValidation();
        }
        else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                showDialog("Location access required!", "You have denied location access too many times. \n\nThis app needs the precise location permission to help you create and manage geofence events.\nPlease Allow that permission from settings.", "Go to Settings", (dialogInterface, i) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+ getPackageName()));
                    startActivity(intent);
                });
            }
            else {
                System.out.println("HOLA2");
                enableForegroundLocation();
            }
        }
    });

    public void enableBackgroundLocation() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {

            Log.i("Ekhane", "15");
            backLocationResultLauncher.launch(new String[]{android.Manifest.permission.ACCESS_BACKGROUND_LOCATION});
        }
        else {
            Log.i("Ekhane", "16");
            backLocationResultLauncher.launch(new String[]{android.Manifest.permission.ACCESS_BACKGROUND_LOCATION});
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
            background_permission = true;
            binding.backgroundLocActivationButton.setVisibility(View.GONE);
            binding.backgroundLocActivationCard.setVisibility(View.VISIBLE);
            checkNextButtonValidation();
        }
        else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                showDialog("Background Location required!", "You have denied background location access too many times. \n\nThis app uses background location to detect when you enter or exit your saved locations and trigger alerts, even when the app is closed or not in use.\nPlease select 'Allow all the time' from location permission", "Go to Settings", (dialogInterface, i) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+ getPackageName()));
                    startActivity(intent);
                });
            }
            else {
                System.out.println("HOLA2");
                enableBackgroundLocation();
            }
        }
    });

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void enableNotification() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.POST_NOTIFICATIONS)) {
            Log.i("Ekhane", "19");
            notifyPermResultLauncher.launch(new String[]{android.Manifest.permission.POST_NOTIFICATIONS});
        }
        else {
            Log.i("Ekhane", "20");
            notifyPermResultLauncher.launch(new String[]{android.Manifest.permission.POST_NOTIFICATIONS});
        }
    }

    private final ActivityResultLauncher<String[]> notifyPermResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
        System.out.println("OnActivityResult: " +result);
        boolean allGranted = true;
        for (String key: result.keySet()) {
            allGranted = allGranted && Boolean.TRUE.equals(result.get(key));
        }
        if (allGranted) {
            System.out.println("HOLA9");
            notification_permission = true;
            binding.notificationActivationButton.setVisibility(View.GONE);
            binding.notificationActivationCard.setVisibility(View.VISIBLE);
            checkNextButtonValidation();
        }
        else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.POST_NOTIFICATIONS)) {
                showDialog("Notification Permission required!", "You have denied notification permission too many times. \n\nThis app needs the Notification permission to alert you when a geofence event is triggered.\nPlease Allow that permission from settings.", "Go to Settings", (dialogInterface, i) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+ getPackageName()));
                    startActivity(intent);
                });
            }
            else {
                System.out.println("HOLA10");
                enableNotification();
            }
        }
    });

    public void enableOverOther() {
        if (!Settings.canDrawOverlays(this)) {
            Intent overlayIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            overlayPermissionLauncher.launch(overlayIntent);
        }
    }

    ActivityResultLauncher<Intent> overlayPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (Settings.canDrawOverlays(this)) {
                    over_other_app_permission = true;
                    binding.displayOverActivationButton.setVisibility(View.GONE);
                    binding.displayOverActivationCard.setVisibility(View.VISIBLE);
                    checkNextButtonValidation();
                }
            });

    public void showDialog(String title, String message, String positiveButtonTitle, DialogInterface.OnClickListener positiveListener) {
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonTitle, positiveListener)
                .setNegativeButton("Cancel",(dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
}