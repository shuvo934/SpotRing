package ttit.com.shuvo.spotring.geofences.get_location;

import static ttit.com.shuvo.spotring.geofences.AddGeoFences.saveLocationListener;
import static ttit.com.shuvo.spotring.user_auth.UserLogin.userInfoLists;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ttit.com.shuvo.spotring.R;
import ttit.com.shuvo.spotring.databinding.ActivityLocationSelectionBinding;
import ttit.com.shuvo.spotring.geofences.adapters.SearchLocationAdapter;
import ttit.com.shuvo.spotring.geofences.model.SearchLocationList;

public class LocationSelection extends AppCompatActivity implements OnMapReadyCallback, SearchLocationAdapter.ClickedItem {

    ImageView backButton;

    private GoogleMap mMap;

    Spinner mapTypeSpinner;

    ImageView screenChanger;
    Boolean fullScreen = false;
    ImageView myLocation;
    ImageView pinLocation;

    ImageView searchImage;
    RelativeLayout searchBoxLay;
    TextInputLayout searchTextLay;
    TextInputEditText searchText;
    String searchedText = "";

    MaterialCardView searchButton;
    TextView searchButtonText;

    ArrayList<SearchLocationList> searchLocationLists;
    RelativeLayout searchResultLayout;

    RecyclerView locationView;
    RecyclerView.LayoutManager layoutManager;
    SearchLocationAdapter searchLocationAdapter;

    LinearLayout slLayout;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationManager locationManager;

    TextView radiusText;
    SeekBar seekBar;
    MaterialButton saveButton;

    LatLng centerLatLng;
    String radius_text = "";
    private Circle circle;

    private int shortAnimationDuration;

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

        searchImage = binding.searchLocationIcon;
        searchBoxLay = binding.searchBoxLay;
        searchBoxLay.setVisibility(View.GONE);

        searchTextLay = binding.searchLocationEditLayout;
        searchText = binding.searchLocationEditText;

        searchButton = binding.searchButtonForLocation;
        searchButtonText = binding.searchButtonText;

        searchLocationLists = new ArrayList<>();

        searchResultLayout = binding.searchResultLayout;
        searchResultLayout.setVisibility(View.GONE);

        locationView = binding.searchResultLocationView;
        locationView.setHasFixedSize(true);
        locationView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        locationView.setLayoutManager(layoutManager);

        slLayout = binding.radiusSaveLayout;

        radiusText = binding.radiusTextToSave;
        seekBar = binding.radiusSeekBar;
        saveButton = binding.setLocationButton;

        shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

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

        searchImage.setOnClickListener(view -> {
            searchResultLayout.setVisibility(View.GONE);
            if (searchBoxLay.getVisibility() == View.GONE) {
                searchBoxLay.setVisibility(View.VISIBLE);
                searchButton.setVisibility(View.GONE);
                if (!Objects.requireNonNull(searchText.getText()).toString().isEmpty()) {
                    searchButton.setVisibility(View.VISIBLE);
                }
                searchBoxLay.setAlpha(0.0f);
                // Start the animation
                searchBoxLay.animate()
                        .alpha(1.0f)
                        .setDuration(shortAnimationDuration)
                        .setListener(null);
            }
            else {
                searchButton.setVisibility(View.GONE);
                searchBoxLay.animate()
                        .alpha(0.0f)
                        .setDuration(shortAnimationDuration)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                searchBoxLay.setVisibility(View.GONE);
                            }
                        });
            }
        });

        searchTextLay.setEndIconOnClickListener(view -> {
            searchResultLayout.setVisibility(View.GONE);
            searchText.setText("");
            if (searchText.hasFocus()) {
                searchText.clearFocus();
            }
        });

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchResultLayout.setVisibility(View.GONE);
                if (s.toString().isEmpty()) {
                    searchButton.setVisibility(View.GONE);
                }
                else {
                    searchButton.setVisibility(View.VISIBLE);
                }
            }
        });

        searchText.setOnEditorActionListener((v2, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.

                    closeKeyBoard();
                    searchText.clearFocus();

                    searchedText = Objects.requireNonNull(searchText.getText()).toString();

                    if (!searchedText.isEmpty()) {
                        getSearchedLocation();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Please write your location then search", Toast.LENGTH_SHORT).show();
                    }

                    return false; // consume.
                }
            }
            return false;
        });

        searchButton.setOnClickListener(v1 -> {
            searchedText = Objects.requireNonNull(searchText.getText()).toString();

            if (!searchedText.isEmpty()) {
                getSearchedLocation();
            }
            else {
                Toast.makeText(getApplicationContext(), "Please write your location then search", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void closeKeyBoard() {
        View view = getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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

    public void getSearchedLocation() {
        String st = "SEARCHING...";
        searchButtonText.setText(st);

        searchLocationLists = new ArrayList<>();
        String url = "https://nominatim.openstreetmap.org/search?q="+searchedText+"&format=json&limit=15";

        RequestQueue requestQueue =  Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONArray array = new JSONArray(response);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject info = array.getJSONObject(i);

                    String s_loc_lat = info.getString("lat")
                            .equals("null") ? "" : info.getString("lat");
                    String s_loc_lon = info.getString("lon")
                            .equals("null") ? "" : info.getString("lon");
                    String s_loc_name = info.getString("name")
                            .equals("null") ? "" : info.getString("name");
                    String s_loc_display_name = info.getString("display_name")
                            .equals("null") ? "" : info.getString("display_name");

                    searchLocationLists.add(new SearchLocationList(s_loc_lat,s_loc_lon,s_loc_name,s_loc_display_name));
                }

                String stt = "SEARCH";
                searchButtonText.setText(stt);
                if (searchLocationLists.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Could not Find location for "+searchedText+". Please Try Again.", Toast.LENGTH_SHORT).show();
                }
                else {
                    searchButton.setVisibility(View.GONE);
                    searchResultLayout.setVisibility(View.VISIBLE);

                    searchLocationAdapter = new SearchLocationAdapter(searchLocationLists, LocationSelection.this, LocationSelection.this);
                    locationView.setAdapter(searchLocationAdapter);
                }
            }
            catch (JSONException e) {
                String stt = "SEARCH";
                searchButtonText.setText(stt);
                Toast.makeText(getApplicationContext(), "Location Finding Error: "+e.getLocalizedMessage()+". Please Try Again.", Toast.LENGTH_SHORT).show();
                System.out.println(e.getLocalizedMessage());
            }
        }, error -> {
            String stt = "SEARCH";
            searchButtonText.setText(stt);
            Toast.makeText(getApplicationContext(), "Request Failed Error: "+error.getLocalizedMessage()+". Please Try Again.", Toast.LENGTH_SHORT).show();
            System.out.println(error.getLocalizedMessage());
        })
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", "SpotRingApp/1.0 ("+userInfoLists.get(0).getP_email()+")");
                return headers;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 4,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(stringRequest);
    }

    @Override
    public void onLocationClicked(int position) {
        if (!searchLocationLists.get(position).getLat().isEmpty() && !searchLocationLists.get(position).getLng().isEmpty()) {
            LatLng latLng = new LatLng(Double.parseDouble(searchLocationLists.get(position).getLat()),Double.parseDouble(searchLocationLists.get(position).getLng()));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            searchResultLayout.setVisibility(View.GONE);
            searchButton.setVisibility(View.VISIBLE);
        }
        else {
            Toast.makeText(getApplicationContext(), "Could not find location", Toast.LENGTH_SHORT).show();
        }
    }
}