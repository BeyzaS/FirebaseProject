package com.example.nico.firebaseproject.Model;

import android.support.annotation.NonNull;
import com.google.firebase.database.Exclude;


public class Location {

    @NonNull
    private String description;

    @NonNull
    private String town;

    @NonNull
    private boolean hasSwimmingPool;

    @NonNull
    private boolean hasCinema;

    @NonNull
    private boolean hasSportCenter;

    // For the method getValue(Location.class)
    public Location (){}

    public Location(String description, String town, boolean hasSwimmingPool, boolean hasCinema, boolean hasSportCenter) {
        this.description = description;
        this.town = town;
        this.hasSwimmingPool = hasSwimmingPool;
        this.hasCinema = hasCinema;
        this.hasSportCenter = hasSportCenter;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public boolean isHasSwimmingPool() {
        return hasSwimmingPool;
    }

    public void setHasSwimmingPool(boolean hasSwimmingPool) {
        this.hasSwimmingPool = hasSwimmingPool;
    }

    public boolean isHasCinema() {
        return hasCinema;
    }

    public void setHasCinema(boolean hasCinema) {
        this.hasCinema = hasCinema;
    }

    public boolean isHasSportCenter() {
        return hasSportCenter;
    }

    public void setHasSportCenter(boolean hasSportCenter) {
        this.hasSportCenter = hasSportCenter;
    }

    @Override
    public String toString() {

        String swimmingpool;
        if(hasSwimmingPool)
        {
            swimmingpool = "Has swimming pool";
        }
        else{
            swimmingpool = "No swimming pool";
        }

        String cinema;
        if(hasCinema)
        {
            cinema = "Has cinema";
        }
        else{
            cinema = "No cinema";
        }

        String sportcenter;
        if(hasSportCenter)
        {
            sportcenter = "Has sport center";
        }
        else{
            sportcenter = "No sport center";
        }

        return
                description +
                "\n" + town +
                        "\n" + swimmingpool +
                        "\n" + cinema +
                        "\n" + sportcenter;
    }
}