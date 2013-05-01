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
package crdt.set.twophases;

import crdt.CRDTMessage;
import crdt.OperationBasedOneMessage;
import crdt.PreconditionException;
import crdt.set.CRDTSet;
import crdt.set.CommutativeSetMessage.OpType;
import java.util.HashSet;
import java.util.Set;
import jbenchmarker.core.LocalOperation;

/**
 *
 * @author score
 */
public class CommutativeTwoPhaseSet<T> extends CRDTSet<T>{

    final private Set setA; //add element
    final private Set setR; //for removing elements

    public CommutativeTwoPhaseSet() {
        this.setA = new HashSet();
        this.setR = new HashSet();
    }

    @Override
    public Set lookup() {
        return setA;
    }

    @Override
    public void applyOneRemote(CRDTMessage Setop) {
        
        TwoPhasesMessage op = (TwoPhasesMessage)((OperationBasedOneMessage) Setop).getOperation();
        if (op.getType() == TwoPhasesMessage.OpType.add) {
            //not innerAdd after innerRemove
            if (!setR.contains(op.getContent())) {
                setA.add(op.getContent());
            }
        } else {
            setA.remove(op.getContent());
            setR.add(op.getContent());
        }
    }

    public Set getSetA() {
        return setA;
    }

    public Set getSetR() {
        return setR;
    }

    @Override
    public CRDTMessage innerAdd(T t) throws PreconditionException {
        if (this.getSetR().contains(t)) {
            throw new PreconditionException("This element was deleted");
        }

        setA.add(t);
        return new  OperationBasedOneMessage(new TwoPhasesMessage(OpType.add, t));
    }

    @Override
    public CRDTMessage innerRemove(T t) {
        setA.remove(t);
        setR.add(t);
        return  new  OperationBasedOneMessage(new TwoPhasesMessage(OpType.del, t));
    }

     @Override
    public boolean contains(T t) {
        return(this.lookup().contains(t));
    }

    @Override
    public CRDTSet<T> create() {
        return new CommutativeTwoPhaseSet<T>();
    }

     
}
