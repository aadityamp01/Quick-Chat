package com.example.demochatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    MaterialEditText username,email,password;
    Button btn_register;
    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_register = findViewById(R.id.btn_register);
        auth = FirebaseAuth.getInstance();

        btn_register.setOnClickListener(view -> {
            String txt_username = Objects.requireNonNull(username.getText()).toString();
            String txt_email = Objects.requireNonNull(email.getText()).toString();
            String txt_password = Objects.requireNonNull(password.getText()).toString();
            if(TextUtils.isEmpty(txt_username ) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password))
            {
                Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
            }else if ( txt_password.length() < 6){
                Toast.makeText(RegisterActivity.this, "Password must be atleast 6 characters", Toast.LENGTH_SHORT).show();
            }else{
                register(txt_username,txt_email,txt_password);
            }
        });


    }
    //add users to database
    public void register(final String username, String email, String password){

        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(
                        task -> {
                            if(task.isSuccessful()){
                                FirebaseUser firebaseUser = auth.getCurrentUser();
                                assert firebaseUser != null;
                                String userid =firebaseUser.getUid();
                                reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                                HashMap<String,String> hashMap = new HashMap<>();
                                hashMap.put("id",userid);
                                hashMap.put("username",username);
                                hashMap.put("imageURL","default");
                                hashMap.put("status","offline");
                                hashMap.put("search",username.toLowerCase());

                                reference.setValue(hashMap).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                });



                            }
                            else{
                                Toast.makeText(RegisterActivity.this,"You can't have this email or password for registration",Toast.LENGTH_LONG).show();
                            }
                        }
                );

    }

}
