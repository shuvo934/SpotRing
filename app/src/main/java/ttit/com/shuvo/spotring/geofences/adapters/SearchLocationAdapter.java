package ttit.com.shuvo.spotring.geofences.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

import ttit.com.shuvo.spotring.R;
import ttit.com.shuvo.spotring.geofences.model.SearchLocationList;


public class SearchLocationAdapter extends RecyclerView.Adapter<SearchLocationAdapter.SLAHolder> {
    private final ArrayList<SearchLocationList> mCategory;
    private final Context myContext;
    private final ClickedItem myClickedItem;

    public SearchLocationAdapter(ArrayList<SearchLocationList> mCategory, Context myContext, ClickedItem myClickedItem) {
        this.mCategory = mCategory;
        this.myContext = myContext;
        this.myClickedItem = myClickedItem;
    }

    @NonNull
    @Override
    public SLAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.search_location_details_view, parent, false);
        return new SLAHolder(view, myClickedItem);
    }

    @Override
    public void onBindViewHolder(@NonNull SLAHolder holder, int position) {
        SearchLocationList locationList = mCategory.get(position);
        holder.locationName.setText(locationList.getName());
        holder.locationDetails.setText(locationList.getDisplay_name());
    }

    @Override
    public int getItemCount() {
        return mCategory != null ? mCategory.size() : 0;
    }

    public static class SLAHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView locationName;
        TextView locationDetails;
        ClickedItem mClickedItem;

        @SuppressLint("NotifyDataSetChanged")
        public SLAHolder(@NonNull View itemView, ClickedItem ci) {
            super(itemView);
            locationName = itemView.findViewById(R.id.display_location_name);
            locationDetails = itemView.findViewById(R.id.details_location_name);

            this.mClickedItem = ci;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getBindingAdapterPosition();
            if (position == RecyclerView.NO_POSITION) {
                return;
            }
            mClickedItem.onLocationClicked(position);
        }
    }

    public interface ClickedItem {
        void onLocationClicked(int position);
    }
}
