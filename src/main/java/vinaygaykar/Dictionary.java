package vinaygaykar;


import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;


/**
 * A simple interface that defines a dictionary data structure that can store key-value pairs. The dictionary is
 * a collection of unique keys, and each key maps to a value object. The keys are stored as a {@code String} object
 * and the values can be of any type V, defined at runtime.
 * <p>
 * This interface has been modelled after but is not similar to {@link java.util.Map}. {@code null} keys or values
 * are strictly prohibited.
 * <p>
 * This interface provides methods for adding and removing key-value pairs, and searching for values associated with
 * a given key or keys with a given prefix. The {@code Optional} type is used in the return type of all methods that
 * return values, to handle null cases gracefully.
 * <p>
 * All methods of this interface that take a key as a parameter will throw an {@code IllegalArgumentException}
 * if the key is an empty string, and a {@code NullPointerException} if the key is null. Similarly, methods that
 * take a value as a parameter will throw a {@code NullPointerException} if the value is null.
 *
 * @param <V> The type of the values that are stored in the dictionary
 *
 * @author Vinay Gaykar
 */
public interface Dictionary<V> {

	/**
	 * Inserts a {@code key} with the associated {@code value} into the dictionary if it was not present. If the
	 * {@code key} is already present then the existing associated value is replaced with the new {@code value} object.
	 * <p>
	 * Return object is previous associated value if key was already present or else {@code null} if this key is new.
	 *
	 * @param key   the {@code key} to be added
	 * @param value the associated {@code value} object for the given {@code key}
	 *
	 * @return Previous {@code value} associated with the {@code key}
	 *
	 * @throws IllegalArgumentException if the {@code key} is empty
	 * @throws NullPointerException     if {@code key} or {@code value} is {@code null}
	 */
	Optional<V> put(final String key, final V value);

	/**
	 * Retrieves associated {@code value} object associated with the given {@code key}, if present in the dictionary
	 * or else return {@link Optional#empty()}.
	 *
	 * @param key the {@code key} to retrieve the {@code value} object for
	 *
	 * @return an {@link Optional} object containing the associated {@code value} object, or {@link Optional#empty()}
	 * if the {@code key} is not present
	 *
	 * @throws IllegalArgumentException if the {@code key} is empty
	 * @throws NullPointerException     if {@code key} is {@code null}
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
	 * @throws IllegalArgumentException if the {@code key} is empty or count is not positive
	 * @throws NullPointerException     if {@code key} is {@code null}
	 */
	List<String> getKeysWithPrefix(final String prefix, final int count);

	/**
	 * Removes the {@code key} from this dictionary if it is present (optional operation).
	 * <p>
	 * Returns the {@code value} to which this map previously associated the {@code key}, or <tt>null</tt> if the map
	 * contained no mapping for the {@code key}.
	 * <p>
	 * The map will not contain a mapping for the specified {@code key} once the call returns.
	 *
	 * @param key {@code key} to be removed
	 *
	 * @return the previous value associated with <tt>key</tt>, or <tt>null</tt> if there was no mapping for
	 * <tt>key</tt>.
	 *
	 * @throws NullPointerException     if the specified {@code key} is {@code null}
	 * @throws IllegalArgumentException if the specified {@code key} is empty
	 */
	Optional<V> remove(final String key);

	/**
	 * Returns the number of words in the dictionary.
	 *
	 * @return the number of words in the dictionary.
	 */
	long size();


	// Defaultable & composite methods

	/**
	 * Only inserts the key-value pair if the key is absent from the dictionary. If the key is already present then
	 * no operation is performed.
	 *
	 * @param key   {@code key} to add
	 * @param value associated {@code value} object
	 *
	 * @return the previously associated {@code value} object, or {@code null} if there was no mapping for the
	 * {@code key}
	 *
	 * @throws IllegalArgumentException if the {@code key} is empty
	 * @throws NullPointerException     if {@code key} or {@code value} is {@code null}
	 * @implSpec The default implementation is equivalent to:
	 * <pre>
	 * {@code
	 * 		V oldVal = get(key);
	 * 		if (oldVal == null) {
	 * 			put(key, value);
	 * 			return null;
	 *        }
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
	 * If the specified {@code key} is not already associated with a {@code value}, attempts to compute its
	 * {@code value} using the given {@code mappingFunction} and enters it into this dictionary.
	 * <p>
	 * If the {@code key} is to be inserted i.e. no {@code value} is associated and the mapping function returns
	 * {@code null} then no mapping is recorded. If the function itself throws  an (unchecked) exception, the
	 * exception is rethrown, and no mapping is recorded.
	 * <p>
	 * The most common usage is to construct a new object serving as an initial mapped {@code value} or memoized
	 * result, as in:
	 *
	 * <pre>
	 * {@code
	 * 		dictionary.computeIfAbsent(key, k -> new Value(f(k)));
	 * }
	 * </pre>
	 *
	 * <p>
	 * Or to implement a multi-value dictionary, {@code Dictionary<Collection<V>>}, supporting multiple values per
	 * {@code key}:
	 * <pre>
	 * {@code
	 * 		dictionary.computeIfAbsent(key, k -> new HashSet<V>()).add(v);
	 * }
	 * </pre>
	 *
	 * @param key             {@code key} to check
	 * @param mappingFunction the function to compute a new {@code value} object
	 *
	 * @return the current (existing or computed) {@code value} associated with the specified {@code key}
	 *
	 * @throws NullPointerException     if the {@code key}, {@code mappingFunction} is {@code null} or the new
	 *                                  {@code value} object generated from the function is {@code null}
	 * @throws IllegalArgumentException if specified {@code key} is empty
	 * @implSpec The default implementation is equivalent to the following steps
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
	 * If the {@code value} for the specified {@code key} is present, attempts to compute a new {@code value} object
	 * using {@code remappingFunction} and associating it with the {@code key}. Returns the new mapped {@code value}.
	 * <p>
	 * If the function returns {@code null}, the mapping is removed. If the function itself throws an (unchecked)
	 * exception, the exception is rethrown, and the current mapping is left unchanged.
	 *
	 * @param key               {@code key} to check
	 * @param remappingFunction the function to compute the new {@code value} object
	 *
	 * @return the new {@code value} object associated with the specified {@code key}, or {@link Optional#empty()} if
	 * the key is not present
	 *
	 * @throws NullPointerException     if the {@code key}, {@code remappingFunction} or new {@code value} generated
	 *                                  from the function is {@code null}
	 * @throws IllegalArgumentException if {@code key} is empty
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
