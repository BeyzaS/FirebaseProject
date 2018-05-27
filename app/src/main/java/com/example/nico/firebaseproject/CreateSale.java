package com.example.nico.firebaseproject;

import android.content.Intent;
import android.print.PrinterId;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nico.firebaseproject.Model.House;
import com.example.nico.firebaseproject.Model.Location;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CreateSale extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference mDatabaseReference= FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private Spinner spinner;
    private EditText description, m2, price, address;
    private AutoCompleteTextView autoCompleteTextView;
    private House house ;
    private Map <String, String> allTownsMapped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_sale);

        description = (EditText)findViewById(R.id.description_create);
        m2 = (EditText)findViewById(R.id.m2_create);
        price = (EditText)findViewById(R.id.price_create);
        address = (EditText)findViewById(R.id.address_create);
        autoCompleteTextView = (AutoCompleteTextView)findViewById(R.id.town_autocomplete);
        spinner = (Spinner) findViewById(R.id.type_create);

        //TO ADD THE NAVIGATION DRAWER TO THE ACTIVITY
        ((NavigationView) findViewById(R.id.navigationdrawer_createsale)).setNavigationItemSelectedListener(this);

        //CREATE AN ARRAYADAPTER USING THE STRING ARRAY AND A DEFAULT SPINNER LAYOUT
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.type_array, android.R.layout.simple_spinner_item);
        //SPECIFY THE LAYOUT TO USE WHEN THE LIST OF CHOICES APPEARS
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //APPLY THE ADAPTER TO THE SPINNER
        spinner.setAdapter(adapter);

        //THIS IS GOING TO ACCESS ONCE THE RIGHT NODE IN FIREBASE
        mDatabaseReference.child("locations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                allTownsMapped = new HashMap<String, String>();

                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    allTownsMapped.put(ds.getValue(Location.class).getTown(), ds.getKey());
                }

                //AUTOCOMPLETE TOWN LIST
                autoCompleteTextView.setAdapter(new ArrayAdapter<String>(CreateSale.this,
                        android.R.layout.simple_list_item_1, new ArrayList<String>(allTownsMapped.keySet())));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
            }
        });
    }

    public void create_house(View view) {

        // UPDATE THE REALTIME DATABASE
        if (description.getText().toString().isEmpty()){
            description.setError(getString(R.string.input_error_username));
            description.requestFocus();
            return;
        }
        if (m2.getText().toString().isEmpty()){
            m2.setError(getString(R.string.input_error_username));
            m2.requestFocus();
            return;
        }
        if (address.getText().toString().isEmpty()){
            address.setError(getString(R.string.input_error_username));
            address.requestFocus();
            return;
        }


        house = new House(spinner.getSelectedItem().toString(),
                            description.getText().toString(),
                            Integer.parseInt(m2.getText().toString()),
                            Integer.parseInt(price.getText().toString()),
                            address.getText().toString(),
                            allTownsMapped.get(autoCompleteTextView.getText().toString()),
                            user.getUid()
        );

        /*
        if (new ArrayList<String>(allTownsMapped.keySet()).contains(allTownsMapped.get(autoCompleteTextView.getText().toString())))
        {
        */
            mDatabaseReference.child("houses").child(UUID.randomUUID().toString()).setValue(house).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(CreateSale.this, R.string.input_error_creationsuccessful,
                                Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                    else{
                        // DISPLAY A FAILURE MESSAGE
                        Toast.makeText(CreateSale.this, R.string.input_error_creationfailed,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        /*
        else
        {
            autoCompleteTextView.requestFocus();
            Toast.makeText(CreateSale.this, "Please add the town before create the house", Toast.LENGTH_SHORT).show();
        }
    }
    */

/*
IL FAUT GARDER ÇA POUR APRÈS
        if(!HouseSellingDatabase.getInstance(this).locationDAO().getAllTown().contains(town)) {
            Toast.makeText(this,"Please add the town before create the house", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
*/

    //ACTION TO THE BUTTON "NEW TOWN"
    public void create_location(View view) {
        startActivity(new Intent(this, CreateLocation.class));
        finish();
    }

    //ACTION TO THE BUTTON "ALL TOWNS"
    public void consult_all_town(View view) {
        startActivity(new Intent(this, AllTowns.class));
        finish();
    }

    @Override
    //TO REDIRECT MENUS TO THE RIGHT PAGE THANKS TO THE ID OF THE ITEM OF THE NAVIGATION DRAWER
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_languages:
                startActivity(new Intent(CreateSale.this, Languages.class));
                return true;
            case R.id.item_about:
                startActivity(new Intent(CreateSale.this, About.class));
                return true;
            case R.id.item_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(CreateSale.this, Signin.class));
                this.finish();
                return true;
            default:
                return false;
        }
    }

    //ACTION TO THE BACK BUTTON
    public void onBackPressed() {
        startActivity(new Intent(this, ListOfActivity.class));
        finish();
    }
}