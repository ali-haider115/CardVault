package com.example.cardvault;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cardvault.utils.UserStorageManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputLayout tilNewPassword;
    private TextInputLayout tilConfirmNewPassword;

    private TextInputEditText etNewPassword;
    private TextInputEditText etConfirmNewPassword;

    private UserStorageManager userStorageManager;
    private String identifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        userStorageManager = new UserStorageManager(this);

        tilNewPassword = findViewById(R.id.til_new_password);
        tilConfirmNewPassword = findViewById(R.id.til_confirm_new_password);

        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmNewPassword = findViewById(R.id.et_confirm_new_password);

        MaterialButton btnSaveNewPassword = findViewById(R.id.btn_save_new_password);
        TextView tvLinkCancelReset = findViewById(R.id.tv_link_cancel_reset);

        if (getIntent() != null && getIntent().hasExtra(ForgotPasswordActivity.EXTRA_RESET_IDENTIFIER)) {
            identifier = getIntent().getStringExtra(ForgotPasswordActivity.EXTRA_RESET_IDENTIFIER);
        }

        if (identifier == null || identifier.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_user_not_found), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnSaveNewPassword.setOnClickListener(v -> saveNewPassword());
        tvLinkCancelReset.setOnClickListener(v -> finish());
    }

    private void saveNewPassword() {
        tilNewPassword.setError(null);
        tilConfirmNewPassword.setError(null);

        String newPassword = etNewPassword.getText() != null ? etNewPassword.getText().toString().trim() : "";
        String confirmPassword = etConfirmNewPassword.getText() != null ? etConfirmNewPassword.getText().toString().trim() : "";

        boolean isValid = true;

        if (newPassword.isEmpty()) {
            tilNewPassword.setError(getString(R.string.error_field_required));
            isValid = false;
        } else if (newPassword.length() < 6) {
            tilNewPassword.setError(getString(R.string.error_password_too_short));
            isValid = false;
        }

        if (confirmPassword.isEmpty()) {
            tilConfirmNewPassword.setError(getString(R.string.error_field_required));
            isValid = false;
        } else if (!confirmPassword.equals(newPassword)) {
            tilConfirmNewPassword.setError(getString(R.string.error_passwords_dont_match));
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        boolean updated = userStorageManager.resetPassword(identifier, newPassword);
        if (updated) {
            Toast.makeText(this, getString(R.string.msg_password_reset_success), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
            intent.putExtra(LoginActivity.EXTRA_PREFILL_IDENTIFIER, identifier);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to update password. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}