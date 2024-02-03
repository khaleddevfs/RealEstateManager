package com.openclassrooms.realestatemanager.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.databinding.FragmentMapBinding;
import com.openclassrooms.realestatemanager.models.RealEstate;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MapFragment extends Fragment implements OnMapReadyCallback, EasyPermissions.PermissionCallbacks {

    private static final String ESTATE_KEY = "ESTATE";
    private static final String ESTATES_KEY = "ESTATES";
    private static final float DEFAULT_ZOOM_LEVEL = 14.0f;

    private RealEstate estate;
    private List<RealEstate> realEstateList;
    private FragmentMapBinding mapBinding;
    private GoogleMap googleMap;

    private MapView mapView;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(RealEstate estate) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putParcelable(ESTATE_KEY, estate);
        fragment.setArguments(args);
        return fragment;
    }

    public static MapFragment newInstance(List<RealEstate> estates) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ESTATES_KEY, (ArrayList<RealEstate>) estates);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mapBinding = FragmentMapBinding.inflate(inflater, container, false);
        View view = mapBinding.getRoot();
        retrieveArguments();
        initializeMap(savedInstanceState);
        return view;

    }

    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (googleMap != null) {
            String STATE_KEY_MAP_CAMERA = "keymap";
            outState.putParcelable(STATE_KEY_MAP_CAMERA, googleMap.getCameraPosition());
        }
    }
    private void retrieveArguments() {
        if (getArguments() != null) {
            estate = getArguments().getParcelable(ESTATE_KEY);
            realEstateList = getArguments().getParcelableArrayList(ESTATES_KEY);
            Log.d("lodi", "retrieveArguments" + realEstateList);

        }
    }

    private void initializeMap(Bundle savedInstanceState) {
        mapView = mapBinding.map;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

            Log.d("lodi", "initializeMap"  );
        }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        Log.d("lodi", "onMapReady"  );
    }

    private void setupMarkers() {
        if (estate != null) {
            // Ajoutez un marqueur pour l'immobilier unique
            addMarker(estate);
        } else if (realEstateList != null) {
            // Ajoutez des marqueurs pour chaque immobilier dans la liste
            for (RealEstate estate : realEstateList) {
                addMarker(estate);
            }
        }
    }

    private void addMarker(RealEstate estate) {
        // Vous devez définir getLatitude() et getLongitude() dans votre modèle RealEstate
        LatLng position = new LatLng(estate.getLatitude(), estate.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(position).title(estate.getName()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_ZOOM_LEVEL));
        Log.d("lodi", "AddMarker"  );
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }
}
