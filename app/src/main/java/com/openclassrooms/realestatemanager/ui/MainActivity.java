package com.openclassrooms.realestatemanager.ui;



import android.annotation.SuppressLint;
import android.app.Activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
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
import com.openclassrooms.realestatemanager.fragments.MapFragment;
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


    private final int NOTIFICATION_ID = 1;



    private final ActivityResultLauncher<Intent> editRealEstateLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            RealEstate editedEstate = result.getData().getParcelableExtra("EDITED_REAL_ESTATE");

                            // Mettre à jour ou ajouter de nouveaux médias
                            viewModel.addNewMedia(editedEstate, editedEstate.getID());

                            Log.d("lodi", "editRealEstateLauncher");

                            updateLocalRealEstateList(editedEstate);
                        }
                        showRealEstateCreatedNotification();
                    });






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initViewModel();
        configureUI();
        SyncDB();
        createNotificationChannel();





        // Déterminer si l'appareil est une tablette
        isTwoPaneLayout = getResources().getBoolean(R.bool.isTablet);

        if (isTwoPaneLayout) {
            // Logique pour la tablette
            setupTabletView();
        } else {
            // Logique pour le téléphone
            setupPhoneView();
        }




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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(Utils.isDeviceTablet(this)) {
            getMenuInflater().inflate(R.menu.main_menu_tablet, menu);
        } else {
            getMenuInflater().inflate(R.menu.main_menu_phone, menu);
        }

        MenuItem searchMenuItem = menu.findItem(R.id.menu_search_button);



        if(filtered) {
            searchMenuItem.setIcon(R.drawable.baseline_close_24);
        } else {
            searchMenuItem.setIcon(R.drawable.baseline_search_24);
        }

        // Configurer l'écouteur de clic sur l'élément de menu de recherche
        searchMenuItem.setOnMenuItemClickListener(item -> {
            // Appeler searchRealEstate ici
            searchRealEstate();
            return true; // Retourner true indique que le clic sur l'élément de menu a été géré
        });


        // Ajouter un écouteur pour menu_add_button
        MenuItem addMenuItem = menu.findItem(R.id.menu_add_button);
        addMenuItem.setOnMenuItemClickListener(item -> {
            // Naviguer vers createRealEstate ici
              createNewRealEstate();
            return true; // Indique que le clic sur l'élément de menu a été géré
        });

        return super.onCreateOptionsMenu(menu);
    }





/*
    //----- Bottom Navigation -----

    private void configureBottomView() {
        if (binding.mainBottomNavigationView != null) {
            binding.mainBottomNavigationView.setOnNavigationItemReselectedListener(item -> onBottomNavigation(item.getItemId()));
        } else {
            Log.e("MainActivity", "BottomNavigationView is null. Unable to set OnNavigationItemReselectedListener.");
        }
    }






    public void onBottomNavigation(int itemId) {

        Log.d("BottomNavigation", "onBottomNavigation called, itemId: " + itemId);

        Fragment selectedFragment = null;

        if (itemId == R.id.bottom_navigation_menu_home_button) {
            selectedFragment = new ListFragment();
        } else if (itemId == R.id.bottom_navigation_menu_map_button) {
            selectedFragment = new MapFragment();
        } else if (itemId == R.id.bottom_navigation_menu_add_button) {
           // selectedFragment = new WorkmatesFragment();
        }

        if (selectedFragment != null) {

            Log.d("BottomNavigation", "Replacing fragment with: " + selectedFragment.getClass().getSimpleName());

            MainActivity.this
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_frame_layout, selectedFragment)
                    .commit();
        }
    }

 */

    private void configureNavigationView() {
        binding.navView.setNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.drawer_menu_map_button) {
            // Lancement de MapActivity
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            startActivity(intent);
        } else if (id == R.id.drawer_menu_simulation_button) {
            // Lancement de SimulationActivity
            Intent intentS = new Intent(MainActivity.this, SimulationActivity.class);
            Log.d("Simulation activity ok", "Simulation on");
            startActivity(intentS);
        } else if (id == R.id.drawer_menu_news_button) {
            // Lancement de NewsActivity
            Intent intentN = new Intent(MainActivity.this, NewsActivity.class);
            startActivity(intentN);
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
               SearchModel searchModel = new SearchModel();
               searchModel.show(getSupportFragmentManager(), "searchModel");
            }
        }






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


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.name_channel);
            String description = getString(R.string.description_channel);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.id_channel), name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            Log.d("lodi", "Channel Created ");

        }
    }



    @SuppressLint("MissingPermission")
    private void showRealEstateCreatedNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.id_channel))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Nouveau bien immobilier")
                .setContentText("Un nouveau bien immobilier a été créé.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

        Log.d("lodi", "Show Notification");

    }





}

