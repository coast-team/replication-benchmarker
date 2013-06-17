/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.woot.wooth.undo;

import jbenchmarker.core.Document;
import crdt.Operation;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.woot.WootIdentifier;
import jbenchmarker.woot.WootOperation;
import jbenchmarker.woot.wooth.LinkedNode;
import jbenchmarker.woot.wooth.WootHashDocument;

/**
 * A woot document that reuse objects that reappear. If the user inserts the
 * same element, the document generate an undo.
 *
 * @author urso
 */
public class WootHashReuse<T> extends WootHashDocument<T> {
    
    public WootHashReuse(WootUndoNode<T> first, WootUndoNode<T> end) {
        super(first, end);
    }

    @Override
    protected WootUndoNode get(WootIdentifier id) {
        return (WootUndoNode) super.get(id);
    }

    @Override
    public WootOperation delete(SequenceOperation o, WootIdentifier id) { // diseappar
        return new WootUndo(id, -get(id).getVisibility());
    }

    @Override
    public WootOperation insert(SequenceOperation o, WootIdentifier ip, WootIdentifier in, T content) {
        LinkedNode<T> p = get(ip).getNext();
        while (p.getId() != in) {
            if (p.getContent().equals(content)) { // reappear
                return new WootUndo(p.getId(), -((WootUndoNode) p).getVisibility() + 1);
            }
            p = p.getNext();
        }
        return super.insert(o, ip, in, content);
    }

    @Override
    public void apply(Operation op) {
        if (op instanceof WootUndo) {
            WootUndo wop = (WootUndo) op;
            undo(wop.getId(), wop.getVisibility());
        } else { // add
            super.apply(op);
        }
    }

    @Override
    protected void add(WootIdentifier id, T content, WootIdentifier ip, WootIdentifier in) {
        super.add(id, content, ip, in);
    }

    @Override
    protected void del(WootIdentifier id) {
        throw new UnsupportedOperationException("Not to be used.");
    }

    protected void undo(WootIdentifier id, int visibility) {
        WootUndoNode<T> n = get(id);
        int v = n.getVisibility();
        n.changeVisibility(visibility);
        if (v <= 0 && n.getVisibility() > 0) {
            ++size;
        } else if (v > 0 && n.getVisibility() <= 0) {
            --size;
        }
    }

    @Override
    protected WootUndoNode<T> newNode(WootIdentifier IE, T content, boolean visible, LinkedNode<T> next, int degree) {
        return new WootUndoNode<T>(IE, content, next, degree, visible ? 1 : 0);
    }
    
    @Override
    public Document create() {
        return newDocument();
    }
    
    // Helper
    public static <T> WootHashReuse<T> newDocument() {
        WootUndoNode<T> e = new WootUndoNode<T> (WootIdentifier.IE, null, null, 0, 0),
                f = new WootUndoNode<T> (WootIdentifier.IB, null, e, 0, 0);
        return new WootHashReuse<T>(f, e);
    }
}
