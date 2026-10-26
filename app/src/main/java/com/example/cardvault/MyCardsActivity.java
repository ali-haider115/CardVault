package com.example.cardvault;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cardvault.models.CardModel;
import com.example.cardvault.utils.CardStorageManager;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class MyCardsActivity extends AppCompatActivity {

    private RecyclerView rvMyCardsList;
    private LinearLayout containerEmptyMyCards;
    private CardStorageManager cardStorageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cards);

        cardStorageManager = new CardStorageManager(this);

        ImageView btnBack = findViewById(R.id.btn_my_cards_back);
        rvMyCardsList = findViewById(R.id.rv_my_cards_list);
        containerEmptyMyCards = findViewById(R.id.container_empty_my_cards);
        MaterialButton btnAddFirstCard = findViewById(R.id.btn_add_first_card);

        if (rvMyCardsList != null) {
            rvMyCardsList.setLayoutManager(new LinearLayoutManager(this));
        }

        btnBack.setOnClickListener(v -> finish());
        btnAddFirstCard.setOnClickListener(v -> {
            Intent intent = new Intent(MyCardsActivity.this, AddCardActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSavedCards();
    }

    private void loadSavedCards() {
        List<CardModel> cards = cardStorageManager.getAllCards();

        if (cards == null || cards.isEmpty()) {
            rvMyCardsList.setVisibility(View.GONE);
            containerEmptyMyCards.setVisibility(View.VISIBLE);
        } else {
            containerEmptyMyCards.setVisibility(View.GONE);
            rvMyCardsList.setVisibility(View.VISIBLE);

            CardSliderAdapter adapter = new CardSliderAdapter(cards, card -> {
                Intent intent = new Intent(MyCardsActivity.this, CardDetailsActivity.class);
                intent.putExtra(CardDetailsActivity.EXTRA_CARD_ID, card.getId());
                startActivity(intent);
            });
            rvMyCardsList.setAdapter(adapter);
        }
    }
}