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
package jbenchmarker.ot.otset;

import crdt.Operation;
import crdt.OperationBasedMessagesBag;

/**
 *
 * @author stephane martin
 */
public class OTSetOperations<Element> implements Operation {

    @Override
    public Operation clone() {
       return new OTSetOperations(type, e, siteId);
    }

    
  
   /* @Override
    protected String toString() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected OperationBasedMessagesBag clone() {
        throw new UnsupportedOperationException("Not supported yet.");
    }*/

    

    public enum OpType {
        Add, Del, Nop
    };
    private OpType type;
    private OpType noped;
    private Element e;
    private final int siteId;
    
    public OTSetOperations(OpType type, Element e, int siteId) {
        this.type = type;
        this.e = e;
        this.siteId = siteId;
    }
    
    public Element getElement() {
        return e;
    }
    
    public OpType getType() {
        return type;
    }

    public void convToNop() {
        noped = type;
        type= OpType.Nop;
    }

    public void convFromNop() {
        type = noped;
        noped = null;
    }

    @Override
    public String toString() {
        return "OTSetOperations{" + "type=" + type + ", noped=" + noped + ", e=" + e + ", siteId=" + siteId + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 73 * hash + (this.noped != null ? this.noped.hashCode() : 0);
        hash = 73 * hash + (this.e != null ? this.e.hashCode() : 0);
        hash = 73 * hash + this.siteId;
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
        final OTSetOperations<Element> other = (OTSetOperations<Element>) obj;
        if (this.type != other.type) {
            return false;
        }
        if (this.noped != other.noped) {
            return false;
        }
        if (this.e != other.e && (this.e == null || !this.e.equals(other.e))) {
            return false;
        }
        if (this.siteId != other.siteId) {
            return false;
        }
        return true;
    }
}
