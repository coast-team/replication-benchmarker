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
package jbenchmarker.ot.ottree;

import crdt.RemoteOperation;
import java.util.LinkedList;
import java.util.List;
import jbenchmarker.core.Operation;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class OTTreeRemoteOperation<T> implements RemoteOperation{

    private List<Integer> path;
    private T contain;
    static public enum OpType{ins,del,chT};
    //int position;
    private int siteId;
    private OpType type;

    public OTTreeRemoteOperation(List<Integer> path, T contain, int siteId, OpType type) {
        this.path = path;
        this.contain = contain;
        this.siteId = siteId;
        this.type = type;
    }

    public OTTreeRemoteOperation(List<Integer> path,  int siteId, OpType type) {
        this.path = path;
        this.siteId = siteId;
        this.type = type;
    }
    public OpType getType() {
        return type;
    }

    public void setType(OpType type) {
        this.type = type;
    }
    @Override
    public Operation clone() {
        return new OTTreeRemoteOperation(new LinkedList(path), contain, siteId, type);
    }

    public T getContain() {
        return contain;
    }

    public void setContain(T contain) {
        this.contain = contain;
    }

    public List<Integer> getPath() {
        return path;
    }

    public void setPath(List<Integer> path) {
        this.path = path;
    }

   /* public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }*/

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    @Override
    public String toString() {
        return "OTTreeRemoteOperation{" + "path=" + path + ", contain=" + contain + ", siteId=" + siteId + ", type=" + type + '}';
    }
    
    
}
