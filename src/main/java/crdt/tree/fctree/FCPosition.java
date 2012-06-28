/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.fctree;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class FCPosition {

    List<Byte> position;

    public static LinkedList<Byte> conv(List<Byte> b, FCIdentifier id) {
        LinkedList<Byte> s1 = new LinkedList<Byte>();
        s1.addAll(b);
        for (byte bb : id.toString().getBytes()) {
            s1.add(bb);
            //System.out.print("" + bb);
        }
        //System.out.println();
        return s1;
    }

    int compareTo(FCIdentifier id1, FCIdentifier id2, FCPosition f2) {
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
    }

    public FCPosition(List<Byte> position) {
        this.position = position;
    }

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
}
