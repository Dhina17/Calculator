package io.github.dhina17.calculator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

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
    private String mResultValue;
    private boolean mIsSci = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /* Action Bar */
        Toolbar appBar = findViewById(R.id.action_bar);
        setSupportActionBar(appBar);

        mCalculationView = findViewById(R.id.calculation_view);
        mResultView = findViewById(R.id.result_view);
        mViewSwitch = findViewById(R.id.view_switch);

        MaterialButton mDelButton = findViewById(R.id.button_delete);
        mScrollView = findViewById(R.id.scroll_view);

        mCalculationString = new StringBuffer();

        /* Restore the prev state of calculation/result/grid view when theme changed */
        if (savedInstanceState != null) {
            String prevStateExpr = savedInstanceState.getString("calculation_str");
            if (!prevStateExpr.isEmpty()) {
                mCalculationString.append(prevStateExpr);
                updateCalculationView(mCalculationString);
            }
            mResultValue = savedInstanceState.getString("result_value");
            if (mResultValue != null) {
                mResultView.setText(mResultValue);
            }
            mIsSci = savedInstanceState.getBoolean("is_sci_page");
            if (mIsSci) mViewSwitch.showNext();

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
            mResultValue = "0";
            mResultView.setText(mResultValue);
            return true;
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        /* Default toggle icon */
        menu.getItem(0).setIcon(
                sharedPreferences.getInt("icon_id", R.drawable.ic_system_default_mode)
        );
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("calculation_str", mCalculationString.toString());
        outState.putString("result_value", mResultValue);
        outState.putBoolean("is_sci_page", mIsSci);
    }

    public void onButtonClick(View view) {
        MaterialButton button = (MaterialButton) view;
        String buttonText = button.getText().toString();
        int len = mCalculationString.length();

        /* Remove the zero in front of the expr after restore zero with del button */
        if (len > 0 && mCalculationString.charAt(0) == '0') {
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
        try {
            BigDecimal cal = Calculator.calculate(mCalculationString.toString());
            mResultValue = cal.toString();
        } catch (Expression.ExpressionException | NumberFormatException e) {
            mResultValue = "Syntax Error";
        } catch (ArithmeticException e) {
            mResultValue = "Can't divide by 0";
        }

        mResultView.setText(mResultValue);
    }

    public void onSciButtonClick(View view) {
        mViewSwitch.showNext();
        mIsSci = !mIsSci;
    }

    public void onThemeButtonClick(MenuItem item) {
        mNightMode = AppCompatDelegate.getDefaultNightMode();
        int resId = 0;
        switch (mNightMode) {
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                mNightMode = AppCompatDelegate.MODE_NIGHT_YES;
                resId = R.drawable.ic_night_mode;
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                mNightMode = AppCompatDelegate.MODE_NIGHT_NO;
                resId = R.drawable.ic_day_mode;
                break;
            case AppCompatDelegate.MODE_NIGHT_NO:
                mNightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                resId = R.drawable.ic_system_default_mode;
                break;
        }
        sharedPreferences.edit()
                .putInt("night_mode", mNightMode)
                .putInt("icon_id", resId)
                .apply();
        item.setIcon(resId);
        AppCompatDelegate.setDefaultNightMode(mNightMode);
    }

    private void updateCalculationView(StringBuffer sb) {
        mCalculationView.setText(sb);
        // Keep the scroll at the end
        mScrollView.post(() -> mScrollView.fullScroll(View.FOCUS_RIGHT));
    }

}