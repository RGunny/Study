package thread.executor.future;

import java.util.Random;
import java.util.concurrent.*;

import static util.MyLogger.log;
import static util.ThreadUtils.sleep;

public class CallableMainV2 {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(1);
        log("submit() 호출");
        Future<Integer> future = es.submit(new MyCallable());
        log("future 즉시 반환, future = " + future);

        log("future.get() [블로킹] 메서드 호출 시작 ->  main 스레드 WAITING");
        Integer result = future.get(); // 블로킹 메서드 : 스레드가 어떤 결과를 얻기 위해 대기 (요청스레드가 락을 얻을 떄처럼, 결과를 얻기 위해 대기)
        // Thread.join(), Future.get() 과 같은 메서드는 스레드가 작업을 바로 수행하지 않고, 다른 작업이 완료될 때까지 기다리게 하는 메서드.
        // 이러한 메서드를 호출하면 호출한 스레드는 지정된 작업이 완료될 때까지 블록(대기)되어 다른 작업을 수행할 수 없다.
        log("future.get() [블로킹] 메서드 호출 완료 ->  main 스레드 RUNNABLE");

        log("result value = " + result);
        log("future 완료, future = " + future);
        es.close();
    }

    static class MyCallable implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            log("Callable 시작");
            sleep(2000);
            int value = new Random().nextInt(10);
            log("create valud  = " + value);
            log("Callable 완료");

            return value;
        }
    }
}
