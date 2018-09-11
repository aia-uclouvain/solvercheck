package be.uclouvain.solvercheck.core;

import be.uclouvain.solvercheck.generators.Generators;
import be.uclouvain.solvercheck.utils.Utils;
import com.google.common.collect.Iterables;
import org.junit.Test;
import org.quicktheories.WithQuickTheories;
import org.quicktheories.core.Gen;

import java.util.Set;
import java.util.stream.Collectors;

import static be.uclouvain.solvercheck.core.StrengthComparison.*;
import static be.uclouvain.solvercheck.utils.Utils.*;

public class TestPartialAssignment implements WithQuickTheories {

    @Test
    public void testSize() {
        qt().forAll(partialAssignments()).check(a ->
           a.size() == a.stream().collect(Collectors.toList()).size()
        );
    }

    @Test
    public void getReturnsTheIthElementIffItsAValidIndex(){
        qt().forAll(nonEmptyAssignments(), integers().between(-1, 10))
            .check((a, i) -> !isValidVarIndex(i, a) || a.get(i) == Iterables.get(a, i >= 0 ? i : a.size()+i));
    }

    @Test
    public void getFailsWithExceptionWhenItsNotAValidIndex() {
        qt().forAll(partialAssignments(), integers().between(-1, 10))
            .check((a,i) -> isValidVarIndex(i, a) || failsThrowing(IndexOutOfBoundsException.class, () -> a.get(i) ));
    }

    @Test
    public void removeReturnsSelfWhenGivenAWrongVariable() {
        qt().forAll(partialAssignments(), integers().between(-1, 10), integers().between(-10, 10))
            .check((ass, var, val) ->
                    isValidVarIndex(var, ass)
                 || ass.remove(var, val) == ass);
    }

    @Test
    public void removeReturnsSelfWhenGivenAWrongValue() {
        qt().withGenerateAttempts(10000)
            .forAll(partialAssignments(), integers().between(0, 10), integers().between(-10, 10))
            .assuming((ass, var, val) -> isValidVarIndex(var, ass) && !ass.get(var).contains(val))
            .check(   (ass, var, val) -> ass.remove(var, val) == ass );
    }

    @Test
    public void removeReturnsAProperSubAssignment() {
        qt().forAll(partialAssignments(), integers().between(-1, 10), integers().between(-10, 10))
                .check((ass, var, val) -> {
                    boolean isProper = true;
                    PartialAssignment modified  = ass.remove(var, val);
                    for(int i = 0; isProper && i < ass.size(); i++) {
                        if( i == var) {
                            isProper &= ass.get(i).remove(val).equals(modified.get(i));
                        } else {
                            isProper &= ass.get(i).equals(modified.get(i));
                        }
                    }
                    return isProper;
                });
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
    public void testToString() {
        qt().forAll(partialAssignments())
            .check(a -> a.toString().equals(a.stream().collect(Collectors.toList()).toString()));
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
    public void testUnion() {
        qt().withExamples(3)
            .forAll(partialAssignments())
            .check(a -> {
                Set<Assignment> prod = a.cartesianProduct();
                PartialAssignment.unionOf(prod).equals(a);
                return true;
            });
    }

    private boolean isValidVarIndex(int i, PartialAssignment a) {
        return isValidRelaxIndex(i, a.size());
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

}
