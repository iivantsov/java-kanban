public class Node<E> {
    E element;
    Node<E> prev;
    Node<E> next;

    public Node(Node<E> prev, E element, Node<E> next) {
        this.element = element;
        this.next = next;
        this.prev = prev;
    }
}