package be.uclouvain.solvercheck.core.data;

/**
 * This enumeration describes the five 'primitive' comparison operators.
 */
public enum Operator {
    /** EQ means **equality** operator. */
    EQ,
    /** NE means **different from** operator. */
    NE,
    /** LE means lesser or equal (&lt;=) operator. */
    LE,
    /** LT means strictly lesser than (&lt;) operator. */
    LT,
    /** GE means greater or equal to (&gt;=) operator. */
    GE,
    /** GT means strictly greater than (&gt;) operator. */
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
    public boolean check(final Number x, final Number y) {
        switch (this) {
            case EQ:
                return x.longValue() == y.longValue();
            case NE:
                return x.longValue() != y.longValue();
            case LE:
                return x.longValue() <= y.longValue();
            case LT:
                return x.longValue() <  y.longValue();
            case GE:
                return x.longValue() >= y.longValue();
            case GT:
                return x.longValue() >  y.longValue();
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
