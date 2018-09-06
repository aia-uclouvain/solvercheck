package be.uclouvain.solvercheck.assertions;

import be.uclouvain.solvercheck.core.Filter;
import org.assertj.core.api.AbstractAssert;


import static org.quicktheories.QuickTheory.qt;
import static org.quicktheories.generators.SourceDSL.*;
import static org.quicktheories.generators.Generate.*;


public final class FilterAssert extends AbstractAssert<FilterAssert, Filter> {

    public FilterAssert(Filter actual) {
        super(actual, FilterAssert.class);
    }

    public static FilterAssert assertThat(final Filter f) {
        return new FilterAssert(f);
    }

    public FilterAssert hasSameFilteringAs(final Filter trusted) {
        isNotNull();

        //qt().forAll()

        return this;
    }
}
