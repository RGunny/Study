package lambda.lambda1;

/**
 * SAM : Single Abstract Method 단일 추상 메서드
 */
@FunctionalInterface // 추상메서드가 1개임을 보장, 1개가 아니면 컴파일 에러 발생 -> Multiple non-overriding abstract methods found in interface lambda. lambda1.SamInterface
public interface SamInterface {
    void run();
}
