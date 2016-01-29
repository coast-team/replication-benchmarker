/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2016 LORIA / Inria / SCORE Team
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

package jbenchmarker.logoot.tree;

import crdt.Factory;
import crdt.Operation;
import java.util.List;
import jbenchmarker.core.Document;
import jbenchmarker.core.SequenceOperation.OpType;
import collect.TreeList;
import java.util.LinkedList;
import jbenchmarker.logoot.ListIdentifier;
import jbenchmarker.logoot.LogootOperation;
import jbenchmarker.logoot.LogootStrategy;
import jbenchmarker.logoot.TimestampedDocument;

/**
 * A Logoot Tree document. Contains a TreeList of <LogootIndentitifer,character> 
 * @author urso mehdi
 */
public class LogootTreeDocument<T> implements Document, Factory<LogootTreeDocument<T>>, TimestampedDocument {

    final private TreeList<LogootTreeElement<T>> document;
    final protected LogootStrategy strategy;
    protected int replicaNumber;
    private int myClock;

     public LogootTreeDocument(int r, LogootStrategy strategy) {
        super();
        document = new TreeList<LogootTreeElement<T>>();
        this.strategy = strategy;
        this.replicaNumber = r;
        myClock = 0;
        
        LogootTreeElement Begin = new LogootTreeElement(strategy.begin());
        LogootTreeElement End = new LogootTreeElement(strategy.end());

        document.add(Begin);
        document.add(End);
    }
    
    @Override
    public String view() {
        StringBuilder s = new StringBuilder();
        for (int i=1; i< document.size()-1; ++i) {
            s.append(document.get(i).getElement());
        }
        return s.toString();
    }

    // SUB-OPTIMAL
    @Override
    public void apply(Operation op) {
        LogootOperation<T> lg = (LogootOperation) op;
        int pos = document.locate(new LogootTreeElement<T>(lg.getPosition(), lg.getContent()));
        //Insertion et Delete
        if (lg.getType() == OpType.insert) {
            document.add(pos, new LogootTreeElement<T>(lg.getPosition(), lg.getContent()));
        } else if (pos < document.size() && 
                document.get(pos).getDigit().equals(lg.getPosition())) {
            document.remove(pos);
        }
    }

    List<ListIdentifier> generateIdentifiers(int position, int N) {
        return strategy.generateLineIdentifiers(this, document.get(position).getDigit(),
                document.get(position + 1).getDigit(), N);
    }
    
    public void insert(int position, List<LogootOperation<T>> patch) {
        List<LogootTreeElement<T>> l = new LinkedList<LogootTreeElement<T>>();
        for (LogootOperation<T> e : patch) {
            l.add(new LogootTreeElement<T>(e.getPosition(), e.getContent()));
        }
        document.addAll(position + 1, l);       // SUB-OPTIMAL
    }
    
    public void remove(int position, int offset) {  
        for (int i = 0; i < offset; ++i) {      // SUB-OPTIMAL
            document.remove(position + 1);
        }
    }
    
    public LogootTreeElement getId(int pos) {
        return document.get(pos);
    }

    @Override
    public int viewLength() {
        return document.size()-2;
    }

    @Override
    public LogootTreeDocument<T> create() {
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
        final LogootTreeDocument<T> other = (LogootTreeDocument<T>) obj;
        if (this.document != other.document && (this.document == null || !this.document.equals(other.document))) {
            return false;
        }
        return true;
    }

    protected int getClock() {
        return this.myClock;
    }

    void setClock(int c) {
        this.myClock = c;
    }
    
    @Override
    public int getReplicaNumber() {
        return replicaNumber;
    }
    
    public void setReplicaNumber(int replicaNumber) {
        this.replicaNumber = replicaNumber;
    }

    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.document != null ? this.document.hashCode() : 0);
        return hash;
    }
    
    @Override
    public int nextClock() {
        return this.myClock++;
    }
}