package me.rgunny.study.stream;

import java.util.stream.Stream;

public class StreamParallel {

    /**
     * 숫자 n을 인수로 받아 1 부터 n 까지의 모든 숫자의 합계를 반환하는 메서드
     */
    public long sequentialSum(long n) {
        return Stream.iterate(1L, i -> i + 1) // 무한 자연수 스트림 생성
                .limit(n) // n 개 이하로 제한
                .reduce(0L, Long::sum); // 모든 숫자를 더하는 스트림 리듀싱 연산
    }

}
