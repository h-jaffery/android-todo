package com.example.todofirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    EditText etRegEmail, etRegPassword;
    TextView tvLoginHere;
    Button btnRegister;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etRegEmail = findViewById(R.id.register_email);
        etRegPassword = findViewById(R.id.register_password);
        tvLoginHere = findViewById(R.id.LOGIN_TEXT);
        btnRegister = findViewById(R.id.register_btn);

        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(view -> {
            createUser();
        });

        tvLoginHere.setOnClickListener(view -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });
    }

    private void createUser() {
        String email = etRegEmail.getText().toString();
        String password = etRegPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            etRegEmail.setError("Fill in the email field.");
            etRegEmail.requestFocus();
        }
        else if (TextUtils.isEmpty(password)) {
            etRegPassword.setError("Fill in the password field.");
            etRegPassword.requestFocus();
        }
        else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this, "User created!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    }
                    else {
                        Toast.makeText(RegisterActivity.this, "Registration FAILED!!!!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}