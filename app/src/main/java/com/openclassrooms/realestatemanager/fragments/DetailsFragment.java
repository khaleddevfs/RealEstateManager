package com.openclassrooms.realestatemanager.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.adapters.MediaGalleryAdapter;
import com.openclassrooms.realestatemanager.database.RealEstateDao;
import com.openclassrooms.realestatemanager.database.SaveRealEstateDB;
import com.openclassrooms.realestatemanager.databinding.FragmentListBinding;
import com.openclassrooms.realestatemanager.databinding.FragmentsDetailsBinding;
import com.openclassrooms.realestatemanager.databinding.MediaListItemBinding;
import com.openclassrooms.realestatemanager.event.OnItemClickListener;
import com.openclassrooms.realestatemanager.event.OnMapCreated;
import com.openclassrooms.realestatemanager.injection.ViewModelFactory;
import com.openclassrooms.realestatemanager.models.RealEstate;
import com.openclassrooms.realestatemanager.models.RealEstateMedia;
import com.openclassrooms.realestatemanager.ui.ImagePopupWindow;
import com.openclassrooms.realestatemanager.ui.SupportActivity;
import com.openclassrooms.realestatemanager.utils.SaveImageTask;
import com.openclassrooms.realestatemanager.viewModel.RealEstateViewModel;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DetailsFragment extends Fragment implements OnMapCreated, OnItemClickListener {

    private List<RealEstate> realEstateList;

    private FragmentsDetailsBinding binding;

    private ImagePopupWindow imagePopupWindow;

    private Context context;


    private RealEstateDao realEstateDao;
    RealEstate estate;
    private RealEstateViewModel viewModel;

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
        initializeViewModel();
        loadRealEstateData();
        return binding.getRoot();
    }

    // ... [other lifecycle methods]

    // Refactored methods for clarity and modularity
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
            } else {
                // L'image existe déjà, l'afficher
                updateMap(mapImageFile);
            }
        }
    }



    private void updateMediaGallery() {
        // Logic for updating media gallery
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


    // ... [other methods]

    // Observer methods refactored for simplicity
    private void mediaObserver(List<RealEstateMedia> mediaList) {
        estate.setMediaList(mediaList);
        setupMediaGallery(mediaList);

        // Traitement des médias si nécessaire. Supposons que vous avez une logique pour déterminer si un téléchargement ou une mise à jour est nécessaire.
        processMediaList(mediaList);
    }

    private void setupMediaGallery(List<RealEstateMedia> mediaList) {
        MediaGalleryAdapter mediaGalleryAdapter = new MediaGalleryAdapter(mediaList, this );
        binding.mediaGallery.setAdapter(mediaGalleryAdapter);
        binding.mediaGallery.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void processMediaList(List<RealEstateMedia> mediaList) {
        for (int i = 0; i < mediaList.size(); i++) {
            RealEstateMedia media = mediaList.get(i);
            processMediaItem(media, i, mediaList.size());
        }
    }

    private void processMediaItem(RealEstateMedia media, int index, int totalSize) {
        // Cette méthode devrait vérifier si les médias nécessitent un traitement, comme un téléchargement ou une mise à jour.
        // Par exemple, vous pourriez vérifier si le fichier existe localement et le télécharger si nécessaire.
        if (!new File(media.getMediaUrl()).exists()) {
            downloadMedia(media, index, totalSize);
        } else {
            if (index == 0) {
                updateFeaturedMediaUrl(media);
            }
            // Votre logique pour traiter le dernier média si nécessaire.
        }
    }

    private void downloadMedia(RealEstateMedia media, int index, int totalSize) {
        // Implémentez votre logique de téléchargement ici. Après le téléchargement :
        // 1. Mettez à jour l'URL ou le chemin du fichier dans l'objet media.
        // 2. Utilisez RealEstateMediaDao pour mettre à jour la base de données avec le nouveau chemin.
        // 3. Si nécessaire, mettez à jour l'interface utilisateur après le téléchargement.

        // Exemple fictif de mise à jour de l'objet et de la base de données :
        String newMediaUrl = "path/to/downloaded/media";
        media.setMediaURL(newMediaUrl);
        // realEstateMediaDao.updateMedia(media); // Supposons que vous avez une instance de RealEstateMediaDao.

        // Mettez à jour l'interface utilisateur si nécessaire.
        if (index == 0) {
            updateFeaturedMediaUrl(media);
        }
        if (index + 1 == totalSize) {
            // Peut-être rafraîchir la galerie ou effectuer d'autres actions de fin.
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

        // Tentez d'extraire l'objet RealEstate à partir des arguments du bundle.
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
    }

    private String extractLocationFromJsonPoint(String jsonPoint) {
        // Extrait les coordonnées depuis le jsonPoint. Retournez-les dans le format "latitude,longitude".
        return "latitude,longitude";
    }

    private String getStaticMapUrl(String location) {
        // Construisez l'URL pour une carte statique basée sur l'emplacement.
        return "https://maps.googleapis.com/maps/api/staticmap?center=" + location + "&zoom=15&size=600x300&maptype=roadmap&markers=color:red%7Clabel:C%7C" + location + "&key=VOTRE_CLE_API";
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
