/*
 *  Replication Benchmarker
 *  https://github.com/score-team/replication-benchmarker/
 *  Copyright (C) 2012 LORIA / Inria / SCORE Team
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */
package crdt.tree.fctree;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class FCPositionTest {
    
    public FCPositionTest() {
    }

    @Test
    public void PostionComparaison() {
        FCIdFactory fc1 = new FCIdFactory(1);
        FCIdFactory fc2 = new FCIdFactory(2);
        FCIdFactory fc3 = new FCIdFactory(3);
        
        FCPositionFactory fp = new FCPositionFactory();
        
        FCIdentifier id1 = fc1.createId();
        FCIdentifier id2 = fc2.createId();
        FCIdentifier id3 = fc3.createId();
        
        FCPosition pa= fp.createBetweenPosition(null, null, id1);
        FCPosition pc= fp.createBetweenPosition(pa, null, id2);//after a
        FCPosition pb= fp.createBetweenPosition(pa, pc, id3);// between a and c
        
        assertEquals(0,pa.compareTo(pa));
        assertEquals(-1,pa.compareTo(pb));
        assertEquals(-1,pa.compareTo(pc));
        
        assertEquals(1,pb.compareTo(pa));
        assertEquals(0,pb.compareTo(pb));
        assertEquals(-1,pb.compareTo(pc));
        
        assertEquals(1,pc.compareTo(pa));
        assertEquals(1,pc.compareTo(pb));
        assertEquals(0,pc.compareTo(pc));
        
    }
}
