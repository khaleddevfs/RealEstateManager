package com.openclassrooms.realestatemanager.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.fragments.MapFragment;
import com.openclassrooms.realestatemanager.injection.ViewModelFactory;
import com.openclassrooms.realestatemanager.models.RealEstate;
import com.openclassrooms.realestatemanager.viewModel.RealEstateViewModel;

import java.util.List;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


       // RealEstateViewModel realEstateViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance(this)).get(RealEstateViewModel.class);
     //   realEstateViewModel.getRealEstates().observe(this,this::getEstatesObserver);

        RealEstate receivedEstate = getIntent().getParcelableExtra("realEstateData");

        if(receivedEstate != null) {
            // Si un RealEstate spécifique est reçu, affichez-le sur la carte
            displaySingleEstateOnMap(receivedEstate);
        } else {
            // Sinon, affichez tous les biens immobiliers
            displayAllEstatesOnMap();
        }
    }

    private void displaySingleEstateOnMap(RealEstate realEstate) {
        MapFragment mapFragment = MapFragment.newInstance(realEstate);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.map_container, mapFragment)
                .commit();
    }

    private void displayAllEstatesOnMap() {
        RealEstateViewModel realEstateViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance(this)).get(RealEstateViewModel.class);
        realEstateViewModel.getRealEstates().observe(this, this::getEstatesObserver);
    }

    private void getEstatesObserver(List<RealEstate> realEstateList) {
        MapFragment mapFragment = MapFragment.newInstance(realEstateList);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.map_container, mapFragment)
                .commit();
    }
}
