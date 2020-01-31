package com.example.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 2;
    GoogleSignInClient mGoogleSignInClient;
    EditText mEmailEdt, mpasswordEdt;
    TextView Nothaveaccount, mRecoverTv;
    Button mLogintBtn;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    SignInButton mggbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //before auth
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        // actionsbar and its title. create title
        ActionBar actionBar = getSupportActionBar(); // tao moi cai acctionbar
        actionBar.setTitle("Login ");
        //enable back button. cho phep quay tro lai . co cai nut goc trai' man hinh
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //init
        mggbtn = findViewById(R.id.googleLoginBtn);
        mEmailEdt = findViewById(R.id.emailEt);
        mpasswordEdt = findViewById(R.id.passEt);
        mLogintBtn = findViewById(R.id.LoginBtn);
        mRecoverTv = findViewById(R.id.recoverPasswordTv);
        Nothaveaccount = findViewById(R.id.Nothave_accountTv);


        // login button click
        mLogintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //input data
                String email = mEmailEdt.getText().toString().trim();
                String password = mpasswordEdt.getText().toString().trim();


                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    //set error
                    mEmailEdt.setError("Invalid Email");
                    mEmailEdt.setFocusable(true);
                } else {
                    LoginUser(email, password);
                }
            }
        });

        // not have account set onclick
        Nothaveaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });


        //handle google sign in button click
        mggbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);

            }
        });


        //set onlick for REcover password
        mRecoverTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showrecoverPasswordDialog();
            }
        });


        progressDialog = new ProgressDialog(this);


    }

    private void showrecoverPasswordDialog() {
        //AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("RECOVER PASSWORD");
        //set layout linearlayout
        LinearLayout linearLayout = new LinearLayout(this);
        ///views to set in dialog

        final EditText emailEt = new EditText(this);
        emailEt.setHint("Email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        /*
        sets the min width of a edittext to fix a text of n 'M'letters regardless
        of the actual text extension and text size
        */
        emailEt.setMinEms(16);


        linearLayout.addView(emailEt);
        linearLayout.setPadding(10, 10, 10, 10);
        builder.setView(linearLayout);

        //buttons recover
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input email
                String email = emailEt.getText().toString().trim();
                beginRecovery(email);

            }
        });

        //buttons cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dismiss dialog
                dialog.dismiss();

            }
        });
        //show dialog
        builder.create().show();

    }

    private void beginRecovery(String email) {

        // show progress
        progressDialog.show();
        progressDialog.setMessage("Sendng Email....");

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,
                                    "Email sent", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this,
                                    "Failed...", Toast.LENGTH_SHORT).show();

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();

                //get and show proper error message
                Toast.makeText(LoginActivity.this,
                        "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void LoginUser(String email, String password) {

        // show progress

        progressDialog.show();
        progressDialog.setMessage("loging in....");

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //dissmis if success
                            progressDialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();
                        } else {
                            //dissmiss
                            progressDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //dismiss progressdialog
                progressDialog.dismiss();
                //error,get and show  error massage
                Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // previous activity
        return super.onSupportNavigateUp();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately

                Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //show user notification

                            FirebaseUser user = mAuth.getCurrentUser();

                            //if user is signing  in first time then get
                            // and show user info from google account

                            if(task.getResult().getAdditionalUserInfo().isNewUser()){

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
                                DatabaseReference reference = database.getReference();
                                //put data within hashmap in database
                                reference.child(uid).setValue(hashMap);



                            }


                            Toast.makeText(LoginActivity.this,""+user.getEmail(),Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this,"Login Failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

}
