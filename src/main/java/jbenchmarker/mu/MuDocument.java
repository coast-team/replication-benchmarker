/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/ Copyright (C) 2013
 * LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package jbenchmarker.mu;

import collect.Tree;
import crdt.Factory;
import java.util.*;
import java.util.Map.Entry;
import jbenchmarker.core.Operation;
import jbenchmarker.core.SequenceMessage;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;
import jbenchmarker.logoot.*;







/**
 * A Move and Update document. Each element is uniquely identified. 
 * It is associated to a set of unique CRDT position, and a map unique timestamp to content value. 
 * Only one value appear in document view (LWW). 
 * 
 * @author urso 
 */
public class MuDocument<T> implements TimestampedDocument, Factory<MuDocument<T>> {

    private int myClock;
    protected int replicaNumber;
    final protected NavigableMap<ListIdentifier, Timestamp> positions;
    final protected Map<Timestamp, Cell<T>> elements;
    final protected LogootStrategy strategy;

    public MuDocument(int r, LogootStrategy strategy) {
        super();
        positions = new TreeMap<ListIdentifier, Timestamp>();
        elements = new HashMap<Timestamp, Cell<T>>();
        this.strategy = strategy;
        this.replicaNumber = r;
        this.myClock = 0;
    }

    @Override
    public String view() {
        StringBuilder s = new StringBuilder();
        for (Timestamp ts : positions.values()) {
            s.append(elements.get(ts).contents.lastEntry().getValue());
        }
        return s.toString();
    }

    @Override
    public void apply(Operation op) {
        MuOperation<T> lg = (MuOperation<T>) op;
        Timestamp target = lg.getTarget();
        Cell<T> c = elements.get(target);
        switch (lg.getType()) {
            case insert:
                elements.put(target, new Cell(lg.getPosition(), lg.getTimestamp(), lg.getContent()));
                positions.put(lg.getPosition(), target);               
                break;
            case move:
                c.places.remove(lg.getPosition());
                positions.remove(lg.getPosition());
                c.places.add(lg.getDestination());
                positions.put(lg.getDestination(), target);
                break;
            case delete:
                c.places.remove(lg.getPosition());
                c.contents.keySet().removeAll(lg.getOldVersions());
                positions.remove(lg.getPosition());
                break;
            case update:
                c.contents.keySet().removeAll(lg.getOldVersions());
                c.contents.put(lg.getTimestamp(), lg.getContent());
                break;
        }
    }

    public List<SequenceMessage> insert(int position, List<T> lc, SequenceOperation opt) {
        List<ListIdentifier> pos = generateIdentifiers(position, lc.size());        
        Iterator<ListIdentifier> itp = pos.iterator();
        List<SequenceMessage> patch = new LinkedList<SequenceMessage>();
        Iterator<T> itc = lc.iterator();
        while (itp.hasNext()) {
            ListIdentifier p = itp.next();
            T value = itc.next();
            Timestamp ts = new Timestamp(p.clock(), p.replica()); 
            MuOperation op = new MuOperation(p, null, null, ts, ts, value, OpType.insert, opt);
            apply(op);
            patch.add(op);
        }
        return patch;
    }

    public List<SequenceMessage> delete(int position, int length, SequenceOperation opt) {
        List<Entry<ListIdentifier, Timestamp>> elems 
                = new ArrayList<Entry<ListIdentifier, Timestamp>>(positions.entrySet()).subList(position, position + length);
        List<SequenceMessage> patch = new LinkedList<SequenceMessage>();
        for (Entry<ListIdentifier, Timestamp> e : elems) {
            Cell<T> c = elements.get(e.getValue());
            MuOperation op = new MuOperation(e.getKey(), null, new TreeSet(c.contents.keySet()), null, e.getValue(), null, OpType.delete, opt);
            apply(op);
            patch.add(op);
        }
        return patch;
    }

    List<SequenceMessage> update(int position, List<T> content, SequenceOperation opt) {
        List<Entry<ListIdentifier, Timestamp>> elems 
                = new ArrayList<Entry<ListIdentifier, Timestamp>>(positions.entrySet()).subList(position, position + content.size());
        List<SequenceMessage> patch = new LinkedList<SequenceMessage>();
        Iterator<T> itc = content.iterator();
        for (Entry<ListIdentifier, Timestamp> e : elems) {
            Cell<T> c = elements.get(e.getValue());
            MuOperation op = new MuOperation(e.getKey(), null, new TreeSet(c.contents.keySet()), nextTimestamp(), e.getValue(), itc.next(), OpType.update, opt);
            apply(op);
            patch.add(op);
        }
        return patch;
    }
    
    @Override
    public int viewLength() {
        return positions.size();
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
    
    private Timestamp nextTimestamp() {
        return new Timestamp(nextClock(), replicaNumber);
    }
    
    // TODO Should be more efficient
    Iterator<Entry<ListIdentifier, Timestamp>> ith(int i) {
        Iterator<Entry<ListIdentifier, Timestamp>> it = positions.entrySet().iterator();
        while (--i > 0) {
            it.next();
        }
        return it;
    }
    
    /**
     * Produce n new identifiers after this position.
     */
    List<ListIdentifier> generateIdentifiers(int position, int N) {
        Iterator<Entry<ListIdentifier, Timestamp>> it = ith(position);
        ListIdentifier previous = position == 0 ? strategy.begin() : it.next().getKey(),
                next = position == viewLength() ? strategy.end() : it.next().getKey();        
        return strategy.generateLineIdentifiers(this, previous, next, N);
    }

    @Override
    public MuDocument<T> create() {
        return new MuDocument<T>(replicaNumber, strategy);
    }

    Object getObject(ListIdentifier p) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}


class Cell<T> {
    Set<ListIdentifier> places;
    NavigableMap<Timestamp, T> contents;

    public Cell() {
        this.places = new TreeSet<ListIdentifier>();
        this.contents = new TreeMap<Timestamp, T>();
    }

    Cell(ListIdentifier pos, Timestamp ts, T value) {
        this();
        places.add(pos);
        contents.put(ts, value);
    }
}

class Timestamp implements Comparable<Timestamp> {

    final int clock;
    final int replica;

    public Timestamp(int clock, int replica) {
        this.clock = clock;
        this.replica = replica;
    }

    @Override
    public int compareTo(Timestamp o) {
        if (this.clock == o.clock) {
            return Integer.compare(this.replica, o.replica);
        }
        return this.clock - o.clock;
    }

    @Override
    public String toString() {
        return "<" + clock + ", " + replica + '>';
    }
}