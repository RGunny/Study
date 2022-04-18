package interview.implementation.datastructure;

import java.util.EmptyStackException;

public class Stack {
    private int top;
    private int maxSize;
    private Object[] stack;

    public Stack(int maxSize) {
        this.maxSize = maxSize;
        this.stack = new Object[maxSize];
        this.top = -1;
    }

    public void push(Object item) {
        if (full()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        stack[++top] = item;
    }

    public Object pop() {
        Object item = peek();

        removeElementAt(top);

        return item;
    }

    public Object peek() {
        if (empty()) {
            throw new EmptyStackException();
        }
        return stack[top];
    }

    public boolean empty() {
        return (top == -1);
    }

    public boolean full() {
        return (top == maxSize - 1);
    }

    public void removeElementAt(int index) {
        if (index > top) {
            throw new ArrayIndexOutOfBoundsException(index + " >= " + top);
        } else if (index < 0) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        int j = top - index - 1;
        if (j > 0) {
            System.arraycopy(stack, index + 1, stack, index, j);
        }
        stack[top--] = null;
    }

    public static void main(String[] args) {
        java.util.Stack<Object> s = new java.util.Stack<>();
        Stack stack = new Stack(3);
        stack.push(1);
        stack.push(2);
        stack.push(3);
        System.out.println("stack.full() = " + stack.full());
        System.out.println("stack.empty() = " + stack.empty());
        System.out.println("stack.peek() = " + stack.peek());
        System.out.println("stack.pop() = " + stack.pop());
        System.out.println("stack.pop() = " + stack.pop());
        System.out.println("stack.peek() = " + stack.peek());
        System.out.println("stack.pop() = " + stack.pop());
        System.out.println("stack.empty() = " + stack.empty());

    }
}
