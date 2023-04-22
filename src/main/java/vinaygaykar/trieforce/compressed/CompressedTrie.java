package vinaygaykar.trieforce.compressed;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.BinaryOperator;

import vinaygaykar.trieforce.Trie;


public class CompressedTrie<V> extends Trie<V> {

	private final Node<V> root;

	private final BinaryOperator<V> mergeFunction;

	private long words;

	private long nodes;


	/**
	 * Creates an object of this class with a "replacing" merge function. If similar words are inserted but with
	 * different values then value the word last inserted will be stored.
	 */
	public CompressedTrie() {
		this((o, n) -> n);
	}

	/**
	 * Creates an object of this class with given merge function.
	 *
	 * @param mergeFunction This parameter will be used to resolve any conflicts that arise when two same words with
	 *                      different values are inserted.
	 */
	public CompressedTrie(final BinaryOperator<V> mergeFunction) {
		this.root = new Node<>();
		this.mergeFunction = mergeFunction;
		this.words = 0L;
		this.nodes = 1L; // root is always there
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
			final Node<V> next = current.children.get(ch);

			if (next == null) {
				final Node<V> node = new Node<>();
				nodes++;
				node.prefix.append(word.substring(i + 1));

				current.children.put(ch, node);
				current = node;
				break;
			} else if (next.prefix.length() == 0) {
				current = next;
				continue;
			}

			final int len = (i < word.length() - 1)
							? getCommonPrefixLength(next.prefix, word.substring(i + 1))
							: 0;

			if (len == 0) {
				splitNode(next, len);
				current = next;
			} else if (next.prefix.length() == len) {
				i += len;
				current = next;
			} else if (next.prefix.length() > len) {
				splitNode(next, len);
				i += len;
				current = next;
			}
		}

		if (current.value != null) current.value = mergeFunction.apply(current.value, value);
		else current.value = value;

		words++;
	}

	private void splitNode(final Node<V> node, final int point) {
		if (node.prefix.length() < point)
			throw new IllegalStateException("Can not split a compressed node at a point which does not exists");

		final char ch = node.prefix.charAt(point);
		final Node<V> child = new Node<>();
		nodes++;

		child.prefix.append(node.prefix.substring(point + 1));
		node.prefix.delete(point, node.prefix.length());

		child.children.putAll(node.children);
		node.children.clear();

		child.value = node.value;
		node.value = null;

		node.children.put(ch, child);
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
		traverse(prefix + node.prefix, count, node, results);

		return results;
	}

	@Override
	public Optional<Node<V>> find(final String word) {
		if (word == null || word.isEmpty())
			return Optional.empty();

		Node<V> current = root;
		for (int i = 0; i < word.length(); ++i) {
			final char ch = word.charAt(i);

			if (!current.children.containsKey(ch))
				return Optional.empty();

			final Node<V> next = current.children.get(ch);
			final int len = getCommonPrefixLength(next.prefix, word.substring(i + 1));
			if (i != word.length() - 1 && len < next.prefix.length())
				return Optional.empty();

			i += len;
			current = next;
		}

		return Optional.of(current);
	}

	private int getCommonPrefixLength(final StringBuilder a, final String b) {
		int aptr = 0;
		int bptr = 0;
		while (aptr < a.length() && bptr < b.length() && a.charAt(aptr) == b.charAt(bptr)) {
			aptr++;
			bptr++;
		}

		return aptr;
	}

	private void traverse(final String prefix, final int count, final Node<V> node, final List<String> results) {
		if (results.size() >= count) return;
		if (node.isTerminal()) results.add(prefix);

		for (final Map.Entry<Character, Node<V>> entry : node.children.entrySet())
			traverse(prefix + entry.getKey() + entry.getValue().prefix, count, entry.getValue(), results);
	}

	@Override
	public long size() {
		return this.words;
	}


	@Override
	public long getCountOfNodes() {
		return this.nodes;
	}


	protected static class Node<V> extends Trie.Node<V> {

		private final Map<Character, Node<V>> children;

		private final StringBuilder prefix;

		private V value;


		private Node() {
			this.children = new TreeMap<>();
			this.value = null;
			this.prefix = new StringBuilder();
		}

		public StringBuilder getPrefix() {
			return prefix;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public boolean isTerminal() {
			return this.value != null;
		}

	}

}
