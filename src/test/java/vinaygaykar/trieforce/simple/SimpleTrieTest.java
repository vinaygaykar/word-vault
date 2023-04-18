package vinaygaykar.trieforce.simple;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class SimpleTrieTest {

	@DisplayName("Add & Get for Null or Empty word or value is not allowed")
	@Test
	void testAddAndGet_NullAndEmpty() {
		// given
		final SimpleTrie<Integer> trie = new SimpleTrie<>();

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
		final SimpleTrie<Integer> trie = new SimpleTrie<>();
		// add some words
		trie.add("hello", 1);
		trie.add("world", 2);
		trie.add("pneumonoultramicroscopicsilicovolcanoconiosis", 3);

		// then
		assertEquals(1, trie.getValue("hello").orElse(-1));
		assertEquals(2, trie.getValue("world").orElse(-1));
		assertEquals(3, trie.getValue("pneumonoultramicroscopicsilicovolcanoconiosis").orElse(-1));
		// words with no value present
		assertFalse(trie.getValue("pneumonoultra").isPresent()); // half of the actual word
		assertFalse(trie.getValue("pneab").isPresent()); // '*ab' split in a non-existing branch
		// assert number of words inserted
		assertEquals(3, trie.size());
	}

	@DisplayName("Validate `add()` and `getValue()` on default merge function")
	@Test
	void testAddAndGet_DefaultMergeFn() {
		// given
		final SimpleTrie<Integer> trie = new SimpleTrie<>();

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
		final SimpleTrie<Integer> trie = new SimpleTrie<>(Integer::sum);

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
		final SimpleTrie<Integer> trie = new SimpleTrie<>();

		// then
		assertTrue(trie.hasPrefix(null, 1).isEmpty());
		assertTrue(trie.hasPrefix("", 1).isEmpty());
	}

	@DisplayName("Validate return value from `hasPrefix()` when both existing and non-existing words are passed")
	@Test
	void testHasPrefix() {
		// given
		final SimpleTrie<String> trie = new SimpleTrie<>();
		// add some words
		trie.add("hello", "abc");
		trie.add("world", "def");

		// then
		// Test prefix has
		assertArrayEquals(trie.hasPrefix("he", 10).toArray(), new String[]{ "hello" });
		assertArrayEquals(trie.hasPrefix("wor", 10).toArray(), new String[]{ "world" });
		assertTrue(trie.hasPrefix("foo", 10).isEmpty());
	}

}
