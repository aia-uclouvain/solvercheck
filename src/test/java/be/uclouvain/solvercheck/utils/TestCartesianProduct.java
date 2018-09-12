package be.uclouvain.solvercheck.utils;

import be.uclouvain.solvercheck.utils.collections.CartesianProduct;
import org.junit.Assert;
import org.junit.Test;
import org.quicktheories.WithQuickTheories;
import org.quicktheories.core.Gen;

import java.util.*;
import java.util.stream.Collectors;

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
