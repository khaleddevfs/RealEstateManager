package com.openclassrooms.realestatemanager;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.openclassrooms.realestatemanager.adapters.RealEstateEditorRvAdapter;
import com.openclassrooms.realestatemanager.adapters.RealEstateEditorRvViewHolder;
import com.openclassrooms.realestatemanager.database.SaveRealEstateDB;
import com.openclassrooms.realestatemanager.databinding.ActivityRealEstateEditorBinding;
import com.openclassrooms.realestatemanager.fragments.MapFragment;
import com.openclassrooms.realestatemanager.injection.ViewModelFactory;
import com.openclassrooms.realestatemanager.models.RealEstate;
import com.openclassrooms.realestatemanager.models.RealEstateMedia;
import com.openclassrooms.realestatemanager.viewModel.RealEstateViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class RealEstateEditor extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 1;
    private RealEstateViewModel realEstateViewModel;
    private RealEstateEditorRvAdapter adapter;
    private ActivityRealEstateEditorBinding binding;
    private RealEstate realEstate;
    private List<RealEstateMedia> mediaList = new ArrayList<>();
    private String selectedAgentName = "";

    private final ActivityResultLauncher<Intent> pickImagesLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::handlePickImagesResult);
    private final ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::handleTakePictureResult);

    private AlertDialog.Builder photoOrGalleryDialogBuilder;
    // Global variables to hold the selected location data
    private Double selectedLatitude = null;
    private Double selectedLongitude = null;
    private String selectedAddress = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRealEstateEditorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        if (!Places.isInitialized()) {Places.initialize(getApplicationContext(), getString(R.string.MAPS_API_KEY));}

        adapter = new RealEstateEditorRvAdapter(mediaList);
        binding.rvSelectedPhotos.setAdapter(adapter);

        ViewModelFactory factory = ViewModelFactory.getInstance(getApplicationContext());
        realEstateViewModel = new ViewModelProvider(this, factory).get(RealEstateViewModel.class);

        initializeUI();
        initializePlaces();
        loadRealEstateData();




        realEstateViewModel.isSaveOperationComplete().observe(this, isComplete -> {
            if (isComplete) {
                finishWithResult();
            }
        });

        if (getIntent().hasExtra("REAL_ESTATE_TO_EDIT")) {
            RealEstate realEstateToEdit = getIntent().getParcelableExtra("REAL_ESTATE_TO_EDIT");
            Log.d("lodi", "RealEstate received: " + realEstateToEdit.toString());
            // Utilisez realEstateToEdit pour remplir les champs d'édition...
        } else {
            Log.d("lodi", "No RealEstate data received");
        }
    }
    /*
    private void initializePlaces() {
        // Initialisation du SDK Places
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.MAPS_API_KEY));
        }

        // Création d'une instance de l'AutocompleteSupportFragment
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Configuration des types de lieux à rechercher
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));

        // Écouteur pour la sélection d'un lieu
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Log.i("lodi", "Place: " + place.getName() + ", " + place.getLatLng());
                // Assurez-vous que l'objet realEstate est initialisé (soit un nouveau, soit récupéré pour la mise à jour)
                if (realEstate == null) {
                    realEstate = new RealEstate(); // Seulement si vous créez un nouvel objet, sinon récupérez l'existant
                }
               realEstate.setLatitude(place.getLatLng().latitude);
              realEstate.setLongitude(place.getLatLng().longitude);
                realEstate.setAddress(place.getAddress()); // Mettez à jour l'adresse
                // Ne pas oublier de persister les changements dans la base de données
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i("lodi", "An error occurred: " + status);
            }
        });
    }

     */


    private void initializePlaces() {
        // Initialisation du SDK Places
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.MAPS_API_KEY));
        }

        // Création d'une instance de l'AutocompleteSupportFragment
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Configuration des types de lieux à rechercher
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));

        // Écouteur pour la sélection d'un lieu
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Log.i("lodi", "Place: " + place.getName() + ", " + place.getLatLng());
                // Update global variables with the selected location data
                selectedLatitude = place.getLatLng().latitude;
                selectedLongitude = place.getLatLng().longitude;
                selectedAddress = place.getAddress();
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i("lodi", "An error occurred: " + status);
            }
        });
    }
    private void initializeUI() {
        binding.ivPhoto.setOnClickListener(view -> {
            setupPhotoOrGalleryDialog();
            checkPermissions();
        });

        setupAgentSpinner();
        setupEditTextListeners();
        binding.btSave.setOnClickListener(view -> btSaveClick());
    }

    private void setupAgentSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.agents_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinnerAgent = binding.spinnerAgent;
        spinnerAgent.setAdapter(adapter);
        spinnerAgent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedAgentName = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                selectedAgentName = ""; // Ou gardez la dernière valeur sélectionnée selon votre logique d'application
            }
        });
    }

    private void setupPhotoOrGalleryDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add Photo")
                .setMessage("Take a photo or choose from gallery?")
                .setPositiveButton("Take a photo", (dialogInterface, which) -> takePhoto())
                .setNegativeButton("Choose from gallery", (dialogInterface, which) -> pickImages())
                .create();
        dialog.show();
    }

    private void setupEditTextListeners() {
        // Simplifié pour l'exemple, peut être étendu avec plus de logique de validation
        View.OnFocusChangeListener listener = (view, hasFocus) -> {
            if (!hasFocus) validateEditText((EditText) view);
        };

        // Appliquez le même écouteur à tous les EditText pertinents
        binding.etBathrooms.setOnFocusChangeListener(listener);
        binding.etPrice.setOnFocusChangeListener(listener);
      //  binding.etLocation.setOnFocusChangeListener(listener);
        binding.etSurface.setOnFocusChangeListener(listener);
        binding.etRooms.setOnFocusChangeListener(listener);
        binding.etBedrooms.setOnFocusChangeListener(listener);
        binding.etName.setOnFocusChangeListener(listener);
        binding.etRegion.setOnFocusChangeListener(listener);
    }

    private void validateEditText(EditText editText) {
        if (editText.getText().toString().trim().isEmpty()) {
            editText.setError("Ce champ ne peut pas être vide");
        }
    }

    // Méthodes de chargement et de sauvegarde des données...
    private void loadRealEstateData() {
        realEstate = getIntent().getParcelableExtra("REAL_ESTATE");
        if (realEstate != null) {
            binding.etBathrooms.setText(String.format(Locale.getDefault(), "%d", realEstate.getBathrooms()));
            binding.etBedrooms.setText(String.format(Locale.getDefault(), "%d", realEstate.getBedrooms()));
            binding.etRooms.setText(String.format(Locale.getDefault(), "%d", realEstate.getRooms()));
            binding.etName.setText(realEstate.getName());
            binding.etRegion.setText(realEstate.getRegion());
            binding.textInputEditTextDescription.setText(realEstate.getDescription());
            mediaList = realEstate.getMediaList();
            binding.rvSelectedPhotos.setAdapter(new RealEstateEditorRvAdapter(mediaList));
            binding.etSurface.setText(String.format(Locale.getDefault(), "%d", realEstate.getSurface()));
          //  binding.etLocation.setText(realEstate.getLocation());
            binding.etPrice.setText(String.format(Locale.getDefault(), "%d", realEstate.getPrice()));
        }
      //  binding.btSave.setOnClickListener();
    }

    private void saveRealEstateData() {
        Log.d("RealEstateEditor", "Entering saveRealEstateData method");
        Log.d("lodi", "Starting saveRealEstateData");



        if (realEstate == null) {
            realEstate = createRealEstate();
            Log.d("RealEstateEditor", "Creating new RealEstate instance");

        }else {
            Log.d("RealEstateEditor", "RealEstate instance already exists");

        }


        Log.d("lodi", "RealEstate details: Name: " + realEstate.getName() + ", Price: " + realEstate.getPrice());

        setAdditionalRealEstateProperties(realEstate);

        new Thread(() -> {

            Log.d("lodi", "Saving RealEstate in background thread");

            SaveRealEstateDB database = SaveRealEstateDB.getInstance(getApplicationContext());

            long realEstateId = database.realEstateDao().createOrUpdateRealEstate(realEstate);
            Log.d("lodi", "RealEstate saved with ID: " + realEstateId);


            if (realEstate.getID() == 0) {
                realEstate.setID(realEstateId);
                for (RealEstateMedia media : mediaList) {
                    media.setRealEstateId(realEstateId);
                }
                Log.d("lodi", "Updated RealEstate ID for associated media");

            }

            for (RealEstateMedia media : mediaList) {
                database.realEstateMediaDao().addMedia(media);
                Log.d("lodi", "Saved media with path: " + media.getMediaUrl());

            }

            runOnUiThread(() -> {
                Toast.makeText(RealEstateEditor.this, "Real Estate saved successfully", Toast.LENGTH_SHORT).show();
                Log.d("lodi", "Real Estate saved successfully, finishing activity");

                finishWithResult();
            });
        }).start();
    }

    // Méthodes de gestion des images...
    private void handlePickImagesResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            ContentResolver contentResolver = getContentResolver();
            ClipData clipData = result.getData().getClipData();
            if (clipData != null) {
                processMultipleImages(clipData, contentResolver);
            } else if (result.getData().getData() != null) {
                processSingleImage(result.getData().getData(), contentResolver);
            }
        }
    }

    private void processSingleImage(Uri uri, ContentResolver contentResolver) {
        try {
            String path = saveImageToInternalStorage(uri, contentResolver);
            addMediaToCollection(path);
        } catch (IOException e) {
            Log.e("lodi", "Error processing single image", e);
        }
    }

    private void processMultipleImages(ClipData clipData, ContentResolver contentResolver) {
        for (int i = 0; i < clipData.getItemCount(); i++) {
            Uri uri = clipData.getItemAt(i).getUri();
            processSingleImage(uri, contentResolver);
        }
    }

    private String saveImageToInternalStorage(Uri uri, ContentResolver contentResolver) throws IOException {
        String name = UUID.randomUUID().toString();
        String path = getFilesDir() + "/" + name;
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
        File file = new File(getFilesDir(), name);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        }
        Log.d("lodi", "file abs path: " + path);
        return path;
    }

    private void addMediaToCollection(String path) {
        RealEstateMedia media;
        if (realEstate == null) {
            media = new RealEstateMedia(path, "");
        } else {
            media = new RealEstateMedia(realEstate.getID(), path, "");
        }
        mediaList.add(media);
        adapter.setRealEstateMediaList(mediaList);
    }

    private void handleTakePictureResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Bundle extras = result.getData().getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            // Enregistrez l'imageBitmap dans le stockage interne et ajoutez-la à la liste des médias
            try {
                String imagePath = saveBitmapToInternalStorage(imageBitmap);
                addMediaToCollection(imagePath);
            } catch (IOException e) {
                Log.e("RealEstateEditor", "Error saving image", e);
            }
        }
    }

    private String saveBitmapToInternalStorage(Bitmap bitmap) throws IOException {
        String name = UUID.randomUUID().toString() + ".jpg";
        File file = new File(getFilesDir(), name);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        }
        return file.getAbsolutePath();
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            takePictureLauncher.launch(takePictureIntent);
        }
    }

    private void pickImages() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        pickImagesLauncher.launch(Intent.createChooser(intent, "Select Images"));
    }

    // Méthodes de gestion des permissions...
    private void checkPermissions() {
        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_MEDIA_IMAGES
            };
        } else {
            permissions = new String[]{
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        }

        boolean shouldRequestPermission = false;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                shouldRequestPermission = true;
                break;
            }
        }

        if (shouldRequestPermission) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                photoOrGalleryDialog();
            }

        }
    }

    private void finishWithResult() {
        if (realEstate != null) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("EDITED_REAL_ESTATE", realEstate);
            setResult(Activity.RESULT_OK, resultIntent);
        } else {
            setResult(Activity.RESULT_CANCELED);
        }
        finish();
    }

    private RealEstate createRealEstate() {
        RealEstate realEstate = new RealEstate();

        realEstate.setName(binding.etName.getText().toString());
        realEstate.setRegion(binding.etRegion.getText().toString());
        realEstate.setDescription(binding.textInputEditTextDescription.getText().toString());
        realEstate.setPrice(Integer.parseInt(binding.etPrice.getText().toString()));
        // Utilisez les informations de localisation sélectionnées si disponibles
        if (selectedLatitude != null && selectedLongitude != null) {
            realEstate.setLatitude(selectedLatitude);
            realEstate.setLongitude(selectedLongitude);
            realEstate.setAddress(selectedAddress); // Définissez l'adresse sélectionnée
        }
        realEstate.setSurface(Integer.parseInt(binding.etSurface.getText().toString()));
        realEstate.setRooms(Integer.parseInt(binding.etRooms.getText().toString()));
        realEstate.setBedrooms(Integer.parseInt(binding.etBedrooms.getText().toString()));
        realEstate.setBathrooms(Integer.parseInt(binding.etBathrooms.getText().toString()));
        realEstate.setMediaList(mediaList);
        realEstate.setAgentName(selectedAgentName);


        return realEstate;


    }

    private void btSaveClick() {
        updateMediaCaptions();
        RealEstate realEstate = createRealEstate();
        setAdditionalRealEstateProperties(realEstate);
        realEstateViewModel.createOrUpdateRealEstate(realEstate);

    }

    private void setAdditionalRealEstateProperties(RealEstate realEstate) {
        if (realEstate.getID() == 0) {
            realEstate.setListingDate(new Date());
        }
    }

    private void updateMediaCaptions() {
        for (int index = 0; index < mediaList.size(); index++) {
            RealEstateMedia media = mediaList.get(index);
            if (index < binding.rvSelectedPhotos.getChildCount()) {
                String caption = ((RealEstateEditorRvViewHolder) Objects.requireNonNull(
                        binding.rvSelectedPhotos.findViewHolderForAdapterPosition(index)))
                        .getCaption().getText().toString();
                media.setMediaCaption(caption);
            }
        }
    }


    private void photoOrGalleryDialog() {
        AlertDialog dialog = photoOrGalleryDialogBuilder.create();
        dialog.show();
    }







}

