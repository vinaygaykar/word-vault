package vinaygaykar;


import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vinaygaykar.trieforce.compressed.CompressedTrie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


class DictionaryTest {

	@DisplayName("Validate `putIfAbsent` functionality")
	@Test
	void putIfAbsent() {
		// given
		final Dictionary<Integer> trie = new CompressedTrie<>();

		// when
		final Optional<Integer> prevValOpt = trie.putIfAbsent("Hello", 1);
		final Optional<Integer> dupValOpt = trie.putIfAbsent("Hello", 2);

		// then
		assertFalse(prevValOpt.isPresent());
		assertEquals(1, dupValOpt.orElse(Integer.MIN_VALUE));
		assertEquals(1, trie.get("Hello").orElse(Integer.MIN_VALUE));
	}

	@DisplayName("Validate `computeIfAbsent` functionality")
	@Test
	void computeIfAbsent() {
		// given
		final Dictionary<Integer> trie = new CompressedTrie<>();

		// when
		final Optional<Integer> newValOpt = trie.computeIfAbsent("Hello", k -> 1);
		final Optional<Integer> dupValOpt = trie.computeIfAbsent("Hello", k -> 2);

		// then
		assertEquals(1, newValOpt.orElse(Integer.MIN_VALUE));
		assertEquals(1, dupValOpt.orElse(Integer.MIN_VALUE));
		assertEquals(1, trie.get("Hello").orElse(Integer.MIN_VALUE));
	}

	@DisplayName("Validate `computeIfPresent` functionality")
	@Test
	void computeIfPresent() {
		// given
		final Dictionary<Integer> trie = new CompressedTrie<>();

		// when
		final Optional<Integer> noValOpt = trie.computeIfPresent("Hello", (k, v) -> 1);
		final Optional<Integer> newValOpt = trie.put("Hello", 2);
		final Optional<Integer> dupValOpt = trie.computeIfPresent("Hello", (k, v) -> 3);
		final Optional<Integer> removedValOpt = trie.computeIfPresent("Hello", (k, v) -> null);

		// then
		assertFalse(noValOpt.isPresent());
		assertFalse(newValOpt.isPresent());
		assertEquals(3, dupValOpt.orElse(Integer.MIN_VALUE));
		assertFalse(removedValOpt.isPresent());
		assertFalse(trie.get("Hello").isPresent());
	}

}