package com.example.cardvault;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cardvault.models.UserModel;
import com.example.cardvault.utils.SessionManager;
import com.example.cardvault.utils.UserStorageManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_PREFILL_IDENTIFIER = "EXTRA_PREFILL_IDENTIFIER";

    private TextInputLayout tilEmailUsername;
    private TextInputLayout tilPassword;
    private TextInputEditText etEmailUsername;
    private TextInputEditText etPassword;

    private UserStorageManager userStorageManager;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userStorageManager = new UserStorageManager(this);
        sessionManager = new SessionManager(this);

        tilEmailUsername = findViewById(R.id.til_email_username);
        tilPassword = findViewById(R.id.til_password);
        etEmailUsername = findViewById(R.id.et_email_username);
        etPassword = findViewById(R.id.et_password);
        MaterialButton btnLogin = findViewById(R.id.btn_login);
        TextView tvForgotPassword = findViewById(R.id.tv_forgot_password);
        TextView tvLinkRegister = findViewById(R.id.tv_link_register);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_PREFILL_IDENTIFIER)) {
            String prefill = intent.getStringExtra(EXTRA_PREFILL_IDENTIFIER);
            if (prefill != null) {
                etEmailUsername.setText(prefill);
            }
        }

        btnLogin.setOnClickListener(v -> attemptLogin());
        tvForgotPassword.setOnClickListener(v -> {
            Intent forgotIntent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(forgotIntent);
        });
        tvLinkRegister.setOnClickListener(v -> {
            Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(registerIntent);
        });
    }

    private void attemptLogin() {
        tilEmailUsername.setError(null);
        tilPassword.setError(null);

        String identifier = etEmailUsername.getText() != null ? etEmailUsername.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        boolean isValid = true;

        if (identifier.isEmpty()) {
            tilEmailUsername.setError(getString(R.string.error_field_required));
            isValid = false;
        }

        if (password.isEmpty()) {
            tilPassword.setError(getString(R.string.error_field_required));
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        boolean isValidUser = userStorageManager.validateUser(identifier, password);
        if (isValidUser) {
            UserModel user = userStorageManager.getUserByEmailOrUsername(identifier);
            if (user != null) {
                sessionManager.createLoginSession(user.getEmail(), user.getUsername());
            }
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
            Intent dashboardIntent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(dashboardIntent);
            finish();
        } else {
            Toast.makeText(this, getString(R.string.error_invalid_credentials), Toast.LENGTH_LONG).show();
            tilPassword.setError(getString(R.string.error_invalid_credentials));
        }
    }
}