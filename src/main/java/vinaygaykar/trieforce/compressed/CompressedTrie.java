package vinaygaykar.trieforce.compressed;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import vinaygaykar.trieforce.Trie;


/**
 * This is a space optimised version of a {@link Trie}.
 * <p>
 * Unlike {@link vinaygaykar.trieforce.simple.SimpleTrie} where every character of a key is a node, in this
 * implementation, if a node has single child, then the child node is merged with parent node to save up on extra
 * pointers and nodes.
 * <p>
 * Consider the following words: `HELLO`, `HELP` & `WORLD`, these are represented as follows:
 * <pre>
 * root
 * ├── HEL
 * │   ├── LO
 * │   └── P
 * └── WORLD
 * </pre>
 * <p>
 * Example Usage:
 * <pre>
 * final Dictionary<Integer> trie = new SimpleTrie<>();
 * trie.put("HELLO", 1);
 * trie.put("HELP", 2);
 * trie.put("WORLD", 3);
 * trie.get("HELLO"); // returns 1 as {@link Optional}
 * trie.get("WORLD"); // returns 3 as {@link Optional}
 * trie.get("WoRlD"); // returns {@link Optional#empty()}
 * </pre>
 * <p>
 * Whether this implementation is beneficial to the user or how much space is saved compared to the other
 * implementation i.e. {@link vinaygaykar.trieforce.simple.SimpleTrie} really depends upon the data to store.
 * If the data is made up of too many common characters, then this implementation can prove beneficial as exemplified
 * by the use of the words `HELLO` & `HELP` above where both have common prefix substring, the worse case that can
 * happen is every character is has its own node and the final tree created is similar to
 * {@link vinaygaykar.trieforce.simple.SimpleTrie}.
 * Consider the following tree where every character is split into a node: Words are `ABC`, `ABD`, `AE`, `FG` & `FH`.
 * <pre>
 * root
 * ├── A
 * │   ├── B
 * │   │   ├── C
 * │   │   └── D
 * │   └── E
 * └── F
 *     ├── G
 *     └── H
 * </pre>
 *
 * @param <V> the type of the values that are stored
 *
 * @author Vinay Gaykar
 * @see vinaygaykar.Dictionary
 * @see vinaygaykar.trieforce.Trie
 * @see vinaygaykar.trieforce.simple.SimpleTrie
 */
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
		traverse(prefix + node.prefix, count, node, comparator, results);

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

	private void traverse(final String prefix,
						  final int count,
						  final Node<V> node,
						  final Comparator<Character> keyComparator,
						  final List<String> results) {
		if (results.size() >= count) return;
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

		return Optional.ofNullable(remove(key, root, 0));
	}

	private V remove(final String key, final Node<V> current, final int pos) {
		if (pos == key.length()) {
			if (!current.isTerminal()) return null;

			final V val = current.value;
			current.value = null;
			words--;

			mergeNode(current);
			return val;
		}

		final char ch = key.charAt(pos);
		final Node<V> node = current.children.get(ch);

		if (node == null) return null;

		final int newPos = pos + getCommonPrefixLength(node.prefix, key.substring(pos + 1)) + 1;
		final V val = remove(key, node, newPos);

		// check if the `node` should be deleted
		if (!node.isTerminal() && node.children.isEmpty()) {
			current.children.remove(ch);
			nodes--;
		}

		if (current != root) mergeNode(current);
		return val;
	}

	private void mergeNode(final Node<V> current) {
		if (current.children.size() == 1 && current != root) {
			final Map.Entry<Character, Node<V>> entry = current.children.entrySet()
					.stream()
					.findFirst()
					.get();

			current.prefix.append(entry.getKey()).append(entry.getValue().prefix);
			current.children.clear();
			current.value = entry.getValue().value;
			nodes--;
		}
	}


	protected static class Node<V> extends Trie.Node<V> {

		private final Map<Character, Node<V>> children;

		private final StringBuilder prefix;

		private V value;


		private Node() {
			this.children = new HashMap<>();
			this.value = null;
			this.prefix = new StringBuilder();
		}

		public StringBuilder getPrefix() {
			return prefix;
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
