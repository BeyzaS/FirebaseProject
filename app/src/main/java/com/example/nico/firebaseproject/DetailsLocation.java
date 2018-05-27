package com.example.nico.firebaseproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nico.firebaseproject.Model.House;
import com.example.nico.firebaseproject.Model.Location;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class DetailsLocation extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference mDatabaseReference= FirebaseDatabase.getInstance().getReference();
    private Location locationSelected, locationUpdated ;
    private EditText description, town ;
    private CheckBox cbswimmingpool, cbcinema, cbsportcenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_location);

        description = (EditText) findViewById(R.id.description_details_location);
        town = (EditText) findViewById(R.id.town_details_location);
        cbswimmingpool = (CheckBox)findViewById(R.id.hasswimmingpool_details_location);
        cbcinema = (CheckBox)findViewById(R.id.hascinema_details_location);
        cbsportcenter = (CheckBox)findViewById(R.id.hassportcenter_details_location);

        ((NavigationView) findViewById(R.id.navigationdrawer_detailslocation)).setNavigationItemSelectedListener(this);

        //THIS IS GOING TO ACCESS ONCE THE RIGHT NODE IN FIREBASE
        mDatabaseReference.child("locations").child(getIntent().getExtras().getString("LocationUUID")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                locationSelected = dataSnapshot.getValue(Location.class);

                //GET THE VALUES OF THE HOUSE AND PUT THEM IT INTO THE EDITTEXTS
                description.setText(locationSelected.getDescription(), TextView.BufferType.EDITABLE);
                town.setText(String.valueOf(locationSelected.getTown()), TextView.BufferType.EDITABLE);

                //HERE YOU SET CHECKED TRUE OR NOT (DEPENDS ON WHAT THE USER HAS INSERTED ON "CREATE LOCATION")
                cbswimmingpool.setChecked(locationSelected.isHasSwimmingPool());
                cbcinema.setChecked(locationSelected.isHasCinema());
                cbsportcenter.setChecked(locationSelected.isHasSportCenter());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
            }
        });
 }

    public void savelocation(View view) {

        // UPDATE THE REALTIME DATABASE
        if (description.getText().toString().isEmpty()){
            description.setError(getString(R.string.input_error_description));
            description.requestFocus();
            return;
        }
        if (town.getText().toString().isEmpty()){
            town.setError(getString(R.string.input_error_town));
            town.requestFocus();
            return;
        }

        locationUpdated = new Location(description.getText().toString(),
                                        town.getText().toString(),
                                        onCheckBoxClickedSwimmingPool2(view),
                                        onCheckBoxClickedCinema2(view),
                                        onCheckBoxClickedSportCenter2(view));

        mDatabaseReference.child("locations").child(getIntent().getExtras().getString("LocationUUID")).setValue(locationUpdated).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(DetailsLocation.this, R.string.input_error_updatesuccessful,
                            Toast.LENGTH_SHORT).show();
                    //WHEN UPDATING DATABASE IS FINISHED, GO TO THE PREVIOUS ACTIVITY
                    onBackPressed();
                }
                else{
                    // DISPLAY A FAILURE MESSAGE
                    Toast.makeText(DetailsLocation.this, R.string.input_error_updatefailed,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //VERIFY IF THE CHECKBOX IS CHECKED OR NOT
    public boolean onCheckBoxClickedSwimmingPool2(View view)
    {
        return cbswimmingpool.isChecked();
    }
    public boolean onCheckBoxClickedCinema2(View view)
    {
        return cbcinema.isChecked();
    }
    public boolean onCheckBoxClickedSportCenter2(View view)
    {
        return cbsportcenter.isChecked();
    }


    public void deletelocation(View view) {

        // DELETE LOCATION AND ALL HOUSES THAT ARE IN THIS LOCATION IN REALTIME DATASE !!!!!
        if (mDatabaseReference.child("locations").child(getIntent().getExtras().getString("LocationUUID")) != null)
        {
            mDatabaseReference.child("houses").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        // IF THE HOUSE IS LINKED TO A LOCATION, THIS HOUSE WILL BE DELETED
                        if (ds.getValue(House.class).getIdLocation().equals(getIntent().getExtras().getString("LocationUUID")))
                        {
                            Toast.makeText(DetailsLocation.this, R.string.houses_linked_to_user, Toast.LENGTH_SHORT).show();
                            ds.getRef().removeValue();
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println(databaseError.getMessage());
                }
            });
            mDatabaseReference.child("locations").child(getIntent().getExtras().getString("LocationUUID")).removeValue();
        }
        else
        {
            Toast.makeText(DetailsLocation.this, R.string.input_error_failedtodelete, Toast.LENGTH_SHORT).show();
        }
        onBackPressed();
    }


    @Override
    //TO REDIRECT MENUS TO THE RIGHT PAGE THANKS TO THE ID OF THE ITEM OF THE NAVIGATION DRAWER
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_languages:
                startActivity(new Intent(DetailsLocation.this, Languages.class));
                return true;

            case R.id.item_about:
                startActivity(new Intent(DetailsLocation.this, About.class));
                return true;

            case R.id.item_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(DetailsLocation.this, Signin.class));
                this.finish();
                return true;

            default:
                return false;
        }
    }

    //ACTION TO THE BACK BUTTON
    public void onBackPressed() {
        startActivity(new Intent(this, AllTowns.class));
        finish();
    }
}