/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/ Copyright (C) 2012
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
package jbenchmarker.logoot;

import java.io.Serializable;
import java.util.Arrays;




/**
 *
 * @author urso Stephane Martin <stephane.martin@loria.fr>
 */
public class LogootListPosition implements ListIdentifier<LogootListPosition> {

    private final Content position;

    private Content createContent(int base, int size) {
        if (base == 8) {
            return new ByteContent(size);
        } else if (base == 16) {
            return new ShortContent(size);
        } else if (base == 32) {
            return new IntContent(size);
        } else {
            throw new IllegalArgumentException("Illegal base.");
        }
    }

    LogootListPosition(Content position) {
        this.position = position;
    }

    /**
     * Make object from position.
     */
    public LogootListPosition(int base, int pos) {
        this.position = createContent(base, 1);
        this.position.set(0, pos);
    }

    /**
     * Prepare ListPosition with given position size replica identifier and
     * clock.
     */
    public LogootListPosition(int base, int size, int replicaId, int clock) {
        if (base < 32) {
            int nr = nbElem(base, replicaId);
            int nc = nbElem(base, clock);
            int iSize = size + nr + nc;
            this.position = createContent(base, iSize + 1);
            putInt(size, replicaId);
            putInt(size + nr, clock);
            this.position.set(iSize, ((nr << 4) + nc));
        } else { // int
            this.position = createContent(base, size + 2);
            this.position.set(size, replicaId);
            this.position.set(size + 1, clock);
        }   
    }

    /**
     * Number of element to represent this int. TODO : More efficient ?
     */
    private static byte nbElem(int base, int value) {
        byte l = 0;
        while (value != 0) {
            value >>>= base;
            ++l;
        }
        return l;
    }


    /**
     * Puts an reserved l-length element presentation of the int value at the
     * given position.
     */
    private void putInt(int pos, int value) {
        for (int i = 0; value != 0; ++i) {
            position.set(pos + i, value & position.mask());
            value >>>= position.base();
        }
    }
    
    /**
     * Retrieves the int encodes with length element finishing after position pos.
     */
    private int getInt(int pos, int length) {
        int value = 0;
        for (int i = 1; i <= length; ++i) {
            value = (value << position.base()) + (position.get(pos - i) & position.mask());
        }
        return value;
    }
    
    /**
     * Return the element in the position i or Byte.MIN_VALUE
     */
    public int getSafe(int i) {
        return i < position.length() ? position.get(i) : position.min();
    }

    /**
     * Sets the lement at the given position.
     *
     */
    void set(int pos, int b) {
        position.set(pos, b);
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
        StringBuilder s = new StringBuilder("FLPosition[");
        for (int i = 0; i < position.length() - 1; ++i) {
            s.append(position.get(i)).append(',');
        }
        s.append(position.get(position.length() - 1)).append(']');
        return s.toString();
    }

    @Override
    public int length() {
        return position.length();
    }

    @Override
    public ListIdentifier clone() {
        return new LogootListPosition(position.clone());
    }

    @Override
    public int compareTo(LogootListPosition o) {
        int i = 0;
        while (i < this.position.length() && i < o.position.length()) {
            int d = this.position.get(i) - o.position.get(i);
            if (d != 0) {
                return d;
            }
            ++i;
        }
        if (i < this.position.length()) {
            return 1;
        } else if (i < o.position.length()) {
            return -1;
        }
        return 0;
    }

    @Override
    public int replica() {
        if (position.base() < 32) {
            int size = position.get(position.length() - 1);
            return getInt(position.length() - 1 - size & 0x0F, size >> 4);
        } else { // int
            return position.get(position.length() - 2);
        }   
    }

    @Override
    public int clock() {        
        if (position.base() < 32) {
            int size = position.get(position.length() - 1);
            return getInt(position.length() - 1, size & 0x0F);
        } else { // int
            return position.get(position.length() - 1);
        }  
    }
}




interface Content extends Cloneable {

    int get(int i);

    void set(int i, int v);

    Content clone();

    int length();

    int min();

    int base();

    public int mask();
}




class ByteContent implements Content, Serializable{

    final private byte[] position;
    
    ByteContent(int size) {
        position = new byte[size];
    }

    @Override
    public int get(int i) {
        return position[i];
    }

    @Override
    public void set(int i, int v) {
        position[i] = (byte) v;
    }
    
    ByteContent(byte [] pos) {
        position = pos;
    }
    
    @Override
    public Content clone() {
        return new ByteContent(position.clone());
    }

    @Override
    public int length() {
        return position.length;
    }

    @Override
    public int min() {
        return Byte.MIN_VALUE;
    }

    @Override
    public int base() {
        return 8;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Arrays.hashCode(this.position);
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
        final ByteContent other = (ByteContent) obj;
        if (!Arrays.equals(this.position, other.position)) {
            return false;
        }
        return true;
    }

    @Override
    public int mask() {
        return 0xFF;
    }
}




class ShortContent implements Content, Serializable {

    final private short[] position;

    ShortContent(int size) {
        position = new short[size];
    }

    @Override
    public int get(int i) {
        return position[i];
    }

    @Override
    public void set(int i, int v) {
        position[i] = (short) v;
    }

    ShortContent(short [] pos) {
        position = pos;
    }
    
    @Override
    public Content clone() {
        return new ShortContent(position.clone());
    }

    @Override
    public int length() {
        return position.length;
    }

    @Override
    public int min() {
        return Short.MIN_VALUE;
    }

    @Override
    public int base() {
        return 16;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + Arrays.hashCode(this.position);
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
        final ShortContent other = (ShortContent) obj;
        if (!Arrays.equals(this.position, other.position)) {
            return false;
        }
        return true;
    }

    @Override
    public int mask() {
        return 0xFFFF;
    }
}


class IntContent implements Content, Serializable {

    final private int[] position;

    IntContent(int size) {
        position = new int[size];
    }

    @Override
    public int get(int i) {
        return position[i];
    }

    @Override
    public void set(int i, int v) {
        position[i] = (int) v;
    }

    IntContent(int [] pos) {
        position = pos;
    }
    
    @Override
    public Content clone() {
        return new IntContent(position.clone());
    }

    @Override
    public int length() {
        return position.length;
    }

    @Override
    public int min() {
        return Integer.MIN_VALUE;
    }

    @Override
    public int base() {
        return 32;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Arrays.hashCode(this.position);
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
        final IntContent other = (IntContent) obj;
        if (!Arrays.equals(this.position, other.position)) {
            return false;
        }
        return true;
    }

    @Override
    public int mask() {
        return 0xFFFFFFFF;
    }
}
