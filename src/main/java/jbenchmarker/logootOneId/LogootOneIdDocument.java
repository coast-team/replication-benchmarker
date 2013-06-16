/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2013 LORIA / Inria / SCORE Team
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

package jbenchmarker.logootOneId;

import collect.RangeList;
import crdt.Factory;
import crdt.Operation;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import jbenchmarker.core.Document;
import jbenchmarker.core.SequenceOperation.OpType;

/**
 * A Logoot document. Contains a list of Charater and the corresponding list of LogootIndentitifer.
 * @author urso mehdi
 */
public class LogootOneIdDocument<T> implements Document, Factory<LogootOneIdDocument<T>> {

    final private RangeList<LogootOneIdentifier> idTable;
    final private RangeList<T> document;
    final protected LogootOneIdStrategy strategy;
    protected int replicaNumber;
    private int myClock;

     public LogootOneIdDocument(int r, LogootOneIdStrategy strategy) {
        super();
        document = new RangeList<T>();
        idTable = new RangeList<LogootOneIdentifier>();
        this.strategy = strategy;
        this.replicaNumber = r;
        myClock = 0;
        
        LogootOneIdentifier Begin = new LogootOneIdentifier(BigDecimal.valueOf(0));
        LogootOneIdentifier End = new LogootOneIdentifier(BigDecimal.valueOf(1));

        idTable.add(Begin);
        document.add(null);
        idTable.add(End);
        document.add(null);
    }
    
    @Override
    public String view() {
        StringBuilder s = new StringBuilder();
        for (int i=1; i< document.size()-1; ++i) {
            s.append(document.get(i));
        }
        return s.toString();
    }

    int dicho(LogootOneIdentifier idToSearch) {
        int startIndex = 0, endIndex = idTable.size() - 1, middleIndex;
        do {
            middleIndex = startIndex + (endIndex - startIndex) / 2;
            int c = idTable.get(middleIndex).compareTo(idToSearch);
            if (c == 0) {
                return middleIndex;
            } else if (c < 0) {
                startIndex = middleIndex + 1;
            } else {
                endIndex = middleIndex - 1;
            }
        } while (startIndex < endIndex);
        return (idTable.get(startIndex).compareTo(idToSearch) < 0)
                ? startIndex + 1 : startIndex;
    }
  
    @Override
    public void apply(Operation op) {
        LogootOneIdOperation lg = (LogootOneIdOperation) op;
        LogootOneIdentifier idToSearch = lg.getIdentifiant();
        int pos = dicho(idToSearch);
        //Insertion et Delete
        if (lg.getType() == OpType.insert) {
            idTable.add(pos, idToSearch);
            document.add(pos, (T) lg.getContent());
        } else if (idTable.get(pos).equals(idToSearch)) {
            idTable.remove(pos);
            document.remove(pos);
        }
    }

    ArrayList<LogootOneIdentifier> generateIdentifiers(int position, int N) {
        return strategy.generateLineIdentifiers(this, idTable.get(position),
                idTable.get(position + 1), N);
    }
    
    public void insert(int position, List<LogootOneIdentifier> patch, List<T> lc) {
        idTable.addAll(position + 1, patch);
        document.addAll(position + 1, lc);
    }
    
    public void remove(int position, int offset) {
        idTable.removeRangeOffset(position + 1, offset);
        document.removeRangeOffset(position + 1, offset);
    }
    
    public LogootOneIdentifier getId(int pos) {
        return idTable.get(pos);
    }

    @Override
    public int viewLength() {
        return document.size()-2;
    }

    @Override
    public LogootOneIdDocument<T> create() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LogootOneIdDocument<T> other = (LogootOneIdDocument<T>) obj;
        if (this.idTable != other.idTable && (this.idTable == null || !this.idTable.equals(other.idTable))) {
            return false;
        }
        if (this.document != other.document && (this.document == null || !this.document.equals(other.document))) {
            return false;
        }
        return true;
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
    int getReplicaNumber() {
        return replicaNumber;
    }
        public void setReplicaNumber(int replicaNumber) {
        this.replicaNumber = replicaNumber;
    }

    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.idTable != null ? this.idTable.hashCode() : 0);
        hash = 97 * hash + (this.document != null ? this.document.hashCode() : 0);
        return hash;
    }
}