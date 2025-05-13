package lambda.ex1;

import lambda.MyFunction;
import lambda.Procedure;

public class M1After {

    public static void greet(Procedure procedure) {
        System.out.println("=== 시작 ===");
        procedure.run();
        System.out.println("=== 끝 ===");
    }

    public static void main(String[] args) {
        greet(() -> System.out.println("Good Morning!"));
        greet(() -> System.out.println("Good Afternoon!"));
        greet(() -> System.out.println("Good Evening!"));
    }
}