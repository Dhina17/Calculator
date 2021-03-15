package io.github.dhina17.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private TextView calculationView;
    private StringBuffer calculationString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calculationView = findViewById(R.id.calculation_view);
        calculationString = new StringBuffer();
    }

    public void onButtonClick(View view) {
        MaterialButton button = (MaterialButton) view;
        calculationString.append(button.getText());
        updateCalculationView(calculationString);
    }

    public void onDelButtonClick(View view) {
        int len = calculationString.length();
        if (len > 0) {
            calculationString.deleteCharAt(len - 1);
            updateCalculationView(calculationString);
        }
    }

    private void updateCalculationView(StringBuffer sb) {
        calculationView.setText(sb);
    }

}