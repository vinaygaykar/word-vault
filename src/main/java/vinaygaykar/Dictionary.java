package vinaygaykar;


import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;


/**
 * An interface that defines a dictionary data structure that can store key-value pairs.
 * The dictionary is a collection of unique keys, and each key maps to a value object.
 * The keys are stored as a {@link String} object and the values can be of any type V, defined at runtime.
 * <p>
 * This interface has been modeled after but is not similar to {@link java.util.Map}.
 * <tt>null</tt> keys or values are strictly prohibited.
 * <p>
 * This interface provides methods for adding and removing key-value pairs, and searching for values associated with
 * a given key or keys with a given prefix.
 * The {@link Optional} type is used in the return type of all methods that return values, to handle <tt>null</tt>
 * cases gracefully.
 * <p>
 * All methods of this interface that take a key as a parameter will throw an {@link IllegalArgumentException} if the
 * key is an empty string, and a {@link NullPointerException} if the key is <tt>null</tt>.
 * Similarly, methods that take a value as a parameter will throw a {@link NullPointerException} if the value is
 * <tt>null</tt>.
 *
 * @param <V> the type of the values that are stored
 *
 * @author Vinay Gaykar
 */
public interface Dictionary<V> {

	/**
	 * Inserts a key with the associated value into the dictionary if it was not present.
	 * If the key is already present, then the existing associated value is replaced with the new value object.
	 * <p>
	 * Return object is previous associated value if key was already present or else <tt>null</tt> if this key is new.
	 *
	 * @param key   the key to be added
	 * @param value the associated value object
	 *
	 * @return Previous value associated with the key
	 *
	 * @throws IllegalArgumentException if the key is empty
	 * @throws NullPointerException     if the key or value is <tt>null</tt>
	 */
	Optional<V> put(final String key, final V value);

	/**
	 * Retrieves an associated value object with the given key, if present in the dictionary or else return
	 * {@link Optional#empty()}.
	 *
	 * @param key the key whose associated value is to be returned
	 *
	 * @return an {@link Optional} object containing the value object, or {@link Optional#empty()} if the key is absent
	 *
	 * @throws IllegalArgumentException if the key is empty
	 * @throws NullPointerException     if the key is <tt>null</tt>
	 */
	Optional<V> get(final String key);

	/**
	 * Searches the dictionary for all keys that that start with the given prefix.
	 * Will return a list of all matching keys as a {@link List} of max size <tt>count</tt>.
	 *
	 * @param prefix the prefix to search for
	 * @param count  the maximum number of words to return
	 *
	 * @return a {@link List} of keys that start with the given prefix, up to a maximum of <tt>count</tt>
	 *
	 * @throws IllegalArgumentException if the key is empty or count is not positive
	 * @throws NullPointerException     if the key is <tt>null</tt>
	 */
	List<String> getKeysWithPrefix(final String prefix, final int count);

	/**
	 * Removes the key from this dictionary if it is present (optional operation).
	 * <p>
	 * Returns the value to which this map previously associated the key, or <tt>null</tt> if there was no mapping
	 * for the key.
	 * <p>
	 * The dictionary will not contain any mapping for the specified key once the call returns.
	 *
	 * @param key the key to be removed
	 *
	 * @return the previous value associated with the key, or <tt>null</tt> if there was no mapping for the key
	 *
	 * @throws NullPointerException     if the key is <tt>null</tt>
	 * @throws IllegalArgumentException if the key is empty
	 */
	Optional<V> remove(final String key);

	/**
	 * Returns the number of words in the dictionary.
	 *
	 * @return the number of words in the dictionary.
	 */
	long size();


	/**
	 * Only inserts the key-value pair if the key is absent from the dictionary.
	 * If the key is already present, then no operation is performed.
	 *
	 * @param key   the key to add
	 * @param value associated value object
	 *
	 * @return the previously associated value object, or <tt>null</tt> if there was no mapping for the key
	 *
	 * @throws IllegalArgumentException if the key is empty
	 * @throws NullPointerException     if the key or value is <tt>null</tt>
	 * @implSpec The default implementation is equivalent to:
	 * <pre>
	 * {@code
	 * 		V oldVal = get(key);
	 * 		if (oldVal == null) {
	 * 			put(key, value);
	 * 			return null;
	 * 		}
	 * 		else return oldVal;
	 * }
	 * </pre>
	 */
	default Optional<V> putIfAbsent(final String key, final V value) {
		validateKey(key);
		validateValue(value);

		final Optional<V> oldVal = get(key);
		if (!oldVal.isPresent()) {
			put(key, value);
			return Optional.empty();
		} else return oldVal;
	}

	/**
	 * If the key-value pair is absent from this dictionary, attempts to compute its new value using the given
	 * {@code mappingFunction} and enters it into this dictionary.
	 * <p>
	 * If the key is to be inserted and the mapping function returns <tt>null</tt> then no mapping is recorded.
	 * If the function itself throws an (unchecked) exception, the exception is rethrown, and no mapping is recorded.
	 * <p>
	 * The most common usage is to construct a new object serving as an initially mapped value or memoized
	 * result, as in:
	 * <pre>
	 * {@code
	 * 		dictionary.computeIfAbsent(key, k -> new Value(f(k)));
	 * }
	 * </pre>
	 * <p>
	 * Or to implement a multi-value dictionary, {@code Dictionary<Collection<V>>}, supporting multiple values per key:
	 * <pre>
	 * {@code
	 * 		dictionary.computeIfAbsent(key, k -> new HashSet<V>()).add(v);
	 * }
	 * </pre>
	 *
	 * @param key             the key to check
	 * @param mappingFunction the function to compute a new value object
	 *
	 * @return the current (existing or computed) value associated with the specified key
	 *
	 * @throws NullPointerException     if the key, {@code mappingFunction} or the new value object generated from
	 *                                  the function is <tt>null</tt>
	 * @throws IllegalArgumentException if the key is empty
	 * @implSpec The default implementation is equivalent to the following steps:
	 * <pre>
	 * {@code
	 * 		if (dictionary.get(key) == null) {
	 * 			V newValue = mappingFunction.apply(key);
	 * 			dictionary.put(key, newValue);
	 *    }
	 * }
	 * </pre>
	 */
	default Optional<V> computeIfAbsent(final String key,
										final Function<String, ? extends V> mappingFunction) {
		Objects.requireNonNull(mappingFunction);
		validateKey(key);

		final Optional<V> oldVal = get(key);
		if (!oldVal.isPresent()) {
			final V newVal = mappingFunction.apply(key);
			if (newVal != null) put(key, newVal);

			return Optional.ofNullable(newVal);
		}

		return oldVal;
	}

	/**
	 * If the value for the specified key is present, attempts to compute a new value object using
	 * {@code remappingFunction} and associating it with the key.
	 * Returns the new mapped value.
	 * <p>
	 * If the function returns <tt>null</tt>, the mapping is removed.
	 * If the function itself throws an (unchecked) exception, the exception is rethrown, and the current mapping is
	 * left unchanged.
	 *
	 * @param key               the key to check
	 * @param remappingFunction the function to compute the new value object
	 *
	 * @return the new value object associated with the specified key, or {@link Optional#empty()} if
	 * the key is not present
	 *
	 * @throws NullPointerException     if the key, {@code remappingFunction} or new value generated
	 *                                  from the function is <tt>null</tt>
	 * @throws IllegalArgumentException if the key is empty
	 * @implSpec The default implementation is equivalent to performing the following steps:
	 * <pre>
	 * {@code
	 * 		if (get(key) != null) {
	 * 			V oldValue = get(key);
	 * 			V newValue = remappingFunction.apply(key, oldValue);
	 * 			if (newValue != null) put(key, newValue);
	 * 			else remove(key);
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
					if (newValue == null) remove(key);
					else put(key, newValue);

					return newValue;
				});
	}

	/**
	 * @param key a {@link String} representing a key to validate
	 *
	 * @throws NullPointerException     if the key is <tt>null</tt>
	 * @throws IllegalArgumentException if the key is empty
	 */
	default void validateKey(final String key) {
		if (key == null) throw new NullPointerException("Key is null");
		if (key.isEmpty()) throw new IllegalArgumentException("Key is empty");
	}

	/**
	 * @param value an value object to validate of type {@code V}
	 *
	 * @throws NullPointerException if the value is <tt>null</tt>
	 */
	default void validateValue(final V value) {
		if (value == null) throw new NullPointerException("Value is null");
	}

}
