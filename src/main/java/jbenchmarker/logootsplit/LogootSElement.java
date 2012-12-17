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
package jbenchmarker.logootsplit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LogootSElement implements Comparable<LogootSElement>,Serializable {

    private List<LogootSIdentifier> idList;
    private int clock;

    public LogootSElement(List<LogootSIdentifier> list, int clock) {
        this.idList = list;
        this.clock = clock;
    }

    public LogootSElement(LogootSElement toSplit, int offset) {
        this.clock = toSplit.clock;
        this.idList = new ArrayList<LogootSIdentifier>();
        int i = 0;
        while (i < toSplit.size() - 1) {
            idList.add(new LogootSIdentifier(toSplit.getIdAt(i)));
            i++;
        }
        this.idList.add(new LogootSIdentifier(toSplit.getIdAt(i), offset + toSplit.getIdAt(i).getOffset()));
    }

    public LogootSElement(LogootSElement clone) {
        this.idList = new ArrayList<LogootSIdentifier>();
        this.clock = clone.clock;
        for (int i = 0; i < clone.size(); i++) {
            this.idList.add(new LogootSIdentifier(clone.getIdAt(i)));
        }
    }

    @Override
    public LogootSElement clone() {
        return new LogootSElement(this);
    }

    public int size() {
        return this.idList.size();
    }

    public LogootSIdentifier getIdAt(int i) {
        return this.idList.get(i);
    }

    @Override
    public int compareTo(LogootSElement other) {
        int i = 0;
        while (i < this.size() && i < other.size()) {
            int sizeComparisonSign = this.getIdAt(i).compareTo(other.getIdAt(i));
            if (sizeComparisonSign  != 0)
                return sizeComparisonSign;
            else
                i++;
        }
        if (i == this.idList.size()) {
            if (i == other.idList.size()) {
                int idComparisonSign = this.clock - other.clock;
                return idComparisonSign;
            }
            return -1;
        }
        return 1;
    }

    public LogootSElement origin() {
        LogootSElement el = new LogootSElement(this);
        LogootSIdentifier id = el.getIdAt(el.size() - 1);
        id.setOffset(0);
        return el;
    }
    
    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < this.idList.size(); i++) {
            s = s + this.idList.get(i) + ".";
        }
        s = s + this.clock;
        return s;

    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof LogootSElement) {
            return compareTo((LogootSElement) o) == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + (this.idList != null ? this.idList.hashCode() : 0);
        return hash;
    }
    
}