package ttit.com.shuvo.spotring.dashboard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import ttit.com.shuvo.spotring.R;
import ttit.com.shuvo.spotring.dashboard.model.SavedLocationList;

public class SavedLocationAdapter extends RecyclerView.Adapter<SavedLocationAdapter.SLAHolder> {

    private final ArrayList<SavedLocationList> mCategoryItem;
    private final Context myContext;
    private final ClickedMap clickedMap;
    private final ClickedEvent clickedEvent;
    private final ClickedSwitch clickedSwitch;
    private final ClickedDelete clickedDelete;

    public SavedLocationAdapter(ArrayList<SavedLocationList> mCategoryItem, Context myContext, ClickedMap clickedMap, ClickedEvent clickedEvent, ClickedSwitch clickedSwitch, ClickedDelete clickedDelete) {
        this.mCategoryItem = mCategoryItem;
        this.myContext = myContext;
        this.clickedMap = clickedMap;
        this.clickedEvent = clickedEvent;
        this.clickedSwitch = clickedSwitch;
        this.clickedDelete = clickedDelete;
    }

    @NonNull
    @Override
    public SLAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.saved_location_details_view, parent, false);
        return new SLAHolder(view, clickedMap, clickedEvent, clickedSwitch, clickedDelete);
    }

    @Override
    public void onBindViewHolder(@NonNull SLAHolder holder, int position) {
        holder.bindView(position);
    }

    @Override
    public int getItemCount() {
        return mCategoryItem != null ? mCategoryItem.size() : 0;
    }

    public class SLAHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback, GoogleMap.OnMapClickListener,
            GoogleMap.OnMarkerClickListener, View.OnClickListener {
        public TextView locDesc;
        public TextView locAddress;
        public TextView locName;
        public TextView eventType;
        public TextView alertType;
        public TextView alertWhen;
        public TextView repeatType;
        public SwitchCompat geoSwitch;
        public ImageButton deleteFence;

        LinearLayout eventLayout;
        public MapView mapView;
        public GoogleMap map;
        public View layout;

        ClickedMap clickedMap;
        ClickedEvent clickedEvent;
        ClickedSwitch clickedSwitch;
        ClickedDelete clickedDelete;

        public SLAHolder(@NonNull View itemView, ClickedMap cm, ClickedEvent ce, ClickedSwitch cs, ClickedDelete cd) {
            super(itemView);
            layout = itemView;
            mapView = layout.findViewById(R.id.map_image);

            if (mapView != null) {
                // Initialise the MapView
                mapView.onCreate(null);
                // Set the map ready callback to receive the GoogleMap object
                mapView.getMapAsync(this);
            }

            locName = layout.findViewById(R.id.name_of_saved_location);
            locAddress = layout.findViewById(R.id.geo_address_of_location);
            locDesc = layout.findViewById(R.id.description_of_location);
            eventType = layout.findViewById(R.id.name_of_event_location);
            alertType = layout.findViewById(R.id.alert_type_location);
            alertWhen = layout.findViewById(R.id.when_need_to_alert_location);
            repeatType = layout.findViewById(R.id.repeat_type_location);
            geoSwitch = layout.findViewById(R.id.geo_activation_switch);
            deleteFence = layout.findViewById(R.id.delete_fence_button);
            eventLayout = layout.findViewById(R.id.event_click_listen_layout);
            this.clickedMap = cm;
            this.clickedEvent = ce;
            this.clickedSwitch = cs;
            this.clickedDelete = cd;
            eventLayout.setOnClickListener(this);
            geoSwitch.setOnClickListener(this::onSwClick);
            deleteFence.setOnClickListener(this::onDeleteClick);
        }

        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            MapsInitializer.initialize(myContext);
            map = googleMap;
            map.getUiSettings().setMapToolbarEnabled(false);
            map.getUiSettings().setCompassEnabled(false);
            map.getUiSettings().setRotateGesturesEnabled(false);
            map.getUiSettings().setZoomGesturesEnabled(false);
            map.getUiSettings().setScrollGesturesEnabled(false);
            setMapLocation();
            map.setOnMapClickListener(this);
            map.setOnMarkerClickListener(this);
        }

        private void setMapLocation() {
            if (map == null) return;

            SavedLocationList data = (SavedLocationList) mapView.getTag();
            if (data == null) return;

            LatLng location = new LatLng(Double.parseDouble(data.getLat()),Double.parseDouble(data.getLng()));

            // Add a marker for this item and set the camera
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13f));
            map.addMarker(new MarkerOptions().position(location));

            // Set the map type back to normal.
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }

        private void bindView(int pos) {
            SavedLocationList item = mCategoryItem.get(pos);
            // Store a reference of the ViewHolder object in the layout.
            layout.setTag(this);
            // Store a reference to the item in the mapView's tag. We use it to get the
            // coordinate of a location, when setting the map location.
            mapView.setTag(item);
            setMapLocation();

            locName.setText(item.getEvent_name());
            locAddress.setText(item.getEvent_address());
            locDesc.setText(item.getNotes());
            eventType.setText(item.getEvent_type_name());
            alertType.setText(item.getAlert_type());
            alertWhen.setText(item.getAlert_when());
            repeatType.setText(item.getRepeat_type());
            geoSwitch.setChecked(item.isActive());
        }

        @Override
        public void onMapClick(@NonNull LatLng latLng) {
            clickedMap.onMapClicked(mCategoryItem.get(getAdapterPosition()).getLat(), mCategoryItem.get(getAdapterPosition()).getLng());
        }

        @Override
        public boolean onMarkerClick(@NonNull Marker marker) {
            clickedMap.onMapClicked(mCategoryItem.get(getAdapterPosition()).getLat(), mCategoryItem.get(getAdapterPosition()).getLng());
            return false;
        }

        @Override
        public void onClick(View view) {
            clickedEvent.onEventClicked(getAdapterPosition());
        }

        public void onSwClick(View view) {
            clickedSwitch.onSwitchClicked(getAdapterPosition(),geoSwitch.isChecked(), geoSwitch);
        }

        public void onDeleteClick(View view) {
            clickedDelete.onDeleteClicked(getAdapterPosition());
        }
    }

    public interface ClickedMap {
        void onMapClicked(String lat, String lng);
    }

    public interface ClickedEvent {
        void onEventClicked(int position);
    }

    public interface ClickedSwitch {
        void onSwitchClicked(int position, boolean isChecked, SwitchCompat g_switch);
    }

    public interface ClickedDelete {
        void onDeleteClicked(int position);
    }
}
