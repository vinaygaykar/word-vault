package vinaygaykar.trieforce;


import java.util.Optional;

import vinaygaykar.Dictionary;


/**
 * An abstract class that implements the {@link java.util.Dictionary} interface and provides a basic implementation
 * of the Trie data structure.
 * <p>
 * A Trie (pronounced "try") is a tree-based data structure used for efficiently storing and searching for strings,
 * also known as a Prefix Tree, because each node in the tree represents a prefix of one or more strings.
 * The key advantage of a Trie is that it allows for very fast lookups of strings, achieved by storing the
 * strings in a tree structure.
 * <p>
 * This class acts as a guideline for all the child classes on which APIs to expose and nothing else.
 * It is encouraged to not rely on this class and instead use {@link java.util.Dictionary} to represent any {@link Trie}
 * implementations.
 *
 * @param <V> the type of the values that are stored
 *
 * @author Vinay Gaykar
 * @see Dictionary
 */
public abstract class Trie<V> implements Dictionary<V> {

	/**
	 * Finds the node in the Trie that corresponds to the given key.
	 * <p>
	 * Returns the node if the key is present or else {@link Optional#empty()}.
	 *
	 * @param key the key to search for
	 *
	 * @return an Optional containing the node that corresponds to the key, or an empty Optional if the key is not in
	 * the Trie.
	 */
	protected abstract Optional<? extends Node<V>> find(final String key);

	/**
	 * Returns the total number of nodes in the Trie.
	 *
	 * @return the total number of nodes in the Trie.
	 */
	protected abstract long getCountOfNodes();


	/**
	 * The Node class represents a node in the Trie.
	 * <p>
	 * Each node contains a map of character keys to child Nodes, and a value object which when non-null indicates the
	 * end of a word.
	 */
	protected static abstract class Node<V> {

		public abstract V getValue();

		/**
		 * @return true if this is an end of a key
		 */
		public abstract boolean isTerminal();

	}

}
