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
package crdt.tree.orderedtree.renderer;

import crdt.CRDTMessage;
import crdt.PreconditionException;
import crdt.tree.fctree.FCTree;
import crdt.tree.fctree.FCTreeT;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class SizeJSonStyleDocTest {
 FCTree<String> tree;
    FCTree<String> tree2;
    SizeJSonStyleDoc size;

    @Before
    public void setUp() throws PreconditionException {
        tree = new FCTreeT();
        tree2 = new FCTreeT();
        CRDTMessage mess1 = tree.add(new ArrayList(), 0, "a");
        CRDTMessage mess2 = tree.add(new ArrayList(), 1, "b");
        CRDTMessage mess3 = tree.add(new ArrayList(), 2, "c");
        CRDTMessage mess4 = tree.add(Arrays.asList(0), 0, "d");
        CRDTMessage mess5 = tree.add(Arrays.asList(0), 1, "e");
        CRDTMessage mess6 = tree.add(Arrays.asList(0, 1), 1, "f");
        CRDTMessage mess7 = tree.add(Arrays.asList(2), 0, "g");
        CRDTMessage mess8 = tree.add(Arrays.asList(2), 1, "h");
        tree2.applyOneRemote(mess1);
        tree2.applyOneRemote(mess4);
        tree2.applyOneRemote(mess5);
        tree2.applyOneRemote(mess6);
        tree2.applyOneRemote(mess3);
        tree2.applyOneRemote(mess7);
        tree2.applyOneRemote(mess8);
        tree2.applyOneRemote(mess2);
        size = new SizeJSonStyleDoc();

    }

    @Test
    public void xmlRendering() {
        System.out.println(size.view(tree.getRoot()));
        assertEquals("{\"a\":{\"d\", \"e\":\"f\"}, \"b\", \"c\":{\"g\", \"h\"}}"
                , size.view(tree.getRoot()));
    }


    @Test
    public void Size() throws IOException {
        assertEquals(41, size.serializ(tree));

    }
}
