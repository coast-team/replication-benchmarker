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
import java.util.List;
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
    protected Type type;

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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Edition(Edit edit, RawText a, RawText b) {
        this.beginA = edit.getBeginA();
        this.endA = edit.getEndA();
        this.beginB = edit.getBeginB();
        this.endB = edit.getEndB();
        this.type = edit.getType();
        this.ca = new ArrayList<String>();
        this.cb = new ArrayList<String>();
        for (int i = beginA; i < endA; ++i) {
            ca.add(a.getString(i) + '\n');
        }
        for (int i = beginB; i < endB; ++i) {
            cb.add(b.getString(i) + '\n');
        }
    }

    @Override
    public String toString() {
        StringBuilder  s = new StringBuilder();
        for (int i = this.getBeginA(); i < this.getEndA(); ++i) {
            s.append("--- (").append(i).append(") ").append(ca.get(i-this.getBeginA()));
        }
        for (int i = this.getBeginB(); i < this.getEndB(); ++i) {
            s.append("+++ (").append(i).append(") ").append(cb.get(i-this.getBeginB()));
        }
        return s.toString();
    }
}
