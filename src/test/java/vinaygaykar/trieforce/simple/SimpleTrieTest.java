package vinaygaykar.trieforce.simple;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
		final SimpleTrie<Integer> trie = new SimpleTrie<>();
		// add some words
		trie.put("hello", 1);
		trie.put("world", 2);
		trie.put("pneumonoultramicroscopicsilicovolcanoconiosis", 3);

		// then
		assertEquals(1, trie.get("hello").orElse(-1));
		assertEquals(2, trie.get("world").orElse(-1));
		assertEquals(3, trie.get("pneumonoultramicroscopicsilicovolcanoconiosis").orElse(-1));
		// words with no value present
		assertFalse(trie.get("pneumonoultra").isPresent()); // half of the actual word
		assertFalse(trie.get("pneab").isPresent()); // '*ab' split in a non-existing branch
		// assert number of words inserted
		assertEquals(3, trie.size());
	}

	@DisplayName("Checking for `getKeysWithPrefix()` on null or Empty word should throw wrror")
	@Test
	void testGetKeysWithPrefix_NullAndEmpty() {
		// given
		final SimpleTrie<Integer> trie = new SimpleTrie<>();

		// then
		assertThrows(NullPointerException.class, () -> trie.getKeysWithPrefix(null, 1));
		assertThrows(IllegalArgumentException.class, () -> trie.getKeysWithPrefix("", 1));
	}

	@DisplayName("Validate return value from `getKeysWithPrefix()` when both existing and non-existing words are " +
			"passed")
	@Test
	void testGetKeysWithPrefix() {
		// given
		final SimpleTrie<String> trie = new SimpleTrie<>();
		// add some words
		trie.put("hello", "abc");
		trie.put("world", "def");

		// then
		// Test prefix has
		assertArrayEquals(trie.getKeysWithPrefix("he", 10).toArray(), new String[]{ "hello" });
		assertArrayEquals(trie.getKeysWithPrefix("wor", 10).toArray(), new String[]{ "world" });
		assertTrue(trie.getKeysWithPrefix("foo", 10).isEmpty());
	}

}
