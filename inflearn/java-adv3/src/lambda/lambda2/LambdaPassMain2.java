package lambda.lambda2;

import lambda.MyFunction;

// 2. 람다를 메서드(함수)에 전달하기
public class LambdaPassMain2 {

    public static void main(String[] args) {
        // 람다를 변수에 담은 후에 매개변수에 전달 분석
        MyFunction add = (a, b) -> a + b; // 1. 람다 인스턴스 생성 -> 2. 참조값 반환, add에 x001 대입
        MyFunction sub = (a, b) -> a - b;

        System.out.println("변수를 통해 전달");
        calculate(add);
        calculate(sub);

        // 람다를 직접 전달 분석
        System.out.println("람다를 직접 전달");
        calculate((a, b) -> a + b);// 1. 람다 인스턴스 생성 -> 2. 참조값 반환, add에 x001 대입 및 매개변수에 전달
        calculate((a, b) -> a - b);
    }

    static void calculate(MyFunction function) { // 메서드 호출, 매개변부세 람다 참조값 대입
        int a = 1;
        int b = 2;

        System.out.println("계산 시작");
        int result = function.apply(a, b);
        System.out.println("계산 결과: " + result);
    }
}
