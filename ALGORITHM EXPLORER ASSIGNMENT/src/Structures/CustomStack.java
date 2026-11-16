package structures;

/** Array-based stack (LIFO) with a fixed capacity chosen by the user. */
public class CustomStack {
    private final int[] data;
    private int top = -1;

    public CustomStack(int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("Capacity must be at least 1.");
        data = new int[capacity];
    }

    public void push(int value) {
        if (isFull()) throw new IllegalStateException("Stack overflow: the stack is full (capacity " + data.length + ").");
        data[++top] = value;
    }

    public int pop() {
        if (isEmpty()) throw new IllegalStateException("Stack underflow: the stack is empty, nothing to pop.");
        return data[top--];
    }

    public int peek() {
        if (isEmpty()) throw new IllegalStateException("The stack is empty, there is no top element.");
        return data[top];
    }

    public int size() { return top + 1; }
    public int capacity() { return data.length; }
    public boolean isEmpty() { return top == -1; }
    public boolean isFull() { return top == data.length - 1; }

    /** Element at an array index (0 = bottom). Used for drawing. */
    public int get(int index) { return data[index]; }

    /** Bottom to top, for example [10, 20, 30]. */
    public String display() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i <= top; i++) {
            sb.append(data[i]);
            if (i < top) sb.append(", ");
        }
        return sb.append("]").toString();
    }
}
