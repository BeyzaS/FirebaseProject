package com.example.nico.firebaseproject.Model;

import android.support.annotation.NonNull;

import com.example.nico.firebaseproject.R;
import com.google.firebase.database.Exclude;


public class House {

    @NonNull
    private String type;

    @NonNull
    private String description;

    @NonNull
    private int squareMeter;

    @NonNull
    private int price;

    @NonNull
    private String address;

    @NonNull
    private String idLocation;

    @NonNull
    private String idUser;

    // For the method getValue(House.class)
    public House (){}

    public House(String type, String description, int squareMeter, int price, String address, String idLocation, String idUser) {
        this.type = type;
        this.description = description;
        this.squareMeter = squareMeter;
        this.price = price;
        this.address = address;
        this.idLocation = idLocation;
        this.idUser = idUser;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSquareMeter() {
        return squareMeter;
    }

    public void setSquareMeter(int squareMeter) {
        this.squareMeter = squareMeter;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIdLocation() {
        return idLocation;
    }

    public void setIdLocation(String idLocation) {
        this.idLocation = idLocation;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }


    public String toString() {
        return  type +
                "\n"+ "Description: "  + description +
                "\n"+ "M2: " + squareMeter +
                "\n"+ "Price: " + price +
                "\n"+ "Address: " + address + "\n";
    }
}