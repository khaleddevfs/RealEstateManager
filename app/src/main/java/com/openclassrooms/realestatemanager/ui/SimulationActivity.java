



package com.openclassrooms.realestatemanager.ui;

        import android.os.Bundle;
        import androidx.appcompat.app.AppCompatActivity;
        import android.widget.EditText;
        import android.widget.Button;
        import android.view.View;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.openclassrooms.realestatemanager.R;

public class SimulationActivity extends AppCompatActivity {

    private TextView tvMonthlyPayment;
    private EditText etLoanAmount;
    private EditText etInterestRate;
    private EditText etLoanPeriod;
    private Button btnCalculate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation); // Assurez-vous d'avoir un layout activity_simulation

        etLoanAmount = findViewById(R.id.etLoanAmount);
        etInterestRate = findViewById(R.id.etInterestRate);
        etLoanPeriod = findViewById(R.id.etLoanPeriod);
        btnCalculate = findViewById(R.id.btnCalculate);
        tvMonthlyPayment = findViewById(R.id.tvMonthlyPayment);


        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateLoan();
            }
        });
    }

    private void calculateLoan() {
        // Récupération des valeurs
        String loanAmountStr = etLoanAmount.getText().toString();
        String interestRateStr = etInterestRate.getText().toString();
        String loanPeriodStr = etLoanPeriod.getText().toString();

        // Validation de base et calcul du prêt
        if (!loanAmountStr.isEmpty() && !interestRateStr.isEmpty() && !loanPeriodStr.isEmpty()) {
            double loanAmount = Double.parseDouble(loanAmountStr);
            double interestRate = Double.parseDouble(interestRateStr) / 100;
            int loanPeriod = Integer.parseInt(loanPeriodStr);

            double monthlyRate = interestRate / 12.0;
            int loanPeriodMonths = loanPeriod * 12;

            double monthlyPayment = (loanAmount * monthlyRate) / (1 - Math.pow(1 + monthlyRate, -loanPeriodMonths));

            // Mise à jour de la TextView avec le résultat
            tvMonthlyPayment.setText(getString(R.string.monthly_payment) + ": " + String.format("%.2f", monthlyPayment));
        } else {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_LONG).show();
        }
    }
}
