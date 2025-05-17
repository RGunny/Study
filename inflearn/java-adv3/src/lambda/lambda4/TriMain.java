package lambda.lambda4;

import java.util.function.BinaryOperator;

public class TriMain {

    public static void main(String[] args) {
        TriFunction<Integer, Integer, Integer, Integer> add1 = (a, b, c) -> a + b + c;
        TriOperator<Integer> add2 = (a, b, c) -> a + b + c;
        System.out.println(add1.apply(1, 2, 3));
        System.out.println(add2.apply(1, 2, 3));


    }

    @FunctionalInterface
    interface TriFunction<A, B, C, R> {
        R apply(A a, B b, C c);
    }

    @FunctionalInterface
    interface TriOperator<T> extends TriFunction<T, T, T, T> {
    }
}
