package me.rgunny.study.thread;

public class SimpleThread {
    public static void main(String[] args) {
        // Implemented with inheritance
        ThreadByInheritance thread1 = new ThreadByInheritance();

        // Implemented as an interface
        Runnable r = new ThreadByImplement();
        Thread thread2 = new Thread(r);    // Constructor: Thread(Runnable target)
        // Thread thread2 = new Thread(new ThreadByImplement());

//        thread1.start();
//        thread2.start();

//        thread1.run();
//        thread2.run();
    }
}

class ThreadRun implements Runnable {

    @Override
    public void run() {

    }
}

class ThreadByInheritance extends Thread {

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            System.out.print(0);
        }
    }
}

class ThreadByImplement implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            System.out.print(1);
        }
    }
}