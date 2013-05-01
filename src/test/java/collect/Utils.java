/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/ Copyright (C) 2013
 * LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package collect;

import crdt.tree.wordtree.Word;
import java.util.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author urso
 */
public class Utils {

    public static <T> HashSet<T> toSet(T... t) {
        HashSet<T> l = new HashSet<T>();
        Collections.addAll(l, t);
        return l;
    }

    public static List<Character> toList(char t[]) {
        List<Character> l = new LinkedList<Character>();
        for (char c : t) {
            l.add(c);
        }
        return new Word(l);
    }

    public static <T> List<T> toList(T... t) {
        List<T> l = new LinkedList<T>();
        for (T c : t) {
            l.add(c);
        }
        return l;
    }

    public static boolean isSorted(List<? extends Comparable> list) {
        return isSorted(list, true);
    }

    public static boolean isSorted(List<? extends Comparable> list, boolean strict) {
        Comparable p = null;
        for (Comparable c : list) {
            if (p != null) {
                int comp = p.compareTo(c);
                if (comp > 0 || (strict && comp == 0)) {
                    return false;
                } 
            }
            p = c;
        }
        return true;
    }

    @Test
    public void testEmptySorted() {
        List<Boolean> l = new ArrayList<Boolean>();
        assertTrue(isSorted(l));
    }
    
    @Test
    public void testOneSorted() {
        assertTrue(isSorted(toList(true)));
    }
    
    @Test
    public void testSorted() {
        assertTrue(isSorted(toList(-100, -100, -23, 0, 0, 2, 7, 7, 42)));
    }

    @Test
    public void testSortedStrictly() {
        assertTrue(isSorted(toList(-100, -23, 0, 1, 2, 7, 42)));
    }
    
    public static int sigint(int x) {
        return (x > 0) ? -1 : (x > 0) ? 1 : 0;
    }
    
    public static <T> void assertGreaterThan(Comparable<T> x, T y) throws AssertionError {        
        assertTrue(x + " not greater than " + y + '(' + x.compareTo(y) + ')', x.compareTo(y) > 0);
    }

    public static <T> void assertGreaterEqual(Comparable<T> x, T y) throws AssertionError {        
        assertTrue(x + " not greater or equal " + y + '(' + x.compareTo(y) + ')', x.compareTo(y) >= 0);
    }
}
