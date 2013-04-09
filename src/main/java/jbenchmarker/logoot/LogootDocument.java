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
package jbenchmarker.logoot;

import collect.RangeList;
import crdt.Factory;
import java.util.List;
import jbenchmarker.core.Document;
import jbenchmarker.core.Operation;
import jbenchmarker.core.SequenceMessage;

/**
 * A Logoot document. Contains a list of Charater and the corresponding list of LogootIndentitifer.
 * @author urso mehdi
 */
public class LogootDocument<T> implements  Factory<LogootDocument<T>>, TimestampedDocument {
    private int myClock;
    protected int replicaNumber;
    
    final protected RangeList<ListIdentifier> idTable;
    final protected RangeList<T> document;
    final protected LogootStrategy strategy;

    public LogootDocument(int r, LogootStrategy strategy) {
        super();
        document = new RangeList<T>();
        idTable = new RangeList<ListIdentifier>();
        this.strategy = strategy;
        this.replicaNumber = r;

        myClock = 0;        
        idTable.add(strategy.begin());
        document.add(null);
        idTable.add(strategy.end());
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

    protected int dicho(ListIdentifier idToSearch) {
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
        ListIdentifier idToSearch = lg.getIdentifiant();
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
    
    public void insert(int position, List<ListIdentifier> patch, List<T> lc) {
        idTable.addAll(position + 1, patch);
        document.addAll(position + 1, lc);
    }
    
    public void remove(int position, int offset) {
        idTable.removeRangeOffset(position + 1, offset);
        document.removeRangeOffset(position + 1, offset);
    }
    
    /**
     * Get the ith identifier in the table. O means begin marker.
     */
    public ListIdentifier getId(int pos) {
        return idTable.get(pos);
    }

    @Override
    public int viewLength() {
        return document.size()-2;
    }

    // TODO : duplicate strategy ?
    @Override
    public LogootDocument<T> create() {
        return new LogootDocument<T>(replicaNumber, strategy);
    }

    @Override
    public int nextClock() {
        return this.myClock++;
    }

    void setClock(int c) {
        this.myClock = c;
    }

    public void setReplicaNumber(int replicaNumber) {
        this.replicaNumber = replicaNumber;
    }

    @Override
    public int getReplicaNumber() {
        return replicaNumber;
    }
    
    /**
     * Produce one new identifier after this position.
     */
    public ListIdentifier getNewId(int postion) {
        return strategy.generateLineIdentifiers(this, idTable.get(postion),
                    idTable.get(postion + 1), 1).get(0);
    }
        
    List<ListIdentifier> generateIdentifiers(int position, int N) {
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
    
    // Test & Debug purpose
    boolean isSorted() {
        Comparable p = null;
        for (Comparable c : this.idTable) {
            if (p != null) {
                int comp = p.compareTo(c);
                if (comp >= 0) {
                    return false;
                } 
            }
            p = c;
        }
        return true;
    }
}
