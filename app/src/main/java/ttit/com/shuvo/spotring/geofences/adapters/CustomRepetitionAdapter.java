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
        CustomRepetitionDataList customRepetitionDataList = mCategory.get(position);
        String text;
        if (customRepetitionDataList.getBegin_date().isEmpty() || customRepetitionDataList.getEnd_date().isEmpty()) {
            if (customRepetitionDataList.getBegin_time().equals(customRepetitionDataList.getEnd_time())) {
                text = "At " + customRepetitionDataList.getBegin_time();
            }
            else {
                text = "From " + customRepetitionDataList.getBegin_time() + " to " + customRepetitionDataList.getEnd_time();
            }
        }
        else if (customRepetitionDataList.getBegin_time().isEmpty() || customRepetitionDataList.getEnd_time().isEmpty()) {
            if (customRepetitionDataList.getBegin_date().equals(customRepetitionDataList.getEnd_date())) {
                text = "At " + customRepetitionDataList.getBegin_date();
            }
            else {
                text = "From " + customRepetitionDataList.getBegin_date() + " to " + customRepetitionDataList.getEnd_date();
            }
        }
        else {
            if (customRepetitionDataList.getBegin_time().equals(customRepetitionDataList.getEnd_time())
                    && customRepetitionDataList.getBegin_date().equals(customRepetitionDataList.getEnd_date())) {
                text = "At " + customRepetitionDataList.getBegin_date() + " in " + customRepetitionDataList.getBegin_time();
            }
            else if (customRepetitionDataList.getBegin_time().equals(customRepetitionDataList.getEnd_time())) {
                text = "From " + customRepetitionDataList.getBegin_date() + " to " + customRepetitionDataList.getEnd_date() +
                        " at " + customRepetitionDataList.getBegin_time();
            }
            else if (customRepetitionDataList.getBegin_date().equals(customRepetitionDataList.getEnd_date())) {
                text = "At " + customRepetitionDataList.getBegin_date()+
                        " From " + customRepetitionDataList.getBegin_time() + " to " + customRepetitionDataList.getEnd_time();
            }
            else {
                text = "From " + customRepetitionDataList.getBegin_date() + " to " + customRepetitionDataList.getEnd_date() +
                        " at " + customRepetitionDataList.getBegin_time() + " to " + customRepetitionDataList.getEnd_time();
            }
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
                mCategory.remove(getAdapterPosition());
                notifyDataSetChanged();
            });
        }

        @Override
        public void onClick(View view) {
            mClickedItem.onItemClicked(getAdapterPosition());
        }
    }

    public interface ClickedItem {
        void onItemClicked(int position);
    }
}
