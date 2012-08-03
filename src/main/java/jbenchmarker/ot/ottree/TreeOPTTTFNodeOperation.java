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
package jbenchmarker.ot.ottree;

import crdt.RemoteOperation;
import jbenchmarker.core.Operation;
import jbenchmarker.ot.soct2.OTMessage;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class TreeOPTTTFNodeOperation<T> implements RemoteOperation{

    static public enum OpType{ins,del,chT,child};
    
    OpType type;
    T contain;
    int position;
    int siteId;
    

    public TreeOPTTTFNodeOperation(OpType type, int position) {
        this.type = type;
        this.position = position;
    }

    public TreeOPTTTFNodeOperation(OpType type, int position, int siteId) {
        this.type = type;
        this.position = position;
        this.siteId = siteId;
    }

    
    public TreeOPTTTFNodeOperation(OpType type, T contain, int position, int siteId) {
        this.type = type;
        this.contain = contain;
        this.position = position;
        this.siteId = siteId;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }
    
    
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
    
    public T getContain() {
        return contain;
    }

    public void setContain(T contain) {
        this.contain = contain;
    }

    public OpType getType() {
        return type;
    }

    public void setType(OpType type) {
        this.type = type;
    }

 
    
    @Override
    public Operation clone() {
        T cont=this.contain;
        if (this.type==OpType.child){
            cont=(T) ((OTMessage)cont).clone();
        }
        return new TreeOPTTTFNodeOperation(type, cont, position, siteId);
    }
    
}
