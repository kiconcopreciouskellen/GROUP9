package structures;

import core.OperationCounter;

/** Array-based stack (LIFO) that doubles its capacity when full. Operation = one push or one pop. */
public class CustomStack {
    private int[] data;
    private int top = -1;
    private final OperationCounter ops;

    public CustomStack(int initialCapacity, OperationCounter ops) {
        this.data = new int[Math.max(1, initialCapacity)];
        this.ops = ops;
    }

    public void push(int value) {
        ops.increment();
        if (top == data.length - 1) grow();
        data[++top] = value;
    }

    public int pop() {
        if (isEmpty()) throw new IllegalStateException("Stack underflow: cannot pop from an empty stack.");
        ops.increment();
        return data[top--];
    }

    public int peek() {
        if (isEmpty()) throw new IllegalStateException("The stack is empty.");
        return data[top];
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public int size() {
        return top + 1;
    }

    private void grow() {
        int[] bigger = new int[data.length * 2];
        for (int i = 0; i < data.length; i++) bigger[i] = data[i];
        data = bigger;
    }

    /** Bottom to top. */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i <= top; i++) {
            sb.append(data[i]);
            if (i < top) sb.append(", ");
        }
        return sb.append("]").toString();
    }
}
