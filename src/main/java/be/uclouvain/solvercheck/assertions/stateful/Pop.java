package be.uclouvain.solvercheck.assertions.stateful;

import be.uclouvain.solvercheck.core.task.StatefulFilter;

final class Pop implements DiveOperation {
    private static final Pop INSTANCE = new Pop();

    private Pop() { }

    @Override
    public String toString() {
        return "Pop";
    }

    public static Pop getInstance() {
        return INSTANCE;
    }
}
