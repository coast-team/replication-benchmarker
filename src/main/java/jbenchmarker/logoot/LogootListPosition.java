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

import java.util.Arrays;

/**
 *
 * @author urso Stephane Martin <stephane.martin@loria.fr>
 */
public class LogootListPosition implements ListIdentifier<LogootListPosition> {

    byte[] position;    

    /**
     * Make object from position.
     * @param position
     */
    LogootListPosition(byte[] position) {
        this.position = position;
    }
    
    public LogootListPosition(byte position) {
        this.position = new byte[1];
        this.position[0] = position;
    }
    
    /**
     * Prepare ListPosition with given position size replica identifier and clock. 
     */
    public LogootListPosition(int size, int replicaId, int clock) {
        byte nr = nbBytes(replicaId);
        byte nc = nbBytes(clock);
        int iSize = size + nr + nc;
        this.position = new byte[iSize + 1];
        putInt(size, replicaId);
        putInt(size + nr, clock);
        this.position[iSize] = (byte) ((nr << 4) + nc);
    }
    
    /**
     * Number of bytes to represent this int.
     * TODO : More efficient ?
     */
    private static byte nbBytes(int value) {
        byte l = 0;
        while (value != 0) {
            value >>>= 8;
            ++l;
        }
        return l;
    }
    
    /** 
     * Puts an reserved l-length byte presentation of the int value at the given position.
     **/
    private void putInt(int pos, int value) {
        for (int i = 0; value != 0; ++i) {
            position[pos + i] = (byte) (value & 0xff);
            value >>>= 8;                        
        }
    }

    /**
     * Return the element in the position i or Byte.MIN_VALUE
     */
    public byte getSafe(int i) {
        return i < position.length ? position[i] : Byte.MIN_VALUE;
    }
    

    /**
     * Sets the lement at the given position.
     **/
    void set(int pos, byte b) {
        position[pos] = b;
    }
    
    /**
     * Return the position
     * @return
     */
    public byte[] getPosition() {
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
        if (!Arrays.equals(this.position, other.position)) {
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
        for (byte b : position) {
            s.append(b).append(',');            
        }
        s.append(']');
        return s.toString();
    }



    @Override
    public int length() {
        return position.length;
    }

    @Override
    public ListIdentifier clone() {
        return new LogootListPosition(position.clone());
    }

    @Override
    public int compareTo(LogootListPosition o) {
        int i = 0;
        while (i < this.position.length && i < o.position.length) {
            int d = this.position[i] - o.position[i];
            if (d != 0) {
                return d;
            }
            ++i;
        }
        if (i < this.position.length) {
            return 1;
        } else if (i < o.position.length) {
            return -1;
        }
        return 0;
    }
}
