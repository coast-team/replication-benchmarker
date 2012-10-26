/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2012 LORIA / Inria / SCORE Team
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

import crdt.tree.fctree.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author urso Stephane Martin <stephane.martin@loria.fr>
 */
public class LogootListPosition implements ListIdentifier<LogootListPosition> {

    LinkedList<Byte> position;    

    /**
     * Make object from position.
     * @param position
     */
    public LogootListPosition(List<Byte> position) {
        this.position = new LinkedList<Byte>(position);
    }
    
    public LogootListPosition(byte position) {
        this.position = new LinkedList<Byte>();
        this.position.add(position);
    }
    
    final void addIntTo4Byte(int value) {
        position.add((byte) (value & 0xff));
        position.add((byte) (value >> 8 & 0xff));
        position.add((byte) (value >> 16 & 0xff));
        position.add((byte) (value >>> 24));
    }

    final void addIntToByte(int value) {
        position.add((byte) (value & 0xff));
        if (value > 0xff) {
            position.add((byte) (value >> 8 & 0xff));
            if (value > 0xffff) {
                position.add((byte) (value >> 16 & 0xff));
                if (value > 0xffffff) {
                    position.add((byte) (value >>> 24));
                }
            }
        }
    }

    private LogootListPosition(List<Byte> digits, int replicaNumber, int clock) {
        this(digits);
        addIntTo4Byte(replicaNumber);
        addIntTo4Byte(clock);
    }

    /**
     * Return the position
     * @return
     */
    public List<Byte> getPosition() {
        return position;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LogootListPosition other = (LogootListPosition) obj;
        if (!this.position.equals(other.position)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.position != null ? this.position.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "FCPosition{" + position + '}';
    }

    @Override
    public long getDigitAt(int index) {
        if (index >= position.size()) {
            return 0;
        } else {
            return position.get(index);
        }
    }

    @Override
    public Object getComponentAt(int index) {
        return position.get(index);
    }

    @Override
    public int length() {
        return position.size();
    }

    @Override
    public ArrayList<ListIdentifier> generateN(int n, ListIdentifier Q, int index, long interval, LogootDocument doc) {
        ArrayList<ListIdentifier> patch = new ArrayList<ListIdentifier>();
        List<Byte> digits = positionFilledwithMin(index);
        LogootListPosition P;
        for (int i = 0; i < n; i++) {
            digits = plus(digits, LogootStrategy.nextLong(interval) + 1);
            P = new LogootListPosition(digits, doc.getReplicaNumber(), doc.getClock());
            doc.incClock();
            patch.add(P);
        }
        return patch;
    }

    @Override
    public ListIdentifier clone() {
        return new LogootListPosition(position);
    }

    @Override
    public int compareTo(LogootListPosition o) {
        Iterator<Byte> s1 = this.position.iterator();
        Iterator<Byte> s2 = o.position.iterator();
        while (s1.hasNext() && s2.hasNext()) {
            int d = s1.next() - s2.next();
            if (d != 0) {
                return d;
            }
        }
        if (s1.hasNext()) {
            return 1;
        } else if (s2.hasNext()) {
            return -1;
        }
        return 0;
    }

    private List<Byte> plus(List<Byte> digits, long l) {
        LinkedList<Byte> list = new LinkedList<Byte>(digits);
        ListIterator<Byte> it = list.listIterator(list.size());      
        while (l > 0) {
            int val = (int) (it.previous() + l);
            it.set((byte) (val & 0xff));
            l = (l >> 8) + ((val + 128) >> 8);
        }
        return list;
    }

    private List<Byte> positionFilledwithMin(int index) {
        if (index < position.size()) {
            return position.subList(0, index + 1);
        } else {
            // TODO : more efficient
            List<Byte> f = new LinkedList<Byte>(position);
            for (int j = position.size(); j <= index; ++j) {
                f.add(Byte.MIN_VALUE);
            }
            return f;
        }
    }
}
