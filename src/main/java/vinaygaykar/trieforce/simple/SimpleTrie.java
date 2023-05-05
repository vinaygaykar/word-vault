package vinaygaykar.trieforce.simple;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import vinaygaykar.trieforce.Trie;


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
public class SimpleTrie<V> extends Trie<V> {

	private final Node<V> root;

	private long words;

	private long nodes;


	public SimpleTrie() {
		this.root = new Node<>();
		this.words = 0;
		this.nodes = 1; // root is always there
	}

	@Override
	public Optional<V> put(final String key, final V value) {
		validateKey(key);
		validateValue(value);

		Node<V> current = root;
		for (int i = 0; i < key.length(); ++i) {
			current = current.children.computeIfAbsent(
					key.charAt(i),
					k -> {
						nodes++;
						return new Node<>();
					}
			);
		}

		words++;
		if (current.value == null) {
			current.value = value;
			return Optional.empty();
		} else return Optional.of(current.value);
	}

	@Override
	public Optional<V> get(final String key) {
		validateKey(key);

		return find(key).map(node -> node.value);
	}

	@Override
	public List<String> getKeysWithPrefix(final String prefix, final int count) {
		validateKey(prefix);
		if (count < 1)
			throw new IllegalArgumentException("Count of values to return with prefix is not a positive number");

		final Optional<Node<V>> nodeOpt = find(prefix);
		if (!nodeOpt.isPresent())
			return Collections.emptyList();

		final Node<V> node = nodeOpt.get();
		final List<String> results = new ArrayList<>(count);
		traverse(prefix, count, node, results);

		return results;
	}

	@Override
	protected Optional<Node<V>> find(final String key) {
		Node<V> current = root;
		for (int i = 0; i < key.length(); ++i) {
			current = current.children.get(key.charAt(i));

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

	@Override
	protected long getCountOfNodes() {
		return this.nodes;
	}

	@Override
	public Optional<V> remove(final String key) {
		validateKey(key);

		return Optional.ofNullable(remove(root, key, 0));
	}

	private V remove(final Node<V> current, final String key, final int index) {
		if (index == key.length()) {
			if (!current.isTerminal()) return null;

			final V val = current.value;
			current.value = null;
			words--;
			return val;
		}

		final char ch = key.charAt(index);
		final Node<V> node = current.children.get(ch);

		if (node == null) return null;

		final V val = remove(node, key, index + 1);

		// check if the `node` should be deleted
		if (!node.isTerminal() && node.children.isEmpty()) {
			current.children.remove(ch);
			nodes--;
		}

		return val;
	}

	protected static class Node<V> extends Trie.Node<V> {

		private final Map<Character, Node<V>> children;

		private V value;

		private Node() {
			this.children = new TreeMap<>();
			this.value = null;
		}

		@Override
		public V getValue() {
			return this.value;
		}

		@Override
		public boolean isTerminal() {
			return this.value != null;
		}

	}

}
