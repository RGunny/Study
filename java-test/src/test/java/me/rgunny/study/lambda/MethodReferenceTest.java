package me.rgunny.study.lambda;

import org.junit.jupiter.api.Test;

import java.util.function.Function;
import java.util.function.Supplier;
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

    /**
     * use method reference (빈 constructor 참조)
     */
    @Test
    void defaultConstructor(){
        // 생성자를 호출할 때 리턴은 객체의 타입 => 입력값은 없는데, 결과같은 있음 => Supplier
        // 아직 인스턴스(Greeting) 아님 Supplier임
        Supplier<Greeting> newGreeting = Greeting::new;
        Greeting greeting = newGreeting.get(); // Supplier => Greeting 생성
        System.out.println("greeting = " + greeting);
        System.out.println("greeting.getName() = " + greeting.getName());
        System.out.println("greeting.getClass() = " + greeting.getClass());
        assertThat(greeting.getName()).isNull();
    }

    /**
     * use method reference (이름을 받는 constructor 참조)
     */
    @Test
    void constructor() {
        // 입력(인자)과 리턴이 다름 => Function
        Function<String, Greeting> gunnyGreeting = Greeting::new;

        // 위 newGreeting과 gunnyGreeting은 서로 다른 생성자를 참조하고 있음
        // => 메서드 레퍼런스로는 호출하는 생성자를 판단하기 힘들다.
        Greeting gunny = gunnyGreeting.apply("gunny");
        System.out.println(gunny.getName()); // 문자를 받는 생성자가 생성된 것을 확인
        System.out.println("gunny = " + gunny);
        assertThat(gunnyGreeting.apply("gunny").getName().equals("gunny")).isTrue();
    }
}