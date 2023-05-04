package vinaygaykar.trieforce;


import java.util.Optional;

import vinaygaykar.Dictionary;


/**
 * An abstract class that implements the Dictionary interface and provides a basic implementation of the Trie data
 * structure.
 *
 * @param <V> the type of the values that the Trie stores.
 *
 * @author Vinay Gaykar
 * @see vinaygaykar.Dictionary
 */
public abstract class Trie<V> implements Dictionary<V> {

	/**
	 * Finds the node in the Trie that corresponds to the given word.
	 *
	 * @param word the word to search for in the Trie.
	 *
	 * @return an Optional containing the node that corresponds to the word, or an empty Optional if the word is not in
	 * the Trie.
	 */
	protected abstract Optional<? extends Node<V>> find(final String word);

	/**
	 * Returns the total number of nodes in the Trie.
	 *
	 * @return the total number of nodes in the Trie.
	 */
	protected abstract long getCountOfNodes();


	/**
	 * The Node class represents a node in the Trie.
	 * <p>
	 * Each node contains a map of character keys to child Nodes, and a value object which when non-null indicates end
	 * of a word.
	 */
	protected static abstract class Node<V> {

		public abstract V getValue();

		/**
		 * Returns true if this node marks end of some word
		 *
		 * @return true if this is an end of a word
		 */
		public abstract boolean isTerminal();

	}

}
