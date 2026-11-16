package structures;

public class CircularQueue implements ArrayQueue {
    private final int[] data;
    private int front = 0;
    private int rear = -1;
    private int count = 0;

    public CircularQueue(int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("Capacity must be at least 1.");
        data = new int[capacity];
    }

    @Override
    public void enqueue(int value) {
        if (isFull()) throw new IllegalStateException("Queue is full (capacity " + data.length + "): every slot is in use.");
        rear = (rear + 1) % data.length;
        data[rear] = value;
        count++;
    }


    @Override
    public int dequeue() {
        if (isEmpty()) throw new IllegalStateException("Queue underflow: the queue is empty, nothing to dequeue.");
        int value = data[front];
        front = (front + 1) % data.length;
        count--;
        return value;
    }

    @Override
    public int front() {
        if (isEmpty()) throw new IllegalStateException("The queue is empty, there is no front element.");
        return data[front];
    }

    @Override public int size() { return count; }
    @Override public boolean isEmpty() { return count == 0; }
    @Override public boolean isFull() { return count == data.length; }
    @Override public int capacity() { return data.length; }
    @Override public int frontIndex() { return front; }
    @Override public int rearIndex() { return rear; }

    @Override
    public boolean isOccupied(int index) {
        if (count == 0) return false;
        int offset = (index - front + data.length) % data.length;
        return offset < count;
    }

    @Override public int valueAt(int index) { return data[index]; }

    @Override
    public String display() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < count; i++) {
            sb.append(data[(front + i) % data.length]);
            if (i < count - 1) sb.append(", ");
        }
        return sb.append("]").toString();
    }
}
