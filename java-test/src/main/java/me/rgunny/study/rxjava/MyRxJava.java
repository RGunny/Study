package me.rgunny.study.rxjava;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observables.ConnectableObservable;

import java.util.concurrent.TimeUnit;

public class MyRxJava {

    public static void main(String[] args) throws InterruptedException {

        Observable<String> observable = Observable.just("Hello", "World", "Bye");
        observable.filter(s -> !s.contains("Bye"))
                .subscribe(System.out::println);

        coldPublisher();
        Thread.sleep(2000);
        hotPublisher();
    }

    public static void coldPublisher() {
        System.out.println("=== Start Cold Publisher ===");
        Observable<Integer> cold = Observable.range(1, 3)
                .doOnSubscribe(s -> System.out.println("new subscription"));

        cold.subscribe(i -> System.out.println("A: " + i));
        cold.subscribe(i -> System.out.println("B: " + i)); // 1~3 다시 방출
        System.out.println("=== End Cold Publisher ===");
    }

    public static void hotPublisher() throws InterruptedException {
        System.out.println("=== Start Hot Publisher ===");
        ConnectableObservable<Long> hot = Observable.interval(200, TimeUnit.MILLISECONDS).publish();
        hot.connect();

        hot.take(5).subscribe(i -> System.out.println("A: " + i));
        Thread.sleep(500); // 조금 늦게 합류
        hot.take(5).subscribe(i -> System.out.println("B: " + i)); // 합류 이후 값만 수신
        System.out.println("=== End Hot Publisher ===");
    }

}
