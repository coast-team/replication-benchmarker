/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.edgetree;

/**
 *
 * @author score
 */
public class Edge<T> {

    private T father;
    private T son;
    private boolean visible=true;
    private boolean visibleInCRDT=true;

    public Edge(T f, T s) {
        father = f;
        son = s;
    }

    public T getFather() {
        return father;
    }
    public void setFather(T t){
        this.father=t;
    }

    public T getSon() {
        return son;
    }

    /**
     * @return the visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @param visible the visible to set
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * @return the visibleInCRDT
     */
    public boolean isVisibleInCRDT() {
        return visibleInCRDT;
    }

    /**
     * @param visibleInCRDT the visibleInCRDT to set
     */
    public void setVisibleInCRDT(boolean visibleInCRDT) {
        this.visibleInCRDT = visibleInCRDT;
    }
    @Override
    public Edge<T> clone(){
        Edge<T> ret =new Edge(father,son);
        ret.setVisible(visible);
        ret.setVisibleInCRDT(visibleInCRDT);
        return ret;
    }
}
