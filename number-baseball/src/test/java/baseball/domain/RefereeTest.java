package baseball.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RefereeTest { // Class 이름 + Test (camel)
    private Referee referee;
    private static final List<Integer> ANSWER = Arrays.asList(1, 2, 3); // 변하지 않을 것 같으니 상수로 만들어 줌

    @BeforeEach // 매 함수마다 시작 전 실행 => 각각 다른 인스턴스 생성
    void setUp() {
        referee = new Referee();
    }

    @Test
    void 스트라이크3() {
//        Referee referee = new Referee();
//        String result = referee.compare(Arrays.asList(1, 2, 3), Arrays.asList(1, 2, 3));
        String result = referee.compare(ANSWER, Arrays.asList(1, 2, 3));
        System.out.println("result = " + result); // 이렇게 사람이 확인하는 것이 아닌,
        assertThat(result).isEqualTo("0 볼 3 스트라이크"); // assert : 주장하다
//        assertThat(result).isEqualTo("0 볼 2 스트라이크");
    }

    @Test
    void 아웃() {
//        Referee referee = new Referee();
//        String result = referee.compare(Arrays.asList(1, 2, 3), Arrays.asList(7,8,9));
        String result = referee.compare(ANSWER, Arrays.asList(7,8,9));
        System.out.println("result = " + result); // 이렇게 사람이 확인하는 것이 아닌,
        assertThat(result).isEqualTo("아웃"); // assert : 주장하다
    }

    @Test
    void 볼3() {
//        Referee referee = new Referee();
//        String result = referee.compare(Arrays.asList(1, 2, 3), Arrays.asList(2, 3, 1));
        String result = referee.compare(ANSWER, Arrays.asList(2, 3, 1));
        System.out.println("result = " + result); // 이렇게 사람이 확인하는 것이 아닌,
        assertThat(result).isEqualTo("3 볼 0 스트라이크"); // assert : 주장하다
    }

    @Test
    void 볼2_스트라이크1() {
//        Referee referee = new Referee();
//        String result = referee.compare(Arrays.asList(1, 2, 3), Arrays.asList(3, 2, 1));
        String result = referee.compare(ANSWER, Arrays.asList(3, 2, 1));
        System.out.println("result = " + result); // 이렇게 사람이 확인하는 것이 아닌,
        assertThat(result).isEqualTo("2 볼 1 스트라이크"); // assert : 주장하다
    }
}