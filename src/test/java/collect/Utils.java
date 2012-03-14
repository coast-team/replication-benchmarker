/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
}
