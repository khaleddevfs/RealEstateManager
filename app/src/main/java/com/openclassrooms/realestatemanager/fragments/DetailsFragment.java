package com.openclassrooms.realestatemanager.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
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


    private void loadRealEstateData() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("REAL_ESTATE")) {
            estate = bundle.getParcelable("REAL_ESTATE");
            if (estate != null) {
                updateUi(); // Assurez-vous que cette méthode est appelée ici
            } else {
                Log.e("DetailsFragment", "L'objet RealEstate 'estate' est null.");
            }
        } else {
            Log.e("DetailsFragment", "Aucune donnée RealEstate n'est disponible dans le Bundle");
        }
    }


    private void updateUi() {
        updatePropertyDetails();

        if (estate != null && estate.getLatitude() != 0 && estate.getLongitude() != 0) {
            String mapImageUrl = generateMapImageUrl(estate.getLatitude(), estate.getLongitude());
            File mapImageFile = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "map_image.png");
            downloadAndSaveMapImage(mapImageUrl, mapImageFile);
        } else {
            Log.e("DetailsFragment", "Coordonnées invalides ou objet estate non initialisé");
        }
    }

    private void setupMediaGalleryViewPager() {
        Log.d("DetailsFragment", "Préparation de la configuration du ViewPager pour l'immobilier: " + (estate == null ? "null" : estate.toString()));


        if (estate != null) { // Assurez-vous que l'objet estate est initialisé
            viewModel.getRealEstateMediasByID(estate.getID()).observe(getViewLifecycleOwner(), new Observer<List<RealEstateMedia>>() {
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
    public void onMapCreated(File mapImageFile) {
        updateMap(mapImageFile);
    }




    private void initializeComponents() {
        imagePopupWindow = new ImagePopupWindow();
        // Other initialization code
    }


    private void initializeViewModel() {
        viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance(getContext())).get(RealEstateViewModel.class);
        realEstateDao = SaveRealEstateDB.getInstance(context).realEstateDao();
    }
/*
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


*/


    private String generateMapImageUrl(double latitude, double longitude) {
        String apiKey = getString(R.string.MAPS_API_KEY); // Obtenez la clé API comme String
        Log.e("DetailsFragment", "generateMapImageUrl");
        return "https://maps.googleapis.com/maps/api/staticmap?center=" + latitude + "," + longitude + "&zoom=15&size=600x300&maptype=roadmap&markers=color:red%7C" + latitude + "," + longitude + "&key=" + apiKey;
    }



    private void downloadAndSaveMapImage(String mapImageUrl, File mapImageFile) {
        // Implémentez votre logique pour télécharger l'image et la sauvegarder dans mapImageFile.
        // Cela peut impliquer l'utilisation d'une tâche asynchrone ou d'une bibliothèque de téléchargement d'image.
        // Exemple:
        new SaveImageTask(getContext(), image -> {
            if (image != null) {
                updateMap(mapImageFile);
            } else {
                Log.e("DetailsFragment", "Erreur lors du téléchargement de l'image de la carte.");
            }
        }).execute(mapImageUrl, mapImageFile.getAbsolutePath());
    }

    private void updatePropertyDetails() {
        liveData = viewModel.getRealEstateMediasByID(estate.getID());
        observer = this::mediaObserver;
        liveData.observe(getViewLifecycleOwner(), observer);
        binding.address.setText(estate.getAddress());
        binding.description.setText(estate.getDescription());
        binding.surface.setText(getString(R.string.surface, estate.getSurface()));
        binding.bathrooms.setText(getString(R.string.number_of_bathrooms, estate.getBathrooms()));
        binding.bedrooms.setText(getString(R.string.number_of_bedrooms, estate.getBedrooms()));
        binding.rooms.setText(getString(R.string.number_of_rooms, estate.getRooms()));
        binding.agentTextView.setText(getString(R.string.agent, estate.getAgentName()));


        Log.d("TAG", "onCreateView: " + estate.toString());
    }

/*
    private void updateMap(File mapImageFile) {
        if (isAdded()) {
            if (getActivity() != null) {
                requireActivity().runOnUiThread(() -> Glide.with(requireActivity())
                        .load(mapImageFile)
                        .override(Target.SIZE_ORIGINAL)
                        .into(binding.staticMap));
            }
        }


    }

 */

    private void updateMap(File mapImageFile) {
        if (isAdded() && getActivity() != null) {
            Glide.with(requireActivity())
                    .load(mapImageFile)
                    .override(Target.SIZE_ORIGINAL)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e("DetailsFragment", "Erreur de chargement de l'image : ", e);
                            return false; // Important pour indiquer si l'événement a été géré.
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false; // Ici aussi, indique si l'événement a été géré.
                        }
                    })
                    .into(binding.staticMap);
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
        imagePopupWindow.showPopup(binding.getRoot(), media);


    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // Extraction de l'objet RealEstate des arguments du fragment
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("REAL_ESTATE")) {
            estate = arguments.getParcelable("REAL_ESTATE");
        }


        // Vérifiez si estate est initialisé avant de continuer
        if (estate == null) {
            Log.e("DetailsFragment", "L'objet RealEstate 'estate' est null.");
            // Gestion d'erreur: Vous pouvez fermer le fragment ou afficher un message
            return; // Arrêtez l'exécution supplémentaire pour éviter les erreurs NullPointerException
        }


        // Configuration de la vue statique de la carte
        String location = extractLocationFromJsonPoint(estate.getJsonPoint());
        if (location != null) {
            String mapUrl = getStaticMapUrl(location);
            Glide.with(this).load(mapUrl).into(binding.staticMap);
        }


        setupStaticMapClickListener();
        mediaViewPager2 = binding.mediaViewPager;
        setupMediaGalleryViewPager();


        // Bouton d'édition
        binding.editRealEstateButton.setOnClickListener(v -> {
            if (estate != null) {
                launchEditRealEstateActivity(estate);
            } else {
                Log.e("DetailsFragment", "Aucun bien immobilier à éditer.");
            }
        });


        // Configuration de la CheckBox pour le statut de vente
        CheckBox soldCheckBox = binding.statusCheckBox;
        soldCheckBox.setChecked(estate.isSold());
        TextView saleDateTextView = binding.saleDateTextView;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        if (estate.getSaleDate() != null) {
            saleDateTextView.setText(dateFormat.format(estate.getSaleDate()));
            saleDateTextView.setVisibility(View.VISIBLE);
        }


        // Gestionnaire pour le changement d'état de la CheckBox
        soldCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            estate.setSold(isChecked);
            if (isChecked) {
                showDatePicker(); // Affiche le DatePickerDialog pour sélectionner la date de vente
            } else {
                estate.setSaleDate(null); // Réinitialise la date de vente
                saleDateTextView.setVisibility(View.GONE);
                updateEstateInDatabase(); // Mise à jour de l'objet RealEstate dans la base de données

            }
        });
    }

    private void launchEditRealEstateActivity(RealEstate realEstate) {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), RealEstateEditor.class);
            intent.putExtra("REAL_ESTATE_TO_EDIT", realEstate); // Assurez-vous que RealEstate implémente Parcelable
            editRealEstateLauncher.launch(intent);
        } else {
            Log.e("DetailsFragment", "Activity is null");
        }
    }



    // Gestionnaire du résultat de l'activité d'édition
    private final ActivityResultLauncher<Intent> editRealEstateLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    RealEstate updatedEstate = result.getData().getParcelableExtra("UPDATED_REAL_ESTATE");
                    updateRealEstateInDatabase(updatedEstate);
                }
                Log.e("DetailsFragment", "ActivityResultLauncher");
            });

    // Méthode pour mettre à jour le RealEstate et ses médias dans la base de données
    private void updateRealEstateInDatabase(RealEstate updatedEstate) {
        viewModel.createOrUpdateRealEstate(updatedEstate);
        Log.e("DetailsFragment", "updateRealEstateInDatabase");
    }

    private void updateEstateInDatabase() {
        if (viewModel != null) {
            viewModel.createOrUpdateRealEstate(estate); // Sauvegardez les modifications dans la base de données
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
            estate.setSaleDate(saleDate);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            binding.saleDateTextView.setText(dateFormat.format(saleDate));
            binding.saleDateTextView.setVisibility(View.VISIBLE);
            updateEstateInDatabase(); // Mise à jour de l'objet RealEstate dans la base de données

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));


        datePickerDialog.show();
    }


/*
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


       mediaViewPager2 = binding.mediaViewPager;


       setupMediaGalleryViewPager();




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
       CheckBox soldCheckBox = binding.statusCheckBox;
       soldCheckBox.setChecked(estate.isSold()); // Assurez-vous que l'état de la checkbox reflète l'état actuel de l'objet
       TextView saleDateTextView = binding.saleDateTextView;
       if (estate.getSaleDate() != null) {
           // Formatez la date comme vous le souhaitez
           SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
           saleDateTextView.setText(dateFormat.format(estate.getSaleDate()));
           saleDateTextView.setVisibility(View.VISIBLE);
       }


       soldCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
           if (isChecked) {
               showDatePicker(); // Affiche le DatePickerDialog lorsque la checkbox est cochée
           } else {
               estate.setSold(false);
               estate.setSaleDate(null); // Réinitialise la date de vente si la checkbox est décochée
               saleDateTextView.setVisibility(View.GONE);
               updateEstateInDatabase();
           }
       });




   }




   private void updateEstateInDatabase() {
       viewModel.createOrUpdateRealEstate(estate); // Mettez à jour l'immobilier dans la base de données
   }


   private void showDatePicker() {
       Calendar calendar = Calendar.getInstance();
       DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
           Calendar selectedCalendar = Calendar.getInstance();
           selectedCalendar.set(year, month, dayOfMonth);
           Date saleDate = selectedCalendar.getTime();
           estate.setSold(true);
           estate.setSaleDate(saleDate);
           updateEstateInDatabase();


           SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
           binding.saleDateTextView.setText(dateFormat.format(saleDate));
           binding.saleDateTextView.setVisibility(View.VISIBLE);
       }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));


       datePickerDialog.show();
   }
*/


/*
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


*/









    private String extractLocationFromJsonPoint(String jsonPoint) {
        // Vérifiez si jsonPoint est null avant de continuer
     if (jsonPoint == null || jsonPoint.isEmpty()) {
            Log.e("DetailsFragment", "jsonPoint est null ou vide.");
            return "0,0"; // Retourner une valeur par défaut pour éviter l'exception
        }



        try {
            JSONObject jsonObject = new JSONObject(jsonPoint);
            double latitude = jsonObject.optDouble("latitude", 0.0);
            double longitude = jsonObject.optDouble("longitude", 0.0);
            return latitude + "," + longitude;
        } catch (JSONException e) {
            e.printStackTrace();
            return "0,0"; // Retourner une valeur par défaut en cas d'erreur de parsing
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
            intent.putExtra("REAL_ESTATE", estate);
            startActivity(intent);
        });
    }




}

