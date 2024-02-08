package com.openclassrooms.realestatemanager.ui;


import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;


import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.navigation.NavigationView;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.RealEstateEditor;
import com.openclassrooms.realestatemanager.adapters.RealEstateAdapter;
import com.openclassrooms.realestatemanager.adapters.RealEstateViewHolder;
import com.openclassrooms.realestatemanager.database.RealEstateDao;
import com.openclassrooms.realestatemanager.database.SaveRealEstateDB;
import com.openclassrooms.realestatemanager.fragments.DetailsFragment;
import com.openclassrooms.realestatemanager.fragments.ListFragment;
import com.openclassrooms.realestatemanager.injection.ViewModelFactory;
import com.openclassrooms.realestatemanager.utils.Utils;
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding;
import com.openclassrooms.realestatemanager.models.RealEstate;
import com.openclassrooms.realestatemanager.viewModel.RealEstateViewModel;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    private RealEstateViewModel viewModel;
    private final List<RealEstate> realEstateList = new ArrayList<>();
    private RealEstate realEstate;
    private boolean filtered = false;
    private boolean shouldObserve = true;

    private boolean isTwoPaneLayout; // Pour détecter si l'appareil est une tablette


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initViewModel();
        configureUI();
        SyncDB();
        // processIntent(getIntent());


        // Déterminer si l'appareil est une tablette
        isTwoPaneLayout = getResources().getBoolean(R.bool.isTablet);

        if (isTwoPaneLayout) {
            // Logique pour la tablette
            setupTabletView();
        } else {
            // Logique pour le téléphone
            setupPhoneView();
        }

        // Configurer le bouton FAB pour ajouter un nouveau RealEstate
        setupFabButton();
/*
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_frame_layout, ListFragment.newInstance(realEstateList))
                .commit();

        Log.d("lodi", "go to ListFragment");*/

    }


    private void setupTabletView() {
        // Afficher ListFragment et DetailsFragment côte à côte
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.list_fragment_container, ListFragment.newInstance())
                .replace(R.id.details_fragment_container, new DetailsFragment())
                .commit();
    }

    private void setupPhoneView() {
        // Afficher uniquement ListFragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_frame_layout, ListFragment.newInstance())
                .commit();
    }

    private void setupFabButton() {
        binding.fabAddRealEstate.setOnClickListener(view -> createNewRealEstate());
    }

    private void createNewRealEstate() {
        Intent intent = new Intent(this, RealEstateEditor.class);
        editRealEstateLauncher.launch(intent);
        Log.d("lodi", "createNewRealEstate");
    }


    private void initView() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance(this.getApplicationContext())).get(RealEstateViewModel.class);

    }


    private void configureUI() {
        this.configureToolbar();
        configureNavigationView();
        this.configureDrawerLayout();
    }


    private void configureToolbar() {
        setSupportActionBar(binding.mainToolbar);
    }

    private void configureDrawerLayout() {

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.mainDrawerLayout, binding.mainToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorWhite));
        binding.mainDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }
/*
    private void processIntent(Intent intent) {
        if (isFilteredIntent(intent)) {
            handleFilteredEstates(intent);
        }  // observeRealEstates();

    }

    private boolean isFilteredIntent(Intent intent) {
        return intent.getParcelableArrayListExtra("filteredEstates") != null;
    }

    private void handleFilteredEstates(Intent intent) {
        realEstateList.clear();
        realEstateList.addAll(intent.getParcelableArrayListExtra("filteredEstates"));
        Log.d("TAG", "FILTERED ");
      //  updateEstates();
        filtered = true;
    }

    private void updateDetailViewVisibility() {
        boolean isTablet = Utils.isDeviceTablet(getApplicationContext());
        binding.mainFrameLayout.setVisibility(isTablet ? View.VISIBLE : View.GONE);
        //binding.noResultsTextView.setVisibility(isTablet && realEstateList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void syncDatabase() {
        // Logic for SyncDB
    }






*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem searchMenuItem;
        if(Utils.isDeviceTablet(this)) {
            getMenuInflater().inflate(R.menu.main_menu_tablet, menu);

        }
        else {
            getMenuInflater().inflate(R.menu.main_menu_phone, menu);

        }

        searchMenuItem = menu.findItem(R.id.menu_search_button);

        if(filtered)
        {
            searchMenuItem.setIcon(R.drawable.baseline_close_24);
        }
        else
            searchMenuItem.setIcon(R.drawable.baseline_search_24);

        return super.onCreateOptionsMenu(menu);
    }








    private void configureNavigationView() {
        binding.navView.setNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.drawer_menu_map_button) {

            Intent intent = new Intent(MainActivity.this, MapActivity.class);

            startActivity(intent);
        } else if (id == R.id.drawer_menu_simulation_button) {
            startActivity(new Intent(this, SimulationActivity.class));
            Log.d("setting activity ok", "setting on");
        }
            this.binding.mainDrawerLayout.closeDrawer(GravityCompat.START);
            return true;

    }

        private void handleRealEstateClick (RealEstate estate){
            this.realEstate = estate;
            if (binding.mainFrameLayout.getVisibility() == View.VISIBLE) {
                showDetailsFragmentInPane();
            } else {
                navigateToSupportActivity();
            }
        }

        private void navigateToSupportActivity () {
        }


        private void showDetailsFragmentInPane () {
            Bundle bundle = new Bundle();
            bundle.putParcelable("REAL_ESTATE", realEstate);
            DetailsFragment fragment = new DetailsFragment();
            fragment.setArguments(bundle);
            configureToolbar();
            getSupportFragmentManager().beginTransaction()
                    .replace(binding.mainFrameLayout.getId(), fragment)
                    .commit();


        }


        private void searchRealEstate () {
            if (filtered) {
                startActivity(new Intent(this, MainActivity.class));
            } else {
                //SearchModal searchModal = new SearchModal();
                //searchModal.show(getSupportFragmentManager(), "searchModal");
            }
        }




        private final ActivityResultLauncher<Intent> editRealEstateLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                                RealEstate editedEstate = result.getData().getParcelableExtra("EDITED_REAL_ESTATE");

                                // Mettre à jour ou ajouter de nouveaux médias
                                viewModel.addNewMedia(editedEstate, editedEstate.getID());

                                // Mettre à jour l'URL du média en vedette si nécessaire
                                // viewModel.updateEstateFeaturedMediaUrl(ancienneUrl, nouvelleUrl);

                                // Mise à jour de l'objet RealEstate dans votre liste locale si nécessaire
                                // Vous devez trouver l'objet RealEstate correspondant dans votre liste et le mettre à jour
                                updateLocalRealEstateList(editedEstate);
                            }
                        });

        private void updateLocalRealEstateList (RealEstate editedEstate){
            for (int i = 0; i < realEstateList.size(); i++) {
                if (realEstateList.get(i).getID() == editedEstate.getID()) {
                    realEstateList.set(i, editedEstate);
                    break;
                }
            }
        }


    private void SyncDB() {
        if (Utils.isInternetAvailable(this)) {
            SaveRealEstateDB db = SaveRealEstateDB.getInstance(this); // Remplacez par votre méthode singleton
            RealEstateDao realEstateDao = db.realEstateDao();
            int totalEstates = realEstateList.size();
            int currentEstateIndex = 0;

            for (RealEstate estate : realEstateList) {
                if (!estate.getSync()) {
                    try {
                        // Créer ou mettre à jour le bien immobilier
                        long result = realEstateDao.createOrUpdateRealEstate(estate);

                        // Si result est > 0, la sauvegarde ou mise à jour a réussi
                        if (result > 0) {
                            estate.setSync(true);
                        } else {
                            estate.setSync(false);
                        }

                    } catch (Exception e) {
                        estate.setSync(false);
                    }

                    currentEstateIndex++;
                    if (currentEstateIndex == totalEstates) {
                        shouldObserve = true;
                    }
                }
            }
        }
    }



}

