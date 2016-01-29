/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2015 LORIA / Inria / SCORE Team
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
package jbenchmarker.logoot.tree;

import collect.TreeList;
import collect.TreeList.AVLNode;
import collect.TreeNode;
import java.io.Serializable;
import jbenchmarker.logoot.ListIdentifier;

public class LogootTreeElement<T> implements Comparable<LogootTreeElement>, Serializable, TreeNode {

    final private ListIdentifier digit;
    final private T element;
    private AVLNode treeNode;
    
    
    public LogootTreeElement(ListIdentifier d) {
        this(d, null);
    }

    LogootTreeElement(ListIdentifier d, T content) {
        this.digit = d;
        this.element = content;
    }

    public ListIdentifier getDigit() {
        return digit;
    }

    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LogootTreeElement other = (LogootTreeElement) obj;
        if (!this.digit.equals(other.digit) ) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "<" + digit + '>';
    }
    
    @Override
    public int compareTo(LogootTreeElement t) {
        return this.digit.compareTo(t.digit);
    }
    
        @Override
    public int hashCode() {
        int hash = 7;
        // TODO
        return hash;
    }


    @Override
    public LogootTreeElement clone() {
        return new LogootTreeElement(digit);
    }

    @Override
    public AVLNode getTree() {
        return treeNode;
    }

    @Override
    public void setTree(AVLNode n) {
        treeNode = n;
    }

    T getElement() {
        return element;
    }

}