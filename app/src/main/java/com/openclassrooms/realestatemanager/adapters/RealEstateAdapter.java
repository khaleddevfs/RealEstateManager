package com.openclassrooms.realestatemanager.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.databinding.RealEstateListItemBinding;
import com.openclassrooms.realestatemanager.fragments.ListFragment;
import com.openclassrooms.realestatemanager.models.RealEstate;

import java.util.function.Consumer;
import java.util.List;

public class RealEstateAdapter  extends RecyclerView.Adapter<RealEstateViewHolder> {

    private final List<RealEstate> realEstateList;

    private final ListFragment listFragment;
    //private final Consumer<Integer> onRealEstateClickListener;


    private int selectedPosition = -1;

    Context mContext;

    public RealEstateAdapter(List<RealEstate> realEstateList, ListFragment listFragment)
    {
        this.realEstateList = realEstateList;


        this.listFragment = listFragment;
    }
    @NonNull
    @Override
    public RealEstateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        com.openclassrooms.realestatemanager.databinding.RealEstateListItemBinding realEstateListItemBinding = RealEstateListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        mContext = parent.getContext();
        return new RealEstateViewHolder(realEstateListItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RealEstateViewHolder holder, int position) {
        Log.d("TAG", "onBindViewHolder: " + realEstateList.get(position).getName());
        holder.getRealEstateName().setText(realEstateList.get(position).getName());
        holder.getRealEstateRegion().setText(realEstateList.get(position).getRegion());
        holder.getRealEstatePrice().setText(mContext.getString(R.string.price,realEstateList.get(position).getPrice()));

        holder.itemView.setSelected(selectedPosition == position);
        holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.item_background_selector));

        Glide.with(mContext)
                .load(realEstateList.get(position).getFeaturedMediaUrl())
                .into(holder.getRealEstateImageView());

        holder.itemView.setOnClickListener(v -> {
            notifyItemChanged(selectedPosition);
            selectedPosition = holder.getAdapterPosition();
          //  onRealEstateClickListener.accept(holder.getAdapterPosition());
            notifyItemChanged(selectedPosition);
        });
    }


    @Override
    public int getItemCount() {
        return realEstateList.size();
    }

}
