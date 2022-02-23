package me.rgunny.study.lambda;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
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
    void defaultConstructor() {
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

    /**
     * 임의 객체의 인스턴스 메서드 참조
     * 특정 타입의 불특정 다수 인스턴스 메서드 참조
     */
    @Test
    void multiObject() {

        String[] names = {"gunny", "kyuhee", "yerim"};
        Arrays.sort(names, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return (o2 + o1).compareTo(o1 + o2);
            }
        });
        Arrays.sort(names, (o1, o2) -> (o2 + o1).compareTo(o1 + o2)); // lambda expression
        /*
         Comparator 가 자바8부터 Functional Interface 로 바뀌어
         구현해야 하는 추상 메서드는 int compare(T o1, T o2) 하나이지만,
         default, static method 들이 있음
         cf. equals()는 Object 에 있는 거지 추상 메서드 아님
         => 애초에 Functional Interface 는 추상 메서드를 하나 가짐

         compareToIgnoreCase :
          자기 자신의 문자열과 파라미터로 받은 문자열을 비교하여 int 반환
          임의의 인스턴스 a, b, c
          a가 b와 비교하여 int 값 반환, b가 c와 비교하여 int 값 반환
         */
        System.out.println(Arrays.toString(names));
        Arrays.sort(names, (s, str) -> s.compareToIgnoreCase(str)); // lambda expression (ASC)
        Arrays.sort(names, String::compareToIgnoreCase); // method reference (ASC)
        System.out.println(Arrays.toString(names));

        Arrays.sort(names, (str, s) -> s.compareToIgnoreCase(str)); // lambda expression (DESC)
        Arrays.sort(names, MethodReferenceTest::compare); // lambda expression (DESC) => 따로 전용 메서드를 생성하게 됨
        Arrays.sort(names, (str, s) -> compare(str, s)); // lambda expression (DESC)
        System.out.println(Arrays.toString(names));
    }

    private static int compare(String str, String s) {
        return s.compareToIgnoreCase(str);
    }
}