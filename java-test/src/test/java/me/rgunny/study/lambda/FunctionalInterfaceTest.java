package me.rgunny.study.lambda;

import org.junit.jupiter.api.Test;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class FunctionalInterfaceTest {

    /**
     * R : apply(T t)
     */
    @Test
    void functionPlus10(){
        Function<Integer, Integer> plus10 = (i) -> i + 10;

        assertThat(plus10.apply(10)).isEqualTo(20);
    }

    /**
     * 함수를 조합할 수 있는 메서드를 디폴트 메서드로 제공
     * default <V> Function<T, V> : andThen(Function<? super R, ? extends V> after)
     *  => A 연산 후 B 연산
     * default <V> Function<V, R> : compose(Function<? super V, ? extends T> before)
     *  => A.compose(B) : B 연산 후 A 연산
     *  => Higher-Order Function(고차 함수) : 함수가 함수를 매개변수로 받을 수 있고 함수를 리턴할 수도 있음
     *
     * static <T> Function<T, T>  : identity()
     *  => 입력값을 리턴해주는 함수, static이므로 조합용으로 보기는 어려움
     */
    @Test
    void functionDefault(){
        Function<Integer, Integer> plus10 = (i) -> i + 10;
        Function<Integer, Integer> multiply2 = (i) -> i * 2;

        Function<Integer, Integer> multiply2AndPlus10 = plus10.compose(multiply2);
        Function<Integer, Integer> plus10AndThenMultiply2 = plus10.andThen(multiply2);
        assertThat(multiply2AndPlus10.apply(2)).isEqualTo(14);
        assertThat(plus10AndThenMultiply2.apply(2)).isEqualTo(24);
    }

}