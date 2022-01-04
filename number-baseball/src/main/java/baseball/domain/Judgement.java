package baseball.domain;

import java.util.List;

public class Judgement {
    public int correctCount(List<Integer> computer, List<Integer> player) {
        /*
        for (int i = 0; i < computer.size(); i++) {
            int computerNumber = computer.get(i);
            player.contains(computerNumber);
        }
         */
        int result = 0;
        for (int i = 0; i < player.size(); i++) {
            int playerNumber = player.get(i);
            if (computer.contains(playerNumber)) {
                result = result + 1; // result = result + 1
            }
        }
        return result;
    }

    public boolean hasPlace(List<Integer> computer, int placeIndex, int number) {
//        if (computer.get(placeIndex) == number) {  // 이렇게 get()으로 특정 자리의 숫자를 가져올 수 있을까요? => 인덱싱이 있음, 0부터 ~
//            return true;
//        }
//        return false;
        return computer.get(placeIndex) == number; // 한 줄로 줄일 수 있음
    }
}
