package vinaygaykar.trieforce.compressed;


import java.util.Comparator;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vinaygaykar.trieforce.simple.SimpleTrie;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class CompressedTrieTest {

	@DisplayName("Adding Null or Empty word or value is not allowed")
	@Test
	void testAddAndGet_NullAndEmpty() {
		// given
		final CompressedTrie<Integer> trie = new CompressedTrie<>();

		// then
		// Add - null & empty word
		assertThrows(NullPointerException.class, () -> trie.put(null, null));
		assertThrows(IllegalArgumentException.class, () -> trie.put("", null));
		// Add - null value
		assertThrows(NullPointerException.class, () -> trie.put("something", null));

		// Get - null & empty word
		assertThrows(NullPointerException.class, () -> trie.get(null));
		assertThrows(IllegalArgumentException.class, () -> trie.get(""));
	}

	@DisplayName("Add and Search for sample words, testing basic functionality")
	@Test
	void testAddAndGet() {
		// given
		final CompressedTrie<Integer> trie = new CompressedTrie<>();
		trie.put("apple", 1);
		trie.put("apply", 11);
		trie.put("banana", 2);
		trie.put("orange", 3);

		// then
		assertEquals(Optional.of(1), trie.get("apple"));
		assertEquals(Optional.of(11), trie.get("apply"));
		assertEquals(Optional.of(2), trie.get("banana"));
		assertEquals(Optional.of(3), trie.get("orange"));
		// Search for non-existing word
		assertEquals(Optional.empty(), trie.get("pear"));

		// assert number of words inserted
		assertEquals(4, trie.size());
	}

	@DisplayName("Checking for `getKeysWithPrefix()` on null or Empty word should throw error")
	@Test
	void testGetKeysWithPrefix_NullAndEmpty() {
		// given
		final CompressedTrie<Integer> trie = new CompressedTrie<>();

		// then
		assertThrows(NullPointerException.class, () -> trie.getKeysWithPrefix(null, 1));
		assertThrows(IllegalArgumentException.class, () -> trie.getKeysWithPrefix("", 1));
	}

	@DisplayName("Validate return value from `getKeysWithPrefix()` when both existing and non-existing words are " +
			"passed")
	@Test
	void testGetKeysWithPrefix() {
		// given
		final SimpleTrie<Integer> trie = new SimpleTrie<>();
		// add some words
		trie.put("ABC", 1);
		trie.put("ABD", 2);
		trie.put("ACE", 3);
		trie.put("ACID", 4);
		trie.put("ADIEU", 5);

		// then
		// Test prefix has
		assertArrayEquals(
				trie.getKeysWithPrefix("A", 4).toArray(),
				new String[]{ "ABC", "ABD", "ACE", "ACID" }
		);
		assertArrayEquals(
				trie.getKeysWithPrefix("AB", 4).toArray(),
				new String[]{ "ABC", "ABD" }
		);
		assertArrayEquals(
				trie.getKeysWithPrefix("A", Comparator.reverseOrder(), 4).toArray(),
				new String[]{ "ADIEU", "ACID", "ACE", "ABD" }
		);
		assertTrue(trie.getKeysWithPrefix("foo", 10).isEmpty());
	}

	@DisplayName("Basic `remove()` functionality test")
	@Test
	void testRemoveExistingWord() {
		// given
		final CompressedTrie<Integer> trie = new CompressedTrie<>();

		// when
		trie.put("hello", 1);

		// then
		assertTrue(trie.get("hello").isPresent());
		assertEquals(1, trie.remove("hello").orElse(Integer.MIN_VALUE));
		assertFalse(trie.get("hello").isPresent());

		assertFalse(trie.get("world").isPresent()); // verify the word does not exist
		assertFalse(trie.remove("world").isPresent()); // test removing non-existent word

		assertEquals(0, trie.size());
		assertEquals(1, trie.getCountOfNodes());
	}

	@DisplayName("Removing a longer word which has a prefix word should not affect the prefix word")
	@Test
	void testRemoveLongerWord() {
		// given
		final CompressedTrie<Integer> trie = new CompressedTrie<>();

		// when
		trie.put("hello", 1);
		trie.put("hell", 2);

		// then
		assertEquals(1, trie.remove("hello").orElse(Integer.MIN_VALUE));
		assertFalse(trie.get("hello").isPresent());
		assertTrue(trie.get("hell").isPresent());

		assertEquals(1, trie.size());
		assertEquals(2, trie.getCountOfNodes());
	}

	@DisplayName("Removing a word which is prefix of a longer word should not affect the longer word")
	@Test
	void testRemovePrefixWord() {
		// given
		final CompressedTrie<Integer> trie = new CompressedTrie<>();

		// when
		trie.put("hello", 1);
		trie.put("hell", 2);

		// then
		assertEquals(2, trie.remove("hell").orElse(Integer.MIN_VALUE));
		assertTrue(trie.get("hello").isPresent());
		assertFalse(trie.get("hell").isPresent());

		assertEquals(1, trie.size());
		assertEquals(2, trie.getCountOfNodes());
	}

	@DisplayName("Removing an intermediate word should not affect other present words")
	@Test
	void testRemoveIntermediateNode() {
		// given
		final CompressedTrie<Integer> trie = new CompressedTrie<>();

		// when
		trie.put("hello", 1);
		trie.put("help", 2);
		trie.put("world", 3);

		// then
		assertEquals(2, trie.remove("help").orElse(Integer.MIN_VALUE));
		assertFalse(trie.get("help").isPresent());
		assertTrue(trie.get("hello").isPresent());
		assertTrue(trie.get("world").isPresent());

		assertEquals(2, trie.size());
		assertEquals(3, trie.getCountOfNodes());
	}

	@DisplayName("New node is added like an extension when new word of greater length but common prefix is added")
	@Test
	void testAdd() {
		final CompressedTrie<Integer> trie = new CompressedTrie<>();

		trie.put("appear", 1);
		{
			final Optional<CompressedTrie.Node<Integer>> a = trie.find("a");
			assertTrue(a.isPresent());
			assertTrue(a.get().isTerminal());
			assertEquals("ppear", a.get().getPrefix().toString());
			assertEquals(trie.find("appear"), a);

			assertEquals(2, trie.getCountOfNodes());
		}

		trie.put("appearance", 2);
		{
			final Optional<CompressedTrie.Node<Integer>> a = trie.find("a");
			assertTrue(a.isPresent());
			assertTrue(a.get().isTerminal());
			assertEquals("ppear", a.get().getPrefix().toString());
			assertEquals(trie.find("appear"), a);

			final Optional<CompressedTrie.Node<Integer>> appeara = trie.find("appeara");
			assertTrue(appeara.isPresent());
			assertTrue(appeara.get().isTerminal());
			assertEquals("nce", appeara.get().getPrefix().toString());
			assertEquals(trie.find("appearance"), appeara);

			assertEquals(3, trie.getCountOfNodes());
		}
	}

	@DisplayName("Existing node is split into two when new word is added " +
			"which is already part of an existing word's prefix")
	@Test
	void testAdd2() {
		final CompressedTrie<Integer> trie = new CompressedTrie<>();

		trie.put("appearance", 1);
		{
			final Optional<CompressedTrie.Node<Integer>> a = trie.find("a");
			assertTrue(a.isPresent());
			assertTrue(a.get().isTerminal());
			assertEquals("ppearance", a.get().getPrefix().toString());
			assertEquals(trie.find("appearance"), a);

			assertEquals(2, trie.getCountOfNodes());
		}

		trie.put("appear", 2);
		{
			final Optional<CompressedTrie.Node<Integer>> a = trie.find("a");
			assertTrue(a.isPresent());
			assertTrue(a.get().isTerminal());
			assertEquals("ppear", a.get().getPrefix().toString());
			assertEquals(trie.find("appear"), a);

			final Optional<CompressedTrie.Node<Integer>> appeara = trie.find("appeara");
			assertTrue(appeara.isPresent());
			assertTrue(appeara.get().isTerminal());
			assertEquals("nce", appeara.get().getPrefix().toString());
			assertEquals(trie.find("appearance"), appeara);

			assertEquals(3, trie.getCountOfNodes());
		}
	}

	@DisplayName("Adding bunch of words together which test tree from root node after every word")
	@Test
	void testAdd3() {
		final CompressedTrie<Integer> trie = new CompressedTrie<>();

		trie.put("appeaser", 1);
		{
			final Optional<CompressedTrie.Node<Integer>> a = trie.find("a");
			assertTrue(a.isPresent());
			assertTrue(a.get().isTerminal());
			assertEquals("ppeaser", a.get().getPrefix().toString());
			assertEquals(trie.find("appeaser"), a);

			assertEquals(2, trie.getCountOfNodes());
		}

		trie.put("appeasement", 2);
		{
			final Optional<CompressedTrie.Node<Integer>> a = trie.find("a");
			assertTrue(a.isPresent());
			assertFalse(a.get().isTerminal());
			assertEquals("ppease", a.get().getPrefix().toString());
			assertEquals(trie.find("appease"), a);

			final Optional<CompressedTrie.Node<Integer>> appeaser = trie.find("appeaser");
			assertTrue(appeaser.isPresent());
			assertTrue(appeaser.get().isTerminal());
			assertEquals("", appeaser.get().getPrefix().toString());
			assertEquals(trie.find("appeaser"), appeaser);

			final Optional<CompressedTrie.Node<Integer>> appeasement = trie.find("appeasement");
			assertTrue(appeasement.isPresent());
			assertTrue(appeasement.get().isTerminal());
			assertEquals("ent", appeasement.get().getPrefix().toString());
			assertEquals(trie.find("appeasement"), appeasement);

			assertEquals(4, trie.getCountOfNodes());
		}

		trie.put("appeasable", 3);
		{
			final Optional<CompressedTrie.Node<Integer>> a = trie.find("a");
			assertTrue(a.isPresent());
			assertFalse(a.get().isTerminal());
			assertEquals("ppeas", a.get().getPrefix().toString());
			assertEquals(trie.find("appeas"), a);

			final Optional<CompressedTrie.Node<Integer>> appeasa = trie.find("appeasa");
			assertTrue(appeasa.isPresent());
			assertTrue(appeasa.get().isTerminal());
			assertEquals("ble", appeasa.get().getPrefix().toString());
			assertEquals(trie.find("appeasable"), appeasa);

			final Optional<CompressedTrie.Node<Integer>> appease = trie.find("appease");
			assertTrue(appease.isPresent());
			assertFalse(appease.get().isTerminal());
			assertEquals("", appease.get().getPrefix().toString());

			final Optional<CompressedTrie.Node<Integer>> appeaser = trie.find("appeaser");
			assertTrue(appeaser.isPresent());
			assertTrue(appeaser.get().isTerminal());
			assertEquals("", appeaser.get().getPrefix().toString());
			assertEquals(trie.find("appeaser"), appeaser);

			final Optional<CompressedTrie.Node<Integer>> appeasement = trie.find("appeasement");
			assertTrue(appeasement.isPresent());
			assertTrue(appeasement.get().isTerminal());
			assertEquals("ent", appeasement.get().getPrefix().toString());
			assertEquals(trie.find("appeasement"), appeasement);

			assertEquals(6, trie.getCountOfNodes());
		}

		trie.put("appear", 4);
		{
			final Optional<CompressedTrie.Node<Integer>> a = trie.find("a");
			assertTrue(a.isPresent());
			assertFalse(a.get().isTerminal());
			assertEquals("ppea", a.get().getPrefix().toString());
			assertEquals(trie.find("appea"), a);

			final Optional<CompressedTrie.Node<Integer>> appear = trie.find("appear");
			assertTrue(appear.isPresent());
			assertTrue(appear.get().isTerminal());
			assertEquals("", appear.get().getPrefix().toString());

			final Optional<CompressedTrie.Node<Integer>> appeas = trie.find("appeas");
			assertTrue(appeas.isPresent());
			assertFalse(appeas.get().isTerminal());
			assertEquals("", appeas.get().getPrefix().toString());

			final Optional<CompressedTrie.Node<Integer>> appeasa = trie.find("appeasa");
			assertTrue(appeasa.isPresent());
			assertTrue(appeasa.get().isTerminal());
			assertEquals("ble", appeasa.get().getPrefix().toString());
			assertEquals(trie.find("appeasable"), appeasa);

			final Optional<CompressedTrie.Node<Integer>> appease = trie.find("appease");
			assertTrue(appease.isPresent());
			assertFalse(appease.get().isTerminal());
			assertEquals("", appease.get().getPrefix().toString());

			final Optional<CompressedTrie.Node<Integer>> appeaser = trie.find("appeaser");
			assertTrue(appeaser.isPresent());
			assertTrue(appeaser.get().isTerminal());
			assertEquals("", appeaser.get().getPrefix().toString());
			assertEquals(trie.find("appeaser"), appeaser);

			final Optional<CompressedTrie.Node<Integer>> appeasement = trie.find("appeasement");
			assertTrue(appeasement.isPresent());
			assertTrue(appeasement.get().isTerminal());
			assertEquals("ent", appeasement.get().getPrefix().toString());
			assertEquals(trie.find("appeasement"), appeasement);
			assertEquals(2, appeasement.map(CompressedTrie.Node::getValue).orElse(Integer.MIN_VALUE));

			assertEquals(8, trie.getCountOfNodes());
		}
	}


}