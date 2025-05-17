package lambda.lambda4;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;

public class BiMain {

    public static void main(String[] args) {
        BiFunction<Integer, Integer, Integer> add1 = (x, y) -> x + y;
        BinaryOperator<Integer> add2 = (x, y) -> x + y;
        System.out.println("Sum: " + add1.apply(1, 2));

        BiConsumer<String, Integer> repeat = (s, n) -> {
            for (int i = 0; i < n; i++) {
                System.out.print(s);
            }
            System.out.println();
        };
        repeat.accept("*", 5);

        BiPredicate<Integer, Integer> isGreater = (x, y) -> x > y;
        System.out.println(isGreater.test(1, 2));
        System.out.println(isGreater.test(2, 1));
    }
}
