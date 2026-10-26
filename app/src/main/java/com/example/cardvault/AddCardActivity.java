package com.example.cardvault;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cardvault.models.CardModel;
import com.example.cardvault.utils.CardStorageManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.UUID;

public class AddCardActivity extends AppCompatActivity {

    private TextInputLayout tilCardName;
    private TextInputEditText etCardName;
    private Spinner spinnerCategory;

    private ImageView ivFrontPreview;
    private TextView tvFrontStatus;
    private MaterialButton btnSelectFrontImage;

    private ImageView ivBackPreview;
    private TextView tvBackStatus;
    private MaterialButton btnSelectBackImage;

    private Uri frontImageUri;
    private Uri backImageUri;

    private CardStorageManager cardStorageManager;

    private final ActivityResultLauncher<String> frontPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    frontImageUri = uri;
                    ivFrontPreview.setImageURI(uri);
                    ivFrontPreview.setVisibility(View.VISIBLE);
                    tvFrontStatus.setVisibility(View.GONE);
                    btnSelectFrontImage.setText(R.string.btn_change_front_image);
                }
            });

    private final ActivityResultLauncher<String> backPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    backImageUri = uri;
                    ivBackPreview.setImageURI(uri);
                    ivBackPreview.setVisibility(View.VISIBLE);
                    tvBackStatus.setVisibility(View.GONE);
                    btnSelectBackImage.setText(R.string.btn_change_back_image);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        cardStorageManager = new CardStorageManager(this);

        tilCardName = findViewById(R.id.til_card_name);
        etCardName = findViewById(R.id.et_card_name);
        spinnerCategory = findViewById(R.id.spinner_card_category);

        ivFrontPreview = findViewById(R.id.iv_front_preview);
        tvFrontStatus = findViewById(R.id.tv_front_status);
        btnSelectFrontImage = findViewById(R.id.btn_select_front_image);

        ivBackPreview = findViewById(R.id.iv_back_preview);
        tvBackStatus = findViewById(R.id.tv_back_status);
        btnSelectBackImage = findViewById(R.id.btn_select_back_image);

        MaterialButton btnSaveCard = findViewById(R.id.btn_save_card);
        MaterialButton btnCancel = findViewById(R.id.btn_cancel);

        setupCategorySpinner();

        btnSelectFrontImage.setOnClickListener(v -> frontPickerLauncher.launch("image/*"));
        btnSelectBackImage.setOnClickListener(v -> backPickerLauncher.launch("image/*"));

        btnSaveCard.setOnClickListener(v -> saveCard());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void setupCategorySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.card_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void saveCard() {
        tilCardName.setError(null);

        String cardName = etCardName.getText() != null ? etCardName.getText().toString().trim() : "";
        if (cardName.isEmpty()) {
            tilCardName.setError(getString(R.string.error_missing_card_name));
            return;
        }

        if (frontImageUri == null) {
            Toast.makeText(this, getString(R.string.error_missing_front_image), Toast.LENGTH_LONG).show();
            return;
        }

        if (backImageUri == null) {
            Toast.makeText(this, getString(R.string.error_missing_back_image), Toast.LENGTH_LONG).show();
            return;
        }

        String cardId = UUID.randomUUID().toString();

        String frontPath = cardStorageManager.saveImageToInternalStorage(frontImageUri, "card_front_" + cardId);
        String backPath = cardStorageManager.saveImageToInternalStorage(backImageUri, "card_back_" + cardId);

        if (frontPath.isEmpty() || backPath.isEmpty()) {
            Toast.makeText(this, "Failed to save card images. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        String category = spinnerCategory.getSelectedItem() != null ?
                spinnerCategory.getSelectedItem().toString() : "Other";

        CardModel card = new CardModel(
                cardId,
                cardName,
                category,
                frontPath,
                backPath,
                System.currentTimeMillis()
        );

        boolean isSaved = cardStorageManager.saveCard(card);
        if (isSaved) {
            Toast.makeText(this, getString(R.string.msg_card_saved_success), Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Error saving card record. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}