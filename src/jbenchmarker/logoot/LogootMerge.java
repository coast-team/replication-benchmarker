/**
 *   This file is part of ReplicationBenchmark.
 *
 *   ReplicationBenchmark is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   ReplicationBenchmark is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with ReplicationBenchmark.  If not, see <http://www.gnu.org/licenses/>.
 *
 **/
package jbenchmarker.logoot;

import java.math.BigInteger;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.Operation;
import jbenchmarker.trace.TraceOperation;
import jbenchmarker.core.Document;

import java.util.*;

/**
 *
 * @author mehdi urso
 */
public class LogootMerge extends MergeAlgorithm {

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
    protected void integrateLocal(Operation op) {
        getDoc().apply(op);
    }

    @Override
    protected List<Operation> generateLocal(TraceOperation opt) {
        List<Operation> lop = new ArrayList<Operation>();
        LogootDocument lg = (LogootDocument) (this.getDoc());
        String content = "";
        int N = 0, offset = 0;
        int position = opt.getPosition();

        if (opt.getType() == TraceOperation.OpType.ins) {
            N = opt.getContent().length();
            content = opt.getContent();
            ArrayList<LogootIdentifier> patch = strategy.generateLineIdentifiers(this, lg.getIdTable().get(position),
                    lg.getIdTable().get(position + 1), N);

            ArrayList<Character> lc = new ArrayList<Character>(patch.size());
            for (int cmpt = 0; cmpt < patch.size(); cmpt++) {
                char c = content.charAt(cmpt);
                LogootOperation log = LogootOperation.insert(opt, patch.get(cmpt), c);
                lop.add(log);
                lc.add(c);
            }
            lg.getIdTable().addAll(position + 1, patch);
            lg.getDocument().addAll(position + 1, lc);

        } else {
            offset = opt.getOffset();
            for (int k = 1; k <= offset; k++) {
                LogootOperation wop = LogootOperation.Delete(opt, lg.getIdTable().get(position + k));
                lop.add(wop);
            }
            lg.getIdTable().removeRangeOffset(position + 1, offset);
            lg.getDocument().removeRangeOffset(position + 1, offset);
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
}