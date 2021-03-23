package io.github.dhina17.calculator;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.HorizontalScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.TextViewCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private HorizontalScrollView mScrollView;
    private MaterialTextView mCalculationView;
    private MaterialTextView mResultView;
    private StringBuffer mCalculationString;

    private static final int AUTO_SIZE_MIN_TEXT_SIZE = 12;
    private static final int AUTO_SIZE_MAX_TEXT_SIZE = 50;
    private static final int AUTO_SIZE_STEP_GRANULARITY = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCalculationView = findViewById(R.id.calculation_view);
        mResultView = findViewById(R.id.result_view);
        MaterialButton mDelButton = findViewById(R.id.button_delete);
        mScrollView = findViewById(R.id.scroll_view);

        /* Auto Size the text  */
        autoSizeText(mCalculationView);
        autoSizeText(mResultView);

        mCalculationString = new StringBuffer();

        /* Set long click listener for delete button to clear the calculation view */
        mDelButton.setOnLongClickListener(view -> {
            mCalculationString.setLength(0);
            updateCalculationView(mCalculationString);
            mResultView.setText("");
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

    public void onEqualButtonClick(View view) {
        String result;
        try{
            double cal = Calculator.calculate(mCalculationString.toString());
            result = String.format(Locale.getDefault(), "%.2f", cal);
        }catch(IllegalArgumentException e){
            result = "Syntax Error";
        }catch(ArithmeticException e){
            result = "Can't divide by 0";
        }

        mResultView.setText(result);
    }

    private void updateCalculationView(StringBuffer sb) {
        mCalculationView.setText(sb);
        // Keep the scroll at the end
        mScrollView.post(() -> mScrollView.fullScroll(View.FOCUS_RIGHT));
    }

    private void autoSizeText(MaterialTextView textView) {
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(textView,
                AUTO_SIZE_MIN_TEXT_SIZE, AUTO_SIZE_MAX_TEXT_SIZE, AUTO_SIZE_STEP_GRANULARITY,
                TypedValue.COMPLEX_UNIT_SP);
    }

}