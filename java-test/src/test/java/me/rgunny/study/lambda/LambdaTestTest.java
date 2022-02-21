package me.rgunny.study.lambda;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LambdaTestTest {

    @Test
    void findLambdaQuiz(){
        Runnable runnable = () -> {}; // 파라미터가 없으며 void를 반환하는 바디가 없는 람다 표현식
        Callable<String> stringCallable = () -> "Gunny"; // 파라미터가 없으며 문자열을 반환하는 표현식
        Callable<String> stringCallable1 = () -> {return "Gunny";}; // 파라미터가 없으며 명시적으로 return 문을 이용해 문자열을 반환하는 표현식
//        (Integer i) -> return "Gunny" + i; // return은 흐름 제어문이다.
        Function<Integer, String> function = (Integer i) -> {return "Gunny" + i;};
//        (String s) -> {"Gunny";} // "Gunny"는 구문(statement)이 아니라 표현식(expression)이다.
        Function<String, String> function1 = (String s) -> "Gunny"; // 올바른 람다 표현식
        Function<String, String> function2 = (String s) -> {return "Gunny";}; // 또는 명시적으로 return 문 사용
    }

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