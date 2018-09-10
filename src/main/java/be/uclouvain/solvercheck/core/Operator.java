package be.uclouvain.solvercheck.core;

public enum Operator {
    EQ, NE, LE, LT, GE, GT;

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

    public boolean check(int x, int y) {
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

    public static Operator from(int i) {
        return values()[i%values().length];
    }
}
