package be.uclouvain.solvercheck.core.data;

import be.uclouvain.solvercheck.core.data.impl.AssignmentFactory;
import be.uclouvain.solvercheck.core.data.impl.DomainFactory;
import be.uclouvain.solvercheck.core.data.impl.PartialAssignmentFactory;
import be.uclouvain.solvercheck.generators.Generators;
import be.uclouvain.solvercheck.utils.Utils;
import org.junit.Test;
import org.quicktheories.WithQuickTheories;
import org.quicktheories.core.Gen;

import java.util.Collection;
import java.util.List;

import static be.uclouvain.solvercheck.core.data.Operator.NE;
import static be.uclouvain.solvercheck.generators.Generators.domains;
import static be.uclouvain.solvercheck.utils.Utils.*;
import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.*;

public class TestPartialAssignment implements WithQuickTheories {

    @Test
    public void testSize() {
        qt().forAll(lists().of(domains().build()).ofSizeBetween(0, 10))
            .check(a -> a.size() == PartialAssignmentFactory.from(a).size()
        );
    }

    @Test
    public void getReturnsTheIthElementIfItsAValidIndex(){
        qt().forAll(lists().of(domains().build()).ofSizeBetween(1, 10))
            .checkAssert(domains ->
                    qt().forAll(integers().between(0, domains.size()-1))
                        .check(index -> domains.get(index).equals(PartialAssignmentFactory.from(domains).get(index)))
            );
    }
    @Test
    public void getFailsWithAnExceptionWhenGivenAWrongIndex(){
        qt().forAll(lists().of(domains().build()).ofSizeBetween(1, 10))
            .checkAssert(domains ->
                qt().forAll(integers().between(-100, -1))
                    .check(index ->
                        failsThrowing(IndexOutOfBoundsException.class,
                                      () -> PartialAssignmentFactory.from(domains).get(index))
                    ));

        qt().forAll(lists().of(domains().build()).ofSizeBetween(1, 10))
            .checkAssert(domains ->
                qt().forAll(integers().between(domains.size()+1, domains.size()+100))
                        .check(index ->
                                failsThrowing(IndexOutOfBoundsException.class,
                                        () -> PartialAssignmentFactory.from(domains).get(index))
                        ));
    }

    @Test
    public void restrictFailsWhenGivenAWrongVariable() {
        qt().forAll(notFailedPartialAssignments(), integers().between(-1, 10), integers().between(-10, 10))
            .check((ass, var, val) ->
                    isValidVarIndex(var, ass)
                 || failsThrowing(IndexOutOfBoundsException.class,
                            () -> PartialAssignmentFactory.restrict(ass, var, NE, val))
            );
    }

    @Test
    public void restrictReturnsSelfWhenGivenAWrongValue() {
        qt().withGenerateAttempts(10000)
            .forAll(partialAssignments(), integers().between(0, 10), integers().between(-10, 10))
            .assuming((ass, var, val) -> isValidVarIndex(var, ass) && !ass.get(var).contains(val))
            .check(   (ass, var, val) -> PartialAssignmentFactory.restrict(ass, var, NE, val) == ass );
    }

    @Test
    public void restrictReturnsAProperSubAssignment() {
        qt().forAll(nonEmptyAssignments())
            .checkAssert(ass ->
                qt().forAll(integers().between(0, ass.size()-1), integers().all())
                    .check((var, val) -> {
                        boolean isProper = true;
                        PartialAssignment modified  = PartialAssignmentFactory.restrict(ass, var, NE, val);
                        for(int i = 0; isProper && i < ass.size(); i++) {
                            if( i == var) {
                                isProper &= DomainFactory.restrict(ass.get(i), NE, val).equals(modified.get(i));
                            } else {
                                isProper &= ass.get(i).equals(modified.get(i));
                            }
                        }
                        return isProper;
                    })
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
    public void testIsComplete() {
        qt().forAll(partialAssignments()).check(a ->
                a.isComplete() == a.stream().allMatch(Domain::isFixed)
        );
    }
    @Test
    public void testIsLeaf() {
        qt().forAll(partialAssignments()).check(a ->
                a.isLeaf() == (a.stream().allMatch(Domain::isFixed) || a.stream().anyMatch(Domain::isEmpty))
        );
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

    @Test
    public void whenAllDomainsAreFixedAPartialAssignmentCanBeSeenAsTheCorrespondingAssignment() {
        qt().forAll(lists().of(integers().all()).ofSizeBetween(0, 20))
            .check( lst ->
                PartialAssignmentFactory.unionOf(List.of(lst))
                        .asAssignment()
                        .equals(AssignmentFactory.from(lst))
                &&
                // check it in both directions
                AssignmentFactory.from(lst)
                        .equals(PartialAssignmentFactory.unionOf(List.of(lst)).asAssignment())
            );
    }

    @Test
    public void oneCannotCallAsAssignmentWhenNotAllDomainsAreFixed() {
        qt().forAll(partialAssignments())
            .assuming(x -> x.stream().anyMatch(dom -> !dom.isFixed()))
            .check(x -> failsThrowing(IllegalStateException.class, ()->x.asAssignment()));
    }

    private boolean isValidVarIndex(int i, Collection<?> a) {
        return isValidIndex(i, a.size());
    }

    private Gen<PartialAssignment> notFailedPartialAssignments() {
        return Generators.partialAssignments()
                .withVariablesRanging(1, 10)
                .withDomainsOfSizeBetween(1, 10)
                .withValuesRanging(-10, 10)
                .build();
    }

    private Gen<PartialAssignment> nonEmptyAssignments() {
        return Generators.partialAssignments()
                .withVariablesRanging(1, 10)
                .withDomainsOfSizeUpTo(10)
                .withValuesRanging(-10, 10)
                .build();
    }
    private Gen<PartialAssignment> partialAssignments() {
        return Generators.partialAssignments()
                .withUpToVariables(5)
                .withDomainsOfSizeUpTo(10)
                .withValuesRanging(-10, 10)
                .build();
    }

    private Gen<List<Integer>> listOfInt() {
        return lists().of(integers().all()).ofSizeBetween(0, 100);
    }
    private Gen<List<Domain>> listOfDomains() {
        return lists().of(domains().build()).ofSizeBetween(0, 100);
    }
}
