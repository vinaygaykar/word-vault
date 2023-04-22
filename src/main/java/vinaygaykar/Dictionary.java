package vinaygaykar;

import java.util.List;
import java.util.Optional;


/**
 * A Dictionary interface to store key-value pairs for each word.
 *
 * @param <V> the type of the value object associated with each word.
 * @author Vinay Gaykar
 */
public interface Dictionary<V> {

    /**
     * Inserts a word with an associated value into the dictionary.
     *
     * @param word  the word to be inserted.
     * @param value the associated value object for the word.
     * @throws IllegalArgumentException if the word is null or empty.
     */
    void add(final String word, final V value);

    /**
     * Retrieves the value object associated with a word, if present in the dictionary.
     *
     * @param word the word to retrieve the value object for.
     * @return an Optional object containing the associated value object, or an empty Optional if the word is not present.
     */
    Optional<V> getValue(final String word);

    /**
     * Searches the dictionary for words that start with the given prefix.
     *
     * @param prefix the prefix to search for.
     * @param count  the maximum number of words to return.
     * @return a list of words that start with the given prefix, up to a maximum of count.
     */
    List<String> hasPrefix(final String prefix, final int count);

    /**
     * Returns the number of words in the dictionary.
     *
     * @return the number of words in the dictionary.
     */
    long size();

}
