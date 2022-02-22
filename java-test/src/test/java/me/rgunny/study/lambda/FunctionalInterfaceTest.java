package me.rgunny.study.lambda;

import org.junit.jupiter.api.Test;

import java.util.function.*;

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

    /**
     * Consumer<T>
     *  T 라는 타입을 받아서 아무 값도 리턴하지 않는 함수 인터페이스
     *      void Accept(T t)
     *  함수 조합용 메서드
     *      andThen()
     */
    @Test
    void consumerAccept(){
//        Consumer<Integer> printT = (i) -> System.out.println(i);
        Consumer<Integer> printT = System.out::println; // use method reference

        printT.accept(10);
    }

    /**
     * Supplier<T>
     *  T 타입의 값을 제공하는 함수 인터페이스
     *      T get()
     *  => 어떤 값을 하나 가져오는 인터페이스 => 입력값을 받지 않고, 리턴 값의 타입을 정함
     */
    @Test
    void supplier(){
        Supplier<Integer> get10 = () -> 10; // 입력값이 없기 때문에 람다 표현식에 인자를 줄 필요가 없음
        assertThat(get10.get()).isEqualTo(10);
    }

    /**
     * Predicate<T>
     *  T 타입을 받아서 boolean을 리턴하는 함수 인터페이스
     *      : boolean test(T t)
     *  함수 조합용 메서드
     *      And
     *      Or
     *      Negate
     */
    @Test
    void predicate(){
        Predicate<String> startsWithGunny = (s) -> s.startsWith("Gunny");
        Predicate<Integer> isEven = (i) -> i % 2 == 0;

        assertThat(startsWithGunny.test("Gunny")).isTrue();
        assertThat(isEven.test(9)).isFalse();
        assertThat(startsWithGunny.negate().test("NoGunny")).isTrue();
    }

    /**
     * UnaryOperator<T>
     *  Function<T, R> 의 특수한 형태로, 입력값 하나를 받아서 동일한 타입을 리턴하는 함수 인터페이스
     *  => 입력값의 타입과 결과값의 타입이 같을 때 (타입이 하나일 때) 사용할 수 있는 특수한 함수 인터페이스
     *  => Function 을 상속받기 때문에 제공하는 apply(), default method 등을 사용할 수 있음
     */
    void unaryOperator(){
//        Function<Integer, Integer> plus10 = (i) -> i + 10;
        UnaryOperator<Integer> plus10 = (i) -> i + 10;

        assertThat(plus10.apply(10)).isEqualTo(20);
    }

}