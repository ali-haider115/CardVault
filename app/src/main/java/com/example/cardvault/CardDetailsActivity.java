package com.example.cardvault;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cardvault.models.CardModel;
import com.example.cardvault.utils.CardStorageManager;
import com.google.android.material.button.MaterialButton;

import java.io.File;

public class CardDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_CARD_ID = "EXTRA_CARD_ID";

    private TextView tvDetailCardName;
    private TextView tvDetailCardCategory;

    private ImageView ivDetailFrontImage;
    private TextView tvFrontErrorMsg;

    private ImageView ivDetailBackImage;
    private TextView tvBackErrorMsg;

    private CardStorageManager cardStorageManager;
    private String cardId;
    private CardModel currentCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);

        cardStorageManager = new CardStorageManager(this);

        ImageView btnBack = findViewById(R.id.btn_back);
        tvDetailCardName = findViewById(R.id.tv_detail_card_name);
        tvDetailCardCategory = findViewById(R.id.tv_detail_card_category);

        ivDetailFrontImage = findViewById(R.id.iv_detail_front_image);
        tvFrontErrorMsg = findViewById(R.id.tv_front_error_msg);

        ivDetailBackImage = findViewById(R.id.iv_detail_back_image);
        tvBackErrorMsg = findViewById(R.id.tv_back_error_msg);

        MaterialButton btnEditCard = findViewById(R.id.btn_edit_card);
        MaterialButton btnDeleteCard = findViewById(R.id.btn_delete_card);

        if (getIntent() != null && getIntent().hasExtra(EXTRA_CARD_ID)) {
            cardId = getIntent().getStringExtra(EXTRA_CARD_ID);
        }

        if (cardId == null || cardId.isEmpty()) {
            Toast.makeText(this, getString(R.string.msg_card_not_found), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnBack.setOnClickListener(v -> finish());
        btnEditCard.setOnClickListener(v -> attemptEditCard());
        btnDeleteCard.setOnClickListener(v -> showDeleteConfirmationDialog());

        ivDetailFrontImage.setOnClickListener(v -> {
            if (currentCard != null && currentCard.getFrontImagePath() != null) {
                openFullScreenImage(currentCard.getFrontImagePath(), getString(R.string.label_front_side));
            }
        });

        ivDetailBackImage.setOnClickListener(v -> {
            if (currentCard != null && currentCard.getBackImagePath() != null) {
                openFullScreenImage(currentCard.getBackImagePath(), getString(R.string.label_back_side));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCardDetails();
    }

    private void loadCardDetails() {
        currentCard = cardStorageManager.getCardById(cardId);
        if (currentCard == null) {
            Toast.makeText(this, getString(R.string.msg_card_not_found), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvDetailCardName.setText(currentCard.getName());
        tvDetailCardCategory.setText(currentCard.getCategory());

        loadSideImage(currentCard.getFrontImagePath(), ivDetailFrontImage, tvFrontErrorMsg);
        loadSideImage(currentCard.getBackImagePath(), ivDetailBackImage, tvBackErrorMsg);
    }

    private void loadSideImage(String imagePath, ImageView imageView, TextView errorTextView) {
        if (imagePath != null && !imagePath.isEmpty()) {
            File file = new File(imagePath);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                    if (errorTextView != null) errorTextView.setVisibility(View.GONE);
                    return;
                }
            }
        }
        imageView.setImageResource(R.drawable.ic_card_wallet);
        if (errorTextView != null) {
            errorTextView.setVisibility(View.VISIBLE);
        }
    }

    private void openFullScreenImage(String imagePath, String title) {
        Intent intent = new Intent(CardDetailsActivity.this, FullScreenImageActivity.class);
        intent.putExtra(FullScreenImageActivity.EXTRA_IMAGE_PATH, imagePath);
        intent.putExtra(FullScreenImageActivity.EXTRA_TITLE, title);
        startActivity(intent);
    }

    private void attemptEditCard() {
        Intent intent = new Intent(CardDetailsActivity.this, EditCardActivity.class);
        intent.putExtra(EXTRA_CARD_ID, cardId);
        startActivity(intent);
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(R.string.dialog_delete_message)
                .setPositiveButton(R.string.dialog_confirm_delete, (dialog, which) -> {
                    boolean deleted = cardStorageManager.deleteCard(cardId);
                    if (deleted) {
                        Toast.makeText(CardDetailsActivity.this, R.string.msg_card_deleted_success, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(CardDetailsActivity.this, "Failed to delete card.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, null)
                .show();
    }
}