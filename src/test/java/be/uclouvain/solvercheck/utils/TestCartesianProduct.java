package be.uclouvain.solvercheck.utils;

import be.uclouvain.solvercheck.WithSolverCheck;
import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.generators.GenBuilder;
import be.uclouvain.solvercheck.fuzzing.Generator;
import be.uclouvain.solvercheck.fuzzing.Randomness;
import be.uclouvain.solvercheck.utils.collections.CartesianProduct;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static be.uclouvain.solvercheck.utils.Utils.failsThrowing;
import static java.lang.Integer.MIN_VALUE;
import static java.util.stream.Collectors.toList;

public class TestCartesianProduct implements WithSolverCheck {

    @Test
    public void TheProductOfAnEmptyListTheUnitProduct() {
        CartesianProduct unitProduct = CartesianProduct.of(new ArrayList<>());

        Assert.assertEquals(1, unitProduct.size());
        Assert.assertEquals(new ArrayList<>(), unitProduct.get(0));
    }

    @Test
    public void theProductMustBeEmptyWhenOneOfTheSetsIsEmpty() {
        assertThat(forAll(listsOfDomains())
            .assuming(l -> l.stream().anyMatch(Domain::isEmpty))
            .itIsTrueThat(l -> CartesianProduct.of(l).isEmpty())
        );
    }

    @Test
    public void theSizeOfTheCartesianProductEqualsTheProductOfTheSizesOfTheSets() {
        assertThat(forAll(listsOfDomains())
        .itIsTrueThat(l -> {
            int actualSize = CartesianProduct.of(l).size();
            int checkSize  = 1;
            for(Set<Integer> s : l) { checkSize *= s.size(); }
            return checkSize == actualSize;
        }));
    }

    @Test
    public void itMustHoldATupleForEachCombinationOfValues(){
        assertThat(forAll(listsOfDomains())
       .assuming  (sets -> sets.stream().allMatch(s -> s.size() > 0))
       .assertThat(sets ->
        forAll(lists().withValuesRanging(0, Integer.MAX_VALUE).ofSize(sets.size()))
       .itIsTrueThat(indices -> {
            List<List<Integer>> lsets = sets.stream().map(ArrayList::new).collect(Collectors.toList());
            List<Integer>  projection = new ArrayList<>();
            for(int i = 0; i < lsets.size(); i++) {
                int index = indices.get(i);
                List<Integer> targetSet = lsets.get(i);
                projection.add( targetSet.get(index % targetSet.size()) );
            }

            return CartesianProduct.of(sets).contains(projection);
        })));
    }

    @Test
    public void testProductIsEmptySize(){
        assertThat(forAll(listsOfDomains())
        .itIsTrueThat(l -> {
            CartesianProduct product = CartesianProduct.of(l);
            return product.isEmpty() == (product.size() == 0);
        }));
    }

    @Test
    public void testIteratorGoesOverAllCombinations(){
        assertThat(forAll(listsOfDomains())
        .itIsTrueThat(sets -> {
            int count = 0;
            CartesianProduct product   = CartesianProduct.of(sets);
            Iterator<List<Integer>> it = product.iterator();

            while(it.hasNext()) { it.next(); count++; }

            return count == product.size();
        }));
    }

    // CONTAINS
    @Test
    public void containsReturnsTrueForAnyCombinationOfActualItems() {
        assertThat(forAll(listsOfDomains())
            .assuming(sets -> sets.stream().noneMatch(Set::isEmpty))
            .itIsTrueThat(sets -> {
                List<Integer> any = sets.stream()
                    .map(set -> set.stream().findAny().get())
                    .collect(Collectors.toList());

                return CartesianProduct.of(sets).contains(any);
            }));
    }
    @Test
    public void containsReturnsFalseForAnyCombinationWithNonItems() {
        assertThat(forAll(listsOfDomains(), integers())
            .assuming((sets, i) -> !sets.isEmpty())
            .assuming((sets, i) -> sets.stream().noneMatch(Set::isEmpty))
            .assuming((sets, i) -> sets.stream().noneMatch(s -> s.contains(i)))
            .itIsTrueThat((sets, i) -> {
                List<Integer> any = sets.stream()
                        .map(set -> set.stream().findAny().get())
                        .collect(Collectors.toList());

                boolean ok = true;
                for (int j = 0; ok && j < sets.size(); j++) {
                    List<Integer> tested = new ArrayList<>(any);
                    tested.set(j, i);
                    ok &= !CartesianProduct.of(sets).contains(tested);
                }

                return ok;
            }));
    }

    @Test
    public void containsReturnsFalseForAnyListOfWrongSize() {
        assertThat(forAll(listsOfDomains())
            .assuming(sets -> sets.stream().noneMatch(Set::isEmpty))
            .itIsTrueThat(sets -> {
                    List<Integer> any = sets.stream()
                            .map(set -> set.stream().findAny().get())
                            .collect(Collectors.toList());

                    any.add(42);
                    return !CartesianProduct.of(sets).contains(any);
            }));
    }

    @Test
    public void containsReturnsFalseForAnEmptyListWhenItIsNotEmpty() {
        assertThat(forAll(listsOfDomains())
            .assuming(sets -> sets.stream().noneMatch(Set::isEmpty))
            .assuming(sets -> !sets.isEmpty())
            .itIsTrueThat(sets ->
                !CartesianProduct.of(sets).contains(List.<Integer>of())
            ));
    }
    @Test
    public void containsReturnsTrueForAnEmptyListWhenItIsEmpty() {
        // because arity is 0
        assertThat(forAll(listsOfDomains())
            .assuming(sets -> sets.isEmpty())
            .itIsTrueThat(sets ->
                    CartesianProduct.of(sets).contains(List.<Integer>of())
            ));
    }

    @Test
    public void containsReturnsFalseForAnEmptyListWhenProductIsEmptyBecauseOfAnEmptySet() {
        // because some domain is empty
        assertThat(forAll(listsOfDomains())
            .assuming(sets -> sets.stream().anyMatch(Set::isEmpty))
            .assuming(sets -> !sets.isEmpty())
            .itIsTrueThat(sets ->
                !CartesianProduct.of(sets).contains(List.<Integer>of())
            ));
    }

    // GET (i in bounds)
    @Test
    public void getReturnsAnItemProvidedIIsInRange() {
        assertThat(forAll(listsOfDomains())
            .assuming(sets -> !CartesianProduct.of(sets).isEmpty())
            .itIsTrueThat(sets -> {
                CartesianProduct product = CartesianProduct.of(sets);
                int i = product.size() -1;

                var item1 = product.get(i);
                var item2 = product.stream().skip(i).findFirst().get();

                return item1.equals(item2);
            }));
    }

    // GET (i out bounds)
    @Test
    public void getFailsWhenIIsLessThanZero() {
        assertThat(forAll(listsOfDomains(), integers().between(MIN_VALUE, 0))
            .assuming((sets, i) -> i < 0)
            .itIsTrueThat((sets, i) ->
                failsThrowing(
                    IndexOutOfBoundsException.class,
                    () -> CartesianProduct.of(sets).get(i))
            ));
    }
    @Test
    public void getFailsWhenIIsLessBiggerThanSize() {
        assertThat(forAll(listsOfDomains(), integers().positive())
                .assuming((sets, i) -> i >= CartesianProduct.of(sets).size())
                .itIsTrueThat((sets, i) ->
                        failsThrowing(
                                IndexOutOfBoundsException.class,
                                () -> CartesianProduct.of(sets).get(i))
                ));
    }

    private GenBuilder<List<Domain>> listsOfDomains() {
        return new GenBuilder<List<Domain>>("List of domains") {
            @Override
            public Generator<List<Domain>> build() {
                return new Generator<List<Domain>>() {
                    @Override
                    public String name() {
                        return null;
                    }

                    @Override
                    public Stream<List<Domain>> generate(Randomness randomness) {
                        return randomness.intsBetween(0, 10)
                           .mapToObj(size ->
                              domains()
                                 .build()
                                 .generate(randomness)
                                 .limit(size)
                                 .collect(toList()));
                    }
                };
            }
        };
    }
}
