package be.uclouvain.solvercheck.core.data;

/**
 * This enumeration describes the five 'primitive' comparison operators.
 */
public enum Operator {
    /** EQ means **equality** operator. */
    EQ,
    /** NE means **different from** operator. */
    NE,
    /** LE means lesser or equal (<=) operator. */
    LE,
    /** LT means strictly lesser than (<) operator. */
    LT,
    /** GE means greater or equal to (>=) operator. */
    GE,
    /** GT means strictly greater than (>) operator. */
    GT;

    /** @return the operator that is the negation from 'this' relation. */
    public Operator not() {
        switch (this) {
            case EQ:
                return NE;
            case NE:
                return EQ;
            case LE:
                return GT;
            case LT:
                return GE;
            case GE:
                return LT;
            case GT:
                return LE;
            default:
                throw new RuntimeException("This should be unreachable");
        }
    }

    /**
     * Tests the validity from the  x [THIS] y predicate.
     *
     * @param x the first operand of the binary operator
     * @param y the second operand of the binary operator
     *
     * @return true iff x [THIS] y is true. False otherwise.
     */
    public boolean check(final int x, final int y) {
        switch (this) {
            case EQ:
                return x == y;
            case NE:
                return x != y;
            case LE:
                return x <= y;
            case LT:
                return x <  y;
            case GE:
                return x >= y;
            case GT:
                return x >  y;
            default:
                throw new RuntimeException("This should be unreachable");
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        switch (this) {
            case EQ:
                return "==";
            case NE:
                return "!=";
            case LE:
                return "<=";
            case LT:
                return "<";
            case GE:
                return ">=";
            case GT:
                return ">";
            default:
                throw new RuntimeException("This should be unreachable");
        }
    }
}
