package com.openclassrooms.realestatemanager.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.RealEstateEditor;
import com.openclassrooms.realestatemanager.adapters.MediaGalleryAdapter;
import com.openclassrooms.realestatemanager.database.RealEstateDao;
import com.openclassrooms.realestatemanager.database.SaveRealEstateDB;
import com.openclassrooms.realestatemanager.databinding.FragmentsDetailsBinding;
import com.openclassrooms.realestatemanager.event.OnItemClickListener;
import com.openclassrooms.realestatemanager.event.OnMapCreated;
import com.openclassrooms.realestatemanager.injection.ViewModelFactory;
import com.openclassrooms.realestatemanager.models.RealEstate;
import com.openclassrooms.realestatemanager.models.RealEstateMedia;
import com.openclassrooms.realestatemanager.ui.ImagePopupWindow;
import com.openclassrooms.realestatemanager.ui.SupportActivity;
import com.openclassrooms.realestatemanager.utils.SaveImageTask;
import com.openclassrooms.realestatemanager.viewModel.RealEstateViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DetailsFragment extends Fragment implements OnMapCreated, OnItemClickListener {

    private List<RealEstate> realEstateList;

    private FragmentsDetailsBinding binding;

    private ImagePopupWindow imagePopupWindow;

    private ViewPager2 mediaViewPager2;


    private Context context;


    private RealEstateDao realEstateDao;
    RealEstate estate;
    private RealEstateViewModel viewModel;

    private boolean isTwoPaneLayout; // Pour détecter si l'appareil est une tablette

    private LiveData<List<RealEstateMedia>> liveData;
    private Observer<List<RealEstateMedia>> observer;
    public DetailsFragment() {
    }

    public static DetailsFragment newInstance(RealEstate realEstate) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("REAL_ESTATE", realEstate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onMapCreated(File file) {
        updateMap(file);
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
        mediaViewPager2 = binding.mediaViewPager;

        initializeViewModel();
        loadRealEstateData();



        return binding.getRoot();
    }






   /* @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);


        if (shouldShowEditMenu()) {
            inflater.inflate(R.menu.edit_menu_phone, menu);
        } else {
            // Gonflez ici le menu par défaut si la condition n'est pas remplie
            inflater.inflate(R.menu.main_menu_tablet, menu);
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            // Assurez-vous que l'objet RealEstate `estate` est initialisé avec les détails à éditer
            Intent intent = new Intent(getActivity(), RealEstateEditor.class);
            intent.putExtra("REAL_ESTATE_TO_EDIT", estate); // 'estate' doit être Parcelable
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */


    private void setupMediaGalleryViewPager() {
        if (estate != null && mediaViewPager2 != null) { // Vérifiez aussi que mediaViewPager n'est pas null
            viewModel.getRealEstateMediasByID(estate.getID()).observe(getViewLifecycleOwner(), mediaList -> {
                if (mediaList != null && !mediaList.isEmpty()) {
                    MediaGalleryAdapter adapter = new MediaGalleryAdapter(mediaList, this);
                    binding.mediaViewPager.setAdapter(adapter);
                }
            });
        }
    }





    private void initializeComponents() {
       imagePopupWindow = new ImagePopupWindow();
        // Other initialization code
    }

    private void initializeViewModel() {
        viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance(getContext())).get(RealEstateViewModel.class);
        realEstateDao = SaveRealEstateDB.getInstance(context).realEstateDao();
    }

    private void loadRealEstateData() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("REAL_ESTATE")) {
            estate = bundle.getParcelable("REAL_ESTATE");
            updateUi();
        } else {
            // Gérer la situation où les données ne sont pas disponibles
            Log.e("DetailsFragment", "Aucune donnée RealEstate n'est disponible dans le Bundle");
        }
    }

    private String generateMapImageUrl(double latitude, double longitude) {
        return "https://maps.googleapis.com/maps/api/staticmap?center=" + latitude + "," + longitude + "&zoom=15&size=600x300&maptype=roadmap&markers=color:red%7C" + latitude + "," + longitude + R.string.MAPS_API_KEY;
    }

    private void updateUi() {
        updateMediaGallery();
        updatePropertyDetails();

        // Générer l'URL de l'image de la carte basée sur les coordonnées
        double latitude = estate.getLatitude();
        double longitude = estate.getLongitude();
        if (latitude != 0.0 && longitude != 0.0) {
            String mapImageUrl = generateMapImageUrl(latitude, longitude);
            String mapImagePath = "map_" + estate.getId() + ".jpg"; // Nom de fichier basé sur l'ID de l'immobilier
            File mapImageFile = new File(getContext().getFilesDir(), mapImagePath);
            if (!mapImageFile.exists()) {
                // Télécharger et sauvegarder l'image localement
                new SaveImageTask(getContext(), this).execute(mapImageUrl, mapImagePath);

                Log.d("TAG", "mapImagePath: " + mapImagePath);

        } else {
                // L'image existe déjà, l'afficher
                updateMap(mapImageFile);
            }
        }
        setupMediaGalleryViewPager();

    }



    private void updateMediaGallery() {
    }

    private void updatePropertyDetails() {
        liveData = viewModel.getRealEstateMediasByID(estate.getID());
        observer = this::mediaObserver;
        liveData.observe(getViewLifecycleOwner(), observer);
        binding.address.setText(estate.getLocation());
        binding.description.setText(estate.getDescription());
        binding.surface.setText(getString(R.string.surface, estate.getSurface()));
        binding.bathrooms.setText(getString(R.string.number_of_bathrooms, estate.getBathrooms()));
        binding.bedrooms.setText(getString(R.string.number_of_bedrooms,estate.getBedrooms()));
        binding.rooms.setText(getString(R.string.number_of_rooms, estate.getRooms()));
        binding.agentTextView.setText(getString(R.string.agent, estate.getAgentName()));

        Log.d("TAG", "onCreateView: " + estate.toString());    }

    private void updateMap(File file) {
        if (isAdded()) {
            if (getActivity() != null) {
                requireActivity().runOnUiThread(() -> Glide.with(requireActivity())
                        .load(file)
                        .override(Target.SIZE_ORIGINAL)
                        .into(binding.staticMap));
            }
        }

    }



    private void mediaObserver(List<RealEstateMedia> mediaList) {
        estate.setMediaList(mediaList);
        setupMediaGallery(mediaList);

        processMediaList(mediaList);
    }

    private void setupMediaGallery(List<RealEstateMedia> mediaList) {
        MediaGalleryAdapter mediaGalleryAdapter = new MediaGalleryAdapter(mediaList, this::onItemClick);
        binding.mediaViewPager.setAdapter(mediaGalleryAdapter);
    }

    private void processMediaList(List<RealEstateMedia> mediaList) {
        for (int i = 0; i < mediaList.size(); i++) {
            RealEstateMedia media = mediaList.get(i);
            processMediaItem(media, i, mediaList.size());
        }
    }

    private void processMediaItem(RealEstateMedia media, int index, int totalSize) {

        if (!new File(media.getMediaUrl()).exists()) {
            downloadMedia(media, index, totalSize);
        } else {
            if (index == 0) {
                updateFeaturedMediaUrl(media);
            }
        }
    }

    private void downloadMedia(RealEstateMedia media, int index, int totalSize) {

        String newMediaUrl = "path/to/downloaded/media";
        media.setMediaURL(newMediaUrl);

        if (index == 0) {
            updateFeaturedMediaUrl(media);
        }
        if (index + 1 == totalSize) {
        }
    }

    private void updateFeaturedMediaUrl(RealEstateMedia media) {
        String newFeaturedUrl = media.getMediaUrl();
        estate.setFeaturedMediaUrl(newFeaturedUrl);
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            realEstateDao.updateFeaturedMediaUrl(estate.getId(), newFeaturedUrl);
        });
    }


    @Override
    public void onItemClick(RealEstateMedia media) {
        imagePopupWindow.showPopup(binding.getRoot(),media);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("REAL_ESTATE")) {
            estate = arguments.getParcelable("REAL_ESTATE");
        }

        if (estate != null && estate.getJsonPoint() != null) {
            String location = extractLocationFromJsonPoint(estate.getJsonPoint());
            String mapUrl = getStaticMapUrl(location);
            Glide.with(this).load(mapUrl).into(binding.staticMap);
        }

        setupStaticMapClickListener();

        binding.editRealEstateButton.setOnClickListener(v -> {
            if (estate != null) {
                Intent intent = new Intent(getActivity(), RealEstateEditor.class);
                intent.putExtra("REAL_ESTATE_TO_EDIT", estate); // Assurez-vous que 'estate' est Parcelable.
                startActivity(intent);
            } else {
                Log.e("DetailsFragment", "Aucun bien immobilier à éditer.");
                // Vous pouvez également afficher un Toast pour informer l'utilisateur
            }
        });
    }

    private String extractLocationFromJsonPoint(String jsonPoint) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPoint);
            double latitude = jsonObject.optDouble("latitude", 0.0);
            double longitude = jsonObject.optDouble("longitude", 0.0);
            return latitude + "," + longitude;
        } catch (JSONException e) {
            e.printStackTrace();
            return "0,0"; // Retourner une valeur par défaut en cas d'erreur
        }
    }

    private String getStaticMapUrl(String location) {
        return "https://maps.googleapis.com/maps/api/staticmap?center=" + location + "&zoom=15&size=600x300&maptype=roadmap&markers=color:red%7Clabel:C%7C" + location + getString(R.string.MAPS_API_KEY);
    }

    private void setupStaticMapClickListener() {
        binding.staticMap.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SupportActivity.class);
            // Assurez-vous que l'objet RealEstate est sérialisable ou Parcelable.
            // Cet exemple suppose que vous avez déjà un objet RealEstate (estate) disponible.
            intent.putExtra("REAL_ESTATE", estate);
            startActivity(intent);
        });
    }



}
