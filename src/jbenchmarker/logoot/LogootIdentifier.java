/**
 *   This file is part of ReplicationBenchmark.
 *
 *   ReplicationBenchmark is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   ReplicationBenchmark is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with ReplicationBenchmark.  If not, see <http://www.gnu.org/licenses/>.
 *
 **/
package jbenchmarker.logoot;

import java.util.*;

public class LogootIdentifier implements Comparable<LogootIdentifier> {

    final private ArrayList<Component> id;

    public LogootIdentifier(int capacity) {
        id = new ArrayList<Component>(capacity);
    }

    public ArrayList<Component> getID() {
        return id;
    }

    public Component getComponentAt(int position) {
        return id.get(position);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LogootIdentifier other = (LogootIdentifier) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public void addComponent(Component cp) {
        id.add(cp);
    }

    public int length() {
        return id.size();
    }

    public String toString() {
        String ligneIdentif = "";
        for (Component c : id) {
            ligneIdentif += c.toString();
        }
        return ligneIdentif;
    }

    LogootIdentifier plus(int index, long sep, LogootIdentifier Q, long max, int peer, int clock) {
        int j = index;
        List<Long> digits = this.digits(index);
        long start = digits.get(index), p = max - start;
        if (p < sep) {
            digits.set(j, sep - p - 1);
            --j;
            while (digits.get(j) == max) {
                digits.set(j,0L);
                --j;
            }
            digits.set(j, digits.get(j)+1);
        } else {
            digits.set(j, start + sep);
        }

        LogootIdentifier R = new LogootIdentifier(index + 1);
        int i = 0;
//        while (i < this.id.size() && digits.get(i) == id.get(i).getDigit()) {
//            R.addComponent(id.get(i).clone());
//            i++;
//        }
//        if (j < index || index >= id.size()) {
//            int m = Math.min(index, Q.id.size());
//            while (i < m && digits.get(i) >= Q.id.get(i).getDigit()) {
//                R.addComponent(Q.id.get(i).clone());
//                i++;
//            }
//        }
//
//        while (i <= index) {
//            R.addComponent(new Component(digits.get(i), peer, clock));
//            i++;
//        }
        while (i < index && i < this.length() && digits.get(i) == this.getDigitAt(i)) {
            R.addComponent(this.getComponentAt(i).clone());
            i++;
        }
        while (i < index && i < Q.length() && digits.get(i) >= Q.getDigitAt(i)) {
            R.addComponent(Q.getComponentAt(i).clone());
            i++;
        }
        while (i <= index) {
            R.addComponent(new Component(digits.get(i), peer, clock));
            i++;
        }
        return R;
    }

    /**
     * Returns O if j > index().
     **/
    long getDigitAt(int index) {
        if (index >= this.length()) {
            return 0;
        } else {
            return id.get(index).getDigit();
        }
    }

    private List<Long> digits(int index) {
        List<Long> l = new ArrayList<Long>();
        for (int i = 0; i <= index; i++) {
            if (i >= id.size()) {
                l.add((long) 0);
            } else {
                l.add(id.get(i).getDigit());
            }
        }
        return l;
    }

    public int compareTo(LogootIdentifier t) {
        int m = Math.min(id.size(), t.id.size());
        for (int i = 0; i < m; i++) {
            int c = id.get(i).compareTo(t.id.get(i));
            if (c != 0) {
                return c;
            }
        }
        return id.size() - t.id.size();
    }

    @Override
    public LogootIdentifier clone() {
        LogootIdentifier o = new LogootIdentifier(id.size());
        for (Component c : id) {
            o.id.add(c.clone());
        }
        return o;
    }
}