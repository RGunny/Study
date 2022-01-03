package baseball;

import baseball.domain.Calculator;
import baseball.domain.Judgement;
import baseball.domain.NumberGenerator;

import java.util.Arrays;
import java.util.List;

/**
 * main메서드에 모든 것을 담기보다는 적절한 클래스를 만들어 각자의 역할을 하게 한다.
 * => 객체지향 프로그래밍
 * 1. 기능을 가지고 있는 클래스를 인스턴스화(: 객체)한다.
 * 2. 필요한 기능을(역할에 맞는) 각 인스턴스가 수행하게 한다. (의인화)
 * 3. 각 결과를 종합한다. (프로그램 동작)
 */
public class Application {
    public static void main(String[] args) {

        System.out.println("Hello, World");

        Calculator calculator = new Calculator();
        System.out.println("calculator.add(1, 4) = " + calculator.add(1, 4));
        System.out.println("calculator.result = " + calculator.result);
        System.out.println("calculator.shareResult = " + calculator.shareResult);

        Calculator calculator2 = new Calculator();
        System.out.println("calculator2.add(2000, 21) = " + calculator2.add(2000, 21));
        System.out.println("calculator2.result = " + calculator2.result);
        System.out.println("calculator.shareResult = " + calculator.shareResult);

        System.out.println("calculator.result = " + calculator.result); // 4
        // => 복사가 된 것이니 값이 변하지 않고 4가 출력되어야 함
        // => Calculator1과 Calculator2는 서로 영향을 끼치지 않는 독립적인 객체
        System.out.println("calculator.shareResult = " + calculator.shareResult); // 2021

        /**
         * 어차피 shareResult는 모든 인스턴스가 같은 값인데 뭐하러 쓰냐 헷갈리게
         * 굳이 instance화 해서 shareResult를 쓰는 이유가 뭐냐?
         * calculator1.sharedResult
         * calculator2.sharedResult
         *
         * => 굳이 그럴 필요 없다
         * => 바로 클래스에서 접근 가능
         * Calculator.shareResult
         */
        System.out.println("Calculator.shareResult = " + Calculator.shareResult);

        System.out.println("Calculator.SHARE_BIRTHDAY = " + Calculator.SHARE_BIRTHDAY);


        ////////////////////////////////////////////////////////////////////////

        int[] computer = {1, 2, 3};
        int[] player = {1, 2, 3};

        // "3 Strike" = match(computer, player);
        ////////////////////////////////////////////////////////////////////////
        final NumberGenerator generator = new NumberGenerator();
        List<Integer> numbers = generator.createRandomNumber();
        System.out.println(numbers);
        // 중복이 발생하네...?

        ////////////////////////////////////////////////////////////////////////
        Judgement judgement = new Judgement();
        final int count = judgement.correctCount(Arrays.asList(1, 2, 3), Arrays.asList(1, 2, 3));
        System.out.println("count = " + count); // 3
        // ctrl + alt + L    /    command + option + L 자동 정렬
        // Class Level 이 아닌 package 레벨에서 자동정렬하고 싶을 시, => 좌측 패키지에서 해당 단축키
    }
}
