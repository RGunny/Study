package baseball.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NumberGenerator {
    /**
     * List : 자바 컬렉션
     * => 구현해서 쓰는 일은 드뭄
     * => 하지만 어떻게 구현되어 있는 지는 알아야 함 => 버젼마다 다를 수 있음 => 다 알아야 함
     */
    /* 중복 발생
    public List<Integer> createRandomNumber() {
        List<Integer> numbers = new ArrayList<>(); // ArrayList : List 인터페이스(타입)의 실제 클래스
        for (int i = 0; i < 3; i++) {
            int number = new Random().nextInt(9) + 1;
            numbers.add(number);
        }
        return numbers;
    }
    */
    // 중복 제거 버전
    public List<Integer> createRandomNumber() {
        // 3개의 숫자가 담길 때까지 => for문으로 해결되지 않을 거 같다. => while이 필요 : 내부가 false가 나올 때까지 반복
        // 만약 이미 존재하는 숫자라면 담지 않는다.
        // 만약 존재하지 않는 숫자라면 담는다.
        List<Integer> numbers = new ArrayList<>(); // ArrayList : List 인터페이스(타입)의 실제 클래스
        while (numbers.size() < 3) {
            int number = new Random().nextInt(9) + 1;
            if (numbers.contains(number)) continue;
            numbers.add(number);
        }

        return numbers;
    }
}
