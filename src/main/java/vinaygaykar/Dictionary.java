package vinaygaykar;


import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;


/**
 * This class maps a {@link String} called {@code key} to a given {@code value} of type {@code V}. This interface has
 * been modelled after but is not similar to {@link java.util.Map}. At its core it is a key-value pair data storage
 * where a key should be a non-null and non-empty {@link String}. Value should be non-null object of type {@code V}.
 * <p>
 * {@code null} keys or values are strictly prohibited.
 *
 * @param <V> the type of the value object associated with each word
 *
 * @author Vinay Gaykar
 */
public interface Dictionary<V> {

	/**
	 * Inserts a key with an associated value into the dictionary if it was not present. If key was present then the
	 * existing value is replaced with the new value object. Returned value is previous associated value (which can be
	 * null if this key is new).
	 *
	 * @param key   the key to be inserted
	 * @param value the associated value object for the key
	 *
	 * @return Previous value associated with the key
	 *
	 * @throws IllegalArgumentException if the key is empty
	 * @throws NullPointerException     if key or value is null
	 */
	Optional<V> put(final String key, final V value);

	/**
	 * Retrieves the {@code value} object associated with the key, if present in the dictionary.
	 *
	 * @param key the key to retrieve the value object for
	 *
	 * @return an {@link Optional} object containing the associated value object, or an empty {@link Optional} if the
	 * key is not present
	 *
	 * @throws IllegalArgumentException if the key is empty
	 * @throws NullPointerException     if key or value is null
	 */
	Optional<V> get(final String key);

	/**
	 * Searches the dictionary for all keys that that start with the given prefix. Will return a list of all matching
	 * keys as a {@link List} of maximum size upto {@code count}.
	 *
	 * @param prefix the prefix to search for
	 * @param count  the maximum number of words to return
	 *
	 * @return a {@link List} of keys that start with the given prefix, up to a maximum of count
	 *
	 * @throws IllegalArgumentException if the key is null or empty or count is not positive
	 */
	List<String> getKeysWithPrefix(final String prefix, final int count);

	/**
	 * Returns the number of words in the dictionary.
	 *
	 * @return the number of words in the dictionary.
	 */
	long size();


	// Defaultable methods

	/**
	 * If the key is not present then it will be entered into the vault associated with the value and {@code null} is
	 * returned. If key is already present then return the associated value.
	 *
	 * @param key   key with which the specified value is to be associated
	 * @param value value to be associated with the specified key
	 *
	 * @return the previous value associated with the specified key, or {@code null} if there was no mapping for the
	 * key
	 *
	 * @throws IllegalArgumentException if the key is empty
	 * @throws NullPointerException     if key or value is null
	 * @implSpec The default implementation is equivalent to, for this {@code dictionary}:
	 *
	 * <pre>
	 * {@code
	 * 		V oldVal = get(key);
	 * 		if (oldVal == null) {
	 * 		    put(key, value);
	 * 		    return value;
	 *        }
	 * 		else return oldVal;
	 * }
	 * </pre>
	 */
	default V putIfAbsent(final String key, final V value) {
		validateKey(key);
		validateValue(value);

		return get(key)
				.orElseGet(() -> {
					put(key, value);
					return value;
				});
	}

	/**
	 * If the specified key is not already associated with a value, attempts to compute its value using the given
	 * mapping function and enters it into this dictionary.
	 * <p>
	 * If the key is to be inserted i.e. no current value association is present and the remapping function returns
	 * {@code null} then {@link IllegalArgumentException} is thrown. If the function itself throws an (unchecked)
	 * exception, the exception is rethrown, and no mapping is recorded. The most common usage is to construct a new
	 * object serving as an initial mapped value or memoized result, as in:
	 *
	 * <pre>
	 * {@code
	 * 		dictionary.computeIfAbsent(key, k -> new Value(f(k)));
	 * }
	 * </pre>
	 *
	 * <p>
	 * Or to implement a multi-value dictionary, {@code Dictionary<Collection<V>>}, supporting multiple values per key:
	 * <pre>
	 * {@code
	 * 		dictionary.computeIfAbsent(key, k -> new HashSet<V>()).add(v);
	 * }
	 * </pre>
	 *
	 * @param key             key with which the specified value is to be associated
	 * @param mappingFunction the function to compute a value
	 *
	 * @return the current (existing or computed) value associated with the specified key
	 *
	 * @throws NullPointerException     if the key or mappingFunction is null or new value generated from remapping
	 *                                  function is null
	 * @throws IllegalArgumentException if specified key is empty
	 * @implSpec The default implementation is equivalent to the following steps
	 *
	 * <pre>
	 * {@code
	 * 		if (dictionary.get(key) == null) {
	 * 			V newValue = mappingFunction.apply(key);
	 * 			dictionary.put(key, newValue);
	 *    }
	 * }
	 * </pre>
	 */
	default V computeIfAbsent(final String key,
							  final Function<String, ? extends V> mappingFunction) {
		Objects.requireNonNull(mappingFunction);
		validateKey(key);

		return get(key)
				.orElseGet(() -> {
					final V v = mappingFunction.apply(key);
					put(key, v);
					return v;
				});
	}

	/**
	 * If the value for the specified key is present, attempts to compute a new value using {@code remappingFunction}
	 * giving the key and its current mapped value.
	 * <p>
	 * If the function returns {@code null}, {@link NullPointerException} will be thrown. If the function itself
	 * throws an (unchecked) exception, the exception is rethrown, and the current mapping is left unchanged.
	 *
	 * @param key               key with which the specified value is to be associated
	 * @param remappingFunction the function to compute a value
	 *
	 * @return the new value associated with the specified key
	 *
	 * @throws NullPointerException     if the key, remappingFunction  or new value generated from remapping
	 *                                  function is null
	 * @throws IllegalArgumentException if key is empty
	 * @implSpec The default implementation is equivalent to performing the following steps for this {@code map},
	 * then returning the current value or {@code null} if now absent:
	 *
	 * <pre>
	 * {@code
	 * 		if (map.get(key) != null) {
	 * 			V oldValue = map.get(key);
	 * 			V newValue = remappingFunction.apply(key, oldValue);
	 * 			if (newValue != null) map.put(key, newValue);
	 *    }
	 * }
	 * </pre>
	 */
	default Optional<V> computeIfPresent(final String key,
										 final BiFunction<String, ? super V, ? extends V> remappingFunction) {
		Objects.requireNonNull(remappingFunction);
		validateKey(key);

		return get(key)
				.map(oldValue -> {
					final V newValue = remappingFunction.apply(key, oldValue);
					put(key, newValue);
					return newValue;
				});
	}

	default void validateKey(final String key) {
		if (key == null)
			throw new NullPointerException("Key is null");
		if (key.isEmpty())
			throw new IllegalArgumentException("Key is empty");
	}

	default void validateValue(final V value) {
		if (value == null)
			throw new NullPointerException("Value is null");
	}

}
