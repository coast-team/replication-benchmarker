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
package crdt.set.observedremove;

import crdt.OperationBasedOneMessage;
import crdt.set.CommutativeSetMessage;
import crdt.set.lastwriterwins.TypedMessage;
import java.util.HashSet;
import java.util.Set;


/**
 *
 * @author score
 */
public class OrMessage<T> extends TypedMessage<T> {
    
    private Set<Tag> tags;

    public OrMessage(OpType type, T t, Set<Tag> tag) {  
        super(type, t);
        this.tags = tag;
    }
    
    public Set<Tag> getTags() {
        return tags;
    }
    
    @Override
    public String toString() {
        return "OM{" + "content=" + content + ",tags=" + tags + ", type=" + type + '}';
    }

    @Override
    public OrMessage<T> clone() {
        return new OrMessage(type, content, new HashSet(tags));
    }
}
