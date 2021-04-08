package io.github.dhina17.calculator;


import com.udojava.evalex.Expression;

import java.math.BigDecimal;

public class Calculator {
    public static BigDecimal calculate(String str) {
        return new Expression(getExpr(str))
                .with("π", Expression.PI)
                .eval();
    }

    public static String getExpr(final String str) {
        if (str.contains("√")) {
            return str.replace("√", "sqrt");
        } else if (str.contains("cosec")) {
            return str.replace("cosec", "csc");
        } else {
            return str;
        }
    }


}
