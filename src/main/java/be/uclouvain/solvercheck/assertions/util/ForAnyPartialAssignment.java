package be.uclouvain.solvercheck.assertions.util;

import be.uclouvain.solvercheck.assertions.Assertion;
import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.randomness.Randomness;
import be.uclouvain.solvercheck.generators.GeneratorsDSL;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A helper class that facilitates the writing of properties bearing on
 * partial assigments (made of several components).
 */
public final class ForAnyPartialAssignment implements Assertion {
    /** The builder for the generator used to produce the partial assignments. */
    private final GeneratorsDSL.GenSinglePartialAssignment builder;
    /** The actual assertion, which must be fed with a partial assignment. */
    private final Function<PartialAssignment, Assertion> assertFn;
    /** A predicate used to filter out irrelevant partial assignments. */
    private Predicate<PartialAssignment> check;

    /**
     * A constructor that wraps a function PartialAssignment -&gt; Assertion.
     *
     * @param fn the PartialAssignment -&gt; Assertion function.
     */
    public ForAnyPartialAssignment(final Function<PartialAssignment, Assertion> fn) {
        builder  = GeneratorsDSL.singlePartialAssignment();
        assertFn = fn;
        check    = pa -> true;
    }

    /**
     * Adds a new condition that must be true for all the generated partial
     * assignments.
     *
     * @param test the new condition.
     * @return this
     */
    public ForAnyPartialAssignment assuming(final Predicate<PartialAssignment> test) {
        check = check.and(test);
        return this;
    }

    /**
     * Adds a component, a list of arguments to the generated partial
     * assignments. One such component is really meant to represent a set of
     * arguments passed to the constructor of an actual constraint. The element
     * constraint is a typical example that illustrates this.
     * The element constraint `X[Y] = Z` has three components:
     * <ul>
     *     <li>X, an array of variables</li>
     *     <li>Y, an index variable</li>
     *     <li>Z, a value variable</li>
     * </ul>
     *
     * A partial assignment for the element constraint can thus be created with
     * <code>
     *  BasicPartialAssignment pa = new BasicPartialAssignment();
     *  pa.addComponent(x);
     *  pa.addComponent(y);
     *  pa.addComponent(z);
     * </code>
     *
     * @param component the component to add to the current partial assignment.
     * @return the identifier (starts at 1) of the added component.
     */
    public ForAnyPartialAssignment with(final List<Domain> component) {
        builder.with(component);
        return this;
    }

    /**
     * Adds a component, a list of arguments to the generated partial
     * assignments. One such component is really meant to represent a set of
     * arguments passed to the constructor of an actual constraint. The element
     * constraint is a typical example that illustrates this.
     * The element constraint `X[Y] = Z` has three components:
     * <ul>
     *     <li>X, an array of variables</li>
     *     <li>Y, an index variable</li>
     *     <li>Z, a value variable</li>
     * </ul>
     *
     * A partial assignment for the element constraint can thus be created with
     * <code>
     *  BasicPartialAssignment pa = new BasicPartialAssignment();
     *  pa.addComponent(x);
     *  pa.addComponent(y);
     *  pa.addComponent(z);
     * </code>
     *
     * @param component the component to add to the current partial assignment.
     * @return the identifier (starts at 1) of the added component.
     */
    public ForAnyPartialAssignment with(final Domain[] component) {
        builder.with(component);
        return this;
    }

    /**
     * Adds a component, a list of arguments to the generated partial
     * assignments. One such component is really meant to represent a set of
     * arguments passed to the constructor of an actual constraint. The element
     * constraint is a typical example that illustrates this.
     * The element constraint `X[Y] = Z` has three components:
     * <ul>
     *     <li>X, an array of variables</li>
     *     <li>Y, an index variable</li>
     *     <li>Z, a value variable</li>
     * </ul>
     *
     * A partial assignment for the element constraint can thus be created with
     * <code>
     *  BasicPartialAssignment pa = new BasicPartialAssignment();
     *  pa.addComponent(x);
     *  pa.addComponent(y);
     *  pa.addComponent(z);
     * </code>
     *
     * @param component the component to add to the current partial assignment.
     * @return the identifier (starts at 1) of the added component.
     */
    public ForAnyPartialAssignment with(final Domain component) {
        builder.with(component);
        return this;
    }

    /**
     * Adds a component, a list of arguments to the generated partial
     * assignments. One such component is really meant to represent a set of
     * arguments passed to the constructor of an actual constraint. The element
     * constraint is a typical example that illustrates this.
     * The element constraint `X[Y] = Z` has three components:
     * <ul>
     *     <li>X, an array of variables</li>
     *     <li>Y, an index variable</li>
     *     <li>Z, a value variable</li>
     * </ul>
     *
     * A partial assignment for the element constraint can thus be created with
     * <code>
     *  BasicPartialAssignment pa = new BasicPartialAssignment();
     *  pa.addComponent(x);
     *  pa.addComponent(y);
     *  pa.addComponent(z);
     * </code>
     *
     * @param component the component to add to the current partial assignment.
     * @return the identifier (starts at 1) of the added component.
     */
    public ForAnyPartialAssignment with(final int component) {
        builder.with(component);
        return this;
    }

    /**
     * Adds a component, a list of arguments to the generated partial
     * assignments. One such component is really meant to represent a set of
     * arguments passed to the constructor of an actual constraint. The element
     * constraint is a typical example that illustrates this.
     * The element constraint `X[Y] = Z` has three components:
     * <ul>
     *     <li>X, an array of variables</li>
     *     <li>Y, an index variable</li>
     *     <li>Z, a value variable</li>
     * </ul>
     *
     * A partial assignment for the element constraint can thus be created with
     * <code>
     *  BasicPartialAssignment pa = new BasicPartialAssignment();
     *  pa.addComponent(x);
     *  pa.addComponent(y);
     *  pa.addComponent(z);
     * </code>
     *
     * @param component the component to add to the current partial assignment.
     * @return the identifier (starts at 1) of the added component.
     */
    public ForAnyPartialAssignment then(final List<Domain> component) {
        builder.then(component);
        return this;
    }

    /**
     * Adds a component, a list of arguments to the generated partial
     * assignments. One such component is really meant to represent a set of
     * arguments passed to the constructor of an actual constraint. The element
     * constraint is a typical example that illustrates this.
     * The element constraint `X[Y] = Z` has three components:
     * <ul>
     *     <li>X, an array of variables</li>
     *     <li>Y, an index variable</li>
     *     <li>Z, a value variable</li>
     * </ul>
     *
     * A partial assignment for the element constraint can thus be created with
     * <code>
     *  BasicPartialAssignment pa = new BasicPartialAssignment();
     *  pa.addComponent(x);
     *  pa.addComponent(y);
     *  pa.addComponent(z);
     * </code>
     *
     * @param component the component to add to the current partial assignment.
     * @return the identifier (starts at 1) of the added component.
     */
    public ForAnyPartialAssignment then(final Domain[] component) {
        builder.then(component);
        return this;
    }

    /**
     * Adds a component, a list of arguments to the generated partial
     * assignments. One such component is really meant to represent a set of
     * arguments passed to the constructor of an actual constraint. The element
     * constraint is a typical example that illustrates this.
     * The element constraint `X[Y] = Z` has three components:
     * <ul>
     *     <li>X, an array of variables</li>
     *     <li>Y, an index variable</li>
     *     <li>Z, a value variable</li>
     * </ul>
     *
     * A partial assignment for the element constraint can thus be created with
     * <code>
     *  BasicPartialAssignment pa = new BasicPartialAssignment();
     *  pa.addComponent(x);
     *  pa.addComponent(y);
     *  pa.addComponent(z);
     * </code>
     *
     * @param component the component to add to the current partial assignment.
     * @return the identifier (starts at 1) of the added component.
     */
    public ForAnyPartialAssignment then(final Domain component) {
        builder.then(component);
        return this;
    }

    /**
     * Adds a component, a list of arguments to the generated partial
     * assignments. One such component is really meant to represent a set of
     * arguments passed to the constructor of an actual constraint. The element
     * constraint is a typical example that illustrates this.
     * The element constraint `X[Y] = Z` has three components:
     * <ul>
     *     <li>X, an array of variables</li>
     *     <li>Y, an index variable</li>
     *     <li>Z, a value variable</li>
     * </ul>
     *
     * A partial assignment for the element constraint can thus be created with
     * <code>
     *  BasicPartialAssignment pa = new BasicPartialAssignment();
     *  pa.addComponent(x);
     *  pa.addComponent(y);
     *  pa.addComponent(z);
     * </code>
     *
     * @param component the component to add to the current partial assignment.
     * @return the identifier (starts at 1) of the added component.
     */
    public ForAnyPartialAssignment then(final int component) {
        builder.then(component);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public void check(final Randomness rnd) {
        builder.build()
           .generate(rnd)
           .filter(check)
           .forEach(pa -> assertFn.apply(pa).check(rnd));
    }
}
