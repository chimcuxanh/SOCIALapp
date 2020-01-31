package com.example.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {
    //import fire auth
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // actionsbar and its title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");


        // init
        firebaseAuth = FirebaseAuth.getInstance();

        // mProfileTv = findViewById(R.id.profileTv);
         //bottom navigation
        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        //home fragment transaction mac dinh
        actionBar.setTitle("Home");
        homeFragment fragment = new homeFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content,fragment,"");
        ft1.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener
            selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            ActionBar actionBar = getSupportActionBar();
            //handle item clicks
            switch (menuItem.getItemId()){
                case R.id.nav_home:
                    actionBar.setTitle("Home");
                    homeFragment fragment = new homeFragment();
                    FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                    ft1.replace(R.id.content,fragment,"");
                    ft1.commit();
                    //home fragment transaction
                    return true;

                case R.id.nav_profile:
                    actionBar.setTitle("profile");
                    profileFragment fragment2 = new profileFragment();
                    FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                    ft2.replace(R.id.content,fragment2,"");
                    ft2.commit();
                    //profile fragment transaction
                    return true;
                case R.id.nav_user:

                    actionBar.setTitle("user");
                    UserFragment fragment3 = new UserFragment();
                    FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                    ft3.replace(R.id.content,fragment3,"");
                    ft3.commit();
                    //user fragment transaction
                    return true;


            }
            return false;
        }
    };
    private void checkUserStatus(){
        //get current User
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            // user is signed in stay here
           // mProfileTv.setText(user.getEmail());
        }
        else {
            //user not signed , go to mainactivity
            startActivity(new Intent(DashboardActivity.this,MainActivity.class));
           finish();
        }

    }
// chua biet
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {
        //check on start of app
        checkUserStatus();
        super.onStart();
    }


    //inflate options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate options menu
        getMenuInflater().inflate(R.menu.menu_main,menu);


        return super.onCreateOptionsMenu(menu);
    }


    //handle menu logout item

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //get item id
        int id = item.getItemId();
        if (id == R.id.action_Logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
}
