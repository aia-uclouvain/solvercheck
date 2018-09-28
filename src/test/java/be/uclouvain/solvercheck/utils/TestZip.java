package be.uclouvain.solvercheck.utils;

import be.uclouvain.solvercheck.utils.collections.Zip;
import be.uclouvain.solvercheck.utils.collections.ZipEntry;
import org.junit.Before;
import org.junit.Test;
import org.quicktheories.QuickTheory;
import org.quicktheories.WithQuickTheories;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestZip implements WithQuickTheories {

    private QuickTheory qt;

    @Before
    public void setUp() {
        qt = qt().withGenerateAttempts(10000);
    }

    @Test
    public void iterateBothSameSize() {
        qt.forAll(integers().between(0, 100))
          .checkAssert( size ->
                qt.forAll(
                    lists().of(integers().all()).ofSize(size),
                    lists().of(integers().all()).ofSize(size))
                  .checkAssert((as, bs) -> {
                      Zip zip = new Zip<>(as, bs);

                      // these two *have to* be copies. Else they would be
                      // consumed by the zipper
                      Iterator<Integer> ai = as.iterator();
                      Iterator<Integer> bi = bs.iterator();

                      Iterator<ZipEntry<Integer, Integer>> zi = zip.iterator();

                      while (zi.hasNext()) {
                          ZipEntry<Integer, Integer> ze = zi.next();
                          Integer ae = ai.next();
                          Integer be = bi.next();

                          assertEquals(ae, ze.first());
                          assertEquals(be, ze.second());
                      }

                      assertTrue(!ai.hasNext());
                      assertTrue(!bi.hasNext());
                  })
            );
    }

    @Test
    public void iterateALongerThanBShouldBeFilledWithNulls() {
        qt.forAll(
            integers().between(0, 10),
            integers().between(0, 10))
          .assuming((i, j) -> i > j)
          .checkAssert((i, j) ->
             qt.forAll(
                 lists().of(integers().all()).ofSize(i),
                 lists().of(integers().all()).ofSize(j))
               .checkAssert((as, bs) -> {
                   Zip zip = new Zip<>(as, bs);

                   // these two *have to* be copies. Else they would be
                   // consumed by the zipper
                   Iterator<Integer> ai = as.iterator();
                   Iterator<Integer> bi = bs.iterator();

                   Iterator<ZipEntry<Integer, Integer>> zi = zip.iterator();

                   while (zi.hasNext()) {
                       ZipEntry<Integer, Integer> ze = zi.next();
                       Integer ae = ai.next();
                       Integer be = bi.hasNext() ? bi.next() : null;

                       assertEquals(ae, ze.first());
                       assertEquals(be, ze.second());
                   }

                   assertTrue(!ai.hasNext());
                   assertTrue(!bi.hasNext());
               })
          );
    }

    @Test
    public void iterateBLongerThanAShoudlBeFilledWithNulls() {
        qt.forAll(
            integers().between(0, 10),
            integers().between(0, 10))
          .assuming((i, j) -> i < j)
          .checkAssert((i, j) ->
             qt.forAll(
                 lists().of(integers().all()).ofSize(i),
                 lists().of(integers().all()).ofSize(j))
               .checkAssert((as, bs) -> {
                   Zip zip = new Zip<>(as, bs);

                   // these two *have to* be copies. Else they would be
                   // consumed by the zipper
                   Iterator<Integer> ai = as.iterator();
                   Iterator<Integer> bi = bs.iterator();

                   Iterator<ZipEntry<Integer, Integer>> zi = zip.iterator();

                   while (zi.hasNext()) {
                       ZipEntry<Integer, Integer> ze = zi.next();
                       Integer ae = ai.hasNext() ? ai.next() : null;
                       Integer be = bi.next();

                       assertEquals(ae, ze.first());
                       assertEquals(be, ze.second());
                   }

                   assertTrue(!ai.hasNext());
                   assertTrue(!bi.hasNext());
               })
          );
    }

    @Test
    public void streamGoesOverAllElementsSequentially() {
        qt.forAll(
            lists().of(integers().all()).ofSizeBetween(0, 10),
            lists().of(integers().all()).ofSizeBetween(0, 10))
          .check((as, bs) -> {
              var z = new Zip<>(as, bs).stream().collect(Collectors.toList());

              Iterator<ZipEntry<Integer, Integer>> it = z.iterator();

              boolean ok = true;
              for (ZipEntry<Integer, Integer> ze : new Zip<>(as, bs)) {
                  ZipEntry<Integer, Integer> se = it.next();

                  Integer zef = ze.first();
                  Integer sef = se.first();
                  Integer zes = ze.second();
                  Integer ses = se.second();

                  ok &= (zef == null && sef == null) || (zef.equals(sef));
                  ok &= (zes == null && ses == null) || (zes.equals(ses));
              }

              ok &= !it.hasNext();

              return ok;
          });
    }

    @Test
    public void testApplyOnZipEntry() {
        List<String> a = List.of("Hello");
        List<String> b = List.of("World");

        Zip<String, String> zip = new Zip<>(a, b);


        List<String> result = zip.stream()
                .map(entry -> entry.apply(String::concat))
                .collect(Collectors.toList());

        assertEquals(List.of("HelloWorld"), result);
    }
}
