package be.uclouvain.solvercheck.fuzzing;

import java.util.stream.Stream;

public interface Generator<T> {

    String name();

    Stream<T> generate(Randomness randomness);

}
