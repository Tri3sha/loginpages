package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {


    public static final String TAG = "TAG";
    EditText menterfullname, mEmail, mpassword, mphone;
    Button mRegisterBtn;
    TextView mloginBtn;
    ProgressBar progressBar;

    FirebaseFirestore fstore ;
    String userID;
    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        menterfullname = findViewById(R.id.enterfullname);
        mEmail = findViewById(R.id.email);
        mpassword = findViewById(R.id.password);
        mphone = findViewById(R.id.phone);
        mRegisterBtn = findViewById(R.id.registerbtn);
        mloginBtn = findViewById(R.id.createtext);
        progressBar = findViewById(R.id.progressBar);


        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();



        if(fAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }


        mloginBtn.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), Login.class)));


        mRegisterBtn.setOnClickListener(view -> {
            final String email = mEmail.getText().toString().trim();
            String password = mpassword.getText().toString().trim();
            final String enterfullname = menterfullname.getText().toString();
            final String phone = mphone.getText().toString();

            if (TextUtils.isEmpty(email)) {
                mEmail.setError("email is required");
                return;

            }

            if (TextUtils.isEmpty(password)) {
                mpassword.setError("password is required");
                return;
            }

            if (password.length() < 6) {

                mpassword.setError("Password must be >= 6 characters");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);


            Task<AuthResult> registerSuccessfull = fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        FirebaseUser fuser = fAuth.getCurrentUser();
                        fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getApplicationContext(), "Register Successfull", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Onfailure:Email not sent" + e.getMessage());
                            }
                        });


                        Toast.makeText(getApplicationContext(), "User created", Toast.LENGTH_SHORT).show();
                        userID=fAuth.getCurrentUser().getUid();
                        DocumentReference documentReference=fstore.collection("user").document(userID);
                        Map<String,Object> user=new HashMap<>();
                        user.put("fName",enterfullname);
                        user.put("email",email);
                        user.put("phone",phone);
                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG,"onsuccess:user profile is created for "+userID);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG,"onFailure:"+e.toString());
                            }
                        });

                        startActivity(new Intent(getApplicationContext(),MainActivity.class));

                    }
                    else {

                        Toast.makeText(Register.this, "Error"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        });


    }


}