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


}