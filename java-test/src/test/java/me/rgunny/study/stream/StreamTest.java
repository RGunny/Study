package me.rgunny.study.stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    /**
     * static <T> Stream<T> iterate(T seed, UnaryOperator<T> f)
     * Returns an infinite sequential ordered Stream produced by iterative application of a function f to an initial element seed,
     * producing a Stream consisting of seed, f(seed), f(f(seed)), etc.
     * The first element (position 0) in the Stream will be the provided seed.
     * For n > 0, the element at position n, will be the result of applying the function f to the element at position n - 1.
     *
     * Type Parameters:
     * T - the type of stream elements
     * Parameters:
     * seed - the initial element
     * f - a function to be applied to the previous element to produce a new element
     * Returns:
     * a new sequential Stream
     */
    @Test
    void stream_iterate() {

        // 10부터 1씩 증가하는 무제한 스트림중에서 앞에 10개 빼고 최대 10개 까지만
        Stream.iterate(10, i -> i + 1); // intermediate operation

        Stream.iterate(10, i -> i + 1)
                .skip(10)
                .limit(10)
                .forEach(System.out::println);

        Integer[] numbers = {20, 21, 22, 23, 24, 25, 26, 27, 28, 29};

        List<Integer> lists = Stream.iterate(10, i -> i + 1)
                .skip(10)
                .limit(10)
                .collect(Collectors.toList());

        assertEquals(Arrays.asList(numbers), lists);
    }

    @Test
    void 자바_수업중에_Test_들어있는_수업이_있는지_확인() {

    }

    @Test
    void 스프링_수업_중에_제목에_spring이_들어간_것만_모아서_List로_만들기() {

    }

}