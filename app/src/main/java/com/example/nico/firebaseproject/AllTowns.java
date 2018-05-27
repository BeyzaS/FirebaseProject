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
import android.widget.ListView;

import com.example.nico.firebaseproject.Model.House;
import com.example.nico.firebaseproject.Model.Location;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllTowns extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference mDatabaseReference= FirebaseDatabase.getInstance().getReference();
    private Map<Location, String> location_map_with_uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_towns);

        //TO ADD THE NAVIGATION DRAWER TO THE ACTIVITY
        ((NavigationView) findViewById(R.id.navigationdrawer_alltowns)).setNavigationItemSelectedListener(this);

        //THIS IS GOING TO ACCESS ONCE THE RIGHT NODE IN FIREBASE, AND MAP ALL LOCATIONS ONE BY ONE WITH A UNIQUE UUID
        mDatabaseReference.child("locations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                location_map_with_uuid = new HashMap<Location, String>();

                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    location_map_with_uuid.put(ds.getValue(Location.class), ds.getKey());
                }

                //PUT THE HOUSES INTO THE LISTVIEW
                ListView listView = (ListView)findViewById(R.id.list);
                listView.setAdapter(new ArrayAdapter<Location>(AllTowns.this,android.R.layout.simple_list_item_1,
                        new ArrayList<Location>(location_map_with_uuid.keySet())));

                //WE NEED TO GET THE THE HOUSE SELECTED, AND PASS HIS UUID TO THE NEXT ACTIVITY
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(AllTowns.this, DetailsLocation.class);
                        intent.putExtra("LocationUUID", location_map_with_uuid.get((Location) parent.getAdapter().getItem(position)));
                        startActivity(intent);
                        finish();
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
            }
        });
    }

    //TO REDIRECT MENUS TO THE RIGHT PAGE THANKS TO THE ID OF THE ITEM OF THE NAVIGATION DRAWER
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_languages:
                startActivity(new Intent(AllTowns.this, Languages.class));
                return true;

            case R.id.item_about:
                startActivity(new Intent(AllTowns.this, About.class));
                return true;

            case R.id.item_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(AllTowns.this, Signin.class));
                this.finish();
                return true;

            default:
                return false;
        }
    }

    //ACTION TO THE BACK BUTTON
    public void onBackPressed(){
        startActivity(new Intent(this, CreateSale.class));
        //IF AN ACTIVITY IS PAUSED OR STOPPED, THE SYSTEM CAN DROP THE ACTIVITY FROM MEMORY BY
        //EITHER ASKING IT TO FINISH, OR SIMPLY KILLING ITS PROCESS. WHEN IT IS DISPLAYED AGAIN
        //TO THE USER, IT MUST BE COMPLETELY RESTARTED AND RESTORED TO ITS PREVIOUS STATE.
        finish();
    }
}