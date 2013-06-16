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
import crdt.Operation;
import crdt.Operation;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;
import jbenchmarker.logoot.*;

/**
 * A Move and Update document. Each element is uniquely identified. It is
 * associated to a set of unique CRDT position, and a map unique timestamp to
 * content value. Only one value appear in document view (LWW).
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
            s.append(elements.get(ts).value());
        }
        return s.toString();
    }

    @Override
    public void apply(Operation op) {
        MuOperation<T> lg = (MuOperation<T>) op;
        Timestamp target = lg.getTarget();
        Cell<T> c = elements.get(target);
        if (lg.getType() == OpType.replace) { // hack replace
            c.places.remove(lg.getOrigin());
            positions.remove(lg.getOrigin());
            elements.put(lg.getTimestamp(), new Cell(lg.getDestination(), lg.getTimestamp(), lg.getContent()));
            positions.put(lg.getDestination(), lg.getTimestamp());
        } else {
            if (c == null) {
                elements.put(target, c = new Cell());
            }
            if (lg.getOrigin() != null) {
                c.places.remove(lg.getOrigin());
                positions.remove(lg.getOrigin());
            }
            if (lg.getDestination() != null) {
                c.places.add(lg.getDestination());
                positions.put(lg.getDestination(), target);
            }
            if (lg.getOldVersions() != null) {
                c.contents.keySet().removeAll(lg.getOldVersions());
            }
            if (lg.getContent() != null) {
                c.contents.put(lg.getTimestamp(), lg.getContent());
            }
            if (!c.places.isEmpty() && c.contents.isEmpty()) { // remove tombstone from view
                positions.keySet().removeAll(c.places);
            }
        }
    }

    public List<Operation> insert(int position, List<T> lc, SequenceOperation opt) {
        List<ListIdentifier> pos = generateIdentifiers(position, lc.size());
        Iterator<ListIdentifier> itp = pos.iterator();
        List<Operation> patch = new LinkedList<Operation>();
        Iterator<T> itc = lc.iterator();
        while (itp.hasNext()) {
            ListIdentifier p = itp.next();
            T value = itc.next();
            Timestamp ts = new Timestamp(p.clock(), p.replica());
            MuOperation op = new MuOperation(ts, null, p, null, ts, value, OpType.insert);
            apply(op);
            patch.add(op);
        }
        return patch;
    }

    public List<Operation> delete(int position, int length, SequenceOperation opt) {
        List<Entry<ListIdentifier, Timestamp>> elems = new ArrayList<Entry<ListIdentifier, Timestamp>>(positions.entrySet()).subList(position, position + length);
        List<Operation> patch = new LinkedList<Operation>();
        for (Entry<ListIdentifier, Timestamp> e : elems) {
            Cell<T> c = elements.get(e.getValue());
            MuOperation op;
            if (c.places.size() > 1) { // move clones hack : treat as single delete
                op = new MuOperation(e.getValue(), e.getKey(), null, null, null, null, OpType.delete);
            } else {
                op = new MuOperation(e.getValue(), e.getKey(), null, new TreeSet(c.contents.keySet()), null, null, OpType.delete);
            }
            apply(op);
            patch.add(op);
        }
        return patch;
    }

    List<Operation> update(int position, List<T> content, SequenceOperation opt) {
        List<Entry<ListIdentifier, Timestamp>> elems = new ArrayList<Entry<ListIdentifier, Timestamp>>(positions.entrySet()).subList(position, position + content.size());
        List<Operation> patch = new LinkedList<Operation>();
        Iterator<T> itc = content.iterator();
        for (Entry<ListIdentifier, Timestamp> e : elems) {
            Cell<T> c = elements.get(e.getValue());
            MuOperation op;
            if (c.places.size() > 1) { // move clones hack : treat as replace
                ListIdentifier pos = generateAfter(e.getKey());
                op = new MuOperation(e.getValue(), e.getKey(), pos, null, Timestamp.of(pos), itc.next(), OpType.replace);
            } else {
                op = new MuOperation(e.getValue(), null, null, new TreeSet(c.contents.keySet()), nextTimestamp(), itc.next(), OpType.update);
            }
            apply(op);
            patch.add(op);
        }
        return patch;
    }

    List<Operation> move(int position, int destination, List<T> content, SequenceOperation opt) {
        List<Entry<ListIdentifier, Timestamp>> elems = new ArrayList<Entry<ListIdentifier, Timestamp>>(positions.entrySet()).subList(position, position + content.size());
        List<Operation> patch = new LinkedList<Operation>();
        Iterator<ListIdentifier> itpos = generateIdentifiers(destination < position ? destination : destination + content.size(), content.size()).iterator();
        Iterator<T> itc = content.iterator();
        for (Entry<ListIdentifier, Timestamp> e : elems) {
            Cell<T> c = elements.get(e.getValue());
            ListIdentifier d = itpos.next();
            T t = itc.next();
            MuOperation op = c.places.size() > 1 // move clones hack : treat as replace
                    ? new MuOperation(e.getValue(), e.getKey(), d, null, Timestamp.of(d), t, OpType.replace)
                    : c.value().equals(t)
                    ? new MuOperation(e.getValue(), e.getKey(), d, null, null, null, OpType.move)
                    : new MuOperation(e.getValue(), e.getKey(), d, new TreeSet(c.contents.keySet()), nextTimestamp(), t, OpType.move);
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
        int k = i;
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

    private ListIdentifier generateAfter(ListIdentifier posid) {
        ListIdentifier next = positions.higherKey(posid);
        if (next == null) {
            next = strategy.end();
        }
        return strategy.generateLineIdentifiers(this, posid, next, 1).get(0);
    }

    @Override
    public MuDocument<T> create() {
        return new MuDocument<T>(replicaNumber, strategy);
    }

    Object getObject(ListIdentifier p) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

/**
 * Elements
 *
 * @author urso
 * @param <T> Elemen
 */
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

    T value() {
        return contents.isEmpty() ? null : contents.lastEntry().getValue();
    }
}

class Timestamp implements Comparable<Timestamp> {

    static Timestamp of(ListIdentifier pos) {
        return new Timestamp(pos.clock(), pos.replica());
    }
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