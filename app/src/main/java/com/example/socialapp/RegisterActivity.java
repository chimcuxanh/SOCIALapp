package com.example.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    //view
    EditText mEmailEt , mPasswordEt;
    Button mRegisterBtn;
    TextView mhave_accountTv;
    // progressBar to display while registering user. cai vong tron loading khi nhan nut dang ky
    ProgressDialog progressdialog;
    // install in firebase insistan
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // actionsbar and its title. create title
        ActionBar actionBar = getSupportActionBar(); // tao moi cai acctionbar
        actionBar.setTitle("Create Account ");
        //enable back button. cho phep quay tro lai . co cai nut goc trai' man hinh
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);
        // install assitans firebase
        mAuth = FirebaseAuth.getInstance();

        //init
        mhave_accountTv = findViewById(R.id.have_accountTv);
        mEmailEt =  findViewById(R.id.emailEt);
        mPasswordEt = findViewById(R.id.passEt);
        mRegisterBtn = findViewById(R.id.registerBtn);
        progressdialog = new ProgressDialog(this); //??? chua hieu cai this
        progressdialog.setMessage("Registering user....");
        //handle click register
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //input email vs password
                String email = mEmailEt.getText().toString().trim();
                String password = mPasswordEt.getText().toString().trim();
                //validate
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    //set error and forcuss to email edittext
                    mEmailEt.setError("Invalid Email");
                    mEmailEt.setFocusable(true);
                    // vien do xung quanh khung nhap
                }
                else if (password.length()<6){
                    //set error and forcuss to password edittext
                    mPasswordEt.setError("Password length at least 6 characters");
                    mPasswordEt.setFocusable(true);
                }
                else {
                    registerUser(email,password);// register the user
                }


            }
        });


        //handle login textview onclick listener

        mhave_accountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,
                        LoginActivity.class));
                finish();
            }
        });

    }

    private void registerUser(String email, String password) {
        //email and password pattern is valid  , show progress dialog and start registering user
        progressdialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, dismiss dialog and start register activity
                            progressdialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();

                            // get user email and uid from auth

                            String email = user.getEmail();
                            String uid = user.getUid();
                            //when user is regestered store user in firebase realtime database too
                            //using HashMap
                            HashMap<Object,String> hashMap = new HashMap<>();
                            //put info in hashmap
                            hashMap.put("email",email);
                            hashMap.put(uid,"uid");
                            hashMap.put("name","");// edit later after edit profile
                            hashMap.put("phone","");// edit later after edit profile
                            hashMap.put("image","");// edit later after edit profile
                            hashMap.put("cover","");// edit later after edit profile
                            //firebase data instance

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            //path to store userdata named"User"
                            DatabaseReference reference = database.getReference("Users");
                            //put data within hashmap in database
                            reference.child(uid).setValue(hashMap);



                            Toast.makeText(RegisterActivity.this ,"Registered\n"+user.getEmail(),Toast.LENGTH_LONG).show();
                            startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressdialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })  .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //error dismiss progress dialog and get and show the message
                progressdialog.dismiss();
                Toast.makeText(RegisterActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // go previous activity
        return super.onSupportNavigateUp();
    }
}
