package be.uclouvain.solvercheck.assertions.stateful;

/**
 * This class is meant to materialize a "push" operation in the context of
 * a dive search.
 *
 * Technical note: This class is implemented as a singleton.
 */
final class Push implements DiveOperation {
    /** The only instance of the class. */
    private static final Push INSTANCE = new Push();

    /** Singleton exposes no public constructor. */
    private Push() { }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Push";
    }

    /**
     * Returns a reference to the only instance of the class.
     *
     * @return a reference to the only instance of the class.
     */
    public static Push getInstance() {
        return INSTANCE;
    }
}
