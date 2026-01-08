package ttit.com.shuvo.spotring.geofences.get_location;

import static ttit.com.shuvo.spotring.geofences.AddGeoFences.saveLocationListener;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import ttit.com.shuvo.spotring.R;
import ttit.com.shuvo.spotring.databinding.ActivityLocationSelectionBinding;

public class LocationSelection extends AppCompatActivity implements OnMapReadyCallback {

    ImageView backButton;

    private GoogleMap mMap;

    Spinner mapTypeSpinner;

    ImageView screenChanger;
    Boolean fullScreen = false;
    ImageView myLocation;
    ImageView pinLocation;

    LinearLayout slLayout;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationManager locationManager;

    TextView radiusText;
    SeekBar seekBar;
    MaterialButton saveButton;

    LatLng centerLatLng;
    String radius_text = "";
    private Circle circle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(LocationSelection.this, R.color.white));

        ActivityLocationSelectionBinding binding = ActivityLocationSelectionBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        backButton = binding.backIconSetLocation;

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.location_map_to_save);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        mapTypeSpinner = binding.mapTypeSpinnerToSave;
        screenChanger = binding.fullScreenChangerToSave;
        myLocation = binding.myLocationIconToSave;
        pinLocation = binding.locationPin;

        slLayout = binding.radiusSaveLayout;

        radiusText = binding.radiusTextToSave;
        seekBar = binding.radiusSeekBar;
        saveButton = binding.setLocationButton;

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        screenChanger.setOnClickListener(view -> {
            if (!fullScreen) {
                slLayout.setVisibility(View.GONE);
                screenChanger.setImageResource(R.drawable.fullscreen_exit);
                fullScreen = true;
            } else {
                slLayout.setVisibility(View.VISIBLE);
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

        Intent intent = getIntent();
        radius_text = intent.getStringExtra("RAD");

        double lat = intent.getDoubleExtra("LAT",0);
        double lng = intent.getDoubleExtra("LNG",0);


        if (lat == 0 || lng == 0) {
            centerLatLng = null;
            System.out.println("HELL");
        }
        else {
            centerLatLng = new LatLng(lat,lng);
            System.out.println("HELLO");
        }

        if (radius_text == null || radius_text.isEmpty()) {
            radius_text = "300";
        }
        String text = "Radius :  "+radius_text+ " Meters";
        radiusText.setText(text);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seekBar.setMin(200);
        }
        seekBar.setProgress(Integer.parseInt(radius_text));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mMap != null && centerLatLng != null) {
                    updateCircle(i);
                    radius_text = String.valueOf(i);
                    String text = "Radius :  "+radius_text+ " Meters";
                    radiusText.setText(text);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        backButton.setOnClickListener(view -> finish());

        saveButton.setOnClickListener(view -> {
            if (!radius_text.isEmpty()) {
                if (centerLatLng != null) {
                    if (saveLocationListener != null) {
                        saveLocationListener.onSave(radius_text, centerLatLng);
                        finish();
                    }
                    else {
                        System.out.println("NOT FOUND");
                    }
                }
                else {
                    Toast.makeText(this, "Could not get Location, Please Try again", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(this, "Please Select Radius", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);

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
                                            LocationSelection.this, R.raw.normal));

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
                                            LocationSelection.this, R.raw.no_landmark));

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

        mMap.setOnCameraMoveListener(() -> mMap.clear());

        mMap.setOnCameraIdleListener(() -> {
            System.out.println("HELLALALAL");
            LatLng latLng = mMap.getCameraPosition().target;
            if (latLng.latitude != 0 && latLng.longitude != 0) {
                centerLatLng = latLng;
                updateCircle(seekBar.getProgress());
            }
        });

        newEnableGps();
    }

    private void updateCircle(double radius) {
        if (circle != null) {
            circle.remove();
        }
        circle = mMap.addCircle(new CircleOptions()
                .center(centerLatLng)
                .radius(radius)
                .strokeColor(getColor(R.color.belize_hole))
                .strokeWidth(4F)
                .fillColor(getColor(R.color.belize_hole_a)));
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
        });

        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(LocationSelection.this,
                            1001);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error.
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
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
                if (centerLatLng != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng, 14));
                }
                else {
                    LatLng latLng = new LatLng(23.6850, 90.3563);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7));
                }
                //Write your code if there's no result
                Log.i("Hoise ", "2");
            }
        }
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
                if (centerLatLng != null) {
                    System.out.println("HELLLLLOOOO");
                    System.out.println(centerLatLng);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng, 14));
                }
                else {
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                }
            }
            else {
                if (centerLatLng != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng, 14));
                }
                else {
                    latLng = new LatLng(23.6850, 90.3563);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7));
                }
            }

        });
        locationTask.addOnFailureListener(e -> {
            if (centerLatLng != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng, 14));
            }
            else {
                LatLng latLng = new LatLng(23.6850, 90.3563);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7));
            }
        });
//        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }
}