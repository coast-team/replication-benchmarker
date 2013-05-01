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
package crdt.tree.fctree;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class FCPosition implements Comparable<FCPosition>, Serializable {

    List<Byte> position;

    /**
     * convert a position with identifier string representation to sequence of byte to ordering them
     * @param b position bytes
     * @param id identifier
     * @return b and id are concatened.
     */
    public static LinkedList<Byte> conv(List<Byte> b, FCIdentifier id) {
        LinkedList<Byte> s1 = new LinkedList<Byte>();
        s1.addAll(b);
        s1.add(Byte.MIN_VALUE);
        byte[] b1=Integer.toHexString(id.getReplicaNumber()).getBytes();
        for (byte bb : b1) {
            s1.add(bb);
        }
        s1.add(Byte.MIN_VALUE);
        b1 = Integer.toHexString(id.getOperationNumber()).getBytes();
        for (byte bb : b1) {
            s1.add(bb);
        }
        return s1;
    }

    /*int compareTo(FCIdentifier id1, FCIdentifier id2, FCPosition f2) {
        Iterator<Byte> s1 = conv(this.position, id1).iterator();
        Iterator<Byte> s2 = conv(f2.position, id2).iterator();
        while (s1.hasNext() && s2.hasNext()) {
            byte b1 = s1.next();
            byte b2 = s2.next();
            if (b1 < b2) {
                return -1;
            }
            if (b1 > b2) {
                return 1;
            }
        }
        if (s1.hasNext()) {
            return 1;
        }
        if (s2.hasNext()) {
            return -1;
        }
        return 0;
    }*/

    /**
     * Make object from position.
     * @param position
     */
    public FCPosition(List<Byte> position) {
        this.position = position;
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
        final FCPosition other = (FCPosition) obj;
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
    public int compareTo(FCPosition o) {
        Iterator <Byte> s1=this.position.iterator();
        Iterator <Byte> s2=o.position.iterator();
        
        while (s1.hasNext() && s2.hasNext()) {
            byte b1 = s1.next();
            byte b2 = s2.next();
            if (b1 < b2) {
                return -1;
            }
            if (b1 > b2) {
                return 1;
            }
        }
        /* S1 is longer than s2*/
        if (s1.hasNext()) {
            return 1;
        }
        /* S2 is longer than s1*/
        if (s2.hasNext()) {
            return -1;
        }
        // Both have same size
        return 0;
    }
}
