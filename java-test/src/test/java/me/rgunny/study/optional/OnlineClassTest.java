package me.rgunny.study.optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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


}