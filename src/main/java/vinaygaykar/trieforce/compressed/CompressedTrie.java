package vinaygaykar.trieforce.compressed;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import vinaygaykar.trieforce.Trie;


public class CompressedTrie<V> extends Trie<V> {

	private final Node<V> root;

	private long words;

	private long nodes;


	public CompressedTrie() {
		this.root = new Node<>();
		this.words = 0L;
		this.nodes = 1L; // root is always there
	}

	@Override
	public Optional<V> put(final String key, final V value) {
		validateKey(key);
		validateValue(value);

		Node<V> current = root;
		for (int i = 0; i < key.length(); ++i) {
			final char ch = key.charAt(i);
			final Node<V> next = current.children.get(ch);

			if (next == null) {
				final Node<V> node = new Node<>();
				nodes++;
				node.prefix.append(key.substring(i + 1));

				current.children.put(ch, node);
				current = node;
				break;
			} else if (next.prefix.length() == 0) {
				current = next;
				continue;
			}

			final int len = (i < key.length() - 1)
							? getCommonPrefixLength(next.prefix, key.substring(i + 1))
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

		words++;
		if (current.value == null) {
			current.value = value;
			return Optional.empty();
		} else return Optional.of(current.value);
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
		traverse(prefix + node.prefix, count, node, results);

		return results;
	}

	@Override
	protected Optional<Node<V>> find(final String key) {
		if (key == null || key.isEmpty())
			return Optional.empty();

		Node<V> current = root;
		for (int i = 0; i < key.length(); ++i) {
			final char ch = key.charAt(i);

			if (!current.children.containsKey(ch))
				return Optional.empty();

			final Node<V> next = current.children.get(ch);
			final int len = getCommonPrefixLength(next.prefix, key.substring(i + 1));
			if (i != key.length() - 1 && len < next.prefix.length())
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
	protected long getCountOfNodes() {
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
