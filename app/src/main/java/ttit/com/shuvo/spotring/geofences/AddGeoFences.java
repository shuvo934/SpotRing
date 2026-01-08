package ttit.com.shuvo.spotring.geofences;

import static ttit.com.shuvo.spotring.user_auth.UserLogin.userInfoLists;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_EVENT_TABLE_NAME;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_EVENT_TYPE_ID;
import static ttit.com.shuvo.spotring.utilities.Constants.KEY_EVENT_TYPE_NAME;
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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.joery.timerangepicker.TimeRangePicker;
import ttit.com.shuvo.spotring.R;
import ttit.com.shuvo.spotring.databinding.ActivityAddGeoFencesBinding;
import ttit.com.shuvo.spotring.geofences.adapters.BottomSheetAdapter;
import ttit.com.shuvo.spotring.geofences.adapters.CustomRepetitionAdapter;
import ttit.com.shuvo.spotring.geofences.get_location.LocationSelection;
import ttit.com.shuvo.spotring.geofences.interfaces.SaveLocationListener;
import ttit.com.shuvo.spotring.geofences.model.CustomRepetitionDataList;
import ttit.com.shuvo.spotring.geofences.model.DocumentDeleteList;
import ttit.com.shuvo.spotring.geofences.model.SpinnerItemList;
import ttit.com.shuvo.spotring.geofences.receiver.GeofenceBroadcastReceiver;

public class AddGeoFences extends AppCompatActivity implements OnMapReadyCallback, SaveLocationListener, BottomSheetAdapter.ClickedItem, CustomRepetitionAdapter.ClickedItem {

    RelativeLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;

    ImageView backButton;
    TextView appBarName;

    TextView savedAddress;
    TextView savedRadius;
    TextView locationMissing;

    CardView mapCard;
    MapView map;
    GoogleMap gMap;

    LatLng saveLatLng;
    String save_radius = "";

    String save_address = "";

    TextInputEditText eventName;
    TextView eventNameMissing;
    String event_name = "";

    TextInputEditText eventDesc;
    String event_desc = "";

    TextInputLayout eventTypeLay;
    TextInputEditText eventType;
    TextView eventTypeMissing;

    String event_type_name = "";
    String event_type_id = "";

    ArrayList<SpinnerItemList> eventTypeLists;

    TextInputEditText alertType;
    TextView alertTypeMissing;
    String alert_type_id = "";
    String alert_type_name = "";

    ArrayList<SpinnerItemList> alertTypeLists;

    TextInputEditText triggeringType;
    TextView triggeringTypeMissing;
    String triggering_type_id = "";
    String triggering_type_name = "";

    ArrayList<SpinnerItemList> triggeringTypeLists;

    TextInputEditText repetitionType;
    TextView repetitionTypeMissing;
    String repetition_type_id = "";
    String repetition_type_name = "";

    ArrayList<SpinnerItemList> repetitionLists;

    LinearLayout customLay;
//    TextInputEditText customData;

    public static SaveLocationListener saveLocationListener;

    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean loading = false;
    String parsing_message = "";

    BottomSheetDialog bottomSheetDialog;

    RecyclerView bottomSheetrecyclerView;
    RecyclerView.LayoutManager bottomSheetlayoutManager;
    BottomSheetAdapter bottomSheetAdapter;

    BottomSheetDialog othersBottomSheet;
    TextInputEditText othersEventType;
    MaterialButton othersCancel;
    MaterialButton othersOk;

    BottomSheetDialog bottomSheetPicker;

    SwitchCompat dateRangeSwitch;
    TextInputLayout dateRangLayout;
    TextInputEditText dateRangeText;

    boolean dateRangeEnabled = true;
    private Long selectedStartDate = null;
    private Long selectedEndDate = null;
    String start_date = "";
    String end_date = "";

    SwitchCompat timeRangeSwitch;
    RelativeLayout timeRangeLayout;
    TimeRangePicker timeRangePicker;
    TextView startTimeText;
    TextView endTimeText;

    boolean timeRangeEnabled = true;
    String start_time = "";
    String end_time = "";

    MaterialButton rangeSetOk;
    MaterialButton rangeSetCancel;

    RecyclerView customRepetitionView;
    RecyclerView.LayoutManager layoutManager;
    CustomRepetitionAdapter customRepetitionAdapter;
    ArrayList<CustomRepetitionDataList> customDataLists;
    MaterialButton addMoreCustom;
    int selectedPosition = -1;
    ScrollView scrollView;

    MaterialButton addGeo;
    MaterialButton updateGeo;
    String geoProcessType = "";

    Logger logger = Logger.getLogger(AddGeoFences.class.getName());

    String geo_id = "";
    ArrayList<DocumentDeleteList> deleteLists;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(AddGeoFences.this, R.color.white));

        ActivityAddGeoFencesBinding binding = ActivityAddGeoFencesBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        fullLayout = binding.addGeoFencesFullLayout;
        circularProgressIndicator = binding.progressIndicatorAddGeoFences;
        circularProgressIndicator.setVisibility(View.GONE);

        backButton = binding.backIconAddGeoFences;
        appBarName = binding.appBarNameGeofenceAdd;

        savedAddress = binding.savedAddressForLocationInGeofeces;
        savedAddress.setVisibility(View.GONE);
        savedRadius = binding.savedRadiusForLocationInGeofeces;
        savedRadius.setVisibility(View.GONE);

        mapCard = binding.mapImageSavedCardViewInGeofences;
        map = binding.mapImageInGeofences;

        map.onCreate(savedInstanceState);
        map.getMapAsync(this);

        locationMissing = binding.locationMissingMsg;
        locationMissing.setVisibility(View.GONE);

        eventName = binding.eventNameForSavingGeofences;
        eventNameMissing = binding.eventNameMissingMsg;
        eventNameMissing.setVisibility(View.GONE);

        eventDesc = binding.eventDescriptionForSavingGeofences;

        eventTypeLay = binding.spinnerLayoutEventType;
        eventType = binding.eventTypeForSavingGeofences;
        eventTypeMissing = binding.eventTypeMissingMsg;
        eventTypeMissing.setVisibility(View.GONE);

        eventTypeLists = new ArrayList<>();

        alertType = binding.alertTypeForSavingGeofences;
        alertTypeMissing = binding.alertTypeMissingMsg;
        alertTypeMissing.setVisibility(View.GONE);

        alertTypeLists = new ArrayList<>();
        alertTypeLists.add(new SpinnerItemList("1", "Notification", true, "alert"));
        alertTypeLists.add(new SpinnerItemList("2", "Alarm & Notification", false, "alert"));

        alert_type_name = "Notification";
        alert_type_id = "1";
        alertType.setText(alert_type_name);

        triggeringType = binding.triggeringTypeForSavingGeofences;
        triggeringTypeMissing = binding.triggeringTypeMissingMsg;
        triggeringTypeMissing.setVisibility(View.GONE);

        triggeringTypeLists = new ArrayList<>();
        triggeringTypeLists.add(new SpinnerItemList("1", "Entering the Zone", true, "trigger"));
        triggeringTypeLists.add(new SpinnerItemList("2", "Exiting the Zone", false, "trigger"));
        triggeringTypeLists.add(new SpinnerItemList("3", "Roaming into the Zone", false, "trigger"));
        triggeringTypeLists.add(new SpinnerItemList("4", "Entering and Exiting the Zone", false, "trigger"));
        triggeringTypeLists.add(new SpinnerItemList("5", "Entering and Roaming into the Zone", false, "trigger"));
        triggeringTypeLists.add(new SpinnerItemList("6", "Exiting and Roaming into the Zone", false, "trigger"));
        triggeringTypeLists.add(new SpinnerItemList("7", "For All Occurrences", false, "trigger"));

        triggering_type_name = "Entering the Zone";
        triggering_type_id = "1";
        triggeringType.setText(triggering_type_name);

        repetitionType = binding.repetitionAlertForSavingGeofences;
        repetitionTypeMissing = binding.repetitionAlertMissingMsg;
        repetitionTypeMissing.setVisibility(View.GONE);

        repetitionLists = new ArrayList<>();
        repetitionLists.add(new SpinnerItemList("1", "Once", true, "repetition"));
        repetitionLists.add(new SpinnerItemList("2", "Every Time", false, "repetition"));
        repetitionLists.add(new SpinnerItemList("3", "Custom", false, "repetition"));

        repetition_type_name = "Once";
        repetition_type_id = "1";
        repetitionType.setText(repetition_type_name);

        customLay = binding.customRepetitionInfoLayout;
        customLay.setVisibility(View.GONE);
        customRepetitionView = binding.customRepetitionInfoListView;
        addMoreCustom = binding.addMoreCustomData;
        customDataLists = new ArrayList<>();
        addMoreCustom.setVisibility(View.GONE);

        customRepetitionView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        customRepetitionView.setLayoutManager(layoutManager);

        customRepetitionAdapter = new CustomRepetitionAdapter(customDataLists, AddGeoFences.this, AddGeoFences.this);
        customRepetitionView.setAdapter(customRepetitionAdapter);

        scrollView = binding.addGeoFencesScrolls;

        bottomSheetDialog = new BottomSheetDialog(AddGeoFences.this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_selection);

        bottomSheetrecyclerView = bottomSheetDialog.findViewById(R.id.bottom_sheet_recycler_view);
        bottomSheetlayoutManager = new LinearLayoutManager(AddGeoFences.this);

        bottomSheetrecyclerView.setHasFixedSize(true);
        bottomSheetrecyclerView.setLayoutManager(bottomSheetlayoutManager);

        othersBottomSheet = new BottomSheetDialog(AddGeoFences.this);
        othersBottomSheet.setContentView(R.layout.bottom_sheet_others_text);

        othersEventType = othersBottomSheet.findViewById(R.id.others_event_type_for_saving_geofences);
        othersOk = othersBottomSheet.findViewById(R.id.others_event_type_finalized_button);
        othersCancel = othersBottomSheet.findViewById(R.id.cancel_others_event_type_button);

        addGeo = binding.saveGeofenceButton;
        updateGeo = binding.updateGeofenceButton;

        bottomSheetPicker = new BottomSheetDialog(AddGeoFences.this);
        bottomSheetPicker.setContentView(R.layout.bottom_sheet_date_time_picker);

        View bottomSheet = bottomSheetPicker.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED); // Open fully
            behavior.setDraggable(false);
        }

        View bottomSheetVal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheetVal != null) {
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheetVal);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED); // Open fully
            behavior.setDraggable(false);
        }

        dateRangeSwitch = bottomSheetPicker.findViewById(R.id.date_range_activation_switch);
        dateRangLayout = bottomSheetPicker.findViewById(R.id.date_range_selection_layout_date_picker);
        dateRangeText = bottomSheetPicker.findViewById(R.id.date_range_selection_date_picker);

        timeRangeSwitch = bottomSheetPicker.findViewById(R.id.time_range_activation_switch);
        timeRangeLayout = bottomSheetPicker.findViewById(R.id.time_range_picker_layout);
        timeRangePicker = bottomSheetPicker.findViewById(R.id.single_date_time_picker);
        startTimeText = bottomSheetPicker.findViewById(R.id.start_time_in_time_picker);
        endTimeText = bottomSheetPicker.findViewById(R.id.end_time_in_time_picker);

        rangeSetOk = bottomSheetPicker.findViewById(R.id.date_time_selection_button);
        rangeSetCancel = bottomSheetPicker.findViewById(R.id.cancel_date_time_selection_button);

        assert bottomSheetPicker != null;
        dateRangeSwitch.setChecked(dateRangeEnabled);
        dateRangLayout.setEnabled(true);

        timeRangeSwitch.setChecked(timeRangeEnabled);
        timeRangeLayout.setVisibility(View.VISIBLE);

        start_time = "03:00 PM";
        end_time = "09:00 PM";

        saveLocationListener = this;

        Intent intentData = getIntent();
        geoProcessType = intentData.getStringExtra("Type");
        if (geoProcessType != null) {
            if (geoProcessType.equals("1")) {
                String t = "ADD EVENT";
                appBarName.setText(t);
                addGeo.setVisibility(View.VISIBLE);
                updateGeo.setVisibility(View.GONE);
                Intent l_intent = new Intent(AddGeoFences.this, LocationSelection.class);
                startActivity(l_intent);
            }
            else if (geoProcessType.equals("2")) {
                String t = "UPDATE EVENT";
                appBarName.setText(t);
                addGeo.setVisibility(View.GONE);
                updateGeo.setVisibility(View.VISIBLE);

                String lat = intentData.getStringExtra("Lat");
                String lng = intentData.getStringExtra("Lng");
                if (lat != null && lng != null) {
                    saveLatLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                }
                save_radius = intentData.getStringExtra("Radius") == null ? "" : intentData.getStringExtra("Radius");
                save_address = intentData.getStringExtra("Address") == null ? "" : intentData.getStringExtra("Address");
                event_name = intentData.getStringExtra("Event_Name") == null ? "" : intentData.getStringExtra("Event_Name");
                event_desc = intentData.getStringExtra("Event_Notes") == null ? "" : intentData.getStringExtra("Event_Notes");
                event_type_name = intentData.getStringExtra("Event_type_name") == null ? "" : intentData.getStringExtra("Event_type_name");
                event_type_id = intentData.getStringExtra("Event_type_id") == null ? "" : intentData.getStringExtra("Event_type_id");
                alert_type_name = intentData.getStringExtra("Alert_type_name") == null ? "" : intentData.getStringExtra("Alert_type_name");
                alert_type_id = intentData.getStringExtra("Alert_type_id") == null ? "" : intentData.getStringExtra("Alert_type_id");
                triggering_type_name = intentData.getStringExtra("Triggering_type_name") == null ? "" : intentData.getStringExtra("Triggering_type_name");
                triggering_type_id = intentData.getStringExtra("Triggering_type_id") == null ? "" : intentData.getStringExtra("Triggering_type_id");

                repetition_type_name = intentData.getStringExtra("Repetition_type_name") == null ? "" : intentData.getStringExtra("Repetition_type_name");
                repetition_type_id = intentData.getStringExtra("Repetition_type_id") == null ? "" : intentData.getStringExtra("Repetition_type_id");
                ArrayList<CustomRepetitionDataList> crdl = intentData.getParcelableArrayListExtra("custom_date");
                if (crdl != null) {
                    for (int c = 0; c < crdl.size(); c++) {
                        customDataLists.add(new CustomRepetitionDataList(crdl.get(c).getBegin_date(), crdl.get(c).getEnd_date(), crdl.get(c).getBegin_time(), crdl.get(c).getEnd_time(), crdl.get(c).isUpdated()));
                    }
                }

                geo_id = intentData.getStringExtra("geo_id") == null ? "" : intentData.getStringExtra("geo_id");
            }
        }

        eventName.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    eventName.clearFocus();
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        eventName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    eventNameMissing.setVisibility(View.VISIBLE);
                    event_name = "";
                } else {
                    eventNameMissing.setVisibility(View.GONE);
                    event_name = s.toString();
                }
            }
        });

        eventDesc.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    eventDesc.clearFocus();
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        eventDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    event_desc = "";
                } else {
                    event_desc = s.toString();
                }
            }
        });

        eventType.setOnClickListener(view -> {
            bottomSheetDialog.cancel();
            bottomSheetAdapter = new BottomSheetAdapter(eventTypeLists, AddGeoFences.this, AddGeoFences.this, "event");
            bottomSheetrecyclerView.setAdapter(bottomSheetAdapter);

            bottomSheetDialog.show();
        });

        othersOk.setOnClickListener(view -> {
            String type_name = Objects.requireNonNull(othersEventType.getText()).toString();
            if (type_name.isEmpty()) {
                Toast.makeText(this, "Please write Others Event Type Name", Toast.LENGTH_SHORT).show();
            } else {
                event_type_name = type_name;
                int pos = 0;
                for (int i = 0; i < eventTypeLists.size(); i++) {
                    if (eventTypeLists.get(i).getId().equals("9")) {
                        pos = i;
                    }
                }
                event_type_id = "9";

                for (int i = 0; i < eventTypeLists.size(); i++) {
                    eventTypeLists.get(i).setClicked(false);
                }

                eventTypeLists.get(pos).setClicked(true);

                eventType.setText(event_type_name);

                othersBottomSheet.dismiss();

            }
        });

        othersCancel.setOnClickListener(view -> othersBottomSheet.dismiss());

        othersEventType.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    othersEventType.clearFocus();
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        alertType.setOnClickListener(view -> {
            bottomSheetDialog.cancel();
            bottomSheetAdapter = new BottomSheetAdapter(alertTypeLists, AddGeoFences.this, AddGeoFences.this, "alert");
            bottomSheetrecyclerView.setAdapter(bottomSheetAdapter);

            bottomSheetDialog.show();
        });

        triggeringType.setOnClickListener(view -> {
            bottomSheetDialog.cancel();
            bottomSheetAdapter = new BottomSheetAdapter(triggeringTypeLists, AddGeoFences.this, AddGeoFences.this, "trigger");
            bottomSheetrecyclerView.setAdapter(bottomSheetAdapter);

            bottomSheetDialog.show();
        });

        repetitionType.setOnClickListener(view -> {
            bottomSheetDialog.cancel();
            bottomSheetAdapter = new BottomSheetAdapter(repetitionLists, AddGeoFences.this, AddGeoFences.this, "repetition");
            bottomSheetrecyclerView.setAdapter(bottomSheetAdapter);

            bottomSheetDialog.show();
        });

        dateRangeSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            dateRangeEnabled = b;
            dateRangLayout.setEnabled(b);
            if (b) {
                if (!start_date.isEmpty() && !end_date.isEmpty()) {
                    String dateRange = start_date + " --- " + end_date;
                    dateRangeText.setText(dateRange);
                } else {
                    dateRangeText.setText("");
                }
            } else {
                dateRangeText.setText("");
            }
        });

        dateRangeText.setOnClickListener(view -> {
            CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointForward.now());

            MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("Select Date Range")
                    .setCalendarConstraints(constraintsBuilder.build());

            // Set previously selected dates if available
            if (!start_date.isEmpty() && !end_date.isEmpty()) {
                SimpleDateFormat da = new SimpleDateFormat("dd MMM, yy", Locale.ENGLISH);
                Date s_date = null;
                Date e_date = null;
                try {
                    s_date = da.parse(start_date);
                    e_date = da.parse(end_date);
                } catch (ParseException e) {
                    logger.log(Level.WARNING, e.getMessage(), e);
                }
                if (s_date != null && e_date != null) {
                    System.out.println(s_date);
                    System.out.println(e_date);
                    selectedStartDate = s_date.getTime() + 21600000;
                    selectedEndDate = e_date.getTime() + 21600000;
                    System.out.println(selectedStartDate);
                    System.out.println(selectedEndDate);
                }
                if (selectedStartDate != null && selectedEndDate != null) {
                    builder.setSelection(new Pair<>(selectedStartDate, selectedEndDate));
                }
            }

            MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();

            // Show Date Picker
            datePicker.show(getSupportFragmentManager(), "DATE_PICKER");

            // Handle Date Selection
            datePicker.addOnPositiveButtonClickListener(selection -> {
                selectedStartDate = selection.first;  // Save selected start date
                selectedEndDate = selection.second;
                System.out.println(selectedStartDate);
                System.out.println(selectedEndDate);// Save selected end date
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yy", Locale.ENGLISH);
                String startDate = sdf.format(selection.first);
                String endDate = sdf.format(selection.second);

                start_date = startDate;
                end_date = endDate;
                String dateRange = startDate + " --- " + endDate;
                dateRangeText.setText(dateRange);
            });

        });

        timeRangeSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            timeRangeEnabled = b;
            if (b) {
                timeRangeLayout.setVisibility(View.VISIBLE);
            } else {
                timeRangeLayout.setVisibility(View.GONE);
            }
        });

        timeRangePicker.setOnTimeChangeListener(new TimeRangePicker.OnTimeChangeListener() {
            @Override
            public void onStartTimeChange(@NonNull TimeRangePicker.Time time) {
                Calendar calendar = time.getCalendar();
                Date date = calendar.getTime();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                String st = simpleDateFormat.format(date);
                start_time = st;
                String sss = "Start Time\n" + st;
                startTimeText.setText(sss);
            }

            @Override
            public void onEndTimeChange(@NonNull TimeRangePicker.Time time) {
                Calendar calendar = time.getCalendar();
                Date date = calendar.getTime();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                String et = simpleDateFormat.format(date);
                end_time = et;
                String eee = "End Time\n" + et;
                endTimeText.setText(eee);
            }

            @Override
            public void onDurationChange(@NonNull TimeRangePicker.TimeDuration timeDuration) {

            }
        });

        rangeSetOk.setOnClickListener(view -> {
            if (selectedPosition == -1) {
                if (dateRangeSwitch.isChecked() && timeRangeSwitch.isChecked()) {
                    if (!start_date.isEmpty() && !end_date.isEmpty() && !start_time.isEmpty() && !end_time.isEmpty()) {
                        int pos = 0;
                        for (int i = 0; i < repetitionLists.size(); i++) {
                            if (repetitionLists.get(i).getId().equals("3")) {
                                pos = i;
                            }
                        }
                        repetition_type_id = "3";
                        repetition_type_name = repetitionLists.get(pos).getName();

                        for (int i = 0; i < repetitionLists.size(); i++) {
                            repetitionLists.get(i).setClicked(false);
                        }

                        repetitionLists.get(pos).setClicked(true);

                        repetitionType.setText(repetition_type_name);
                        customLay.setVisibility(View.VISIBLE);
//                        String cd = "From  " + start_date + "  to  " + end_date + "\nat  " + start_time + "  to  " + end_time;
                        customDataLists.add(new CustomRepetitionDataList(start_date, end_date, start_time, end_time, false));
                        customRepetitionAdapter.notifyDataSetChanged();
                        addMoreCustom.setVisibility(View.VISIBLE);
                        bottomSheetPicker.dismiss();
                        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
                    } else {
                        Toast.makeText(AddGeoFences.this, "Please Select Date & Time Range", Toast.LENGTH_SHORT).show();
                    }
                } else if (dateRangeSwitch.isChecked() && !timeRangeSwitch.isChecked()) {
                    if (!start_date.isEmpty() && !end_date.isEmpty()) {

                        int pos = 0;
                        for (int i = 0; i < repetitionLists.size(); i++) {
                            if (repetitionLists.get(i).getId().equals("3")) {
                                pos = i;
                            }
                        }
                        repetition_type_id = "3";
                        repetition_type_name = repetitionLists.get(pos).getName();

                        for (int i = 0; i < repetitionLists.size(); i++) {
                            repetitionLists.get(i).setClicked(false);
                        }

                        repetitionLists.get(pos).setClicked(true);

                        repetitionType.setText(repetition_type_name);
                        customLay.setVisibility(View.VISIBLE);
//                        String cd = "From  " + start_date + "  to  " + end_date;
                        customDataLists.add(new CustomRepetitionDataList(start_date, end_date, "", "", false));
                        customRepetitionAdapter.notifyDataSetChanged();
                        addMoreCustom.setVisibility(View.VISIBLE);

                        bottomSheetPicker.dismiss();
                        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
                    } else {
                        Toast.makeText(AddGeoFences.this, "Please Select Date Range", Toast.LENGTH_SHORT).show();
                    }
                } else if (!dateRangeSwitch.isChecked() && timeRangeSwitch.isChecked()) {
                    if (!start_time.isEmpty() && !end_time.isEmpty()) {

                        int pos = 0;
                        for (int i = 0; i < repetitionLists.size(); i++) {
                            if (repetitionLists.get(i).getId().equals("3")) {
                                pos = i;
                            }
                        }
                        repetition_type_id = "3";
                        repetition_type_name = repetitionLists.get(pos).getName();

                        for (int i = 0; i < repetitionLists.size(); i++) {
                            repetitionLists.get(i).setClicked(false);
                        }

                        repetitionLists.get(pos).setClicked(true);

                        repetitionType.setText(repetition_type_name);
                        customLay.setVisibility(View.VISIBLE);
//                        String cd = "From  " + start_time + "  to  " + end_time;
                        customDataLists.add(new CustomRepetitionDataList("", "", start_time, end_time, false));
                        customRepetitionAdapter.notifyDataSetChanged();
                        addMoreCustom.setVisibility(View.VISIBLE);
                        bottomSheetPicker.dismiss();
                        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
                    } else {
                        Toast.makeText(AddGeoFences.this, "Please Select Time Range", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddGeoFences.this, "Please Select At least Date or Time Range", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (dateRangeSwitch.isChecked() && timeRangeSwitch.isChecked()) {
                    if (!start_date.isEmpty() && !end_date.isEmpty() && !start_time.isEmpty() && !end_time.isEmpty()) {

                        Calendar calendar = Calendar.getInstance();
                        Date to_date = calendar.getTime();
                        SimpleDateFormat da = new SimpleDateFormat("dd MMM, yy", Locale.ENGLISH);
                        String n_date = da.format(to_date);

                        Date date = null;
                        Date s_date = null;
                        try {
                            date = da.parse(n_date);
                            s_date = da.parse(start_date);
                        } catch (ParseException e) {
                            String er = e.getLocalizedMessage();
                            System.out.println(er);
                        }

                        if (date != null && s_date != null) {
                            if (s_date.after(date) || s_date.equals(date)) {
                                int pos = 0;
                                for (int i = 0; i < repetitionLists.size(); i++) {
                                    if (repetitionLists.get(i).getId().equals("3")) {
                                        pos = i;
                                    }
                                }
                                repetition_type_id = "3";
                                repetition_type_name = repetitionLists.get(pos).getName();

                                for (int i = 0; i < repetitionLists.size(); i++) {
                                    repetitionLists.get(i).setClicked(false);
                                }

                                repetitionLists.get(pos).setClicked(true);
                                repetitionType.setText(repetition_type_name);

                                customLay.setVisibility(View.VISIBLE);
//                        String cd = "From  " + start_date + "  to  " + end_date + "\nat  " + start_time + "  to  " + end_time;
                                customDataLists.get(selectedPosition).setBegin_date(start_date);
                                customDataLists.get(selectedPosition).setEnd_date(end_date);
                                customDataLists.get(selectedPosition).setBegin_time(start_time);
                                customDataLists.get(selectedPosition).setEnd_time(end_time);
                                customRepetitionAdapter.notifyDataSetChanged();

                                bottomSheetPicker.dismiss();
                                scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
                            } else {
                                Toast.makeText(AddGeoFences.this, "You can not select Date before today's date. Please Select Date again", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(AddGeoFences.this, "Invalid Date", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AddGeoFences.this, "Please Select Date & Time Range", Toast.LENGTH_SHORT).show();
                    }
                } else if (dateRangeSwitch.isChecked() && !timeRangeSwitch.isChecked()) {
                    if (!start_date.isEmpty() && !end_date.isEmpty()) {

                        Calendar calendar = Calendar.getInstance();
                        Date to_date = calendar.getTime();
                        SimpleDateFormat da = new SimpleDateFormat("dd MMM, yy", Locale.ENGLISH);
                        String n_date = da.format(to_date);

                        Date date = null;
                        Date s_date = null;
                        try {
                            date = da.parse(n_date);
                            s_date = da.parse(start_date);
                        } catch (ParseException e) {
                            String er = e.getLocalizedMessage();
                            System.out.println(er);
                        }

                        if (date != null && s_date != null) {
                            if (s_date.after(date) || date.equals(s_date)) {
                                int pos = 0;
                                for (int i = 0; i < repetitionLists.size(); i++) {
                                    if (repetitionLists.get(i).getId().equals("3")) {
                                        pos = i;
                                    }
                                }
                                repetition_type_id = "3";
                                repetition_type_name = repetitionLists.get(pos).getName();

                                for (int i = 0; i < repetitionLists.size(); i++) {
                                    repetitionLists.get(i).setClicked(false);
                                }

                                repetitionLists.get(pos).setClicked(true);

                                repetitionType.setText(repetition_type_name);
                                customLay.setVisibility(View.VISIBLE);
//                        String cd = "From  " + start_date + "  to  " + end_date;
                                customDataLists.get(selectedPosition).setBegin_date(start_date);
                                customDataLists.get(selectedPosition).setEnd_date(end_date);
                                customDataLists.get(selectedPosition).setBegin_time("");
                                customDataLists.get(selectedPosition).setEnd_time("");
                                customRepetitionAdapter.notifyDataSetChanged();

                                bottomSheetPicker.dismiss();
                                scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
                            } else {
                                Toast.makeText(AddGeoFences.this, "You can not select Date before today's date. Please Select Date again", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(AddGeoFences.this, "Invalid Date", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AddGeoFences.this, "Please Select Date Range", Toast.LENGTH_SHORT).show();
                    }
                } else if (!dateRangeSwitch.isChecked() && timeRangeSwitch.isChecked()) {
                    if (!start_time.isEmpty() && !end_time.isEmpty()) {

                        int pos = 0;
                        for (int i = 0; i < repetitionLists.size(); i++) {
                            if (repetitionLists.get(i).getId().equals("3")) {
                                pos = i;
                            }
                        }
                        repetition_type_id = "3";
                        repetition_type_name = repetitionLists.get(pos).getName();

                        for (int i = 0; i < repetitionLists.size(); i++) {
                            repetitionLists.get(i).setClicked(false);
                        }

                        repetitionLists.get(pos).setClicked(true);

                        repetitionType.setText(repetition_type_name);
                        customLay.setVisibility(View.VISIBLE);
//                        String cd = "From  " + start_time + "  to  " + end_time;
                        customDataLists.get(selectedPosition).setBegin_date("");
                        customDataLists.get(selectedPosition).setEnd_date("");
                        customDataLists.get(selectedPosition).setBegin_time(start_time);
                        customDataLists.get(selectedPosition).setEnd_time(end_time);
                        customRepetitionAdapter.notifyDataSetChanged();

                        bottomSheetPicker.dismiss();
                        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
                    } else {
                        Toast.makeText(AddGeoFences.this, "Please Select Time Range", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddGeoFences.this, "Please Select At least Date or Time Range", Toast.LENGTH_SHORT).show();
                }
            }
            System.out.println("DATA LENGTH: " + customDataLists.size());
        });

        rangeSetCancel.setOnClickListener(view -> bottomSheetPicker.dismiss());

        addMoreCustom.setOnClickListener(view -> {
            showPicker(-1);
            selectedPosition = -1;
        });

        addGeo.setOnClickListener(view -> {
            if (saveLatLng != null) {
                if (!event_name.isEmpty()) {
                    if (!event_type_id.isEmpty() && !event_type_id.equals("1")) {
                        if (!repetition_type_id.equals("3")) {
                            newEnableGps();
                        } else {
                            if (!customDataLists.isEmpty()) {
                                newEnableGps();
                            } else {
                                Toast.makeText(this, "Please Add Custom Date", Toast.LENGTH_SHORT).show();
                                repetitionTypeMissing.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        Toast.makeText(this, "Please Select Event Type", Toast.LENGTH_SHORT).show();
                        eventTypeMissing.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(this, "Please Write Event Name", Toast.LENGTH_SHORT).show();
                    eventNameMissing.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(this, "Please Select Location", Toast.LENGTH_SHORT).show();
                locationMissing.setVisibility(View.VISIBLE);
            }

        });

        updateGeo.setOnClickListener(view -> {
            if (saveLatLng != null) {
                if (!event_name.isEmpty()) {
                    if (!event_type_id.isEmpty() && !event_type_id.equals("1")) {
                        if (!repetition_type_id.equals("3")) {
                            newEnableUpGps();
                        } else {
                            if (!customDataLists.isEmpty()) {
                                boolean ff = false;
                                for (int i = 0; i < customDataLists.size(); i++) {
                                    Calendar calendar = Calendar.getInstance();
                                    Date to_date = calendar.getTime();
                                    SimpleDateFormat da = new SimpleDateFormat("dd MMM, yy", Locale.ENGLISH);
                                    String n_date = da.format(to_date);

                                    String b_date = customDataLists.get(i).getBegin_date();
                                    if (!b_date.isEmpty()) {
                                        Date date = null;
                                        Date s_date = null;
                                        try {
                                            date = da.parse(n_date);
                                            s_date = da.parse(b_date);
                                        } catch (ParseException e) {
                                            String er = e.getLocalizedMessage();
                                            System.out.println(er);
                                        }
                                        if (date != null && s_date != null) {
                                            if (s_date.before(date)) {
                                                ff = true;
                                                break;
                                            }
                                        }
                                    }
                                }

                                if (ff) {
                                    Toast.makeText(this, "Your Custom Date range has gone before today's date. Please select your Date again", Toast.LENGTH_LONG).show();
                                } else {
                                    newEnableUpGps();
                                }
                            } else {
                                Toast.makeText(this, "Please Add Custom Date", Toast.LENGTH_SHORT).show();
                                repetitionTypeMissing.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        Toast.makeText(this, "Please Select Event Type", Toast.LENGTH_SHORT).show();
                        eventTypeMissing.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(this, "Please Write Event Name", Toast.LENGTH_SHORT).show();
                    eventNameMissing.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(this, "Please Select Location", Toast.LENGTH_SHORT).show();
                locationMissing.setVisibility(View.VISIBLE);
            }

        });

        backButton.setOnClickListener(view -> {
            if (loading) {
                Toast.makeText(this, "Please wait while loading", Toast.LENGTH_SHORT).show();
            } else {
                finish();
            }
        });


        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (loading) {
                    Toast.makeText(getApplicationContext(), "Please wait while loading", Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                }
            }
        });

        getData();

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
    protected void onDestroy() {
        super.onDestroy();
        saveLocationListener = null;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        MapsInitializer.initialize(this);
        gMap = googleMap;
        gMap.getUiSettings().setMapToolbarEnabled(false);
        gMap.getUiSettings().setCompassEnabled(false);
        gMap.getUiSettings().setRotateGesturesEnabled(false);
        gMap.getUiSettings().setZoomGesturesEnabled(false);
        gMap.getUiSettings().setScrollGesturesEnabled(false);

        gMap.setOnMapClickListener(latLng -> {
            Intent intent = new Intent(AddGeoFences.this, LocationSelection.class);
            if (!save_radius.isEmpty()) {
                intent.putExtra("RAD", save_radius);
            }
            if (saveLatLng != null) {
                intent.putExtra("LAT", saveLatLng.latitude);
                intent.putExtra("LNG", saveLatLng.longitude);
            }
            startActivity(intent);
        });

        gMap.setOnMarkerClickListener(marker -> {
            Intent intent = new Intent(AddGeoFences.this, LocationSelection.class);
            if (!save_radius.isEmpty()) {
                intent.putExtra("RAD", save_radius);
            }
            if (saveLatLng != null) {
                intent.putExtra("LAT", saveLatLng.latitude);
                intent.putExtra("LNG", saveLatLng.longitude);
            }
            startActivity(intent);
            return false;
        });
    }

    @Override
    public void onSave(String rad, LatLng latLng) {
        saveLatLng = latLng;
        save_radius = rad;
        String text = "Radius :  " + save_radius + " Meters";
        savedRadius.setText(text);
        savedRadius.setVisibility(View.VISIBLE);

        gMap.clear();

        gMap.addMarker(new MarkerOptions().position(saveLatLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_micro_24_select)));

        double rr = Double.parseDouble(save_radius);
        if (rr <= 1000) {
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(saveLatLng, 13f));
        } else if (rr <= 2000) {
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(saveLatLng, 12f));
        } else if (rr <= 3000) {
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(saveLatLng, 11f));
        } else {
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(saveLatLng, 10f));
        }
        gMap.addCircle(new CircleOptions()
                .center(saveLatLng)
                .radius(rr)
                .strokeColor(getColor(R.color.belize_hole))
                .strokeWidth(4F)
                .fillColor(getColor(R.color.belize_hole_a)));

        getAddress(saveLatLng.latitude, saveLatLng.longitude);
        locationMissing.setVisibility(View.GONE);
    }

    public void getAddress(double lat, double lng) {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        new Thread(() -> {
            Geocoder geocoder = new Geocoder(AddGeoFences.this, Locale.ENGLISH);
            try {
                List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                if (Geocoder.isPresent()) {
                    assert addresses != null;
                    Address obj = addresses.get(0);
                    save_address = obj.getAddressLine(0);
                } else {
                    save_address = "";
                }
            } catch (IOException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
                save_address = "";
            }

            runOnUiThread(() -> {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                String text;
                if (save_address.isEmpty()) {
                    text = "No Address Found";
                } else {
                    text = "Address: " + save_address;
                }

                savedAddress.setText(text);
                savedAddress.setVisibility(View.VISIBLE);
            });

        }).start();
    }

    private void getData() {
        circularProgressIndicator.setVisibility(View.VISIBLE);
        fullLayout.setVisibility(View.GONE);
        conn = false;
        connected = false;
        loading = true;
        eventTypeLists = new ArrayList<>();

        FirebaseFirestore cloudDatabase = FirebaseFirestore.getInstance();
        cloudDatabase.collection(KEY_EVENT_TABLE_NAME)
                .orderBy(KEY_EVENT_TYPE_ID, Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    System.out.println("COMPLETED");
                    conn = true;
                    if (task.isSuccessful()) {
                        System.out.println("SUCCESS");
                        connected = true;
                        if (task.getResult() != null && !task.getResult().getDocuments().isEmpty()) {
                            List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();

                            SpinnerItemList spinnerItemList = null;
                            for (int i = 0; i < documentSnapshots.size(); i++) {
                                String type_name = documentSnapshots.get(i).getString(KEY_EVENT_TYPE_NAME);
                                String type_id = documentSnapshots.get(i).getString(KEY_EVENT_TYPE_ID);
                                if (type_id != null) {
                                    if (i == 0) {
                                        eventTypeLists.add(new SpinnerItemList(type_id, type_name, true, "event"));
                                    } else {
                                        if (type_id.equals("9")) {
                                            spinnerItemList = new SpinnerItemList(type_id, type_name, false, "event");
                                        } else {
                                            eventTypeLists.add(new SpinnerItemList(type_id, type_name, false, "event"));
                                        }
                                    }
                                }
                            }
                            if (spinnerItemList != null) {
                                eventTypeLists.add(spinnerItemList);
                            }
                        }
                        updateLayout();
                    } else {
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

    @SuppressLint("NotifyDataSetChanged")
    private void updateLayout() {
        loading = false;
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);

                conn = false;
                connected = false;

                if (geoProcessType.equals("1")) {
                    if (eventTypeLists.isEmpty()) {
                        eventTypeLay.setEnabled(false);
                        String text = "No Selectable Event Type Found";
                        eventType.setText(text);
                    } else {
                        for (int i = 0; i < eventTypeLists.size(); i++) {
                            if (eventTypeLists.get(i).isClicked()) {
                                event_type_name = eventTypeLists.get(i).getName();
                                event_type_id = eventTypeLists.get(i).getId();
                            }
                        }
                        eventType.setText(event_type_name);
                        eventTypeLay.setEnabled(true);
                    }

                    eventTypeMissing.setVisibility(View.GONE);
                } else if (geoProcessType.equals("2")) {
                    String text = "Radius :  " + save_radius + " Meters";
                    savedRadius.setText(text);
                    savedRadius.setVisibility(View.VISIBLE);

                    gMap.clear();

                    gMap.addMarker(new MarkerOptions().position(saveLatLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_micro_24_select)));

                    double rr = Double.parseDouble(save_radius);
                    if (rr <= 1000) {
                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(saveLatLng, 13f));
                    } else if (rr <= 2000) {
                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(saveLatLng, 12f));
                    } else if (rr <= 3000) {
                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(saveLatLng, 11f));
                    } else {
                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(saveLatLng, 10f));
                    }
                    gMap.addCircle(new CircleOptions()
                            .center(saveLatLng)
                            .radius(rr)
                            .strokeColor(getColor(R.color.belize_hole))
                            .strokeWidth(4F)
                            .fillColor(getColor(R.color.belize_hole_a)));

                    if (save_address.isEmpty()) {
                        text = "No Address Found";
                    } else {
                        text = "Address: " + save_address;
                    }

                    savedAddress.setText(text);
                    savedAddress.setVisibility(View.VISIBLE);
                    locationMissing.setVisibility(View.GONE);

                    eventName.setText(event_name);
                    eventNameMissing.setVisibility(View.GONE);

                    eventDesc.setText(event_desc);

                    if (eventTypeLists.isEmpty()) {
                        eventTypeLay.setEnabled(false);
                        String text1 = "No Selectable Event Type Found";
                        eventType.setText(text1);
                    } else {
                        for (int i = 0; i < eventTypeLists.size(); i++) {
                            eventTypeLists.get(i).setClicked(false);
                            if (eventTypeLists.get(i).getId().equals(event_type_id)) {
                                eventTypeLists.get(i).setClicked(true);
                            }
                        }
                        if (event_type_id.equals("9")) {
                            othersEventType.setText(event_type_name);
                        } else {
                            othersEventType.setText("");
                        }
                        eventType.setText(event_type_name);
                        eventTypeMissing.setVisibility(View.GONE);
                        eventTypeLay.setEnabled(true);
                    }

                    for (int i = 0; i < alertTypeLists.size(); i++) {
                        alertTypeLists.get(i).setClicked(false);
                        if (alertTypeLists.get(i).getId().equals(alert_type_id)) {
                            alertTypeLists.get(i).setClicked(true);
                        }
                    }

                    alertType.setText(alert_type_name);
                    alertTypeMissing.setVisibility(View.GONE);

                    for (int i = 0; i < triggeringTypeLists.size(); i++) {
                        triggeringTypeLists.get(i).setClicked(false);
                        if (triggeringTypeLists.get(i).getId().equals(triggering_type_id)) {
                            triggeringTypeLists.get(i).setClicked(true);
                        }
                    }

                    triggeringType.setText(triggering_type_name);
                    triggeringTypeMissing.setVisibility(View.GONE);

                    for (int i = 0; i < repetitionLists.size(); i++) {
                        repetitionLists.get(i).setClicked(false);
                        if (repetitionLists.get(i).getId().equals(repetition_type_id)) {
                            repetitionLists.get(i).setClicked(true);
                        }
                    }

                    repetitionType.setText(repetition_type_name);
                    repetitionTypeMissing.setVisibility(View.GONE);

                    if (repetition_type_id.equals("3")) {
                        customRepetitionAdapter.notifyDataSetChanged();
                        customLay.setVisibility(View.VISIBLE);
                        addMoreCustom.setVisibility(View.VISIBLE);
                        System.out.println(customDataLists.size());
                        for (int i = 0; i < customDataLists.size(); i++) {
                            System.out.println(customDataLists.get(i).getBegin_date());
                        }
                    } else {
                        customLay.setVisibility(View.GONE);
                    }
                }
            } else {
                alertMessage();
            }
        } else {
            alertMessage();
        }
    }

    public void alertMessage() {
        fullLayout.setVisibility(View.VISIBLE);
        circularProgressIndicator.setVisibility(View.GONE);
        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        } else {
            parsing_message = "Server problem or Internet not connected";
        }
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: " + parsing_message + ".\n" + "Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    getData();
                    dialog.dismiss();
                })
                .setNegativeButton("Exit", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void alertMessageAdd() {
        fullLayout.setVisibility(View.VISIBLE);
        circularProgressIndicator.setVisibility(View.GONE);
        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        } else {
            parsing_message = "Server problem or Internet not connected";
        }
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: " + parsing_message + ".\n" + "Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    newEnableGps();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onItemClicked(int position, String type) {
        switch (type) {
            case "event": {
                String id = eventTypeLists.get(position).getId();

                if (id.equals("9")) {
                    if (event_type_id.equals("9")) {
                        othersEventType.setText(event_type_name);
                    } else {
                        othersEventType.setText("");
                    }
                    othersBottomSheet.show();
                } else {
                    for (int i = 0; i < eventTypeLists.size(); i++) {
                        eventTypeLists.get(i).setClicked(false);
                    }

                    eventTypeLists.get(position).setClicked(true);
                    event_type_id = eventTypeLists.get(position).getId();
                    event_type_name = eventTypeLists.get(position).getName();

                    eventType.setText(event_type_name);
                }
                eventTypeMissing.setVisibility(View.GONE);
                break;
            }
            case "alert":
                for (int i = 0; i < alertTypeLists.size(); i++) {
                    alertTypeLists.get(i).setClicked(false);
                }

                alertTypeLists.get(position).setClicked(true);
                alert_type_id = alertTypeLists.get(position).getId();
                alert_type_name = alertTypeLists.get(position).getName();

                alertType.setText(alert_type_name);

                alertTypeMissing.setVisibility(View.GONE);
                break;
            case "trigger":
                for (int i = 0; i < triggeringTypeLists.size(); i++) {
                    triggeringTypeLists.get(i).setClicked(false);
                }

                triggeringTypeLists.get(position).setClicked(true);
                triggering_type_id = triggeringTypeLists.get(position).getId();
                triggering_type_name = triggeringTypeLists.get(position).getName();

                triggeringType.setText(triggering_type_name);

                triggeringTypeMissing.setVisibility(View.GONE);
                break;
            case "repetition": {
                String id = repetitionLists.get(position).getId();
                if (id.equals("3")) {
                    if (customDataLists.isEmpty()) {
                        showPicker(-1);
                        selectedPosition = -1;
                    } else {
                        int pos = 0;
                        for (int i = 0; i < repetitionLists.size(); i++) {
                            if (repetitionLists.get(i).getId().equals("3")) {
                                pos = i;
                            }
                        }
                        repetition_type_id = "3";
                        repetition_type_name = repetitionLists.get(pos).getName();

                        for (int i = 0; i < repetitionLists.size(); i++) {
                            repetitionLists.get(i).setClicked(false);
                        }

                        repetitionLists.get(pos).setClicked(true);

                        repetitionType.setText(repetition_type_name);
                        customLay.setVisibility(View.VISIBLE);
                    }
                } else {
                    for (int i = 0; i < repetitionLists.size(); i++) {
                        repetitionLists.get(i).setClicked(false);
                    }

                    repetitionLists.get(position).setClicked(true);
                    repetition_type_id = repetitionLists.get(position).getId();
                    repetition_type_name = repetitionLists.get(position).getName();

                    repetitionType.setText(repetition_type_name);

                    repetitionTypeMissing.setVisibility(View.GONE);
                    customLay.setVisibility(View.GONE);
                }

                break;
            }
        }

        bottomSheetAdapter.notifyDataSetChanged();
        bottomSheetDialog.dismiss();
    }

    public void showPicker(int pos) {
        if (pos == -1) {
            dateRangeEnabled = true;
            timeRangeEnabled = true;
            dateRangeSwitch.setChecked(true);
            timeRangeSwitch.setChecked(true);
            dateRangLayout.setEnabled(true);
            dateRangeText.setText("");
            timeRangeLayout.setVisibility(View.VISIBLE);
            start_date = "";
            end_date = "";
            start_time = "03:00 PM";
            end_time = "09:00 PM";

            timeRangePicker.setStartTime(new TimeRangePicker.Time(15, 0));
            timeRangePicker.setEndTimeMinutes(1260);


            System.out.println(timeRangePicker.getStartTime());
            System.out.println(timeRangePicker.getEndTime());
            System.out.println(timeRangePicker.getEndTime().getTotalMinutes());
            String sss = "Start Time\n" + start_time;
            startTimeText.setText(sss);
            String eee = "End Time\n" + end_time;
            endTimeText.setText(eee);

        } else {
            start_date = customDataLists.get(pos).getBegin_date();
            end_date = customDataLists.get(pos).getEnd_date();
            start_time = customDataLists.get(pos).getBegin_time();
            end_time = customDataLists.get(pos).getEnd_time();

            dateRangeEnabled = !start_date.isEmpty() || !end_date.isEmpty();
            timeRangeEnabled = !start_time.isEmpty() || !end_time.isEmpty();
            dateRangeSwitch.setChecked(dateRangeEnabled);
            timeRangeSwitch.setChecked(timeRangeEnabled);
            dateRangLayout.setEnabled(dateRangeEnabled);
            timeRangeLayout.setVisibility(timeRangeEnabled ? View.VISIBLE : View.GONE);

            if (dateRangeEnabled) {
                if (!start_date.isEmpty() && !end_date.isEmpty()) {
                    String dateRange = start_date + " --- " + end_date;
                    dateRangeText.setText(dateRange);
                } else {
                    dateRangeText.setText("");
                }
            } else {
                dateRangeText.setText("");
            }

            if (!start_time.isEmpty() && start_time.length() > 2) {
                int hour = Integer.parseInt(start_time.substring(0, 2));
                int minute = Integer.parseInt(start_time.substring(3, 5));
                String meridian = start_time.substring(6, 8);
                if (meridian.equals("AM")) {
                    timeRangePicker.setStartTime(new TimeRangePicker.Time(hour, minute));
                } else {
                    timeRangePicker.setStartTime(new TimeRangePicker.Time(hour + 12, minute));
                }
            } else {
                start_time = "03:00 PM";
                timeRangePicker.setStartTime(new TimeRangePicker.Time(15, 0));
            }

            if (!end_time.isEmpty() && end_time.length() > 2) {
                int hour = Integer.parseInt(end_time.substring(0, 2));
                int minute = Integer.parseInt(end_time.substring(3, 5));
                String meridian = end_time.substring(6, 8);
                int tm;
                if (meridian.equals("AM")) {
                    tm = 60 * hour;
                } else {
                    tm = 60 * (hour + 12);
                }
                tm = tm + minute;
                timeRangePicker.setEndTimeMinutes(tm);
            } else {
                end_time = "09:00 PM";
                timeRangePicker.setEndTimeMinutes(1260);
            }

            String sss = "Start Time\n" + start_time;
            startTimeText.setText(sss);
            String eee = "End Time\n" + end_time;
            endTimeText.setText(eee);

        }

        bottomSheetPicker.setCancelable(false);
        bottomSheetPicker.setCanceledOnTouchOutside(false);
        bottomSheetPicker.show();
    }

    @Override
    public void onItemClicked(int position) {
        showPicker(position);
        selectedPosition = position;
    }

    private void saveGeoFence() {
        circularProgressIndicator.setVisibility(View.VISIBLE);
        fullLayout.setVisibility(View.GONE);
        conn = false;
        connected = false;
        loading = true;

        FirebaseFirestore database = FirebaseFirestore.getInstance();

        DocumentReference docRef = database.collection(KEY_LOC_TABLE_NAME).document();

        String generatedId = docRef.getId();
        HashMap<String, Object> geoFences = new HashMap<>();

        geoFences.put(KEY_LOC_ID, generatedId);
        geoFences.put(KEY_LATITUDE, saveLatLng.latitude);
        geoFences.put(KEY_LONGITUDE, saveLatLng.longitude);
        geoFences.put(KEY_RADIUS, save_radius);
        geoFences.put(KEY_LOC_ADDRESS, save_address);
        geoFences.put(KEY_GEO_EVENT_NAME, event_name);
        geoFences.put(KEY_GEO_EVENT_NOTES, event_desc);
        geoFences.put(KEY_GEO_EVENT_TYPE_ID, event_type_id);
        geoFences.put(KEY_GEO_EVENT_TYPE_NAME, event_type_name);
        geoFences.put(KEY_GEO_ALERT_TYPE_ID, alert_type_id);
        geoFences.put(KEY_GEO_ALERT_TYPE_NAME, alert_type_name);
        geoFences.put(KEY_GEO_TRIGGER_TYPE_ID, triggering_type_id);
        geoFences.put(KEY_GEO_TRIGGER_TYPE_NAME, triggering_type_name);
        geoFences.put(KEY_GEO_REPEAT_ID, repetition_type_id);
        geoFences.put(KEY_GEO_REPEAT_NAME, repetition_type_name);
        geoFences.put(KEY_GEO_USER_NAME, userInfoLists.get(0).getP_phone());
        geoFences.put(KEY_GEO_IS_ACTIVE, true);
        geoFences.put(KEY_GEO_UPDATE_DATE, Calendar.getInstance().getTime());

        database.collection(KEY_LOC_TABLE_NAME)
                .add(geoFences)
                .addOnSuccessListener(documentReference -> {
                    if (repetition_type_id.equals("3")) {
                        checkToInsertCustomDate(generatedId, "1");
                    } else {
                        addGeoFence(generatedId);
                    }
                })
                .addOnFailureListener(e -> {
                    conn = false;
                    connected = false;
                    loading = false;
                    parsing_message = e.getLocalizedMessage();
                    alertMessageAdd();
                });


    }

    public void checkToInsertCustomDate(String id, String type) {
        boolean allUpdated = false;
        for (int x = 0; x < customDataLists.size(); x++) {
            allUpdated = customDataLists.get(x).isUpdated();
            if (!customDataLists.get(x).isUpdated()) {
                saveCustomDate(x, id);
                break;
            }
        }
        if (allUpdated) {
            conn = true;
            connected = true;
            if (type.equals("1")) {
                addGeoFence(id);
            } else {
                updateGeoFences(id);
            }

        }
    }

    private void saveCustomDate(int index, String id) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        HashMap<String, Object> customRepeatData = new HashMap<>();

        customRepeatData.put(KEY_GEO_ID_FOR_TIME, id);
        customRepeatData.put(KEY_GEO_REPEAT_BEGIN_DATE, customDataLists.get(index).getBegin_date());
        customRepeatData.put(KEY_GEO_REPEAT_END_DATE, customDataLists.get(index).getEnd_date());
        customRepeatData.put(KEY_GEO_REPEAT_BEGIN_TIME, customDataLists.get(index).getBegin_time());
        customRepeatData.put(KEY_GEO_REPEAT_END_TIME, customDataLists.get(index).getEnd_time());

        database.collection(KEY_GEO_TIME_TABLE_NAME)
                .add(customRepeatData)
                .addOnSuccessListener(documentReference -> {
                    customDataLists.get(index).setUpdated(true);
                    checkToInsertCustomDate(id, "1");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to insert. Retrying...", Toast.LENGTH_SHORT).show();
                    checkToInsertCustomDate(id, "1");
                });
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

        task.addOnSuccessListener(this, locationSettingsResponse -> saveGeoFence());

        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(AddGeoFences.this,
                            1990);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error.
                }
            }
        });
    }

    private void newEnableUpGps() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(1000)
                .setMaxUpdateDelayMillis(2000)
                .build();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, locationSettingsResponse -> updateGeoFence());

        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(AddGeoFences.this,
                            1999);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error.
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1990) {
            if (resultCode == Activity.RESULT_OK) {
                saveGeoFence();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
                alertDialogBuilder.setTitle("Warning!")
                        .setMessage("You need to Set your GPS/Location ON for adding Event")
                        .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());

                AlertDialog alert = alertDialogBuilder.create();

                alert.setCanceledOnTouchOutside(false);
                alert.show();
            }
        }
        else if (requestCode == 1999) {
            if (resultCode == Activity.RESULT_OK) {
                updateGeoFence();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
                alertDialogBuilder.setTitle("Warning!")
                        .setMessage("You need to Set your GPS/Location ON for updating Event")
                        .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());

                AlertDialog alert = alertDialogBuilder.create();

                alert.setCanceledOnTouchOutside(false);
                alert.show();
            }
        }
    }

    public void addGeoFence(String id) {
        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(this);
        ArrayList<Geofence> geofenceList = new ArrayList<>();

        Geofence geofence = new Geofence.Builder()
                .setCircularRegion(saveLatLng.latitude, saveLatLng.longitude, Float.parseFloat(save_radius))
                .setRequestId(id)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setLoiteringDelay(120000)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();

        geofenceList.add(geofence);

        GeofencingRequest geofencingRequest;
        try {
            geofencingRequest = new GeofencingRequest.Builder()
                    .addGeofences(geofenceList)
                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1001, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Event Added Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    System.out.println(e.getLocalizedMessage());
                    String errorMsg = getErrorString(e);
                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    System.out.println("HOI NAI");
                });
    }

    public void updateGeoFences(String id) {
        ArrayList<String> ids = new ArrayList<>();
        ids.add(id);

        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(this);
        ArrayList<Geofence> geofenceList = new ArrayList<>();

        geofencingClient.removeGeofences(ids)
                .addOnSuccessListener(unused -> {})
                .addOnFailureListener(e -> {
                    String errorMessage = getErrorString(e);
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                });

        Geofence geofence = new Geofence.Builder()
                .setCircularRegion(saveLatLng.latitude, saveLatLng.longitude, Float.parseFloat(save_radius))
                .setRequestId(id)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setLoiteringDelay(120000)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();

        geofenceList.add(geofence);

        GeofencingRequest geofencingRequest;
        try {
            geofencingRequest = new GeofencingRequest.Builder()
                    .addGeofences(geofenceList)
                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1001, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Event Updated Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    System.out.println(e.getLocalizedMessage());
                    String errorMsg = getErrorString(e);
                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    System.out.println("HOI NAI");
                });

    }

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

    private void updateGeoFence() {
        circularProgressIndicator.setVisibility(View.VISIBLE);
        fullLayout.setVisibility(View.GONE);
        conn = false;
        connected = false;
        loading = true;

        FirebaseFirestore database = FirebaseFirestore.getInstance();

        HashMap<String , Object> geoFences = new HashMap<>();
        geoFences.put(KEY_LATITUDE, saveLatLng.latitude);
        geoFences.put(KEY_LONGITUDE, saveLatLng.longitude);
        geoFences.put(KEY_RADIUS, save_radius);
        geoFences.put(KEY_LOC_ADDRESS, save_address);
        geoFences.put(KEY_GEO_EVENT_NAME, event_name);
        geoFences.put(KEY_GEO_EVENT_NOTES, event_desc);
        geoFences.put(KEY_GEO_EVENT_TYPE_ID, event_type_id);
        geoFences.put(KEY_GEO_EVENT_TYPE_NAME, event_type_name);
        geoFences.put(KEY_GEO_ALERT_TYPE_ID, alert_type_id);
        geoFences.put(KEY_GEO_ALERT_TYPE_NAME, alert_type_name);
        geoFences.put(KEY_GEO_TRIGGER_TYPE_ID, triggering_type_id);
        geoFences.put(KEY_GEO_TRIGGER_TYPE_NAME, triggering_type_name);
        geoFences.put(KEY_GEO_REPEAT_ID, repetition_type_id);
        geoFences.put(KEY_GEO_REPEAT_NAME, repetition_type_name);
        geoFences.put(KEY_GEO_USER_NAME, userInfoLists.get(0).getP_phone());
        geoFences.put(KEY_GEO_IS_ACTIVE, true);
        geoFences.put(KEY_GEO_UPDATE_DATE, Calendar.getInstance().getTime());

        database.collection(KEY_LOC_TABLE_NAME).whereEqualTo(KEY_LOC_ID,geo_id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().getDocuments().isEmpty()) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        database.collection(KEY_LOC_TABLE_NAME).document(documentSnapshot.getId()).update(geoFences)
                                .addOnSuccessListener(unused -> database.collection(KEY_GEO_TIME_TABLE_NAME).whereEqualTo(KEY_GEO_ID_FOR_TIME,geo_id)
                                        .get()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful() && task1.getResult() != null && !task1.getResult().getDocuments().isEmpty()) {
                                                List<DocumentSnapshot> documentSnapshots = task1.getResult().getDocuments();
                                                deleteLists = new ArrayList<>();
                                                for (int i = 0; i < documentSnapshots.size(); i++) {
                                                    deleteLists.add(new DocumentDeleteList(documentSnapshots.get(i).getId(),false));
                                                }
                                                checkToDeleteCustomTime();
                                            }
                                            else {
                                                if (repetition_type_id.equals("3")) {
                                                    checkToInsertCustomDate(geo_id,"2");
                                                }
                                                else {
                                                    updateGeoFences(geo_id);
                                                }
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            conn = false;
                                            connected = false;
                                            loading = false;
                                            parsing_message = e.getLocalizedMessage();
                                            alertMessageUpdate();
                                        }))
                                .addOnFailureListener(e -> {
                                    conn = false;
                                    connected = false;
                                    loading = false;
                                    parsing_message = e.getLocalizedMessage();
                                    alertMessageUpdate();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    conn = false;
                    connected = false;
                    loading = false;
                    parsing_message = e.getLocalizedMessage();
                    alertMessageUpdate();
                });
    }

    public void checkToDeleteCustomTime() {
        boolean allUpdated = false;
        for (int x = 0; x < deleteLists.size(); x++) {
            allUpdated = deleteLists.get(x).isDeleted();
            if (!deleteLists.get(x).isDeleted()) {
                deleteCustomDate(x, deleteLists.get(x).getDocumentId());
                break;
            }
        }
        if (allUpdated) {
            if (repetition_type_id.equals("3")) {
                checkToInsertCustomDate(geo_id,"2");
            }
            else {
                updateGeoFences(geo_id);
            }
        }
    }

    private void deleteCustomDate(int index, String id) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection(KEY_GEO_TIME_TABLE_NAME).document(id).delete()
                .addOnSuccessListener(unused1 -> {
                    deleteLists.get(index).setDeleted(true);
                    checkToDeleteCustomTime();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update. Retrying...", Toast.LENGTH_SHORT).show();
                    checkToDeleteCustomTime();
                });
    }

    public void alertMessageUpdate() {
        fullLayout.setVisibility(View.VISIBLE);
        circularProgressIndicator.setVisibility(View.GONE);
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
                    newEnableUpGps();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> dialog.dismiss());

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }
}