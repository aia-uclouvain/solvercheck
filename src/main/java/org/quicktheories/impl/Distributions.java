package org.quicktheories.impl;

import org.quicktheories.core.Gen;
import org.quicktheories.core.Strategy;

public final class Distributions {

    public static <T> Distribution<T> boundarySkewed(
            final Strategy config,
            final Gen<T> generator) {
        return new BoundarySkewedDistribution<>(config, generator);
    }

    public static <T> Distribution<T> random(
            final Strategy config,
            final Gen<T> generator) {
        return new RandomDistribution<>(config, generator);
    }

    public static <T> Distribution<T> forced(
            final Strategy config,
            final Gen<T> generator,
            final long[] forced) {
        return new ForcedDistribution<>(config, generator, forced);
    }

    public static <T> T nextValue(final Distribution<T> distribution) {
        return distribution.generate().value();
    }

}
