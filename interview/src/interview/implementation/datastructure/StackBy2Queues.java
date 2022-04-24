package interview.implementation.datastructure;

import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.IntStream;

public class StackBy2Queues {

    public static void main(String[] args) {
        MyStack<Integer> stack = new MyStack<>();
        IntStream.rangeClosed(1, 5).forEach(i -> stack.add(i));
        System.out.println("stack.isEmpty() = " + stack.isEmpty());
        System.out.println("stack.peek() = " + stack.peek());
        System.out.println("stack.poll() = " + stack.poll());
        System.out.println("stack.poll() = " + stack.poll());
        System.out.println("stack.poll() = " + stack.poll());
        stack.add(6);
        System.out.println("stack.peek() = " + stack.peek());
        System.out.println("stack.poll() = " + stack.poll());
        System.out.println("stack.poll() = " + stack.poll());
        System.out.println("stack.poll() = " + stack.poll());
        System.out.println("stack.poll() = " + stack.poll());
    }

}

class MyStack<T> {
    Queue<T> mainQueue, subQueue;

    public MyStack() {
        mainQueue = new LinkedList<>();
        subQueue = new LinkedList<>();
    }

    public void add(T value) {
        mainToSub();
        mainQueue.add(value);
        subToMain();
    }

    public T poll() {
        return mainQueue.isEmpty() ? null : mainQueue.poll();
    }

    public T peek() {
        return mainQueue.isEmpty() ? null : mainQueue.peek();
    }

    public int size() {
        return mainQueue.size();
    }

    public boolean isEmpty() {
        return mainQueue.isEmpty();
    }

    private void mainToSub() {
        while (!mainQueue.isEmpty()) {
            subQueue.add(mainQueue.poll());
        }
    }

    private void subToMain() {
        while (!subQueue.isEmpty()) {
            mainQueue.add(subQueue.poll());
        }
    }

}