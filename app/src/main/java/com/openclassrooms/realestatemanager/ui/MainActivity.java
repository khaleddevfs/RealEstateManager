package com.openclassrooms.realestatemanager.ui;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.openclassrooms.realestatemanager.Manifest;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.RealEstateEditor;
import com.openclassrooms.realestatemanager.Utils;
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding;
import com.openclassrooms.realestatemanager.models.RealEstate;
import com.openclassrooms.realestatemanager.viewModel.RealEstateViewModel;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private RealEstateViewModel viewModel;

    private RealEstate realEstate;

    private boolean filtered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
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


    private void initView() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }

    @Override
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

    private void editRealEstate() {
        Intent intent = new Intent(this, RealEstateEditor.class);
        intent.putExtra("REAL_ESTATE", realEstate);
        mEditRealEstateLauncher.launch(intent);
    }

    private void createNewRealEstate() {
        Intent intent = new Intent(this, RealEstateEditor.class);
        mEditRealEstateLauncher.launch(intent);
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
            SearchModal searchModal = new SearchModal();
            searchModal.show(getSupportFragmentManager(), "searchModal");
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
    }



    private final ActivityResultLauncher<Intent> mEditRealEstateLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            RealEstate editedEstate = result.getData().getParcelableExtra("EDITED_REAL_ESTATE");
                            viewModel.createOrUpdateRealEstate(editedEstate);
                            realEstate.clone(editedEstate);
                            realEstate.get(realEstate.indexOf(editedEstate)).clone(editedEstate);
                        }
                    });
}
