package com.example.nico.firebaseproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nico.firebaseproject.Model.House;
import com.example.nico.firebaseproject.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetailsHouse extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference mDatabaseReference= FirebaseDatabase.getInstance().getReference();
    private House houseSelected, houseUpdated ;
    private EditText description, m2, price, address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_house);

        description = (EditText)findViewById(R.id.description_details_house);
        m2 = (EditText)findViewById(R.id.m2_details_house);
        price = (EditText)findViewById(R.id.price_details_house);
        address = (EditText)findViewById(R.id.address_details_house);

        //TO ADD THE NAVIGATION DRAWER TO THE ACTIVITY
        ((NavigationView) findViewById(R.id.navigationdrawer_detailshouse)).setNavigationItemSelectedListener(this);

        //THIS IS GOING TO ACCESS ONCE THE RIGHT NODE IN FIREBASE
        mDatabaseReference.child("houses").child(getIntent().getExtras().getString("HouseUUID")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                houseSelected = dataSnapshot.getValue(House.class);

                //GET THE VALUES OF THE HOUSE AND PUT THEM IT INTO THE EDITTEXTS
                description.setText(houseSelected.getDescription(), TextView.BufferType.EDITABLE);
                m2.setText(String.valueOf(houseSelected.getSquareMeter()), TextView.BufferType.EDITABLE);
                price.setText(String.valueOf(houseSelected.getPrice()), TextView.BufferType.EDITABLE);
                address.setText(houseSelected.getAddress(), TextView.BufferType.EDITABLE);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
            }
        });
    }

    //ACTION TO THE BUTTON "SAVE"
    public void savehouse(View view) {

        // UPDATE THE REALTIME DATABASE
        if (description.getText().toString().isEmpty()){
            description.setError(getString(R.string.input_error_description));
            description.requestFocus();
            return;
        }
        if (m2.getText().toString().isEmpty()){
            m2.setError(getString(R.string.input_error_m2));
            m2.requestFocus();
            return;
        }
        if (price.getText().toString().isEmpty()){
            price.setError(getString(R.string.input_error_price));
            price.requestFocus();
            return;
        }
        if (address.getText().toString().isEmpty()) {
            address.setError(getString(R.string.input_error_address));
            address.requestFocus();
            return;
        }

        houseUpdated = new House (houseSelected.getType(),
                                    description.getText().toString(),
                                    Integer.parseInt(m2.getText().toString()),
                                    Integer.parseInt(price.getText().toString()),
                                    address.getText().toString(),
                                    houseSelected.getIdLocation(),
                                    houseSelected.getIdUser()
                                 );

        mDatabaseReference.child("houses").child(getIntent().getExtras().getString("HouseUUID")).setValue(houseUpdated).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(DetailsHouse.this, R.string.input_error_updatesuccessful,
                            Toast.LENGTH_SHORT).show();
                    //WHEN UPDATING DATABASE IS FINISHED, GO TO THE PREVIOUS ACTIVITY
                    onBackPressed();
                }
                else{
                    // DISPLAY A FAILURE MESSAGE
                    Toast.makeText(DetailsHouse.this, R.string.input_error_updatefailed,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //ACTION TO THE BUTTON "DELETE"
    public void deletehouse(View view) {

        // DELETE USER IN REALTIME DATASE
        mDatabaseReference.child("houses").child(getIntent().getExtras().getString("HouseUUID")).removeValue();
        onBackPressed();
    }

    @Override
    //TO REDIRECT MENUS TO THE RIGHT PAGE THANKS TO THE ID OF THE ITEM OF THE NAVIGATION DRAWER
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_languages:
                startActivity(new Intent(DetailsHouse.this, Languages.class));
                return true;

            case R.id.item_about:
                startActivity(new Intent(DetailsHouse.this, About.class));
                return true;

            case R.id.item_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(DetailsHouse.this, Signin.class));
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