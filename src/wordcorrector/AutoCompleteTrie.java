package wordcorrector;

import edu.caltech.cs2.datastructures.IterableString;
import edu.caltech.cs2.datastructures.LinkedDeque;
import edu.caltech.cs2.datastructures.TrieMap;
import edu.caltech.cs2.interfaces.IDeque;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.function.Function;

public class AutoCompleteTrie extends TrieMap<Character, IterableString, Integer> {

    /**
     * Initialize an AutoCompleteTrie and populate it using words from the dictFilename
     * @param collector - the function that collapses letters to keys
     * @param dictFilename - the filename containing the dictionary of words
     */
    public AutoCompleteTrie(Function<IDeque<Character>, IterableString> collector, String dictFilename) {
        super(collector);

        Scanner dict;
        try {
            dict = new Scanner(new File(dictFilename));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Could not find dictionary for AutoCompleteTrie");
        }

        Integer counter = 0;
        while(dict.hasNext()) {
            String nextWord = dict.next();
            this.put(new IterableString(nextWord), counter);
            counter++;
        }

        dict.close();
    }

    /**
     * Convert the given String to an IDeque of Characters
     */
    public static IDeque<Character> stringToIDeque(String term) {
        IDeque<Character> characters = new LinkedDeque<>();
        for (char c : term.toCharArray()) {
           characters.addBack(c);
        }
        return characters;
    }

    protected TrieNode<Character, Integer> followKey(IterableString key) {
        Iterator<Character> letters = key.iterator();
        TrieNode<Character, Integer> current = this.root;
        while (letters.hasNext()) {
            Character letter = letters.next();
            if (!current.pointers.containsKey(letter)) {
                return null;
            }
            current = current.pointers.get(letter);
        }
        return current;
    }

    protected void getKeys(TrieNode<Character, Integer> current, IDeque<Character> keySoFar, IDeque<IterableString> keys) {
        if (current.value != null) {
            keys.add(super.collector.apply(keySoFar));
        }

        for (Character letter : current.pointers.keySet()) {
            keySoFar.addBack(letter);
            getKeys(current.pointers.get(letter), keySoFar, keys);
            keySoFar.removeBack();
        }
    }

    /**
     * Retrieves keys that have the given key as a prefix
     */
    public IDeque<IterableString> autocomplete(String keyString) {
        TrieNode<Character, Integer> startingNode = followKey(new IterableString(keyString));
        if (startingNode == null) {
            return null;
        }
        IDeque<IterableString> suggestions = new LinkedDeque<>();
        getKeys(startingNode, stringToIDeque(keyString), suggestions);
        return suggestions;
    }
}
