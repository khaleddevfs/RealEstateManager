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
        Log.d("SearchModel", "onCreate: Dialog created");

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d("SearchModel", "onCreateDialog: Creating search dialog");

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.Theme_AppCompat_Light_Dialog_Alert);
        builder.setView(createDialogView());
        return builder.create();
    }

    private ScrollView createDialogView() {
        Log.d("SearchModel", "createDialogView: Creating dialog view");

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
        Log.d("SearchModel", "initializeComponents: Initializing components for search dialog");

        estateNameSearchEditText = addEditText(dialogView, R.string.search_name_hint, InputType.TYPE_CLASS_TEXT, R.string.estate_name_search_text_view);
        surfaceSeekBar = addRangeSeekBar(dialogView, 0, 300, R.string.choose_the_surface_range_m);
        priceSeekBar = addRangeSeekBar(dialogView, 0, 10000000, R.string.choose_the_price_range);
        listedWeeksEditText = addEditText(dialogView, R.string.weeks_since_listed, InputType.TYPE_CLASS_NUMBER, R.string.weeks_since_listed);
        soldWeeksEditText = addEditText(dialogView, R.string.weeks_since_listed, InputType.TYPE_CLASS_NUMBER, R.string.weeks_since_sold);

        addButton(dialogView, R.string.search);
    }

    private EditText addEditText(ViewGroup parent, int hintResId, int inputType, int labelTextResId) {
        addTextView(parent, labelTextResId);
        EditText editText = new EditText(getActivity());
        editText.setHint(hintResId);
        editText.setInputType(inputType);
        editText.setBackgroundColor(getResources().getColor(R.color.editTextBackground));
        editText.setTextColor(getResources().getColor(R.color.editTextTextColor));
        editText.setPadding(20, 20, 20, 20);
        parent.addView(editText);
        return editText;
    }

    private void addTextView(ViewGroup parent, int textResId) {
        TextView textView = new TextView(getActivity());
        textView.setText(textResId);
        textView.setTextColor(getResources().getColor(R.color.textViewTextColor));
        textView.setPadding(0, 10, 0, 10);
        parent.addView(textView);
    }

    private RangeSeekBar<Integer> addRangeSeekBar(ViewGroup parent, int minValue, int maxValue, int labelTextResId) {
        addTextView(parent, labelTextResId);
        final TextView minTextView = addValueTextView(parent, "Min: " + minValue);
        final TextView maxTextView = addValueTextView(parent, "Max: " + maxValue);
        RangeSeekBar<Integer> rangeSeekBar = new RangeSeekBar<>(requireActivity());
        rangeSeekBar.setRangeValues(minValue, maxValue);
        rangeSeekBar.setSelectedMinValue(minValue);
        rangeSeekBar.setSelectedMaxValue(maxValue);
        rangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                minTextView.setText("Min: " + minValue);
                maxTextView.setText("Max: " + maxValue);
            }
        });
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 20, 0, 20);
        rangeSeekBar.setLayoutParams(layoutParams);
        parent.addView(rangeSeekBar);
        return rangeSeekBar;
    }

    private void addButton(ViewGroup parent, int textResId) {
        Button button = new Button(getActivity(), null, 0, R.style.Widget_Button_Colored);
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

        Log.d("SearchModel", "performSearch: Performing search with parameters - Name: " + name + ", Surface: " + minSurface + "-" + maxSurface + ", Price: " + minPrice + "-" + maxPrice + ", Listed Weeks: " + listedWeeks + ", Sold Weeks: " + soldWeeks);

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
            Log.d("SearchModel", "performSearch: Found " + realEstates.size() + " estates matching criteria");

            Intent intent = new Intent(requireContext(), MainActivity.class);

            Log.d("SearchModel", "performSearch: " + realEstates );
            intent.putParcelableArrayListExtra("filteredEstates",toParcelableList(realEstates));
            Log.d("SearchModel", "performSearch: FILTER ");
            startActivity(intent);
        });
    }
    private ArrayList<Parcelable> toParcelableList(List<RealEstate> realEstates) {
        Log.d("SearchModel", "toParcelableList: Converting list to Parcelable list");

        return new ArrayList<>(realEstates);
    }

    private TextView addValueTextView(ViewGroup parent, String text) {
        TextView textView = new TextView(getActivity());
        textView.setText(text);
        parent.addView(textView);
        return textView;
    }

}
