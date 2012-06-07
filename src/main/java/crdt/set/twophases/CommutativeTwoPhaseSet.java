/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set.twophases;

import crdt.CRDTMessage;
import crdt.PreconditionException;
import crdt.set.CRDTSet;
import crdt.set.CommutativeSetMessage.OpType;
import java.util.HashSet;
import java.util.Set;

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
    public void applyRemote(CRDTMessage Setop) {
        
        TwoPhasesMessage op = (TwoPhasesMessage) Setop;
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

        return new TwoPhasesMessage(OpType.add, t);
    }

    @Override
    public CRDTMessage innerRemove(T t) {
        setA.remove(t);
        setR.add(t);
        return  new TwoPhasesMessage(OpType.del, t);
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
