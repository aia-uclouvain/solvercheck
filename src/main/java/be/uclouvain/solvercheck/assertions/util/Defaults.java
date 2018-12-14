package be.uclouvain.solvercheck.assertions.util;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;

/**
 * The only purpose of this class is to provide a common namespace to store the
 * constants used to provide a default value to all the configuration
 * parameters.
 */
public final class Defaults {
    /**
     * The default number of partial assignment generated (and tested) for each
     * anchor value.
     */
    public static final int DEFAULT_EXAMPLES = 20;
    /**
     * The default minimal value that may appear in a generated partial
     * assignment.
     */
    public static final int DEFAULT_MIN_VALUE = MIN_VALUE;
    /**
     * The default maximal value that may appear in a generated partial
     * assignment.
     */
    public static final int DEFAULT_MAX_VALUE = MAX_VALUE;
    /**
     * The default maximum difference between any two values occurring in a
     * partial assignment.
     */
    public static final int DEFAULT_SPREAD = 10;
    /**
     * The default minimum number of variables constituting a partial
     * assignment.
     */
    public static final int DEFAULT_NB_VARS_MIN = 0;
    /**
     * The default maximum number of variables constituting a partial
     * assignment.
     */
    public static final int DEFAULT_NB_VARS_MAX = 5;
    /**
     * The default maximum number of values in a domain constitutive of a
     * partial assignment.
     */
    public static final int DEFAULT_MAX_DOM_SIZE = 5;

    /** Utility class should have no public constructor. */
    private Defaults() { }
}
