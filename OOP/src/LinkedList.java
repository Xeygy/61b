public class LinkedList {
    IntNode first;

    public LinkedList() {}
    public LinkedList(int v) {
        first = new IntNode(v);
    }
    public void addFirst(int v) {
        if (first == null) {
            first = new IntNode(v);
        } else {
            first = new IntNode(v, first);
        }
    }
    public int getFirst() {
        return first.value;
    }
    public int removeFirst() {
        int removedInt = first.value;
        first = first.next;
        return removedInt;
    }
}
