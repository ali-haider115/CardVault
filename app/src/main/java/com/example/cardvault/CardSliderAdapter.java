package com.example.cardvault;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cardvault.models.CardModel;

import java.io.File;
import java.util.List;

public class CardSliderAdapter extends RecyclerView.Adapter<CardSliderAdapter.CardViewHolder> {

    public interface OnCardClickListener {
        void onCardClick(CardModel card);
    }

    private final List<CardModel> cardList;
    private final OnCardClickListener listener;

    public CardSliderAdapter(List<CardModel> cardList, OnCardClickListener listener) {
        this.cardList = cardList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_slider, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        CardModel card = cardList.get(position);
        holder.tvCardName.setText(card.getName());
        holder.tvCardCategory.setText(card.getCategory());

        String imagePath = card.getFrontImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                if (bitmap != null) {
                    holder.ivCardFront.setImageBitmap(bitmap);
                } else {
                    holder.ivCardFront.setImageResource(R.drawable.ic_card_wallet);
                }
            } else {
                holder.ivCardFront.setImageResource(R.drawable.ic_card_wallet);
            }
        } else {
            holder.ivCardFront.setImageResource(R.drawable.ic_card_wallet);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCardClick(card);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardList != null ? cardList.size() : 0;
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCardFront;
        TextView tvCardName;
        TextView tvCardCategory;

        CardViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCardFront = itemView.findViewById(R.id.iv_card_front);
            tvCardName = itemView.findViewById(R.id.tv_card_name);
            tvCardCategory = itemView.findViewById(R.id.tv_card_category);
        }
    }
}