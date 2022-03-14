package me.rgunny.study.stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

        assertEquals(Arrays.asList(onlineClassIds), onlineClasses);
    }

    /**
     * Predicate Functional Interface 내부 `not` static method 를 사용하여 메서드 레퍼런스 반대 구현
     */
    @Test
    void stream_filter_predicate_not_test() {
        Integer[] onlineClassIds = {3, 4, 5};

        // closed 되지 않은 수업
        springClasses.stream()
                .filter(oc -> !oc.isClosed())
                .forEach(oc -> System.out.println(oc.getId()));

        List<Integer> onlineClasses = springClasses.stream()
                .filter(Predicate.not(OnlineClass::isClosed))
                .map(oc -> oc.getId())
                .collect(Collectors.toList());

        assertEquals(Arrays.asList(onlineClassIds), onlineClasses);
    }

    @Test
    void 수업_이름_모아서_스트림_만들기() {

    }

    @Test
    void 두_수업_목록에_들어있는_모든_수업_아이디_출력() {

    }

    @Test
    void T_10부터_1씩_증가하는_무제한_스트림중에서_앞에_10개_빼고_최대_10개_까지만() {

    }

    @Test
    void 자바_수업중에_Test_들어있는_수업이_있는지_확인() {

    }

    @Test
    void 스프링_수업_중에_제목에_spring이_들어간_것만_모아서_List로_만들기() {

    }

}