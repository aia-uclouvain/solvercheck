package be.uclouvain.solvercheck.utils;

import be.uclouvain.solvercheck.utils.collections.CartesianProduct;
import org.junit.Assert;
import org.junit.Test;
import org.quicktheories.WithQuickTheories;
import org.quicktheories.core.Gen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static be.uclouvain.solvercheck.utils.Utils.failsThrowing;
import static java.lang.Integer.MIN_VALUE;

public class TestCartesianProduct implements WithQuickTheories {

    @Test
    public void TheProductOfAnEmptyListTheUnitProduct() {
        CartesianProduct unitProduct = CartesianProduct.of(new ArrayList<>());

        Assert.assertEquals(1, unitProduct.size());
        Assert.assertEquals(new ArrayList<>(), unitProduct.get(0));
    }

    @Test
    public void theProductMustBeEmptyWhenOneOfTheSetsIsEmpty() {
        qt().withGenerateAttempts(10000)
            .forAll(listsOfSets())
            .assuming(l -> l.stream().anyMatch(Set::isEmpty))
            .check(l -> CartesianProduct.of(l).isEmpty() );
    }

    @Test
    public void theSizeOfTheCartesianProductEqualsTheProductOfTheSizesOfTheSets() {
        qt().forAll(listsOfSets())
            .check(l -> {
                int actualSize = CartesianProduct.of(l).size();
                int checkSize  = 1;
                for( Set<Integer> s : l) { checkSize *= s.size(); }
                return checkSize == actualSize;
            });
    }

    @Test
    public void itMustHoldATupleForEachCombinationOfValues(){
        qt().forAll(listsOfSets())
            .assuming(   sets -> sets.stream().allMatch(s -> s.size() > 0))
            .checkAssert(sets ->
                    qt().forAll(lists().of(integers().between(0, Integer.MAX_VALUE)).ofSize(sets.size()))
                        .check( indices -> {
                            List<List<Integer>> lsets = sets.stream().map(ArrayList::new).collect(Collectors.toList());
                            List<Integer>  projection = new ArrayList<>();
                            for(int i = 0; i < lsets.size(); i++) {
                                int index = indices.get(i);
                                List<Integer> targetSet = lsets.get(i);
                                projection.add( targetSet.get(index % targetSet.size()) );
                            }

                            return CartesianProduct.of(sets).contains(projection);
                        })
            );
    }

    @Test
    public void testProductIsEmptySize(){
        qt().forAll(listsOfSets())
            .check(l -> {
                CartesianProduct product = CartesianProduct.of(l);
                return product.isEmpty() == (product.size() == 0);
            } );
    }

    @Test
    public void testIteratorGoesOverAllCombinations(){
        qt().forAll(listsOfSets())
            .check(sets -> {
                int count = 0;
                CartesianProduct product   = CartesianProduct.of(sets);
                Iterator<List<Integer>> it = product.iterator();

                while(it.hasNext()) { it.next(); count++; }

                return count == product.size();
            });
    }

    // CONTAINS
    @Test
    public void containsReturnsTrueForAnyCombinationOfActualItems() {
        qt().withGenerateAttempts(10000)
            .forAll(listsOfSets())
            .assuming(sets -> sets.stream().noneMatch(Set::isEmpty))
            .check(sets -> {
                List<Integer> any = sets.stream()
                    .map(set -> set.stream().findAny().get())
                    .collect(Collectors.toList());

                return CartesianProduct.of(sets).contains(any);
            });
    }
    @Test
    public void containsReturnsFalseForAnyCombinationWithNonItems() {
        qt().withGenerateAttempts(10000)
            .forAll(listsOfSets(), integers().all())
            .assuming((sets, i) -> !sets.isEmpty())
            .assuming((sets, i) -> sets.stream().noneMatch(Set::isEmpty))
            .assuming((sets, i) -> sets.stream().noneMatch(s -> s.contains(i)))
            .check((sets, i) -> {
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
            });
    }

    @Test
    public void containsReturnsFalseForAnyListOfWrongSize() {
        qt().withGenerateAttempts(10000)
            .forAll(listsOfSets())
            .assuming(sets -> sets.stream().noneMatch(Set::isEmpty))
            .check(sets -> {
                    List<Integer> any = sets.stream()
                            .map(set -> set.stream().findAny().get())
                            .collect(Collectors.toList());

                    any.add(42);
                    return !CartesianProduct.of(sets).contains(any);
            });
    }

    @Test
    public void containsReturnsFalseForAnEmptyListWhenItIsNotEmpty() {
        qt().withGenerateAttempts(10000)
            .forAll(listsOfSets())
            .assuming(sets -> sets.stream().noneMatch(Set::isEmpty))
            .assuming(sets -> !sets.isEmpty())
            .check(sets ->
                !CartesianProduct.of(sets).contains(List.<Integer>of())
            );
    }
    @Test
    public void containsReturnsTrueForAnEmptyListWhenItIsEmpty() {
        // because arity is 0
        qt().withGenerateAttempts(10000)
            .forAll(listsOfSets())
            .assuming(sets -> sets.isEmpty())
            .check(sets ->
                    CartesianProduct.of(sets).contains(List.<Integer>of())
            );
    }

    @Test
    public void containsReturnsFalseForAnEmptyListWhenProductIsEmptyBecauseOfAnEmptySet() {
        // because some domain is empty
        qt().withGenerateAttempts(10000)
            .forAll(listsOfSets())
            .assuming(sets -> sets.stream().anyMatch(Set::isEmpty))
            .assuming(sets -> !sets.isEmpty())
            .check(sets ->
                !CartesianProduct.of(sets).contains(List.<Integer>of())
            );
    }

    // GET (i in bounds)
    @Test
    public void getReturnsAnItemProvidedIIsInRange() {
        qt().withGenerateAttempts(10000)
            .forAll(listsOfSets())
            .assuming(sets -> !CartesianProduct.of(sets).isEmpty())
            .check(sets -> {
                CartesianProduct product = CartesianProduct.of(sets);
                int i = product.size() -1;

                var item1 = product.get(i);
                var item2 = product.stream().skip(i).findFirst().get();

                return item1.equals(item2);
            });
    }

    // GET (i out bounds)
    @Test
    public void getFailsWhenIIsLessThanZero() {
        qt().withGenerateAttempts(10000)
            .forAll(listsOfSets(), integers().between(MIN_VALUE, 0))
            .assuming((sets, i) -> i < 0)
            .check((sets, i) ->
                failsThrowing(
                    IndexOutOfBoundsException.class,
                    () -> CartesianProduct.of(sets).get(i))
            );
    }
    @Test
    public void getFailsWhenIIsLessBiggerThanSize() {
        qt().withGenerateAttempts(10000)
                .forAll(listsOfSets(), integers().allPositive())
                .assuming((sets, i) -> i >= CartesianProduct.of(sets).size())
                .check((sets, i) ->
                        failsThrowing(
                                IndexOutOfBoundsException.class,
                                () -> CartesianProduct.of(sets).get(i))
                );
    }

    public Gen<List<Set<Integer>>> listsOfSets(){
        return lists().of(sets()).ofSizeBetween(0, 5);
    }

    public Gen<Set<Integer>> sets() {
        return sets(integers().all());
    }
    public <T> Gen<Set<T>> sets(Gen<T> of) {
        return lists().of(of).ofSizeBetween(0, 5).map(x->new HashSet(x));
    }
}
