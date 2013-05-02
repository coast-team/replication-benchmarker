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
 
package crdt.tree.fctree.Operations;

import crdt.PreconditionException;
import crdt.tree.fctree.FCIdentifier;
import crdt.tree.fctree.FCNodeGf;
import crdt.tree.fctree.FCPosition;
import crdt.tree.fctree.FCTree;
import crdt.tree.fctree.FCTreeGf;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class ChXTest {

    FCTree<String> tree;

    @Before
    public void setUp() throws PreconditionException {
        tree = new FCTreeGf();
        tree.add(new ArrayList(), 0, "a");
        tree.add(new ArrayList(), 1, "b");
        tree.add(new ArrayList(), 2, "c");
        tree.add(Arrays.asList(0), 0, "d");
        tree.add(Arrays.asList(0), 1, "e");
        tree.add(Arrays.asList(0, 1), 1, "f");
        tree.add(Arrays.asList(2), 0, "g");
        tree.add(Arrays.asList(2), 1, "h");

    }

    public ChXTest() {
        
    }

    @Test
    public void ChLabel() {
        //tree.
    }

    
    
    @Test
    public void ChOrder() {
        FCNodeGf node=(FCNodeGf)tree.getRoot();
        FCIdentifier id= tree.getIdFactory().createId();
        FCPosition pos= tree.getPositionFactory().createBetweenNode(node.getChild(2),null,id);
        ChX operation = new ChX(id, (FCNodeGf)node.getChild(0),pos, FCNodeGf.FcLabels.priority);
        tree.applyOneRemote(operation);
        assertEquals("b", node.getChild(0).getValue());
        assertEquals("c", node.getChild(1).getValue());
        assertEquals("a", node.getChild(2).getValue());
                
    }

    @Test
    public void Move() {
    }
}
