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

import collect.RangeList;
import crdt.Factory;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import jbenchmarker.core.Document;
import jbenchmarker.core.Operation;
import jbenchmarker.core.SequenceMessage;
import jbenchmarker.core.SequenceOperation;

/**
 * A Logoot document. Contains a list of Charater and the corresponding list of LogootIndentitifer.
 * @author urso mehdi
 */
public class LogootDocument<T> implements Document, Factory<LogootDocument<T>> {
    protected final int nbBit;
    protected final long max; // MAX = 2^nbBit - 1
    private int myClock;
    protected int replicaNumber;
    protected final BigInteger base;
    
    final protected RangeList<LogootIdentifier> idTable;
    final protected RangeList<T> document;
    final protected LogootStrategy strategy;

    public LogootDocument(int r, int nbBit, LogootStrategy strategy) {
        super();
        document = new RangeList<T>();
        idTable = new RangeList<LogootIdentifier>();
        this.strategy = strategy;
        this.replicaNumber = r;

        myClock = 0;
        this.nbBit = nbBit;
//        this.strategy = strategy;
        if (nbBit == 64) {
            this.max = Long.MAX_VALUE;
        } else {
            this.max = (long) Math.pow(2, nbBit) - 1;
        }
        base = BigInteger.valueOf(2).pow(nbBit);
        
        LogootIdentifier Begin = new LogootIdentifier(1), End = new LogootIdentifier(1);
        Begin.addComponent(new Component(0, -1, -1));
        End.addComponent(new Component(max, -1, -1));

        idTable.add(Begin);
        document.add(null);
        idTable.add(End);
        document.add(null);
    } 
    
    public long getNbBit() {
        return nbBit;
    }
    
    @Override
    public String view() {
        StringBuilder s = new StringBuilder();
        for (int i=1; i< document.size()-1; ++i) {
            s.append(document.get(i));
        }
        return s.toString();
    }

    protected int dicho(LogootIdentifier idToSearch) {
        int startIndex = 1, endIndex = idTable.size() - 1, middleIndex;
        while (startIndex < endIndex) {
            middleIndex = startIndex + (endIndex - startIndex) / 2;
            int c = idTable.get(middleIndex).compareTo(idToSearch);
            if (c == 0) {
                return middleIndex;
            } else if (c < 0) {
                startIndex = middleIndex + 1;
            } else {
                endIndex = middleIndex;
            }
        } 
        return startIndex;
    }
    
    @Override
    public void apply(Operation op) {
        LogootOperation lg = (LogootOperation) op;
        LogootIdentifier idToSearch = lg.getIdentifiant();
        int pos = dicho(idToSearch);
        //Insertion et Delete
        if (lg.getType() == SequenceMessage.MessageType.ins) {
            idTable.add(pos, idToSearch);
            document.add(pos, (T) lg.getContent());
        } else if (idTable.get(pos).equals(idToSearch)) {
            idTable.remove(pos);
            document.remove(pos);
        }
    }
    
    public void insert(int position, List<LogootIdentifier> patch, List<T> lc) {
        idTable.addAll(position + 1, patch);
        document.addAll(position + 1, lc);
    }
    
    public void remove(int position, int offset) {
        idTable.removeRangeOffset(position + 1, offset);
        document.removeRangeOffset(position + 1, offset);
    }
    
    public LogootIdentifier getId(int pos) {
        return idTable.get(pos);
    }

    @Override
    public int viewLength() {
        return document.size()-2;
    }

    // TODO : duplicate strategy ?
    @Override
    public LogootDocument<T> create() {
        return new LogootDocument<T>(replicaNumber, nbBit, strategy);
    }
    
    protected void incClock() {
        this.myClock++;
    }

    protected int getClock() {
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

    public void setReplicaNumber(int replicaNumber) {
        this.replicaNumber = replicaNumber;
    }

    int getReplicaNumber() {
        return replicaNumber;
    }
    
    public LogootIdentifier getNewId(int p) {
        return strategy.generateLineIdentifiers(this, idTable.get(p),
                    idTable.get(p + 1), 1).get(0);
    }
        
    ArrayList<LogootIdentifier> generateIdentifiers(int position, int N) {
        return strategy.generateLineIdentifiers(this, idTable.get(position),
                    idTable.get(position + 1), N);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LogootDocument<T> other = (LogootDocument<T>) obj;
        if (this.idTable != other.idTable && (this.idTable == null || !this.idTable.equals(other.idTable))) {
            return false;
        }
        if (this.document != other.document && (this.document == null || !this.document.equals(other.document))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.idTable != null ? this.idTable.hashCode() : 0);
        hash = 97 * hash + (this.document != null ? this.document.hashCode() : 0);
        return hash;
    }
}
