package vinaygaykar.trieforce.compressed;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
		assertThrows(IllegalArgumentException.class, () -> trie.add(null, null));
		assertThrows(IllegalArgumentException.class, () -> trie.add("", null));
		// Add - null value
		assertThrows(IllegalArgumentException.class, () -> trie.add("something", null));

		// Get - null & empty word
		assertFalse(trie.getValue(null).isPresent());
		assertFalse(trie.getValue("").isPresent());
	}

	@DisplayName("Add and Search for sample words, testing basic functionality")
	@Test
	void testAddAndGet() {
		// given
		final CompressedTrie<Integer> trie = new CompressedTrie<>();
		trie.add("apple", 1);
		trie.add("apply", 11);
		trie.add("banana", 2);
		trie.add("orange", 3);

		// then
		assertEquals(Optional.of(1), trie.getValue("apple"));
		assertEquals(Optional.of(11), trie.getValue("apply"));
		assertEquals(Optional.of(2), trie.getValue("banana"));
		assertEquals(Optional.of(3), trie.getValue("orange"));
		// Search for non-existing word
		assertEquals(Optional.empty(), trie.getValue("pear"));

		// assert number of words inserted
		assertEquals(4, trie.size());
	}

	@DisplayName("Validate `add()` and `getValue()` on default merge function")
	@Test
	void testAddAndGet_DefaultMergeFn() {
		// given
		final CompressedTrie<Integer> trie = new CompressedTrie<>();

		// when
		trie.add("hello", 1);
		trie.add("hello", 2);

		// then
		assertEquals(2, trie.getValue("hello").orElse(-1));
	}

	@DisplayName("Validate `add()` and `getValue()` on custom merge function")
	@Test
	void testAddAndGet_CustomMergeFn() {
		// given
		final CompressedTrie<Integer> trie = new CompressedTrie<>(Integer::sum);

		// when
		trie.add("hello", 100);
		trie.add("hello", 200);

		// then
		assertEquals(300, trie.getValue("hello").orElse(-1));
	}

	@DisplayName("Checking for `hasPrefix()` on null or Empty word should return empty list for any count")
	@Test
	void testHasPrefix_NullAndEmpty() {
		// given
		final CompressedTrie<Integer> trie = new CompressedTrie<>();

		// then
		assertTrue(trie.hasPrefix(null, 1).isEmpty());
		assertTrue(trie.hasPrefix("", 1).isEmpty());
	}

	@DisplayName("Validate return value from `hasPrefix()` when both existing and non-existing words are passed")
	@Test
	void testHasPrefix() {
		// given
		final CompressedTrie<Integer> trie = new CompressedTrie<>();
		trie.add("apple", 1);
		trie.add("apply", 11);
		trie.add("banana", 2);
		trie.add("orange", 3);
		trie.add("peach", 4);
		trie.add("pear", 5);
		trie.add("pineapple", 6);

		// then
		final List<String> expected = Arrays.asList("apple", "apply");
		assertEquals(expected, trie.hasPrefix("a", 3));
		assertEquals(Arrays.asList("peach", "pear", "pineapple"), trie.hasPrefix("p", 3));
		assertEquals(Collections.emptyList(), trie.hasPrefix("z", 3));
	}

	@DisplayName("New node is added like an extension when new word of greater length but common prefix is added")
	@Test
	void testAdd() {
		final CompressedTrie<Integer> trie = new CompressedTrie<>();

		trie.add("appear", 1);
		{
			final Optional<CompressedTrie.Node<Integer>> a = trie.find("a");
			assertTrue(a.isPresent());
			assertTrue(a.get().isTerminal());
			assertEquals("ppear", a.get().getPrefix().toString());
			assertEquals(trie.find("appear"), a);

			assertEquals(2, trie.getCountOfNodes());
		}

		trie.add("appearance", 2);
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

		trie.add("appearance", 1);
		{
			final Optional<CompressedTrie.Node<Integer>> a = trie.find("a");
			assertTrue(a.isPresent());
			assertTrue(a.get().isTerminal());
			assertEquals("ppearance", a.get().getPrefix().toString());
			assertEquals(trie.find("appearance"), a);

			assertEquals(2, trie.getCountOfNodes());
		}

		trie.add("appear", 2);
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

		trie.add("appeaser", 1);
		{
			final Optional<CompressedTrie.Node<Integer>> a = trie.find("a");
			assertTrue(a.isPresent());
			assertTrue(a.get().isTerminal());
			assertEquals("ppeaser", a.get().getPrefix().toString());
			assertEquals(trie.find("appeaser"), a);

			assertEquals(2, trie.getCountOfNodes());
		}

		trie.add("appeasement", 2);
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

		trie.add("appeasable", 3);
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

		trie.add("appear", 4);
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

			assertEquals(8, trie.getCountOfNodes());
		}
	}


}