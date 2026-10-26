package com.example.cardvault;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class FullScreenImageActivity extends AppCompatActivity {

    public static final String EXTRA_IMAGE_PATH = "EXTRA_IMAGE_PATH";
    public static final String EXTRA_TITLE = "EXTRA_TITLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        ImageView btnClose = findViewById(R.id.btn_close_full_screen);
        TextView tvTitle = findViewById(R.id.tv_full_screen_title);
        ImageView ivFullScreenImage = findViewById(R.id.iv_full_screen_image);

        btnClose.setOnClickListener(v -> finish());

        String imagePath = null;
        String title = "Card Image";

        if (getIntent() != null) {
            if (getIntent().hasExtra(EXTRA_IMAGE_PATH)) {
                imagePath = getIntent().getStringExtra(EXTRA_IMAGE_PATH);
            }
            if (getIntent().hasExtra(EXTRA_TITLE)) {
                title = getIntent().getStringExtra(EXTRA_TITLE);
            }
        }

        tvTitle.setText(title);

        if (imagePath != null && !imagePath.isEmpty()) {
            File file = new File(imagePath);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                if (bitmap != null) {
                    ivFullScreenImage.setImageBitmap(bitmap);
                    return;
                }
            }
        }

        Toast.makeText(this, getString(R.string.msg_missing_image_file), Toast.LENGTH_SHORT).show();
        ivFullScreenImage.setImageResource(R.drawable.ic_card_wallet);
    }
}