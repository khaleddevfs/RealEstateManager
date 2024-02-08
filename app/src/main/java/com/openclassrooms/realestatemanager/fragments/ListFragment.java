package com.openclassrooms.realestatemanager.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
/*
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

 */




import androidx.lifecycle.ViewModelProvider;


public class ListFragment extends Fragment implements RealEstateAdapter.OnItemClickListener {

    private FragmentListBinding binding;
    private List<RealEstate> realEstateList = new ArrayList<>();
    private RealEstateAdapter adapter;
    private RealEstateViewModel realEstateViewModel;

    private static final String TAG = "ListFragment";


    public ListFragment() {
        // Required empty public constructor
    }

    public static ListFragment newInstance() {
        return new ListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Starting");

        binding = FragmentListBinding.inflate(inflater, container, false);
        initViewModel();
        initRecyclerView();
        return binding.getRoot();
    }

    private void initViewModel() {
        Log.d(TAG, "initViewModel: Initializing");

        realEstateViewModel = new ViewModelProvider(requireActivity()).get(RealEstateViewModel.class);
        observeRealEstates();
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: Setting up RecyclerView");

        adapter = new RealEstateAdapter(realEstateList, this);
        binding.realEstateListRv.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.realEstateListRv.setAdapter(adapter);
    }

    private void observeRealEstates() {
        Log.d(TAG, "observeRealEstates: Observing real estates");
        realEstateViewModel.getRealEstates().observe(getViewLifecycleOwner(), realEstates -> {
            if (realEstates != null && !realEstates.isEmpty()) {
                Log.d(TAG, "observeRealEstates: Received " + realEstates.size() + " real estates");
            } else {
                Log.d(TAG, "observeRealEstates: No real estates received");
            }
            realEstateList.clear();
            realEstateList.addAll(realEstates);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onItemClick(RealEstate realEstate) {
        Log.d(TAG, "onItemClick: Clicked on a real estate item");

        DetailsFragment detailFragment = DetailsFragment.newInstance(realEstate);

        // Vérifiez si l'appareil est une tablette ou un téléphone
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);

        // Choisissez le conteneur en fonction du type d'appareil
        int containerId = isTablet ? R.id.details_fragment_container : R.id.main_frame_layout;

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(containerId, detailFragment);
        if (!isTablet) {
            // Ajoutez la transaction à la pile arrière uniquement pour les téléphones
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

}
