package com.example.cardvault;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cardvault.models.UserModel;
import com.example.cardvault.utils.SecurityUtils;
import com.example.cardvault.utils.UserStorageManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilFullName;
    private TextInputLayout tilEmail;
    private TextInputLayout tilUsername;
    private TextInputLayout tilSecurityAnswer;
    private TextInputLayout tilPassword;
    private TextInputLayout tilConfirmPassword;

    private TextInputEditText etFullName;
    private TextInputEditText etEmail;
    private TextInputEditText etUsername;
    private Spinner spinnerSecurityQuestion;
    private TextInputEditText etSecurityAnswer;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;

    private UserStorageManager userStorageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userStorageManager = new UserStorageManager(this);

        tilFullName = findViewById(R.id.til_full_name);
        tilEmail = findViewById(R.id.til_email);
        tilUsername = findViewById(R.id.til_username);
        tilSecurityAnswer = findViewById(R.id.til_security_answer);
        tilPassword = findViewById(R.id.til_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);

        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etUsername = findViewById(R.id.et_username);
        spinnerSecurityQuestion = findViewById(R.id.spinner_security_question);
        etSecurityAnswer = findViewById(R.id.et_security_answer);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);

        setupSecurityQuestionSpinner();

        MaterialButton btnRegister = findViewById(R.id.btn_register);
        TextView tvLinkLogin = findViewById(R.id.tv_link_login);

        btnRegister.setOnClickListener(v -> attemptRegistration());
        tvLinkLogin.setOnClickListener(v -> finish());
    }

    private void setupSecurityQuestionSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.security_questions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSecurityQuestion.setAdapter(adapter);
    }

    private void attemptRegistration() {
        tilFullName.setError(null);
        tilEmail.setError(null);
        tilUsername.setError(null);
        tilSecurityAnswer.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        String fullName = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String securityAnswer = etSecurityAnswer.getText() != null ? etSecurityAnswer.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

        boolean isValid = true;

        if (fullName.isEmpty()) {
            tilFullName.setError(getString(R.string.error_field_required));
            isValid = false;
        }

        if (email.isEmpty()) {
            tilEmail.setError(getString(R.string.error_field_required));
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.error_invalid_email));
            isValid = false;
        } else if (userStorageManager.isEmailTaken(email)) {
            tilEmail.setError(getString(R.string.error_email_exists));
            isValid = false;
        }

        if (username.isEmpty()) {
            tilUsername.setError(getString(R.string.error_field_required));
            isValid = false;
        } else if (username.length() < 3) {
            tilUsername.setError("Username must be at least 3 characters");
            isValid = false;
        } else if (userStorageManager.isUsernameTaken(username)) {
            tilUsername.setError(getString(R.string.error_username_exists));
            isValid = false;
        }

        if (securityAnswer.isEmpty()) {
            tilSecurityAnswer.setError(getString(R.string.error_field_required));
            isValid = false;
        }

        if (password.isEmpty()) {
            tilPassword.setError(getString(R.string.error_field_required));
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError(getString(R.string.error_password_too_short));
            isValid = false;
        }

        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.setError(getString(R.string.error_field_required));
            isValid = false;
        } else if (!confirmPassword.equals(password)) {
            tilConfirmPassword.setError(getString(R.string.error_passwords_dont_match));
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        String selectedQuestion = spinnerSecurityQuestion.getSelectedItem() != null ?
                spinnerSecurityQuestion.getSelectedItem().toString() : "What was the name of your first pet?";

        String normalizedAnswer = securityAnswer.toLowerCase();
        String securityAnswerHash = SecurityUtils.hashPassword(normalizedAnswer);
        String passwordHash = SecurityUtils.hashPassword(password);

        UserModel newUser = new UserModel(
                username,
                email,
                passwordHash,
                fullName,
                selectedQuestion,
                securityAnswerHash
        );

        boolean registered = userStorageManager.registerUser(newUser);
        if (registered) {
            Toast.makeText(this, getString(R.string.msg_registration_success), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            intent.putExtra(LoginActivity.EXTRA_PREFILL_IDENTIFIER, username);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}