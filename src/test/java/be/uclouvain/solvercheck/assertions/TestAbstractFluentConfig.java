package be.uclouvain.solvercheck.assertions;

import be.uclouvain.solvercheck.assertions.util.AbstractFluentConfig;
import be.uclouvain.solvercheck.core.data.PartialAssignment;
import org.junit.Test;

import java.util.Set;
import java.util.function.Predicate;

public class TestAbstractFluentConfig {
    @Test
    public void testWithValuesBetween() {
        int low = 10;
        int high= 100;
        new DummyFluentConfig()
                .withValuesBetween(low, high)
                .doChk(pa ->
                    pa.stream().allMatch(domain ->
                        domain.stream().allMatch(value ->
                            low <= value && value <= high
                        )
                    )
                );
    }

    @Test(expected = IllegalArgumentException.class)
    public void withValuesBetweenMustFailIfXGreaterThanY() {
        int low = 100;
        int high= 10;

        new DummyFluentConfig()
                .withValuesBetween(low, high)
                .doChk(pa -> true);
    }

    @Test
    public void withValuesBetweenMustGenerateSearchTreeLeavesWhenXEqualsY() {
        int low = 10;
        int high= 10;

        new DummyFluentConfig()
                .withValuesBetween(low, high)
                .doChk(PartialAssignment::isLeaf);
    }


    @Test
    public void testSpreading() {
        int spread = 10;
        new DummyFluentConfig()
                .spreading(spread)
                .assuming(pa -> !pa.isError())
                .doChk(pa ->
                   pa.stream().allMatch(domain ->
                      domain.maximum() - domain.minimum() <= spread
                   )
                );
    }

    @Test(expected = IllegalArgumentException.class)
    public void spreadingMustFailWhenSpreadIsNegative() {
        int spread = -1;

        new DummyFluentConfig()
                .spreading(spread)
                .doChk(pa -> true);
    }

    @Test
    public void spreadingMustGenerateErrorsPaWhenSpreadIsNul() {
        int spread = 0;

        new DummyFluentConfig()
                .spreading(spread)
                .doChk(PartialAssignment::isLeaf);
    }

    @Test
    public void testWithDomainSizeUpTo() {
        int size = 10;
        new DummyFluentConfig()
                .withDomainsOfSizeUpTo(size)
                .doChk(pa ->
                   pa.stream().allMatch(domain ->
                      domain.size() <= size
                   )
                );
    }

    @Test(expected = IllegalArgumentException.class)
    public void withDomainSizeUpToMustFailWhenSizeIsNegative() {
        int size = -10;
        new DummyFluentConfig()
                .withDomainsOfSizeUpTo(size)
                .doChk(pa -> true);
    }

    @Test
    public void withDomainSizeUpToMustGenerateErrorPaWhenSizeIsNul() {
        int size = 0;
        new DummyFluentConfig()
                .withDomainsOfSizeUpTo(size)
                .doChk(pa -> pa.stream().allMatch(Set::isEmpty));
    }

    @Test
    public void testOfSizeBetween() {
        int low = 10;
        int high= 20;
        new DummyFluentConfig()
                .ofSizeBetween(low, high)
                .doChk(pa ->
                   low <= pa.size() && pa.size() <= high
                );
    }

    @Test(expected = IllegalArgumentException.class)
    public void ofSizeBetweenMustFailWhenYLowerThanX() {
        int low = 20;
        int high= 10;
        new DummyFluentConfig()
                .ofSizeBetween(low, high)
                .doChk(pa -> true);
    }

    @Test
    public void ofSizeBetweenMustSetAnExactSizeWhenXEqualsY() {
        int low = 20;
        int high= 20;
        new DummyFluentConfig()
                .ofSizeBetween(low, high)
                .doChk(pa -> pa.size() == 20);
    }

    @Test
    public void testOfSize() {
        int size = 10;
        new DummyFluentConfig()
               .ofSize(size)
               .doChk(pa ->
                  pa.size() == size
               );
    }

    @Test(expected = IllegalArgumentException.class)
    public void ofSizeMustFailWhenSizeIsNegative() {
        int size = -10;
        new DummyFluentConfig()
                .ofSize(size)
                .doChk(pa -> true);
    }

    @Test
    public void testAssuming() {
        int size = 10;
        new DummyFluentConfig()
                .assuming(pa -> pa.size() <= size)
                .doChk(pa ->
                   pa.stream().allMatch(domain ->
                      domain.size() <= size
                   )
                );
    }


    @Test(expected = AssertionError.class)
    public void assumingMustThrowAnExceptionWhenAssumptionIsUnsat() {
        new DummyFluentConfig()
                .assuming(pa -> false)
                .doChk(pa -> true);
    }

    private class DummyFluentConfig extends AbstractFluentConfig<DummyFluentConfig> {
        public void doChk(Predicate<PartialAssignment> pa) {
            super.doCheck(pa);
        }
        @Override
        protected DummyFluentConfig getThis() {
            return this;
        }
    }
}
