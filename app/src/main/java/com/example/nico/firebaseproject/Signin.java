package com.example.nico.firebaseproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nico.firebaseproject.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class Signin extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference mDatabaseReference= FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth;
    private EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mAuth = FirebaseAuth.getInstance();
        email = (EditText)findViewById(R.id.email_signin);
        password = (EditText)findViewById(R.id.password_signin);

        //TO ADD THE NAVIGATION DRAWER TO THE ACTIVITY
        ((NavigationView) findViewById(R.id.navigationdrawer_signin)).setNavigationItemSelectedListener(this);
    }

    public void sign_in(View view) {

        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(Signin.this, R.string.loginsuccessful, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Signin.this, ListOfActivity.class));
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Signin.this, R.string.loginfailed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    //WHEN THE SELLER CLICK ON THE TEXTVIEW "Dont' have an account yet ? ...",
    // HE IS REDIRECTED TO THE MAIN ACTIVITY (REGISTER)
    public void sign_in_link(View view) {
       onBackPressed();
    }

    @Override
    //TO REDIRECT MENUS TO THE RIGHT PAGE THANKS TO THE ID OF THE ITEM OF THE NAVIGATION DRAWER
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_languages:
                startActivity(new Intent(Signin.this, Languages.class));
                return true;

            case R.id.item_about:
                startActivity(new Intent(Signin.this, About.class));
                return true;

            case R.id.item_logout:
                //THERE IS NO SENSE TO LOG OUT FROM THIS ACTIVITY
                Toast.makeText(this, R.string.input_error_cantlogoutfromhere, Toast.LENGTH_SHORT).show();
                this.finish();
                return true;

            default:
                return false;
        }
    }

    //ACTION TO THE BACK BUTTON
    public void onBackPressed() {
        startActivity(new Intent(Signin.this, MainActivity.class));
        this.finish();
    }
}