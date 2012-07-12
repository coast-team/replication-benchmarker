/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.wordtree.policy;

import collect.Node;
import collect.UnorderedNode;
import crdt.tree.wordtree.WordConnectionPolicy;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Skip the orphan nodes.
 * @author urso
 */
public class WordRoot<T> extends WordConnectionPolicy<T> implements Serializable {
    @Override 
    protected void treatOrphan(List<T> orphan) {
        LinkedList<T> ancestor = new LinkedList<T>(orphan);
        T elem = ancestor.pollLast();
        while (words.contains(ancestor)) {
            ancestor.pollLast();
        }
        UnorderedNode<T> father = nodeToWord.getInverse(orphan.subList(ancestor.size(), orphan.size()-1)),
                node = father.getChild(elem);
        if (node == null) {
            node = tree.add(node, elem);
        }
        nodeToWord.put(node, orphan);
    }

    @Override
    public WordRoot<T> create() {
        return new WordRoot<T>();
    }
}
