package be.uclouvain.solvercheck.utils;

import be.uclouvain.solvercheck.WithSolverCheck;
import be.uclouvain.solvercheck.utils.collections.Zip;
import be.uclouvain.solvercheck.utils.collections.ZipEntry;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestZip implements WithSolverCheck {

    @Test
    public void iterateBothSameSize() {
        assertThat(forAll(integer().between(0, 100))
       .assertThat(size -> forAll(
                    listOf(integer()).ofSize(size),
                    listOf(integer()).ofSize(size))
       .itIsTrueThat((as, bs) -> {
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
          return true;
      })));
    }

    @Test
    public void iterateALongerThanBShouldBeFilledWithNulls() {
        assertThat(forAll(
           integer().between(0, 10),
           integer().between(0, 10))
          .assuming((i, j) -> i > j)
          .assertThat((i, j) ->
               forAll(listOf(integer()).ofSize(i), listOf(integer()).ofSize(j))
               .itIsTrueThat((as, bs) -> {
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

                   return true;
               })
          ));
    }

    @Test
    public void iterateBLongerThanAShoudlBeFilledWithNulls() {
        assertThat(forAll(
           integer().between(0, 10),
           integer().between(0, 10))
          .assuming((i, j) -> i < j)
          .assertThat((i, j) ->
              forAll(
                 listOf(integer()).ofSize(i),
                 listOf(integer()).ofSize(j))
               .itIsTrueThat((as, bs) -> {
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
                   return true;
               }))
          );
    }

    @Test
    public void streamGoesOverAllElementsSequentially() {
        assertThat(forAll(
           listOf(integer().between(0, 10)),
           listOf(integer().between(0, 10)))
          .itIsTrueThat((as, bs) -> {
              var z =
                 new Zip<>(as, bs).stream().collect(Collectors.toList());

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
          }));
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
