package be.uclouvain.solvercheck;

import be.uclouvain.solvercheck.utils.CartesianProduct;
import be.uclouvain.solvercheck.utils.Zip;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.stream.Collectors;

public class Brolle {

    public static void main(String[] args) {
        List<Set<Integer>> sets = ImmutableList.of(
                ImmutableSet.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ImmutableSet.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ImmutableSet.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ImmutableSet.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ImmutableSet.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ImmutableSet.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ImmutableSet.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ImmutableSet.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ImmutableSet.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
        );

        int count = 0;
        long strt1 = System.currentTimeMillis();
        CartesianProduct<Integer> p = new CartesianProduct<>(sets);
        for (List<Integer> l : p) {
            count ++;
        }
        long end1 = System.currentTimeMillis();
        System.out.println(count);

        count = 0;
        long strt2 = System.currentTimeMillis();
        Set<List<Integer>> google = Sets.cartesianProduct(sets);
        for (List<Integer> l : google) {
            count ++;
        }
        long end2 = System.currentTimeMillis();
        System.out.println(count);


        System.out.println("1 = "+(end1-strt1));
        System.out.println("2 = "+(end2-strt2));
    }
}
