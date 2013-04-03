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

package crdt.tree.fctree;

import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeSet;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class FCComparatorTest {

    public FCComparatorTest() {
    }

    @Test
    public void compare() {
        TreeSet<FCNode<String>> childrens = new TreeSet(new FCComparator());
        FCIdFactory fc1 = new FCIdFactory(1);
        FCIdFactory fc2 = new FCIdFactory(2);
        FCIdFactory fc3 = new FCIdFactory(3);
        FCPositionFactory fp = new FCPositionFactory();
        FCIdentifier id1 = fc1.createId();
        FCIdentifier id2 = fc2.createId();
        FCIdentifier id3 = fc3.createId();
        FCNode node1 = new FCNode(null, "a", fp.createBetweenNode(null, null, id1), id1);
        FCNode node2 = new FCNode(null, "c", fp.createBetweenNode(node1,null , id2), id2);
        FCNode node3 = new FCNode(null, "b", fp.createBetweenNode(node1, node2, id3), id3);


        childrens.add(node3);
        childrens.add(node2);
        childrens.add(node1);
        Iterator<FCNode<String>> it = childrens.iterator();
        for (String str : Arrays.asList("a", "b", "c")) {
            assertTrue(it.hasNext());
            FCNode<String> node = it.next();
            assertEquals(str, node.getValue());
        }

    }
}
