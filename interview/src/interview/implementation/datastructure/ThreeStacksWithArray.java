package interview.implementation.datastructure;

import java.util.EmptyStackException;
import java.util.stream.IntStream;

public class ThreeStacksWithArray {
    public static void main(String[] args) {
        ThreeStacks stacks = new ThreeStacks(3);
        IntStream.rangeClosed(1, 3).forEach(i -> stacks.push(0, i));
        IntStream.rangeClosed(4, 6).forEach(i -> stacks.push(1, i));
        IntStream.rangeClosed(7, 9).forEach(i -> stacks.push(2, i));

        System.out.println("stacks.isFull(0) = " + stacks.isFull(0));
        System.out.println("stacks.isFull(0) = " + stacks.isFull(1));
        System.out.println("stacks.isFull(0) = " + stacks.isFull(2));

        System.out.println("stacks.peek(0,1) = " + stacks.peek(0));
        System.out.println("stacks.peek(0,1) = " + stacks.peek(1));
        System.out.println("stacks.peek(0,1) = " + stacks.peek(2));

        System.out.println("stacks.pop(0) = " + stacks.pop(0));
        System.out.println("stacks.pop(0) = " + stacks.pop(0));
        System.out.println("stacks.pop(0) = " + stacks.pop(0));
        System.out.println("stacks.pop(0) = " + stacks.pop(0));
    }
}

class ThreeStacks {
    private final int NUMBER_OF_STACKS = 3;
    private int stackCapacity;
    private int[] values;
    private int[] sizes;

    public ThreeStacks(int stackSize) {
        stackCapacity = stackSize;
        values = new int[stackSize * NUMBER_OF_STACKS];
        sizes = new int[NUMBER_OF_STACKS];
    }

    public void push(int stackNum, int value) {
        if (isFull(stackNum)) {
            throw new ArrayIndexOutOfBoundsException("스택이 가득 차 있습니다.");
        }

        sizes[stackNum]++;
        values[indexOfTop(stackNum)] = value;
    }

    public int pop(int stackNums) {
        if (isEmpty(stackNums)) {
            throw new EmptyStackException();
        }

        int topIndex = indexOfTop(stackNums);
        int value = values[topIndex];
        values[topIndex] = 0;
        sizes[stackNums]--;

        return value;
    }

    public int peek(int stackNum) {
        if (isEmpty(stackNum)) {
            throw new EmptyStackException();
        }

        return values[indexOfTop(stackNum)];
    }

    public boolean isEmpty(int stackNum) {
        return sizes[stackNum] == 0;
    }

    public boolean isFull(int stackNum) {
        return sizes[stackNum] == stackCapacity;
    }

    private int indexOfTop(int stackNum) {
        int offset = stackNum * stackCapacity;
        int size = sizes[stackNum];

        return offset + size - 1;
    }

}