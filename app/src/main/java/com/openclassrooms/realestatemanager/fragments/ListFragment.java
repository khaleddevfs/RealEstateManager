package com.openclassrooms.realestatemanager.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.adapters.RealEstateAdapter;
import com.openclassrooms.realestatemanager.databinding.FragmentListBinding;
import com.openclassrooms.realestatemanager.databinding.FragmentMapBinding;
import com.openclassrooms.realestatemanager.models.RealEstate;

import java.util.List;

public class ListFragment extends Fragment implements AdapterView.OnItemClickListener {


        private RecyclerView recyclerView;
        private RealEstateAdapter adapter;


    private FragmentListBinding binding;

    private final List<RealEstate> realEstateList;

    public ListFragment(List<RealEstate> realEstateList) {
        this.realEstateList = realEstateList;
    }

    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_list, container, false);
            recyclerView = view.findViewById(R.id.real_estate_list_rv);
            // Initialisez et configurez votre RecyclerView

            adapter = new RealEstateAdapter(realEstateList, this);
            recyclerView.setAdapter(adapter);

            return view;
        }
/*
        @Override
        public void onItemClick(RealEstate realEstate) {
            // Ici, naviguez vers le fragment de détails
            DetailsFragment detailFragment = DetailsFragment.newInstance(realEstate);
            assert getFragmentManager() != null;
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.main_frame_layout, detailFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

 */


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        RealEstate realEstate = new RealEstate();
        DetailsFragment detailFragment = DetailsFragment.newInstance(realEstate);
        assert getFragmentManager() != null;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame_layout, detailFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
