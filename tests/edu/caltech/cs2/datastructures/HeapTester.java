package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.helpers.Inspection;
import edu.caltech.cs2.helpers.Reflection;

import java.util.*;
import java.util.ArrayList;

import edu.caltech.cs2.interfaces.IPriorityQueue;
import edu.caltech.cs2.misc.IntegerComparator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class HeapTester {
    private static String STRING_SOURCE = "src/edu/caltech/cs2/datastructures/MinFourHeap.java";

    @Order(0)
    @DisplayName("Does not use or import disallowed classes")
    @Test
    @Tag("C")
    public void testForInvalidClasses() {
        List<String> regexps = List.of("java.util.(?!Iterator|function.Function)", "java.lang.reflect", "java.io");
        Inspection.assertNoImportsOf(STRING_SOURCE, regexps);
        Inspection.assertNoUsageOf(STRING_SOURCE, regexps);
    }
    @Test
    @Tag("C")
    public void testPublicInterface() {
        Reflection.assertPublicInterface(MinFourHeap.class, List.of(
                "enqueue",
                "dequeue",
                "iterator",
                "decreaseKey",
                "increaseKey",
                "peek",
                "size"
        ));
    }

    @Test
    @Tag("C")
    public void testDuplicateThrows() {
        MinFourHeap<Integer> heap = new MinFourHeap<>();
        heap.enqueue(new IPriorityQueue.PQElement(10, 10));
        assertThrows(IllegalArgumentException.class, () -> {
            heap.enqueue(new IPriorityQueue.PQElement(10, 10));
        });
    }

    @Test
    @Tag("C")
    public void testIdxTooLargeThrows() {
        Comparator<Integer> c = new IntegerComparator();
        MinFourHeap<Integer> heap = new MinFourHeap<>();
        heap.enqueue(new IPriorityQueue.PQElement<>(10, 10));
        assertThrows(IllegalArgumentException.class, () -> {
            heap.increaseKey(new IPriorityQueue.PQElement<>(11, 11));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            heap.decreaseKey(new IPriorityQueue.PQElement<>(11, 11));
        });
    }

    @Test
    @Tag("C")
    public void testEnqueue() {
        // create heap
        MinFourHeap<Integer> heap = new MinFourHeap<>();
        List<Integer> values = new ArrayList<>(Arrays.asList(9, -100, 19, 3, -2, 1, 7, -84, -4, 2, 70));

        // step by step look at what heap internal data array should look like
        List<List<Integer>> step_by_step = new ArrayList<>();
        step_by_step.add(Arrays.asList(9));
        step_by_step.add(Arrays.asList(-100, 9));
        step_by_step.add(Arrays.asList(-100, 9, 19));
        step_by_step.add(Arrays.asList(-100, 9, 19, 3));
        step_by_step.add(Arrays.asList(-100, 9, 19, 3, -2));
        step_by_step.add(Arrays.asList(-100, 1, 19, 3, -2, 9));
        step_by_step.add(Arrays.asList(-100, 1, 19, 3, -2, 9, 7));
        step_by_step.add(Arrays.asList(-100, -84, 19, 3, -2, 9, 7, 1));
        step_by_step.add(Arrays.asList(-100, -84, 19, 3, -2, 9, 7, 1, -4));
        step_by_step.add(Arrays.asList(-100, -84, 2, 3, -2, 9, 7, 1, -4, 19));
        step_by_step.add(Arrays.asList(-100, -84, 2, 3, -2, 9, 7, 1, -4, 19, 70));

        // enqueue values while examining internal state
        for (int i = 0; i < values.size(); i++) {
            assertTrue(heap.enqueue(new IPriorityQueue.PQElement<>(values.get(i), values.get(i))));
            assertEquals(i + 1, heap.size());

            IPriorityQueue.PQElement[] heap_data = Reflection.getFieldValue(MinFourHeap.class, "data", heap);
            for(int j = 0; j < heap.size(); j++) {
                assertEquals(step_by_step.get(i).toArray()[j], heap_data[j].data);
            }
        }
    }

    @Test
    @Tag("C")
    public void testDequeue() {
        Comparator<Integer> c = new IntegerComparator();
        MinFourHeap<Integer> heap = new MinFourHeap<>();
        PriorityQueue<Integer> reference = new PriorityQueue<>(c);
        List<Integer> values = new ArrayList<>(Arrays.asList(9, -100, 19, 3, -2, 1, 7, -84, -4, 2, 70));
        for (int i = 0; i < values.size(); i++) {
            assertTrue(heap.enqueue(new IPriorityQueue.PQElement<>(values.get(i), values.get(i))));
            reference.add(values.get(i));
        }
        for (int i = 0; i < reference.size(); i++) {
            assertEquals(reference.remove(), heap.dequeue().data);
            assertEquals(reference.size(), heap.size());
        }

    }

    @Tag("C")
    @ParameterizedTest(name = "Stress test enqueue and dequeue.")
    @CsvSource({
            "100, 3000", "42, 1000"
    })
    public void stressTestAddRemove(int seed, int size) {
        MinFourHeap<Integer> heap = new MinFourHeap<>();
        Comparator<Integer> c = new IntegerComparator();
        PriorityQueue<Integer> reference = new PriorityQueue<>(c);
        Random r = new Random(seed);
        for (int i = 0; i < size; i++) {
            int num = r.nextInt();
            while (reference.contains(num)) {
                num = r.nextInt();
            }
            reference.add(num);
            assertTrue(heap.enqueue(new IPriorityQueue.PQElement<>(num, num)));
            assertEquals(heap.size(), reference.size());
        }
        while (heap.size() != 0) {
            assertEquals(heap.dequeue().data, reference.remove());
        }
    }

    @Test
    @Tag("C")
    public void testIncreaseKey() {
        Comparator<Integer> c = new IntegerComparator();
        MinFourHeap<Integer> heap = new MinFourHeap<>();
        List<Integer> values = new ArrayList<>(Arrays.asList(9, -100, 19, 3, -2, 1, 7, -84, -4, 2, 70));
        for (int i = 0; i < values.size(); i++) {
            assertTrue(heap.enqueue(new IPriorityQueue.PQElement<>(values.get(i), values.get(i))));
        }
        Integer[] correctHeapData = {-100, -84, 2, 3, -2, 9, 7, 1, -4, 19, 70};
        IPriorityQueue.PQElement[] heap_data = Reflection.getFieldValue(MinFourHeap.class, "data", heap);
        for(int j = 0; j < heap.size(); j++) {
            assertEquals(correctHeapData[j], heap_data[j].data);
        }
        heap.increaseKey(new IPriorityQueue.PQElement<>(-100, 100));
        Integer[] correctHeapAfterIncrease = {-84, -4, 2, 3, -2, 9, 7, 1, 100, 19, 70};
        heap_data = Reflection.getFieldValue(MinFourHeap.class, "data", heap);
        for (int i = 0; i < heap.size(); i++) {
           assertEquals(correctHeapAfterIncrease[i], heap_data[i].priority);
        }
    }

    @Test
    @Tag("C")
    public void testDecreaseKey() {
        MinFourHeap<Integer> heap = new MinFourHeap<>();
        List<Integer> values = new ArrayList<>(Arrays.asList(9, -100, 19, 3, -2, 1, 7, -84, -4, 2, 70));
        for (int i = 0; i < values.size(); i++) {
            assertTrue(heap.enqueue(new IPriorityQueue.PQElement<>(values.get(i), values.get(i))));
        }
        Integer[] correctHeapData = {-100, -84, 2, 3, -2, 9, 7, 1, -4, 19, 70};
        IPriorityQueue.PQElement[] heap_data = Reflection.getFieldValue(MinFourHeap.class, "data", heap);
        for(int j = 0; j < heap.size(); j++) {
            assertEquals(correctHeapData[j], heap_data[j].data);
        }
        heap.decreaseKey(new IPriorityQueue.PQElement<>(7, -105));
        Integer[] correctHeapAfterDecrease = {-105, -100, 2, 3, -2, 9, -84, 1, -4, 19, 70};
        heap_data = Reflection.getFieldValue(MinFourHeap.class, "data", heap);
        for (int i = 0; i < heap.size(); i++) {
           assertEquals(correctHeapAfterDecrease[i], heap_data[i].priority);
        }
    }

    @Tag("C")
    @ParameterizedTest(name = "Stress test increase/decrease key")
    @CsvSource({
            "100,  3000, 1500", "42, 1000, 500"
    })
    public void stressTestIncreaseDecrease(int seed, int size, int numToReplace) {
        MinFourHeap<Integer> heap = new MinFourHeap<>();
        Comparator<Integer> c = new IntegerComparator();
        PriorityQueue<Integer> reference = new PriorityQueue<>(c);
        Set<Integer> removed = new TreeSet<>();
        Random r = new Random(seed);
        for (int i = 0; i < size; i++) {
            int num = r.nextInt();
            while (reference.contains(num)) {
                num = r.nextInt();
            }
            reference.add(num);
            heap.enqueue(new IPriorityQueue.PQElement<>(num, num));
            assertEquals(reference.size(), heap.size());
        }

        for (int j = 0; j < numToReplace; j++) {
            int newKey = r.nextInt();
            while (reference.contains(newKey) || removed.contains(newKey)) {
                newKey = r.nextInt();
            }
            IPriorityQueue.PQElement[] heap_data = Reflection.getFieldValue(MinFourHeap.class, "data", heap);
            int idx = r.nextInt(heap.size());
            Integer origKey = (Integer)heap_data[idx].data;
            while (removed.contains(origKey)) {
                idx = r.nextInt(heap.size());
                origKey = (Integer)heap_data[idx].data;
            }
            if (newKey < origKey) {
                heap.decreaseKey(new IPriorityQueue.PQElement(origKey, newKey));
            }
            else {
                heap.increaseKey(new IPriorityQueue.PQElement(origKey, newKey));
            }
            assertEquals(reference.size(), heap.size());
            removed.add(origKey);
            reference.remove(origKey);
            reference.add(newKey);
            assertEquals(reference.size(), heap.size());
        }

        while(!reference.isEmpty()) {
            Integer er = reference.remove();
            IPriorityQueue.PQElement mr = heap.dequeue();
            assertEquals(er, mr.priority);
        }

    }
}