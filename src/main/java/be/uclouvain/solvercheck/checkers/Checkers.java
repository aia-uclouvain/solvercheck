package be.uclouvain.solvercheck.checkers;

import be.uclouvain.solvercheck.core.Checker;

import static be.uclouvain.solvercheck.utils.Utils.isValidIndex;

public final class Checkers {
    private Checkers() {
    }

    public static Checker alwaysTrue() {
        return s -> true;
    }

    public static Checker alwaysFalse() {
        return s -> false;
    }

    public static Checker allDiff() {
        return s -> s.asSet().size() == s.size();
    }

    public static Checker element(int index, int value) {
        return s -> isValidIndex(index, s.size()) && s.asArray()[index] == value;
    }

    // TODO: gcc, gccVar, sum, table
}
