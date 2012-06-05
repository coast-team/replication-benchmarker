/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2011 INRIA / LORIA / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jbenchmarker.logoot;

import collect.Node;
import collect.OrderedNode;
import crdt.CRDT;
import crdt.tree.orderedtree.PositionIdentificator;
import crdt.tree.orderedtree.PositionIdentifier;
import java.math.BigInteger;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.SequenceMessage;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.Document;

import java.util.*;

/**
 *
 * @author mehdi urso
 */
public class LogootMerge<T> extends MergeAlgorithm implements PositionIdentificator {

    final private int nbBit;
    final private long max; // MAX = 2^nbBit - 1
    private int myClock;
    private final LogootStrategy strategy;
    private final BigInteger base;

    // nbBit <= 64
    public LogootMerge(Document doc, int r, int nbBit, LogootStrategy strategy) {
        super(doc, r);
        myClock = 0;
        this.nbBit = nbBit;
        this.strategy = strategy;
        if (nbBit == 64) {
            max = Long.MAX_VALUE;
        } else {
            this.max = (long) Math.pow(2, nbBit) - 1;
        }
        base = BigInteger.valueOf(2).pow(nbBit);
    }

    public long getNbBit() {
        return nbBit;
    }

    @Override
    protected void integrateLocal(SequenceMessage op) {
        getDoc().apply(op);
    }

    @Override
    protected List<SequenceMessage> generateLocal(SequenceOperation opt) {
        List<SequenceMessage> lop = new ArrayList<SequenceMessage>();
        LogootDocument<T> lg = (LogootDocument<T>) (this.getDoc());
        int N = 0, offset = 0;
        int position = opt.getPosition();

        if (opt.getType() == SequenceOperation.OpType.ins) {
            N = opt.getContent().size();
            List<T> content = opt.getContent();
            ArrayList<LogootIdentifier> patch = strategy.generateLineIdentifiers(this, lg.getId(position),
                    lg.getId(position + 1), N);

            ArrayList<T> lc = new ArrayList<T>(patch.size());
            for (int cmpt = 0; cmpt < patch.size(); cmpt++) {
                T c = content.get(cmpt);
                LogootOperation<T> log = LogootOperation.insert(opt, patch.get(cmpt), c);
                lop.add(log);
                lc.add(c);
            }
            lg.insert(position, patch, lc);

        } else {
            offset = opt.getOffset();
            for (int k = 1; k <= offset; k++) {
                LogootOperation<T> wop = LogootOperation.Delete(opt, lg.getId(position + k));
                lop.add(wop);
            }
            lg.remove(position, offset);
        }      
        return lop;
    }

    void incClock() {
        this.myClock++;
    }

    int getClock() {
        return this.myClock;
    }

    void setClock(int c) {
        this.myClock = c;
    }

    long getMax() {
        return this.max;
    }

    BigInteger getBase() {
        return base;
    }

    @Override
    public CRDT<String> create() {
        return new LogootMerge(new LogootDocument(Long.MAX_VALUE), 0, 64, new BoundaryStrategy(1000000000));
    }

    @Override
    public PositionIdentifier generate(OrderedNode father, PositionIdentifier p, PositionIdentifier n) {
        if (p == null) {
            p = ((LogootDocument) getDoc()).getId(0);
        } 
        if (n == null) {
            n = ((LogootDocument) getDoc()).getId(getDoc().viewLength()+1);
        }
        return strategy.generateLineIdentifiers(this, (LogootIdentifier) p, (LogootIdentifier) n, 1).get(0);
    }

    @Override
    public int getInteger(final OrderedNode father, PositionIdentifier pi) {
        List<LogootIdentifier> l = new AbstractList<LogootIdentifier>() {
            
            @Override
            public int size() {
                return father.getChildrenNumber()+2;
            }

            @Override
            public LogootIdentifier get(int i) {
                if (i == father.getChildrenNumber()+1) {
                    return ((LogootDocument) getDoc()).getId(getDoc().viewLength()+1);
                }
                return (LogootIdentifier) father.getChild(i-1).getPosition();
            }
        };
        return  ((LogootDocument) getDoc()).dicho(l, (LogootIdentifier) pi)-1;
    }
}
