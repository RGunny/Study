package me.rgunny.study.lambda;

public class Foo {

    public static void main(String[] args) {
        // 익명 내부 클래스 (anonymous inner class)
        RunSomething runSomething = new RunSomething() {
            @Override
            public void doIt() {
                System.out.println("Hello");
            }
        };
        // interface가 하나 인 경우 자바8부터 줄여서 사용가능한 문법이 생김 (lambda)
        RunSomething usingLambda = () -> System.out.println("Hello");
    }
}
