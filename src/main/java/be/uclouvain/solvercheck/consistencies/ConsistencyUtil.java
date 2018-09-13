package be.uclouvain.solvercheck.consistencies;

import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.data.impl.AssignmentFactory;
import be.uclouvain.solvercheck.core.data.impl.DomainFactory;
import be.uclouvain.solvercheck.core.data.impl.PartialAssignmentFactory;
import be.uclouvain.solvercheck.core.task.Checker;
import be.uclouvain.solvercheck.core.task.Filter;
import be.uclouvain.solvercheck.utils.collections.CartesianProduct;
import be.uclouvain.solvercheck.utils.collections.Range;

import java.util.*;
import java.util.stream.Collectors;

public class ConsistencyUtil {

    public static Filter arcConsistency(final Checker checker) {
        return partial ->
                    PartialAssignmentFactory.unionOf(
                            supportCandidates(partial)
                                .stream()
                                 .map(AssignmentFactory::from)
                                .filter(checker)
                                .collect(Collectors.toList())
                    );
    }

    public static PartialAssignment bcdFiltering(final Checker checker, final PartialAssignment partial) {
        List<List<Integer>> domains =
                partial.stream()
                       .map(ConsistencyUtil::toSortedList)
                       .collect(Collectors.toList());

        boolean fixpoint = false;
        while(!fixpoint) {
            fixpoint = true;

            // crop all lower bounds
            for(int i = 0; i < domains.size(); i++) {
                List<Integer>   varDomain = domains.get(i);

                while(!varDomain.isEmpty()) {
                    int     var        = i;
                    int     bound      = varDomain.get(0);
                    boolean hasSupport = CartesianProduct.of(domains).stream()
                                                .filter(supp -> supp.get(var).equals(bound))
                                                .map(AssignmentFactory::from)
                                                .anyMatch(checker);

                    if( hasSupport ) {
                        break;
                    } else {
                        varDomain.remove(0);
                        fixpoint = false;
                    }

                    // return all empty domains
                    if( varDomain.isEmpty() ) {
                        return PartialAssignmentFactory.from(
                            domains.stream()
                                   .map(x -> DomainFactory.from())
                                   .collect(Collectors.toList())
                        );
                    }
                }
            }

            // crop all upper bounds
            for(int i = 0; i < domains.size(); i++) {
                List<Integer>   varDomain = domains.get(i);

                while(!varDomain.isEmpty()) {
                    int     var        = i;
                    int     idx        = varDomain.size()-1;
                    int     bound      = varDomain.get(idx);
                    boolean hasSupport = CartesianProduct.of(domains).stream()
                            .filter(supp -> supp.get(var).equals(bound))
                            .map(AssignmentFactory::from)
                            .anyMatch(checker);

                    if( hasSupport ) {
                        break;
                    } else {
                        varDomain.remove(idx);
                        fixpoint = false;
                    }

                    // return all empty domains
                    if( varDomain.isEmpty() ) {
                        return PartialAssignmentFactory.from(
                                domains.stream()
                                        .map(x -> DomainFactory.from())
                                        .collect(Collectors.toList())
                        );
                    }
                }
            }
        }

        return PartialAssignmentFactory.from(
                domains.stream()
                       .map(DomainFactory::fromCollection)
                       .collect(Collectors.toList())
        );
    }

    private static List<Integer> toSortedList(final Collection<Integer> c) {
        ArrayList<Integer> res = new ArrayList<>(c);
        Collections.sort(res);
        return res;
    }

    public static Set<List<Integer>> supportCandidates(final PartialAssignment partial) {
        return CartesianProduct.of(partial);
    }

    public static Set<List<Integer>> boundSupportCandidates(final PartialAssignment partial) {
        return CartesianProduct.of(
                partial.stream()
                       .map(domain -> Range.between(domain.minimum(), domain.maximum()))
                       .collect(Collectors.toList())
        );
    }

    public static Filter leastFixPoint(final Filter filter) {
        return partial -> {
            var current = partial;
            while(true) {
                var filtered = filter.filter(current);
                if ( filtered.equals(current) ) {
                    return current;
                } else {
                    current = filtered;
                }
            }
        };
    }

}
