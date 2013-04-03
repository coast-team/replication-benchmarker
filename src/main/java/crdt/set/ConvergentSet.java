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
package crdt.set;

import crdt.CRDT;
import crdt.CRDTMessage;
import crdt.PreconditionException;

/**
 *
 * @author urso
 */
public abstract class ConvergentSet<T> extends CRDTSet<T> implements CRDTMessage {        
    @Override
    public CRDTMessage concat(CRDTMessage msg) {
       return msg;
    }
    
    @Override
    public void execute(CRDT crdt){
        crdt.applyOneRemote(this);
    }
    
    @Override
    abstract public ConvergentSet<T> innerAdd(T t) throws PreconditionException;
    
    @Override
    abstract public ConvergentSet<T> innerRemove(T t) throws PreconditionException;
    
    @Override
    abstract public ConvergentSet<T> clone();

    @Override
    public int size() {
        return 1;
    }
}
