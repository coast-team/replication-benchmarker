/*
 *  Replication Benchmarker
 *  https://github.com/score-team/replication-benchmarker/
 *  Copyright (C) 2013 LORIA / Inria / SCORE Team
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jbenchmarker.logootsplitO;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class IDFactoryTest {

    public IDFactoryTest() {
    }

    @Test
    public void testSomeMethod() {
        Identifier id1 = new Identifier(Arrays.asList(0, 1, 0), 1);
        Identifier id2 = new Identifier(Arrays.asList(0, 1, 0, 1, 1, 1), 0);
        List<Integer> l = IDFactory.createBetweenPosition(id1, id2, 9, 1);
        Identifier idf = new Identifier(l, 0);

        assertEquals(id1.compareTo(idf), -1);
        assertEquals(id2.compareTo(idf), 1);

    }

    @Test
    public void testSomeMethod2() {
        Identifier id1 = new Identifier(Arrays.asList(0, 0, 5, 0), 7);
        Identifier id2 = new Identifier(Arrays.asList(0, 0, 6, 0), 0);
        List<Integer> l = IDFactory.createBetweenPosition(id1, id2, 3, 1);
        Identifier idf = new Identifier(l, 0);
        assertEquals(id1.compareTo(idf), -1);
        assertEquals(id2.compareTo(idf), 1);

    }

    @Test
    public void testSomeMethod3() {
        Identifier id1 = new Identifier(Arrays.asList(0, 0, 7, 0), 0);
        Identifier id2 = new Identifier(Arrays.asList(0, 0, 7, 0, 0, -1073741820, 7, 2), 0);
        List<Integer> l = IDFactory.createBetweenPosition(id1, id2, 3, 1);
        Identifier idf = new Identifier(l, 0);
        assertEquals(id1.compareTo(idf), -1);
        assertEquals(id2.compareTo(idf), 1);

    }
}