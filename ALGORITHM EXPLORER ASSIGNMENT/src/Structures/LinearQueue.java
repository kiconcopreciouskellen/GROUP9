package structures;

public class LinearQueue implements ArrayQueue {
    private final int[] data;
    private int front = 0;
    private int rear = -1;

    public LinearQueue(int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("Capacity must be at least 1.");
        data = new int[capacity];
    }

    @Override
    public void enqueue(int value) {
        if (isFull()) {
            if (front > 0) {
                throw new IllegalStateException("Queue is full (FALSE FULL): rear is at the last index ("
                        + (data.length - 1) + "), but the " + front + " slot(s) before the front are empty. "
                        + "A linear queue cannot reuse them. A circular queue can.");
            }
            throw new IllegalStateException("Queue is full (capacity " + data.length + ").");
        }
        data[++rear] = value;
    }

    @Override
    public int dequeue() {
        if (isEmpty()) throw new IllegalStateException("Queue underflow: the queue is empty, nothing to dequeue.");
        return data[front++];
    }

    @Override
    public int front() {
        if (isEmpty()) throw new IllegalStateException("The queue is empty, there is no front element.");
        return data[front];
    }

    @Override public int size() { return front > rear ? 0 : rear - front + 1; }
    @Override public boolean isEmpty() { return front > rear; }
    @Override public boolean isFull() { return rear == data.length - 1; }
    @Override public int capacity() { return data.length; }
    @Override public int frontIndex() { return front; }
    @Override public int rearIndex() { return rear; }
    @Override public boolean isOccupied(int index) { return index >= front && index <= rear; }
    @Override public int valueAt(int index) { return data[index]; }

    @Override
    public String display() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = front; i <= rear; i++) {
            sb.append(data[i]);
            if (i < rear) sb.append(", ");
        }
        return sb.append("]").toString();
    }
}
