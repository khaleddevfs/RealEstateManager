package com.openclassrooms.realestatemanager;



import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.openclassrooms.realestatemanager.adapters.RealEstateEditorRvViewHolder;
import com.openclassrooms.realestatemanager.databinding.ActivityRealEstateEditorBinding;
import com.openclassrooms.realestatemanager.models.RealEstate;
import com.openclassrooms.realestatemanager.models.RealEstateMedia;



import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

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
            // ... other property checks and assignments ...
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

    private void handlePickImagesResult(ActivityResult result) {
        // Handle the result of the image picker
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

        ActivityResultLauncher<Intent> mPickImagesLauncher = null;
        mPickImagesLauncher.launch(Intent.createChooser(intent, "Select Images"));
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

}
