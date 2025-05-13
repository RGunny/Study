package lambda.lambda2;

import lambda.MyFunction;

// 1. 람다를 변수에 대입하기
public class LambdaPassMain1 {

    public static void main(String[] args) {
        MyFunction add = (a, b) -> a + b; // 1. 람다 인스턴스 생성 -> 2. 참조값 반환, add에 x001 대입
        MyFunction sub = (a, b) -> a - b;

        System.out.println("add.apply(1, 2) = " + add.apply(1, 2));
        System.out.println("sub.apply(1, 2) = " + sub.apply(1, 2));

        MyFunction cal = add; // 3. cal에 참조값 대입
        System.out.println("cal(add).apply(1, 2) = " + cal.apply(1, 2));

        cal = sub;
        System.out.println("cal(sub).apply(1, 2) = " + cal.apply(1, 2));
    }

}
