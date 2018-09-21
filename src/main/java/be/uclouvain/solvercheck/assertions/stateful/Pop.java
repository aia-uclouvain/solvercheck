package be.uclouvain.solvercheck.assertions.stateful;

/**
 * This class is meant to materialize a "pop" operation in the context of
 * a dive search.
 *
 * Technical note: This class is implemented as a singleton.
 */
/* package */ final class Pop implements DiveOperation {
    /** The only instance of the class. */
    private static final Pop INSTANCE = new Pop();

    /** Singleton exposes no public constructor. */
    private Pop() { }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Pop";
    }

    /**
     * Returns a reference to the only instance of the class.
     *
     * @return a reference to the only instance of the class.
     */
    public static Pop getInstance() {
        return INSTANCE;
    }
}
