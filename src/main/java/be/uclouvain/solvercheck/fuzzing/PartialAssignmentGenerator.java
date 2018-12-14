package be.uclouvain.solvercheck.fuzzing;

import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.data.impl.BasicPartialAssignment;

import java.util.ArrayList;
import java.util.List;

public final class PartialAssignmentGenerator extends BaseGenerator<PartialAssignment> {
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

    public PartialAssignmentGenerator addListComponent(final Generator<List<Domain>> component) {
        delegates.add(component);
        return this;
    }

    public PartialAssignmentGenerator addArrayComponent(final Generator<Domain[]> component) {
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

    public PartialAssignmentGenerator addSingleComponent(final Generator<Domain> component) {
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

    public PartialAssignmentGenerator addFixedComponent(final Generator<Integer> component) {
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
