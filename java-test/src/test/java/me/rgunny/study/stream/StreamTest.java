package me.rgunny.study.stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class StreamTest {

    private List<OnlineClass> springClasses = new ArrayList<>();
    private List<OnlineClass> javaClasses = new ArrayList<>();
    private List<List<OnlineClass>> gunnyEvents = new ArrayList<>();

    @BeforeEach
    void setUp() {
        springClasses.add(new OnlineClass(1, "spring boot", true));
        springClasses.add(new OnlineClass(2, "spring data jpa", true));
        springClasses.add(new OnlineClass(3, "spring mvc", false));
        springClasses.add(new OnlineClass(4, "spring core", false));
        springClasses.add(new OnlineClass(5, "rest api development", false));

        javaClasses.add(new OnlineClass(6, "The Java, Test", true));
        javaClasses.add(new OnlineClass(7, "The Java, Code manipulation", true));
        javaClasses.add(new OnlineClass(8, "The Java, 8 to 11", false));

        gunnyEvents.add(springClasses);
        gunnyEvents.add(javaClasses);
    }

    /**
     * Stream<T> filter(Predicate<? super T> predicate)
     * Returns a stream consisting of the elements of this stream that match the given predicate.
     * This is an intermediate operation.
     */
    @Test
    void stream_filter() {
        Integer[] onlineClassIds = {1, 2, 3, 4};

        // spring 으로 시작하는 수업
        springClasses.stream()
                .filter(oc -> oc.getTitle().startsWith("spring")) // 중개 오퍼레이터
                .forEach(oc -> System.out.println(oc.getId())); // forEach() -> void 이기 때문에 스트림을 리턴하지 않음 -> 종료 오퍼레이터

        List<Integer> onlineClasses = springClasses.stream()
                .filter(oc -> oc.getTitle().startsWith("spring"))
                .map(oc -> oc.getId())
                .collect(Collectors.toList());

        Assertions.assertEquals(Arrays.asList(onlineClassIds), onlineClasses);
    }


}