package me.rgunny.study.lambda;

import java.util.Comparator;

public class LambdaTest {
    public static void main(String[] args) {

        RunSomething runSomething = new RunSomething() {
            @Override
            public void doIt() {
                System.out.println("Hello");
            }
        };
        // interface가 하나 인 경우 자바8부터 줄여서 사용가능한 문법이 생김 (lambda)
        RunSomething usingLambda = () -> System.out.println("Hello");


    }

    public int pureFunction(int number){
        return number + 10;
    }

    public void lambda3Expressions(){
        // Existing Code
        Comparator<Integer> byWeight1 = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        };

        // Use lambda Code
        Comparator<Integer> byWeight2 = (Integer o1, Integer o2) -> o1.compareTo(o2);

        // Replace lambda with method reference
        Comparator<Integer> byWeight3 = Integer::compareTo;
    }
}
