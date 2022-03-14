package me.rgunny.study.stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * References
 * - https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html
 * - https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html
 */
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

    /**
     * <R> Stream<R> map(Function<? super T,? extends R> mapper)
     * Returns a stream consisting of the results of applying the given function to the elements of this stream.
     * This is an intermediate operation.
     */
    @Test
    void stream_map() {
        String[] onlineClassTitles = {"spring boot", "spring data jpa", "spring mvc", "spring core", "rest api development"};

        // 수업 이름 모아서 스트림 만들기
        springClasses.stream()
                .map(oc -> oc.getTitle())
                .forEach(System.out::println);

        List<String> onlineClasses = this.springClasses.stream()
                .map(oc -> oc.getTitle())
                .collect(Collectors.toList());

        assertEquals(Arrays.asList(onlineClassTitles), onlineClasses);
    }

    /**
     * <R> Stream<R> flatMap(Function<? super T,? extends Stream<? extends R>> mapper)
     * Returns a stream consisting of the results of replacing each element of this stream with the contents
     * of a mapped stream produced by applying the provided mapping function to each element.
     * Each mapped stream is closed after its contents have been placed into this stream.
     * (If a mapped stream is null an empty stream is used, instead.)
     * This is an intermediate operation.
     *
     * API Note:
     * The flatMap() operation has the effect of applying a one-to-many transformation to the elements of the stream,
     * and then flattening the resulting elements into a new stream.
     *
     * flatMap 은 특수한 형태의 map operation 이다.
     */
    @Test
    void stream_flatMap() {
        Integer[] onlineClassIds = {1, 2, 3, 4, 5, 6, 7, 8};

        // 두 수업 목록에 들어있는 모든 수업 아이디 출력
        gunnyEvents.stream()
                .flatMap(Collection::stream) // (list -> list.stream())
                .forEach(oc -> System.out.println(oc.getId()));
        List<Integer> onlineClasses = gunnyEvents.stream()
                .flatMap(Collection::stream)
                .map(oc -> oc.getId())
                .collect(Collectors.toList());

        assertEquals(Arrays.asList(onlineClassIds), onlineClasses);
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