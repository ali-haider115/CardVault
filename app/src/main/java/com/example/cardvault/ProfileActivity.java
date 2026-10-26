package com.example.cardvault;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cardvault.models.UserModel;
import com.example.cardvault.utils.SessionManager;
import com.example.cardvault.utils.UserStorageManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivProfileAvatar;
    private TextView tvHeaderUserName;
    private TextInputLayout tilProfileFullName;
    private TextInputEditText etProfileFullName;
    private TextInputEditText etProfileEmail;
    private TextInputEditText etProfileUsername;

    private MaterialButton btnEditProfile;
    private MaterialButton btnCancelProfileEdit;
    private MaterialButton btnSaveProfile;

    private SessionManager sessionManager;
    private UserStorageManager userStorageManager;
    private UserModel currentUser;

    private final ActivityResultLauncher<String> profilePicPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    saveAndApplyProfilePicture(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);
        userStorageManager = new UserStorageManager(this);

        ImageView btnBack = findViewById(R.id.btn_profile_back);
        ivProfileAvatar = findViewById(R.id.iv_profile_avatar);
        ImageView btnEditPhoto = findViewById(R.id.btn_edit_profile_photo);

        tvHeaderUserName = findViewById(R.id.tv_header_user_name);

        tilProfileFullName = findViewById(R.id.til_profile_full_name);
        etProfileFullName = findViewById(R.id.et_profile_full_name);
        etProfileEmail = findViewById(R.id.et_profile_email);
        etProfileUsername = findViewById(R.id.et_profile_username);

        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnCancelProfileEdit = findViewById(R.id.btn_cancel_profile_edit);
        btnSaveProfile = findViewById(R.id.btn_save_profile);
        MaterialButton btnChangePassword = findViewById(R.id.btn_change_password);
        MaterialButton btnLogoutAccount = findViewById(R.id.btn_logout_account);

        btnBack.setOnClickListener(v -> finish());
        ivProfileAvatar.setOnClickListener(v -> profilePicPickerLauncher.launch("image/*"));
        if (btnEditPhoto != null) {
            btnEditPhoto.setOnClickListener(v -> profilePicPickerLauncher.launch("image/*"));
        }

        btnEditProfile.setOnClickListener(v -> setEditMode(true));
        btnCancelProfileEdit.setOnClickListener(v -> {
            setEditMode(false);
            populateProfileData();
        });
        btnSaveProfile.setOnClickListener(v -> saveProfileChanges());
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
        btnLogoutAccount.setOnClickListener(v -> showLogoutConfirmationDialog());

        populateProfileData();
    }

    private void populateProfileData() {
        String userEmail = sessionManager.getUserEmail();
        currentUser = userStorageManager.getUserByEmailOrUsername(userEmail);

        if (currentUser != null) {
            etProfileFullName.setText(currentUser.getFullName());
            etProfileEmail.setText(currentUser.getEmail());
            etProfileUsername.setText(currentUser.getUsername());

            String displayName = currentUser.getFullName() != null && !currentUser.getFullName().isEmpty() ?
                    currentUser.getFullName() : currentUser.getUsername();
            tvHeaderUserName.setText(displayName);
        } else {
            etProfileFullName.setText(sessionManager.getFullName());
            etProfileEmail.setText(sessionManager.getUserEmail());
            etProfileUsername.setText(sessionManager.getUsername());
            tvHeaderUserName.setText(sessionManager.getFullName());
        }

        loadProfilePicture();
    }

    private void loadProfilePicture() {
        String picPath = sessionManager.getProfilePicPath();
        if (picPath != null && !picPath.isEmpty()) {
            File file = new File(picPath);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                if (bitmap != null) {
                    ivProfileAvatar.setImageBitmap(bitmap);
                    return;
                }
            }
        }
        ivProfileAvatar.setImageResource(R.drawable.ic_profile);
    }

    private void saveAndApplyProfilePicture(Uri uri) {
        try {
            File picDir = new File(getFilesDir(), "profile_pictures");
            if (!picDir.exists()) {
                picDir.mkdirs();
            }

            File destFile = new File(picDir, "profile_pic_" + System.currentTimeMillis() + ".jpg");
            try (InputStream inputStream = getContentResolver().openInputStream(uri);
                 FileOutputStream outputStream = new FileOutputStream(destFile)) {

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                    outputStream.flush();

                    String oldPath = sessionManager.getProfilePicPath();
                    if (oldPath != null && !oldPath.isEmpty()) {
                        File oldFile = new File(oldPath);
                        if (oldFile.exists()) oldFile.delete();
                    }

                    String newPath = destFile.getAbsolutePath();
                    sessionManager.updateProfilePicPath(newPath);

                    ivProfileAvatar.setImageBitmap(bitmap);
                    Toast.makeText(this, getString(R.string.msg_profile_photo_updated), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to set profile photo.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setEditMode(boolean editing) {
        etProfileFullName.setEnabled(editing);

        if (editing) {
            btnEditProfile.setVisibility(View.GONE);
            btnCancelProfileEdit.setVisibility(View.VISIBLE);
            btnSaveProfile.setVisibility(View.VISIBLE);
            etProfileFullName.requestFocus();
        } else {
            tilProfileFullName.setError(null);
            btnEditProfile.setVisibility(View.VISIBLE);
            btnCancelProfileEdit.setVisibility(View.GONE);
            btnSaveProfile.setVisibility(View.GONE);
        }
    }

    private void saveProfileChanges() {
        tilProfileFullName.setError(null);

        String newFullName = etProfileFullName.getText() != null ? etProfileFullName.getText().toString().trim() : "";
        if (newFullName.isEmpty()) {
            tilProfileFullName.setError(getString(R.string.error_field_required));
            return;
        }

        String identifier = sessionManager.getUserEmail();
        boolean updated = userStorageManager.updateUserProfile(identifier, newFullName);

        sessionManager.updateFullName(newFullName);

        if (updated) {
            Toast.makeText(this, getString(R.string.msg_profile_updated_success), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Profile preferences saved locally.", Toast.LENGTH_SHORT).show();
        }

        tvHeaderUserName.setText(newFullName);
        setEditMode(false);
    }

    private void showChangePasswordDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);

        TextInputLayout tilCurrentPass = dialogView.findViewById(R.id.til_current_password);
        TextInputLayout tilNewPass = dialogView.findViewById(R.id.til_new_password);
        TextInputLayout tilConfirmPass = dialogView.findViewById(R.id.til_confirm_new_password);

        TextInputEditText etCurrentPass = dialogView.findViewById(R.id.et_current_password);
        TextInputEditText etNewPass = dialogView.findViewById(R.id.et_new_password);
        TextInputEditText etConfirmPass = dialogView.findViewById(R.id.et_confirm_new_password);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.btn_change_password)
                .setView(dialogView)
                .setPositiveButton("Update", null)
                .setNegativeButton(R.string.dialog_cancel, null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                tilCurrentPass.setError(null);
                tilNewPass.setError(null);
                tilConfirmPass.setError(null);

                String currentPass = etCurrentPass.getText() != null ? etCurrentPass.getText().toString().trim() : "";
                String newPass = etNewPass.getText() != null ? etNewPass.getText().toString().trim() : "";
                String confirmPass = etConfirmPass.getText() != null ? etConfirmPass.getText().toString().trim() : "";

                boolean valid = true;

                if (currentPass.isEmpty()) {
                    tilCurrentPass.setError(getString(R.string.error_field_required));
                    valid = false;
                } else {
                    String userEmail = sessionManager.getUserEmail();
                    if (!userStorageManager.validateUser(userEmail, currentPass)) {
                        tilCurrentPass.setError(getString(R.string.error_incorrect_current_password));
                        valid = false;
                    }
                }

                if (newPass.isEmpty()) {
                    tilNewPass.setError(getString(R.string.error_field_required));
                    valid = false;
                } else if (newPass.length() < 6) {
                    tilNewPass.setError(getString(R.string.error_password_too_short));
                    valid = false;
                }

                if (confirmPass.isEmpty()) {
                    tilConfirmPass.setError(getString(R.string.error_field_required));
                    valid = false;
                } else if (!confirmPass.equals(newPass)) {
                    tilConfirmPass.setError(getString(R.string.error_passwords_dont_match));
                    valid = false;
                }

                if (valid) {
                    String userEmail = sessionManager.getUserEmail();
                    userStorageManager.resetPassword(userEmail, newPass);
                    Toast.makeText(ProfileActivity.this, getString(R.string.msg_password_changed_success), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_logout_title)
                .setMessage(R.string.dialog_logout_message)
                .setPositiveButton(R.string.btn_logout_account, (dialog, which) -> {
                    sessionManager.logoutUser();
                    Toast.makeText(ProfileActivity.this, R.string.msg_logged_out, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(R.string.dialog_cancel, null)
                .show();
    }
}