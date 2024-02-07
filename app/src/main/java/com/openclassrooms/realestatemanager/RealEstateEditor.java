package com.openclassrooms.realestatemanager;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.openclassrooms.realestatemanager.adapters.RealEstateEditorRvAdapter;
import com.openclassrooms.realestatemanager.adapters.RealEstateEditorRvViewHolder;
import com.openclassrooms.realestatemanager.database.SaveRealEstateDB;
import com.openclassrooms.realestatemanager.databinding.ActivityRealEstateEditorBinding;
import com.openclassrooms.realestatemanager.models.RealEstate;
import com.openclassrooms.realestatemanager.models.RealEstateMedia;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
/*
public class RealEstateEditor extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGES = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_PERMISSION_CODE = 3;

    private ActivityRealEstateEditorBinding binding;
    private RealEstate realEstate;
    private List<RealEstateMedia> mediaList = new ArrayList<>();

    private final ActivityResultLauncher<Intent> pickImagesLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), this::handlePickImagesResult);

    private final ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), this::handleTakePictureResult);

    private AlertDialog.Builder photoOrGalleryDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRealEstateEditorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initializeUI();
        loadRealEstateData();
    }


    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        photoOrGalleryDialogBuilder = new AlertDialog.Builder(this);
        photoOrGalleryDialogBuilder.setTitle("Add Photo");
        photoOrGalleryDialogBuilder.setMessage("Take a photo or choose from gallery?");

        photoOrGalleryDialogBuilder.setPositiveButton("Take a photo", (dialog, which) -> takePhoto());

        photoOrGalleryDialogBuilder.setNegativeButton("Choose from gallery", (dialog, which) -> pickImages());

        return super.onCreateView(parent, name, context, attrs);
    }


    private void initializeUI() {
        setupPhotoOrGalleryDialog();
        binding.ivPhoto.setOnClickListener(view -> checkPermissions());
        setupEditTextListeners();
        binding.btSave.setOnClickListener(view -> saveRealEstateData());
    }

    private void setupPhotoOrGalleryDialog() {
        photoOrGalleryDialogBuilder = new AlertDialog.Builder(this);
        photoOrGalleryDialogBuilder.setTitle("Add Photo");
        photoOrGalleryDialogBuilder.setMessage("Take a photo or choose from gallery?");
        photoOrGalleryDialogBuilder.setPositiveButton("Take a photo", (dialog, which) -> takePhoto());
        photoOrGalleryDialogBuilder.setNegativeButton("Choose from gallery", (dialog, which) -> pickImages());
    }

    private void setupEditTextListeners() {

            View.OnFocusChangeListener listener = (view, hasFocus) -> {
                if (!hasFocus) {
                    // Ici, vous pouvez ajouter votre code de validation ou d'autres actions
                    // Par exemple, vous pouvez appeler une méthode validateEditText
                    validateEditText((EditText) view);
                }
            };

            binding.etBathrooms.setOnFocusChangeListener(listener);
            binding.etPrice.setOnFocusChangeListener(listener);
            binding.etLocation.setOnFocusChangeListener(listener);
            binding.etSurface.setOnFocusChangeListener(listener);
            binding.etRooms.setOnFocusChangeListener(listener);
            binding.etBedrooms.setOnFocusChangeListener(listener);
            binding.etName.setOnFocusChangeListener(listener);
            binding.etRegion.setOnFocusChangeListener(listener);
        }


        private void validateEditText(EditText editText) {
            // Ici, vous pouvez ajouter votre logique de validation pour le EditText.
            // Par exemple, vérifier si le champ est vide :
            if (editText.getText().toString().trim().isEmpty()) {
                editText.setError("Ce champ ne peut pas être vide");
            }
            // Vous pouvez ajouter d'autres règles de validation ici si nécessaire.
        }



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
         //   binding.rvSelectedPhotos.setAdapter(new RealEstateEditorRvAdapter(realEstateMedias));
            binding.etSurface.setText(String.format(Locale.getDefault(), "%d", realEstate.getSurface()));
            binding.etLocation.setText(realEstate.getLocation());
            binding.etPrice.setText(String.format(Locale.getDefault(), "%d", realEstate.getPrice()));
        }
        binding.btSave.setOnClickListener(view -> btSaveClick());

    }






    private void btSaveClick() {
        updateMediaCaptions();
        RealEstate realEstate = createRealEstate();
        setAdditionalRealEstateProperties(realEstate);
        finishWithResult(realEstate);
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

    private RealEstate createRealEstate() {
        return null;
        
    }

    private void setAdditionalRealEstateProperties(RealEstate realEstate) {
        realEstate.setAgentName(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName());
        if (realEstate != null) {
            realEstate.setID(realEstate.getID());
        } else {
            realEstate.setListingDate(new Date());
        }
    }

    private void finishWithResult(RealEstate realEstate) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("EDITED_REAL_ESTATE", realEstate);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void saveRealEstateData() {
        // Save real estate data
    }
// Path/filename: [appropriate file location within the Android project]

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
    }


    private void handleTakePictureResult(ActivityResult result) {
        // Handle the result of the picture capture
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

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.CAMERA,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.READ_MEDIA_IMAGES},
                        REQUEST_PERMISSION_CODE);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.CAMERA,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        REQUEST_PERMISSION_CODE);

            }
        }

    }

    private void saveImageToGallery(Bitmap bitmap) {
        // Save the bitmap image to the gallery
    }

    // Other overridden methods like onRequestPermissionsResult, onActivityResult, etc.


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                photoOrGalleryDialog();
            }

        }
    }

    private void photoOrGalleryDialog() {
        AlertDialog dialog = photoOrGalleryDialogBuilder.create();
        dialog.show();
    }
}

 */


// Path/filename: [appropriate file location within the Android project]

public class RealEstateEditor extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGES = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_PERMISSION_CODE = 3;

    private ActivityRealEstateEditorBinding binding;
    private RealEstate realEstate;
    private List<RealEstateMedia> mediaList = new ArrayList<>();

    private SaveRealEstateDB saveRealEstateDB;
    private AlertDialog.Builder photoOrGalleryDialogBuilder;

    private final ActivityResultLauncher<Intent> pickImagesLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), this::handlePickImagesResult);

    private final ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), this::handleTakePictureResult);



    public RealEstateEditor() {
        super();
    }

    // Lifecycle methods...
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRealEstateEditorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initializeUI();
        loadRealEstateData();
    }

    // UI setup methods...
    private void initializeUI() {
        setupPhotoOrGalleryDialog();
        binding.ivPhoto.setOnClickListener(view -> checkPermissions());
        setupEditTextListeners();
        binding.btSave.setOnClickListener(view -> saveRealEstateData());
    }

    private void setupPhotoOrGalleryDialog() {
        photoOrGalleryDialogBuilder = new AlertDialog.Builder(this);
        photoOrGalleryDialogBuilder.setTitle("Add Photo");
        photoOrGalleryDialogBuilder.setMessage("Take a photo or choose from gallery?");
        photoOrGalleryDialogBuilder.setPositiveButton("Take a photo", (dialog, which) -> takePhoto());
        photoOrGalleryDialogBuilder.setNegativeButton("Choose from gallery", (dialog, which) -> pickImages());
    }

    private void setupEditTextListeners() {
        View.OnFocusChangeListener listener = (view, hasFocus) -> {
            if (!hasFocus) {
                // Ici, vous pouvez ajouter votre code de validation ou d'autres actions
                // Par exemple, vous pouvez appeler une méthode validateEditText
                validateEditText((EditText) view);
            }
        };



        binding.etBathrooms.setOnFocusChangeListener(listener);
        binding.etPrice.setOnFocusChangeListener(listener);
        binding.etLocation.setOnFocusChangeListener(listener);
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

    // Data loading and saving methods...
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
            binding.etLocation.setText(realEstate.getLocation());
            binding.etPrice.setText(String.format(Locale.getDefault(), "%d", realEstate.getPrice()));
        }
        binding.btSave.setOnClickListener(view -> btSaveClick());
    }

    private void saveRealEstateData() {
        // Création d'une nouvelle instance de RealEstate si elle n'existe pas déjà
        if (realEstate == null) {
            realEstate = createRealEstate();
        }

        // Mise à jour des propriétés supplémentaires de RealEstate
        setAdditionalRealEstateProperties(realEstate);

        // Utiliser un thread séparé pour l'opération de base de données, car elle ne doit pas être exécutée sur le thread principal
        new Thread(() -> {
            // Obtention de l'instance de la base de données
            SaveRealEstateDB database = SaveRealEstateDB.getInstance(getApplicationContext());

            // Insertion ou mise à jour de l'immobilier dans la base de données
            long realEstateId = database.realEstateDao().createOrUpdateRealEstate(realEstate);

            // Mise à jour de l'ID de RealEstate pour tous les médias si c'est un nouvel immobilier
            if (realEstate.getID() == 0) {
                realEstate.setID(realEstateId);
                for (RealEstateMedia media : mediaList) {
                    media.setRealEstateId(realEstateId);
                }
            }

            // Insertion ou mise à jour des médias associés à l'immobilier dans la base de données
            for (RealEstateMedia media : mediaList) {
                database.realEstateMediaDao().addMedia(media);
            }

            // Exécution des actions nécessaires après la sauvegarde (par exemple, retourner au précédent écran ou afficher un message)
            runOnUiThread(() -> {
                // Actions à exécuter sur le thread principal après la sauvegarde
                Toast.makeText(RealEstateEditor.this, "Real Estate saved successfully", Toast.LENGTH_SHORT).show();
                finishWithResult(realEstate);
            });
        }).start();
    }


    // Image handling methods...
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
        }    }

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
        return path;    }



    private void addMediaToCollection(String path) {
        RealEstateMedia media;
        if (realEstate == null) {
            media = new RealEstateMedia(path, "");
        } else {
            media = new RealEstateMedia(realEstate.getID(), path, "");
        }
        mediaList.add(media);
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

    // Permissions handling methods...
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
        if (requestCode == REQUEST_PERMISSION_CODE) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                photoOrGalleryDialog();
            }

        }
    }

    private void photoOrGalleryDialog() {
        AlertDialog dialog = photoOrGalleryDialogBuilder.create();
        dialog.show();
    }

    private void btSaveClick() {
        updateMediaCaptions();
        RealEstate realEstate = createRealEstate();
        setAdditionalRealEstateProperties(realEstate);
        finishWithResult(realEstate);
    }

    private void finishWithResult(RealEstate realEstate) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("EDITED_REAL_ESTATE", realEstate);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
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

    private RealEstate createRealEstate() {
        RealEstate realEstate = new RealEstate();

        realEstate.setName(binding.etName.getText().toString());
        realEstate.setRegion(binding.etRegion.getText().toString());
        realEstate.setDescription(binding.textInputEditTextDescription.getText().toString());
        realEstate.setPrice(Integer.parseInt(binding.etPrice.getText().toString()));
        realEstate.setLocation(binding.etLocation.getText().toString());
        realEstate.setSurface(Integer.parseInt(binding.etSurface.getText().toString()));
        realEstate.setRooms(Integer.parseInt(binding.etRooms.getText().toString()));
        realEstate.setBedrooms(Integer.parseInt(binding.etBedrooms.getText().toString()));
        realEstate.setBathrooms(Integer.parseInt(binding.etBathrooms.getText().toString()));
        realEstate.setMediaList(mediaList);

        return realEstate;

    }

    private void setAdditionalRealEstateProperties(RealEstate realEstate) {
        realEstate.setAgentName(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName());
        if (realEstate != null) {
            realEstate.setID(realEstate.getID());
        } else {
            realEstate.setListingDate(new Date());
        }
    }
}
