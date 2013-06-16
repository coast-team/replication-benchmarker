/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.treedocReplace;

import crdt.CRDT;
import crdt.simulator.IncorrectTraceException;
import java.util.LinkedList;
import java.util.List;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.SequenceMessage;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;
import jbenchmarker.treedoc.TreedocIdentifier;
import jbenchmarker.treedoc.TreedocOperation;
import jbenchmarker.treedoc.UniqueTag;

/**
 *
 * @author score
 */
public class TreedocRepMerge extends MergeAlgorithm{
    
    public TreedocRepMerge(int r) {
		super(new TreedocReplaceDocument(UniqueTag.createGenerator()), r);
	}
    
    
    @Override
    protected void integrateRemote(crdt.Operation message) throws IncorrectTraceException {
        getDoc().apply(message);
    }

    @Override
    protected List<SequenceMessage> localInsert(SequenceOperation opt)
            throws IncorrectTraceException {
        final TreedocReplaceDocument doc = ((TreedocReplaceDocument) getDoc());
        final List<SequenceMessage> ops = new LinkedList<SequenceMessage>();
         final TreedocIdentifier id;
        int pos = opt.getPosition();
        
        if (opt.getType().equals(OpType.replace) && pos != doc.viewLength()) {
            id = doc.insertAt(restrictedIndex(pos + 1, true), opt.getContent(), getReplicaNumber(), true);
        } else {
            id = doc.insertAt(restrictedIndex(pos, true), opt.getContent(), getReplicaNumber(), false);
        }
        
        ops.add(new TreedocOperation(opt, id, opt.getContent()));
System.out.println("--- localInsert ---"+id);

        return ops;
    }

    @Override
    protected List<SequenceMessage> localDelete(SequenceOperation opt)
            throws IncorrectTraceException {
        final TreedocReplaceDocument doc = ((TreedocReplaceDocument) getDoc());
        final List<SequenceMessage> ops = new LinkedList<SequenceMessage>();


        // TODO: implement batch delete more efficiently?
        for (int i = opt.getPosition(); i < opt.getPosition()
                + opt.getLenghOfADel(); i++) {
            final TreedocIdentifier deletedId = doc
                    .deleteAt(restrictedIndex(opt.getPosition(), false));
            ops.add(new TreedocOperation(opt, deletedId));
System.out.println("---- localDelete --- "+deletedId);
        }

        return ops;
    }
    

    protected int restrictedIndex(final int index, final boolean insert) {
        // FIXME: Hack with restricting index within the range!
        // It seems to be caused by Simulator replaying delete blindly without
        // verifying replica document size first. Not 100% sure though.
        return Math.min(index, ((TreedocReplaceDocument) getDoc()).getContentSize()
                - (insert ? 0 : 1));
    }

    @Override
    public CRDT<String> create() {
        return new TreedocRepMerge(0);
        // FIXME: what is the semantics: what replica number should we use!?
    }

    @Override
    protected List<SequenceMessage> localReplace(SequenceOperation opt) throws IncorrectTraceException {
        System.out.println("---Replace--");
        List<SequenceMessage> lop = localInsert(opt);
        int newPos = opt.getPosition()+opt.getContent().size();
        opt.setPosition(newPos);
        lop.addAll(localDelete(opt));
        return lop;
    }
}
