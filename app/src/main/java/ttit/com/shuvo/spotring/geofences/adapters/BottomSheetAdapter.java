package ttit.com.shuvo.spotring.geofences.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.spotring.R;
import ttit.com.shuvo.spotring.geofences.model.SpinnerItemList;

public class BottomSheetAdapter extends RecyclerView.Adapter<BottomSheetAdapter.BSAHolder> {
    private final ArrayList<SpinnerItemList> mCategory;
    private final Context myContext;
    private final ClickedItem myClickedItem;
    private final String type;

    public BottomSheetAdapter(ArrayList<SpinnerItemList> mCategory, Context myContext, ClickedItem myClickedItem, String type) {
        this.mCategory = mCategory;
        this.myContext = myContext;
        this.myClickedItem = myClickedItem;
        this.type = type;
    }

    @NonNull
    @Override
    public BSAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.bottom_sheet_dropdown_item, parent, false);
        return new BSAHolder(view, myClickedItem);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull BSAHolder holder, int position) {
        SpinnerItemList itemList = mCategory.get(position);
        holder.itemName.setText(itemList.getName());

        if (itemList.isClicked()) {
            holder.itemCardView.setCardBackgroundColor(myContext.getColor(R.color.belize_hole));
            holder.itemLayout.setVisibility(View.VISIBLE);
            holder.borderLay.setBackground(myContext.getDrawable(R.drawable.bottom_sheet_border_seleted));
        }
        else {
            holder.itemCardView.setCardBackgroundColor(myContext.getColor(R.color.white));
            holder.itemLayout.setVisibility(View.GONE);
            holder.borderLay.setBackground(myContext.getDrawable(R.drawable.bottom_sheet_border_not_selected));
        }
    }

    @Override
    public int getItemCount() {
        return mCategory != null ? mCategory.size() : 0;
    }

    public class BSAHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView itemCardView;
        RelativeLayout borderLay;
        ImageView itemLayout;
        TextView itemName;
        ClickedItem mClickedItem;

        public BSAHolder(@NonNull View itemView, ClickedItem ci) {
            super(itemView);
            itemCardView = itemView.findViewById(R.id.bottom_sheet_drop_down_card_view);
            itemLayout = itemView.findViewById(R.id.drop_down_item_layout_in_bottom_sheet);
            itemName = itemView.findViewById(R.id.drop_down_item_in_bottom_sheet);
            borderLay = itemView.findViewById(R.id.bottom_sheet_border_layout);

            this.mClickedItem = ci;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mClickedItem.onItemClicked(getAdapterPosition(), type);
        }
    }

    public interface ClickedItem {
        void onItemClicked(int position,String type);
    }
}
