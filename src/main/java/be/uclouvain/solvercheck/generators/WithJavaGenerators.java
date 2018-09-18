package be.uclouvain.solvercheck.generators;

import org.quicktheories.generators.ArbitraryDSL;
import org.quicktheories.generators.ArraysDSL;
import org.quicktheories.generators.BigDecimalsDSL;
import org.quicktheories.generators.BigIntegersDSL;
import org.quicktheories.generators.BooleansDSL;
import org.quicktheories.generators.CharactersDSL;
import org.quicktheories.generators.DatesDSL;
import org.quicktheories.generators.DoublesDSL;
import org.quicktheories.generators.FloatsDSL;
import org.quicktheories.generators.IntegersDSL;
import org.quicktheories.generators.ListsDSL;
import org.quicktheories.generators.LocalDatesDSL;
import org.quicktheories.generators.LongsDSL;
import org.quicktheories.generators.MapsDSL;
import org.quicktheories.generators.SourceDSL;
import org.quicktheories.generators.StringsDSL;

/**
 * This interface provides you with a simple, and consitent way to use
 * QuickTheories (the PBT engine used by SolverCheck under the hood)
 * generators as if they were part of a SolverCheck custom DSL.
 *
 * This interface should really be thought of as a stackable trait in Scala
 * parlance.
 */
public interface WithJavaGenerators {

    /**
     * @return a configurable generator meant to produce random `Boolean`
     * values.
     */
    default BooleansDSL booleans() {
        return SourceDSL.booleans();
    }

    /**
     * @return a configurable generator meant to produce random `Long` numbers.
     */
    default LongsDSL longs() {
        return SourceDSL.longs();
    }

    /**
     * @return a configurable generator meant to produce random `Integer`
     * numbers.
     */
    default IntegersDSL integers() {
        return SourceDSL.integers();
    }

    /**
     * @return a configurable generator meant to produce random `Double`
     * numbers.
     */
    default DoublesDSL doubles() {
        return SourceDSL.doubles();
    }

    /**
     * @return a configurable generator meant to produce random `Float` numbers.
     */
    default FloatsDSL floats() {
        return SourceDSL.floats();
    }

    /**
     * @return a configurable generator meant to produce random `Character`s.
     */
    default CharactersDSL characters() {
        return SourceDSL.characters();
    }

    /**
     * @return a configurable generator meant to produce random `String`s.
     */
    default StringsDSL strings() {
        return SourceDSL.strings();
    }

    /**
     * @return a configurable generator meant to produce random `List`s of
     * items.
     */
    default ListsDSL lists() {
        return SourceDSL.lists();
    }

    /**
     * @return a configurable generator meant to produce random `Map`s
     * dictionaries.
     */
    default MapsDSL maps() {
        return SourceDSL.maps();
    }

    /**
     * @return a configurable generator meant to produce random arrays.
     */
    default ArraysDSL arrays() {
        return SourceDSL.arrays();
    }

    /**
     * @return a configurable generator meant to produce random `BigInteger`
     * numbers.
     */
    default BigIntegersDSL bigIntegers() {
        return SourceDSL.bigIntegers();
    }
    /**
     * @return a configurable generator meant to produce random `BigDecimal`
     * numbers.
     */
    default BigDecimalsDSL bigDecimals() {
        return SourceDSL.bigDecimals();
    }

    /**
     * @return a generator to produce random sequences of constant values, enum
     * values, sequences and specified items of the same type.
     */
    default ArbitraryDSL arbitrary() {
        return SourceDSL.arbitrary();
    }

    /**
     * @return a configurable generator meant to produce random `Date`s.
     */
    default DatesDSL dates() {
        return SourceDSL.dates();
    }

    /**
     * @return a configurable generator meant to produce random `LocalDate`s
     * (dates without a timezone).
     */
    default LocalDatesDSL localDates() {
        return SourceDSL.localDates();
    }
}
