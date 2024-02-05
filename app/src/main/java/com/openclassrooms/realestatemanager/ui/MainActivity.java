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
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;


import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.RealEstateEditor;
import com.openclassrooms.realestatemanager.adapters.RealEstateAdapter;
import com.openclassrooms.realestatemanager.adapters.RealEstateViewHolder;
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

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private RealEstateViewModel viewModel;
    private final List<RealEstate> realEstateList = new ArrayList<>();
    private RealEstate realEstate;
    private boolean filtered = false;
    private boolean shouldObserve = true;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initViewModel();
        configureUI();
        processIntent(getIntent());




        // connecting MapsFragment with activity

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_frame_layout, new ListFragment(realEstateList))
                .commit();

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
  /*
    private void updateEstates() {
        setRealEstateClickListener();
        updateRealEstateList();
        updateDetailViewVisibility();
        syncDatabase();
        ScrollView();
        ConfigureDetailsFragment();
    }

  private void ConfigureDetailsFragment() {
        if (binding.detailViewContainer.getVisibility() == View.VISIBLE) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("REAL_ESTATE", realEstate);
            DetailsFragment fragment = new DetailsFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            fragment.setArguments(bundle);
            int color = Color.BLUE;
            String title = realEstate.getName();
            if (realEstate.getSaleDate() != null) {
                color = Color.RED;
                title += " " + getString(R.string.sold, realEstate.getSaleDate());
            }
            Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(color));
            getSupportActionBar().setTitle(title);


            transaction.replace(binding.FragmentContainer.getId(), fragment);
            transaction.commit();

    }
    }



    private void setRealEstateClickListener() {
        binding.realEstateListRv.setAdapter(new RealEstateAdapter(realEstateList, position -> {
            RealEstate selectedEstate = realEstateList.get(position);
            handleRealEstateClick(selectedEstate);
        }));
    }

    private void handleRealEstateClick(RealEstate estate) {
        this.realEstate = estate;
        if (binding.detailViewContainer.getVisibility() == View.VISIBLE) {
            showDetailsFragment();
        } else {
            navigateToSupportActivity();
        }
    }

    private void navigateToSupportActivity() {
    }



    private void showDetailsFragment() {
       Bundle bundle = new Bundle();
        bundle.putParcelable("REAL_ESTATE", realEstate);
        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(bundle);
        updateActionBar();
        getSupportFragmentManager().beginTransaction()
                .replace(mBinding.FragmentContainer.getId(), fragment)
                .commit();


    }






    private void updateRealEstateList() {
        // Additional logic if required
    }

    private void updateActionBar() {
        int color = realEstate.getSaleDate() != null ? Color.RED : Color.BLUE;
        String title = realEstate.getName();
        if (realEstate.getSaleDate() != null) {
            title += " " + getString(R.string.sold, realEstate.getSaleDate());
        }
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(color));
        getSupportActionBar().setTitle(title);
    }

 */


    private void updateDetailViewVisibility() {
        boolean isTablet = Utils.isDeviceTablet(getApplicationContext());
        binding.mainFrameLayout.setVisibility(isTablet ? View.VISIBLE : View.GONE);
        //binding.noResultsTextView.setVisibility(isTablet && realEstateList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void syncDatabase() {
        // Logic for SyncDB
    }






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



  /*  @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getOrder()) {
            case 1: // edit
                editRealEstate();
                break;
            case 2: // new
                createNewRealEstate();
                break;
            case 3: // sell
                sellRealEstate();
                break;
            case 4: // search
                searchRealEstate();
                break;
            case 5: // map
                showMap();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

   */

    private void editRealEstate() {
        Intent intent = new Intent(this, RealEstateEditor.class);
        intent.putExtra("REAL_ESTATE", realEstate);
        editRealEstateLauncher.launch(intent);
    }

    private void createNewRealEstate() {
        Intent intent = new Intent(this, RealEstateEditor.class);
        editRealEstateLauncher.launch(intent);

    }

    private void sellRealEstate() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            cal.set(year, month, dayOfMonth);
            realEstate.setSaleDate(cal.getTime());
            viewModel.createOrUpdateRealEstate(realEstate);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void searchRealEstate() {
        if (filtered) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            //SearchModal searchModal = new SearchModal();
            //searchModal.show(getSupportFragmentManager(), "searchModal");
        }
    }

    private void showMap() {
        if (Utils.isInternetAvailable(this)) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 10);
            } else {
                Intent mapActivityIntent = new Intent(this, MapActivity.class);
                startActivity(mapActivityIntent);
            }
        } else {
            Toast.makeText(this, getString(R.string.internet_is_required), Toast.LENGTH_LONG).show();
        }
        Log.d("lodi", "showMap"  );
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

    private void updateLocalRealEstateList(RealEstate editedEstate) {
        for (int i = 0; i < realEstateList.size(); i++) {
            if (realEstateList.get(i).getID() == editedEstate.getID()) {
                realEstateList.set(i, editedEstate);
                break;
            }
        }
    }


}
