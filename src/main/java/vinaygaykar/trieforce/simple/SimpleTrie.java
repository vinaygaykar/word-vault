package vinaygaykar.trieforce.simple;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BinaryOperator;

import vinaygaykar.Dictionary;


/**
 * SimpleTrie is an implementation of the Trie data structure for storing and searching words efficiently. It stores
 * words with an associated value of generic type and supports insertion of non-empty strings, and provides methods for
 * searching and checking for prefixes.
 * <p>
 * The SimpleTrie class provides methods for inserting words into the Trie, searching for a word in the Trie, and
 * searching for words starting with a prefix. The Trie is implemented using a Node class which contains a map of
 * character keys to child Nodes. Each Node also has a boolean flag indicating whether it marks the end of a word in the
 * Trie.
 * <p>
 * Example Usage:
 * <pre>
 *      final SimpleTrie<Integer> trie = new SimpleTrie<>();
 *      trie.insert("hello", 1);
 *      trie.insert("world", 2);
 *      trie.search("hello"); // returns true
 *      trie.search("world"); // returns true
 *      trie.search("foobar"); // returns false
 *      trie.startsWith("he"); // returns true
 *      trie.startsWith("w"); // returns true
 *      trie.startsWith("f"); // returns false
 *      trie.getValue("hello"); // return Optional.of(1);
 *      trie.getValue("foobar"); // return Optional.empty();
 * </pre>
 *
 * @param <V> Type of value associated with each word
 *
 * @author Vinay Gaykar
 */
public class SimpleTrie<V> implements Dictionary<V> {

	private final Node<V> root;

	private final BinaryOperator<V> mergeFunction;

	private long words;


	/**
	 * Creates an object of this class with a "replacing" merge function. If similar words are inserted but with
	 * different values then value the word last inserted will be stored.
	 */
	public SimpleTrie() {
		this((o, n) -> n);
	}

	/**
	 * Creates an object of this class with given merge function.
	 *
	 * @param mergeFunction This parameter will be used to resolve any conflicts that arise when two same words with
	 *                      different values are inserted.
	 */
	public SimpleTrie(final BinaryOperator<V> mergeFunction) {
		this.root = new Node<>();
		this.mergeFunction = mergeFunction;
		this.words = 0L;
	}

	@Override
	public void add(final String word, final V value) {
		if (word == null || word.isEmpty())
			throw new IllegalArgumentException("Cannot insert null or empty string");

		if (value == null)
			throw new IllegalArgumentException("Null value object for word association is not allowed");

		Node<V> current = root;
		for (int i = 0; i < word.length(); ++i) {
			final char ch = word.charAt(i);
			current = current.children.computeIfAbsent(ch, k -> new Node<>());
		}

		if (current.value != null) current.value = mergeFunction.apply(current.value, value);
		else current.value = value;

		words++;
	}

	@Override
	public Optional<V> getValue(final String word) {
		return find(word).map(node -> node.value);
	}

	@Override
	public List<String> hasPrefix(final String prefix, final int count) {
		final Optional<Node<V>> nodeOpt = find(prefix);
		if (!nodeOpt.isPresent())
			return Collections.emptyList();

		final Node<V> node = nodeOpt.get();
		final List<String> results = new ArrayList<>(count);
		traverse(prefix, count, node, results);

		return results;
	}

	private Optional<Node<V>> find(final String word) {
		if (word == null || word.isEmpty())
			return Optional.empty();

		Node<V> current = root;
		for (int i = 0; i < word.length(); ++i) {
			current = current.children.get(word.charAt(i));

			if (current == null)
				return Optional.empty();
		}

		return Optional.of(current);
	}

	private void traverse(final String prefix, final int count, final Node<V> node, final List<String> results) {
		if (results.size() == count) return;
		if (node.isTerminal()) results.add(prefix);

		for (final Map.Entry<Character, Node<V>> entry : node.children.entrySet())
			traverse(prefix + entry.getKey(), count, entry.getValue(), results);
	}

	@Override
	public long size() {
		return this.words;
	}


	/**
	 * The Node class represents a node in the Trie.
	 * <p>
	 * Each node contains a map of character keys to child Nodes, and a value object which when non-null indicates end
	 * of a word.
	 */
	private static class Node<V> {

		private final Map<Character, Node<V>> children;

		private V value;

		Node() {
			this.children = new HashMap<>();
			this.value = null;
		}

		/**
		 * Returns true if this node marks end of some word
		 *
		 * @return true if this is an end of a word
		 */
		public boolean isTerminal() {
			return this.value != null;
		}

	}

}
