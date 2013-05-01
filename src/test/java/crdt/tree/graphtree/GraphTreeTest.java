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
package crdt.tree.graphtree;

import crdt.Factory;
import crdt.set.CRDTSet;
import crdt.set.counter.CommutativeCounterSet;
import crdt.set.counter.ConvergentCounterSet;
import crdt.set.lastwriterwins.CommutativeLwwSet;
import crdt.set.lastwriterwins.ConvergentLwwSet;
import crdt.set.observedremove.CommutativeOrSet;
import crdt.set.observedremove.ConvergentOrSet;
import crdt.tree.CrdtTreeGeneric;
import crdt.tree.graphtree.connection.GraphCompact;
import crdt.tree.graphtree.connection.GraphReappear;
import crdt.tree.graphtree.connection.GraphRoot;
import crdt.tree.graphtree.connection.GraphSkip;
import crdt.tree.graphtree.mapping.GraphSeveral;
import crdt.tree.graphtree.mapping.GraphZero;

import org.junit.Test;


/**
 *
 * @author Mehdi
 */
public class GraphTreeTest {
    
    public GraphTreeTest() {
    }
    
     public void testGraphBasic(Factory<CRDTSet> sf) throws Exception {
        CrdtTreeGeneric test = new CrdtTreeGeneric();
        
//        test.runAllBasic(new Gtree(sf.create(), new GraphSkip(), new GraphZero()));
//        test.runAllBasic(new Gtree(sf.create(), new GraphRoot(), new GraphZero()));
//        test.runAllBasic(new Gtree(sf.create(), new GraphReappear(), new GraphZero()));
//        test.runAllBasic(new Gtree(sf.create(), new GraphCompact(), new GraphZero()));
//        test.runAllBasic(new Gtree(sf.create(), new GraphSkip(), new GraphSeveral()));
//        test.runAllBasic(new Gtree(sf.create(), new GraphRoot(), new GraphSeveral()));
//        test.runAllBasic(new Gtree(sf.create(), new GraphReappear(), new GraphSeveral()));
//        test.runAllBasic(new Gtree(sf.create(), new GraphCompact(), new GraphSeveral()));
     }
     
     
     @Test
    public void testBasicCmCounter() throws Exception {
        testGraphBasic(new CommutativeCounterSet());
    }

    @Test
    public void testBasicCvCounter() throws Exception {
        testGraphBasic(new ConvergentCounterSet());
    }

    @Test
    public void testBasicCmLww() throws Exception {
        testGraphBasic(new CommutativeLwwSet());
    }

    @Test
    public void testBasicCvLww() throws Exception {
        testGraphBasic(new ConvergentLwwSet());
    }
    
    @Test
    public void testBasicCmOr() throws Exception {
        testGraphBasic(new CommutativeOrSet());
    }

    @Test
    public void testBasicCvOr() throws Exception {
        testGraphBasic(new ConvergentOrSet());
    }
}
    
