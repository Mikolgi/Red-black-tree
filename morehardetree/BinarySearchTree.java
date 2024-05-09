package morehardetree;

public interface BinarySearchTree extends BinaryTree {

    Node searchNode(int key);

    void insertNode(int key);

    void deleteNode(int key);
}