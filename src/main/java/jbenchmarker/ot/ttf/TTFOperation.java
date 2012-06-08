/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2011 INRIA / LORIA / SCORE Team
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
package jbenchmarker.ot.ttf;

import jbenchmarker.core.Operation;
import jbenchmarker.core.SequenceOperation.OpType;

/**
 *
 * @author oster
 * 
 * TTFOperation Operation Add,Del,up, of TTFSet
 */
public class TTFOperation<T> implements Operation{

    private int pos;
    private T content;
    //private VectorClock clock;
   // private final int siteId;
    private OpType type;
    private int siteId;

    public int getSiteId() {
        return siteId;
    }

    public TTFOperation( OpType type,int pos,T content, int siteId) {
        this.pos = pos;
        this.type = type;
        this.content = content;
        this.siteId=siteId;
    }
    

     public TTFOperation( OpType type,int pos, int siteId) {
        this.pos = pos;
        this.type = type;
        this.siteId=siteId;
    }
    
    public TTFOperation(OpType t) {
        //super(o);
      //  this.siteId = this.getOriginalOp().getReplica();
        this.type = t;
    }

    // FIXME: should be moved to SequenceOperation class?
   public OpType getType() {
        return this.type;
    }

    public int getPosition() {
        return this.pos;
    }

    public void setPosition(int pos) {
        this.pos = pos;
    }

    public T getChar() {
        return this.content;
    }

   /* public int getSiteId() {
        return this.siteId;
    }*/

  /*  public VectorClock getClock() {
        return this.clock;
    }*/

    @Override
    public TTFOperation<T>  clone() {
        return new TTFOperation(this.getType(),this.getPosition(),this.content,siteId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getType());
        sb.append('(');
        sb.append(this.pos);
        if (OpType.ins == this.getType()) {
            sb.append(',');
            sb.append(this.content);
        } 
        sb.append(')');
        return sb.toString();
    }

   /* public static TTFOperation delete(SequenceOperation o, int pos/*, VectorClock vc*) {
        TTFOperation op = new TTFOperation(OpType.del,pos);
        //op.pos = pos;
        //op.clock = vc;
        return op;
    }

    public static <T> TTFOperation insert(SequenceOperation o, int pos, T content/*, VectorClock vc*) {
        TTFOperation op = new TTFOperation(OpType.ins,pos,);
        op.pos = pos;
        op.content = content;
        //op.clock = vc;
        return op;
    }

    public static TTFOperation from(SequenceOperation opt) {
        TTFOperation op = new TTFOperation(opt,opt.getType());
        //op.clock = opt.getVectorClock();
        op.pos = opt.getPosition();
        if (opt.getType() == OpType.ins) {
            op.content = opt.getContent().get(0);
        }
        return op;
    }*/

  /*  @Override
    public SequenceMessage clone() {
        TTFOperation op = new TTFOperation(getOriginalOp());
        op.pos = this.pos;
        op.content = this.content;
//        op.clock = new VectorClock(this.clock);

        return op;
    }*/
}