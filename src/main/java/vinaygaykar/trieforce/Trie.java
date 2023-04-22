package vinaygaykar.trieforce;


import java.util.Optional;

import vinaygaykar.Dictionary;


public abstract class Trie<V> implements Dictionary<V> {

	public abstract Optional<? extends Node<V>> find(final String word);

	public abstract long getCountOfNodes();


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
