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
package crdt.tree;

import crdt.CRDTMessage;
import crdt.PreconditionException;
import crdt.set.CRDTSet;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author urso
 */
public class PseudoSet extends CRDTSet {

    Set s;

    public PseudoSet() {
        this.s = new HashSet();
    }

    public PseudoSet(Set s) {
        this.s = s;
    }

    @Override
    public Object lookup() {
        return s;
    }

    @Override
    public CRDTSet create() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CRDTMessage innerAdd(Object t) throws PreconditionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CRDTMessage innerRemove(Object t) throws PreconditionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean contains(Object t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void applyOneRemote(CRDTMessage msg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void adding(Object t) {
        s.add(t);
        notifyAdd(t);
    }

    public void removing(Object t) {
        s.remove(t);
        notifyDel(t);
    }
}
