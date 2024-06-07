package ru.yandex.practicum.kanban.model;

public class Node<E> {
    public E element;
    public Node<E> prev;
    public Node<E> next;

    public Node(Node<E> prev, E element, Node<E> next) {
        this.element = element;
        this.next = next;
        this.prev = prev;
    }
}