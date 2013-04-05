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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.reflections.vfs.Vfs;


/**
 * Treedoc identifiers for logoot algorithm.
 * Identifier are a list of triple << left/right, replica_id, clock_id >>.
 * @author urso
 */
class LogootBinaryPosition implements ListIdentifier<LogootBinaryPosition> {

    enum Direction { left, right };
    List<Component> position;
    
    LogootBinaryPosition(List<Component> position) {
        this.position = new ArrayList<Component>(position);
    }
    
    LogootBinaryPosition(int size) {
        this.position = new ArrayList<Component>(size);
    }
    
    LogootBinaryPosition(Direction d, int r, int c) {
        this.position = new ArrayList<Component>(1);
        this.position.add(new Component(d, r, c));
    }
    
    LogootBinaryPosition plus(LogootBinaryPosition rest) {
        LogootBinaryPosition p = new LogootBinaryPosition(position);
        p.position.addAll(rest.position);
        return p;
    }
    
    LogootBinaryPosition plus(Direction direction, int replicaNumber, int nextClock) {
        LogootBinaryPosition p = new LogootBinaryPosition(position);
        p.position.add(new Component(direction, replicaNumber, nextClock));
        return p;
    }
    
    @Override
    public int length() {
        return position.size();
    }

    @Override
    public int replica() {
        return position.get(position.size() - 1).replica;
    }

    @Override
    public int clock() {
        return position.get(position.size() - 1).clock;
    }

    @Override
    public ListIdentifier clone() {
        return new LogootBinaryPosition(this.position);
    }

    @Override
    public int compareTo(LogootBinaryPosition other) {
        Iterator<Component> it = this.position.iterator(), ot = other.position.iterator();
        while (it.hasNext() && ot.hasNext()) {
            Component t = it.next(), o = ot.next();
            if (t.dir != o.dir) {
                return t.dir == Direction.left ? -1 : 1;
            } else if (t.replica != o.replica) {
                return t.replica - o.replica;
            } else if (t.clock != o.clock) {
                return t.clock - o.clock;
            }
        }
        if (!it.hasNext() && !ot.hasNext()) {
            return 0;
        } else if (it.hasNext()) {
            return it.next().dir == Direction.left ? -1 : 1;
        } else {
            return ot.next().dir == Direction.right ? -1 : 1;
        }
    }

    public boolean isRightSonOf(LogootBinaryPosition father) {
        Iterator<Component> it = this.position.iterator(), ot = father.position.iterator();
        while (it.hasNext() && ot.hasNext()) {
            if (!it.next().equals(ot.next())) {
                return false;
            }
        }
        return it.hasNext() && it.next().dir == Direction.right;
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.position != null ? this.position.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LogootBinaryPosition other = (LogootBinaryPosition) obj;
        if (this.position != other.position && (this.position == null || !this.position.equals(other.position))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return position.toString();
    }

    static class Component {
        final Direction dir;
        final int replica, clock;

        Component(Direction d, int r, int c) {
            this.dir = d;
            this.replica = r;
            this.clock = c;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 67 * hash + (this.dir != null ? this.dir.hashCode() : 0);
            hash = 67 * hash + this.replica;
            hash = 67 * hash + this.clock;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Component other = (Component) obj;
            if (this.dir != other.dir) {
                return false;
            }
            if (this.replica != other.replica) {
                return false;
            }
            if (this.clock != other.clock) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "<" + dir + ", " + replica + ", " + clock + '>';
        }
    }
}
