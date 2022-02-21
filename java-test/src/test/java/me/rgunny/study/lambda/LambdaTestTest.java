package me.rgunny.study.lambda;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LambdaTestTest {

    @Test
    void lambda3Expressions() {
        // Existing Code
        Comparator<Integer> byWeight1 = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        };

        // Use lambda Code
        Comparator<Integer> byWeight2 = (Integer o1, Integer o2) -> o1.compareTo(o2);

        // Replace lambda with method reference
        Comparator<Integer> byWeight3 = Integer::compareTo;

        Integer a = 1;
        Integer b = 2;

        int w1 = byWeight1.compare(a, b);
        int w2 = byWeight2.compare(a, b);
        int w3 = byWeight3.compare(a, b);

        assertThat(w1).isEqualTo(w2).isEqualTo(w3);
    }
}