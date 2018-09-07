package be.uclouvain.solvercheck.checkers;

import be.uclouvain.solvercheck.core.Assignment;
import be.uclouvain.solvercheck.generators.Generators;
import org.junit.Test;
import org.quicktheories.WithQuickTheories;
import org.quicktheories.core.Gen;

import static be.uclouvain.solvercheck.checkers.Checkers.allDiff;
import static be.uclouvain.solvercheck.checkers.Checkers.element;
import static be.uclouvain.solvercheck.utils.Utils.isValidIndex;

public class TestCheckers implements WithQuickTheories {

    @Test
    public void testAllDiff(){
        qt().forAll(assignments())
            .check(a -> allDiff().test(a) == (a.asSet().size() == a.size()));
    }

    @Test
    public void testElementIsFalseWhenGivenAnInfeasibleIndex(){
        qt().withGenerateAttempts(10000)
            .forAll(assignments(), integers().between(0, 10), integers().between(10, 10))
            .assuming((a, i, v) -> !isValidIndex(i, a.size()))
            .check   ((a, i, v) -> !element(i, v).test(a) ); // always false
    }

    @Test
    public void testElementChecksValueOfIthElement(){
        qt().withGenerateAttempts(10000)
            .forAll(assignments(), integers().between(0, 10), integers().between(10, 10))
            .assuming((a, i, v) -> i < a.size())
            .check   ((a, i, v) -> element(i, v).test(a) == (a.asArray()[i].equals(v)));
    }

    private Gen<Assignment> assignments() {
        return Generators.assignments().build();
    }
}
