package me.rgunny.study.rxjava;

import io.reactivex.rxjava3.core.BackpressureOverflowStrategy;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BackPressureStrategy {

    /**
     * - 생성 시 전략: Flowable.create(..., BackpressureStrategy.XXX)
     * - 연산자: source.onBackpressureBuffer/Drop/Latest(...)
     */
    public static void main(String[] args) {
        missingWithLaterOperator();   // 연산자 방식으로 정책 부착
        errorSourceStrategy();        // 생성 시 ERROR
        bufferUnboundedSource();      // 생성 시 BUFFER
        dropLatestOperator();         // 연산자: 용량 제한 + DROP_LATEST
        dropOldestOperator();         // 연산자: 용량 제한 + DROP_OLDEST
        dropSourceStrategy();         // 생성 시 DROP
        latestSourceStrategy();       // 생성 시 LATEST
    }

    /**
     * 1) MISSING (생성 시) + 나중에 LATEST 연산자 부착
     * - MISSING 자체는 아무 정책도 적용하지 않음
     * - 뒤에 onBackpressureLatest()를 붙여 drop 정책을 연산자 레벨에서 지정
     */
    static void missingWithLaterOperator() {
        System.out.println("=== MISSING + onBackpressureLatest (Operator) ===");
        Flowable<Integer> src = Flowable.create(em -> {
            for (int i = 0; i < 10_000; i++) em.onNext(i); // 빠른 생산자 (요청량 무시)
            em.onComplete();
        }, BackpressureStrategy.MISSING);

        src.onBackpressureLatest()
                .observeOn(Schedulers.io()) // observeOn: 소비 스레드 경계 + 내부 버퍼(기본 128, 여기선 기본값)
                .map(BackPressureStrategy::slow) // 느린 소비 흉내
                .blockingSubscribe(i -> System.out.println("MISSING→LATEST: " + i));
        System.out.println("=================================================");
    }

    /**
     * 2) ERROR (생성 시 전략)
     * - 요청량 초과 주입 시 즉시 MissingBackpressureException.
     * - 유실 금지 + 과주입 즉시 실패 시나리오에 적합.
     */
    static void errorSourceStrategy() {
        System.out.println("=== ERROR (Source Strategy) ===");
        Flowable<Integer> src = Flowable.create(em -> {
            for (int i = 0; i < 10_000; i++) em.onNext(i);  // 요청량 안 보고 밀어넣음 → 에러 유도
            em.onComplete();
        }, BackpressureStrategy.ERROR);

        src.observeOn(Schedulers.io())
                .map(BackPressureStrategy::slow)
                .blockingSubscribe(
                        i -> System.out.println("ERROR: " + i),
                        e -> System.out.println("onError: " + e) // MissingBackpressureException
                );
        System.out.println("================================");
    }

    /**
     * 3) BUFFER (생성 시 전략) - 무제한
     * - 내부 큐가 사실상 무제한이라 메모리 위험
     */
    static void bufferUnboundedSource() {
        System.out.println("=== BUFFER (Source, Unbounded) ===");
        Flowable<Integer> unbounded = Flowable.create(em -> {
            for (int i = 0; i < 100_000; i++) em.onNext(i);
            em.onComplete();
        }, BackpressureStrategy.BUFFER);

        unbounded.observeOn(Schedulers.io())
                .map(BackPressureStrategy::slow)
                .blockingSubscribe(i -> System.out.println("BUFFER(unbounded): " + i));
        System.out.println("==================================");
    }

    /**
     * 4) BUFFER + DROP_LATEST (연산자 방식)
     * - 용량(capacity) 초과 시 마지막에 들어온 것 부터 버림
     * - 최신성보다 과거 누적 보존을 우선할 때 사용
     */
    static void dropLatestOperator() {
        System.out.println("=== onBackpressureBuffer(DROP_LATEST, capacity=1024) ===");
        Flowable<Integer> src = fastMissing();

        src.onBackpressureBuffer(
                        1024,                                    // 유한 버퍼
                        () -> System.out.println("overflow!"),
                        BackpressureOverflowStrategy.DROP_LATEST // 초과 시 최신항목 Drop
                )
                .observeOn(Schedulers.io(), false, 256)          // observeOn 버퍼 = 256 (기본 128)
                .map(BackPressureStrategy::slow)
                .blockingSubscribe(i -> System.out.println("DROP_LATEST → " + i));
        System.out.println("=========================================================");
    }

    /**
     * 5) BUFFER + DROP_OLDEST (연산자 방식)
     * - 용량 초과 시 가장 오래된 것 부터 버림
     * - 최신 상태 유지가 더 중요할 때 사용
     */
    static void dropOldestOperator() {
        System.out.println("=== onBackpressureBuffer(DROP_OLDEST, capacity=1024) ===");
        Flowable<Integer> src = fastMissing();

        src.onBackpressureBuffer(
                        1024,
                        () -> System.out.println("overflow!"),
                        BackpressureOverflowStrategy.DROP_OLDEST
                )
                .observeOn(Schedulers.io(), false, 256)
                .map(BackPressureStrategy::slow)
                .blockingSubscribe(i -> System.out.println("DROP_OLDEST → " + i));
        System.out.println("========================================================");
    }

    /**
     * 6) DROP (생성 시 전략)
     * - 초과분을 즉시 버리는 소스 전략(드롭 지점은 소스 쪽)
     * - 지연을 최소화해야 하고 일부 유실을 허용할 때 사용
     */
    static void dropSourceStrategy() {
        System.out.println("=== DROP (Source Strategy) ===");
        Flowable<Integer> drop = Flowable.create(em -> {
            for (int i = 0; i < 10_000; i++) em.onNext(i);  // 초과 시 즉시 폐기
            em.onComplete();
        }, BackpressureStrategy.DROP);

        drop.observeOn(Schedulers.io(), false, 256)
                .map(BackPressureStrategy::slow)
                .blockingSubscribe(i -> System.out.println("DROP → " + i));
        System.out.println("================================");
    }

    /**
     * 7) LATEST (생성 시 전략)
     * - 원자 레퍼런스로 "가장 최신" 하나만 유지(소스 레벨에서 최신화)
     * - 대시보드/모니터링처럼 최신 상태만 중요할 때 사용
     */
    static void latestSourceStrategy() {
        System.out.println("=== LATEST (Source Strategy) ===");
        Flowable<Integer> latest = Flowable.create(em -> {
            for (int i = 0; i < 10_000; i++) em.onNext(i);
            em.onComplete();
        }, BackpressureStrategy.LATEST);

        latest.observeOn(Schedulers.io(), false, 256)
                .map(BackPressureStrategy::slow)
                .blockingSubscribe(i -> System.out.println("LATEST → " + i));
        System.out.println("=================================");
    }

    // 빠른 생산자 (요청량을 고려하지 않음)
    static Flowable<Integer> fastMissing() {
        return Flowable.create(em -> {
            for (int i = 0; i < 10_000; i++) em.onNext(i);
            em.onComplete();
        }, BackpressureStrategy.MISSING);
    }

    // 느린 소비 시뮬레이션(블로킹, 소비 병목 유도용)
    static <T> T slow(T v) {
        try { Thread.sleep(10); } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return v;
    }
}