package vinaygaykar.trieforce.simple;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import vinaygaykar.trieforce.Trie;


/**
 * As the name suggests this is simplest and default implementation of a Trie data structure, where every character
 * of a key is represented as a tree node.
 * <p>
 * Consider the following words: `HELLO`, `HELP` &amp; `WORLD`, are represented as follows:
 * <pre>
 * root
 * ├── H
 * │   └── E
 * │       └── L
 * │           ├── L
 * │           │   └── O (end)
 * │           └── P (end)
 * └── W
 *     └── O
 *         └── R
 *             └── L
 *                 └── D (end)
 * </pre>
 * <p>
 * Example Usage:
 * <pre>
 *      final Dictionary&lt;Integer&gt; trie = new SimpleTrie&lt;&gt;();
 *      trie.put("HELLO", 1);
 *      trie.put("HELP", 2);
 *      trie.put("WORLD", 3);
 *      trie.get("HELLO"); // returns 1 as {@link Optional}
 *      trie.get("WORLD"); // returns 3 as {@link Optional}
 *      trie.get("WoRlD"); // returns {@link Optional#empty()}
 * </pre>
 *
 * @param <V> the type of the values that are stored
 *
 * @author Vinay Gaykar
 * @see vinaygaykar.Dictionary
 * @see vinaygaykar.trieforce.Trie
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
	public List<String> getKeysWithPrefix(final String prefix,
										  final Comparator<Character> comparator,
										  final int count) {
		validateKey(prefix);
		if (count < 1)
			throw new IllegalArgumentException("Count of values to return with prefix is not a positive number");

		final Optional<Node<V>> nodeOpt = find(prefix);
		if (!nodeOpt.isPresent())
			return Collections.emptyList();

		final Node<V> node = nodeOpt.get();
		final List<String> results = new ArrayList<>(count);
		traverse(prefix, count, node, comparator, results);

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

	private void traverse(final String prefix,
						  final int count,
						  final Node<V> node,
						  final Comparator<Character> keyComparator,
						  final List<String> results) {
		if (results.size() == count) return;
		if (node.isTerminal()) results.add(prefix);

		node.children.entrySet().stream()
				.sorted((e1, e2) -> keyComparator.compare(e1.getKey(), e2.getKey()))
				.forEachOrdered(entry -> {
					if (results.size() <= count)
						traverse(prefix + entry.getKey(), count, entry.getValue(), keyComparator, results);
				});
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
			this.children = new HashMap<>();
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
