package me.rgunny.study.lambda;

// FuntionalInterace를 사용해 interface를 더 견고하게 사용 가능
@FunctionalInterface
public interface RunSomething {

    abstract void doIt(); // 추상 메서드가 하나 있는 인터페이스 => 함수형 인터페이스 (Funtional Interface)

//    abstract void doItAgain(); // 추상 메서드가 두 개 => 함수형 인터페이스 X => @FuntionalInterface 애노테이션으로 정의했다면 컴파일 에러 발생

    // 자바8부터 interface에서 static method 정의 가능
    // static, default method와 같은 다른 메서드가 있더라도 함수형 인터페이스
    // 함수형 인터페이스를 구분짓는 관건은 추상 메서드가 한 개 인지 아닌지
    static void printName(){
        System.out.println("RGunny");
    }

    default void printAge(){
        System.out.println("29");
    }
}
