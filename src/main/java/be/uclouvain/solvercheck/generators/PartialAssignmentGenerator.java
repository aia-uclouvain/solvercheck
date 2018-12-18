package be.uclouvain.solvercheck.generators;

import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.data.impl.BasicPartialAssignment;
import be.uclouvain.solvercheck.randomness.Randomness;

import java.util.ArrayList;
import java.util.List;

/**
 * This generator produces streams of random partial assignments.
 * The generated partial assignments are not purely random. The values
 * occurring in the domains follow a multimodal distribution and additional
 * constraints are imposed on the domains. For instance the minimum and maximum
 * values, the maximum distance between any two values of the domain (spread).
 */
public final class PartialAssignmentGenerator extends BaseGenerator<PartialAssignment> {
    /** The generators used to create all the components of the partial assignment. */
    private final List<Generator<List<Domain>>> delegates;

    /**
     * Creates a new instance with a given name.
     *
     * @param name    the name (description) associated to the generated values
     *                in an error report.
     */
    public PartialAssignmentGenerator(final String name) {
        super(name);
        this.delegates = new ArrayList<>();
    }

    /**
     * Adds a component, a list of arguments to the partial assignment.
     * One such component is really meant to represent a set of arguments passed
     * to the constructor of an actual constraint. The element constraint is
     * a typical example that illustrates this. The element constraint
     * `X[Y] = Z` has three components:
     * <ul>
     *     <li>X, an array of variables</li>
     *     <li>Y, an index variable</li>
     *     <li>Z, a value variable</li>
     * </ul>
     *
     * A partial assignment generator for the element constraint can thus be
     * configured with:
     * <code>
     *  PartialAssignmentGenerator pa = new PartialAssignmentGenerator("data");
     *  pa.addListComponent(x);
     *  pa.addSingleComponent(y);
     *  pa.addSingleComponent(z);
     * </code>
     *
     * @param component the component to add to the current partial assignment.
     * @return this
     */
    public PartialAssignmentGenerator addListComponent(
                                      final Generator<List<Domain>> component) {
        delegates.add(component);
        return this;
    }

    /**
     * Adds a component, a list of arguments to the partial assignment.
     * One such component is really meant to represent a set of arguments passed
     * to the constructor of an actual constraint. The element constraint is
     * a typical example that illustrates this. The element constraint
     * `X[Y] = Z` has three components:
     * <ul>
     *     <li>X, an array of variables</li>
     *     <li>Y, an index variable</li>
     *     <li>Z, a value variable</li>
     * </ul>
     *
     * A partial assignment generator for the element constraint can thus be
     * configured with:
     * <code>
     *  PartialAssignmentGenerator pa = new PartialAssignmentGenerator("data");
     *  pa.addListComponent(x);
     *  pa.addSingleComponent(y);
     *  pa.addSingleComponent(z);
     * </code>
     *
     * @param component the component to add to the current partial assignment.
     * @return this
     */
    public PartialAssignmentGenerator addArrayComponent(
                                          final Generator<Domain[]> component) {
        delegates.add(new Generator<List<Domain>>() {
            @Override
            public String name() {
                return component.name();
            }

            @Override
            public List<Domain> item(final Randomness randomness) {
                return List.of(component.item(randomness));
            }
        });
        return this;
    }

    /**
     * Adds a component, a list of arguments to the partial assignment.
     * One such component is really meant to represent a set of arguments passed
     * to the constructor of an actual constraint. The element constraint is
     * a typical example that illustrates this. The element constraint
     * `X[Y] = Z` has three components:
     * <ul>
     *     <li>X, an array of variables</li>
     *     <li>Y, an index variable</li>
     *     <li>Z, a value variable</li>
     * </ul>
     *
     * A partial assignment generator for the element constraint can thus be
     * configured with:
     * <code>
     *  PartialAssignmentGenerator pa = new PartialAssignmentGenerator("data");
     *  pa.addListComponent(x);
     *  pa.addSingleComponent(y);
     *  pa.addSingleComponent(z);
     * </code>
     *
     * @param component the component to add to the current partial assignment.
     * @return this
     */
    public PartialAssignmentGenerator addSingleComponent(
                                            final Generator<Domain> component) {
        delegates.add(new Generator<List<Domain>>() {
            @Override
            public String name() {
                return component.name();
            }

            @Override
            public List<Domain> item(final Randomness randomness) {
                return List.of(component.item(randomness));
            }
        });
        return this;
    }

    /**
     * Adds a component, a list of arguments to the partial assignment.
     * One such component is really meant to represent a set of arguments passed
     * to the constructor of an actual constraint. The element constraint is
     * a typical example that illustrates this. The element constraint
     * `X[Y] = Z` has three components:
     * <ul>
     *     <li>X, an array of variables</li>
     *     <li>Y, an index variable</li>
     *     <li>Z, a value variable</li>
     * </ul>
     *
     * A partial assignment generator for the element constraint can thus be
     * configured with:
     * <code>
     *  PartialAssignmentGenerator pa = new PartialAssignmentGenerator("data");
     *  pa.addListComponent(x);
     *  pa.addSingleComponent(y);
     *  pa.addSingleComponent(z);
     * </code>
     *
     * @param component the component to add to the current partial assignment.
     * @return this
     */
    public PartialAssignmentGenerator addFixedComponent(
                                           final Generator<Integer> component) {
        delegates.add(new Generator<List<Domain>>() {
            @Override
            public String name() {
                return component.name();
            }

            @Override
            public List<Domain> item(final Randomness randomness) {
                return List.of(Domain.from(component.item(randomness)));
            }
        });
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public PartialAssignment item(final Randomness randomness) {
        BasicPartialAssignment pa =  new BasicPartialAssignment();

        for (Generator<List<Domain>> g : delegates) {
            pa.addComponent(g.item(randomness));
        }

        return pa;
    }
}
