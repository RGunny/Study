package baseball.domain;

import java.util.List;

public class Referee {
    public String compare(List<Integer> computer, List<Integer> player) {
        // 몇 개의 숫자가 같은 지 알아낸 뒤 => Judgement Class가 가지고 있는 기능 => 협력 필요
        // 스트라이크의 개수를 구해 뺀다. : 볼의 개수
        // 즉, 남은 수는 볼의 개수이다.

        final Judgement judgement = new Judgement();
        int correctCount = judgement.correctCount(computer, player);

        int strike = 0;
        for (int placeIndex = 0; placeIndex < computer.size(); placeIndex++) {
            if (judgement.hasPlace(computer, placeIndex, player.get(placeIndex))) {
                strike++;
            }
        }
        int ball = correctCount - strike;

        if (strike == 0) {
            return "아웃";
        }
        return ball + " 볼 " + strike + " 스트라이크";
    }
}
