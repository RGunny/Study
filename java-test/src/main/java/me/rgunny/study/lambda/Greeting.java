package me.rgunny.study.lambda;

public class Greeting {

    private String name;

    public Greeting() {} // 빈 생성자

    public Greeting(String name) { // name을 받는 생성자
        this.name = name;
    }

    public String hello(String name) { // 인스턴스 메서드
        return "hello " + name;
    }

    public static String hi(String name) { // 스태틱 메서드
        return "hi " + name;
    }

    public String getName() { // getter
        return name;
    }
}