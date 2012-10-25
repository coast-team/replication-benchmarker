/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2011 INRIA / LORIA / SCORE Team
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
package jbenchmarker.logootOneId;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class LogootOneIdentifier implements Comparable<LogootOneIdentifier>, Serializable {

    final private BigDecimal digit;
    final private int peerID;
    final private int clock;
    
    public LogootOneIdentifier(BigDecimal d, int pid, int c) {
        this.digit = d;
        this.peerID = pid;
        this.clock = c;
    }

    public BigDecimal getDigit() {
        return digit;
    }

    public int getPeerID() {
        if(peerID == -1)
            return 1;
        return peerID;
    }

    public int getClock() {
        return clock;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LogootOneIdentifier other = (LogootOneIdentifier) obj;
        if (!this.digit.equals(other.digit) ) {
            return false;
        }
        if (this.peerID != other.peerID) {
            return false;
        }
        if (this.clock != other.clock) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        //hash = 97 * hash + (int) (this.digit.pow(this.digit >>> 32));
        hash = 97 * hash + this.peerID;
        hash = 97 * hash + this.clock;
        return hash;
    }

    @Override
    public String toString() {
        return "<" + digit + ',' + peerID + ',' + clock + '>';
    }
    
    @Override
    public int compareTo(LogootOneIdentifier t) {
        int precision = Math.max(this.digit.precision(), t.digit.precision());
        BigDecimal p = this.digit.setScale(precision, RoundingMode.DOWN),
                q = t.digit.setScale(precision, RoundingMode.DOWN);
        if (p.unscaledValue().equals(q.unscaledValue())) {
            return (this.peerID == t.peerID) ? this.clock - t.clock : this.peerID - t.peerID;
        } else {
            if(this.digit.equals(BigDecimal.valueOf(0))) //first id in table
                return -1;
             if(this.digit.equals(BigDecimal.valueOf(1))) //last id in table
                return 1;
            else {
                p = new BigDecimal(this.digit + "" + this.getPeerID());
                if(t.digit.equals(BigDecimal.ZERO))
                    q = new BigDecimal("0." + t.getPeerID());
                else
                    q = new BigDecimal(t.digit + "" + t.getPeerID());
                precision = Math.max(p.precision(), q.precision());
                p = p.setScale(precision, RoundingMode.DOWN);
                q = q.setScale(precision, RoundingMode.DOWN);
                return p.unscaledValue().compareTo(q.unscaledValue());
            }
        }

    }
    

//    @Override
//    public int compareTo(LogootOneIdentifier t) {
//        int precision = Math.max(this.digit.precision(), t.digit.precision());        
//        BigDecimal p = this.digit.setScale(precision, RoundingMode.DOWN),
//                q = t.digit.setScale(precision, RoundingMode.DOWN);
//
//        if (p.unscaledValue().equals(q.unscaledValue()) ) {
//            return (this.peerID == t.peerID) ? this.clock - t.clock : this.peerID - t.peerID;
//        } else {
//            return p.unscaledValue().compareTo(q.unscaledValue());
//        }
//    }
    
    public int getIndexReplica(LogootOneIdentifier Q) {
        double p, q;

        if (this.peerID == 0) {
            p = 0;
        } else {
            p = Math.log10((double) this.peerID);
        }
        if (Q.peerID == 0) {
            q = 0;
        } else {
            q = Math.log10((double) Q.peerID);
        }
        return (int) Math.max(p, q) + 1;
    }

    
    public BigDecimal degitWithReplica(int scale) {
        int p = this.peerID;
        if(this.peerID < 0) p= this.peerID*(-1);
        return new BigDecimal(String.format("%s%0" + scale + "d", this.digit, p));
//        BigDecimal b = new BigDecimal(this.digit + "" + this.peerID);
//        if (scale == 1) {
//            return b;
//        } else {
//            return new BigDecimal(String.format("%s%0" + scale + "d", b, 0));
//        }
    }

    @Override
    public LogootOneIdentifier clone() {
        return new LogootOneIdentifier(digit, peerID, clock);
    }

}