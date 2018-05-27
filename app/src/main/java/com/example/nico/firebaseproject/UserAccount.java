package com.example.nico.firebaseproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nico.firebaseproject.Model.House;
import com.example.nico.firebaseproject.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserAccount extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference mDatabaseReference= FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private User userUpdated ;
    private EditText lastname, username, email, firstname, password ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        lastname = (EditText)findViewById(R.id.lastname_account);
        username = (EditText)findViewById(R.id.username_account);
        email = (EditText)findViewById(R.id.email_account);
        firstname = (EditText)findViewById(R.id.firstname_account);
        password = (EditText)findViewById(R.id.password_account);

        //ADD THE NAVIGATION DRAWER
        ((NavigationView) findViewById(R.id.navigationdrawer_useraccount)).setNavigationItemSelectedListener(this);


        //THIS IS GOING TO ACCESS ONCE THE RIGHT NODE IN FIREBASE
        mDatabaseReference.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                //GET THE VALUES OF THE USER AND PUT THEM IT INTO THE EDITTEXTS
                lastname.setText(user.getLastname(), TextView.BufferType.EDITABLE);
                username.setText(user.getUsername(), TextView.BufferType.EDITABLE);
                email.setText(user.getEmail(), TextView.BufferType.EDITABLE);
                firstname.setText(user.getFirstname(), TextView.BufferType.EDITABLE);
                password.setText(user.getPassword(), TextView.BufferType.EDITABLE);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
            }
        });
    }


    //ACTION TO THE BUTTON SAVE
    public void saveuser(View view) {

        // UPDATE THE FIREBASE AUTHENTICATION
        if (user != null)
        {
           user.updateEmail(email.getText().toString());
           user.updatePassword(password.getText().toString());
        }

        // UPDATE THE REALTIME DATABASE
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

        userUpdated = new User(firstname.getText().toString(),
                                    lastname.getText().toString(),
                                    username.getText().toString(),
                                    password.getText().toString(),
                                    email.getText().toString());

        mDatabaseReference.child("users").child(user.getUid()).setValue(userUpdated).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(UserAccount.this, R.string.input_error_updatesuccessful,
                            Toast.LENGTH_SHORT).show();
                    //DEFINE THE NEXT ACTIVITY
                    onBackPressed();
                }
                else{
                    // DISPLAY A FAILURE MESSAGE
                    Toast.makeText(UserAccount.this, R.string.input_error_updatefailed,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void deleteuser(View view) {

        // DELETE USER IN REALTIME DATASE
        if (mDatabaseReference.child("users").child(user.getUid())!= null)
        {
            if (mDatabaseReference.child("houses").child(user.getUid())!= null)
            {
                mDatabaseReference.child("houses").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot ds : dataSnapshot.getChildren())
                        {
                            // IF THE HOUSE IS LINKED TO A LOCATION, THIS HOUSE WILL BE DELETED
                            if (ds.getValue(House.class).getIdUser().equals(user.getUid()))
                            {
                                Toast.makeText(UserAccount.this, R.string.houses_linked_to_user, Toast.LENGTH_SHORT).show();
                                ds.getRef().removeValue();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println(databaseError.getMessage());
                    }
                });
            }
            mDatabaseReference.child("users").child(user.getUid()).removeValue();
        }
        else
        {
            Toast.makeText(UserAccount.this, R.string.input_error_failedtodelete, Toast.LENGTH_SHORT).show();
        }

        // DELETE THE FIREBASE AUTHENTICATION
        FirebaseAuth.getInstance().signOut();
        user.delete();

        // GO BACK TO THE MAINACTIVITY
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    //TO REDIRECT MENUS TO THE RIGHT PAGE THANKS TO THE ID OF THE ITEM OF THE NAVIGATION DRAWER
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_languages:
                startActivity(new Intent(UserAccount.this, Languages.class));
                return true;

            case R.id.item_about:
                startActivity(new Intent(UserAccount.this, About.class));
                return true;

            case R.id.item_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(UserAccount.this, Signin.class));
                this.finish();
                return true;

            default:
                return false;
        }
    }

    //ACTION THE BACK BUTTON
    public void onBackPressed() {
        startActivity(new Intent(this, ListOfActivity.class));
        finish();
    }
}