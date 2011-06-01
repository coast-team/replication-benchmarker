package jbenchmarker.woot.wooth;

import jbenchmarker.woot.WootIdentifier;
import jbenchmarker.woot.WootNode;

/**
 * A linked list of WootNodes.
 * @author urso
 */
public class WootHashNode extends WootNode {
    private WootHashNode next;
    final private int degree;

    public WootHashNode(WootIdentifier id, char content, boolean visible, WootHashNode next, int degree) {
        super(id, content, visible);
        this.next = next;
        this.degree = degree;
    }

    public int getDegree() {
        return degree;
    }

    WootHashNode getNext() {
        return next;
    }

    void setNext(WootHashNode next) {
        this.next = next;
    }
}
