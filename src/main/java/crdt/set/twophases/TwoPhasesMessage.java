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
package crdt.set.twophases;

import crdt.set.observedremove.*;
import crdt.OperationBasedOneMessage;
import crdt.set.CommutativeSetMessage;
import crdt.set.CommutativeSetMessage.OpType;
import crdt.set.lastwriterwins.TypedMessage;


/**
 *
 * @author score
 */
public class TwoPhasesMessage<T> extends TypedMessage<T> {

    public TwoPhasesMessage(OpType type, T t) {  
        super(type, t);
    }

    
    public void addTag(int r, int o) {
        Tag t = new Tag(r, o);
    }
    

    public void setContent(T c) {
        content = c;
    }

    @Override
    public String toString() {
        return "2M{" + "content=" + content + ", type=" + type + '}';
    }

    @Override
    public TwoPhasesMessage clone() {
        return new TwoPhasesMessage(type, content);
    }  
}
