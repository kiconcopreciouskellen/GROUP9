package structures;

public interface ArrayQueue {
    void enqueue(int value);
    int dequeue();
    int front();
    int size();
    boolean isEmpty();
    boolean isFull();
    int capacity();
    // Array index of the element where dequeue takes from. 
    int frontIndex();
    // Array index of the element where the last enqueue wrote.
    int rearIndex();
    boolean isOccupied(int index);
    int valueAt(int index);
    
    String display();
}
