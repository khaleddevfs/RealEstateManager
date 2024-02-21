package com.openclassrooms.realestatemanager.ui;



import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.fragments.DetailsFragment;
import com.openclassrooms.realestatemanager.fragments.MapFragment;
import com.openclassrooms.realestatemanager.injection.ViewModelFactory;
import com.openclassrooms.realestatemanager.models.RealEstate;
import com.openclassrooms.realestatemanager.viewModel.RealEstateViewModel;

import java.util.List;


public class MapActivity extends AppCompatActivity implements MapFragment.OnMarkerClickListener {

    private static final String TAG = "MapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Log.d(TAG, "onCreate: MapActivity started.");

        // Handle intent to display the map or a specific real estate
        handleIntent();
    }

    private void handleIntent() {
        RealEstate receivedEstate = getIntent().getParcelableExtra("realEstateData");
        if (receivedEstate != null) {
            displaySingleEstateOnMap(receivedEstate);
        } else {
            displayAllEstatesOnMap();
        }
    }
    private void displaySingleEstateOnMap(RealEstate realEstate) {
        Log.d(TAG, "displaySingleEstateOnMap: Preparing to display a single estate.");

        MapFragment mapFragment = MapFragment.newInstance(realEstate);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.map_container, mapFragment)
                .commit();
    }

    private void displayAllEstatesOnMap() {
        Log.d(TAG, "displayAllEstatesOnMap: Fetching all estates to display on map.");

        RealEstateViewModel realEstateViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance(this)).get(RealEstateViewModel.class);
        realEstateViewModel.getRealEstates().observe(this, this::getEstatesObserver);
    }

    private void getEstatesObserver(List<RealEstate> realEstateList) {
        Log.d(TAG, "getEstatesObserver: Received real estate list for map display.");

        MapFragment mapFragment = MapFragment.newInstance(realEstateList);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.map_container, mapFragment)
                .commit();
    }

    @Override
    public void onMarkerClick(RealEstate realEstate) {
        Log.d(TAG, "onMarkerClick: Marker clicked for estate: " + realEstate.getID());
        goToDetailsFragment(realEstate);
    }

    private void goToDetailsFragment(RealEstate realEstate) {
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet) {
            // For tablets, send an intent to MainActivity with RealEstate data
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("ACTION", "SHOW_DETAILS");
            intent.putExtra("REAL_ESTATE", realEstate);
            startActivity(intent);
        } else {
            // For phones, start MainActivity with specific instructions
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("ACTION", "SHOW_DETAILS");
            intent.putExtra("REAL_ESTATE", realEstate);
            startActivity(intent);
        }
        }
    }

