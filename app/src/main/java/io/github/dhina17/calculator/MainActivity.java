package io.github.dhina17.calculator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.udojava.evalex.Expression;

import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity {

    private HorizontalScrollView mScrollView;
    private MaterialTextView mCalculationView;
    private MaterialTextView mResultView;
    private StringBuffer mCalculationString;
    private ViewSwitcher mViewSwitch;
    private int mNightMode;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCalculationView = findViewById(R.id.calculation_view);
        mResultView = findViewById(R.id.result_view);
        mViewSwitch = findViewById(R.id.view_switch);

        MaterialButton mDelButton = findViewById(R.id.button_delete);
        mScrollView = findViewById(R.id.scroll_view);

        mCalculationString = new StringBuffer();

        /* Restore the prev state of calculation view when theme changed */
        if (savedInstanceState != null) {
            mCalculationString.append(savedInstanceState.getString("calculation_str"));
            updateCalculationView(mCalculationString);
        }

        sharedPreferences = getSharedPreferences(
                "io.github.dhina17.Calculator.shared_prefs", MODE_PRIVATE);

        /* Default night mode */
        mNightMode = sharedPreferences.getInt("night_mode",
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        /* Apply the theme */
        AppCompatDelegate.setDefaultNightMode(mNightMode);

        /* Set long click listener for delete button to clear the calculation view */
        mDelButton.setOnLongClickListener(view -> {
            mCalculationString.setLength(0);
            mCalculationString.append("0");
            updateCalculationView(mCalculationString);
            mResultView.setText("0");
            return true;
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("calculation_str", mCalculationString.toString());
    }

    public void onButtonClick(View view) {
        MaterialButton button = (MaterialButton) view;
        String buttonText = button.getText().toString();
        int len = mCalculationString.length();

        /* Remove the zero in front of the expr after restore zero with del button */
        if(len > 0 && mCalculationString.charAt(0) == '0') {
            mCalculationString.deleteCharAt(0);
            len = mCalculationString.length();
        }

        if (len > 0 && buttonText.length() < 3) {
            char prevText = mCalculationString.charAt(len - 1);
            if (!Character.isDigit(prevText) && !Character.isDigit(buttonText.charAt(0))
                    && !(buttonText.equals("π") || buttonText.equals("√") || buttonText.equals("(")
                    || buttonText.equals(")"))) {
                mCalculationString.deleteCharAt(len - 1);
            }
        } else if (buttonText.length() >= 3 || buttonText.contains("√")) {
            buttonText += "(";
        }
        mCalculationString.append(buttonText);
        updateCalculationView(mCalculationString);
    }

    public void onDelButtonClick(View view) {
        int len = mCalculationString.length();

        if (len > 0) {
            mCalculationString.deleteCharAt(len - 1);
        }
        if (mCalculationString.length() == 0) {
            mCalculationString.append("0");
        }
        updateCalculationView(mCalculationString);
    }

    public void onEqualButtonClick(View view) {
        String result;
        try {
            BigDecimal cal = Calculator.calculate(mCalculationString.toString());
            result = cal.toString();
        } catch (Expression.ExpressionException e) {
            result = "Syntax Error";
        } catch (ArithmeticException e) {
            result = "Can't divide by 0";
        }

        mResultView.setText(result);
    }

    public void onSciButtonClick(View view) {
        mViewSwitch.showNext();
    }

    public void onThemeButtonClick(View view) {
        mNightMode = AppCompatDelegate.getDefaultNightMode();
        String mode = null;
        switch (mNightMode) {
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                mNightMode = AppCompatDelegate.MODE_NIGHT_YES;
                mode = "Dark Theme";
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                mNightMode = AppCompatDelegate.MODE_NIGHT_NO;
                mode = "Light Theme";
                break;
            case AppCompatDelegate.MODE_NIGHT_NO:
                mNightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                mode = "System Theme";
                break;
        }
        sharedPreferences.edit()
                .putInt("night_mode", mNightMode)
                .apply();
        AppCompatDelegate.setDefaultNightMode(mNightMode);
        Toast.makeText(this, "Switched to " + mode, Toast.LENGTH_SHORT).show();
    }

    private void updateCalculationView(StringBuffer sb) {
        mCalculationView.setText(sb);
        // Keep the scroll at the end
        mScrollView.post(() -> mScrollView.fullScroll(View.FOCUS_RIGHT));
    }

}