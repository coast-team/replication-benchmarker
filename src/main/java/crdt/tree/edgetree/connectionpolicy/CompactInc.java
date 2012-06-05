/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.edgetree.connectionpolicy;

import collect.HashMapSet;
import crdt.set.CRDTSet;
import crdt.set.SetOperation;
import crdt.tree.edgetree.Edge;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Set;

/**
 *
 * @author Stephane Martin
 */
public class CompactInc<T> extends EdgeConnectionPolicy<T> {

    
    HashMapSet<T, Edge<T>> tEdges;
    HashMapSet<T, Edge<T>> fEdges;
    HashMapSet<T, Edge<T>> links; /*
     * T renvois vers le(s) prochain(s) t visible. Comment on le calcul ?
     */
    
    //HashTree tree;
    public CompactInc() {
    }

    @Override
    public EdgeConnectionPolicy<T> create() {
        CompactInc<T> ret = new CompactInc<T>();
        /*
         * ret.orphans = new HashMapSet<T, Node<T>>(); ret.tree = new
         * HashTree();
         */
        ret.tEdges = new HashMapSet<T, Edge<T>>();
        ret.fEdges = new HashMapSet<T, Edge<T>>();
        ret.links = new HashMapSet<T, Edge<T>>();

        return ret;


    }

    @Override
    public void update(Observable o, Object o1) {
        if (o instanceof CRDTSet
                && o1 instanceof SetOperation) {
            SetOperation o2 = (SetOperation) o1;
            Edge<T> edge = ((Edge<T>) o2.getContent()).clone();
            if (o2.getType() == SetOperation.OpType.add) {
                /*
                 * Si c'est un Add
                 */

                if (edge.getFather() != null && !tEdges.containsKey(edge.getFather())) {
                    /*
                     * Si le père n'existe pas.
                     */
                    T father = edge.getFather();
                    /*
                     * Si le père n'existe pas on crée ver les suivant
                     */
                    for (Edge<T> f : links.getAll(father)) {
                        createEdge(new Edge(f.getFather(), edge.getSon()));
                        //links.put(edge.getFather(),nEdge);
                    }
                }else
                    createEdge(edge);
                
//                nextFather.removeAll(edge.getSon());

            } else {
                /*
                 * TODO: Si C'est un Del
                 */
                /*
                 * Supprime de l'ensemble d'edge
                 */
                //edgesSet.remove(edge);

                /* 
                 * On renseingne le nextFather
                 */
                
                /*
                 *  mets le fils orphans
                 */
                
                for (Edge<T> e : fEdges.getAll(edge.getSon())) {
                    e.setFather(edge.getFather());
                    emp.moved(edge.getSon(), e, this);
                    links.put(e.getSon(), e);
                }

                fEdges.remove(edge.getFather(), edge);
                tEdges.remove(edge.getSon(), edge);
                //orphans.remove(edge.getFather(), edge);
                emp.del(edge, this);
            }
        }
    }

    private void createEdge(Edge<T> e) {
        emp.add(e, this);
        tEdges.put(e.getSon(), e);
        fEdges.put(e.getFather(), e);
        LinkedList<Edge<T>> toRemove=new LinkedList();
        
        for(Edge<T> t: links.getAll(e.getSon())){
            if (t.getFather()==e.getFather()){
                toRemove.add(t);
                T oldFather=t.getFather();
                t.setFather(e.getSon());
                emp.moved(oldFather, t, this);
                
            }
        }
        for (Edge<T> t:toRemove){
            links.remove(e.getSon(), t);
        }
    }

    @Override
    public Set<Edge<T>> getEdges() {
        return (Set<Edge<T>>) tEdges.values();
    }
}
