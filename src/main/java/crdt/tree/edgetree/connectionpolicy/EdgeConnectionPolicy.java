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
package crdt.tree.edgetree.connectionpolicy;

import crdt.Factory;
import crdt.tree.edgetree.Edge;
import crdt.tree.edgetree.mappingpolicy.EdgeMappPolicy;
import java.util.Observer;
import java.util.Set;

/**
 *
 * @author Stephane Martin
 */
public abstract class EdgeConnectionPolicy<T> implements Factory <EdgeConnectionPolicy<T>>,Observer {
    protected EdgeMappPolicy<T> emp;
    
    abstract public Set <Edge<T>>getEdges();
    
    public void SetMappingPolicy(EdgeMappPolicy<T> emp){
        this.emp=emp;
    }
}
