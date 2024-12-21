package thread.start.test;

import util.MyLogger;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 1. `CounterRunnable` 이라는 이름의 클래스를 만들자, 이 클래스는 `Runnable` 인터페이스를 구현해야 한다.
 * 2. `CounterRunnable` 은 1부터 5까지의 숫자를 1초 간격으로 출력해야 한다. 앞서 우리가 만든 `log()` 기능을 사용해서 출력해라.
 * 3. `main` 메서드에서 `CounterRunnable` 의 인스턴스를 이용해서 `Thread` 를 생성하고 실행해라.
 * 4. 스레드의 이름은 "counter"로 지정해야 한다.
 */
public class StartTest2Main {

    public static void main(String[] args) {
        Thread thread = new Thread(new CounterRunnable(), "counter");
        thread.start();
    }

    public static class CounterRunnable implements Runnable {

        @Override
        public void run() {

            IntStream.rangeClosed(1, 5).forEach(i -> {
                MyLogger.log(i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

        }
    }

}
