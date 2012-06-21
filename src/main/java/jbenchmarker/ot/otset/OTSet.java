/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.otset;

import crdt.*;
import crdt.set.CRDTSet;
import java.util.HashSet;
import java.util.Set;
import jbenchmarker.ot.soct2.OTAlgorithm;
import jbenchmarker.ot.soct2.OTMessage;

/**
 *
 * @param <T> Type of elements in set.
 * @author stephane martin OT set
 */
public class OTSet<T> extends CRDTSet<T>  {

    final Set set = new HashSet();
    final OTAlgorithm<OTSetOperations<T>> otAlgo;
    
    /*
     * -- Factory --
     */
    static int created = 0;

    public OTSet(Factory<OTAlgorithm<OTSetOperations<T>>> otalgo) {
        super(++created); 
        this.otAlgo = otalgo.create();
        this.otAlgo.setReplicaNumber(created);
    }

    /**
     *
     * @param ot OT policy for this set AddWin or DelWin
     * @param replicaNumber Site number of replicat number
     */
    public OTSet(Factory<OTAlgorithm<OTSetOperations<T>>> otalgo, int siteId) {
        super(siteId);
        this.otAlgo = otalgo.create();
        this.otAlgo.setReplicaNumber(siteId);
    }

    @Override
    public void setReplicaNumber(int replicaNumber) {
        super.setReplicaNumber(replicaNumber);
        otAlgo.setReplicaNumber(replicaNumber);
    }
    
    /**
     * return new Otset
     *
     * @return
     */
    @Override
    public CRDTSet create() {
        return new OTSet(otAlgo);
    }

    /**
     * Local add is perfomed
     *
     * @param t new element t
     * @return Message sent to another replicas.
     * @throws PreconditionException if the element is already present.
     */
    @Override
    protected CRDTMessage innerAdd(T t) throws PreconditionException {
        if (set.contains(t)) {
            throw new PreconditionException("add : set already contains element " + t);
        } else {
            OTSetOperations<T> op = new OTSetOperations(OTSetOperations.OpType.Add, t, getReplicaNumber());
            set.add(t);
            return new OperationBasedOneMessage(otAlgo.estampileMessage(op));
        }
    }

    /**
     * remove element t from set and return the message to inform another
     * replicas
     *
     * @param t element
     * @return CRDTmessage sent to another
     * @throws PreconditionException if t is not present in set.
     */
    @Override
    protected CRDTMessage innerRemove(T t) throws PreconditionException {
        if (set.contains(t)) {
            OTSetOperations<T> op = new OTSetOperations(OTSetOperations.OpType.Del, t, getReplicaNumber());
            set.remove(t);
            return new OperationBasedOneMessage(otAlgo.estampileMessage(op));
        } else {
            throw new PreconditionException("del : element " + t + " is not present in set");
        }
    }

    /**
     * check if set containes t
     *
     * @param t
     * @return true if it contains
     */
    @Override
    public boolean contains(T t) {
        return set.contains(t);
    }

    /**
     * is called when a remote message is recieved.
     *
     * @param msg OTSetOperation message from another replicas.
     */
   /* @Override
    public void applyRemote(CRDTMessage msg) {
            
        applyOneRemote(msg);
        for (Object mess : ((CommutativeMessage) msg).getMsgs()) {
            applyOneRemote((CRDTMessage) mess);
        }


    }*/

    @Override
    public void applyOneRemote(CRDTMessage msg) {
        OTSetOperations<T> op = (OTSetOperations<T>) otAlgo.integrateRemote((OTMessage) ((OperationBasedOneMessage) msg).getOperation());
        switch (op.getType()) {
            case Add:
                if (!set.contains(op.getElement())) {
                    set.add(op.getElement());
                    notifyAdd(op.getElement());
                }
                break;
            case Del:
                if (set.contains(op.getElement())) {
                    set.remove(op.getElement());
                    notifyDel(op.getElement());
                }
                break;
            case Nop:
        }

    }

    /**
     *
     * @return set
     */
    @Override
    public Set<T> lookup() {

        return set;
    }

    @Override
    public String toString() {
        return "OTSet{" + "set=" + set + ", soct2=" + otAlgo + ", replicaNumber=" + getReplicaNumber() + '}';
    }

    public OTAlgorithm<OTSetOperations<T>> getOtAlgo() {
        return otAlgo;
    }

    
    
}
