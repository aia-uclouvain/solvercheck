package be.uclouvain.solvercheck.utils.relations;

/**
 * This enum encapsulates the possible outcomes from a comparison between two
 * partially orderable objects.
 */
public enum PartialOrdering {
    /** Current object is higher up in the partial order lattice */
    STRONGER,
    /** Current object is lower down in the partial order lattice */
    WEAKER,
    /** Current object denotes the same node from the partial order lattice */
    EQUIVALENT,
    /** Current object cannot be compared with the other according to the given partial order */
    INCOMPARABLE
}
