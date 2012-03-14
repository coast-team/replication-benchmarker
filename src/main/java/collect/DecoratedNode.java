/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package collect;

import collect.NodeImpl;

/**
 *
 * @author urso
 */
public class DecoratedNode<T, V> extends NodeImpl<T> {
    private V attached;
    
    DecoratedNode(DecoratedNode<T,V> f, T t) {
        super(f, t);
    }

    DecoratedNode() {
        super();
    }

    public V getAttached() {
        return attached;
    }

    void setAttached(V attached) {
        this.attached = attached;
    }
}
