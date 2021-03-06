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

    /**
     * legacy style
     */
    public long iterativeSum(long n) {
        long result = 0;
        for (long i = 1L; i <= n; i++) {
            result += i;
        }
        return result;
    }

    /**
     * 순차 스트림을 병렬 스트림으로 변환한 메서드
     * - 기존 리듀싱 연산이 병렬로 처리됨
     * - 스트림이 여러 청크로 분할되어 있음
     * - 마지막으로 리듀싱 연산으로 생성된 부분 결과를 다시 리듀싱 연산으로 합쳐서 전체 스트림의 리듀싱 결과를 도출
     */
    public long parallelSum(long n) {
        return Stream.iterate(1L, i -> i + 1)
                .limit(n)
                .parallel() // 스트림을 병렬 스트림으로 변환
                .reduce(0L, Long::sum);
    }

}
