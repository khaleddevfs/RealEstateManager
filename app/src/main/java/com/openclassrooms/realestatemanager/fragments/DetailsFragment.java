package com.openclassrooms.realestatemanager.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
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
import com.openclassrooms.realestatemanager.utils.ImageLoader;
import com.openclassrooms.realestatemanager.utils.SaveImageTask;
import com.openclassrooms.realestatemanager.viewModel.RealEstateViewModel;


import org.json.JSONException;
import org.json.JSONObject;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class DetailsFragment extends Fragment implements OnMapCreated, OnItemClickListener {

    private FragmentsDetailsBinding binding;
    private ImagePopupWindow imagePopupWindow;
    private ViewPager2 mediaViewPager2;
    private Context context;
    private final int NOTIFICATION_ID = 1;
    private RealEstateDao realEstateDao;
    private RealEstate realEstate;
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentsDetailsBinding.inflate(inflater, container, false);
        context = container.getContext();
        mediaViewPager2 = binding.mediaViewPager;
        initializeViewModel();
        loadRealEstateData();
        initializeComponents();
        return binding.getRoot();
    }


    private void initializeViewModel() {
        viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance(getContext())).get(RealEstateViewModel.class);
        realEstateDao = SaveRealEstateDB.getInstance(context).realEstateDao();
    }






    private void loadRealEstateData() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("REAL_ESTATE")) {
            realEstate = bundle.getParcelable("REAL_ESTATE");
            if (realEstate != null) {
                updateUi();
            } else {
                Log.e("DetailsFragment", "L'objet RealEstate 'estate' est null.");
            }
        } else {
            Log.e("DetailsFragment", "Aucune donnée RealEstate n'est disponible dans le Bundle");
        }
    }



    private void updateUi() {
        updatePropertyDetails();

        if (realEstate != null && realEstate.getLatitude() != 0 && realEstate.getLongitude() != 0) {
            String mapImageUrl = generateMapImageUrl(realEstate.getLatitude(), realEstate.getLongitude());
            // Utilisez ImageLoader pour télécharger et afficher l'image
            loadImage(mapImageUrl);
        } else {
            Log.e("DetailsFragment", "Coordonnées invalides ou objet estate non initialisé");
        }
    }



    private String generateMapImageUrl(double latitude, double longitude) {
        String apiKey = getString(R.string.MAPS_API_KEY); // Obtenez la clé API comme String
        Log.e("DetailsFragment", "generateMapImageUrl");
        return "https://maps.googleapis.com/maps/api/staticmap?center=" + latitude + "," + longitude + "&zoom=15&size=600x300&maptype=roadmap&markers=color:red%7C" + latitude + "," + longitude + "&key=" + apiKey;
    }



    private void loadImage(String mapImageUrl) {
        ImageLoader imageLoader = new ImageLoader();
        imageLoader.loadImage(mapImageUrl, new ImageLoader.ImageLoadCallback() {
            @Override
            public void onImageLoaded(Drawable drawable) {
                if (drawable != null) {
                    getActivity().runOnUiThread(() -> binding.staticMap.setImageDrawable(drawable));
                } else {
                    Log.e("DetailsFragment", "Impossible de charger l'image de la carte.");
                }
            }
        });
    }

    private void setupMediaGalleryViewPager() {
        Log.d("DetailsFragment", "Préparation de la configuration du ViewPager pour l'immobilier: " + (realEstate == null ? "null" : realEstate.toString()));


        if (realEstate != null) {
            viewModel.getRealEstateMediasByID(realEstate.getID()).observe(getViewLifecycleOwner(), new Observer<List<RealEstateMedia>>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onChanged(List<RealEstateMedia> mediaList) {
                    Log.d("DetailsFragment", "Médias chargés: " + mediaList.size());
                    // Reste du code...
                    if (!mediaList.isEmpty()) {
                        MediaGalleryAdapter adapter = new MediaGalleryAdapter(mediaList, DetailsFragment.this);
                        mediaViewPager2.setAdapter(adapter);
                        adapter.notifyDataSetChanged(); // Informez l'adaptateur que les données ont changé pour rafraîchir les vues.
                    } else {
                        Log.d("DetailsFragment", "La liste des médias est vide ou null");
                    }
                }
            });
        } else {
            Log.e("DetailsFragment", "L'objet estate est null lors de la configuration du ViewPager");
        }
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            realEstate = getArguments().getParcelable("REAL_ESTATE");

        }
        setHasOptionsMenu(true);

    }
    @Override
    public void onMapCreated(File mapImageFile) {
        updateMap(mapImageFile);
    }




    private void initializeComponents() {
        imagePopupWindow = new ImagePopupWindow();
    }









    private void updatePropertyDetails() {
        liveData = viewModel.getRealEstateMediasByID(realEstate.getID());
        observer = this::mediaObserver;
        liveData.observe(getViewLifecycleOwner(), observer);
        binding.address.setText(realEstate.getAddress());
        binding.description.setText(realEstate.getDescription());
        binding.surface.setText(getString(R.string.surface, realEstate.getSurface()));
        binding.bathrooms.setText(getString(R.string.number_of_bathrooms, realEstate.getBathrooms()));
        binding.bedrooms.setText(getString(R.string.number_of_bedrooms, realEstate.getBedrooms()));
        binding.rooms.setText(getString(R.string.number_of_rooms, realEstate.getRooms()));
        binding.agentTextView.setText(getString(R.string.agent, realEstate.getAgentName()));


        Log.d("TAG", "onCreateView: " + realEstate.toString());
    }


    private void updateMap(File mapImageFile) {
        if (isAdded() && getActivity() != null) {
            Glide.with(requireActivity())
                    .load(mapImageFile)
                    .override(Target.SIZE_ORIGINAL)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e("DetailsFragment", "Erreur de chargement de l'image : ", e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(binding.staticMap);
        }
    }



    private void mediaObserver(List<RealEstateMedia> mediaList) {
        realEstate.setMediaList(mediaList);
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
        realEstate.setFeaturedMediaUrl(newFeaturedUrl);
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            realEstateDao.updateFeaturedMediaUrl(realEstate.getId(), newFeaturedUrl);
        });
    }




    @Override
    public void onItemClick(RealEstateMedia media) {
        imagePopupWindow.showPopup(binding.getRoot(), media);


    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // Extraction de l'objet RealEstate des arguments du fragment
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("REAL_ESTATE")) {
            realEstate = arguments.getParcelable("REAL_ESTATE");
        }


        // Vérifiez si estate est initialisé avant de continuer
        if (realEstate == null) {
            Log.e("DetailsFragment", "L'objet RealEstate 'estate' est null.");

            return;
        }




        setupStaticMapClickListener();
        mediaViewPager2 = binding.mediaViewPager;
        setupMediaGalleryViewPager();



        // Configuration de la vue statique de la carte
        String location = extractLocationFromJsonPoint(realEstate.getJsonPoint());
        if (location != null) {
            String mapUrl = getStaticMapUrl(location);
            Glide.with(this).load(mapUrl).into(binding.staticMap);
        }
        // Bouton d'édition
        binding.editRealEstateButton.setOnClickListener(v -> {
            if (realEstate != null) {
                launchEditRealEstateActivity(realEstate);
            } else {
                Log.e("DetailsFragment", "Aucun bien immobilier à éditer.");
            }
        });


        // Configuration de la CheckBox pour le statut de vente
        CheckBox soldCheckBox = binding.statusCheckBox;
        soldCheckBox.setChecked(realEstate.isSold());
        TextView saleDateTextView = binding.saleDateTextView;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        if (realEstate.getSaleDate() != null) {
            saleDateTextView.setText(dateFormat.format(realEstate.getSaleDate()));
            saleDateTextView.setVisibility(View.VISIBLE);
        }


        // Gestionnaire pour le changement d'état de la CheckBox
        soldCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            realEstate.setSold(isChecked);
            if (isChecked) {
                showDatePicker(); // Affiche le DatePickerDialog pour sélectionner la date de vente
            } else {
                realEstate.setSaleDate(null); // Réinitialise la date de vente
                saleDateTextView.setVisibility(View.GONE);
                updateEstateInDatabase(); // Mise à jour de l'objet RealEstate dans la base de données

            }
        });
    }

    private void launchEditRealEstateActivity(RealEstate realEstate) {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), RealEstateEditor.class);
            intent.putExtra("REAL_ESTATE_TO_EDIT", realEstate);
            editRealEstateLauncher.launch(intent);
        } else {
            Log.e("DetailsFragment", "Activity is null");
        }
    }



    private final ActivityResultLauncher<Intent> editRealEstateLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    RealEstate updatedEstate = result.getData().getParcelableExtra("UPDATED_REAL_ESTATE");
                    updateRealEstateInDatabase(updatedEstate);
                }
                Log.e("DetailsFragment", "ActivityResultLauncher");
            });

    private void updateRealEstateInDatabase(RealEstate updatedEstate) {
        viewModel.createOrUpdateRealEstate(updatedEstate);
        Log.e("DetailsFragment", "updateRealEstateInDatabase");
    }

    private void updateEstateInDatabase() {
        if (viewModel != null) {
            viewModel.createOrUpdateRealEstate(realEstate);
        } else {
            Log.e("DetailsFragment", "ViewModel n'est pas initialisé.");
        }
    }




    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(year, month, dayOfMonth);
            Date saleDate = selectedCalendar.getTime();
            realEstate.setSaleDate(saleDate);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            binding.saleDateTextView.setText(dateFormat.format(saleDate));
            binding.saleDateTextView.setVisibility(View.VISIBLE);
            updateEstateInDatabase();

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));


        datePickerDialog.show();
    }


    private String extractLocationFromJsonPoint(String jsonPoint) {
     if (jsonPoint == null || jsonPoint.isEmpty()) {
            Log.e("DetailsFragment", "jsonPoint est null ou vide.");
            return "0,0";
        }



        try {
            JSONObject jsonObject = new JSONObject(jsonPoint);
            double latitude = jsonObject.optDouble("latitude", 0.0);
            double longitude = jsonObject.optDouble("longitude", 0.0);
            return latitude + "," + longitude;
        } catch (JSONException e) {
            e.printStackTrace();
            return "0,0";
        }
    }






    private String getStaticMapUrl(String location) {
        Log.d("DetailsFragment", "getStaticMapUrl ok");
        return "https://maps.googleapis.com/maps/api/staticmap?center=" + location + "&zoom=15&size=600x300&maptype=roadmap&markers=color:red%7Clabel:C%7C" + location + getString(R.string.MAPS_API_KEY);
    }




    private void setupStaticMapClickListener() {
        binding.staticMap.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SupportActivity.class);
            // Assurez-vous que l'objet RealEstate est sérialisable ou Parcelable.
            // Cet exemple suppose que vous avez déjà un objet RealEstate (estate) disponible.
            intent.putExtra("REAL_ESTATE", realEstate);
            startActivity(intent);
        });
    }




}

