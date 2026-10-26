package com.example.cardvault;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cardvault.models.CardModel;
import com.example.cardvault.models.UserModel;
import com.example.cardvault.utils.CardStorageManager;
import com.example.cardvault.utils.SessionManager;
import com.example.cardvault.utils.UserStorageManager;

import java.io.File;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvWelcomeTitle;
    private TextView tvWelcomeSubtitle;
    private TextView tvEmptyCardsHint;
    private RecyclerView rvCardSlider;
    private ImageView ivProfileIcon;

    private SessionManager sessionManager;
    private UserStorageManager userStorageManager;
    private CardStorageManager cardStorageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sessionManager = new SessionManager(this);
        userStorageManager = new UserStorageManager(this);
        cardStorageManager = new CardStorageManager(this);

        tvWelcomeTitle = findViewById(R.id.tv_welcome_title);
        tvWelcomeSubtitle = findViewById(R.id.tv_welcome_subtitle);
        tvEmptyCardsHint = findViewById(R.id.tv_empty_cards_hint);
        rvCardSlider = findViewById(R.id.rv_card_slider);
        ivProfileIcon = findViewById(R.id.iv_dashboard_profile_icon);

        if (rvCardSlider != null) {
            rvCardSlider.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        }

        setupGridClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDashboardHeader();
        loadSavedCards();
    }

    private void updateDashboardHeader() {
        String email = sessionManager.getUserEmail();
        UserModel user = userStorageManager.getUserByEmailOrUsername(email);

        String displayName = sessionManager.getFullName();
        if (user != null && user.getFullName() != null && !user.getFullName().isEmpty()) {
            displayName = user.getFullName();
        } else if (sessionManager.getUsername() != null && !sessionManager.getUsername().isEmpty()) {
            displayName = sessionManager.getUsername();
        }

        if (tvWelcomeTitle != null) {
            tvWelcomeTitle.setText("Welcome back, " + displayName + "!");
        }

        String profilePicPath = sessionManager.getProfilePicPath();
        if (profilePicPath != null && !profilePicPath.isEmpty() && ivProfileIcon != null) {
            File picFile = new File(profilePicPath);
            if (picFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(picFile.getAbsolutePath());
                if (bitmap != null) {
                    ivProfileIcon.setImageBitmap(bitmap);
                    return;
                }
            }
        }
        if (ivProfileIcon != null) {
            ivProfileIcon.setImageResource(R.drawable.ic_profile);
        }
    }

    private void loadSavedCards() {
        if (cardStorageManager == null || rvCardSlider == null) return;

        List<CardModel> cards = cardStorageManager.getAllCards();
        int cardCount = cards != null ? cards.size() : 0;

        if (tvWelcomeSubtitle != null) {
            if (cardCount == 0) {
                tvWelcomeSubtitle.setText(R.string.dashboard_subtitle);
            } else {
                tvWelcomeSubtitle.setText("You have " + cardCount + " saved " + (cardCount == 1 ? "card" : "cards") + " in your vault.");
            }
        }

        if (cards == null || cards.isEmpty()) {
            rvCardSlider.setVisibility(View.GONE);
            if (tvEmptyCardsHint != null) {
                tvEmptyCardsHint.setVisibility(View.VISIBLE);
            }
        } else {
            if (tvEmptyCardsHint != null) {
                tvEmptyCardsHint.setVisibility(View.GONE);
            }
            rvCardSlider.setVisibility(View.VISIBLE);

            CardSliderAdapter adapter = new CardSliderAdapter(cards, card -> {
                Intent intent = new Intent(DashboardActivity.this, CardDetailsActivity.class);
                intent.putExtra(CardDetailsActivity.EXTRA_CARD_ID, card.getId());
                startActivity(intent);
            });
            rvCardSlider.setAdapter(adapter);
        }
    }

    private void setupGridClickListeners() {
        LinearLayout btnMyCards = findViewById(R.id.btn_grid_my_cards);
        LinearLayout btnAddCard = findViewById(R.id.btn_grid_add_card);
        LinearLayout btnCategories = findViewById(R.id.btn_grid_categories);
        LinearLayout btnProfile = findViewById(R.id.btn_grid_profile);
        LinearLayout btnSecurity = findViewById(R.id.btn_grid_security);
        LinearLayout btnSettings = findViewById(R.id.btn_grid_settings);

        if (btnMyCards != null) {
            btnMyCards.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardActivity.this, MyCardsActivity.class);
                startActivity(intent);
            });
        }

        if (btnAddCard != null) {
            btnAddCard.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardActivity.this, AddCardActivity.class);
                startActivity(intent);
            });
        }

        if (btnCategories != null) {
            btnCategories.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardActivity.this, CategoriesActivity.class);
                startActivity(intent);
            });
        }

        if (btnProfile != null) {
            btnProfile.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
                startActivity(intent);
            });
        }

        if (ivProfileIcon != null) {
            ivProfileIcon.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
                startActivity(intent);
            });
        }

        if (btnSecurity != null) {
            btnSecurity.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
                startActivity(intent);
            });
        }

        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
                startActivity(intent);
            });
        }
    }
}