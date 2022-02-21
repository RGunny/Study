package me.rgunny.study.lambda;

import java.util.Comparator;

public class LambdaTest {
    public static void main(String[] args) {

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
