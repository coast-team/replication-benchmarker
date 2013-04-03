/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2013 LORIA / Inria / SCORE Team
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
package crdt.tree.wordtree;

import crdt.tree.wordtree.policy.WordIncrementalCompact;
import crdt.tree.wordtree.policy.WordIncrementalRoot;
import crdt.tree.wordtree.policy.WordIncrementalSkipOpti;
import crdt.tree.wordtree.policy.WordIncrementalReappear;
import crdt.tree.PseudoSet;
import crdt.tree.wordtree.policy.WordIncrementalSkip;
import crdt.tree.wordtree.policy.WordCompact;
import crdt.tree.wordtree.policy.WordReappear;
import crdt.tree.wordtree.policy.WordRoot;
import crdt.tree.wordtree.policy.WordSkip;
import java.util.Set;
import collect.Node;
import java.util.List;
import collect.Tree;
import collect.HashTree;
import collect.UnorderedNode;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import static collect.Utils.*;

/**
 *
 * @author urso
 */
public class WordConnectionPolicyTest {
    static final char ta[] = {'a'}, tb[] = {'b'}, 
            tbc[] = {'b', 'c'}, tac[] = {'a', 'c'}, 
            taca[] = {'a', 'c', 'a'}, tacd[] = {'a', 'c','d'}, 
            tacde[] = {'a', 'c', 'd', 'e'}, tacaf[] = {'a', 'c', 'a', 'f'};
    static final List<Character> a = toList(ta), b = toList(tb),  
            bc = toList(tbc), ac = toList(tac), aca = toList(taca),
            acd = toList(tacd), acde = toList(tacde), acaf = toList(tacaf);
    static final Set<List<Character>> wordsorphan = toSet(a, b, bc, acde, aca, acaf);
    static final Set<List<Character>> words = toSet(a, b, bc, ac, aca, acd, acde);        
        
    public WordConnectionPolicyTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    private void notifyAll(Set<List<Character>> wordsorphan, WordPolicy policy) { 
        PseudoSet set = new PseudoSet();
        set.addObserver(policy);
        for (List l : wordsorphan) {
            set.adding(l);
        }
    }

    
    @Test
    public void testSkipEmpty() {
        WordConnectionPolicy policy = new WordSkip();

        policy.update(new PseudoSet(), null);
        policy.connect();
//        assertSame(1, policy.addMapping().size());
        assertEquals(new HashTree<Character>(), policy.lookup());
    }
    
    @Test
    public void testSkip() {
        WordConnectionPolicy policy = new WordSkip();      
        Tree<Character> treeResult = new HashTree<Character>();
        Node<Character> na = treeResult.add(null, 'a'),
                nb = treeResult.add(null, 'b'),
                nbc = treeResult.add(nb, 'c');
        
        policy.update(new PseudoSet(wordsorphan), null);
        policy.connect();        
//        assertSame("" + policy.addMapping(), 4, policy.addMapping().size());
        assertEquals(treeResult, policy.lookup());                
    }
    
    @Test
    public void testSkipIncrementalEmpty() {
        WordPolicy policy = new WordIncrementalSkip();

//        assertSame(1, policy.addMapping().size());
        assertEquals(new HashTree<Character>(), policy.lookup());
    }
    
    @Test
    public void testSkipIncremental() {
        WordPolicy policy = new WordIncrementalSkip();      
        Tree<Character> treeResult = new HashTree<Character>();
        Node<Character> na = treeResult.add(null, 'a'),
                nb = treeResult.add(null, 'b'),
                nbc = treeResult.add(nb, 'c');
        
        notifyAll(wordsorphan, policy);
//        assertSame("" + policy.addMapping(), 7, policy.addMapping().size());
        assertEquals(treeResult, policy.lookup());                
    }
    
    @Test
    public void testSkipIncOptilEmpty() {
        WordPolicy policy = new WordIncrementalSkipOpti();

//        assertSame(1, policy.addMapping().size());
        assertEquals(new HashTree<Character>(), policy.lookup());
    }
    
    @Test
    public void testSkipIncOpti() {
        WordPolicy policy = new WordIncrementalSkipOpti();      
        Tree<Character> treeResult = new HashTree<Character>();
        Node<Character> na = treeResult.add(null, 'a'),
                nb = treeResult.add(null, 'b'),
                nbc = treeResult.add(nb, 'c');
        
        notifyAll(wordsorphan, policy);
//        assertSame("" + policy.addMapping(), 7, policy.addMapping().size());
        assertEquals(treeResult, policy.lookup());                
    }
    
    @Test
    public void testReappearEmpty() {
        WordConnectionPolicy policy = new WordReappear();

        policy.update(new PseudoSet(), null);
        policy.connect();
//        assertSame(1, policy.addMapping().size());
        assertEquals(new HashTree<Character>(), policy.lookup());
    }
    
    @Test
    public void testReappear() {
        WordConnectionPolicy policy = new WordReappear();
      
        Tree<Character> treeResult = new HashTree<Character>();
        Node<Character> na = treeResult.add(null, 'a'),
                nb = treeResult.add(null, 'b'),
                nbc = treeResult.add(nb, 'c'),
                nac = treeResult.add(na, 'c'),
                naca = treeResult.add(nac, 'a'),
                nacd = treeResult.add(nac, 'd'),
                nacde = treeResult.add(nacd, 'e'),
                nacaf = treeResult.add(naca, 'f');
        
        policy.update(new PseudoSet(wordsorphan), null);
        policy.connect();
//        assertSame("" + policy.addMapping(), 7, policy.addMapping().size());
        Node acn = ((UnorderedNode) policy.lookup().getRoot()).getChild('a').getChild('c');
//        assertNull(policy.addMapping(acn));
        assertEquals(treeResult, policy.lookup());                
    }
    
    @Test
    public void testReappearIncrementalEmpty() {
        WordPolicy policy = new WordIncrementalReappear();

//        assertSame(1, policy.addMapping().size());
        assertEquals(new HashTree<Character>(), policy.lookup());
    }
    
    @Test
    public void testReappearIncremental() {
        WordPolicy policy = new WordIncrementalReappear();
      
        Tree<Character> treeResult = new HashTree<Character>();
        Node<Character> na = treeResult.add(null, 'a'),
                nb = treeResult.add(null, 'b'),
                nbc = treeResult.add(nb, 'c'),
                nac = treeResult.add(na, 'c'),
                naca = treeResult.add(nac, 'a'),
                nacd = treeResult.add(nac, 'd'),
                nacde = treeResult.add(nacd, 'e'),
                nacaf = treeResult.add(naca, 'f');
        
        notifyAll(wordsorphan, policy);

//        assertSame("" + policy.addMapping(), 7, policy.addMapping().size());
        Node acn = ((UnorderedNode) policy.lookup().getRoot()).getChild('a').getChild('c');
//        assertNull(policy.addMapping(acn));
        assertEquals(treeResult, policy.lookup());                
    }
    
    @Test
    public void testRootEmpty() {
        WordConnectionPolicy policy = new WordRoot();

        policy.update(new PseudoSet(), null);
        policy.connect();
//        assertSame(1, policy.addMapping().size());
        assertEquals(new HashTree<Character>(), policy.lookup());
    }
    
    @Test
    public void testRoot() {
        WordConnectionPolicy policy = new WordRoot();      
        Tree<Character> treeResult = new HashTree<Character>();
        Node<Character> na = treeResult.add(null, 'a'),
                nb = treeResult.add(null, 'b'),
                nbc = treeResult.add(nb, 'c'),
                naf = treeResult.add(na, 'f'),
                ne = treeResult.add(null, 'e');        
                
        policy.update(new PseudoSet(wordsorphan), null);
        policy.connect();
//        assertSame("" + policy.addMapping(), 7, policy.addMapping().size());
        assertEquals(treeResult, policy.lookup());                
    }
    
    @Test
    public void testRootIncrementalEmpty() {
        WordPolicy policy = new WordIncrementalRoot();

//        assertSame(1, policy.addMapping().size());
        assertEquals(new HashTree<Character>(), policy.lookup());
    }
    
    @Test
    public void testRootIncremental() {
        WordPolicy policy = new WordIncrementalRoot();      
        Tree<Character> treeResult = new HashTree<Character>();
        Node<Character> na = treeResult.add(null, 'a'),
                nb = treeResult.add(null, 'b'),
                nbc = treeResult.add(nb, 'c'),
                naf = treeResult.add(na, 'f'),
                ne = treeResult.add(null, 'e');        
                
        notifyAll(wordsorphan,policy);
//        assertSame("" + policy.addMapping(), 7, policy.addMapping().size());
        assertEquals(treeResult, policy.lookup());                
    }    
    
    @Test
    public void testCompactEmpty() {
        WordConnectionPolicy policy = new WordCompact();

        policy.update(new PseudoSet(), null);
        policy.connect();
//        assertSame(1, policy.addMapping().size());
        assertEquals(new HashTree<Character>(), policy.lookup());
    }
    
    @Test
    public void testCompact() {
        WordConnectionPolicy policy = new WordCompact();      
        Tree<Character> treeResult = new HashTree<Character>();
        Node<Character> na = treeResult.add(null, 'a'),
                nb = treeResult.add(null, 'b'),
                nbc = treeResult.add(nb, 'c'),
                naa = treeResult.add(na, 'a'),
                naaf = treeResult.add(naa, 'f'),
                nae = treeResult.add(na, 'e');
        
        policy.update(new PseudoSet(wordsorphan), null);
        policy.connect();
//        assertSame("" + policy.addMapping(), 7, policy.addMapping().size());
        assertEquals(treeResult, policy.lookup());                
    }
    
    @Test
    public void testCompactIncrementalEmpty() {
        WordPolicy policy = new WordIncrementalCompact();

//        assertSame(1, policy.addMapping().size());
        assertEquals(new HashTree<Character>(), policy.lookup());
    }
    
    @Test
    public void testCompactIncremental() {
        WordPolicy policy = new WordIncrementalCompact();      
        Tree<Character> treeResult = new HashTree<Character>();
        Node<Character> na = treeResult.add(null, 'a'),
                nb = treeResult.add(null, 'b'),
                nbc = treeResult.add(nb, 'c'),
                naa = treeResult.add(na, 'a'),
                naaf = treeResult.add(naa, 'f'),
                nae = treeResult.add(na, 'e');
        
        notifyAll(wordsorphan,policy);
        //        assertSame("" + policy.addMapping(), 7, policy.addMapping().size());
        assertEquals(treeResult, policy.lookup());                
    }
}
