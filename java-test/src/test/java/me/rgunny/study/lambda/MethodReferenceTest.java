package me.rgunny.study.lambda;

import org.junit.jupiter.api.Test;

import java.util.function.UnaryOperator;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 메서드 레퍼런스(Method Reference)
 * - 람다 표현식을 구현할 때, 사용할 수 있는 방법
 * - 구현부 바디를 기존에 이미 있는 메서드를 참조하여 Functional Interface 의 구현체로 사용
 */
class MethodReferenceTest {

    /**
     * use method reference (static method 참조)
     */
    @Test
    void hi() {
        UnaryOperator<String> hi1 = (s) -> "hi " + s;
        UnaryOperator<String> hi2 = Greeting::hi;

        System.out.println("hi1 = " + hi1);
        System.out.println("hi1.apply(\"gunny\") = " + hi1.apply("gunny"));

        assertThat(hi1.apply("gunny").equals(hi2.apply("gunny"))).isTrue();
    }

    /**
     * use method reference (특정 객체의 instance method 참조)
     */
    @Test
    void hello() {
        Greeting greeting = new Greeting();
        // 아직 hello메서드를 호출한게 아님
        // => hello 메서드를 참조하는 UnaryOperator가 생성된 것
        UnaryOperator<String> hello = greeting::hello;

        // 생성된 hello로 apply()해야 "gunny"라는 값이 Greeting 클래스의 hello 메서드에 전달되어 출력
        System.out.println(hello.apply("gunny"));
    }
}