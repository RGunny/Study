package me.rgunny.study.lambda;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;

import static org.assertj.core.api.Assertions.assertThat;

class LambdaTestTest {

    private LambdaTest lambdaTest;

    @BeforeEach
    void setUp() {
        lambdaTest = new LambdaTest();
    }

    /**
     * 순수 함수 (Pure Funtion)
     * 사이드 이펙트가 없다. (함수 밖에 있는 값을 변경하지 않는다.)
     * 상태가 없다. (함수 밖에 있는 값을 사용하지 않는다.)
     */
    @Test
    void pureFunctionTest() {
        int number = 1;
        assertThat(lambdaTest.pureFunction(number))
                .isEqualTo(lambdaTest.pureFunction(number))
                .isEqualTo(lambdaTest.pureFunction(number));
    }

    @Test
    void findLambdaQuiz() {
        Runnable runnable = () -> {
        }; // 파라미터가 없으며 void를 반환하는 바디가 없는 람다 표현식
        Callable<String> stringCallable = () -> "Gunny"; // 파라미터가 없으며 문자열을 반환하는 표현식
        Callable<String> stringCallable1 = () -> {
            return "Gunny";
        }; // 파라미터가 없으며 명시적으로 return 문을 이용해 문자열을 반환하는 표현식
//        (Integer i) -> return "Gunny" + i; // return은 흐름 제어문이다.
        Function<Integer, String> function = (Integer i) -> {
            return "Gunny" + i;
        };
//        (String s) -> {"Gunny";} // "Gunny"는 구문(statement)이 아니라 표현식(expression)이다.
        Function<String, String> function1 = (String s) -> "Gunny"; // 올바른 람다 표현식
        Function<String, String> function2 = (String s) -> {
            return "Gunny";
        }; // 또는 명시적으로 return 문 사용
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

    @Test
    void localVariableCapture() {
//        final int baseNumber = 10; // 자바 8 이전에는 항상 final 키워드가 있어야 익명, 내부 클래스에서 참조가 가능했음
        /*
         자바 8 부터는 final 이 생략 가능한 경우가 있는데, 사실상 사용이 final 인 경우가 그렇다.
         - 사실상 final 인 경우 : effectively final
         => final 이라는 키워드는 없지만, 해당 변수를 더이상 어디서도 변경하지 않는 경우
         => 추후 해당 변수의 변경이 생길 경우 effective final 속성이 사라지며 참조불가 컴파일 에러 발생
         - Variable used in lambda expression should be final or effectively final
         */
        int baseNumber = 10; // effectively final

        // Local Inner Class 에서 local variable 참조
        class LocalClass {
            void printBaseNumber() {
                System.out.println(baseNumber);
            }
        }
        // Anonymous Class 에서 local variable 참조
        Consumer<Integer> abstractClass = new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                System.out.println(baseNumber);
            }
        };

        // Lambda
        IntConsumer printInt = (i) -> {
            System.out.println(i + baseNumber);
        };

        printInt.accept(10);
    }

    @Test
    void localAndAnonymousClassShadowing() {
        int baseNumber = 10; // effectively final

        // Local Inner Class
        class LocalClass {
            void printBaseNumber() {
                int baseNumber = 11;
                System.out.println(baseNumber);
            }
        }
        // Anonymous Class
        Consumer<Integer> abstractClass = new Consumer<Integer>() {
            @Override
            public void accept(Integer baseNumber) { // 파라미터 변수 명을 같게 하면, local variable 이 아닌, 전달 받는 변수를 받아 쉐도잉됨
                System.out.println(baseNumber);
            }
        };
    }

}