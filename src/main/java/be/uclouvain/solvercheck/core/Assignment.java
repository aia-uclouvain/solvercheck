package be.uclouvain.solvercheck.core;

import com.google.common.collect.ImmutableList;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class Assignment implements Iterable<Integer> {
    private final List<Integer> values;

    public Assignment(final List<Integer> values) {
        this.values = ImmutableList.copyOf(values);
    }

    public Integer get(int var) {
        return values.get(var);
    }

    public int size() {
        return values.size();
    }

    public List<Integer> asList() {
        return values;
    }

    public Set<Integer> asSet() {
        return stream().collect(Collectors.toSet());
    }

    public Integer[] asArray() {
        return values.toArray(new Integer[]{});
    }

    @Override
    public Iterator<Integer> iterator() {
        return values.iterator();
    }

    public Stream<Integer> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    @Override
    public String toString() {
        return values.toString();
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(final Object other) {
        return other instanceof Assignment && values.equals(((Assignment) other).values);
    }
}
