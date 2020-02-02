package edu.caltech.cs2.sorts;

import edu.caltech.cs2.helpers.Inspection;
import edu.caltech.cs2.interfaces.IPriorityQueue;
import edu.caltech.cs2.misc.IntegerComparator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SortTester {
    private static String STRING_SOURCE = "src/edu/caltech/cs2/sorts/TopKSort.java";

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
    public void testEmptyArray() {
        Comparator<Integer> c = new IntegerComparator();
        IPriorityQueue.PQElement[] array = new IPriorityQueue.PQElement[0];
        TopKSort.sort(array, 1);
        assertArrayEquals(new Integer[0], array);
    }

    @Test
    @Tag("C")
    public void testNegativeK() {
        Comparator<Integer> c = new IntegerComparator();
        IPriorityQueue.PQElement[] array = new IPriorityQueue.PQElement[0];
        assertThrows(IllegalArgumentException.class, () -> {
            TopKSort.sort(array, -1);
        });
    }

    @Test
    @Tag("C")
    public void testZeroK() {
        Comparator<Integer> c = new IntegerComparator();
        IPriorityQueue.PQElement[] array = {
                new IPriorityQueue.PQElement(1, 1),
                new IPriorityQueue.PQElement(2, 2),
                new IPriorityQueue.PQElement(3, 3),
                new IPriorityQueue.PQElement(4, 4),
                new IPriorityQueue.PQElement(5, 5)
        };
        TopKSort.sort(array, 0);
        Integer[] correct = new Integer[5];
        assertArrayEquals(correct, array);
    }

    @Tag("C")
    @ParameterizedTest(name = "Stress test TopKSort: size: {1}, k: {2}")
    @CsvSource({
            "42, 3000, 2000", "15, 5000, 1235", "20, 1000, 50"
    })
    public void stressTest(int seed, int size, int k) {
        Comparator<IPriorityQueue.PQElement<Integer>> c = (x, y) -> Integer.compare(x.priority, y.priority);
        Random r = new Random(seed);
        Integer[] intarray = r.ints(size).boxed().toArray(Integer[]::new);
        IPriorityQueue.PQElement[] array = new IPriorityQueue.PQElement[intarray.length];
        for (int i = 0; i < intarray.length; i++) {
            array[i] = new IPriorityQueue.PQElement(intarray[i], intarray[i]);
        }
        IPriorityQueue.PQElement<Integer>[] sortedArray = array.clone();
        Arrays.sort(sortedArray, c);
        IPriorityQueue.PQElement[] correct = new IPriorityQueue.PQElement[size];
        for (int i = 0; i < correct.length; i++) {
            if (i < k) {
                correct[i] = sortedArray[sortedArray.length - i - 1];
            }
            else {
                correct[i] = null;
            }
        }
        TopKSort.sort(array, k);
        assertArrayEquals(correct, array);
    }
}
