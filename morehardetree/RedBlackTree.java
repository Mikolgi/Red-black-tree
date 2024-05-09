package morehardetree;

public class RedBlackTree extends BaseBinaryTree implements BinarySearchTree {

    static final boolean RED = false;
    static final boolean BLACK = true;
    private long insertOperationsCount; // Счетчик операций вставки
    private long searchOperationsCount; // Счетчик операций поиска
    private long deleteOperationsCount; // Счетчик операций удаления

    @Override
    public Node searchNode(int key) {
        Node node = root;
        while (node != null) {
            if (key == node.data) {
                // Увеличиваем счетчик операций поиска
                searchOperationsCount++;
                return node;
            } else if (key < node.data) {
                node = node.left;
            } else {
                node = node.right;
            }
            // Увеличиваем счетчик операций поиска
            searchOperationsCount++;
        }
        return null;
    }

    // -- Вставка ----------------------------------------------------------------------------------

    @Override
    public void insertNode(int key) {
        Node node = root;
        Node parent = null;

        // Обход дерева влево или вправо в зависимости от ключа
        while (node != null) {
            parent = node;
            if (key < node.data) {
                node = node.left;
            } else if (key > node.data) {
                node = node.right;
            } else {
                throw new IllegalArgumentException("Дерево уже содержит узел с ключом " + key);
            }
            // Увеличиваем счетчик операций вставки
            insertOperationsCount++;
        }

        // Вставка нового узла
        Node newNode = new Node(key);
        newNode.color = RED;
        if (parent == null) {
            root = newNode;
        } else if (key < parent.data) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }
        newNode.parent = parent;

        fixRedBlackPropertiesAfterInsert(newNode);
    }

    @SuppressWarnings("squid:S125") // Игнорировать жалобы SonarCloud на закомментированную строку кода 70.
    private void fixRedBlackPropertiesAfterInsert(Node node) {
        Node parent = node.parent;

        // Случай 1: Родитель равен null, мы достигли корня, конец рекурсии
        if (parent == null) {
            // Раскомментируйте следующую строку, если хотите обеспечить черные корни (правило 2):
            // node.color = BLACK;
            return;
        }

        // Родитель черный --> ничего не делаем
        if (parent.color == BLACK) {
            return;
        }

        // Отсюда и далее родитель красный
        Node grandparent = parent.parent;

        // Случай 2:
        // Отсутствие дедушки означает, что родитель - корень. Если мы обеспечим черные корни
        // (правило 2), то дедушка никогда не будет null, и следующий блок if-then можно будет
        // удалить.
        if (grandparent == null) {
            // Поскольку этот метод вызывается только для красных узлов (либо только что вставленных,
            // либо рекурсивно для красных дедушек), все, что нам нужно сделать, это перекрасить корень в черный.
            parent.color = BLACK;
            return;
        }

        // Получаем дядю (может быть null/nil, в таком случае его цвет - черный)
        Node uncle = getUncle(parent);

        // Случай 3: Дядя красный -> перекрашиваем родителя, дедушку и дядю
        if (uncle != null && uncle.color == RED) {
            parent.color = BLACK;
            grandparent.color = RED;
            uncle.color = BLACK;

            // Рекурсивный вызов для дедушки, который теперь красный.
            // Он может быть корнем или иметь красного родителя, в этом случае нам нужно исправить еще...
            fixRedBlackPropertiesAfterInsert(grandparent);
        }


        // Родитель - левый потомок дедушки
        else if (parent == grandparent.left) {
            // Случай 4a: Дядя черный, а узел - "внутренний" левый->правый потомок дедушки
            if (node == parent.right) {
                rotateLeft(parent);

                // Пусть "родитель" указывает на новый корневой узел повернутого поддерева.
                // Он будет перекрашен на следующем шаге, к которому мы перейдем.
                parent = node;
            }

            // Случай 5a: Дядя черный, а узел - "внешний" левый->левый потомок дедушки
            rotateRight(grandparent);

            // Перекрашиваем исходного родителя и дедушку
            parent.color = BLACK;
            grandparent.color = RED;
        }

        // Родитель - правый потомок дедушки
        else {
            // Случай 4b: Дядя черный, а узел - "внутренний" правый->левый потомок дедушки
            if (node == parent.left) {
                rotateRight(parent);

                // Пусть "родитель" указывает на новый корневой узел повернутого поддерева.
                // Он будет перекрашен на следующем шаге, к которому мы перейдем.
                parent = node;
            }

            // Случай 5b: Дядя черный, а узел - "внешний" правый->правый потомок дедушки
            rotateLeft(grandparent);

            // Перекрашиваем исходного родителя и дедушку
            parent.color = BLACK;
            grandparent.color = RED;
        }
    }

    private Node getUncle(Node parent) {
        Node grandparent = parent.parent;
        if (grandparent.left == parent) {
            return grandparent.right;
        } else if (grandparent.right == parent) {
            return grandparent.left;
        } else {
            throw new IllegalStateException("Родитель не является потомком своего дедушки");
        }
    }

    // -- Удаление -----------------------------------------------------------------------------------

    @SuppressWarnings("squid:S2259") // SonarCloud выдает неверное предупреждение о возможном NPE
    @Override
    public void deleteNode(int key) {
        Node node = root;

        // Находим узел для удаления
        while (node != null && node.data != key) {
            // Обход дерева влево или вправо в зависимости от ключа
            if (key < node.data) {
                node = node.left;
            } else {
                node = node.right;
            }
            // Увеличиваем счетчик операций удаления
            deleteOperationsCount++;
        }

        // Узел не найден?
        if (node == null) {
            return;
        }

        // На этом этапе "узел" - узел для удаления

        // В этой переменной мы будем хранить узел, с которого начнем исправлять свойства R-B
        // после удаления узла.
        Node movedUpNode;
        boolean deletedNodeColor;

        // У узла ноль или один потомок
        if (node.left == null || node.right == null) {
            movedUpNode = deleteNodeWithZeroOrOneChild(node);
            deletedNodeColor = node.color;
        }

        // У узла два потомка
        else {
            // Находим минимальный узел правого поддерева ("последователь следующего в порядке" текущего узла)
            Node inOrderSuccessor = findMinimum(node.right);

            // Копируем данные последователя в текущий узел (сохраняем его цвет)
            node.data = inOrderSuccessor.data;

            // Удаляем последователя так же, как бы мы удалили узел с 0 или 1 потомком
            movedUpNode = deleteNodeWithZeroOrOneChild(inOrderSuccessor);
            deletedNodeColor = inOrderSuccessor.color;
        }

        if (deletedNodeColor == BLACK) {
            fixRedBlackPropertiesAfterDelete(movedUpNode);

            // Удаляем временный узел NIL
            if (movedUpNode.getClass() == NilNode.class) {
                replaceParentsChild(movedUpNode.parent, movedUpNode, null);
            }
        }
    }

    private Node deleteNodeWithZeroOrOneChild(Node node) {
        // У узла есть ТОЛЬКО левый потомок --> заменяем на его левого потомка
        if (node.left != null) {
            replaceParentsChild(node.parent, node, node.left);
            return node.left; // поднятый узел
        }

        // У узла есть ТОЛЬКО правый потомок --> заменяем на его правого потомка
        else if (node.right != null) {
            replaceParentsChild(node.parent, node, node.right);
            return node.right; // поднятый узел
        }

        // У узла нет потомков -->
        // * узел красный --> просто удаляем его
        // * узел черный --> заменяем его временным узлом NIL (нужно для исправления правил R-B)
        else {
            Node newChild = node.color == BLACK ? new NilNode() : null;
            replaceParentsChild(node.parent, node, newChild);
            return newChild;
        }
    }

    private Node findMinimum(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    @SuppressWarnings("squid:S125") // Игнорировать жалобы SonarCloud на закомментированную строку кода 256.
    private void fixRedBlackPropertiesAfterDelete(Node node) {
        // Случай 1: Рассматриваемый узел - корень, конец рекурсии
        if (node == root) {
            node.color = BLACK;
            return;
        }

        Node sibling = getSibling(node);

        // Случай 2: Красный "брат"
        if (sibling.color == RED) {
            handleRedSibling(node, sibling);
            sibling = getSibling(node); // Получаем нового "брата" для перехода к случаям 3-6
        }

        // Случаи 3+4: Черный "брат" с двумя черными потомками
        if (isBlack(sibling.left) && isBlack(sibling.right)) {
            sibling.color = RED;

            // Случай 3: Черный "брат" с двумя черными потомками + красный родитель
            if (node.parent.color == RED) {
                node.parent.color = BLACK;
            }

            // Случай 4: Черный "брат" с двумя черными потомками + черный родитель
            else {
                fixRedBlackPropertiesAfterDelete(node.parent);
            }
        }

        // Случаи 5+6: Черный "брат" с хотя бы одним красным потомком
        else {
            handleBlackSiblingWithAtLeastOneRedChild(node, sibling);
        }
    }

    private void handleRedSibling(Node node, Node sibling) {
        // Перекрашиваем
        sibling.color = BLACK;
        node.parent.color = RED;

        //  вращаем
        if (node == node.parent.left) {
            rotateLeft(node.parent);
        } else {
            rotateRight(node.parent);
        }
    }

    private void handleBlackSiblingWithAtLeastOneRedChild(Node node, Node sibling) {
        boolean nodeIsLeftChild = node == node.parent.left;

        // Случай 5: Черный "брат" с хотя бы одним красным потомком + "внешний племянник" черный
        // --> Перекрашиваем "брата" и его потомка и вращаем вокруг "брата"
        if (nodeIsLeftChild && isBlack(sibling.right)) {
            sibling.left.color = BLACK;
            sibling.color = RED;
            rotateRight(sibling);
            sibling = node.parent.right;
        } else if (!nodeIsLeftChild && isBlack(sibling.left)) {
            sibling.right.color = BLACK;
            sibling.color = RED;
            rotateLeft(sibling);
            sibling = node.parent.left;
        }

        // Переход к случаю 6...

        // Случай 6: Черный брат с хотя бы одним красным потомком + "внешний племянник" красный
        // --> Перекрашиваем "брата" + родителя + потомка "брата" и вращаем вокруг родителя
        sibling.color = node.parent.color;
        node.parent.color = BLACK;
        if (nodeIsLeftChild) {
            sibling.right.color = BLACK;
            rotateLeft(node.parent);
        } else {
            sibling.left.color = BLACK;
            rotateRight(node.parent);
        }
    }

    private Node getSibling(Node node) {
        Node parent = node.parent;
        if (node == parent.left) {
            return parent.right;
        } else if (node == parent.right) {
            return parent.left;
        } else {
            throw new IllegalStateException("Родитель не является потомком своего дедушки");
        }
    }

    private boolean isBlack(Node node) {
        return node == null || node.color == BLACK;
    }

    private static class NilNode extends Node {
        private NilNode() {
            super(0);
            this.color = BLACK;
        }
    }

    // -- Помощники для вставки и удаления ---------------------------------------------------------

    private void rotateRight(Node node) {
        Node parent = node.parent;
        Node leftChild = node.left;

        node.left = leftChild.right;
        if (leftChild.right != null) {
            leftChild.right.parent = node;
        }

        leftChild.right = node;
        node.parent = leftChild;

        replaceParentsChild(parent, node, leftChild);
    }

    private void rotateLeft(Node node) {
        Node parent = node.parent;
        Node rightChild = node.right;

        node.right = rightChild.left;
        if (rightChild.left != null) {
            rightChild.left.parent = node;
        }

        rightChild.left = node;
        node.parent = rightChild;

        replaceParentsChild(parent, node, rightChild);
    }

    private void replaceParentsChild(Node parent, Node oldChild, Node newChild) {
        if (parent == null) {
            root = newChild;
        } else if (parent.left == oldChild) {
            parent.left = newChild;
        } else if (parent.right == oldChild) {
            parent.right = newChild;
        } else {
            throw new IllegalStateException("Узел не является потомком своего родителя");
        }

        if (newChild != null) {
            newChild.parent = parent;
        }
    }
    public long getInsertOperationsCount() {
        return insertOperationsCount;
    }

    public long getSearchOperationsCount() {
        return searchOperationsCount;
    }

    public long getDeleteOperationsCount() {
        return deleteOperationsCount;
    }

    public void setDeleteOperationsCount(long deleteOperationsCount) {
        this.deleteOperationsCount = deleteOperationsCount;
    }
    public void setInsertOperationsCount(long insertOperationsCount){
        this.insertOperationsCount = insertOperationsCount;
    }

    public void setSearchOperationsCount(long searchOperationsCount) {
        this.searchOperationsCount = searchOperationsCount;
    }
    // -- Для toString() -----------------------------------------------------------------------------

    @Override
    protected void appendNodeToString(Node node, StringBuilder builder) {
        builder.append(node.data).append(node.color == RED ? "[R]" : "[B]");
    }
}