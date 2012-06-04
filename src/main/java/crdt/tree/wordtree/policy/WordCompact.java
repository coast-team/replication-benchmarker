/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.wordtree.policy;

import collect.Node;
import collect.UnorderedNode;
import crdt.tree.wordtree.Word;
import crdt.tree.wordtree.WordConnectionPolicy;
import java.util.LinkedList;
import java.util.List;

/**
 * Skip the orphan nodes.
 * @author urso
 */
public class WordCompact<T> extends WordConnectionPolicy<T> {
    @Override 
    protected void treatOrphan(List<T> orphan) {
        LinkedList<T> word = new LinkedList<T>(orphan);
        T elem = word.pollLast();
        while (words.contains(word)) {
            word.pollLast();
        }
        int i = word.size();    
        while (!word.isEmpty() && !words.contains(word)) {
            word.pollLast();
        }
        word.addAll(orphan.subList(i, orphan.size()-1));
        UnorderedNode<T> father = nodeToWord.getInverse(word),
                node = father.getChild(elem);
        if (node == null) {
            node = tree.add(father, elem);
        }
        nodeToWord.put(node, orphan);
    }

    @Override
    public WordConnectionPolicy<T> create() {
       return new WordCompact<T>();
    }
}
