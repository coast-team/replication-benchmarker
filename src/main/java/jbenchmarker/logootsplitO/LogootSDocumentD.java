/*
 *  Replication Benchmarker
 *  https://github.com/score-team/replication-benchmarker/
 *  Copyright (C) 2013 LORIA / Inria / SCORE Team
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import jbenchmarker.core.Operation;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class LogootSDocumentD implements LogootSDoc {

    private int clock = 0;
    private HashMap<List<Integer>, LogootSBlock> mapBaseToBlock = new HashMap<List<Integer>, LogootSBlock>(); //for test
    private ArrayList<LinkBlock> list = new ArrayList<LinkBlock>();//dichotomic ready
    private StringBuilder view = new StringBuilder();
    private int replicaNumber = 0;

    @Override
    public void setReplicaNumber(int i) {
        this.replicaNumber = i;
    }

    public static class LinkBlock implements Comparable {

        LogootSBlock block;
        int offset;

        public LinkBlock(LogootSBlock block, int offset) {
            this.block = block;
            this.offset = offset;
        }

        @Override
        public int compareTo(Object t) {
            if (t instanceof LinkBlock) {
                this.getID().compareTo(((LinkBlock)t).getID());
            }
            throw new UnsupportedOperationException("Bad comparaison"); //To change body of generated methods, choose Tools | Templates.
        }

        public LogootSBlock getBlock() {
            return block;
        }

        public Identifier getID() {
            return new Identifier(this.block.id.getBase(), offset);
        }

        @Override
        public String toString() {
            return "L{" + block.id.base + "," + offset + '}';
        }
    }

    @Override
    public String view() {
        return view.toString();
    }

    @Override
    public int viewLength() {
        return view.length();
    }

    /**
     * search a position
     */
    int dicSearch(Identifier id, int min) {
        //int min = 0;
        int max = list.size() - 1;
        while (min <= max) {
            int i = (int) (min + max) / 2;

            int p = list.get(i).getID().compareTo(id);
            //int p = list.get(i).getID().compareTo(block.id.
            if (p < 0) {
                min = i + 1;
            } else if (p > 0) {
                max = i - 1;
            } else {
                min = i;
                break;
            }

        }
        return min;
        //min=Math.min(min, max);
        /*System.out.println("dic : "+min);
        if (min > 0 && min < list.size() && list.size() > 0) {
            if (list.get(min - 1).getID().compareTo(id) > 0) {
                System.out.println("merde");
            }
            assert (list.get(min - 1).getID().compareTo(id) <= 0);
        }
        if (min < list.size() && list.size() > 0) {
            if (list.get(min).getID().compareTo(id) < 0) {
                System.out.println("merde");
            }
            assert (list.get(min).getID().compareTo(id) >= 0);
        }*/
        
    }

    /**
     * Count diff with offset 0
     *
     * @param l
     * @param l2
     * @return
     */
    static int maxOffsetBeforeNex(Identifier bI, Identifier nex, int max) {
        Iterator<Integer> i = bI.base.iterator();
        Iterator<Integer> i2 = nex.iterator();
        while (i.hasNext() && i2.hasNext()) {
            if (!i.next().equals(i2.next())) {
                return max;
            }
        }

        if (i2.hasNext()) {
            return Math.min(i2.next(), max);
        } else {
            return max;
        }

    }

    /**
     *
     * @param pos
     * @param block
     * @return inserted block
     */
    public void addBlock(LogootSBlock block, int begin, List elem) {
        
        int offset = begin;
        int pos = 0;
        int end = begin + elem.size() - 1;
        Iterator it = elem.iterator();
        while (offset <= end) {
            //search the first position
            pos = dicSearch(block.getId().getBaseId(offset), pos);
            //computation of offset Max 
            int offsetMax;
            if (pos < list.size()) {
                Identifier beginId = new Identifier(block.id.base, begin);
                offsetMax = maxOffsetBeforeNex(beginId, list.get(pos).getBlock().getId().getBeginId(), end);
               /* System.out.println("max:"+offsetMax + "   "+beginId+ list.get(pos ).getBlock().getId().getBeginId());
                assert (offsetMax <= end);*/
            } else {
                offsetMax = end;
            }


            for (; offset <= offsetMax; offset++) {
                add(pos, block, offset, it.next());
                pos++;
            }
        }
        //assert (list.size() == view.length());
    }

    private void add(int pos, LogootSBlock block, int offset, Object o) {
        
        list.add(pos, new LinkBlock(block, offset));
        view.insert(pos, o);
        
        /*if(pos<list.size()-1 && list.size()>1){
            if(list.get(pos).getID().compareTo(list.get(pos+1).getID())>=0){
                System.out.println("merde");
            }
        }
         if(pos>0 && list.size()>0){
            if(list.get(pos-1).getID().compareTo(list.get(pos).getID())>=0){
                System.out.println("merde");
            }
        }*/
    }

    @Override
    public void addBlock(Identifier id, List l) {
        LogootSBlock block = mapBaseToBlock.get(id.base);
        IdentifierInterval idi = new IdentifierInterval(id.base, id.last, id.last + l.size() - 1);
        if (block == null) {
            block = new LogootSBlockLight(idi, l.size());//TODO build factory
            mapBaseToBlock.put(id.base, block);
        } else {
            block.addBlock(id.last, l);
        }
        addBlock(block, id.last, l);
    }

    public void delBlock(LogootSBlock block, int begin, int end) {
        
        int offset = begin;
        int pos = 0;
        LinkBlock lb;
        int nbElement = 0;
        while (offset <= end) {
            //search the first position.
            pos = dicSearch(block.getId().getBaseId(offset), pos);
            if (pos >= list.size()) {
                break;
            }
            lb = list.get(pos);
            //while we are in block
            if (lb.getBlock() != block) {//element does not existing
                pos++;
                offset++;
            } else {
                do {
                    if (lb.offset != offset) {
                        offset = lb.offset;
                    } else {
                        list.remove(pos);
                        view.deleteCharAt(pos);
                        lb = pos < list.size() ? list.get(pos) : null;
                        offset++;
                        nbElement++;
                    }
                } while (lb != null && lb.getBlock() == block && offset <= end);
            }
        }
        block.delBlock(begin, end, nbElement);
        if (block.numberOfElements() == 0) {// little garbage collection
            this.mapBaseToBlock.remove(block.getId().getBase());
        }
       
    }

    @Override
    public void delBlock(IdentifierInterval id) {
        LogootSBlock block = mapBaseToBlock.get(id.base);
        if (block != null) {
            delBlock(block, id.begin, id.end);
        }
    }

    @Override
    public void apply(Operation op) {
    }

    @Override
    public LogootSOpAdd insertLocal(int pos, List l) {
        assert(list.size()==view.length());
        LinkBlock after = pos < list.size() ? list.get(pos) : null;
        LinkBlock before = pos > 0 ? list.get(pos - 1) : null;
        int offset;
        LogootSBlock block;
        if (after != null && before == null && after.block.mine && after.block.getId().begin - l.size() > Integer.MIN_VALUE) {// Block in position is mine
            //add before block
            block = after.block;
            offset = after.offset;
            block.addBlock(offset, l);
        } else if (before != null && after == null && before.block.mine && before.block.getId().end + l.size() < Integer.MAX_VALUE) {
            //add after block
            block = before.block;
            offset = block.id.begin - l.size();
            block.addBlock(offset, l);

        } else {
            // create new block

            List<Integer> base = IDFactory.createBetweenPosition(before == null ? null : before.getID(),
                    after == null ? null : after.getID(), replicaNumber, clock++);
            /*if (before != null && after != null) {
                Identifier idf = new Identifier(base);
                if (before.getID().compareTo(idf) != -1){
                    assert(false);
                }
                if(after.getID().compareTo(idf) != 1){
                    assert(false);
                }
            
            }*/
            IdentifierInterval id = new IdentifierInterval(base, 0, l.size() - 1);
            block = new LogootSBlockLight(id);//TODO build factory
            offset = 0;
            block.addBlock(offset, l);
            mapBaseToBlock.put(block.getId().getBase(), block);
        }
        Identifier idi = new Identifier(block.getId().base, offset);
        int i = pos;
        for (Object o : l) {
            add(i,block,offset++,o);
            i++;
        }
        assert (list.size() == view.length());
        return new LogootSOpAdd(idi, l);
    }

    /**
     * Delete local begin inclusive to end inclusive
     *
     * @param begin
     * @param end
     * @return operation to make to other
     */
    @Override
    public LogootSOpDel delLocal(int begin, int end) {
        System.out.println(""+list.size()+"+"+view.length());
       assert(list.size()==view.length());
        List<IdentifierInterval> li = new LinkedList<IdentifierInterval>();
        LinkBlock lb = list.get(begin);
        LogootSBlock block = lb.getBlock();
        int b = lb.offset;
        int e = b;
        int nbElement = 0;
        int i = begin;
        do {
            lb = list.get(begin);//Todo try to put at end of loop
            if (lb.block != block) {
                addDelIdf(block, b, e, li, nbElement);
                block = lb.block;
                b = lb.offset;
                nbElement = 0;
            }
            e = lb.offset;

            list.remove(begin);
            i++;
            nbElement++;
        } while (i <= end);
        addDelIdf(block, b, e, li, nbElement);
        view.delete(begin, end + 1);
       
        return new LogootSOpDel(li);
    }

    private void addDelIdf(LogootSBlock block, int begin, int end, List<IdentifierInterval> li, int nbElement) {
        li.add(new IdentifierInterval(block.id.base, begin, end));
        block.delBlock(begin, end, nbElement);
        if (block.numberOfElements() == 0) {
            mapBaseToBlock.remove(block.getId().getBase());
        }
    }

    @Override
    public LogootSDoc create() {
        return new LogootSDocumentD();
    }

    /**
     * For test
     */
    public ArrayList<LinkBlock> getList() {
        return list;
    }

    public StringBuilder getView() {
        return view;
    }

    public HashMap<List<Integer>, LogootSBlock> getMapBaseToBlock() {
        return mapBaseToBlock;
    }
}
