/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.graphtree;
/**
 *
 * @author score
 */
public class Edge<T> {
    
    private T father;
    private T son;
    
    public Edge(T f, T s)
    {
        father = f;
        son = s;
    }
    
    public T getFather()
    {
        return father;
    }
    
    public T getSon()
    {
        return son;
    }

    /**
     * @param father the father to set
     */
    /*public void setFather(T father) {
        this.father = father;
    }*/
    
   
}
