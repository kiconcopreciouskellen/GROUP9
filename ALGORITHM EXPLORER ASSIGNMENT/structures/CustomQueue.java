package structures;

import core.OperationCounter;

/** Circular-array queue (FIFO) that doubles its capacity when full. Operation = one enqueue or one dequeue. */
public class CustomQueue {
    private int[] data;
    private int front = 0;
    private int size = 0;
    private final OperationCounter ops;

    public CustomQueue(int initialCapacity, OperationCounter ops) {
        this.data = new int[Math.max(1, initialCapacity)];
        this.ops = ops;
    }

    public void enqueue(int value) {
        ops.increment();
        if (size == data.length) grow();
        data[(front + size) % data.length] = value;
        size++;
    }

    public int dequeue() {
        if (isEmpty()) throw new IllegalStateException("Queue underflow: cannot dequeue from an empty queue.");
        ops.increment();
        int value = data[front];
        front = (front + 1) % data.length;
        size--;
        return value;
    }

    public int peek() {
        if (isEmpty()) throw new IllegalStateException("The queue is empty.");
        return data[front];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    private void grow() {
        int[] bigger = new int[data.length * 2];
        for (int i = 0; i < size; i++) bigger[i] = data[(front + i) % data.length];
        data = bigger;
        front = 0;
    }

    /** Front to back. */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(data[(front + i) % data.length]);
            if (i < size - 1) sb.append(", ");
        }
        return sb.append("]").toString();
    }
}
