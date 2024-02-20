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


    // Interface de callback pour le clic sur un marqueur
    public interface OnMarkerClickListener {
        void onMarkerClick(RealEstate estate);
    }

    private OnMarkerClickListener callback;
    private static final String ESTATE_KEY = "ESTATE";
    private static final String ESTATES_KEY = "ESTATES";
    private static final float DEFAULT_ZOOM_LEVEL = 14.0f;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private MapView mapView;

    private RealEstate estate;
    private List<RealEstate> realEstateList;
    private FragmentMapBinding mapBinding;
    private GoogleMap googleMap;


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


    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (checkLocationPermissions()) {
            getCurrentLocation(); // Continuer à obtenir la position actuelle de l'utilisateur si nécessaire
        }


   // Configurez ici l'écouteur de clic sur les marqueurs
        this.googleMap.setOnMarkerClickListener(marker -> {
            handleMarkerClick(marker); // Appelez votre méthode handleMarkerClick
            return true; // Retournez true pour indiquer que nous avons géré l'événement de clic
        });


        setupMarkers(); // Assurez-vous que cette ligne est appelée pour configurer les marqueurs

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
        RealEstate estate = (RealEstate) marker.getTag();
        if (estate != null && callback != null) {
            callback.onMarkerClick(estate);
        }
    }

    // Assurez-vous d'appeler handleMarkerClick(marker) lorsque l'utilisateur clique sur un marqueur

    private boolean checkLocationPermissions() {
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
            for (RealEstate estate : realEstateList) {
                addMarker(estate);
            }
        }
    }

    private void addMarker(RealEstate estate) {
        LatLng position = new LatLng(estate.getLatitude(), estate.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(position).title(estate.getName());
        Marker marker = googleMap.addMarker(markerOptions);
        if (marker != null) {
            marker.setTag(estate); // Store the estate object as a tag
        }
    }








    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }
}



/*
 import android.Manifest;
 import android.content.Context;
 import android.content.pm.PackageManager;
 import android.os.Bundle;
 import android.util.Log;
 import android.view.LayoutInflater;
 import android.view.View;
 import android.view.ViewGroup;

 import androidx.annotation.NonNull;
 import androidx.core.app.ActivityCompat;
 import androidx.core.content.ContextCompat;

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

 import java.util.ArrayList;
 import java.util.List;

 import pub.devrel.easypermissions.AfterPermissionGranted;
 import pub.devrel.easypermissions.EasyPermissions;

 public class MapFragment extends SupportMapFragment implements OnMapReadyCallback, EasyPermissions.PermissionCallbacks {

     private static final String ESTATE_KEY = "ESTATE";
     private static final String ESTATES_KEY = "ESTATES";
     private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
     private static final float DEFAULT_ZOOM_LEVEL = 14.0f;

     private MapView mapView;

     private RealEstate estate;
     private FragmentMapBinding mapBinding;
     private GoogleMap googleMap;
     private List<RealEstate> realEstateList;

     // Interface de callback pour le clic sur un marqueur
     public interface OnMarkerClickListener {
         void onMarkerClick(RealEstate estate);
     }

     private OnMarkerClickListener callback;

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
     public void onAttach(@NonNull Context context) {
         super.onAttach(context);
         if (context instanceof OnMarkerClickListener) {
             callback = (OnMarkerClickListener) context;
         } else {
             throw new RuntimeException(context.toString() + " must implement OnMarkerClickListener");
         }
     }

     @Override
     public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         getMapAsync(this);
     }

     @Override
     public void onMapReady(@NonNull GoogleMap googleMap) {
         this.googleMap = googleMap;
         checkLocationPermissionsAndSetupMap();
     }

     @AfterPermissionGranted(LOCATION_PERMISSION_REQUEST_CODE)
     private void checkLocationPermissionsAndSetupMap() {
         if (hasLocationPermission()) {
             setupMapForLocation();
         } else {
             EasyPermissions.requestPermissions(
                     this,
                     getString(R.string.permission_rationale_location),
                     LOCATION_PERMISSION_REQUEST_CODE,
                     Manifest.permission.ACCESS_FINE_LOCATION);
         }
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

     private boolean hasLocationPermission() {
         return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
     }

     private void setupMapForLocation() {
         if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
             return;
         }
         googleMap.setMyLocationEnabled(true);
         googleMap.getUiSettings().setMyLocationButtonEnabled(true);

         this.googleMap.setOnMarkerClickListener(marker -> {
             handleMarkerClick(marker); // Appelez votre méthode handleMarkerClick
             return true; // Retournez true pour indiquer que nous avons géré l'événement de clic
         });
         setupMarkers();
     }

     private void setupMarkers() {
         if (realEstateList != null && !realEstateList.isEmpty()) {
             for (RealEstate estate : realEstateList) {
                 addMarker(estate);
             }
         }
     }

     private void addMarker(RealEstate estate) {
         LatLng position = new LatLng(estate.getLatitude(), estate.getLongitude());
         MarkerOptions markerOptions = new MarkerOptions().position(position).title(estate.getName());
         Marker marker = googleMap.addMarker(markerOptions);
         if (marker != null) {
             marker.setTag(estate); // Store the estate object as a tag
         }
     }


     @Override
     public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
         if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
             setupMapForLocation();
         }
     }

     @Override
     public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
         // Handle permissions denial, possibly with a dialog or a snackbar
     }

     // Méthode pour gérer le clic sur le marqueur dans votre MapFragment
     private void handleMarkerClick(Marker marker) {
         RealEstate estate = (RealEstate) marker.getTag();
         if (estate != null && callback != null) {
             callback.onMarkerClick(estate);
         }
     }

     // Assurez-vous d'appeler handleMarkerClick(marker) lorsque l'utilisateur clique sur un marqueur

     private boolean checkLocationPermissions() {
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

 }

 */
