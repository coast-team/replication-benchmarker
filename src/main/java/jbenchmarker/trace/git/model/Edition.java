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
/*
 * represent a block od edits.
 */
package jbenchmarker.trace.git.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import jbenchmarker.core.SequenceOperation.OpType;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.Edit.Type;
import org.eclipse.jgit.diff.RawText;

/**
 *
 * @author urso
 */
public class Edition implements Serializable{    

    protected int beginA, endA, beginB, endB;
    protected List<String> ca;
    protected List<String> cb;
    protected OpType type;

    public Edition() {
    }

    public int getBeginA() {
        return beginA;
    }

    public void setBeginA(int beginA) {
        this.beginA = beginA;
    }

    public int getBeginB() {
        return beginB;
    }

    public void setBeginB(int beginB) {
        this.beginB = beginB;
    }

    public List<String> getCa() {
        return ca;
    }

    public void setCa(List<String> ca) {
        this.ca = ca;
    }

    public List<String> getCb() {
        return cb;
    }

    public void setCb(List<String> cb) {
        this.cb = cb;
    }

    public int getEndA() {
        return endA;
    }

    public void setEndA(int endA) {
        this.endA = endA;
    }

    public int getEndB() {
        return endB;
    }

    public void setEndB(int endB) {
        this.endB = endB;
    }

    public OpType getType() {
        return type;
    }

    public void setType(OpType type) {
        this.type = type;
    }

    public Edition(Edit edit, RawText a, RawText b) {
        this.beginA = edit.getBeginA();
        this.endA = edit.getEndA();
        this.beginB = edit.getBeginB();
        this.endB = edit.getEndB();
        this.type = typeof(edit.getType());
        this.ca = new ArrayList<String>();
        this.cb = new ArrayList<String>();
        for (int i = beginA; i < endA; ++i) {
            ca.add(a.getString(i) + '\n');
        }
        for (int i = beginB; i < endB; ++i) {
            cb.add(b.getString(i) + '\n');
        }
    }

    public Edition(OpType type, int beginA, int endA, int beginB, int endB, List<String> ca, List<String> cb) {
        this.beginA = beginA;
        this.endA = endA;
        this.beginB = beginB;
        this.endB = endB;
        this.ca = ca;
        this.cb = cb;
        this.type = type;
    }
    /**
     * One line edition.
     */
    public Edition(OpType type, int beginA, int beginB, String a, String b) {
        this.beginA = beginA;
        this.endA = beginA;
        this.beginB = beginB;
        this.endB = beginB;
        if (a != null) {
            this.ca = new LinkedList<String>();
            this.ca.add(a);
            ++endA;
        }
        if (b != null) {
            this.cb = new LinkedList<String>();
            this.cb.add(b);
            ++endB;
        }
        this.type = type;
    }
    
    @Override
    public String toString() {
        StringBuilder  s = new StringBuilder();
        for (int i = this.getBeginA(); i < this.getEndA(); ++i) {
            s.append("--- (").append(i).append(") ").append(ca.get(i-this.getBeginA()));
        }
        for (int i = this.getBeginB(); i < this.getEndB(); ++i) {
            s.append("+++ (").append(type == OpType.move ? i : i - this.getBeginB() + this.getBeginA()).
                    append(") ").append(cb.get(i-this.getBeginB()));
        }
        return s.toString();
    }

    private static OpType typeof(Type type) {
                switch (type) {
        case DELETE: 
            return OpType.delete;
        case INSERT:
            return OpType.insert;
        case REPLACE:
            return OpType.replace;
        default:
            return OpType.unsupported;    
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.beginA;
        hash = 37 * hash + this.beginB;
        hash = 37 * hash + (this.ca != null ? this.ca.hashCode() : 0);
        hash = 37 * hash + (this.cb != null ? this.cb.hashCode() : 0);
        hash = 37 * hash + (this.type != null ? this.type.hashCode() : 0);
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
        final Edition other = (Edition) obj;
        if (this.beginA != other.beginA) {
            return false;
        }
        if (this.endA != other.endA) {
            return false;
        }
        if (this.beginB != other.beginB) {
            return false;
        }
        if (this.endB != other.endB) {
            return false;
        }
        if (this.ca != other.ca && (this.ca == null || !this.ca.equals(other.ca))) {
            return false;
        }
        if (this.cb != other.cb && (this.cb == null || !this.cb.equals(other.cb))) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        return true;
    }
}
