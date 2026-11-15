package structures;

/**
 * Doubly linked list of ints built from our own Node objects (each node has data, prev and next).
 * It keeps both a head and a tail reference. When a value occurs more than once, operations that
 * take a reference element use its first occurrence.
 */
public class DoublyLinkedList {
    private static class Node {
        int data;
        Node prev;
        Node next;

        Node(int data) {
            this.data = data;
        }
    }

    private Node head;
    private Node tail;
    private int size;

    // ---------- insertion ----------

    /** O(1). */
    public void insertAtBeginning(int value) {
        Node node = new Node(value);
        node.next = head;
        if (head != null) head.prev = node; else tail = node;
        head = node;
        size++;
    }

    /** O(1): the tail reference avoids walking the list. */
    public void insertAtEnd(int value) {
        Node node = new Node(value);
        node.prev = tail;
        if (tail != null) tail.next = node; else head = node;
        tail = node;
        size++;
    }

    /** O(n) to find target, then O(1). */
    public void insertAfter(int target, int value) {
        Node ref = find(target);
        if (ref == tail) {
            insertAtEnd(value);
            return;
        }
        Node node = new Node(value);
        node.prev = ref;
        node.next = ref.next;
        ref.next.prev = node;
        ref.next = node;
        size++;
    }

    /** O(n) to find target, then O(1). */
    public void insertBefore(int target, int value) {
        Node ref = find(target);
        if (ref == head) {
            insertAtBeginning(value);
            return;
        }
        Node node = new Node(value);
        node.next = ref;
        node.prev = ref.prev;
        ref.prev.next = node;
        ref.prev = node;
        size++;
    }

    // ---------- deletion ----------

    /** O(1). Returns the removed value. */
    public int deleteAtBeginning() {
        if (head == null) throw new IllegalStateException("The list is empty, nothing to delete.");
        int value = head.data;
        unlink(head);
        return value;
    }

    /** O(1). Returns the removed value. */
    public int deleteAtEnd() {
        if (tail == null) throw new IllegalStateException("The list is empty, nothing to delete.");
        int value = tail.data;
        unlink(tail);
        return value;
    }

    /** O(n) to find target, then O(1). Returns the removed value. */
    public int deleteAfter(int target) {
        Node ref = find(target);
        if (ref.next == null) {
            throw new IllegalStateException("There is no element after " + target + ": it is the last node.");
        }
        int value = ref.next.data;
        unlink(ref.next);
        return value;
    }

    /** O(n) to find target, then O(1). Returns the removed value. */
    public int deleteBefore(int target) {
        Node ref = find(target);
        if (ref.prev == null) {
            throw new IllegalStateException("There is no element before " + target + ": it is the first node.");
        }
        int value = ref.prev.data;
        unlink(ref.prev);
        return value;
    }

    /** O(n). Deletes the first node holding value. */
    public void delete(int value) {
        if (head == null) throw new IllegalStateException("The list is empty, nothing to delete.");
        unlink(find(value));
    }

    // ---------- queries ----------

    /** O(n). Returns the 0-based position of the first node holding value, or -1. */
    public int search(int value) {
        int index = 0;
        for (Node cur = head; cur != null; cur = cur.next) {
            if (cur.data == value) return index;
            index++;
        }
        return -1;
    }

    public int size() { return size; }
    public boolean isEmpty() { return head == null; }

    /** Values from head to tail. Used for drawing. */
    public int[] toArray() {
        int[] values = new int[size];
        int i = 0;
        for (Node cur = head; cur != null; cur = cur.next) values[i++] = cur.data;
        return values;
    }

    /** Head to tail, for example null <-> 5 <-> 3 <-> 8 <-> null. */
    public String displayForward() {
        StringBuilder sb = new StringBuilder("null");
        for (Node cur = head; cur != null; cur = cur.next) sb.append(" <-> ").append(cur.data);
        return sb.append(" <-> null").toString();
    }

    /** Tail to head, following the prev references. */
    public String displayBackward() {
        StringBuilder sb = new StringBuilder("null");
        for (Node cur = tail; cur != null; cur = cur.prev) sb.append(" <-> ").append(cur.data);
        return sb.append(" <-> null").toString();
    }

    private void unlink(Node node) {
        if (node.prev != null) node.prev.next = node.next; else head = node.next;
        if (node.next != null) node.next.prev = node.prev; else tail = node.prev;
        size--;
    }

    private Node find(int target) {
        if (head == null) throw new IllegalStateException("The list is empty.");
        for (Node cur = head; cur != null; cur = cur.next) {
            if (cur.data == target) return cur;
        }
        throw new IllegalArgumentException("Element " + target + " was not found in the list.");
    }
}
