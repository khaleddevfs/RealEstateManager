package com.openclassrooms.realestatemanager.injection;



import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.openclassrooms.realestatemanager.database.SaveRealEstateDB;
import com.openclassrooms.realestatemanager.repositories.RealEstateMediaRepo;
import com.openclassrooms.realestatemanager.repositories.RealEstateRepo;
import com.openclassrooms.realestatemanager.viewModel.RealEstateViewModel;


import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static volatile ViewModelFactory modelFactory;
    private final RealEstateRepo realEstateRepo;
    private final RealEstateMediaRepo realEstateMediaRepo;
    private final Executor executor;

    public ViewModelFactory(Context context) {
        SaveRealEstateDB db = SaveRealEstateDB.getInstance(context);
        realEstateRepo = new RealEstateRepo(db.realEstateDao());
        realEstateMediaRepo = new RealEstateMediaRepo(db.realEstateMediaDao());
        executor = Executors.newSingleThreadExecutor();
    }

    public static ViewModelFactory getInstance(Context context) {
        if (modelFactory == null) {
            synchronized (ViewModelFactory.class) {
                if (modelFactory == null) {
                    modelFactory = new ViewModelFactory(context);
                }
            }
        }
        return modelFactory;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RealEstateViewModel.class)) {

            return Objects.requireNonNull(modelClass.cast(new RealEstateViewModel(realEstateRepo, realEstateMediaRepo, executor)));

        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
