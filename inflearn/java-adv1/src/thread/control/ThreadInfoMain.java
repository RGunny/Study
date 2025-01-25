package thread.control;

import util.MyLogger;

import static util.MyLogger.log;

public class ThreadInfoMain {

    public static void main(String[] args) {
        // main thread
        Thread mainThread = Thread.currentThread();
        log("mainThread = " + mainThread);
        log("mainThread.threadId=()" + mainThread.threadId());
        log("mainThread.getName() = " + mainThread.getName());
        log("mainThread.getThreadGroup() = " + mainThread.getThreadGroup());
        log("mainThread.getPriority() = " + mainThread.getPriority());
        log("mainThread.getState() = " + mainThread.getState());

        // my thread
        Thread myThread = Thread.currentThread();
        log("myThread = " + myThread);
        log("myThread.threadId=()" + myThread.threadId());
        log("myThread.getName() = " + myThread.getName());
        log("myThread.getThreadGroup() = " + myThread.getThreadGroup());
        log("myThread.getPriority() = " + myThread.getPriority());
        log("myThread.getState() = " + myThread.getState());

    }

}
