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
package jbenchmarker.logootsplitO;

import crdt.Operation;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class LogootSRopes<T> implements LogootSDoc<T>, Serializable {

    int replicatNumber = 0;
    int clock = 0;
    RopesNodes root = null;
    private HashMap<List<Integer>, LogootSBlockLight> mapBaseToBlock = new HashMap<List<Integer>, LogootSBlockLight>(); //for test

    HashMap<List<Integer>, LogootSBlockLight> getMapBaseToBlock() {
        return mapBaseToBlock;
    }

    public LogootSBlockLight getBlock(IdentifierInterval id) {
        LogootSBlockLight ret = mapBaseToBlock.get(id.base);
        if (ret == null) {
            ret = new LogootSBlockLight<String>(id);
            mapBaseToBlock.put(id.base, ret);
        }
        return ret;
    }

    @Override
    public void addBlock(Identifier id, List<T> str) {
//        //size += str.size();
//        LinkedList l2;//= new LinkedList();
//       /* if (scoreCheckT(root, l2)) {
//         System.out.println("\n\n" + root.viewRec());
//         System.out.println(l2);
//         }*/
//        LinkedList path42 = searchErr();
//        assert (path42 == null);

        IdentifierInterval idi = new IdentifierInterval(id.base, id.last, id.last + str.size() - 1);
        if (root == null) {
            LogootSBlockLight bl = new LogootSBlockLight(idi);
            mapBaseToBlock.put(bl.id.base, bl);
            root = new RopesNodes(str, id.getLast(), bl);
        } else {
            addBlock(idi, str, root);
        }
//        l2 = new LinkedList();
//
//        if (scoreCheckT(root, l2)) {
//            System.out.println("\n\n" + root.viewRec());
//            System.out.println(l2);
//        }
    }

    void addBlock(IdentifierInterval idi, List<T> str, RopesNodes from) {
        LinkedList<RopesNodes> path = new LinkedList();
        LinkedList<RopesNodes> path2;
        boolean con = true;
        while (con) {
            path.add(from);
            IteratorHelperIdentifier ihi = new IteratorHelperIdentifier(idi, from.getIdentifierInterval());
            //System.out.println(ihi.computeResults() + "; " + from + "; " + str + " " + idi.getBegin());
            int split;
            switch (ihi.computeResults()) {
                case B1AfterB2:
                    if (from.getRight() == null) {
                        from.setRight(new RopesNodes(str, idi.getBegin(), getBlock(idi)));
                        con = false;
                    } else {
                        from = from.getRight();
                    }
                    break;
                case B1BeforeB2:
                    if (from.getLeft() == null) {
                        from.setLeft(new RopesNodes(str, idi.getBegin(), getBlock(idi)));
                        con = false;
                    } else {
                        from = from.getLeft();
                    }
                    break;
                case B1InsideB2: //split b2 the object node
                    //int split = maxOffsetBeforeNex(node.getIdBegin(), id, node.str.size() + node.offset - 1);
                    split = Math.min(from.maxOffset(), ihi.getNextOffset());
                    RopesNodes rp = new RopesNodes(str, idi.getBegin(), getBlock(idi));
                    path.add(from.split(split - from.offset + 1, rp));
                    con = false;
                    break;
                case B2insideB1: // split b1 the node to insert
                    //int split2 = maxOffsetBeforeNex(id, node.getIdBegin(), str.size() + id.last - 1);
                    int split2 = /*Math.min(idi.getEnd(), */ ihi.getNextOffset()/*)*/;
                    List ls = str.subList(0, split2 + 1 - idi.getBegin());
                    IdentifierInterval idi1 = new IdentifierInterval(idi.base, idi.getBegin(), split2);
                    if (from.getLeft() == null) {
                        from.setLeft(new RopesNodes(ls, idi1.getBegin(), getBlock(idi1)));
                    } else {
                        addBlock(idi1, ls, from.getLeft());
                    }
                    ls = str.subList(split2 + 1 - idi.getBegin(), str.size());
                    idi1 = new IdentifierInterval(idi.base, split2 + 1, idi.end);
                    if (from.getRight() == null) {
                        from.setRight(new RopesNodes(ls, idi1.getBegin(), getBlock(idi1)));
                    } else {
                        addBlock(idi1, ls, from.getRight());
                    }
                    return;
                case B1concatB2: //node to insert concat the node
                    if (from.getLeft() != null) {
                        path2 = (LinkedList) path.clone();
                        path2.add(from.getLeft());
                        getXest(RopesNodes.RIGHT, path2);

                        split = from.getIdBegin().minOffsetAfterPrev(path2.getLast().getIdEnd(), idi.getBegin());
                        List l = str.subList(split + 1 - idi.getBegin(), str.size());
                        from.appendBegin(l);
                        ascendentUpdate(path, 0, l.size());
                        str = str.subList(0, split + 1 - idi.getBegin());
                        idi = new IdentifierInterval(idi.base, idi.begin, split);

                        //check if previous is smaller or not
                        if (idi.end >= idi.begin) {
                            from = from.getLeft();
                        } else {
                            return;
                        }
                    } else {
                        from.appendBegin(str);
                        ascendentUpdate(path, 0, str.size());
                        return;
                    }



                    break;
                case B2ConcatB1://concat at end
                    if (from.getRight() != null) {
                        path2 = (LinkedList) path.clone();
                        path2.add(from.getRight());
                        getXest(RopesNodes.LEFT, path2);

                        split = from.getIdEnd().maxOffsetBeforeNex(path2.getLast().getIdBegin(), idi.getEnd());
                        List l = str.subList(0, split + 1 - idi.getBegin());
                        from.appendEnd(l);
                        ascendentUpdate(path, 0, l.size());
                        str = str.subList(split + 1 - idi.getBegin(), str.size());
                        idi = new IdentifierInterval(idi.base, split+1, idi.end);
                        if (idi.end >= idi.begin) {
                            from = from.getRight();
                        } else {
                            return;
                        }
                    } else {
                        from.appendEnd(str);
                        ascendentUpdate(path, 0, str.size());
                        return;
                    }

                    break;
                default:
                    throw new UnsupportedOperationException("Not implemented yet");
            }
        }
        balance(path);

    }

    boolean searchFull(RopesNodes node, Identifier id, LinkedList<RopesNodes> path) {
        if (node == null) {
            return false;
        }
        path.add(node);
        if (node.getIdBegin().compareTo(id) == 0
                || searchFull(node.getLeft(), id, path)
                || searchFull(node.getRight(), id, path)) {
            return true;
        }
        path.removeLast();
        return false;
    }

    RopesNodes mkNode(Identifier id1, Identifier id2, List l) {
        List<Integer> base = IDFactory.createBetweenPosition(id1, id2, replicatNumber, clock++);
        IdentifierInterval idi = new IdentifierInterval(base, 0, l.size() - 1);
        LogootSBlockLight newBlock = new LogootSBlockLight(idi);
        mapBaseToBlock.put(idi.base, newBlock);
        return new RopesNodes(l, 0, newBlock);
    }
    //Todo: improve readability with search function

    @Override
    public LogootSOp insertLocal(int pos, List l) {

        if (root == null) {//empty tree
            root = mkNode(null, null, l);
            root.block.setMine(true);
            return new LogootSOpAdd(root.getIdBegin(), l);
        } else {
            RopesNodes newNode;
            int length = this.viewLength();
            LinkedList<RopesNodes> path;
            if (pos == 0) {//begin of string
                // System.out.println("begin");
                path = new LinkedList();
                path.add(root);
                RopesNodes n = getXest(RopesNodes.LEFT, path);
                if (n.isAppendableBefore()) {
                    Identifier id = n.appendBegin(l);
                    ascendentUpdate(path, 0, l.size());
                    return new LogootSOpAdd(id, l);
                } else {//add node
                    newNode = mkNode(null, n.getIdBegin(), l);
                    newNode.block.setMine(true);
                    n.setLeft(newNode);
                }
            } else if (pos >= length) {//end
                // System.out.println("end");
                path = new LinkedList();
                path.add(root);
                RopesNodes n = getXest(RopesNodes.RIGHT, path);
                if (n.isAppendableAfter()) {//append
                    Identifier id = n.appendEnd(l);
                    ascendentUpdate(path, 0, l.size());
                    return new LogootSOpAdd(id, l);
                } else {//add at end
                    newNode = mkNode(n.getIdEnd(), null, l);
                    newNode.block.setMine(true);
                    n.setRight(newNode);
                }

            } else {//middle
                ResponseIntNode inPos = search(pos);
                if (inPos.getI() > 0) {//split
                    //   System.out.println("split");
                    Identifier id1 = inPos.getNode().block.id.getBaseId(inPos.getNode().offset + inPos.getI() - 1);
                    Identifier id2 = inPos.getNode().block.id.getBaseId(inPos.getNode().offset + inPos.getI());
                    newNode = mkNode(id1, id2, l);
                    newNode.block.setMine(true);
                    path = inPos.getPath();
                    path.add(inPos.getNode().split(inPos.getI(), newNode));
                } else {
                    ResponseIntNode prev = search(pos - 1);
                    if (inPos.getNode().isAppendableBefore() && inPos.getNode().getIdBegin().hasPlaceBefore(prev.getNode().getIdEnd(), l.size())) {//append before
                        //     System.out.println("Append before");
                        Identifier id = inPos.getNode().appendBegin(l);
                        ascendentUpdate(inPos.path, 0, l.size());
                        //   path42 = searchErr();
                        // assert (path42 == null);

                        return new LogootSOpAdd(id, l);
                    } else {

                        if (prev.getNode().isAppendableAfter() && prev.getNode().getIdEnd().hasPlaceAfter(inPos.getNode().getIdBegin(), l.size())) {//append after
                            //   System.out.println("Append after");
                            Identifier id = prev.getNode().appendEnd(l);
                            ascendentUpdate(prev.path, 0, l.size());
                            // path42 = searchErr();
                            // assert (path42 == null);

                            return new LogootSOpAdd(id, l);
                        } else {
                            //System.out.println("between");
                            newNode = mkNode(prev.getNode().getIdEnd(), inPos.getNode().getIdBegin(), l);
                            newNode.block.setMine(true);
                            newNode.setRight(prev.getNode().getRight());
                            prev.getNode().setRight(newNode);
                            path = prev.getPath();
                            path.add(newNode);
                        }
                    }
                }
            }

            //path42 = searchErr();
            //assert (path42 == null);
            balance(path);

            /*TEST===*/
//            LinkedList<RopesNodes> l25 = new LinkedList();
//            LinkedList<RopesNodes> lp25 = new LinkedList();



            /*  boolean b = true;
             if (!search(newNode.getIdBegin(), l25)) {
             b = searchFull(this.root, newNode.getIdBegin(), lp25);
             }
             if (l25.getLast().getIdBegin().compareTo(newNode.getIdBegin()) != 0) {
             assert (false);
             }


             l25 = new LinkedList();
             if (scoreCheckT(root, l25)) {
             System.out.println("\n\n" + root.viewRec());
             System.out.println(l25);
             }*/

            /*end Test */



            return new LogootSOpAdd(newNode.getIdBegin(), l);
        }
    }

//    public LinkedList searchErr() {
//        LinkedList path = new LinkedList();
//        if (searchErr(root, path)) {
//            return path;
//        } else {
//            return null;
//        }
//
//    }
//
//    boolean searchErr(RopesNodes node, LinkedList<RopesNodes> path) {
//        if (node == null) {
//            return false;
//        }
//        path.add(node);
//        if (node.getRight() != null && node.getRight().getIdBegin().compareTo(node.getIdEnd()) < 0) {
//            return true;
//        }
//        if (node.getLeft() != null && node.getLeft().getIdEnd().compareTo(node.getIdBegin()) > 0) {
//            return true;
//        }
//        if (searchErr(node.getLeft(), path)
//                || searchErr(node.getRight(), path)) {
//            return true;
//        }
//        path.removeLast();
//        return false;
//    }
    /*public RopesNodes getPrevious(LinkedList<RopesNodes> path) {
     RopesNodes n = path.getLast();
     if (n.getLeft() != null) {
     if (n.getLeft().getRight() == null) {
     return n.getLeft();
     } else {
     return getXest(RopesNodes.RIGHT, n);
     }
     } else {
     if (path.size() > 1) {
     }
     }


     }

     */
    RopesNodes getXest(int i, RopesNodes n) {
        while (n.getChild(i) != null) {
            n = n.getChild(i);
        }
        return n;
    }

    RopesNodes getXest(int i, LinkedList<RopesNodes> path) {
        RopesNodes n = path.getLast();
        while (n.getChild(i) != null) {
            n = n.getChild(i);
            path.add(n);
        }
        return n;
    }

    boolean search(Identifier id, LinkedList<RopesNodes> path) {

        RopesNodes node = root;
        while (node != null) {
            path.addLast(node);
            if (id.compareTo(node.getIdBegin()) < 0) {
                node = node.getLeft();
            } else if (id.compareTo(node.getIdEnd()) > 0) {
                node = node.getRight();
            } else {
                return true;
            }
        }
        return false;
    }

    ResponseIntNode search(int pos) {
        RopesNodes node = root;
        LinkedList<RopesNodes> path = new LinkedList();
        while (node != null) {
            path.add(node);
            int before = node.getLeft() == null ? 0 : node.getLeft().sizeNodeAndChildren;
            if (pos < before) {//Before
                node = node.getLeft();
            } else if (pos < before + node.str.size()) {
                return new ResponseIntNode(pos - before, node, path);
            } else {
                pos -= before + node.str.size();
                node = node.getRight();
            }
        }
        return null;
    }

    void ascendentUpdate(LinkedList<RopesNodes> path, int node, int string) {
        Iterator<RopesNodes> it = path.descendingIterator();
        while (it.hasNext()) {
            it.next().addNums(node, string);
        }
    }

    @Override
    public LogootSOp delLocal(int begin, int end) {
//        LinkedList l = new LinkedList();
//        if (scoreCheckT(root, l)) {
//            System.out.println("\n\n" + root.viewRec());
//            System.out.println(l);
//        }
//        LinkedList path42 = searchErr();
//        assert (path42 == null);
//        assert (begin < this.view().length());
//        assert (end < this.view().length());
        // size -= end - begin + 1;
        int lenght = end - begin + 1;
        List<IdentifierInterval> li = new LinkedList<IdentifierInterval>();
        do {
            ResponseIntNode start = search(begin);

            int be = start.node.offset + start.getI();
            int en = Math.min(be + lenght - 1, start.node.maxOffset());
            //int i = this.view().length();
            li.add(new IdentifierInterval(start.getNode().block.getId().getBase(), be, en));
            RopesNodes r = start.node.deleteOffsets(be, en);
            lenght -= en - be + 1;
            //begin -= en - be+1;

            if (start.node.getSize() == 0) {
                // assert (i - this.view().length() == en - be + 1);
                delNode(start.getPath());
                //assert (i - this.view().length() == en - be + 1);
                //this.ascendentUpdate(start.path, 1, en-be);
            } else if (r != null) {
                start.path.add(r);
                balance(start.path);
            } else {
                this.ascendentUpdate(start.path, 0, be - en - 1);
            }


//            path42 = searchErr();
//            assert (path42 == null);
//            l = new LinkedList();
//            if (scoreCheckT(root, l)) {
//                System.out.println("\n\n" + root.viewRec());
//                System.out.println(l);
//            }
        } while (lenght > 0);

        return new LogootSOpDel(li);
    }

    void delNode(LinkedList< RopesNodes> path) {
        RopesNodes node = path.getLast();
        //assert (node.block.numberOfElements() >= 0);
        if (node.block.numberOfElements() == 0) {
            this.mapBaseToBlock.remove(node.block.id.base);
        }
        if (node.getRight() == null) {
            if (node == root) {
                root = node.getLeft();
            } else {
                path.removeLast();
                path.getLast().replaceChildren(node, node.getLeft());
            }
        } else if (node.getLeft() == null) {
            if (node == root) {
                root = node.getRight();
            } else {
                path.removeLast();
                path.getLast().replaceChildren(node, node.getRight());
            }
        } else {//two children
            path.add(node.getRight());
            RopesNodes min = getMinPath(path);
            node.become(min);
            path.removeLast();
            path.getLast().replaceChildren(min, min.getRight());
        }
        balance(path);

    }

    RopesNodes getMinPath(LinkedList<RopesNodes> path) {
        RopesNodes node = path.getLast();
        if (node == null) {
            return null;
        }
        while (node.getLeft() != null) {
            node = node.getLeft();
            path.add(node);
        }
        return node;
    }

    RopesNodes getLeftest(RopesNodes node) {
        if (node == null) {
            return null;
        }
        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node;
    }

    Identifier getMinId(RopesNodes node) {
        RopesNodes back = getLeftest(node);
        return back != null ? back.getIdBegin() : null;
    }

    /*
     * Balancing 
     */
    /**
     * Balance tree on path ascendent
     *
     * @param path
     */
    void balance(LinkedList<RopesNodes> path) {
//        LinkedList path42 = searchErr();
//        assert (path42 == null);
        Iterator<RopesNodes> it = path.descendingIterator();
        RopesNodes node = it.hasNext() ? it.next() : null;
        RopesNodes father = it.hasNext() ? it.next() : null;
        while (node != null) {
            node.sumDirectChildren();
            int balance = node.balanceScore();
            if (balance >= 2) {
                if (node.getRight() != null && node.getRight().balanceScore() == -1) {
                    rotateRL(node, father);
                } else {
                    rotateLeft(node, father);
                }
            } else if (balance <= -2) {
                if (node.getLeft() != null && node.getLeft().balanceScore() == 1) {
                    rotateLR(node, father);
                } else {
                    rotateRight(node, father);
                }
            }
            node = father;
            father = it.hasNext() ? it.next() : null;
        }
//        path42 = searchErr();
//        assert (path42 == null);
    }

    RopesNodes rotateLeft(RopesNodes node, RopesNodes father) {
        RopesNodes r = node.getRight();
        if (node == root) {
            root = r;
        } else {
            father.replaceChildren(node, r);
        }
        node.setRight(r.getLeft());
        r.setLeft(node);
        node.sumDirectChildren();
        r.sumDirectChildren();
        return r;
    }

    RopesNodes rotateRight(RopesNodes node, RopesNodes father) {
        RopesNodes r = node.getLeft();
        if (node == root) {
            root = r;
        } else {
            father.replaceChildren(node, r);
        }
        node.setLeft(r.getRight());
        r.setRight(node);
        node.sumDirectChildren();
        r.sumDirectChildren();
        return r;
    }
    //public void remove()

    RopesNodes rotateRL(RopesNodes node, RopesNodes father) {
        rotateRight(node.getRight(), node);
        return rotateLeft(node, father);

    }

    RopesNodes rotateLR(RopesNodes node, RopesNodes father) {
        rotateLeft(node.getLeft(), node);
        return rotateRight(node, father);
    }

    @Override
    public void delBlock(IdentifierInterval id) {
//        LinkedList path42 = searchErr();
//        assert (path42 == null);
//        LinkedList l2 = new LinkedList();
//        if (scoreCheckT(root, l2)) {
//            System.out.println("\n\n" + root.viewRec());
//            System.out.println(l2);
//        }
        while (true) {
            LinkedList<RopesNodes> path = new LinkedList<RopesNodes>();
            // LinkedList<RopesNodes> path = ;
            if (!search(id.getBeginId(), path)) {
                if (id.getBegin() < id.end) {
                    id = new IdentifierInterval(id.base, id.getBegin() + 1, id.end);
                } else {
//                    path42 = searchErr();
//                    assert (path42 == null);
                    return;
                }

            } else {
                RopesNodes node = path.getLast();
                int end = Math.min(id.end, node.maxOffset());
                RopesNodes t = node.deleteOffsets(id.getBegin(), end);
                //size -= end - id.getBegin() - 1;
                if (node.getSize() == 0) {//del node
                    delNode(path);
                } else if (t != null) {
                    path.add(t);
                    balance(path);

                } else {
                    ascendentUpdate(path, 0, id.getBegin() - end - 1);
                }
                if (end == id.end) {
                    break;
                } else {
                    id = new IdentifierInterval(id.base, end, id.end);
                }
            }
        }

//        path42 = searchErr();
//        assert (path42 == null);
//        l2 = new LinkedList();
//        if (scoreCheckT(root, l2)) {
//            System.out.println("\n\n" + root.viewRec());
//            System.out.println(l2);
//        }
    }

    boolean getNext(LinkedList<RopesNodes> path) {
        RopesNodes node = path.getLast();
        if (node.getRight() == null) {
            if (path.size() > 1) {
                RopesNodes father = path.get(path.size() - 2);
                if (father.getLeft() == node) {
                    path.removeLast();
                    return true;
                }
            }
            return false;
        } else {
            path.add(node.getRight());
            getXest(RopesNodes.LEFT, path);
            return true;
        }
    }

    @Override
    public LogootSDoc create() {
        return new LogootSRopes();
    }

    @Override
    public void setReplicaNumber(int i) {
        this.replicatNumber = i;
    }

    @Override
    public String view() {
        if (root == null) {
            return "";
        }
        StringBuilder ret = new StringBuilder(root.sizeNodeAndChildren);
        LinkedList<RopesNodes> pile = new LinkedList();

        pile.add(root);
        RopesNodes<T> n = root;
        while (pile.size() > 0) {
            while (n.getLeft() != null) {
                pile.addLast(n.getLeft());
                n = n.getLeft();
            }
            do {
                for (T t : n.str) {
                    ret.append(t);
                }
                pile.removeLast();
                if (n.getRight() != null) {
                    pile.addLast(n.getRight());
                    n = n.getRight();
                    break;
                }
                n = pile.size() > 0 ? pile.getLast() : null;
            } while (pile.size() > 0);
        }

        return ret.toString();
    }

//    static boolean scoreCheckT(RopesNodes node, LinkedList<RopesNodes> list) {
//        if (node == null) {
//            return false;
//        }
//        boolean ret = false;
//        list.add(node);
//        ret = scoreCheckT(node.getLeft(), list);
//        ret |= scoreCheckT(node.getRight(), list);
//        int nodeinsub = 1 + node.getNodesInSubtree(0) + node.getNodesInSubtree(1);
//        if (node.getNodesInSubtree() != nodeinsub) {
//            System.err.println("error number node : " + node.getNodesInSubtree() + "<>" + nodeinsub + " " + node.str);
//            ret = true;
//        }
//        nodeinsub = node.str.size() + node.getSizeNodeAndChildren(0) + node.getSizeNodeAndChildren(1);
//        if (node.getSizeNodeAndChildren() != nodeinsub) {
//            System.err.println("error lenght : " + node.getSizeNodeAndChildren() + "<>" + nodeinsub + " " + node.str);
//            ret = true;
//        }
//        list.removeLast();
//        return ret;
//    }
    @Override
    public int viewLength() {
        int ret = root == null ? 0 : root.sizeNodeAndChildren;
//        if (ret != view().length()) {
//            System.out.println(root.viewRec());
//            System.out.println("");
//            scoreCheckT(root, new LinkedList());
//            assert (false);
//        }

        return ret;
    }

    @Override
    public void apply(Operation op) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static class RopesNodes<T> {

        /**
         * Node childrenLeftRight
         */
        public static int LEFT = 0;
        public static int RIGHT = 1;
        private RopesNodes[] childrenLeftRight = new RopesNodes[2];
        private int nodesInSubtree = 1;
        private int sizeNodeAndChildren = 0;
        /**
         * String info
         */
        int offset;
        List<T> str;
        LogootSBlockLight<String> block;

        public Identifier getIdBegin() {
            return this.block.id.getBaseId(offset);
        }

        public Identifier getIdEnd() {
            return this.block.id.getBaseId(offset + str.size() - 1);
        }

        public RopesNodes(List<T> str, int offset, LogootSBlockLight block) {
            this(str, offset, block, true);
        }

        public RopesNodes(List<T> str, int offset, LogootSBlockLight block, boolean newer) {
            this.str = new ArrayList(str);
            this.block = block;
            this.offset = offset;
            this.sizeNodeAndChildren = str.size();
            if (newer && block != null) {
                block.addBlock(offset, str);
            }
        }

        public void addNums(int node, int string) {
            this.nodesInSubtree += node;
            this.sizeNodeAndChildren += string;
        }

        public RopesNodes getChild(int i) {
            return childrenLeftRight[i];
        }

        public Identifier appendEnd(List s) {
            int b = this.maxOffset() + 1;
            str.addAll(s);
            block.addBlock(b, s);
            return this.block.id.getBaseId(b);
        }

        public Identifier appendBegin(List s) {
            str.addAll(0, s);
            offset -= s.size();
            block.addBlock(this.offset, s);
            return this.getIdBegin();
        }

        public RopesNodes deleteOffsets(int begin, int end) {
//            assert (begin >= this.offset);
//            assert (end < this.offset + str.size());
//            assert (begin <= end);
            int sizeToDelete = end - begin + 1;
            //this.sizeNodeAndChildren -=  sizeToDelete;     
            this.block.delBlock(begin, end, sizeToDelete);
            if (sizeToDelete == this.str.size()) {
                this.str.clear();
                return null;
            }
            RopesNodes ret = null;
            if (end == this.offset + str.size() - 1) {
                this.str = str.subList(0, begin - offset);
            } else if (begin == this.offset) {
                this.str = str.subList(end - offset + 1, str.size());
                offset = end + 1;
            } else {
                ret = this.split(end - offset + 1);
                str = str.subList(0, begin - offset);
            }
            return ret;
        }

        public RopesNodes split(int size) {
            this.nodesInSubtree++;
            RopesNodes n = new RopesNodes(new ArrayList(str.subList(size, str.size())), offset + size, block, false);
            this.str = new ArrayList(str.subList(0, size));
            if (this.childrenLeftRight[RIGHT] != null) {
                n.childrenLeftRight[RIGHT] = this.childrenLeftRight[RIGHT];
                n.nodesInSubtree += n.childrenLeftRight[RIGHT].nodesInSubtree + 1;
                n.sizeNodeAndChildren += n.childrenLeftRight[RIGHT].sizeNodeAndChildren;
            }
            this.childrenLeftRight[RIGHT] = n;
            return n;
        }

        public RopesNodes split(int size, RopesNodes node) {

            this.nodesInSubtree++;
            RopesNodes n = split(size);
            n.childrenLeftRight[LEFT] = node;
            n.nodesInSubtree++;
            return n;
        }

        int maxOffset() {
            return offset + str.size() - 1;
        }

        int getSize() {
            return str.size();
        }

        public void setLeft(RopesNodes n) {
            this.childrenLeftRight[LEFT] = n;
        }

        public void setRight(RopesNodes n) {
            this.childrenLeftRight[RIGHT] = n;
        }

        public RopesNodes getLeft() {
            return this.childrenLeftRight[LEFT];
        }

        public RopesNodes getRight() {
            return this.childrenLeftRight[RIGHT];
        }

        public void sumDirectChildren() {
            nodesInSubtree = getNodesInSubtree(LEFT) + getNodesInSubtree(RIGHT) + 1;
            sizeNodeAndChildren = getSizeNodeAndChildren(LEFT) + getSizeNodeAndChildren(RIGHT) + str.size();
        }

        public int getNodesInSubtree(int i) {
            RopesNodes s = childrenLeftRight[i];
            return s == null ? 0 : s.nodesInSubtree;
        }

        public int getSizeNodeAndChildren(int i) {
            RopesNodes s = childrenLeftRight[i];
            return s == null ? 0 : s.sizeNodeAndChildren;
        }

        public int getNodesInSubtree() {
            return nodesInSubtree;
        }

        public void setChildrens(int childrens) {
            this.nodesInSubtree = childrens;
        }

        public int getSizeNodeAndChildren() {
            return sizeNodeAndChildren;
        }

        public void replaceChildren(RopesNodes node, RopesNodes by) {
            if (childrenLeftRight[LEFT] == node) {
                childrenLeftRight[LEFT] = by;
            } else if (childrenLeftRight[RIGHT] == node) {
                childrenLeftRight[RIGHT] = by;
            }
        }

        public int balanceScore() {
            return getNodesInSubtree(RIGHT) - getNodesInSubtree(LEFT);
        }

        public void become(RopesNodes node) {
            this.sizeNodeAndChildren = -str.size() + node.str.size();
            this.str = node.str;
            this.offset = node.offset;
            this.block = node.block;
        }

        public boolean isAppendableAfter() {
            return this.block.isMine() && block.id.end == this.maxOffset();
        }

        public boolean isAppendableBefore() {
            return this.block.isMine() && block.id.getBegin() == this.offset;
        }

        @Override
        public String toString() {
            //return str.toString();
            //return "{" + nodesInSubtree + "," + sizeNodeAndChildren + ", " + offset + ", " + str + "," + block + ",[" + this.childrenLeftRight[0] + "," + this.childrenLeftRight[1] + "]}";
            String str2 = "";
            for (Object o : str) {
                str2 += o;
            }
            return new IdentifierInterval(this.block.id.base, this.offset, this.maxOffset()).toString() + "," + str2;
        }

        public String viewRec() {
            String str2 = "";
            if (getLeft() != null || getRight() != null) {
                str2 += "( ";
            }
            if (getLeft() != null) {
                str2 += getLeft().viewRec();
            }
            if (getLeft() != null || getRight() != null) {
                str2 += " , ";
            }
            for (Object o : str) {
                str2 += o;
            }
            if (getLeft() != null || getRight() != null) {
                str2 += " , ";
            }
            if (getRight() != null) {
                str2 += getRight().viewRec();
            }
            if (getLeft() != null || getRight() != null) {
                str2 += " )";
            }
            return str2;
        }

        public IdentifierInterval getIdentifierInterval() {
            return new IdentifierInterval(this.block.id.base, this.offset, this.offset + str.size() - 1);
        }
    }

    public static class ResponseIntNode {

        int i;
        RopesNodes node;
        LinkedList path;

        public ResponseIntNode(int i, RopesNodes node, LinkedList path) {
            this.i = i;
            this.node = node;
            this.path = path;
        }

        public LinkedList getPath() {
            return path;
        }

        public int getI() {
            return i;
        }

        public RopesNodes getNode() {
            return node;
        }
    }
}
