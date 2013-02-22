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

import crdt.CRDTMessage;
import crdt.PreconditionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;


/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class FCTreeTest {
    FCTree<String> tree;
    FCTree<String> tree2;
    
    @Before
    public void setUp() throws PreconditionException{
        tree=new FCTree();
        tree2=new FCTree();
        CRDTMessage mess1=tree.add(new ArrayList(), 0, "a");
        CRDTMessage mess2=tree.add(new ArrayList(), 1, "b");
        CRDTMessage mess3=tree.add(new ArrayList(), 2, "c");
        CRDTMessage mess4=tree.add(Arrays.asList(0), 0, "d");
        CRDTMessage mess5=tree.add(Arrays.asList(0), 1, "e");
        CRDTMessage mess6=tree.add(Arrays.asList(0,1), 1, "f");
        CRDTMessage mess7=tree.add(Arrays.asList(2), 0, "g");
        CRDTMessage mess8=tree.add(Arrays.asList(2), 1, "h");
        tree2.applyOneRemote(mess1);
        tree2.applyOneRemote(mess4);
        tree2.applyOneRemote(mess5);
        tree2.applyOneRemote(mess6);
        tree2.applyOneRemote(mess3);
        tree2.applyOneRemote(mess7);
        tree2.applyOneRemote(mess8);
        tree2.applyOneRemote(mess2);
    }

    public static void goodTreeAdd(FCTree tree){
         FCNode node = tree.getRoot();
        assertEquals(3, node.childrenNumber());
        assertEquals("a",node.getChild(0).getValue());
        assertEquals("b",node.getChild(1).getValue());
        assertEquals("c",node.getChild(2).getValue());
        assertEquals(0, node.getChild(1).childrenNumber());
        
        node=node.getChild(0);
        assertEquals(2, node.childrenNumber());
        assertEquals("d",node.getChild(0).getValue());
        assertEquals("e",node.getChild(1).getValue());
        assertEquals(0, node.getChild(0).childrenNumber());
        
        node=node.getChild(1);
        assertEquals(1, node.childrenNumber());
        assertEquals("f",node.getChild(0).getValue());
        assertEquals(0, node.getChild(0).childrenNumber());
        
        node=tree.getRoot().getChild(2);
        assertEquals(2, node.childrenNumber());
        assertEquals("g",node.getChild(0).getValue());
        assertEquals("h",node.getChild(1).getValue());
        assertEquals(0, node.getChild(0).childrenNumber());
        assertEquals(0, node.getChild(1).childrenNumber());
    }
    
    @Test
    public void addTest(){
        goodTreeAdd(tree);
        goodTreeAdd(tree2);
    }
    
       public static void goodTreeDel(FCTree tree){
            FCNode node = tree.getRoot();
        assertEquals(3, node.childrenNumber());
        assertEquals("a",node.getChild(0).getValue());
        assertEquals("b",node.getChild(1).getValue());
        assertEquals("c",node.getChild(2).getValue());
        assertEquals(0, node.getChild(1).childrenNumber());
        
        node=node.getChild(0);
        assertEquals(1, node.childrenNumber());
        assertEquals("d",node.getChild(0).getValue());
        assertEquals(0, node.getChild(0).childrenNumber());
        
         node=tree.getRoot().getChild(2);
        assertEquals(2, node.childrenNumber());
        assertEquals("g",node.getChild(0).getValue());
        assertEquals("h",node.getChild(1).getValue());
        assertEquals(0, node.getChild(0).childrenNumber());
        assertEquals(0, node.getChild(1).childrenNumber());
        }
    @Test
    public void removeTest() throws PreconditionException{
        CRDTMessage m=tree2.remove(Arrays.asList(0,1));
        tree.applyOneRemote(m);
        goodTreeDel(tree);
        goodTreeDel(tree2);
        
    }
    public static void goodTreeRename(FCTree tree){
         FCNode node = tree.getRoot();
        assertEquals(3, node.childrenNumber());
        assertEquals("ZoidBerg",node.getChild(0).getValue());
        assertEquals("b",node.getChild(1).getValue());
        assertEquals("c",node.getChild(2).getValue());
        assertEquals(0, node.getChild(1).childrenNumber());
        
        node=node.getChild(0);
        assertEquals(2, node.childrenNumber());
        assertEquals("d",node.getChild(0).getValue());
        assertEquals("e",node.getChild(1).getValue());
        assertEquals(0, node.getChild(0).childrenNumber());
        
        node=node.getChild(1);
        assertEquals(1, node.childrenNumber());
        assertEquals("f",node.getChild(0).getValue());
        assertEquals(0, node.getChild(0).childrenNumber());
         
        node=tree.getRoot().getChild(2);
        assertEquals(2, node.childrenNumber());
        assertEquals("g",node.getChild(0).getValue());
        assertEquals("h",node.getChild(1).getValue());
        assertEquals(0, node.getChild(0).childrenNumber());
        assertEquals(0, node.getChild(1).childrenNumber());
    }
    
    @Test
    public void ChLabelTest(){
        CRDTMessage m=tree2.rename(Arrays.asList(0), "ZoidBerg");
        tree.applyOneRemote(m);
        goodTreeRename(tree);
        goodTreeRename(tree2);
        
    }
    
    public static void goodTreeMove(FCTree tree){
         FCNode node = tree.getRoot();
        assertEquals(3, node.childrenNumber());
        assertEquals("a",node.getChild(0).getValue());
        assertEquals("b",node.getChild(1).getValue());
        assertEquals("c",node.getChild(2).getValue());
        assertEquals(0, node.getChild(1).childrenNumber());
        
        
        node=node.getChild(0);
        assertEquals(1, node.childrenNumber());
        assertEquals("d",node.getChild(0).getValue());
        assertEquals(0, node.getChild(0).childrenNumber());
        
        node=tree.getRoot().getChild(2);
        assertEquals(3, node.childrenNumber());
        assertEquals("g",node.getChild(0).getValue());
        assertEquals("e",node.getChild(1).getValue());
        assertEquals("h",node.getChild(2).getValue());
        assertEquals(0, node.getChild(0).childrenNumber());
        assertEquals(1, node.getChild(1).childrenNumber());
        assertEquals(0, node.getChild(2).childrenNumber());
        
        node=node.getChild(1);
        assertEquals(1, node.childrenNumber());
        assertEquals("f",node.getChild(0).getValue());
        assertEquals(0, node.getChild(0).childrenNumber());
         
        
    }
    @Test
    public void switchTest(){
        CRDTMessage m=tree2.move(Arrays.asList(0),Arrays.asList(1));
        tree.applyOneRemote(m);
        
        FCNode node=tree.getRoot();
        
        assertEquals("b",node.getChild(0).getValue());
        assertEquals("a",node.getChild(1).getValue());
        assertEquals("c",node.getChild(2).getValue());
    }
    @Test
    public void moveTest(){
        CRDTMessage m=tree2.move(Arrays.asList(0,1),Arrays.asList(2,1));
        tree.applyRemote(m);
        goodTreeMove(tree);
        goodTreeMove(tree2);
    }
    
    
    public FCTreeTest() {
    }

    
   /* public static class NodeMock{
        List<NodeMock> children;
        String name;
        public NodeMock(String name,NodeMock... node){
           children=Arrays.asList(node);
           this.name=name;
        }
        public boolean equals(FCNode<String> node){
           String val=node.getValue();
           if((name!=null && !name.equals(val)) || (name == null && val!=null)){
                   return false;
           }
           Iterator<FCNode> it=node.getElements().iterator();
           
           
        }
    }*/
}
