package be.uclouvain.solvercheck.assertions;

import be.uclouvain.solvercheck.core.data.PartialAssignment;
import be.uclouvain.solvercheck.generators.Generators;
import org.quicktheories.QuickTheory;
import org.quicktheories.core.Gen;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;

import static org.quicktheories.generators.SourceDSL.integers;

public class ForAnyPartialAssignment {

    private static final int DEFAULT_GEN_ATTEMPTS = 10000;
    private static final int DEFAULT_ANCHOR_SAMPLES = 100;
    private static final int DEFAULT_EXAMPLES = 10;

    private static final int DEFAULT_MIN_VALUE = MIN_VALUE;
    private static final int DEFAULT_MAX_VALUE = MAX_VALUE;
    private static final int DEFAULT_SPREAD = 10;
    private static final int DEFAULT_NB_VARS_MIN = 0;
    private static final int DEFAULT_NB_VARS_MAX = 5;
    private static final int DEFAULT_MAX_DOM_SIZE = 10;

    private QuickTheory qt;

    private int anchorSamples;
    private int examples;

    private int minValue;
    private int maxValue;
    private int spread;
    private int nbVarMin;
    private int nbVarMax;
    private int maxDomSize;

    private Predicate<PartialAssignment> assumptions;

    public ForAnyPartialAssignment() {
        this.qt = QuickTheory.qt().withGenerateAttempts(DEFAULT_GEN_ATTEMPTS);

        this.anchorSamples = DEFAULT_ANCHOR_SAMPLES;
        this.examples      = DEFAULT_EXAMPLES;

        this.minValue      = DEFAULT_MIN_VALUE;
        this.maxValue      = DEFAULT_MAX_VALUE;
        this.spread        = DEFAULT_SPREAD;
        this.nbVarMin      = DEFAULT_NB_VARS_MIN;
        this.nbVarMax      = DEFAULT_NB_VARS_MAX;
        this.maxDomSize    = DEFAULT_MAX_DOM_SIZE;

        this.assumptions   = pa -> true;
    }

    public ForAnyPartialAssignment withFixedSeed(final long seed) {
        qt = qt.withFixedSeed(seed);
        return this;
    }
    public ForAnyPartialAssignment withGenerateAttempts(final int attempts) {
        qt = qt.withGenerateAttempts(attempts);
        return this;
    }

    public ForAnyPartialAssignment withAnchorSamples(final int n) {
        anchorSamples = n;
        return this;
    }
    public ForAnyPartialAssignment withExamples(final int n) {
        examples = n;
        return this;
    }

    public ForAnyPartialAssignment withShrinkCycles(final int cycles) {
        qt = qt.withShrinkCycles(cycles);
        return this;
    }


    public ForAnyPartialAssignment withValuesBetween(final int x, final int y) {
        if (y < x) {
            throw new IllegalArgumentException("x must be >= y");
        }
        minValue = x;
        maxValue = y;

        return this;
    }
    public ForAnyPartialAssignment spreading(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be >= 0");
        }
        spread = n;

        return this;
    }
    public ForAnyPartialAssignment withDomainsOfSizeUpTo(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be >= 0");
        }
        this.maxDomSize = n;

        return this;
    }
    public ForAnyPartialAssignment ofSize(final int x) {
        return ofSizeBetween(x, x);
    }
    public ForAnyPartialAssignment ofSizeBetween(final int x, final int y) {
        if (0 < x || y < x) {
            throw new IllegalArgumentException("0 <= x <= y");
        }
        this.nbVarMin = x;
        this.nbVarMax = y;

        return this;
    }
    public ForAnyPartialAssignment assuming(
            final Predicate<PartialAssignment> assumption) {
        this.assumptions = this.assumptions.and(assumption);
        return this;
    }

    public void check(final Predicate<PartialAssignment> test) {
        qt.withExamples(anchorSamples)
          .forAll(integers().between(anchorMin(), anchorMax()))
          .checkAssert(anchor ->
             qt.withExamples(examples)
               .forAll(partialAssignments(anchor))
               .assuming(assumptions)
               .check(test)
          );
    }

    public void checkAssert(final Consumer<PartialAssignment> test) {
        qt.withExamples(anchorSamples)
          .forAll(integers().between(anchorMin(), anchorMax()))
          .checkAssert(anchor ->
             qt.withExamples(examples)
               .forAll(partialAssignments(anchor))
               .assuming(assumptions)
               .checkAssert(test)
          );
    }

    private Gen<PartialAssignment> partialAssignments(final int anchor) {
        return Generators.partialAssignments()
                .withVariablesBetween(nbVarMin, nbVarMax)
                .withDomainsOfSizeUpTo(maxDomSize)
                .withValuesRanging(lowerBound(anchor), upperBound(anchor));
    }

    private Gen<Integer> anchors() {
        return integers().between(anchorMin(), anchorMax());
    }

    private int lowerBound(final int anchor) {
        if (rangeFitsInSpreadMax()) {
            return minValue;
        } else {
            return anchor - ((spread + 1) / 2);
        }
    }

    private int upperBound(final int anchor) {
        if (rangeFitsInSpreadMax()) {
            return maxValue;
        } else {
            return anchor + (spread / 2);
        }
    }

    private int anchorMin() {
        if (rangeFitsInSpreadMax()) {
            return minValue;
        } else {
            return minValue + ((spread + 1) / 2);
        }
    }

    private int anchorMax() {
        if (rangeFitsInSpreadMax()) {
            return maxValue;
        } else {
            return maxValue - (spread / 2);
        }
    }

    private boolean rangeFitsInSpreadMax() {
        return (long) maxValue - (long) minValue <= (long) spread;
    }
}
