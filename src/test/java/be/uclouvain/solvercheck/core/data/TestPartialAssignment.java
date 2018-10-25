package be.uclouvain.solvercheck.core.data;

import be.uclouvain.solvercheck.assertions.util.ForAnyPartialAssignment;
import be.uclouvain.solvercheck.generators.Generators;
import be.uclouvain.solvercheck.generators.WithCpGenerators;
import be.uclouvain.solvercheck.utils.Utils;
import be.uclouvain.solvercheck.utils.collections.CartesianProduct;
import org.junit.Test;
import org.quicktheories.WithQuickTheories;
import org.quicktheories.core.Gen;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static be.uclouvain.solvercheck.core.data.Operator.NE;
import static be.uclouvain.solvercheck.utils.Utils.domainIsStronger;
import static be.uclouvain.solvercheck.utils.Utils.domainIsWeaker;
import static be.uclouvain.solvercheck.utils.Utils.domainsAreEquivalent;
import static be.uclouvain.solvercheck.utils.Utils.failsThrowing;
import static be.uclouvain.solvercheck.utils.Utils.isValidIndex;
import static be.uclouvain.solvercheck.utils.Utils.zip;
import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.EQUIVALENT;
import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.INCOMPARABLE;
import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.STRONGER;
import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.WEAKER;

public class TestPartialAssignment
        implements WithQuickTheories, WithCpGenerators {

    @Test
    public void testIsComplete() {
        new ForAnyPartialAssignment()
            .check(
                a -> a.isComplete() == a.stream().allMatch(Domain::isFixed)
            );
    }
    @Test
    public void testIsError() {
        new ForAnyPartialAssignment()
            .check(
                a -> a.isError() == a.stream().anyMatch(Domain::isEmpty)
            );
    }
    @Test
    public void testIsLeaf() {
        new ForAnyPartialAssignment()
            .check(
                a -> a.isLeaf() == (a.stream().allMatch(Domain::isFixed)
                  || a.stream().anyMatch(Domain::isEmpty))
            );
    }

    @Test
    public void whenAllDomainsAreFixedAPartialAssignmentCanBeSeenAsTheCorrespondingAssignment() {
        qt().forAll(lists().of(integers().all()).ofSizeBetween(0, 20))
                .check( lst ->
                        PartialAssignment.unionOf(lst.size(), List.of(lst))
                                .asAssignment()
                                .equals(Assignment.from(lst))
                                &&
                                // check it in both directions
                                Assignment.from(lst)
                                        .equals(PartialAssignment.unionOf(lst.size(),
                                                List.of(lst)).asAssignment())
                );
    }

    @Test
    public void oneCannotCallAsAssignmentWhenNotAllDomainsAreFixed() {
        qt().withGenerateAttempts(10000)
                .forAll(partialAssignments())
                .assuming(x -> x.stream().anyMatch(dom -> !dom.isFixed()))
                .check(x -> failsThrowing(IllegalStateException.class, ()->x.asAssignment()));
    }

    @Test
    public void testSize() {
        qt().forAll(lists().of(domains().build()).ofSizeBetween(0, 10))
            .check(a -> a.size() == PartialAssignment.from(a).size()
        );
    }

    @Test
    public void getReturnsTheIthElementIfItsAValidIndex(){
        qt().forAll(lists().of(domains().build()).ofSizeBetween(1, 10))
            .checkAssert(domains ->
                    qt().forAll(integers().between(0, domains.size()-1))
                        .check(index -> domains.get(index).equals(PartialAssignment.from(domains).get(index)))
            );
    }
    @Test
    public void getFailsWithAnExceptionWhenGivenAWrongIndex(){
        qt().forAll(lists().of(domains().build()).ofSizeBetween(1, 10))
            .checkAssert(domains ->
                qt().forAll(integers().between(-100, -1))
                    .check(index ->
                        failsThrowing(IndexOutOfBoundsException.class,
                                      () -> PartialAssignment.from(domains).get(index))
                    ));

        qt().forAll(lists().of(domains().build()).ofSizeBetween(1, 10))
            .checkAssert(domains ->
                qt().forAll(integers().between(domains.size()+1, domains.size()+100))
                        .check(index ->
                                failsThrowing(IndexOutOfBoundsException.class,
                                        () -> PartialAssignment.from(domains).get(index))
                        ));
    }

    @Test
    public void testFromList() {
        qt().forAll(lists().of(domains()).ofSizeBetween(0, 1000))
            .check(lst -> lst.equals(PartialAssignment.from(lst)));
    }

    @Test
    public void testFromArray() {
        qt().forAll(
           arrays().ofClass(domains(), Domain.class).withLengthBetween(0, 1000))
           .check(array ->
              Arrays.asList(array).equals(PartialAssignment.from(array))
           );
    }

    // ERROR
    @Test
    public void testError() {
        qt().forAll(integers().between(0, 1000))
           .check(arity -> {
               PartialAssignment result = PartialAssignment.error(arity);

               return result.size() == arity
                  && result.stream().allMatch(Domain::isEmpty);
           });
    }
    @Test
    public void errorMustRejectNegativeArity() {
        qt().forAll(integers().between(-1000, -1))
           .check(arity ->
              failsThrowing(
                 IllegalArgumentException.class,
                 () -> PartialAssignment.error(arity)));
    }


    @Test
    public void restrictFailsWhenGivenAWrongVariable() {
        qt().withGenerateAttempts(10000)
            .forAll(notFailedPartialAssignments(), integers().between(-1, 10), integers().between(-10, 10))
            .check((ass, var, val) ->
                    isValidVarIndex(var, ass)
                 || failsThrowing(IndexOutOfBoundsException.class,
                            () -> PartialAssignment.restrict(ass, var, NE, val))
            );
    }

    @Test
    public void restrictReturnsSelfWhenGivenAWrongValue() {
        qt().withGenerateAttempts(10000)
            .forAll(partialAssignments(), integers().between(0, 10), integers().between(-10, 10))
            .assuming((ass, var, val) -> isValidVarIndex(var, ass) && !ass.get(var).contains(val))
            .check(   (ass, var, val) -> PartialAssignment.restrict(ass, var, NE, val) == ass );
    }

    @Test
    public void restrictReturnsAProperSubAssignment() {
        qt().forAll(nonEmptyAssignments())
            .checkAssert(ass ->
                qt().forAll(integers().between(0, ass.size()-1), integers().all())
                    .check((var, val) -> {
                        boolean isProper = true;
                        PartialAssignment modified  = PartialAssignment.restrict(ass, var, NE, val);
                        for(int i = 0; isProper && i < ass.size(); i++) {
                            if( i == var) {
                                isProper &= Domain.restrict(ass.get(i), NE, val).equals(modified.get(i));
                            } else {
                                isProper &= ass.get(i).equals(modified.get(i));
                            }
                        }
                        return isProper;
                    })
            );
    }

    // COLLECTOR
    @Test
    public void testCollector() {
        qt().forAll(lists().of(domains()).ofSizeBetween(0, 100))
            .check(lst -> {
                PartialAssignment d = lst.stream().collect(PartialAssignment.collector());

                return d.containsAll(lst) && lst.containsAll(d);
            });
    }

    // UNION OF
    @Test
    public void unionOfTheCartesianProductMustEqualOriginalPartialAssignmentWhenNoDomainIsEmpty() {
        new ForAnyPartialAssignment()
            .assuming(pa -> pa.stream().noneMatch(Domain::isEmpty))
            .check(
                pa -> pa.equals(PartialAssignment.unionOf(
                        pa.size(),
                        CartesianProduct.of(pa)))
            );
    }
    @Test
    public void unionOfTheCartesianProductMustYieldAnEmptyPartialAssignmentOfTheGivenAriry() {
        new ForAnyPartialAssignment()
            .assuming(PartialAssignment::isError)
            .check(
                partialAssignment -> {
                    CartesianProduct<Integer> cp =
                            CartesianProduct.of(partialAssignment);

                    PartialAssignment pa =
                            PartialAssignment.unionOf(partialAssignment.size(), cp);

                    boolean sameSize = pa.size() == partialAssignment.size();
                    boolean allEmpty = pa.stream().allMatch(Domain::isEmpty);

                    return sameSize && allEmpty;
                }
            );
    }

    @Test
    public void compareWithReturnsIncomparableWhenPAHaveDifferentArity() {
        qt().withGenerateAttempts(10000)
            .forAll  (partialAssignments(), partialAssignments())
            .assuming((a, b) -> a.size() != b.size())
            .check   ((a, b) -> a.compareWith(b) == INCOMPARABLE);
    }

    @Test
    public void testIncomparable() {
        qt().withGenerateAttempts(10000)
            .forAll  (partialAssignments(), partialAssignments())
            .assuming((a, b) -> a.size() == b.size() )
            .check   ((a, b) -> {
                boolean isIncomparable         = a.compareWith(b) == INCOMPARABLE;
                boolean hasIncomparableDomains = zip(a, b).stream().anyMatch(Utils::domainsAreIncomparable);
                boolean hasStrongerDomain      = zip(a, b).stream().anyMatch(Utils::domainIsStronger);
                boolean hasWeakerDomain        = zip(a, b).stream().anyMatch(Utils::domainIsWeaker);

                return isIncomparable == (hasIncomparableDomains || (hasStrongerDomain && hasWeakerDomain));
            });
    }

    @Test
    public void testEquivalent() {
        qt().withGenerateAttempts(10000)
            .forAll  (partialAssignments(), partialAssignments())
            .assuming((a, b) -> a.size() == b.size() )
            .check   ((a, b) -> {
                boolean isEquivalent  = a.compareWith(b) == EQUIVALENT;
                boolean allEquivalents= zip(a, b).stream().allMatch(Utils::domainsAreEquivalent);

                return isEquivalent == allEquivalents;
            });
    }
    @Test
    public void testStronger() {
        qt().withGenerateAttempts(10000)
            .forAll  (partialAssignments(), partialAssignments())
            .assuming((a, b) -> a.size() == b.size() )
            .check   ((a, b) -> {
                boolean isStronger         = a.compareWith(b) == STRONGER;
                boolean hasStrongerDomain  = zip(a, b).stream().anyMatch(Utils::domainIsStronger);
                boolean allEquivOrStronger = zip(a, b).stream().allMatch(e-> domainsAreEquivalent(e) || domainIsStronger(e));

                return isStronger == (hasStrongerDomain && allEquivOrStronger);
            });
    }
    @Test
    public void testWeaker() {
        qt().withGenerateAttempts(10000)
            .forAll  (partialAssignments(), partialAssignments())
            .assuming((a, b) -> a.size() == b.size() )
            .check   ((a, b) -> {
                boolean isWeaker         = a.compareWith(b) == WEAKER;
                boolean hasWeakerDomain  = zip(a, b).stream().anyMatch(Utils::domainIsWeaker);
                boolean allEquivOrWeaker = zip(a, b).stream().allMatch(e-> domainsAreEquivalent(e) || domainIsWeaker(e));

                return isWeaker == (hasWeakerDomain && allEquivOrWeaker);
            });
    }


    @Test
    public void testEqualsIffEquivalent() {
        qt().forAll(partialAssignments(), partialAssignments())
                .check ((a, b) -> a.equals(b) == (a.compareWith(b) == EQUIVALENT));
    }

    @Test
    public void testHashCode() {
        qt().forAll(partialAssignments(), partialAssignments())
            .check ((a, b) -> !a.equals(b) || (a.hashCode() == b.hashCode()));
    }

    private boolean isValidVarIndex(int i, Collection<?> a) {
        return isValidIndex(i, a.size());
    }

    private Gen<PartialAssignment> notFailedPartialAssignments() {
        return Generators.partialAssignments()
                .withVariablesBetween(1, 10)
                .withDomainsOfSizeUpTo(10)
                .withValuesRanging(-10, 10)
                .build()
                .assuming(pa -> !pa.isError());
    }

    private Gen<PartialAssignment> nonEmptyAssignments() {
        return Generators.partialAssignments()
                .withVariablesBetween(1, 10)
                .withDomainsOfSizeUpTo(10)
                .withValuesRanging(-10, 10)
                .build();
    }
}
