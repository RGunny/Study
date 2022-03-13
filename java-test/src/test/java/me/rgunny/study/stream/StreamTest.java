package me.rgunny.study.stream;

import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;

class StreamTest {

    private List<OnlineClass> springClasses = new ArrayList<>();

    @BeforeEach
    void setUp() {
        springClasses.add(new OnlineClass(1, "spring boot", true));
        springClasses.add(new OnlineClass(2, "spring data jpa", true));
        springClasses.add(new OnlineClass(3, "spring mvc", false));
        springClasses.add(new OnlineClass(4, "spring core", false));
        springClasses.add(new OnlineClass(5, "rest api development", false));
    }



}