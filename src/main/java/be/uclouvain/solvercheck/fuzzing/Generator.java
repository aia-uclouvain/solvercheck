package be.uclouvain.solvercheck.fuzzing;

import java.util.stream.Stream;

/**
 * This is the interface of a generator. That is to say of an object whose
 * purpose is to act as if it were an infinite stream of data with the
 * appropriate type T.
 *
 * @param <T> The type of generated object.
 */
public interface Generator<T> {
    /**
     * The name (description) associated with the generated values. This
     * proves particularly useful to generate an intelligible error message.
     *
     * @return the name (description) of this generator.
     */
    String name();

    /**
     * Produces (generates) one T using the given randomness source.
     *
     * @param randomness the source of randomness used to perform the fuzzing.
     * @return one single made up object of type T.
     */
    T item(Randomness randomness);

    /**
     * Produces (generates) a stream of the T using the given randomness
     * source to invent pseudo-random T objects.
     *
     * @param randomness the source of randomness used to perform the fuzzing.
     * @return a stream of data, all of type T.
     */
    default Stream<T> generate(Randomness randomness) {
        return Stream.generate(() -> item(randomness));
    }
}
