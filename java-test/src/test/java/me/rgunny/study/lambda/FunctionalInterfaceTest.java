package me.rgunny.study.lambda;

import org.junit.jupiter.api.Test;

import java.util.function.Function;

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

}