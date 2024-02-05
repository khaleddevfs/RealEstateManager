package com.openclassrooms.realestatemanager.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.adapters.RealEstateAdapter;
import com.openclassrooms.realestatemanager.databinding.FragmentListBinding;
import com.openclassrooms.realestatemanager.models.RealEstate;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment implements RealEstateAdapter.OnItemClickListener {


    private RealEstateAdapter adapter;


    private FragmentListBinding binding;

    private List<RealEstate> realEstateList;



    public static ListFragment newInstance(List<RealEstate> realEstateList) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("realEstateList", new ArrayList<>(realEstateList));
        fragment.setArguments(args);
        return fragment;

    }

    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater, container, false);
       // View view = inflater.inflate(R.layout.fragment_list, container, false);

        if (getArguments() != null) {
            realEstateList = getArguments().getParcelableArrayList("realEstateList");
        }
        initRecyclerView();

            return binding.getRoot();

               }


    private void initRecyclerView() {
        adapter = new RealEstateAdapter(realEstateList, this);
        binding.realEstateListRv.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.realEstateListRv.setAdapter(adapter);
        Log.d("lodi", "list fragmnet");
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
