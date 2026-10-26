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
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity {

    private ChipGroup chipGroupCategories;
    private RecyclerView rvCategoryCards;
    private LinearLayout containerEmptyCategory;
    private TextView tvEmptyCategoryMsg;

    private CardStorageManager cardStorageManager;
    private String selectedCategory = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        cardStorageManager = new CardStorageManager(this);

        ImageView btnBack = findViewById(R.id.btn_categories_back);
        chipGroupCategories = findViewById(R.id.chip_group_categories);
        rvCategoryCards = findViewById(R.id.rv_category_cards);
        containerEmptyCategory = findViewById(R.id.container_empty_category);
        tvEmptyCategoryMsg = findViewById(R.id.tv_empty_category_msg);

        if (rvCategoryCards != null) {
            rvCategoryCards.setLayoutManager(new LinearLayoutManager(this));
        }

        btnBack.setOnClickListener(v -> finish());

        if (chipGroupCategories != null) {
            chipGroupCategories.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (checkedIds != null && !checkedIds.isEmpty()) {
                    int checkedId = checkedIds.get(0);
                    Chip chip = findViewById(checkedId);
                    if (chip != null) {
                        selectedCategory = chip.getText().toString();
                        filterAndDisplayCards();
                    }
                } else {
                    selectedCategory = "All";
                    filterAndDisplayCards();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        filterAndDisplayCards();
    }

    private void filterAndDisplayCards() {
        List<CardModel> allCards = cardStorageManager.getAllCards();
        List<CardModel> filteredList = new ArrayList<>();

        if (allCards != null) {
            for (CardModel card : allCards) {
                if (selectedCategory == null || selectedCategory.equalsIgnoreCase("All") || selectedCategory.equalsIgnoreCase(card.getCategory())) {
                    filteredList.add(card);
                }
            }
        }

        if (filteredList.isEmpty()) {
            rvCategoryCards.setVisibility(View.GONE);
            containerEmptyCategory.setVisibility(View.VISIBLE);
            if (tvEmptyCategoryMsg != null) {
                tvEmptyCategoryMsg.setText("No cards found under " + selectedCategory + " category.");
            }
        } else {
            containerEmptyCategory.setVisibility(View.GONE);
            rvCategoryCards.setVisibility(View.VISIBLE);

            CardSliderAdapter adapter = new CardSliderAdapter(filteredList, card -> {
                Intent intent = new Intent(CategoriesActivity.this, CardDetailsActivity.class);
                intent.putExtra(CardDetailsActivity.EXTRA_CARD_ID, card.getId());
                startActivity(intent);
            });
            rvCategoryCards.setAdapter(adapter);
        }
    }
}