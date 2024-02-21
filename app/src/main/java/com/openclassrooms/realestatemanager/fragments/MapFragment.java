 package com.openclassrooms.realestatemanager.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.databinding.FragmentMapBinding;
import com.openclassrooms.realestatemanager.models.RealEstate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MapFragment extends Fragment implements OnMapReadyCallback, EasyPermissions.PermissionCallbacks {

    private static final String TAG = "MapFragment";


    // Interface de callback pour le clic sur un marqueur
    public interface OnMarkerClickListener {
        void onMarkerClick(RealEstate realEstate);
    }

    private OnMarkerClickListener callback;
    private static final String ESTATE_KEY = "REAL ESTATE";
    private static final String ESTATES_KEY = "REAL ESTATES";
    private static final float DEFAULT_ZOOM_LEVEL = 14.0f;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private MapView mapView;

    private RealEstate realEstate;
    private List<RealEstate> realEstateList;
    private FragmentMapBinding mapBinding;
    private GoogleMap googleMap;


    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(RealEstate realEstate) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putParcelable(ESTATE_KEY, realEstate);
        fragment.setArguments(args);
        return fragment;
    }

    public static MapFragment newInstance(List<RealEstate> realEstates) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ESTATES_KEY, (ArrayList<RealEstate>) realEstates);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Initializing MapFragment view.");

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
        Log.d(TAG, "retrieveArguments: Retrieving arguments for MapFragment.");

        if (getArguments() != null) {
            realEstate = getArguments().getParcelable(ESTATE_KEY);
            realEstateList = getArguments().getParcelableArrayList(ESTATES_KEY);
            Log.d(TAG, "retrieveArguments" + realEstateList);

        }
    }

    private void initializeMap(Bundle savedInstanceState) {
        mapView = mapBinding.map;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

            Log.d("lodi", "initializeMap"  );
        }


    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        Log.d(TAG, "onMapReady: Map is ready.");


        if (checkLocationPermissions()) {
            getCurrentLocation();
        }


   // Configurez ici l'écouteur de clic sur les marqueurs
        this.googleMap.setOnMarkerClickListener(marker -> {
            handleMarkerClick(marker);
            return true;
        });


        setupMarkers();

    }





    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMarkerClickListener) {
            callback = (OnMarkerClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMarkerClickListener");
        }
    }

    // Méthode pour gérer le clic sur le marqueur dans votre MapFragment
    private void handleMarkerClick(Marker marker) {
        Log.d(TAG, "handleMarkerClick: Marker clicked.");

        RealEstate estate = (RealEstate) marker.getTag();
        if (estate != null && callback != null) {
            callback.onMarkerClick(estate);
        }
    }



    private boolean checkLocationPermissions() {
        Log.d(TAG, "checkLocationPermissions: Checking location permissions.");

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Demander les autorisations
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);

            return false;
        }
        return true;

    }

    private void getCurrentLocation() {
        FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        Log.d(TAG, "getCurrentLocation: Getting current location.");


        try {
            if (checkLocationPermissions()) {
                locationProviderClient.getLastLocation()
                        .addOnSuccessListener(requireActivity(), location -> {
                            if (location != null) {
                                LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                googleMap.addMarker(new MarkerOptions().position(userLatLng).title("Votre Position"));
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, DEFAULT_ZOOM_LEVEL));
                            }
                        });
            }
        } catch (SecurityException e) {
            Log.e("MapFragment", "Erreur de sécurité en récupérant la localisation : ", e);
        }
    }




    private void setupMarkers() {
        if (realEstateList != null && !realEstateList.isEmpty()) {
            for (RealEstate realEstate : realEstateList) {
                addMarker(realEstate);
                Log.d(TAG, "setupMarkers: Setting up markers on the map.");

            }
        }
    }

    private void addMarker(RealEstate realEstate) {
        Log.d(TAG, "addMarker: Adding marker for estate: " + realEstate.getName());

        LatLng position = new LatLng(realEstate.getLatitude(), realEstate.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(position).title(realEstate.getName());
        Marker marker = googleMap.addMarker(markerOptions);
        if (marker != null) {
            marker.setTag(realEstate);
        }
    }








    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }
}


