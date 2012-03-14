/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.graphtree;

import collect.HashMapSet;
import crdt.set.CRDTSet;
import crdt.set.SetOperation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Set;

/**
 *
 * @author score
 */
public abstract class GraphConnectionPolicyNoInc<T> extends GraphConnectionPolicy<T> {

    protected boolean fresh;
    protected Set<T> node;
    protected Set<Edge<T>> edge;
    protected Set<Edge<T>> tombstone;
    protected HashMapSet<T, Edge<T>> nodeToEdge;
    protected HashMapSet<T, Edge<T>> SetTree;
    protected HashMapSet<T, Edge<T>> SetTreeOut;

    public GraphConnectionPolicyNoInc() {
        this.fresh = false;
        this.node = new HashSet<T>();
        this.edge = new HashSet<Edge<T>>();
        this.tombstone = new HashSet<Edge<T>>();
        this.nodeToEdge = new HashMapSet();
        this.SetTree = new HashMapSet<T, Edge<T>>();
        this.SetTreeOut = new HashMapSet<T, Edge<T>>();
    }

    abstract protected void connect();

    @Override
    public void update(Observable o, Object op) {
        fresh = false;
        updateNoInc(o, op);
        if (gmp != null) {
            gmp.update(o, op);
        }
    }

    public abstract void updateNoInc(Observable o, Object op);

    @Override
    public HashMapSet<T, Edge<T>> lookup() {
        if (!fresh) {
            connect();
        }
        return SetTreeOut;
    }

    abstract protected void getRooted(Edge<T> orphanEdge);

    public boolean getStat() {
        return fresh;
    }

    public Set<T> getNode() {
        return node;
    }

    public Set<Edge<T>> getEdge() {
        return edge;
    }

    public Set<Edge<T>> getTombs() {
        return tombstone;
    }

    public HashMapSet<T, Edge<T>> SetTree() {
        return SetTree;
    }

    public HashMapSet<T, Edge<T>> getNodeToEdge() {
        return nodeToEdge;
    }
}
