package com.openclassrooms.realestatemanager.ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.databinding.ActivitySupportBinding;

import com.openclassrooms.realestatemanager.fragments.DetailsFragment;
import com.openclassrooms.realestatemanager.models.RealEstate;

public class SupportActivity extends AppCompatActivity {

    private RealEstate realEstate;

    ActivitySupportBinding binding;



        @Override
        protected void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);

            ActivitySupportBinding binding = ActivitySupportBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            RealEstate realEstate = getIntent().getParcelableExtra("REAL_ESTATE");

            if (realEstate != null) {
                setupDetailsFragment(binding, realEstate);
            }
        }


        private void setupDetailsFragment(ActivitySupportBinding binding, RealEstate realEstate) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            DetailsFragment fragment = new DetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("REAL_ESTATE", realEstate);
            fragment.setArguments(bundle);

            transaction.replace(binding.SupportFrame.getId(), fragment);

         //   setupActionBar(realEstate);

            transaction.commit();
        }
/*
        private void setupActionBar(RealEstate realEstate) {
            int color = Color.BLUE;
            String title = realEstate.getName();

            if (realEstate.getSaleDate() != null) {
                color = Color.RED;
                title += " " + getString(R.string.sold, realEstate.getSaleDate());
            }

            if (getSupportActionBar() != null) {
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
                getSupportActionBar().setTitle(title);
            }
        }

 */
}
