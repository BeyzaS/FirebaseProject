package com.example.nico.firebaseproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nico.firebaseproject.Model.Location;
import com.example.nico.firebaseproject.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class CreateLocation extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    private EditText description, town ;
    private CheckBox cbswimmingpool, cbcinema, cbsportcenter;
    private Location location ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_location);

        description = (EditText)findViewById(R.id.description_newlocation);
        town = (EditText)findViewById(R.id.town_newlocation);
        cbswimmingpool = (CheckBox) findViewById(R.id.hasswimmingpool_newlocation);
        cbcinema = (CheckBox) findViewById(R.id.hascinema_newlocation);
        cbsportcenter = (CheckBox) findViewById(R.id.hassportcenter_newlocation);

        //TO ADD THE NAVIGATION DRAWER TO THE ACTIVITY
        ((NavigationView) findViewById(R.id.navigationdrawer_createlocation)).setNavigationItemSelectedListener(this);
    }

    public void insert_new_location(View view) {

        if (description.getText().toString().isEmpty()) {
            description.setError(getString(R.string.input_error_description));
            description.requestFocus();
            return;
        }

        if (town.getText().toString().isEmpty()) {
            town.setError(getString(R.string.input_error_town));
            town.requestFocus();
            return;
        }

        location = new Location (description.getText().toString(),
                                    town.getText().toString(),
                                    onCheckBoxClickedSwimmingPool(view),
                                    onCheckBoxClickedCinema(view),
                                    onCheckBoxClickedSportCenter(view));

        mDatabaseReference.child("locations").child(UUID.randomUUID().toString()).setValue(location).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(CreateLocation.this, R.string.input_error_creationsuccessful,
                            Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
                else{
                    // DISPLAY A FAILURE MESSAGE
                    Toast.makeText(CreateLocation.this, R.string.input_error_creationfailed,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //VERIFY IF THE CHECKBOX IS CHECKED OR NOT
    public boolean onCheckBoxClickedSwimmingPool(View view)
    {
        return cbswimmingpool.isChecked();
    }
    public boolean onCheckBoxClickedCinema(View view)
    {
        return cbcinema.isChecked();
    }
    public boolean onCheckBoxClickedSportCenter(View view)
    {
        return cbsportcenter.isChecked();
    }


    //TO REDIRECT MENUS TO THE RIGHT PAGE THANKS TO THE ID OF THE ITEM OF THE NAVIGATION DRAWER
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_languages:
                startActivity(new Intent(CreateLocation.this, Languages.class));
                return true;

            case R.id.item_about:
                startActivity(new Intent(CreateLocation.this, About.class));
                return true;

            case R.id.item_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(CreateLocation.this, Signin.class));
                this.finish();
                return true;

            default:
                return false;
        }
    }

    //ACTION TO THE BACK BUTTON
    public void onBackPressed() {
        startActivity(new Intent(CreateLocation.this, CreateSale.class));
        finish();
    }
}