package com.openclassrooms.realestatemanager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.openclassrooms.realestatemanager.databinding.FragmentListBinding;
import com.openclassrooms.realestatemanager.databinding.FragmentsDetailsBinding;
import com.openclassrooms.realestatemanager.injection.ViewModelFactory;
import com.openclassrooms.realestatemanager.models.RealEstate;
import com.openclassrooms.realestatemanager.models.RealEstateMedia;
import com.openclassrooms.realestatemanager.viewModel.RealEstateViewModel;

import java.util.List;

public class DetailsFragment extends Fragment implements AdapterView.OnItemClickListener {

    private List<RealEstate> realEstateList;

    private FragmentsDetailsBinding binding;

    private RealEstateViewModel viewModel;

    public DetailsFragment() {
    }

    public static DetailsFragment newInstance(RealEstate realEstate) {
        return null;
    }


    // ... [class variables]

    // Lifecycle methods simplified
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        initializeComponents();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentsDetailsBinding.inflate(inflater, container, false);
        initializeViewModel();
        loadRealEstateData();
        return binding.getRoot();
    }

    // ... [other lifecycle methods]

    // Refactored methods for clarity and modularity
    private void initializeComponents() {
       // mImagePopupWindow = new ImagePopupWindow();
        // Other initialization code
    }

    private void initializeViewModel() {
        viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance(getContext())).get(RealEstateViewModel.class);
        // ViewModel initialization code
    }

    private void loadRealEstateData() {
        Bundle bundle = getArguments();
        assert bundle != null;
        realEstateList = bundle.getParcelable("REAL_ESTATE");
        updateUi();
    }

    private void updateUi() {
        // UI update logic broken down into smaller methods
        updateMediaGallery();
        updatePropertyDetails();
        // More UI updates
    }

    private void updateMediaGallery() {
        // Logic for updating media gallery
    }

    private void updatePropertyDetails() {
        // Logic for updating property details
    }

    // ... [other methods]

    // Observer methods refactored for simplicity
    private void mediaObserver(List<RealEstateMedia> mediaList) {
        // Logic for media observation
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    // ... [more methods and inner classes]

}
