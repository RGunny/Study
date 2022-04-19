package interview.implementation.datastructure;

import java.util.LinkedList;

public class Queue {
    private int front;
    private int rear;
    private int maxSize;
    private Object[] queue;

    public Queue(int maxSize) {
        this.maxSize = maxSize;
        this.front = 0;
        this.rear = -1;
        this.queue = new Object[maxSize];
    }

    public void add(Object item) {
        if (isFull()) throw new ArrayIndexOutOfBoundsException();

        queue[++rear] = item;
    }

    public Object poll() {
        Object item = peek();

        front++;

        return item;
    }

    public Object peek() {
        if(isEmpty()) throw new ArrayIndexOutOfBoundsException();

        return queue[front];
    }

    public boolean isEmpty() {
        return (front == rear + 1);
    }

    public boolean isFull() {
        return (rear == maxSize - 1);
    }

    public static void main(String[] args) {
        java.util.Queue<Object> q = new LinkedList<>();

        Queue queue = new Queue(3);
        System.out.println("queue.isEmpty() = " + queue.isEmpty());
        System.out.println("queue.isFull() = " + queue.isFull());
        queue.add(3);
        queue.add(1);
        queue.add(2);
        System.out.println("queue.isFull() = " + queue.isFull());
        System.out.println("queue.peek() = " + queue.peek());
        System.out.println("queue.poll() = " + queue.poll());
        System.out.println("queue.poll() = " + queue.poll());
        System.out.println("queue.poll() = " + queue.poll());
        System.out.println("queue.isEmpty() = " + queue.isEmpty());

    }
}
