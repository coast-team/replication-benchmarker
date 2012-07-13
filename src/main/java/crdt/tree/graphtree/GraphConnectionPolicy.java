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
package crdt.tree.graphtree;

import collect.HashMapSet;
import crdt.Factory;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author Stephane Martin
 */
public abstract class GraphConnectionPolicy<T> extends Observable implements Factory<GraphConnectionPolicy<T>>, Observer{
    protected GraphMappPolicy<T> gmp;

    /**
     * @param gmp the gmp to set
     */

    public void setGraphMappingPolicy(GraphMappPolicy<T> gmp) {
        this.gmp = gmp;
    }
    abstract public HashMapSet <T,Edge<T>> lookup ();
    @Override
    abstract public  void update(Observable o, Object op);
    
}
