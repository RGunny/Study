package me.rgunny.study.optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OnlineClassTest {

    @BeforeEach
    void setUp() {
        List<Object> springClasses = new ArrayList<>();
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
}