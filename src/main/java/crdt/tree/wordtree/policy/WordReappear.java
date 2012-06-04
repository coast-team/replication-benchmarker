/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.wordtree.policy;

import collect.UnorderedNode;
import crdt.tree.wordtree.Word;
import crdt.tree.wordtree.WordConnectionPolicy;
import java.util.*;

/**
 * Skip the orphan nodes.
 * @author urso
 */
public class WordReappear<T> extends WordConnectionPolicy<T> {
    @Override 
    protected void treatOrphan(List<T> orphan) {
        LinkedList<T> ancestor = new LinkedList<T>(orphan);
        while (nodeToWord.getInverse(ancestor) == null) {
            ancestor.pollLast();
        }
        UnorderedNode<T> node = nodeToWord.getInverse(ancestor);
        int i = ancestor.size();
        ListIterator<T> it = orphan.listIterator(i);
        while (it.hasNext()) {
            node = tree.add(node, it.next());
            i++;
//            nodeToWord.put(node, null); // orphan.subList(0, i));
        }
        nodeToWord.put(node, orphan);
    }

    @Override
    public WordReappear<T> create() {
        return new WordReappear<T>();
    }

    @Override
    public Collection<List<T>> addMapping(UnorderedNode<T> node) {
        Collection<List<T>> m = super.addMapping(node);
        if (m == null) {
            Collection<List<T>> set = new LinkedList<List<T>>();
            set.add(new Word(node.getPath()));
            return set;            
        } else {
            return m;
        }
    }
    
    @Override
    public Collection<List<T>> delMapping(UnorderedNode<T> node) {
        Collection<List<T>> m = super.addMapping(node);
        if (m == null) {
            return Collections.EMPTY_SET;
        } else {
            return m;
        }
    }
}
