package be.uclouvain.solvercheck.core.data;

import be.uclouvain.solvercheck.WithSolverCheck;
import be.uclouvain.solvercheck.generators.GenBuilder;
import be.uclouvain.solvercheck.utils.Utils;
import be.uclouvain.solvercheck.utils.collections.CartesianProduct;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static be.uclouvain.solvercheck.core.data.Operator.NE;
import static be.uclouvain.solvercheck.utils.Utils.domainIsStronger;
import static be.uclouvain.solvercheck.utils.Utils.domainsAreEquivalent;
import static be.uclouvain.solvercheck.utils.Utils.failsThrowing;
import static be.uclouvain.solvercheck.utils.Utils.isValidIndex;
import static be.uclouvain.solvercheck.utils.Utils.zip;
import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.EQUIVALENT;
import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.INCOMPARABLE;
import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.STRONGER;
import static be.uclouvain.solvercheck.utils.relations.PartialOrdering.WEAKER;

public class TestPartialAssignment implements WithSolverCheck {

    @Test
    public void testIsComplete() {
        assertThat(forAll(partialAssignment()).itIsTrueThat(a ->
            a.isComplete() == a.stream().allMatch(Domain::isFixed)
        ));
    }
    @Test
    public void testIsError() {
        assertThat(forAll(partialAssignment()).itIsTrueThat(a ->
          a.isError() == a.stream().anyMatch(Domain::isEmpty)
        ));
    }
    @Test
    public void testIsLeaf() {
        assertThat(forAll(partialAssignment()).itIsTrueThat(a ->
             a.isLeaf() == (a.stream().allMatch(Domain::isFixed)
           || a.stream().anyMatch(Domain::isEmpty))
        ));
    }

    @Test
    public void whenAllDomainsAreFixedAPartialAssignmentCanBeSeenAsTheCorrespondingAssignment() {
        assertThat(forAll(listOf(integer())).itIsTrueThat(lst ->
                PartialAssignment.unionOf(lst.size(), List.of(lst))
                        .asAssignment()
                        .equals(Assignment.from(lst))
                &&
                // check it in both directions
                Assignment.from(lst)
                        .equals(PartialAssignment.unionOf(lst.size(),List.of(lst)).asAssignment())
        ));
    }

    @Test
    public void oneCannotCallAsAssignmentWhenNotAllDomainsAreFixed() {
        assertThat(
           forAll(partialAssignment())
                .assuming(x -> x.stream().anyMatch(dom -> !dom.isFixed()))
                .itIsTrueThat(x ->
                   failsThrowing(IllegalStateException.class,
                   () -> x.asAssignment()))
        );
    }

    @Test
    public void testSize() {
        assertThat(forAll(partialAssignment()).itIsTrueThat(a ->
           a.size() == PartialAssignment.from(a).size()
        ));
    }

    @Test
    public void getReturnsTheIthElementIfItsAValidIndex(){
        assertThat(
           forAll(listsOfDomains())
              .assuming(domains -> domains.size() >= 1)
              .assertThat(domains ->
           forAll(integer().between(0, domains.size()-1))
           .itIsTrueThat(index -> domains.get(index).equals(PartialAssignment.from(domains).get(index)))
        ));
    }
    @Test
    public void getFailsWithAnExceptionWhenGivenAWrongIndex(){
        assertThat(
           forAll(listsOfDomains()).assertThat(domains ->
           forAll(integer().between(-100, -1))
           .itIsTrueThat(index ->
                failsThrowing(IndexOutOfBoundsException.class,
                              () -> PartialAssignment.from(domains).get(index))
            )));

        assertThat(
           forAll(listsOfDomains()).assertThat(domains ->
           forAll(integer().between(domains.size()+1, domains.size()+100))
           .itIsTrueThat(index ->
                failsThrowing(IndexOutOfBoundsException.class,
                        () -> PartialAssignment.from(domains).get(index))
            )));
    }

    @Test
    public void testFromList() {
        assertThat(forAll(listsOfDomains()).itIsTrueThat(lst ->
           lst.equals(PartialAssignment.from(lst)
       )));
    }

    @Test
    public void testFromArray() {
        assertThat(
           forAll(listsOfDomains()).itIsTrueThat(domains -> {
              Domain[] array = domains.toArray(new Domain[0]);
              return domains.equals(PartialAssignment.from(array));
        }));
    }

    // ERROR
    @Test
    public void testError() {
        assertThat(forAll(integer().between(0, 1000)).itIsTrueThat(arity -> {
           PartialAssignment result = PartialAssignment.error(arity);

           return result.size() == arity
              && result.stream().allMatch(Domain::isEmpty);
       }));
    }

    @Test
    public void errorMustRejectNegativeArity() {
        assertThat(
           forAll(
              integer().between(-1000, -2))
              .itIsTrueThat(arity ->
                failsThrowing(
                    IllegalArgumentException.class,
                    () -> PartialAssignment.error(arity)))
        );
    }


    @Test
    public void restrictFailsWhenGivenAWrongVariable() {
        assertThat(
           forAll(partialAssignment()).assuming(pa -> !pa.isError()).assertThat(pa ->
           forAll(
              integer("vars").between(-1, 10),
              integer("val").between(-10, 10)
           )
           .itIsTrueThat((var, val) ->
              isValidVarIndex(var, pa)
                 || failsThrowing(IndexOutOfBoundsException.class,
                 () -> PartialAssignment.restrict(pa, var, NE, val)
           )))
        );
    }

    @Test
    public void restrictReturnsSelfWhenGivenAWrongValue() {
        assertThat(
           forAll(partialAssignment().withVariablesBetween(1, 10)).assertThat(ass ->
           forAll(
              integer("Var").between(0, 10),
              integer("Val").between(-10, 10)
           )
          .assuming((var, val) -> isValidVarIndex(var, ass) && !ass.get(var).contains(val))
          .itIsTrueThat((var, val) -> PartialAssignment.restrict(ass, var, NE, val) == ass )
       ));
    }

    @Test
    public void restrictReturnsAProperSubAssignment() {
        assertThat(
           forAll(partialAssignment().withVariablesBetween(1, 5)).assertThat(ass ->
           forAll(integer("X").between(0, ass.size()-1), integer("Y"))
           .itIsTrueThat((var, val) -> {
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
            })));
    }

    // COLLECTOR
    @Test
    public void testCollector() {
        assertThat(forAll(listsOfDomains()).itIsTrueThat(lst -> {
            PartialAssignment d = lst.stream().collect(PartialAssignment.collector());

            return d.containsAll(lst) && lst.containsAll(d);
        }));
    }

    // UNION OF
    @Test
    public void unionOfTheCartesianProductMustEqualOriginalPartialAssignmentWhenNoDomainIsEmpty() {
        assertThat(forAll(partialAssignment())
            .assuming(pa -> pa.stream().noneMatch(Domain::isEmpty))
            .itIsTrueThat(
                pa -> pa.equals(PartialAssignment.unionOf(
                        pa.size(),
                        CartesianProduct.of(pa)))
            ));
    }

    @Test
    public void unionOfTheCartesianProductMustYieldAnEmptyPartialAssignmentOfTheGivenAriry() {
        assertThat(forAll(partialAssignment())
            .assuming(PartialAssignment::isError)
            .itIsTrueThat(
                partialAssignment -> {
                    CartesianProduct<Integer> cp =
                            CartesianProduct.of(partialAssignment);

                    PartialAssignment pa =
                            PartialAssignment.unionOf(partialAssignment.size(), cp);

                    boolean sameSize = pa.size() == partialAssignment.size();
                    boolean allEmpty = pa.stream().allMatch(Domain::isEmpty);

                    return sameSize && allEmpty;
                }
            ));
    }

    @Test
    public void compareWithReturnsIncomparableWhenPAHaveDifferentArity() {
        assertThat(
            forAll(partialAssignment(), partialAssignment())
            .assuming    ((a, b) -> a.size() != b.size())
            .itIsTrueThat((a, b) -> a.compareWith(b) == INCOMPARABLE)
        );
    }

    @Test
    public void testIncomparable() {
        assertThat(
           forAll  (partialAssignment(), partialAssignment())
          .assuming((a, b) -> a.size() == b.size() )
          .itIsTrueThat((a, b) -> {
                boolean isIncomparable         = a.compareWith(b) == INCOMPARABLE;
                boolean hasIncomparableDomains = zip(a, b).stream().anyMatch(Utils::domainsAreIncomparable);
                boolean hasStrongerDomain      = zip(a, b).stream().anyMatch(Utils::domainIsStronger);
                boolean hasWeakerDomain        = zip(a, b).stream().anyMatch(Utils::domainIsWeaker);

                return isIncomparable == (hasIncomparableDomains || (hasStrongerDomain && hasWeakerDomain));
            }));
    }

    @Test
    public void testEquivalent() {
       assertThat(
           forAll  (partialAssignment(), partialAssignment())
          .assuming((a, b) -> a.size() == b.size() )
          .itIsTrueThat((a, b) -> {
            boolean isEquivalent  = a.compareWith(b) == EQUIVALENT;
            boolean allEquivalents= zip(a, b).stream().allMatch(Utils::domainsAreEquivalent);

            return isEquivalent == allEquivalents;
        }));
    }
    @Test
    public void testStronger() {
        assertThat(
           forAll  (partialAssignment(), partialAssignment())
              .assuming((a, b) -> a.size() == b.size() )
              .itIsTrueThat((a, b) -> {
                boolean isStronger         = a.compareWith(b) == STRONGER;
                boolean hasStrongerDomain  = zip(a, b).stream().anyMatch(Utils::domainIsStronger);
                boolean allEquivOrStronger = zip(a, b).stream().allMatch(e-> domainsAreEquivalent(e) || domainIsStronger(e));

                return isStronger == (hasStrongerDomain && allEquivOrStronger);
            }));
    }
    @Test
    public void testWeaker() {
        assertThat(
           forAll  (partialAssignment(), partialAssignment())
              .assuming((a, b) -> a.size() == b.size() )
              .itIsTrueThat((a, b) -> {
                boolean isWeaker         = a.compareWith(b) == WEAKER;
                boolean hasWeakerDomain  = zip(a, b).stream().anyMatch(Utils::domainIsWeaker);
                boolean allEquivOrWeaker = zip(a, b).stream().allMatch(e-> domainsAreEquivalent(e) || Utils.domainIsWeaker(e));

                return isWeaker == (hasWeakerDomain && allEquivOrWeaker);
            }));
    }


    @Test
    public void testEqualsIffEquivalent() {
        assertThat(forAll(partialAssignment(), partialAssignment())
        .itIsTrueThat((a, b) -> a.equals(b) == (a.compareWith(b) == EQUIVALENT)));
    }

    @Test
    public void testHashCode() {
        assertThat(forAll(partialAssignment(), partialAssignment())
        .itIsTrueThat((a, b) -> !a.equals(b) || (a.hashCode() == b.hashCode())));
    }

    private boolean isValidVarIndex(int i, Collection<?> a) {
        return isValidIndex(i, a.size());
    }


    private GenBuilder<List<Domain>> listsOfDomains() {
        return listOf(domain().withValuesBetween(0, 10));
    }
}
