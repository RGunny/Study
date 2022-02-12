package me.rgunny.study.thread;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleThreadTest {

    @Test
    public void Thread2Start() throws Exception{
        // Implemented with inheritance
        ThreadByInheritance thread1 = new ThreadByInheritance();

        // Implemented as an interface
        Runnable r = new ThreadByImplement();
        Thread thread2 = new Thread(r);    // Constructor: Thread(Runnable target)
        // Thread thread2 = new Thread(new ThreadByImplement());

        thread1.start();
        thread2.start();


    }

    @Test
    public void Thread2StartDuplicate() throws Exception{
        // Implemented with inheritance
        ThreadByInheritance thread1 = new ThreadByInheritance();

        // Implemented as an interface
        Runnable r = new ThreadByImplement();
        Thread thread2 = new Thread(r);    // Constructor: Thread(Runnable target)
        // Thread thread2 = new Thread(new ThreadByImplement());

        thread1.start();
        thread2.start();

        thread1.run();
        thread2.run();
    }

    @Test
    public void Thread2Run() throws Exception{
        // Implemented with inheritance
        ThreadByInheritance thread1 = new ThreadByInheritance();

        // Implemented as an interface
        Runnable r = new ThreadByImplement();
        Thread thread2 = new Thread(r);    // Constructor: Thread(Runnable target)
        // Thread thread2 = new Thread(new ThreadByImplement());

        thread1.run();
        thread2.run();
    }

    @Test
    public void MathThread() throws Exception{
        // currentThread 메서드는 현재 실행 중인 쓰레드의 참조를 반환하는 스태틱 메서드
        Thread thread1 = Thread.currentThread();
        System.out.println("thread1 = " + thread1);

        Thread thread2 = new Thread(new ThreadRun());
        System.out.println("thread2 = " + thread2);
    }

}