package com.example.cardvault;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cardvault.models.UserModel;
import com.example.cardvault.utils.UserStorageManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ForgotPasswordActivity extends AppCompatActivity {

    public static final String EXTRA_RESET_IDENTIFIER = "EXTRA_RESET_IDENTIFIER";

    private TextInputLayout tilIdentifier;
    private TextView tvSecurityQuestionDisplay;
    private TextInputLayout tilSecurityAnswer;

    private TextInputEditText etIdentifier;
    private TextInputEditText etSecurityAnswer;

    private UserStorageManager userStorageManager;
    private UserModel matchedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        userStorageManager = new UserStorageManager(this);

        tilIdentifier = findViewById(R.id.til_identifier);
        tvSecurityQuestionDisplay = findViewById(R.id.tv_security_question_display);
        tilSecurityAnswer = findViewById(R.id.til_security_answer);

        etIdentifier = findViewById(R.id.et_identifier);
        etSecurityAnswer = findViewById(R.id.et_security_answer);

        MaterialButton btnVerifyAccount = findViewById(R.id.btn_verify_account);
        TextView tvLinkBackLogin = findViewById(R.id.tv_link_back_login);

        etIdentifier.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkAccountAndDisplayQuestion(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnVerifyAccount.setOnClickListener(v -> attemptAccountVerification());
        tvLinkBackLogin.setOnClickListener(v -> finish());
    }

    private void checkAccountAndDisplayQuestion(String identifier) {
        if (identifier.isEmpty()) {
            matchedUser = null;
            tvSecurityQuestionDisplay.setText("Enter registered email or username above");
            return;
        }

        matchedUser = userStorageManager.getUserByEmailOrUsername(identifier);
        if (matchedUser != null) {
            tilIdentifier.setError(null);
            String question = matchedUser.getSecurityQuestion();
            if (question != null && !question.isEmpty()) {
                tvSecurityQuestionDisplay.setText(question);
            } else {
                tvSecurityQuestionDisplay.setText("What was the name of your first pet?");
            }
        } else {
            tvSecurityQuestionDisplay.setText("No account found with that email or username");
        }
    }

    private void attemptAccountVerification() {
        tilIdentifier.setError(null);
        tilSecurityAnswer.setError(null);

        String identifier = etIdentifier.getText() != null ? etIdentifier.getText().toString().trim() : "";
        String securityAnswer = etSecurityAnswer.getText() != null ? etSecurityAnswer.getText().toString().trim() : "";

        if (identifier.isEmpty()) {
            tilIdentifier.setError(getString(R.string.error_field_required));
            return;
        }

        matchedUser = userStorageManager.getUserByEmailOrUsername(identifier);
        if (matchedUser == null) {
            tilIdentifier.setError(getString(R.string.error_user_not_found));
            return;
        }

        if (securityAnswer.isEmpty()) {
            tilSecurityAnswer.setError(getString(R.string.error_field_required));
            return;
        }

        boolean answerValid = userStorageManager.validateSecurityAnswer(identifier, securityAnswer);
        if (answerValid) {
            Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
            intent.putExtra(EXTRA_RESET_IDENTIFIER, matchedUser.getEmail());
            startActivity(intent);
            finish();
        } else {
            tilSecurityAnswer.setError(getString(R.string.error_incorrect_security_answer));
            Toast.makeText(this, getString(R.string.error_incorrect_security_answer), Toast.LENGTH_SHORT).show();
        }
    }
}