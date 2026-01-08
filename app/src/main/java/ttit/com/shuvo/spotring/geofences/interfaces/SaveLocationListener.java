package ttit.com.shuvo.spotring.geofences.interfaces;

import com.google.android.gms.maps.model.LatLng;

public interface SaveLocationListener {
    void onSave(String rad, LatLng latLng);
}
