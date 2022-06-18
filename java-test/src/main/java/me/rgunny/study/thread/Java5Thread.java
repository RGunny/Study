package me.rgunny.study.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Thread after Java 5
 * - ExecutorService : 쓰레드 실행과 테스크 제출을 분리
 * - Callable : Runnable 의 발전된 형태로 Generics 지원, 결과 리턴 가능, throw exception 가능
 * - Future : 비동기 결과 값을 담기 위한 객체
 */
public class Java5Thread {

    public static void main(String[] args) throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        List<Future<Integer>> resultList = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            FactorialCalculator calculator = new FactorialCalculator(i);
            Future<Integer> result = executorService.submit(calculator);
            resultList.add(result);
        }

        executorService.awaitTermination(5, TimeUnit.SECONDS);

        for (int i = 0; i < resultList.size(); i++) {
            Future<Integer> result = resultList.get(i);
            Integer number = null;

            try {
                number = result.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            System.out.printf("Main: Task %d: %d\n", i, number);

        }

        executorService.shutdown();

    }

}

class FactorialCalculator implements Callable<Integer>
{
    private final Integer number;

    public FactorialCalculator(Integer number) {
        this.number = number;
    }

    @Override
    public Integer call() throws Exception {

        int result = 1;

        if ((number == 0) || (number == 1)) {
            result = 1;
        } else {
            for (int i = 2; i <= number; i++) {
                result *= i;
                TimeUnit.MILLISECONDS.sleep(20);
            }
        }

        System.out.printf("Factorial of %d is :: %d\n", number, result);
        return result;
    }
}
