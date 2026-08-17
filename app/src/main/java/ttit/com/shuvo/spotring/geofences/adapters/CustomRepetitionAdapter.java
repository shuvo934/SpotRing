package ttit.com.shuvo.spotring.geofences.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import ttit.com.shuvo.spotring.R;
import ttit.com.shuvo.spotring.geofences.model.CustomRepetitionDataList;

public class CustomRepetitionAdapter extends RecyclerView.Adapter<CustomRepetitionAdapter.CRAHolder> {
    private final ArrayList<CustomRepetitionDataList> mCategory;
    private final Context myContext;
    private final ClickedItem myClickedItem;

    public CustomRepetitionAdapter(ArrayList<CustomRepetitionDataList> mCategory, Context myContext, ClickedItem myClickedItem) {
        this.mCategory = mCategory;
        this.myContext = myContext;
        this.myClickedItem = myClickedItem;
    }

    @NonNull
    @Override
    public CRAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.custom_repetition_item_layout, parent, false);
        return new CRAHolder(view, myClickedItem);
    }

    @Override
    public void onBindViewHolder(@NonNull CRAHolder holder, int position) {
        CustomRepetitionDataList dataList = mCategory.get(position);
        String text;
        if (dataList.getBegin_date().isEmpty() && dataList.getEnd_date().isEmpty() && !dataList.getBegin_time().isEmpty() && !dataList.getEnd_time().isEmpty() && dataList.getWeeklyDays().isEmpty()) {
            if (dataList.getBegin_time().equals(dataList.getEnd_time())) {
                text = "At " + dataList.getBegin_time();
            }
            else {
                text = "From " + dataList.getBegin_time() + " to " + dataList.getEnd_time();
            }
        }
        else if (!dataList.getBegin_date().isEmpty() && !dataList.getEnd_date().isEmpty() && dataList.getBegin_time().isEmpty() && dataList.getEnd_time().isEmpty() && dataList.getWeeklyDays().isEmpty()) {
            if (dataList.getBegin_date().equals(dataList.getEnd_date())) {
                text = "On " + dataList.getBegin_date();
            }
            else {
                text = "From " + dataList.getBegin_date() + " to " + dataList.getEnd_date();
            }
        }
        else if (!dataList.getBegin_date().isEmpty() && !dataList.getEnd_date().isEmpty() && !dataList.getBegin_time().isEmpty() && !dataList.getEnd_time().isEmpty() && dataList.getWeeklyDays().isEmpty()) {
            if (dataList.getBegin_time().equals(dataList.getEnd_time())
                    && dataList.getBegin_date().equals(dataList.getEnd_date())) {
                text = "On " + dataList.getBegin_date() + " at " + dataList.getBegin_time();
            }
            else if (dataList.getBegin_time().equals(dataList.getEnd_time())) {
                text = "From " + dataList.getBegin_date() + " to " + dataList.getEnd_date() +
                        " at " + dataList.getBegin_time();
            }
            else if (dataList.getBegin_date().equals(dataList.getEnd_date())) {
                text = "On " + dataList.getBegin_date()+
                        " From " + dataList.getBegin_time() + " to " + dataList.getEnd_time();
            }
            else {
                text = "From " + dataList.getBegin_date() + " to " + dataList.getEnd_date() +
                        " at " + dataList.getBegin_time() + " to " + dataList.getEnd_time();
            }
        }
        else if (dataList.getBegin_date().isEmpty() && dataList.getEnd_date().isEmpty() && !dataList.getBegin_time().isEmpty() && !dataList.getEnd_time().isEmpty() && !dataList.getWeeklyDays().isEmpty()) {
            if (dataList.getBegin_time().equals(dataList.getEnd_time())) {
                text = "On " + dataList.getWeeklyDays() + " at " + dataList.getBegin_time();
            }
            else {
                text = "On " + dataList.getWeeklyDays() + " From " + dataList.getBegin_time() + " to " + dataList.getEnd_time();
            }
        }
        else if (dataList.getBegin_date().isEmpty() && dataList.getEnd_date().isEmpty() && dataList.getBegin_time().isEmpty() && dataList.getEnd_time().isEmpty() && !dataList.getWeeklyDays().isEmpty()) {
            text = "On " + dataList.getWeeklyDays();
        }
        else {
            text = "Invalid Date Range. Please select again";
        }
        holder.itemName.setText(text);
        if (position == 0) {
            holder.delete.setVisibility(View.GONE);
        }
        else {
            holder.delete.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mCategory != null ? mCategory.size() : 0;
    }

    public class CRAHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView itemCardView;
        MaterialButton delete;
        TextView itemName;
        ClickedItem mClickedItem;

        @SuppressLint("NotifyDataSetChanged")
        public CRAHolder(@NonNull View itemView, ClickedItem ci) {
            super(itemView);
            itemCardView = itemView.findViewById(R.id.custom_data_card_view);
            itemName = itemView.findViewById(R.id.custom_repeat_data);
            delete = itemView.findViewById(R.id.custom_repeat_delete);

            this.mClickedItem = ci;
            itemView.setOnClickListener(this);
            delete.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position == RecyclerView.NO_POSITION) {
                    return;
                }
                mCategory.remove(position);
                notifyDataSetChanged();
            });
        }

        @Override
        public void onClick(View view) {
            int position = getBindingAdapterPosition();
            if (position == RecyclerView.NO_POSITION) {
                return;
            }
            mClickedItem.onItemClicked(position);
        }
    }

    public interface ClickedItem {
        void onItemClicked(int position);
    }
}
