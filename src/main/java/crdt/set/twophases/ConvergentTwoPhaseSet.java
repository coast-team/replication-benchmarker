/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set.twophases;

import crdt.CRDTMessage;

import crdt.PreconditionException;
import crdt.set.CRDTSet;
import crdt.set.ConvergentSet;
import crdt.set.twophases.TwoPhasesMessage.OpType;
import java.util.*;

/**
 *
 * @author score
 */
public class ConvergentTwoPhaseSet<T> extends ConvergentSet<T>  {

    private HashSet setA; //add element
    private HashSet setR; //for removing elements

    public ConvergentTwoPhaseSet() {
        this.setA = new HashSet<T>();
        this.setR = new HashSet<T>();
    }

    @Override
    public Set lookup() {
        Set result = new HashSet<T>();
        T t;
        Iterator iterator = setA.iterator();
        while (iterator.hasNext()) {
            t = (T) iterator.next();
            if (!setR.contains(t)) {
                result.add(t);
            }
        }
        return result;
    }
    
    @Override
    public void applyRemote (CRDTMessage stat) {
        ConvergentTwoPhaseSet tpcs = (ConvergentTwoPhaseSet) stat;
        setA.addAll(tpcs.getSetA());
        setR.addAll(tpcs.getSetR());
    }

    public Set<T> getSetA() {
        return setA;
    }

    public Set<T> getSetR() {
        return setR;
    }
   
    @Override
    public ConvergentTwoPhaseSet<T> innerAdd(T t) throws PreconditionException {
        if (this.getSetR().contains(t)) {
            throw new PreconditionException("This element was deleted");
        }
        setA.add(t);
        return this;
    }

    @Override
    public ConvergentTwoPhaseSet innerRemove(T t) {
        setR.add(t);
        return this;
    }

    @Override
    public boolean contains(T t) {
        return(this.lookup().contains(t));
    }

    @Override
    public CRDTSet<T> create() {
        return new ConvergentTwoPhaseSet();
    }
    
    @Override
    public ConvergentTwoPhaseSet<T> clone() {
        ConvergentTwoPhaseSet<T> clone = new ConvergentTwoPhaseSet<T>();
        clone.setA = (HashSet) this.setA.clone();
        clone.setR = (HashSet) this.setR.clone();
        return clone;
    }
}
