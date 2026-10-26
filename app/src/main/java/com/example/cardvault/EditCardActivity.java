package com.example.cardvault;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cardvault.models.CardModel;
import com.example.cardvault.utils.CardStorageManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;

public class EditCardActivity extends AppCompatActivity {

    private TextInputLayout tilEditCardName;
    private TextInputEditText etEditCardName;
    private Spinner spinnerEditCategory;

    private ImageView ivEditFrontPreview;
    private ImageView ivEditBackPreview;

    private Uri newFrontUri;
    private Uri newBackUri;

    private CardStorageManager cardStorageManager;
    private String cardId;
    private CardModel existingCard;

    private final ActivityResultLauncher<String> frontPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    newFrontUri = uri;
                    ivEditFrontPreview.setImageURI(uri);
                }
            });

    private final ActivityResultLauncher<String> backPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    newBackUri = uri;
                    ivEditBackPreview.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_card);

        cardStorageManager = new CardStorageManager(this);

        tilEditCardName = findViewById(R.id.til_edit_card_name);
        etEditCardName = findViewById(R.id.et_edit_card_name);
        spinnerEditCategory = findViewById(R.id.spinner_edit_category);

        ivEditFrontPreview = findViewById(R.id.iv_edit_front_preview);
        ivEditBackPreview = findViewById(R.id.iv_edit_back_preview);

        MaterialButton btnReplaceFront = findViewById(R.id.btn_replace_front_image);
        MaterialButton btnReplaceBack = findViewById(R.id.btn_replace_back_image);
        MaterialButton btnSaveChanges = findViewById(R.id.btn_save_changes);
        MaterialButton btnCancelEdit = findViewById(R.id.btn_cancel_edit);

        if (getIntent() != null && getIntent().hasExtra(CardDetailsActivity.EXTRA_CARD_ID)) {
            cardId = getIntent().getStringExtra(CardDetailsActivity.EXTRA_CARD_ID);
        }

        if (cardId == null || cardId.isEmpty()) {
            Toast.makeText(this, getString(R.string.msg_card_not_found), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        existingCard = cardStorageManager.getCardById(cardId);
        if (existingCard == null) {
            Toast.makeText(this, getString(R.string.msg_card_not_found), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        populateExistingCardData();

        btnReplaceFront.setOnClickListener(v -> frontPickerLauncher.launch("image/*"));
        btnReplaceBack.setOnClickListener(v -> backPickerLauncher.launch("image/*"));
        btnSaveChanges.setOnClickListener(v -> saveCardChanges());
        btnCancelEdit.setOnClickListener(v -> finish());
    }

    private void populateExistingCardData() {
        etEditCardName.setText(existingCard.getName());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.card_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEditCategory.setAdapter(adapter);

        if (existingCard.getCategory() != null) {
            int position = adapter.getPosition(existingCard.getCategory());
            if (position >= 0) {
                spinnerEditCategory.setSelection(position);
            }
        }

        loadImagePreview(existingCard.getFrontImagePath(), ivEditFrontPreview);
        loadImagePreview(existingCard.getBackImagePath(), ivEditBackPreview);
    }

    private void loadImagePreview(String imagePath, ImageView imageView) {
        if (imagePath != null && !imagePath.isEmpty()) {
            File file = new File(imagePath);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                    return;
                }
            }
        }
        imageView.setImageResource(R.drawable.ic_card_wallet);
    }

    private void saveCardChanges() {
        tilEditCardName.setError(null);

        String cardName = etEditCardName.getText() != null ? etEditCardName.getText().toString().trim() : "";
        if (cardName.isEmpty()) {
            tilEditCardName.setError(getString(R.string.error_missing_card_name));
            return;
        }

        String selectedCategory = spinnerEditCategory.getSelectedItem() != null ?
                spinnerEditCategory.getSelectedItem().toString() : "Other";

        String updatedFrontPath = existingCard.getFrontImagePath();
        String oldFrontPathToDelete = null;

        if (newFrontUri != null) {
            String savedFront = cardStorageManager.saveImageToInternalStorage(newFrontUri, "card_front_" + cardId + "_" + System.currentTimeMillis());
            if (!savedFront.isEmpty()) {
                oldFrontPathToDelete = existingCard.getFrontImagePath();
                updatedFrontPath = savedFront;
            }
        }

        String updatedBackPath = existingCard.getBackImagePath();
        String oldBackPathToDelete = null;

        if (newBackUri != null) {
            String savedBack = cardStorageManager.saveImageToInternalStorage(newBackUri, "card_back_" + cardId + "_" + System.currentTimeMillis());
            if (!savedBack.isEmpty()) {
                oldBackPathToDelete = existingCard.getBackImagePath();
                updatedBackPath = savedBack;
            }
        }

        CardModel updatedCard = new CardModel(
                cardId,
                cardName,
                selectedCategory,
                updatedFrontPath,
                updatedBackPath,
                existingCard.getCreatedAt()
        );

        boolean isUpdated = cardStorageManager.updateCard(updatedCard);
        if (isUpdated) {
            if (oldFrontPathToDelete != null) {
                cardStorageManager.deleteImageFile(oldFrontPathToDelete);
            }
            if (oldBackPathToDelete != null) {
                cardStorageManager.deleteImageFile(oldBackPathToDelete);
            }
            Toast.makeText(this, getString(R.string.msg_card_updated_success), Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update card. Please try again.", Toast.LENGTH_SHORT).show();
            if (oldFrontPathToDelete != null) {
                cardStorageManager.deleteImageFile(updatedFrontPath);
            }
            if (oldBackPathToDelete != null) {
                cardStorageManager.deleteImageFile(updatedBackPath);
            }
        }
    }
}