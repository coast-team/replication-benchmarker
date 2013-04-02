/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2012 LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package collect;

import crdt.tree.wordtree.Word;
import java.util.*;

/**
 *
 * @author urso
 */
public class Utils {
    public static <T> HashSet<T> toSet(T ... t) {
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
    
    public static <T> List<T> toList(T ... t) {
        List<T> l = new LinkedList<T>();
        for (T c : t) {
            l.add(c);
        }
        return l;
    }
}
