package me.rgunny.study.optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class OptionalTest {

    private List<OnlineClass> springClasses = new ArrayList<>();

    private static void accept(OnlineClass oc) {
        System.out.println(oc.getTitle());
    }

    @BeforeEach
    void setUp() {
        springClasses.add(new OnlineClass(1, "spring boot", true));
        springClasses.add(new OnlineClass(2, "spring data jpa", true));
        springClasses.add(new OnlineClass(3, "spring mvc", false));
        springClasses.add(new OnlineClass(4, "spring core", false));
        springClasses.add(new OnlineClass(5, "rest api development", false));
    }

    @Test
    void occur_NPE_and_throws() {
        OnlineClass springBoot = new OnlineClass(1, "spring boot", true);
        assertThrows(NullPointerException.class, () -> springBoot.getProgress().getStudyDuration());
    }

    /**
     * 기존 코드, 에러를 만들기 쉬운 코드
     *    1. null check 를 까먹기 쉬움
     *    2. null 을 return 하는 것 자체가 문제 (Java 8 이전에는 별 대안이 없었음)
     *      => throw new IllegalStateException() : RuntimeException 을 던지면 편하지만, CheckedException 을 던지면 에러처리를 강제하게 됨
     *      => 에러가 발생하면 Java 는 Stack Trace(Error 가 발생하기 까지의 Call Stack 정보들)를 찍게 되는데, 이 자체로 자원 낭비가 생긴다.
     *      => 진짜 필요할 경우에만 예외를 사용해야지, 로직 처리할 때 에러를 사용하는 것은 좋지 않다.
     *      => 그냥 null 을 리턴하고 null check
     */
    @Test
    void legacy_code(){
        OnlineClass springBoot = new OnlineClass(1, "spring boot", true);
        Progress progress = springBoot.getProgress();
        if (progress != null) {
            System.out.println("progress.getStudyDuration() = " + progress.getStudyDuration());
        }
    }

    /**
     * Optional 을 파라미터로 사용 시, 해당 메서드에서 오히려 별도의 체크를 해야할 뿐만 아니라
     * 호출부에서 null 을 직접 넣을 수도 있다.
     * => Optional 을 사용하는 의미가 없어지고, 오히려 null check 를 한 번 더 해줘야 함
     */
    @Test
    void optional_as_parameter_and_throw() {
        OnlineClass springBoot = new OnlineClass(1, "spring boot", true);
        assertThrows(NullPointerException.class, () -> springBoot.setProgressByOptionalParameter(null));
    }

    @Test
    void optional_primitive_type() {
        Optional<Integer> opt10 = Optional.of(10);
        OptionalInt optInt10 = OptionalInt.of(10);

        assertEquals((Integer) 10, opt10.get());
        assertEquals(10, optInt10.getAsInt());
    }

    @Test
    void optional_empty() {
        OnlineClass springBoot = new OnlineClass(1, "spring boot", true);
        Optional<Progress> progress = springBoot.getProgressByOptionalReturnEmpty();
        System.out.println("progress = " + progress);
    }

    /**
     * filter(Functional Interface Predicate)
     * => Optional 에 있는 값 걸러냄
     * => Optional 반환
     */
    @Test
    void optional_filter(){
        Optional<OnlineClass> optional = springClasses.stream()
                .filter(oc -> oc.getTitle().startsWith("spring"))
                .findFirst();

        assertTrue(optional.isPresent());

        Optional<OnlineClass> onlineClass1 = optional
                .filter(oc -> !oc.isClosed());
        Optional<OnlineClass> onlineClass2 = optional
                .filter(OnlineClass::isClosed);

        assertTrue(onlineClass1.isEmpty());
    }

    /**
     * get() : Optional 의 값을 꺼내는 메서드
     * 비어있는 Optional 에서 무언가를 꺼낸다면
     * => `NoSuchElementException` 발생
     * => 먼저 값이 있는 지 확인하고 꺼내야 함
     * => 이게 무슨 의미인가...
     */
    @Test
    void optional_get(){
        Optional<OnlineClass> optional = springClasses.stream()
                .filter(oc -> oc.getTitle().startsWith("no"))
                .findFirst();

        assertThrows(NoSuchElementException.class, () -> optional.get());
        assertThrows(NoSuchElementException.class, optional::get);
    }

    /**
     * ifPresent(Functional Interface Consumer<T>)
     * => get() 으로 꺼내기 위해 조건 확인 등의 처리가 사라짐
     */
    @Test
    void optional_ifPresent(){
        Optional<OnlineClass> optional = springClasses.stream()
                .filter(oc -> oc.getTitle().startsWith("spring"))
                .findFirst();

        optional.ifPresent(oc -> System.out.println(oc.getTitle()));
        optional.ifPresent(OptionalTest::accept);
    }

    /**
     * ofElse(T)
     * => 이미 만들어져 있는 `인스턴스`를 사용할 때 사용
     * => orElse() 파라미터로는 functional interface 구현체가 아닌, optional 이 감싸고 있는 instance 가 들어옴
     * => 있든 없든 뒤에 있는 연산이 무조건 실행됨
     */
    @Test
    void optional_orElse(){
        Optional<OnlineClass> optional1 = springClasses.stream()
                .filter(oc -> oc.getTitle().startsWith("spring"))
                .findFirst();
        Optional<OnlineClass> optional2 = springClasses.stream()
                .filter(oc -> oc.getTitle().startsWith("jpa"))
                .findFirst();

        OnlineClass onlineClass1 = optional1.orElse(createNewClass());
        OnlineClass onlineClass2 = optional2.orElse(createNewClass());

        assertEquals(onlineClass1.getTitle().startsWith("spring"), true);
        assertEquals(onlineClass2.getTitle().startsWith("New"), true);
    }

    /**
     * orElseGet(Functional Interface Supplier)
     * => 이미 만들어져 있는 것이 아닌, 동적인 작업을 해서 만들거나 추가할 때 사용
     * => orElse 조건에 들어올 때만 메서드 파라미터의 Supplier 를 실행함 (함수형 인터페이스의 구현체이기 때문에 Lazy 하게 다룰 수 있음)
     */
    @Test
    void optional_orElseGet(){
        Optional<OnlineClass> optional1 = springClasses.stream()
                .filter(oc -> oc.getTitle().startsWith("spring"))
                .findFirst();
        Optional<OnlineClass> optional2 = springClasses.stream()
                .filter(oc -> oc.getTitle().startsWith("jpa"))
                .findFirst();

        OnlineClass onlineClass1 = optional1.orElseGet(OptionalTest::createNewClass);
        OnlineClass onlineClass2 = optional2.orElseGet(OptionalTest::createNewClass);
    }

    /**
     * orElseThrow()
     * => Optional 에 값이 있으면 가져오고, 없는 경우 에러를 던짐
     * => orElse(), orElseGet() 등 무언가 만들 수도 없는 상황일 때 사용
     * => default 로 NoSuchElementException 을 던지고, 원하는 에러 Supplier 로 원하는 제공 가능
     */
    @Test
    void optional_orElseThrow(){
        Optional<OnlineClass> optional = springClasses.stream()
                .filter(oc -> oc.getTitle().startsWith("jpa"))
                .findFirst();

//        OnlineClass onlineClass = optional.orElseThrow(() -> {
//            return new IllegalStateException(); // Supplier 로 원하는 에러 던짐
//        });
        // method reference 로 생성자 참조
//        OnlineClass onlineClass = optional.orElseThrow(IllegalStateException::new);

        assertThrows(NoSuchElementException.class, () -> optional.orElseThrow());
    }

    /**
     * map(U)
     * => mapper 함수를 통해 입력 값을 다른 값으로 변환하는 메서드
     * => 결과를 Optional 로 감싸서 리턴
     * => map() 으로 꺼내는 타입이 Optional 인 경우 복잡해짐
     * => stream 의 map() 은 1:1 매핑
     *
     * flatMap(Optional(U))
     * => mapper 함수를 통해 입력 값을 다른 값으로 변환하는 메서드
     * => map()이 제네릭 U로 정의했던 것과는 다르게, 메서드 시그니처의 파라미터가 제네릭으로 Optional(U) 로 정의되어 있음
     * => flatMap() 가 반환하는 값은 Optional 임
     * => mapping 해서 꺼내는 타입 자체가 optional 인 경우 한 번 꺼내어 반환
     */
    @Test
    void optional_map(){
        Optional<OnlineClass> optional = springClasses.stream()
                .filter(oc -> oc.getTitle().startsWith("jpa"))
                .findFirst();

        Optional<Integer> integer = optional.map(OnlineClass::getId);

        Optional<Optional<Progress>> progress1 = optional.map(OnlineClass::getOptionalProgress);
        Optional<Progress> progress2 = optional.flatMap(OnlineClass::getOptionalProgress);
    }

    private static OnlineClass createNewClass() {
        System.out.println("실행되었습니다.");
        return new OnlineClass(10, "New Class", false);
    }
}