package com.gmail.subnokoii78.util.other;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.DoubleUnaryOperator;

public final class CalcExpEvaluator {
    public static void main(String[] args) {
        System.out.println(evaluate(""));
    }

    private static final Set<Character> IGNORED = Set.of(' ', '\n');

    private static final Set<Character> SIGNS = Set.of('+', '-');

    private static final Set<Character> INTEGERS = Set.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');

    private static final char DECIMAL_POINT = '.';

    private static final char FUNCTION_ARGUMENT_SEPARATOR = ',';

    private static final char PARENTHESIS_START = '(';

    private static final char PARENTHESIS_END = ')';

    private final Map<Character, BinaryOperator<Double>> MONOMIAL_OPERATORS = Map.of(
        '^', Math::pow,
        '*', (a, b) -> a * b,
        '/', (a, b) -> {
            if (a == 0 && b == 0) {
                throw new IllegalArgumentException("式 '0 / 0'はNaNを返します");
            }

            return a / b;
        },
        '%', (a, b) -> {
            if (a == 0 && b == 0) {
                throw new IllegalArgumentException("式 '0 / 0'はNaNを返します");
            }

            return a % b;
        }
    );

    private final Map<Character, BinaryOperator<Double>> POLYNOMIAL_OPERATORS = new HashMap<>(Map.of(
        '+', Double::sum,
        '-', (a, b) -> a - b
    ));

    private final Map<Character, DoubleUnaryOperator> NUMBER_PREFIX_OPERATOR = new HashMap<>(Map.of(
        '!', value -> {
            if (value != (double) (int) value) {
                throw new IllegalArgumentException("階乗演算子は実質的な整数の値にのみ使用できます");
            }

            double result = 1;

            for (int i = 2; i <= value; i++) {
                result *= i;
            }

            return result;
        }
    ));

    private final Map<String, DoubleUnaryOperator> SINGLE_ARGUMENT_FUNCTIONS = new HashMap<>(Map.of(
        "sqrt", Math::sqrt
    ));

    private final Map<String, BinaryOperator<Double>> DOUBLE_ARGUMENTS_FUNCTIONS = new HashMap<>(Map.of(
        "log", (a, b) -> Math.log(b) / Math.log(a)
    ));

    private final String expression;

    private int location = 0;

    public CalcExpEvaluator(@NotNull String expression) {
        this.expression = expression;
    }

    private boolean isOver() {
        return location >= expression.length();
    }

    private char next() {
        if (isOver()) {
            throw new IllegalStateException("文字数を超えた位置へのアクセスが発生しました");
        }

        final char current = expression.charAt(location++);

        if (IGNORED.contains(current)) return next();

        return current;
    }

    private double number() {
        if (isSingleArgFunction()) {
            return getSingleArgFunction().applyAsDouble(factor());
        }
        else if (isDoubleArgsFunction()) {
            final var function = getDoubleArgsFunction();
            final var args = arguments();
            if (args.size() != 2) throw new IllegalArgumentException("関数の引数の数は2つが要求されています");
            return function.apply(args.get(0), args.get(1));
        }

        final char init = next();

        if (!INTEGERS.contains(init) && !SIGNS.contains(init)) {
            throw new IllegalArgumentException("数値は[0-9+-]で開始する必要があります('" + init + "')");
        }

        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(init);
        boolean dotAlreadyAppended = false;

        while (!isOver()) {
            final char current = next();

            if (INTEGERS.contains(current)) stringBuilder.append(current);
            else if (current == DECIMAL_POINT) {
                if (dotAlreadyAppended) {
                    throw new IllegalArgumentException("無効な小数点を検知しました");
                }

                stringBuilder.append(current);
                dotAlreadyAppended = true;
            }
            else {
                location--;
                break;
            }
        }

        try {
            return Double.parseDouble(stringBuilder.toString());
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("数値の解析に失敗しました", e);
        }
    }

    private double monomial() {
        double value = factor();

        while (!isOver()) {
            final char current = next();
            if (MONOMIAL_OPERATORS.containsKey(current)) {
                value = MONOMIAL_OPERATORS.get(current).apply(value, factor());
            }
            else if (NUMBER_PREFIX_OPERATOR.containsKey(current)) {
                value = NUMBER_PREFIX_OPERATOR.get(current).applyAsDouble(value);
            }
            else {
                location--;
                break;
            }
        }

        return value;
    }

    private double polynomial() {
        double value = monomial();

        while (!isOver()) {
            final char current = next();
            if (POLYNOMIAL_OPERATORS.containsKey(current)) {
                value = POLYNOMIAL_OPERATORS.get(current).apply(value, monomial());
            }
            else {
                location--;
                break;
            }
        }

        return value;
    }

    private double factor() {
        final char current = next();

        if (current == PARENTHESIS_START) {
            double value = polynomial();
            if (isOver()) throw new IllegalArgumentException("括弧が閉じられていません");
            final char next = next();
            if (next == PARENTHESIS_END) return value;
            else throw new IllegalArgumentException("括弧が閉じられていません: " + next);
        }
        else {
            location--;
            return number();
        }
    }

    private List<Double> arguments() {
        final char current = next();
        final List<Double> args = new ArrayList<>();

        if (current == PARENTHESIS_START) {
            while (true) {
                if (isOver()) throw new IllegalArgumentException("引数の探索中に文字列外に来ました");
                double value = polynomial();
                final char next = next();
                if (next == FUNCTION_ARGUMENT_SEPARATOR) {
                    args.add(value);
                }
                else if (next == PARENTHESIS_END) {
                    args.add(value);
                    return args;
                }
                else throw new IllegalArgumentException("関数の引数の区切りが見つかりません: " + next);
            }
        }
        else throw new IllegalArgumentException("関数の呼び出しには括弧が必要です: " + current);
    }

    private boolean isSingleArgFunction() {
        final String str = expression.substring(location);

        for (final String name : SINGLE_ARGUMENT_FUNCTIONS.keySet()) {
            if (str.startsWith(name + "(")) {
                return true;
            }
        }

        return false;
    }

    private DoubleUnaryOperator getSingleArgFunction() {
        final String str = expression.substring(location);

        for (final String name : SINGLE_ARGUMENT_FUNCTIONS.keySet()) {
            if (str.startsWith(name + "(")) {
                location += name.length();
                return SINGLE_ARGUMENT_FUNCTIONS.get(name);
            }
        }

        throw new IllegalArgumentException("関数を取得できませんでした");
    }

    private boolean isDoubleArgsFunction() {
        final String str = expression.substring(location);

        for (final String name : DOUBLE_ARGUMENTS_FUNCTIONS.keySet()) {
            if (str.startsWith(name + "(")) {
                return true;
            }
        }

        return false;
    }

    private BinaryOperator<Double> getDoubleArgsFunction() {
        final String str = expression.substring(location);

        for (final String name : DOUBLE_ARGUMENTS_FUNCTIONS.keySet()) {
            if (str.startsWith(name + "(")) {
                location += name.length();
                return DOUBLE_ARGUMENTS_FUNCTIONS.get(name);
            }
        }

        throw new IllegalArgumentException("関数を取得できませんでした");
    }

    public double evaluate() {
        if (isOver()) throw new IllegalArgumentException("空文字は計算できません");
        final double value = polynomial();
        if (!expression.substring(location).isEmpty()) {
            throw new IllegalArgumentException("式の終了後に無効な文字を検出しました: " + expression.substring(location));
        }
        return value;
    }

    public void define() {

    }

    public static double evaluate(@NotNull String expression) {
        return new CalcExpEvaluator(expression).evaluate();
    }
}
