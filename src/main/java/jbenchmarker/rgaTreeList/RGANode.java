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
package jbenchmarker.rgaTreeList;

import java.io.Serializable;

import jbenchmarker.rgaTreeList.TreeList.AVLNode;

/**
 *
 * @author Roh
 */
public class RGANode<T> implements Serializable {

    private RGAS4Vector key;	 
    private T content;
    private RGANode next;
    private AVLNode tree;
    
    

    public RGANode() {
    }

    public RGANode(RGAS4Vector s4v, T c) {
        this.key = s4v;
        this.content = c;
    }

    public RGAS4Vector getKey() {
        return key;
    }

    public T getContent() {
        return content;
    }
    
    public boolean isVisible() {
        return content != null;
    }

    public void makeTombstone() {
        this.content = null;
        this.tree=null;
    }

    public void setNext(RGANode nd) {
        this.next = nd;
    }

    public RGANode getNext() {
        return next;
    }
    
    public AVLNode getTree() {
 		return tree;
 	}

 	public void setTree(AVLNode tree) {
 		this.tree = tree;
 	}
    
    public RGANode getNextVisible() {
        RGANode node = next;
        while (node != null && !node.isVisible()) {
            node = node.next;
        }
        return node;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RGANode other = (RGANode) obj;
        if (this.key != other.key && (this.key == null || !this.key.equals(other.key))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.key != null ? this.key.hashCode() : 0);
        return hash;
    }

    /*@Override
    public String toString() {
        return "RGANode{" + key + ", T=" + content + "}";
    }*/

    @Override
    public String toString() {
        return content+"";
    }
}
