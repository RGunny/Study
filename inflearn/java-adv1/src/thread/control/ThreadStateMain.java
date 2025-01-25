package thread.control;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static util.MyLogger.log;

public class ThreadStateMain {

    public static void main(String[] args) throws InterruptedException {
        Thread myThread = new Thread(new MyRunnable(), "myThread");
        log("myThread.state = " + myThread.getState());
        log("myThread.start()");
        myThread.start();
        Thread.sleep(1000);
        log("myThread.state3 = " + myThread.getState()); // TIMED_WAITING
        Thread.sleep(4000);
        log("myThread.state5() = " + myThread.getState()); // TERMINATED
        log("end");

        Predicate<String> startsWithJ = (word) -> word.startsWith("j");
        Predicate<String> fourCharacters = (word) -> word.length() == 4;
        List<String> word = Stream
                .of("java", "javascript", "jakarta")
                .filter(startsWithJ.and(fourCharacters))
                .toList();
        System.out.println("word = " + word);
    }

    static class MyRunnable implements Runnable {

        @Override
        public void run() {
            try {
                log("start");
                log("myThread.state2 = " + Thread.currentThread().getState()); // RUNNABLE
                log("sleep() start");
                Thread.sleep(1000);
                log("myThread.state4 = " + Thread.currentThread().getState()); // RUNNABLE
                log("sleep() end");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
