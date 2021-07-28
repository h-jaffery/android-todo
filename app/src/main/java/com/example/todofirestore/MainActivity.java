package com.example.todofirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private EditText mTitle, mDesc;
    private Button mSaveBtn, mShowBtn, mLogout;
    private FirebaseFirestore db;
    private String uTitle, uDesc, uId;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle  = findViewById(R.id.edit_title);
        mDesc = findViewById(R.id.edit_desc);
        mSaveBtn = findViewById(R.id.save_btn);
        mShowBtn = findViewById(R.id.showall_btn);
        mLogout = findViewById(R.id.logout_btn);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mAuth.getCurrentUser();
        String usid =  mAuth.getUid();


        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            mSaveBtn.setText("Update");
            uTitle = bundle.getString("uTitle");
            uId = bundle.getString("uId");
            uDesc = bundle.getString("uDesc");

            mTitle.setText(uTitle);
            mDesc.setText(uDesc);

        }
        else {
            mSaveBtn.setText("Save");
        }

        mLogout.setOnClickListener(view -> {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            Toast.makeText(this, "Successfully logged out.", Toast.LENGTH_SHORT).show();
        });

        mShowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this , ShowActivity.class));
            }
        });

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = mTitle.getText().toString();
                String desc = mDesc.getText().toString();

                Bundle bundle1 = getIntent().getExtras();
                if (bundle1 != null){
                    String id = uId;
                    updateToFireStore(id, title, desc, usid);
                }
                else {
                    String id = UUID.randomUUID().toString();
                    saveToFireStore(id, title, desc, usid);
                }

                mTitle.setText("");
                mDesc.setText("");
            }
        });
    }

    protected void onStart(){
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    private void updateToFireStore(String id, String title, String desc, String usid){

        db.collection("Documents").document(usid).collection("hello").document(id).update("title", title, "desc", desc)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Data Updated.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Error Updating Data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void saveToFireStore(String id, String title, String desc, String usid)
    {
        if (!title.isEmpty() && !desc.isEmpty()){
            HashMap<String, Object> map = new HashMap<>();
            map.put("id", id);
            map.put("title", title);
            map.put("desc", desc);

            db.collection("Documents").document(usid).collection("hello").document(id).set(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            });

        }else
            Toast.makeText(this, "Empty Fields.", Toast.LENGTH_SHORT).show();
    }
}