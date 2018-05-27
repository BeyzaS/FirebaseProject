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
import android.widget.Toast;

import com.example.nico.firebaseproject.Model.House;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListOfActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference mDatabaseReference= FirebaseDatabase.getInstance().getReference();
    private Map <House, String> house_map_with_uuid;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of);

        //TO ADD THE NAVIGATION DRAWER TO THE ACTIVITY
        ((NavigationView) findViewById(R.id.navigationdrawer_listofactivity)).setNavigationItemSelectedListener(this);

        //THIS IS GOING TO ACCESS ONCE THE RIGHT NODE IN FIREBASE, AND MAP ALL HOUSES ONE BY ONE WITH A UNIQUE UUID
        mDatabaseReference.child("houses").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                house_map_with_uuid = new HashMap<House, String>();

                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    if(ds.getValue(House.class).getIdUser().trim().equals(user.getUid().trim()))
                    {
                        house_map_with_uuid.put(ds.getValue(House.class), ds.getKey());
                    }
                }

                //PUT THE HOUSES INTO THE LISTVIEW
                ListView listView = (ListView)findViewById(R.id.listview_houses);
                listView.setAdapter(new ArrayAdapter<House>(ListOfActivity.this,android.R.layout.simple_list_item_1,
                        new ArrayList<House>(house_map_with_uuid.keySet())));

                //WE NEED TO GET THE THE HOUSE SELECTED, AND PASS HIS UUID TO THE NEXT ACTIVITY
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(ListOfActivity.this, DetailsHouse.class);
                        intent.putExtra("HouseUUID", house_map_with_uuid.get((House)parent.getAdapter().getItem(position)));
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

    //ACTION TO THE FLOATING BUTTON "+"
    public void create_sale(View view) {
        startActivity(new Intent(this, CreateSale.class));
    }

    //ACTION TO THE IMAGEVIEW THAT SYMBOLIZE THE USER ACCOUNT
    public void useraccount_update(View view) {
        startActivity(new Intent(this, UserAccount.class));
    }

    @Override
    //TO REDIRECT MENUS TO THE RIGHT PAGE THANKS TO THE ID OF THE ITEM OF THE NAVIGATION DRAWER
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_languages:
                startActivity(new Intent(ListOfActivity.this, Languages.class));
                return true;

            case R.id.item_about:
                startActivity(new Intent(ListOfActivity.this, About.class));
                return true;

            case R.id.item_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ListOfActivity.this, Signin.class));
                this.finish();
                return true;

            default:
                return false;
        }
    }

    // THE USER HAS TO LOGOUT TO GO TO THE PAGE SIGN IN
    public void onBackPressed() {
        Toast.makeText(this, R.string.input_error_pleaselogout, Toast.LENGTH_SHORT).show();
    }
}