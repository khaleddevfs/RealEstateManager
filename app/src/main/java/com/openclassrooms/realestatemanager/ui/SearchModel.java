package com.openclassrooms.realestatemanager.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.injection.ViewModelFactory;
import com.openclassrooms.realestatemanager.models.RealEstate;
import com.openclassrooms.realestatemanager.viewModel.RealEstateViewModel;


import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
public class SearchModel extends DialogFragment {



    private RangeSeekBar<Integer> surfaceSeekBar, priceSeekBar;
    private EditText listedWeeksEditText, soldWeeksEditText, estateNameSearchEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialogTheme);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.Theme_AppCompat_Light_Dialog_Alert);
        builder.setView(createDialogView());
        return builder.create();
    }

    private ScrollView createDialogView() {
        LinearLayout dialogView = new LinearLayout(getActivity());
        dialogView.setOrientation(LinearLayout.VERTICAL);
        dialogView.setPadding(16, 16, 16, 16);

        ScrollView scrollView = new ScrollView(getActivity());
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        scrollView.addView(dialogView);

        initializeComponents(dialogView);

        return scrollView;
    }

    private void initializeComponents(LinearLayout dialogView) {
        // Initialize and add components like TextViews, EditTexts, RangeSeekBars, and Buttons
        estateNameSearchEditText = addEditText(dialogView, R.string.search_name_hint, InputType.TYPE_CLASS_TEXT, R.string.estate_name_search_text_view);
        surfaceSeekBar = addRangeSeekBar(dialogView, 10000, 1000000, R.string.choose_the_surface_range_m);
        priceSeekBar = addRangeSeekBar(dialogView, 10000000, 1000000000, R.string.choose_the_price_range);
        listedWeeksEditText = addEditText(dialogView, R.string.weeks_since_listed, InputType.TYPE_CLASS_NUMBER, R.string.weeks_since_listed);
        soldWeeksEditText = addEditText(dialogView, R.string.weeks_since_listed, InputType.TYPE_CLASS_NUMBER, R.string.weeks_since_sold);

        addButton(dialogView, R.string.search);
    }



    private EditText addEditText(ViewGroup parent, int hintResId, int inputType, int labelTextResId) {
        addTextView(parent, labelTextResId);
        EditText editText = new EditText(getActivity());
        editText.setHint(hintResId);
        editText.setInputType(inputType);
        parent.addView(editText);
        return editText;
    }

    private void addTextView(ViewGroup parent, int textResId) {
        TextView textView = new TextView(getActivity());
        textView.setText(textResId);
        parent.addView(textView);
    }

    private RangeSeekBar<Integer> addRangeSeekBar(ViewGroup parent, int minValue, int maxValue, int labelTextResId) {
        addTextView(parent, labelTextResId);
        final TextView minTextView = addValueTextView(parent, "Min: " + minValue); // Pour la valeur min
        final TextView maxTextView = addValueTextView(parent, "Max: " + maxValue); // Pour la valeur max
        RangeSeekBar<Integer> rangeSeekBar = new RangeSeekBar<>(requireActivity());
        rangeSeekBar.setRangeValues(minValue, maxValue);
        rangeSeekBar.setSelectedMinValue(minValue);
        rangeSeekBar.setSelectedMaxValue(maxValue);
        // Mettre à jour les TextViews lors du changement des valeurs
        rangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                minTextView.setText("Min: " + minValue); // Met à jour la valeur min
                maxTextView.setText("Max: " + maxValue); // Met à jour la valeur max
            }
        });
        parent.addView(rangeSeekBar);
        return rangeSeekBar;
    }


    private void addButton(ViewGroup parent, int textResId) {
        Button button = new Button(getActivity());
        button.setText(textResId);
        button.setOnClickListener(v -> {
            String name = estateNameSearchEditText.getText().toString();
            int minSurface = surfaceSeekBar.getSelectedMinValue();
            int maxSurface = surfaceSeekBar.getSelectedMaxValue();
            int minPrice = priceSeekBar.getSelectedMinValue();
            int maxPrice = priceSeekBar.getSelectedMaxValue();
            int listedWeeks = !listedWeeksEditText.getText().toString().isEmpty() ?
                    Integer.parseInt(listedWeeksEditText.getText().toString()) : 0;
            int soldWeeks = !soldWeeksEditText.getText().toString().isEmpty() ?
                    Integer.parseInt(soldWeeksEditText.getText().toString()) : 0;

            performSearch(name, minSurface, maxSurface, minPrice, maxPrice, listedWeeks, soldWeeks);
        });
        parent.addView(button);
    }


    private void performSearch(String name,int minSurface, int maxSurface, int minPrice, int maxPrice, int listedWeeks, int soldWeeks) {
        RealEstateViewModel mRealEstateViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance(requireContext())).get(RealEstateViewModel.class);

        Calendar calendar = Calendar.getInstance(), calendar1 = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR, -soldWeeks);
        calendar1.add(Calendar.WEEK_OF_YEAR,-listedWeeks);
        Date maxSaleDate = null;
        if(soldWeeks != 0)
            maxSaleDate = calendar.getTime();

        Date minListingDate = null;
        if(listedWeeks != 0)
            minListingDate = calendar1.getTime();

        mRealEstateViewModel.filterEstates(name,maxSaleDate,minListingDate,maxPrice,minPrice,maxSurface,minSurface).observe(this, realEstates -> {
            Intent intent = new Intent(requireContext(), MainActivity.class);

            Log.d("TAG", "performSearch: " + realEstates );
            intent.putParcelableArrayListExtra("filteredEstates",toParcelableList(realEstates));
            Log.d("TAG", "performSearch: FILTER ");
            startActivity(intent);
        });
    }
    private ArrayList<Parcelable> toParcelableList(List<RealEstate> realEstates) {
        return new ArrayList<>(realEstates);
    }

    private TextView addValueTextView(ViewGroup parent, String text) {
        TextView textView = new TextView(getActivity());
        textView.setText(text);
        parent.addView(textView);
        return textView;
    }

}

