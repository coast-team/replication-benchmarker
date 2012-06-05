/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.edgetree.mappingpolicy;

import collect.HashMapSet;
import collect.HashTree;
import collect.Node;
import collect.Tree;
import crdt.tree.edgetree.Edge;
import crdt.tree.edgetree.connectionpolicy.EdgeConnectionPolicy;
import java.util.Set;

/**
 *
 * @author Stephane Martin
 */
public class SeveralInc<T> extends EdgeMappPolicy<T> {
    HashTree tree;
    HashMapSet<T,Node<T>> nodes; /*t est fils*/
    HashMapSet<T,Node<T>> fathers; /*t est le père*/
    //HashMap <Edge<T>,Node<T>> edges;
    
    @Override
    public Tree<T> getTree() {
        return tree;
    }

    @Override
    public EdgeMappPolicy<T> create() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
    @Override
    public void add(Edge<T> e, EdgeConnectionPolicy<T> ecp) {
        Set<Node<T>> tfathers=nodes.getAll(e.getFather());
        Set<Node<T>> sons=nodes.getAll(e.getSon());
        
        for(Node <T> f:tfathers){
            Node<T> n = tree.add(f, e.getSon());
            nodes.put(e.getSon(),n);
            
        }
        //n, n)
    }

    @Override
    public void del(Edge<T> e, EdgeConnectionPolicy<T> ecp) {
        for(Node<T> n : nodes.getAll(e.getSon())){
            if (n.getFather()!=null && n.getFather().getValue()==e.getFather()){
                tree.remove(n);
            }
        }
    }


    @Override
    public void moved(T OdFather, Edge<T> e, EdgeConnectionPolicy<T> ecp) {
       /* boolean first=true;      
        Node<T> nodeForMove=null;
        for(Node<T> n : nodes.getAll(e.getSon())){
            if (nodeForMove==null){
               nodeForMove=n;
            }else{
                tree.remove(n);
            }
            
        }
        first=true;
        for(Node<T> n : nodes.getAll(e.getFather())){
            if (n.getFather()!=null && n.getFather().getValue()==OdFather){
                if (first)/*TODO : finish *
                    //tree.move(n,);
             ;               
            }
        }*/
    }

  

 
    
}
