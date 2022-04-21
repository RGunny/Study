package interview.implementation.datastructure;

import java.util.Stack;
import java.util.stream.IntStream;

public class QueueBy2Stacks {

    public static void main(String[] args) {
        MyQueue<Integer> queue = new MyQueue<>();

        IntStream.rangeClosed(1, 5).forEach(i -> queue.push(i));
        System.out.println("queue.size() = " + queue.size());
        System.out.println("queue.peek() = " + queue.peek());
        System.out.println("queue.remove() = " + queue.pop());
        System.out.println("queue.remove() = " + queue.pop());
        System.out.println("queue.remove() = " + queue.pop());
        queue.push(10);
        System.out.println("queue.remove() = " + queue.pop());
        System.out.println("queue.remove() = " + queue.pop());
        System.out.println("queue.remove() = " + queue.pop());
        System.out.println("queue.remove() = " + queue.pop());
        System.out.println("queue.peek() = " + queue.peek());
    }

}

class MyQueue<T> {
    Stack<T> stackNewest, stackOldest;

    public MyQueue() {
        stackNewest = new Stack();
        stackOldest = new Stack();
    }

    public void push(T value) {
        stackNewest.add(value);
    }

    public T peek() {
        if (size() == 0) {
            return null;
        }

        shiftStacks();

        return stackOldest.peek();
    }

    public T pop() {
        if (size() == 0) {
            return null;
        }

        shiftStacks();
        return stackOldest.pop();
    }

    public int size() {
        return stackNewest.size() + stackOldest.size();
    }

    private void shiftStacks() {
        if (stackOldest.empty()) {
            while (!stackNewest.empty()) {
                stackOldest.add(stackNewest.pop());
            }
        }
    }
}