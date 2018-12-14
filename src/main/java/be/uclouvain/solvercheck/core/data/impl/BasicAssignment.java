package be.uclouvain.solvercheck.core.data.impl;

import be.uclouvain.solvercheck.core.data.Assignment;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import java.util.RandomAccess;
import java.util.stream.Collectors;

/**
 * This class merely wraps an existing list type to interpret it as a (complete)
 * assignment {@see Assignment}.
 */
public final class BasicAssignment extends AbstractList<Integer>
        implements Assignment, RandomAccess {

    /** The wrapped collection. */
    private final List<Integer> values;

    /**
     * Creates a new (immutable !) value from the given list of values.
     *
     * @param values the list of values assigned to each of the variables.
     *               (ith item in the list corresponds to the value assigned
     *               to variable x_i)
     */
    /* package */ BasicAssignment(final List<Integer> values) {
        this.values = List.copyOf(values);
    }

    /**
     * Creates a new (immutable !) value from the given list of values.
     *
     * @param values the list of values assigned to each of the variables.
     *               (ith item in the list corresponds to the value assigned
     *               to variable x_i)
     */
    public BasicAssignment(final int... values) {
        this.values = Arrays.stream(values).boxed().collect(Collectors.toList());
    }

    /** {@inheritDoc} */
    @Override
    public Integer get(final int index) {
        return values.get(index);
    }

    /** {@inheritDoc} */
    @Override
    public int size() {
        return values.size();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return values.hashCode();
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object other) {
        return other instanceof Assignment && super.equals(other);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < size(); i++) {
            sb.append("x").append(i).append("=").append(get(i));

            if (i < size() - 1) {
                sb.append(", ");
            }
        }

        return sb.toString();
    }
}
