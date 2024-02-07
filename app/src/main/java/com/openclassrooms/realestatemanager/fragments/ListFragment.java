package com.openclassrooms.realestatemanager.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.adapters.RealEstateAdapter;
import com.openclassrooms.realestatemanager.databinding.FragmentListBinding;
import com.openclassrooms.realestatemanager.models.RealEstate;
import com.openclassrooms.realestatemanager.viewModel.RealEstateViewModel;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment implements RealEstateAdapter.OnItemClickListener {


    private FragmentListBinding binding;
    private List<RealEstate> realEstateList;
    private RealEstateAdapter adapter;

    private RealEstateViewModel realEstateViewModel;


    public ListFragment() {

    }


    public static ListFragment newInstance(List<RealEstate> realEstateList) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("realEstateList", new ArrayList<>(realEstateList));
        fragment.setArguments(args);
        return fragment;

    }

    @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater, container, false);


        if (getArguments() != null) {
            realEstateList = getArguments().getParcelableArrayList("realEstateList");
        }

        initRecyclerView();
        Log.d("lodi", "list fragment created");
            return binding.getRoot();

               }


    private void initRecyclerView() {
        if (realEstateList == null) {
            realEstateList = new ArrayList<>(); // ou utilisez une liste factice
        }
        adapter = new RealEstateAdapter(realEstateList, this);
        binding.realEstateListRv.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.realEstateListRv.setAdapter(adapter);


    }

    // Appelée pour mettre à jour la liste et rafraîchir l'interface utilisateur
    public void updateRealEstateList(List<RealEstate> newList) {
        realEstateList.clear();
        realEstateList.addAll(newList);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onResume() {
        super.onResume();
        // Observer les LiveData retournés par le DAO
        realEstateViewModel.getRealEstates().observe(getViewLifecycleOwner(), realEstates -> {
            // Mettre à jour la liste avec les données observées
            updateRealEstateList(realEstates);
        });

    }



    @Override
    public void onItemClick(RealEstate realEstate) {
        DetailsFragment detailFragment = DetailsFragment.newInstance(realEstate);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame_layout, detailFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
