package edu.caltech.cs2.sorts;

import edu.caltech.cs2.datastructures.MinFourHeap;
import edu.caltech.cs2.interfaces.IPriorityQueue;

public class TopKSort {
    /**
     * Sorts the largest K elements in the array in descending order.
     * @param array - the array to be sorted; will be manipulated.
     * @param K - the number of values to sort
     * @param <E> - the type of values in the array
     */
    public static <E> void sort(IPriorityQueue.PQElement<E>[] array, int K) {
        if (K < 0) {
            throw new IllegalArgumentException("K cannot be negative!");
        }

        if (array.length == 0) {
            return;
        }

        if (K == 0) {
            for (int i = 0; i < array.length; i++) {
                array[i] = null;
            }
            return;
        }

        //enqueue first K elements of array
        MinFourHeap<E> sortHeap = new MinFourHeap<>();
        for (int i = 0; i < K && i < array.length; i++) {
            sortHeap.enqueue(array[i]);
        }

        //Check rest of array
        for (int arrIndex = K; arrIndex < array.length; arrIndex++) {
            if (sortHeap.peek().priority < array[arrIndex].priority) {
                sortHeap.dequeue();
                sortHeap.enqueue(array[arrIndex]);
            }
        }


        for (int i = 0; i < array.length; i++) {
            if (i < K && K-i-1 <array.length) {
                array[K - i - 1] = sortHeap.dequeue();
            }
            else if (array.length >= K ){
                array[i] = null;
            }
            else {
                array[array.length-i-1] = sortHeap.dequeue();
            }
        }


    }
}
