/*
 *  Replication Benchmarker
 *  https://github.com/score-team/replication-benchmarker/
 *  Copyright (C) 2013 LORIA / Inria / SCORE Team
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package crdt.tree.fctree;

import collect.OrderedNode;
import collect.SimpleNode;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public abstract class FCNode<T> implements OrderedNode<T>, Serializable {

    ArrayList<FCNode<T>> childrens;
    FCNode<T> father;
    FCIdentifier id;

    /**
     *
     * @return Identifier of node
     */
    public FCIdentifier getId() {
        return id;
    }

    public FCNode(FCNode<T> father, FCIdentifier id) {
        this.childrens = new ArrayList();
        this.father = father;
        this.id = id;
    }

    public FCNode<T> getNodeFromPath(List<Integer> path) {
        FCNode<T> ret = this;
        for (Integer i : path) {
            ret = ret.getChild(i);
        }
        return ret;
    }

    @Override
    public FCNode<T> getChild(int p) {
        if (p < 0 || p >= childrens.size()) {
            return null;
        }
        return childrens.get(p);
        /*
         if (p < 0 || p >= childrens.size()) {
         return null;
         }
         Iterator<FCNode<T>> it = childrens.iterator();
         while (p > 0) {
         it.next();
         p--;
         }
         return it.next();
         */
        //return p >= 0 && p < childrens.size() ? childrens.get(p) : null;
    }

    @Override
    public List<FCNode<T>> getElements() {
        //return Arrays.asList(childrens.toArray());
        return childrens;
    }

    @Override
    public int getChildrenNumber() {
        return childrens.size();
    }

    public void delChildren(FCNode node) {
        childrens.remove(node);
    }

    /**
     *
     * @return
     */
    public FCNode<T> getFather() {
        return father;
    }

    public void addChildren(FCNode node) {
        int min = 0;
        int max = childrens.size() - 1;

        while (min <= max) {
            int i = (int) (min + max) / 2;
            int p = childrens.get(i).getPosition().compareTo(node.getPosition());
            if (p < 0) {
                min = i + 1;
            } else if (p > 0) {
                max = i - 1;
            } else {
                break;
            }

        }
        childrens.add(min, node);
        // this.childrens.add(node);
    }

    @Override
    public boolean isChildren(SimpleNode<T> n) {
        return childrens.contains((FCNodeGf) n);
    }

    public abstract FCNode createNode(FCNode<T> father, T contain, FCPosition position, FCIdentifier id);

    @Override
    public void setReplicaNumber(int replicaNumber) {
    }

    abstract public FCPosition getPosition();

    @Override
    public Iterator<FCNode<T>> iterator() {
        return childrens.iterator();
    }

    public int size() {
        return childrens.size();
    }

    public String nodetail() {
        StringBuilder str = new StringBuilder();
        str.append(this.getValue());
        if (size() > 0) {
            str.append('{');
            for (FCNode node : childrens) {
                str.append(node.nodetail());
                str.append(',');
            }
            str.append('}');

        }
        return str.toString();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (this.childrens != null ? this.childrens.hashCode() : 0);
        hash = 67 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final FCNode<T> other = (FCNode<T>) obj;
        if (this.childrens != other.childrens && (this.childrens == null || !this.childrens.equals(other.childrens))) {
            return false;
        }
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

   
   
}
