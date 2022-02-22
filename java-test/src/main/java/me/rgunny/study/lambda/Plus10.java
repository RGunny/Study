package me.rgunny.study.lambda;

import java.util.function.Function;

/**
 * Interface Function<T, R> 을 보면, apply() 라는 abstract method가 정의되어 있다.
 *  R apply(T t) => T 라는 타입을 받아 R 타입을 리턴하는 함수이다. (두 개의 타입이 다를 수 있기 때문)
 *  => apply() 만 구현하면 된다.
 *  => 이전에 구현한 RunSomething 함수형 인터페이스의 doIt()도 어떤 값을 받아 어떤 값을 리턴하기 때문에 이 인터페이스는 사실상 필요하지 않아 고도화 해보겠다.
 */
public class Plus10 implements Function<Integer, Integer> {
    @Override
    public Integer apply(Integer integer) {
        return integer + 10;
    }
}
