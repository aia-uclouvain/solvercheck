package be.uclouvain.solvercheck.pbt;

public abstract class BaseGenerator<T> implements Generator<T> {
    private final String name;

    public BaseGenerator(final String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }
}
