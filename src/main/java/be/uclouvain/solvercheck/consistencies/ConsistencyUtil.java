package be.uclouvain.solvercheck.consistencies;

import be.uclouvain.solvercheck.core.data.Assignment;
import be.uclouvain.solvercheck.core.data.Domain;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.core.task.Checker;
import be.uclouvain.solvercheck.core.task.Filter;
import be.uclouvain.solvercheck.utils.collections.CartesianProduct;
import be.uclouvain.solvercheck.utils.collections.Range;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConsistencyUtil {

    public static Filter arcConsistent(final Checker checker) {
        return partial ->
                    PartialAssignment.unionOf(
                            CartesianProduct.of(partial)
                                .stream()
                                .map(Assignment::from)
                                .filter(checker)
                                .collect(Collectors.toList())
                    );
    }

    // NOTE:: --------------------------------------------------------------------------------------------
    // NOTE:: The following definition is also a valid implementation of the arc consistent filtering.
    // NOTE:: It does stick to the mathematical definition of AC much closer than the above.
    // NOTE:: Meanwhile, the above implementation is (much) faster since it operates on all domains in one
    // NOTE:: pass, whereas this commented definition requires one pass *per variable* from the domain.
    // NOTE:: --------------------------------------------------------------------------------------------
    // NOTE:: public static Filter arcConsistent(final Checker checker) {
    // NOTE::     return partial -> Range.between(0, partial.size()-1)
    // NOTE::                 .stream()
    // NOTE::                 .map(variable -> partial.get(variable).stream()
    // NOTE::                                         .filter(v -> exists(ConsistencyUtil::support)
    // NOTE::                                                 .satisfying(checker)
    // NOTE::                                                 .forVariable(variable)
    // NOTE::                                                 .assignedTo(v)
    // NOTE::                                                 .givenDomains(partial))
    // NOTE::                                         .collect(toDomain()))
    // NOTE::                 .collect(toPartialAssignment());
    // NOTE:: }



    // FIXME: reformulate in terms of "fixpoint", "support" and "exists"
    private static Domain shrinkBounds(final int var, final Domain dom, final Checker checker, final PartialAssignment context) {
        return null;
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
                                                .map(Assignment::from)
                                                .anyMatch(checker);

                    if( hasSupport ) {
                        break;
                    } else {
                        varDomain.remove(0);
                        fixpoint = false;
                    }

                    // return all empty domains
                    if( varDomain.isEmpty() ) {
                        return domains.stream()
                                   .map(x -> Domain.emptyDomain())
                                   .collect(PartialAssignment.collector());
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
                            .map(Assignment::from)
                            .anyMatch(checker);

                    if( hasSupport ) {
                        break;
                    } else {
                        varDomain.remove(idx);
                        fixpoint = false;
                    }

                    // return all empty domains
                    if( varDomain.isEmpty() ) {
                        return domains.stream()
                                .map(x -> Domain.emptyDomain())
                                .collect(PartialAssignment.collector());
                    }
                }
            }
        }

        return domains.stream().collect(PartialAssignment.collector());
    }

    private static List<Integer> toSortedList(final Collection<Integer> c) {
        ArrayList<Integer> res = new ArrayList<>(c);
        Collections.sort(res);
        return res;
    }

    public static Set<List<Integer>> support(final PartialAssignment partial) {
        return CartesianProduct.of(partial);
    }

    public static Set<List<Integer>> boundSupport(final PartialAssignment partial) {
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

    private static ExistsSupport exists(final Function<PartialAssignment, Set<List<Integer>>> supportType) {
        return new ExistsSupport(supportType);
    }

    private static class ExistsSupport {
        private Function<PartialAssignment, Set<List<Integer>>> supportType;
        private Checker checker;
        private int variable;
        private int value;

        public ExistsSupport(final Function<PartialAssignment, Set<List<Integer>>> supportType) {
            this.supportType = supportType;
            this.checker      = null;
        }
        public ExistsSupport satisfying(final Checker checker) {
            this.checker = checker;
            return this;
        }
        public ExistsSupport forVariable(final int variable) {
            this.variable = variable;
            return this;
        }
        public ExistsSupport assignedTo(final int value) {
            this.value = value;
            return this;
        }
        public boolean givenDomains(final PartialAssignment domains) {
            return supportType.apply(domains).stream()
                              .anyMatch(support ->
                                      support.get(variable).equals(value)
                                   && checker.test(Assignment.from(support))
                              );
        }

    }
}
