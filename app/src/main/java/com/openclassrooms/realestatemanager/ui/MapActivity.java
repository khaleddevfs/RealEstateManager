package com.openclassrooms.realestatemanager.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
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

    @Override
    public void onMarkerClick(RealEstate estate) {
        // Logique pour afficher les détails du bien immobilier. Par exemple :
        goToDetailsFragment(estate);
    }

    private void goToDetailsFragment(RealEstate estate) {
        Log.d("MapActivity", "Attempting to go to details fragment for: " + estate.getName());

        DetailsFragment detailFragment = DetailsFragment.newInstance(estate);

        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        Log.d("MapActivity", "Is tablet: " + isTablet);

        int containerId = isTablet ? R.id.details_fragment_container : R.id.map_container; // Assurez-vous que c'est map_container ici
        Log.d("MapActivity", "Container ID: " + containerId);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Remplacez simplement par detailFragment dans le conteneur approprié
        transaction.replace(containerId, detailFragment);
        if (!isTablet) {
            // Ajoutez la transaction à la pile arrière uniquement pour les téléphones
          transaction.addToBackStack(null);
        }
        transaction.commit();
    }
}
