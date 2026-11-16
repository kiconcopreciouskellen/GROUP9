package structures;

/**
 * Singly linked list of ints built from our own Node objects (each node has data and next).
 * When a value occurs more than once, operations that take a reference element use its first occurrence.
 * Errors are reported with IllegalArgumentException (element not found) or IllegalStateException (no such position).
 */
public class SinglyLinkedList {
    private static class Node {
        int data;
        Node next;

        Node(int data) {
            this.data = data;
        }
    }

    private Node head;
    private int size;

    // ---------- insertion ----------

    /** O(1). */
    public void insertAtBeginning(int value) {
        Node node = new Node(value);
        node.next = head;
        head = node;
        size++;
    }

    /** O(n): walks to the last node. */
    public void insertAtEnd(int value) {
        Node node = new Node(value);
        if (head == null) {
            head = node;
        } else {
            Node cur = head;
            while (cur.next != null) cur = cur.next;
            cur.next = node;
        }
        size++;
    }

    /** O(n). Inserts value immediately after the first node holding target. */
    public void insertAfter(int target, int value) {
        Node ref = find(target);
        Node node = new Node(value);
        node.next = ref.next;
        ref.next = node;
        size++;
    }

    /** O(n). Inserts value immediately before the first node holding target. */
    public void insertBefore(int target, int value) {
        Node ref = find(target);
        if (ref == head) {
            insertAtBeginning(value);
            return;
        }
        Node prev = head;
        while (prev.next != ref) prev = prev.next;
        Node node = new Node(value);
        node.next = ref;
        prev.next = node;
        size++;
    }

    // ---------- deletion ----------

    /** O(1). Returns the removed value. */
    public int deleteAtBeginning() {
        if (head == null) throw new IllegalStateException("The list is empty, nothing to delete.");
        int value = head.data;
        head = head.next;
        size--;
        return value;
    }

    /** O(n). Returns the removed value. */
    public int deleteAtEnd() {
        if (head == null) throw new IllegalStateException("The list is empty, nothing to delete.");
        if (head.next == null) {
            int value = head.data;
            head = null;
            size--;
            return value;
        }
        Node prev = head;
        while (prev.next.next != null) prev = prev.next;
        int value = prev.next.data;
        prev.next = null;
        size--;
        return value;
    }

    /** O(n). Deletes the node right after the first node holding target. Returns the removed value. */
    public int deleteAfter(int target) {
        Node ref = find(target);
        if (ref.next == null) {
            throw new IllegalStateException("There is no element after " + target + ": it is the last node.");
        }
        int value = ref.next.data;
        ref.next = ref.next.next;
        size--;
        return value;
    }

    /** O(n). Deletes the node right before the first node holding target. Returns the removed value. */
    public int deleteBefore(int target) {
        Node ref = find(target);
        if (ref == head) {
            throw new IllegalStateException("There is no element before " + target + ": it is the first node.");
        }
        if (head.next == ref) {
            return deleteAtBeginning();
        }
        Node beforePrev = head;
        while (beforePrev.next.next != ref) beforePrev = beforePrev.next;
        int value = beforePrev.next.data;
        beforePrev.next = ref;
        size--;
        return value;
    }

    /** O(n). Deletes the first node holding value. */
    public void delete(int value) {
        if (head == null) throw new IllegalStateException("The list is empty, nothing to delete.");
        if (head.data == value) {
            head = head.next;
            size--;
            return;
        }
        Node prev = head;
        while (prev.next != null && prev.next.data != value) prev = prev.next;
        if (prev.next == null) throw new IllegalArgumentException("Element " + value + " was not found in the list.");
        prev.next = prev.next.next;
        size--;
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

    /** Head to tail, for example 5 -> 3 -> 8 -> null. */
    public String displayForward() {
        StringBuilder sb = new StringBuilder();
        for (Node cur = head; cur != null; cur = cur.next) sb.append(cur.data).append(" -> ");
        return sb.append("null").toString();
    }

    private Node find(int target) {
        if (head == null) throw new IllegalStateException("The list is empty.");
        for (Node cur = head; cur != null; cur = cur.next) {
            if (cur.data == target) return cur;
        }
        throw new IllegalArgumentException("Element " + target + " was not found in the list.");
    }
}
