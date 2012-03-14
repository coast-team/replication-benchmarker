/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.wordtree.policy;

import collect.Node;
import crdt.tree.wordtree.Word;
import crdt.tree.wordtree.WordConnectionPolicy;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

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
        Node<T> node = nodeToWord.getInverse(ancestor);
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
    public Collection<List<T>> addMapping(Node<T> node) {
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
    public Collection<List<T>> delMapping(Node<T> node) {
        Collection<List<T>> m = super.addMapping(node);
        if (m == null) {
            return Collections.EMPTY_SET;
        } else {
            return m;
        }
    }
}
