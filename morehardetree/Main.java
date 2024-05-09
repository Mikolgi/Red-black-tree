package morehardetree;

import java.util.Random;

public class Main {

    public static void main(String[] args) {
        RedBlackTree redBlackTree = new RedBlackTree();

        Random random = new Random();

        //  Создание массива из случайной последовательности 10000 целых чисел
        int[] randomArray = generateRandomArray(10000);

        long totalInsertOperations = 0;
        long totalSearchOperations = 0;
        long totalDeleteOperations = 0;
        long totalInsertTime = 0;
        long totalSearchTime = 0;
        long totalDeleteTime = 0;

        //  Вставка чисел в структуру и замер времени для каждого добавления
        for (int i = 0; i < randomArray.length; i++) {
            long startTime = System.nanoTime();
            redBlackTree.insertNode(randomArray[i]);
            long endTime = System.nanoTime();
            long insertTime = endTime - startTime;
            long insertOperations = redBlackTree.getInsertOperationsCount();
            redBlackTree.setInsertOperationsCount(0);
            totalInsertTime += insertTime;
            totalInsertOperations += insertOperations;
            System.out.println("Время вставки элемента " + randomArray[i] + " (в нс): " + insertTime);
            System.out.println("Количество операций вставки для элемента " + randomArray[i] + ": " + insertOperations);
        }

        //  Поиск 100 случайных элементов в структуре и замер времени для каждого поиска
        for (int i = 0; i < 100; i++) {
            int randomIndex = random.nextInt(randomArray.length);
            int keyToSearch = randomArray[randomIndex];
            long startTime = System.nanoTime();
            redBlackTree.searchNode(keyToSearch);
            long endTime = System.nanoTime();
            long searchTime = endTime - startTime;
            long searchOperations = redBlackTree.getSearchOperationsCount();
            redBlackTree.setSearchOperationsCount(0);
            totalSearchTime += searchTime;
            totalSearchOperations += searchOperations;
            System.out.println("Время поиска элемента " + keyToSearch + " (в нс): " + searchTime);
            System.out.println("Количество операций поиска для элемента " + keyToSearch + ": " + searchOperations);
        }

        //  Удаление 1000 случайных элементов из структуры и замер времени для каждого удаления
        for (int i = 0; i < 1000; i++) {
            int randomIndex = random.nextInt(randomArray.length);
            int keyToDelete = randomArray[randomIndex];
            long startTime = System.nanoTime();
            redBlackTree.deleteNode(keyToDelete);
            long endTime = System.nanoTime();
            long deleteTime = endTime - startTime;
            long deleteOperations = redBlackTree.getDeleteOperationsCount();
            redBlackTree.setDeleteOperationsCount(0);
            totalDeleteTime += deleteTime;
            totalDeleteOperations += deleteOperations;
            System.out.println("Время удаления элемента " + keyToDelete + " (в нс): " + deleteTime);
            System.out.println("Количество операций удаления для элемента " + keyToDelete + ": " + deleteOperations);
        }

        // Вывод общего количества операций для всех действий
        System.out.println("Общее количество операций вставки: " + totalInsertOperations);
        System.out.println("Общее количество операций поиска: " + totalSearchOperations);
        System.out.println("Общее количество операций удаления: " + totalDeleteOperations);

        // Вывод среднего количества операций и времени
        double averageInsertOperations = (double) totalInsertOperations / 10000;
        double averageSearchOperations = (double) totalSearchOperations / 100;
        double averageDeleteOperations = (double) totalDeleteOperations / 1000;
        double averageInsertTime = (double) totalInsertTime / 10000;
        double averageSearchTime = (double) totalSearchTime / 100;
        double averageDeleteTime = (double) totalDeleteTime / 1000;

        System.out.println("Среднее количество операций вставки: " + averageInsertOperations);
        System.out.println("Среднее количество операций поиска: " + averageSearchOperations);
        System.out.println("Среднее количество операций удаления: " + averageDeleteOperations);
        System.out.println("Среднее время вставки (в нс): " + averageInsertTime);
        System.out.println("Среднее время поиска (в нс): " + averageSearchTime);
        System.out.println("Среднее время удаления (в нс): " + averageDeleteTime);
    }

    private static int[] generateRandomArray(int size) {
        int[] array = new int[size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt();
        }
        return array;
    }
}



