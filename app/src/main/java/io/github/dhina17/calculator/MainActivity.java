package io.github.dhina17.calculator;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.TextViewCompat;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private TextView mCalculationView;
    private StringBuffer mCalculationString;

    private static final int AUTO_SIZE_MIN_TEXT_SIZE = 12;
    private static final int AUTO_SIZE_MAX_TEXT_SIZE = 50;
    private static final int AUTO_SIZE_STEP_GRANULARITY = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCalculationView = findViewById(R.id.calculation_view);
        MaterialButton mDelButton = findViewById(R.id.button_delete);

        /* Auto Size the text  */
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(mCalculationView,
                AUTO_SIZE_MIN_TEXT_SIZE, AUTO_SIZE_MAX_TEXT_SIZE, AUTO_SIZE_STEP_GRANULARITY,
                TypedValue.COMPLEX_UNIT_SP);

        mCalculationString = new StringBuffer();

        /* Set long click listener for delete button to clear the calculation view */
        mDelButton.setOnLongClickListener(view -> {
            mCalculationString.setLength(0);
            updateCalculationView(mCalculationString);
            return true;
        });
    }

    public void onButtonClick(View view) {
        MaterialButton button = (MaterialButton) view;
        mCalculationString.append(button.getText());
        updateCalculationView(mCalculationString);
    }

    public void onDelButtonClick(View view) {
        int len = mCalculationString.length();
        if (len > 0) {
            mCalculationString.deleteCharAt(len - 1);
            updateCalculationView(mCalculationString);
        }
    }

    private void updateCalculationView(StringBuffer sb) {
        mCalculationView.setText(sb);
    }

}