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
package crdt.tree.graphtree.mapping;

/**
 *
 * @author Stephane Martin
 */
public class MappingUpdateOperation<T> {
    public  enum Type{add,del,move};
    private T object;
    private T old;
    private Type type;
    /**
     * @return the object
     */
    public T getObject() {
        return object;
    }

    /**
     * @return the type
     */
    public Type getType() {
        return type;
    }

    public T getOld() {
        return old;
    }
    
    

    public MappingUpdateOperation( Type type,T object) {
        if (type==MappingUpdateOperation.Type.move){
            throw new UnsupportedOperationException("Error move operation");
        }
        this.object = object;
        this.type = type;
    }
    public MappingUpdateOperation( Type type,T old,T object) {
        this.object = object;
        this.type = type;
        this.old=old;
    }
    
}
