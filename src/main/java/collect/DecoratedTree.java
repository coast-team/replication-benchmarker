/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package collect;

/**
 *
 * @author urso
 */
public class DecoratedTree<T,V> extends HashTree<T> {

    @Override
    protected DecoratedNode<T,V> createNode(Node<T> father, T t) {
        return new DecoratedNode<T,V>((DecoratedNode<T,V>)father, t);
    }

    @Override
    protected DecoratedNode<T,V> createRoot() {
        return new DecoratedNode<T,V>();
    }
    
    public V getAttached(Node<T> n) {
        return ((DecoratedNode<T,V>) n).getAttached();
    }

    public void setAttached(Node<T> n, V value) {
        ((DecoratedNode<T,V>) n).setAttached(value);
    }
}
