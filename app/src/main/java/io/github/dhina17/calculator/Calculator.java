package io.github.dhina17.calculator;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class Calculator {
    public static double calculate(String str) throws IllegalArgumentException,ArithmeticException {
        Expression expr = new ExpressionBuilder(str)
                .build();

        return expr.evaluate();
    }
}
