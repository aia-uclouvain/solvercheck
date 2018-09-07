package be.uclouvain.solvercheck.assertions;

import be.uclouvain.solvercheck.core.Filter;
import org.assertj.core.api.AbstractAssert;


public final class FilterAssert extends AbstractAssert<FilterAssert, Filter> {

    public FilterAssert(final Filter actual) {
        super(actual, FilterAssert.class);
    }

    public static FilterAssert assertThat(final Filter actual) {
        return new FilterAssert(actual);
    }

    public FilterAssert hasSameFilteringAs(final Filter trusted) {
        isNotNull();

        //FIXME
        //qt().forAll()

        return this;
    }
}
