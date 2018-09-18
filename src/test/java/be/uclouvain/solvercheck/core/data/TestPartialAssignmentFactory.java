package be.uclouvain.solvercheck.core.data;

import be.uclouvain.solvercheck.utils.collections.CartesianProduct;
import org.junit.Test;
import org.quicktheories.WithQuickTheories;

import static be.uclouvain.solvercheck.generators.Generators.partialAssignments;

public class TestPartialAssignmentFactory implements WithQuickTheories {

    @Test
    public void unionOfTheCartesianProductMustEqualOriginalPartialAssignmentWhenNoDomainIsEmpty() {
        qt().forAll(partialAssignments().build())
            .assuming(x -> x.stream().noneMatch(Domain::isEmpty))
            .check   (x ->
                x.equals(PartialAssignment.unionOf(
                        x.size(),
                        CartesianProduct.of(x)))
            );
    }

}
