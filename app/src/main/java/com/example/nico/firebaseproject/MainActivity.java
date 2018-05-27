package com.example.nico.firebaseproject;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nico.firebaseproject.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference mDatabaseReference=FirebaseDatabase.getInstance().getReference("users");
    private FirebaseAuth mAuth;
    private EditText lastname, username, email, firstname, password ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        lastname = (EditText)findViewById(R.id.lastname_register);
        username = (EditText)findViewById(R.id.username_register);
        email = (EditText)findViewById(R.id.email_register);
        firstname = (EditText)findViewById(R.id.firstname_register);
        password = (EditText)findViewById(R.id.password_register);


        //TO ADD THE NAVIGATION DRAWER TO THE ACTIVITY
        ((NavigationView) findViewById(R.id.navigationdrawer_register)).setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // IF A USER IS ALREADY LOGIN, GO DIRECTLY ON THE LIST OF HOUSES
        if (mAuth.getCurrentUser() != null)
        {
            Toast.makeText(this, R.string.input_error_useralreadylogin, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ListOfActivity.class));
            finish();
        }
    }

    //METHOD FOR THE ACTION OF THE BUTTON "REGISTER"
    public void register(View view) {

        // CREATE USER IN THE REALTIME DATABASE
        if (username.getText().toString().isEmpty()){
            username.setError(getString(R.string.input_error_username));
            username.requestFocus();
            return;
        }
        if (lastname.getText().toString().isEmpty()){
            lastname.setError(getString(R.string.input_error_lastname));
            lastname.requestFocus();
            return;
        }
        if (firstname.getText().toString().isEmpty()){
            firstname.setError(getString(R.string.input_error_firstname));
            firstname.requestFocus();
            return;
        }
        if (password.getText().toString().isEmpty()){
            password.setError(getString(R.string.input_error_password));
            password.requestFocus();
            return;
        }
        if(email.getText().toString().isEmpty()){
            email.setError(getString(R.string.input_error_email));
            email.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()) {
            email.setError(getString(R.string.input_error_email_valid));
            email.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            // WE WILL STORE THE ADDITIONAL FIELDS IN FIREBASE DATABASE
                            User user = new User(firstname.getText().toString(),
                                                 lastname.getText().toString(),
                                                 username.getText().toString(),
                                                 password.getText().toString(),
                                                 email.getText().toString());


                            mDatabaseReference.child(mAuth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(MainActivity.this, R.string.input_error_registrationsuccessful,
                                                Toast.LENGTH_SHORT).show();
                                        //DEFINE THE NEXT ACTIVITY
                                        Intent intent = new Intent(MainActivity.this, Signin.class);
                                        //START THE ACTIVITY
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{
                                        // DISPLAY A FAILURE MESSAGE
                                        Toast.makeText(MainActivity.this,  R.string.input_error_registrationfailed,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    //ACTION TO THE TEXTVIEW AT THE BOTTOM OF THE ACTIVITY
    public void register_link(View view) {
        //START ACTIVITY
        startActivity(new Intent(this, Signin.class));
        finish();
    }


    //TO REDIRECT MENUS TO THE RIGHT PAGE THANKS TO THE ID OF THE ITEM OF THE NAVIGATION DRAWER
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_languages:
                startActivity(new Intent(MainActivity.this, Languages.class));
                return true;

            case R.id.item_about:
                startActivity(new Intent(MainActivity.this, About.class));
                return true;

            case R.id.item_logout:
                //THERE IS NO SENSE TO LOG OUT FROM THIS ACTIVITY
                Toast.makeText(this, R.string.input_error_cantlogoutfromhere, Toast.LENGTH_SHORT).show();
                return true;

            default:
                return false;
        }
    }

    // ADD LIKE THE REAL ANDROID PHONE WHEN YOU LEAVE AN APP BY PRESSING THE RETURN BUTTON.
    // YOU HAVE TO TAP TWICE TO EXIT THE APP
    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        // IF THE USER HAS ALREADY PRESS ONCE, YOU GO HERE
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory( Intent.CATEGORY_HOME );
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
            this.finish();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.input_error_back, Toast.LENGTH_SHORT).show();

        // IF THE USER WAIT MORE THAN 2 SECONDS, HE HAS TO TAP TWICE AGAIN
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }

        }, 2000);
    }
}